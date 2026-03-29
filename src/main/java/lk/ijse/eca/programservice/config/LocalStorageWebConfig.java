package lk.ijse.eca.programservice.config;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.nio.file.Path;
import java.nio.file.Paths;

@Configuration
@Profile({"default", "dev", "local"})
@RequiredArgsConstructor
public class LocalStorageWebConfig implements WebMvcConfigurer {

    private final StorageProperties storageProperties;

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        Path uploadPath = Paths.get(storageProperties.getLocalPath()).toAbsolutePath().normalize();

        String baseUrl = storageProperties.getBaseUrl();
        if (!baseUrl.startsWith("/")) {
            baseUrl = "/" + baseUrl;
        }
        if (baseUrl.endsWith("/")) {
            baseUrl = baseUrl.substring(0, baseUrl.length() - 1);
        }

        registry.addResourceHandler(baseUrl + "/**")
                .addResourceLocations(uploadPath.toUri().toString());
    }
}
