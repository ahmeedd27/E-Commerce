package com.ahmed.E_CommerceApp.mapper;

import com.ahmed.E_CommerceApp.dto.CartDTO;
import com.ahmed.E_CommerceApp.dto.CartItemDTO;
import com.ahmed.E_CommerceApp.model.Cart;
import com.ahmed.E_CommerceApp.model.CartItems;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;


@Mapper(componentModel = "spring")
public interface CartMapper {

    @Mapping(source = "user.id", target = "user")
    @Mapping(source = "items", target = "items")
    CartDTO toDto(Cart cart);

    @Mapping(source = "user", target = "user.id")
    @Mapping(source = "items", target = "items")
    Cart toCart(CartDTO cartDTO);

    @Mapping(source = "product.id", target = "product")
    CartItemDTO toItemDto(CartItems cartItems);

    @Mapping(source = "product", target = "product.id")
    @Mapping(target = "cart", ignore = true)
    CartItems toItem(CartItemDTO cartItemDTO);


}
