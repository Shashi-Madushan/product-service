package lk.ijse.eca.programservice.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = "app.storage")
@Getter
@Setter
public class StorageProperties {

    private String type = "local";
    private String localPath = "./uploads/products";
    private String baseUrl = "/api/v1/products/images";
    private CloudStorage cloud = new CloudStorage();

    @Getter
    @Setter
    public static class CloudStorage {
        private String provider = "gcs"; // gcs, aws, azure
        private String bucketName;
        private String projectId; // For Google Cloud Storage
        private String region = "us-central1";
        private String accessKey;
        private String secretKey;
        private String endpoint; // For S3-compatible services
    }
}
