package com.ahmed.E_CommerceApp.mapper;

import com.ahmed.E_CommerceApp.dto.CartItemDTO;
import com.ahmed.E_CommerceApp.model.CartItems;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface CartMapper {

    @Mapping(target = "cartItemId",   source = "id")
    @Mapping(target = "productId",    source = "product.id")
    @Mapping(target = "productName",  source = "product.name")
    @Mapping(target = "imageUrl",     source = "product.imageUrl")
    @Mapping(target = "price",        source = "product.price")
    // subtotal is computed — service sets it after mapping
    @Mapping(target = "subtotal",     ignore = true)
    CartItemDTO toDTO(CartItems cartItem);
}