package lk.ijse.eca.programservice;

import lk.ijse.eca.programservice.config.StorageProperties;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

@SpringBootApplication
@EnableConfigurationProperties(StorageProperties.class)
public class ProductCatalogServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(ProductCatalogServiceApplication.class, args);
    }

}
