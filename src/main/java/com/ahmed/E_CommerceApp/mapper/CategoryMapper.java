package com.ahmed.E_CommerceApp.mapper;

import com.ahmed.E_CommerceApp.dto.CategoryCreateRequest;
import com.ahmed.E_CommerceApp.dto.CategoryDTO;
import com.ahmed.E_CommerceApp.model.Category;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface CategoryMapper {

    // Category entity → CategoryDTO
    // productCount is NOT a field on Category entity — service sets it manually
    @Mapping(target = "productCount", ignore = true)
    CategoryDTO toDTO(Category category);

    // CategoryCreateRequest → new Category entity
    @Mapping(target = "id",       ignore = true) // DB generates it
    @Mapping(target = "products", ignore = true) // no products on create
    Category toEntity(CategoryCreateRequest request);

    // Update existing category in-place
    @Mapping(target = "id",       ignore = true)
    @Mapping(target = "products", ignore = true)
    void updateEntityFromRequest(CategoryCreateRequest request, @MappingTarget Category category);
}