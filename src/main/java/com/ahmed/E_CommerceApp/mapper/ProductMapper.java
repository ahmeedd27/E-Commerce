package com.ahmed.E_CommerceApp.mapper;

import com.ahmed.E_CommerceApp.dto.ProductCreateRequest;
import com.ahmed.E_CommerceApp.dto.ProductDTO;
import com.ahmed.E_CommerceApp.dto.ProductUpdateRequest;
import com.ahmed.E_CommerceApp.model.Product;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface ProductMapper {

    // ─── Product → ProductDTO (response) ──────────────────────
    @Mapping(target = "categoryName", source = "category.name")
    ProductDTO toDTO(Product product);

    // ─── ProductCreateRequest → Product (new entity) ──────────
    @Mapping(target = "id",       ignore = true)
    @Mapping(target = "imageUrl", ignore = true)
    @Mapping(target = "comments", ignore = true)
    @Mapping(target = "category", ignore = true)
    Product toEntity(ProductCreateRequest request);

    /**
     * fix: replaces the 4 manual setters in ProductService.updateProduct.
     * @MappingTarget tells MapStruct to update the existing entity in-place
     * rather than creating a new one — no more manual field-by-field mapping.
     */
    @Mapping(target = "id",       ignore = true)
    @Mapping(target = "imageUrl", ignore = true)
    @Mapping(target = "comments", ignore = true)
    @Mapping(target = "category", ignore = true)
    void updateEntityFromRequest(ProductUpdateRequest request, @MappingTarget Product product);
}