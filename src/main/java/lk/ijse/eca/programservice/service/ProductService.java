package lk.ijse.eca.programservice.service;

import lk.ijse.eca.programservice.dto.ProductDto;
import org.springframework.web.multipart.MultipartFile;

import java.math.BigDecimal;
import java.util.List;

public interface ProductService {

    ProductDto createProduct(ProductDto dto, List<MultipartFile> images);

    ProductDto createProduct(ProductDto dto);

    ProductDto getProduct(String productId);

    List<ProductDto> getAllProducts();

    List<ProductDto> getActiveProducts();

    ProductDto updateProduct(String productId, ProductDto dto, List<MultipartFile> newImages, List<String> imagesToDelete);

    ProductDto updateProduct(String productId, ProductDto dto);

    void deleteProduct(String productId);

    List<ProductDto> getProductsByCategory(String category);

    List<ProductDto> getProductsBySupplier(String supplier);

    List<ProductDto> getLowStockProducts(Integer threshold);

    List<ProductDto> searchProductsByName(String name);

    List<ProductDto> getProductsByPriceRange(BigDecimal minPrice, BigDecimal maxPrice);

    ProductDto getProductByBarcode(String barcode);

    ProductDto getProductBySku(String sku);

    void updateStock(String productId, Integer quantity);

    void reduceStock(String productId, Integer quantity);

    List<ProductDto> getTopSellingProducts();
}
