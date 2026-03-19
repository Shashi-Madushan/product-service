package lk.ijse.eca.programservice.repository;

import lk.ijse.eca.programservice.entity.Product;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;

@Repository
public interface ProductRepository extends MongoRepository<Product, String> {

    List<Product> findByCategory(String category);

    List<Product> findBySupplier(String supplier);

    List<Product> findByIsActive(Boolean isActive);

    List<Product> findByStockQuantityLessThan(Integer threshold);

    @Query("{ 'price': { $gte: ?0, $lte: ?1 } }")
    List<Product> findByPriceBetween(BigDecimal minPrice, BigDecimal maxPrice);

    @Query("{ 'name': { $regex: ?0, $options: 'i' } }")
    List<Product> findByNameContaining(String name);

    @Query(value = "{ 'category': ?0, 'isActive': true }", sort = "{ 'stockQuantity': -1 }")
    List<Product> findByCategoryAndIsActiveOrderByStockQuantityDesc(String category);

    @Query("{ 'barcode': ?0 }")
    Product findByBarcode(String barcode);

    @Query("{ 'sku': ?0 }")
    Product findBySku(String sku);
}
