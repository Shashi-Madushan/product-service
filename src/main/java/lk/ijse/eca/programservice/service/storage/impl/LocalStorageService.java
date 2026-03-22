package lk.ijse.eca.programservice.service.storage.impl;

import jakarta.annotation.PostConstruct;
import lk.ijse.eca.programservice.config.StorageProperties;
import lk.ijse.eca.programservice.service.storage.StorageService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
@Profile({"dev", "local"})
public class LocalStorageService implements StorageService {

    private final StorageProperties storageProperties;
    private Path uploadPath;

    @PostConstruct
    public void init() {
        this.uploadPath = Paths.get(storageProperties.getLocalPath()).toAbsolutePath().normalize();
        try {
            Files.createDirectories(uploadPath);
            log.info("Local storage initialized at: {}", uploadPath);
        } catch (IOException e) {
            log.error("Could not create upload directory: {}", uploadPath, e);
            throw new RuntimeException("Could not create upload directory", e);
        }
    }

    @Override
    public String store(MultipartFile file, String productId) {
        try {
            // Create product-specific subdirectory
            Path productDir = uploadPath.resolve(productId);
            Files.createDirectories(productDir);

            // Generate unique filename
            String originalFilename = file.getOriginalFilename();
            String extension = getFileExtension(originalFilename);
            String filename = UUID.randomUUID().toString() + "." + extension;

            // Store file
            Path targetLocation = productDir.resolve(filename);
            Files.copy(file.getInputStream(), targetLocation, StandardCopyOption.REPLACE_EXISTING);

            // Return relative URL path
            String fileUrl = storageProperties.getBaseUrl() + "/" + productId + "/" + filename;
            log.info("File stored locally: {} -> {}", originalFilename, fileUrl);

            return fileUrl;

        } catch (IOException e) {
            log.error("Failed to store file for product: {}", productId, e);
            throw new RuntimeException("Failed to store file", e);
        }
    }

    @Override
    public void delete(String fileUrl) {
        try {
            // Extract path from URL
            String relativePath = fileUrl.replace(storageProperties.getBaseUrl(), "");
            Path filePath = uploadPath.resolve(relativePath).normalize();

            // Security check: ensure the path is within upload directory
            if (!filePath.startsWith(uploadPath)) {
                log.warn("Attempted to delete file outside upload directory: {}", filePath);
                throw new SecurityException("Invalid file path");
            }

            Files.deleteIfExists(filePath);
            log.info("File deleted: {}", filePath);

        } catch (IOException e) {
            log.error("Failed to delete file: {}", fileUrl, e);
            throw new RuntimeException("Failed to delete file", e);
        }
    }

    @Override
    public StorageType getStorageType() {
        return StorageType.LOCAL;
    }

    @Override
    public boolean isActive() {
        return "local".equalsIgnoreCase(storageProperties.getType());
    }

    private String getFileExtension(String filename) {
        if (filename == null || filename.lastIndexOf(".") == -1) {
            return "jpg";
        }
        return filename.substring(filename.lastIndexOf(".") + 1).toLowerCase();
    }

    public Path getUploadPath() {
        return uploadPath;
    }
}
