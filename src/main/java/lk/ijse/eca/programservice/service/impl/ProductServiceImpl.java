package lk.ijse.eca.programservice.service.impl;

import lk.ijse.eca.programservice.dto.ProductDto;
import lk.ijse.eca.programservice.entity.Product;
import lk.ijse.eca.programservice.exception.DuplicateProductException;
import lk.ijse.eca.programservice.exception.ProductNotFoundException;
import lk.ijse.eca.programservice.exception.InsufficientStockException;
import lk.ijse.eca.programservice.mapper.ProductMapper;
import lk.ijse.eca.programservice.repository.ProductRepository;
import lk.ijse.eca.programservice.service.ProductService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class ProductServiceImpl implements ProductService {

    private final ProductRepository productRepository;
    private final ProductMapper productMapper;

    @Override
    @Transactional
    public ProductDto createProduct(ProductDto dto) {
        log.debug("Creating product with ID: {}", dto.getProductId());

        if (productRepository.existsById(dto.getProductId())) {
            log.warn("Duplicate product ID detected: {}", dto.getProductId());
            throw new DuplicateProductException(dto.getProductId());
        }

        Product saved = productRepository.save(productMapper.toEntity(dto));
        log.info("Product created successfully: {}", saved.getProductId());
        return productMapper.toDto(saved);
    }

    @Override
    @Transactional(readOnly = true)
    public ProductDto getProduct(String productId) {
        log.debug("Fetching product with ID: {}", productId);
        return productRepository.findById(productId)
                .map(productMapper::toDto)
                .orElseThrow(() -> {
                    log.warn("Product not found: {}", productId);
                    return new ProductNotFoundException(productId);
                });
    }

    @Override
    @Transactional(readOnly = true)
    public List<ProductDto> getAllProducts() {
        log.debug("Fetching all products");
        List<ProductDto> products = productRepository.findAll()
                .stream()
                .map(productMapper::toDto)
                .toList();
        log.debug("Fetched {} product(s)", products.size());
        return products;
    }

    @Override
    @Transactional(readOnly = true)
    public List<ProductDto> getActiveProducts() {
        log.debug("Fetching active products");
        return productRepository.findByIsActive(true)
                .stream()
                .map(productMapper::toDto)
                .toList();
    }

    @Override
    @Transactional
    public ProductDto updateProduct(String productId, ProductDto dto) {
        log.debug("Updating product with ID: {}", productId);

        Product product = productRepository.findById(productId)
                .orElseThrow(() -> {
                    log.warn("Product not found for update: {}", productId);
                    return new ProductNotFoundException(productId);
                });

        productMapper.updateEntity(dto, product);
        Product updated = productRepository.save(product);
        log.info("Product updated successfully: {}", updated.getProductId());
        return productMapper.toDto(updated);
    }

    @Override
    @Transactional
    public void deleteProduct(String productId) {
        log.debug("Deleting product with ID: {}", productId);

        Product product = productRepository.findById(productId)
                .orElseThrow(() -> {
                    log.warn("Product not found for deletion: {}", productId);
                    return new ProductNotFoundException(productId);
                });

        productRepository.delete(product);
        log.info("Product deleted successfully: {}", productId);
    }

    @Override
    @Transactional(readOnly = true)
    public List<ProductDto> getProductsByCategory(String category) {
        log.debug("Fetching products by category: {}", category);
        return productRepository.findByCategory(category)
                .stream()
                .map(productMapper::toDto)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<ProductDto> getProductsBySupplier(String supplier) {
        log.debug("Fetching products by supplier: {}", supplier);
        return productRepository.findBySupplier(supplier)
                .stream()
                .map(productMapper::toDto)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<ProductDto> getLowStockProducts(Integer threshold) {
        log.debug("Fetching low stock products with threshold: {}", threshold);
        return productRepository.findByStockQuantityLessThan(threshold)
                .stream()
                .map(productMapper::toDto)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<ProductDto> searchProductsByName(String name) {
        log.debug("Searching products by name: {}", name);
        return productRepository.findByNameContaining(name)
                .stream()
                .map(productMapper::toDto)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<ProductDto> getProductsByPriceRange(BigDecimal minPrice, BigDecimal maxPrice) {
        log.debug("Fetching products in price range: {} - {}", minPrice, maxPrice);
        return productRepository.findByPriceBetween(minPrice, maxPrice)
                .stream()
                .map(productMapper::toDto)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public ProductDto getProductByBarcode(String barcode) {
        log.debug("Fetching product by barcode: {}", barcode);
        Product product = productRepository.findByBarcode(barcode);
        if (product == null) {
            throw new ProductNotFoundException("Product not found with barcode: " + barcode);
        }
        return productMapper.toDto(product);
    }

    @Override
    @Transactional(readOnly = true)
    public ProductDto getProductBySku(String sku) {
        log.debug("Fetching product by SKU: {}", sku);
        Product product = productRepository.findBySku(sku);
        if (product == null) {
            throw new ProductNotFoundException("Product not found with SKU: " + sku);
        }
        return productMapper.toDto(product);
    }

    @Override
    @Transactional
    public void updateStock(String productId, Integer quantity) {
        log.debug("Updating stock for product {}: {}", productId, quantity);
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> {
                    log.warn("Product not found for stock update: {}", productId);
                    return new ProductNotFoundException(productId);
                });

        product.setStockQuantity(quantity);
        productRepository.save(product);
        log.info("Stock updated for product {}: {}", productId, quantity);
    }

    @Override
    @Transactional
    public void reduceStock(String productId, Integer quantity) {
        log.debug("Reducing stock for product {}: {}", productId, quantity);
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> {
                    log.warn("Product not found for stock reduction: {}", productId);
                    return new ProductNotFoundException(productId);
                });

        if (product.getStockQuantity() < quantity) {
            log.warn("Insufficient stock for product {}: available={}, requested={}", 
                    productId, product.getStockQuantity(), quantity);
            throw new InsufficientStockException(productId, product.getStockQuantity(), quantity);
        }

        product.setStockQuantity(product.getStockQuantity() - quantity);
        productRepository.save(product);
        log.info("Stock reduced for product {}: new quantity={}", productId, product.getStockQuantity());
    }

    @Override
    @Transactional(readOnly = true)
    public List<ProductDto> getTopSellingProducts() {
        log.debug("Fetching top selling products");
        return productRepository.findByCategoryAndIsActiveOrderByStockQuantityDesc("", true)
                .stream()
                .limit(10)
                .map(productMapper::toDto)
                .toList();
    }
}
