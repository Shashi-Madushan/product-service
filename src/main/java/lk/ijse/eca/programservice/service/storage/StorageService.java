package lk.ijse.eca.programservice.service.storage;

import org.springframework.web.multipart.MultipartFile;

public interface StorageService {

    /**
     * Store a file and return the accessible URL
     */
    String store(MultipartFile file, String productId);

    /**
     * Delete a file by its URL
     */
    void delete(String fileUrl);

    /**
     * Get the storage type (local, cloud, etc.)
     */
    StorageType getStorageType();

    /**
     * Check if this storage service is active for current profile
     */
    boolean isActive();

    enum StorageType {
        LOCAL,
        CLOUD
    }
}
