package lk.ijse.eca.programservice.service.storage.impl;

import com.google.cloud.storage.BlobId;
import com.google.cloud.storage.BlobInfo;
import com.google.cloud.storage.Storage;
import com.google.cloud.storage.StorageOptions;
import lk.ijse.eca.programservice.config.StorageProperties;
import lk.ijse.eca.programservice.service.storage.StorageService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import jakarta.annotation.PostConstruct;
import java.io.IOException;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
@Profile({"prod", "production", "cloud"})
public class CloudStorageService implements StorageService {

    private final StorageProperties storageProperties;
    private Storage storage;
    private String bucketName;

    @PostConstruct
    public void init() {
        try {
            this.bucketName = storageProperties.getCloud().getBucketName();

            // Initialize GCS client - uses Application Default Credentials
            // Set GOOGLE_APPLICATION_CREDENTIALS env var to service account key file in production
            StorageOptions.Builder builder = StorageOptions.newBuilder();

            if (storageProperties.getCloud().getProjectId() != null) {
                builder.setProjectId(storageProperties.getCloud().getProjectId());
            }

            this.storage = builder.build().getService();
            log.info("Google Cloud Storage initialized with bucket: {}", bucketName);

        } catch (Exception e) {
            log.error("Failed to initialize Google Cloud Storage", e);
            throw new RuntimeException("Failed to initialize Google Cloud Storage", e);
        }
    }

    @Override
    public String store(MultipartFile file, String productId) {
        try {
            String originalFilename = file.getOriginalFilename();
            String extension = getFileExtension(originalFilename);
            String filename = String.format("products/%s/%s.%s", productId, UUID.randomUUID(), extension);

            BlobId blobId = BlobId.of(bucketName, filename);
            BlobInfo blobInfo = BlobInfo.newBuilder(blobId)
                    .setContentType(file.getContentType())
                    .build();

            storage.create(blobInfo, file.getBytes());

            // Build public URL for GCS
            String fileUrl = String.format("https://storage.googleapis.com/%s/%s", bucketName, filename);
            log.info("File stored in GCS: {} -> {}", originalFilename, fileUrl);

            return fileUrl;

        } catch (IOException e) {
            log.error("Failed to store file in GCS for product: {}", productId, e);
            throw new RuntimeException("Failed to store file in Google Cloud Storage", e);
        }
    }

    @Override
    public void delete(String fileUrl) {
        try {
            String blobName = extractBlobNameFromUrl(fileUrl);
            BlobId blobId = BlobId.of(bucketName, blobName);

            boolean deleted = storage.delete(blobId);
            if (deleted) {
                log.info("File deleted from GCS: {}", fileUrl);
            } else {
                log.warn("File not found in GCS for deletion: {}", fileUrl);
            }

        } catch (Exception e) {
            log.error("Failed to delete file from GCS: {}", fileUrl, e);
            throw new RuntimeException("Failed to delete file from Google Cloud Storage", e);
        }
    }

    @Override
    public StorageType getStorageType() {
        return StorageType.CLOUD;
    }

    @Override
    public boolean isActive() {
        return "cloud".equalsIgnoreCase(storageProperties.getType()) ||
               "gcs".equalsIgnoreCase(storageProperties.getType()) ||
               "google".equalsIgnoreCase(storageProperties.getType());
    }

    private String getFileExtension(String filename) {
        if (filename == null || filename.lastIndexOf(".") == -1) {
            return "jpg";
        }
        return filename.substring(filename.lastIndexOf(".") + 1).toLowerCase();
    }

    private String extractBlobNameFromUrl(String fileUrl) {
        // GCS URL format: https://storage.googleapis.com/BUCKET_NAME/OBJECT_NAME
        if (fileUrl.contains("storage.googleapis.com")) {
            String prefix = String.format("https://storage.googleapis.com/%s/", bucketName);
            if (fileUrl.startsWith(prefix)) {
                return fileUrl.substring(prefix.length());
            }
        }
        // Fallback: extract everything after the bucket name
        int bucketIndex = fileUrl.indexOf(bucketName);
        if (bucketIndex != -1) {
            int slashIndex = fileUrl.indexOf("/", bucketIndex + bucketName.length() + 1);
            if (slashIndex != -1) {
                return fileUrl.substring(slashIndex + 1);
            }
        }
        // Last resort: assume the last segment is the blob name
        return fileUrl.substring(fileUrl.lastIndexOf("/") + 1);
    }
}
