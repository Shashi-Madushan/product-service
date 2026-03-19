package lk.ijse.eca.programservice.dto;

import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProductDto {

    public interface OnCreate {}

    @NotBlank(groups = OnCreate.class, message = "Product ID is required")
    @Pattern(groups = OnCreate.class, regexp = "^[A-Z0-9]{6,12}$", message = "Product ID must be 6-12 alphanumeric characters")
    private String productId;

    @NotBlank(message = "Product name is required")
    @Size(max = 100, message = "Product name must not exceed 100 characters")
    private String name;

    @Size(max = 500, message = "Description must not exceed 500 characters")
    private String description;

    @NotNull(message = "Price is required")
    @DecimalMin(value = "0.01", message = "Price must be greater than 0")
    @Digits(integer = 10, fraction = 2, message = "Price must have maximum 10 integer digits and 2 decimal places")
    private BigDecimal price;

    @NotBlank(message = "Category is required")
    @Size(max = 50, message = "Category must not exceed 50 characters")
    private String category;

    @NotNull(message = "Stock quantity is required")
    @Min(value = 0, message = "Stock quantity cannot be negative")
    private Integer stockQuantity;

    @Pattern(regexp = "^[0-9]{8,13}$", message = "Barcode must be 8-13 digits")
    private String barcode;

    @Size(max = 100, message = "Supplier name must not exceed 100 characters")
    private String supplier;

    private List<String> images;

    private List<ProductVariantDto> variants;

    @NotNull(message = "Active status is required")
    private Boolean isActive;

    @DecimalMin(value = "0.00", message = "Cost price cannot be negative")
    @Digits(integer = 10, fraction = 2, message = "Cost price must have maximum 10 integer digits and 2 decimal places")
    private BigDecimal costPrice;

    @Pattern(regexp = "^[A-Z0-9-]{8,20}$", message = "SKU must be 8-20 alphanumeric characters with hyphens")
    private String sku;

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class ProductVariantDto {
        private String variantId;
        private String name;
        private BigDecimal price;
        private Integer stockQuantity;
        private String sku;
    }
}
