package lk.ijse.eca.programservice.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.validation.Valid;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ValidationException;
import jakarta.validation.Validator;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.DecimalMin;
import lk.ijse.eca.programservice.dto.ProductDto;
import lk.ijse.eca.programservice.service.ProductService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.math.BigDecimal;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/v1/products")
@RequiredArgsConstructor
@Slf4j
@Validated
public class ProductController {

    private final ProductService productService;
    private final ObjectMapper objectMapper;
    private final Validator validator;

    private static final String PRODUCT_ID_REGEXP = "^[A-Z0-9]{6,12}$";
    private static final String BARCODE_REGEXP = "^[0-9]{8,13}$";
    private static final String SKU_REGEXP = "^[A-Z0-9-]{8,20}$";

    private ProductDto parseAndValidateProduct(String productJson, Class<?>... groups) {
        try {
            ProductDto dto = objectMapper.readValue(productJson, ProductDto.class);
            Set<ConstraintViolation<ProductDto>> violations = validator.validate(dto, groups);
            if (!violations.isEmpty()) {
                String errors = violations.stream()
                        .map(v -> v.getPropertyPath() + ": " + v.getMessage())
                        .collect(Collectors.joining(", "));
                throw new ValidationException("Validation failed: " + errors);
            }
            return dto;
        } catch (Exception e) {
            throw new ValidationException("Invalid product JSON: " + e.getMessage());
        }
    }

    @PostMapping(
            consumes = MediaType.MULTIPART_FORM_DATA_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public ResponseEntity<ProductDto> createProduct(
            @RequestParam("product") String productJson,
            @RequestParam(value = "images", required = false) List<MultipartFile> images) {
        log.info("POST /api/v1/products - images: {}", images != null ? images.size() : 0);
        ProductDto dto = parseAndValidateProduct(productJson, ProductDto.OnCreate.class);
        ProductDto response = productService.createProduct(dto, images);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping(value = "/{productId}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ProductDto> getProduct(
            @PathVariable
            @Pattern(regexp = PRODUCT_ID_REGEXP, message = "Product ID must be 6-12 alphanumeric characters")
            String productId) {
        log.info("GET /api/v1/products/{}", productId);
        ProductDto response = productService.getProduct(productId);
        return ResponseEntity.ok(response);
    }

    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<ProductDto>> getAllProducts() {
        log.info("GET /api/v1/products - retrieving all products");
        List<ProductDto> products = productService.getAllProducts();
        return ResponseEntity.ok(products);
    }

    @GetMapping(value = "/active", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<ProductDto>> getActiveProducts() {
        log.info("GET /api/v1/products/active - retrieving active products");
        List<ProductDto> products = productService.getActiveProducts();
        return ResponseEntity.ok(products);
    }

    @PutMapping(
            value = "/{productId}",
            consumes = MediaType.MULTIPART_FORM_DATA_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public ResponseEntity<ProductDto> updateProduct(
            @PathVariable
            @Pattern(regexp = PRODUCT_ID_REGEXP, message = "Product ID must be 6-12 alphanumeric characters")
            String productId,
            @RequestParam("product") String productJson,
            @RequestParam(value = "newImages", required = false) List<MultipartFile> newImages,
            @RequestParam(value = "imagesToDelete", required = false) List<String> imagesToDelete) {
        log.info("PUT /api/v1/products/{} - newImages: {}, imagesToDelete: {}",
                productId,
                newImages != null ? newImages.size() : 0,
                imagesToDelete != null ? imagesToDelete.size() : 0);
        ProductDto dto = parseAndValidateProduct(productJson);
        ProductDto response = productService.updateProduct(productId, dto, newImages, imagesToDelete);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{productId}")
    public ResponseEntity<String> deleteProduct(
            @PathVariable
            @Pattern(regexp = PRODUCT_ID_REGEXP, message = "Product ID must be 6-12 alphanumeric characters")
            String productId) {
        log.info("DELETE /api/v1/products/{}", productId);
        productService.deleteProduct(productId);
        return ResponseEntity.ok("Product deleted successfully");
    }

    @GetMapping(value = "/category/{category}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<ProductDto>> getProductsByCategory(
            @PathVariable String category) {
        log.info("GET /api/v1/products/category/{}", category);
        List<ProductDto> products = productService.getProductsByCategory(category);
        return ResponseEntity.ok(products);
    }

    @GetMapping(value = "/supplier/{supplier}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<ProductDto>> getProductsBySupplier(
            @PathVariable String supplier) {
        log.info("GET /api/v1/products/supplier/{}", supplier);
        List<ProductDto> products = productService.getProductsBySupplier(supplier);
        return ResponseEntity.ok(products);
    }

    @GetMapping(value = "/low-stock", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<ProductDto>> getLowStockProducts(
            @RequestParam(defaultValue = "10") @Min(value = 0, message = "Threshold cannot be negative") Integer threshold) {
        log.info("GET /api/v1/products/low-stock?threshold={}", threshold);
        List<ProductDto> products = productService.getLowStockProducts(threshold);
        return ResponseEntity.ok(products);
    }

    @GetMapping(value = "/search", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<ProductDto>> searchProductsByName(
            @RequestParam String name) {
        log.info("GET /api/v1/products/search?name={}", name);
        List<ProductDto> products = productService.searchProductsByName(name);
        return ResponseEntity.ok(products);
    }

    @GetMapping(value = "/price-range", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<ProductDto>> getProductsByPriceRange(
            @RequestParam @DecimalMin(value = "0.01", message = "Min price must be positive") BigDecimal minPrice,
            @RequestParam @DecimalMin(value = "0.01", message = "Max price must be positive") BigDecimal maxPrice) {
        log.info("GET /api/v1/products/price-range?minPrice={}&maxPrice={}", minPrice, maxPrice);
        List<ProductDto> products = productService.getProductsByPriceRange(minPrice, maxPrice);
        return ResponseEntity.ok(products);
    }

    @GetMapping(value = "/barcode/{barcode}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ProductDto> getProductByBarcode(
            @PathVariable
            @Pattern(regexp = BARCODE_REGEXP, message = "Barcode must be 8-13 digits")
            String barcode) {
        log.info("GET /api/v1/products/barcode/{}", barcode);
        ProductDto product = productService.getProductByBarcode(barcode);
        return ResponseEntity.ok(product);
    }

    @GetMapping(value = "/sku/{sku}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ProductDto> getProductBySku(
            @PathVariable
            @Pattern(regexp = SKU_REGEXP, message = "SKU must be 8-20 alphanumeric characters with hyphens")
            String sku) {
        log.info("GET /api/v1/products/sku/{}", sku);
        ProductDto product = productService.getProductBySku(sku);
        return ResponseEntity.ok(product);
    }

    @PutMapping("/{productId}/stock")
    public ResponseEntity<String> updateStock(
            @PathVariable
            @Pattern(regexp = PRODUCT_ID_REGEXP, message = "Product ID must be 6-12 alphanumeric characters")
            String productId,
            @RequestParam @Min(value = 0, message = "Stock quantity cannot be negative") Integer quantity) {
        log.info("PUT /api/v1/products/{}/stock?quantity={}", productId, quantity);
        productService.updateStock(productId, quantity);
        return ResponseEntity.ok("Stock updated successfully");
    }

    @PutMapping("/{productId}/stock/reduce")
    public ResponseEntity<String> reduceStock(
            @PathVariable
            @Pattern(regexp = PRODUCT_ID_REGEXP, message = "Product ID must be 6-12 alphanumeric characters")
            String productId,
            @RequestParam @Min(value = 1, message = "Quantity to reduce must be positive") Integer quantity) {
        log.info("PUT /api/v1/products/{}/stock/reduce?quantity={}", productId, quantity);
        productService.reduceStock(productId, quantity);
        return ResponseEntity.ok("Stock reduced successfully");
    }

    @GetMapping(value = "/top-selling", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<ProductDto>> getTopSellingProducts() {
        log.info("GET /api/v1/products/top-selling");
        List<ProductDto> products = productService.getTopSellingProducts();
        return ResponseEntity.ok(products);
    }
}
