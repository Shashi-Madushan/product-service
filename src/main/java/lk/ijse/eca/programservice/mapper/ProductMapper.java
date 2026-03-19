package lk.ijse.eca.programservice.mapper;

import lk.ijse.eca.programservice.dto.ProductDto;
import lk.ijse.eca.programservice.entity.Product;
import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.Named;
import org.mapstruct.NullValuePropertyMappingStrategy;
import org.mapstruct.ReportingPolicy;

import java.util.List;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.ERROR)
public interface ProductMapper {

    ProductDto toDto(Product product);

    Product toEntity(ProductDto dto);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(target = "productId", ignore = true)
    void updateEntity(ProductDto dto, @MappingTarget Product product);

    @Mapping(target = "productId", ignore = true)
    @Mapping(target = "variants", source = "variants", qualifiedByName = "mapVariantDtosToEntities")
    Product toEntityWithoutId(ProductDto dto);

    @Named("mapVariantDtosToEntities")
    default List<Product.ProductVariant> mapVariantDtosToEntities(List<ProductDto.ProductVariantDto> variantDtos) {
        if (variantDtos == null) return null;
        return variantDtos.stream()
                .map(this::mapVariantDtoToEntity)
                .toList();
    }

    @Named("mapVariantEntitiesToDtos")
    default List<ProductDto.ProductVariantDto> mapVariantEntitiesToDtos(List<Product.ProductVariant> variants) {
        if (variants == null) return null;
        return variants.stream()
                .map(this::mapVariantEntityToDto)
                .toList();
    }

    default Product.ProductVariant mapVariantDtoToEntity(ProductDto.ProductVariantDto dto) {
        if (dto == null) return null;
        return new Product.ProductVariant(
                dto.getVariantId(),
                dto.getName(),
                dto.getPrice(),
                dto.getStockQuantity(),
                dto.getSku()
        );
    }

    default ProductDto.ProductVariantDto mapVariantEntityToDto(Product.ProductVariant entity) {
        if (entity == null) return null;
        return new ProductDto.ProductVariantDto(
                entity.getVariantId(),
                entity.getName(),
                entity.getPrice(),
                entity.getStockQuantity(),
                entity.getSku()
        );
    }
}
