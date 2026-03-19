package lk.ijse.eca.programservice.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.math.BigDecimal;
import java.util.List;

@Document(collection = "products")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Product {

    @Id
    private String productId;
    
    private String name;
    
    private String description;
    
    private BigDecimal price;
    
    private String category;
    
    private Integer stockQuantity;
    
    private String barcode;
    
    private String supplier;
    
    private List<String> images;
    
    private List<ProductVariant> variants;
    
    private Boolean isActive;
    
    private BigDecimal costPrice;
    
    private String sku;
    
    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ProductVariant {
        private String variantId;
        private String name;
        private BigDecimal price;
        private Integer stockQuantity;
        private String sku;
    }
}
