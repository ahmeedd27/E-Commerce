package com.ahmed.E_CommerceApp.mapper;

import com.ahmed.E_CommerceApp.dto.CartDTO;
import com.ahmed.E_CommerceApp.dto.CartItemDTO;
import com.ahmed.E_CommerceApp.model.Cart;
import com.ahmed.E_CommerceApp.model.CartItems;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface CartMapper {

    CartDTO toDto(Cart cart);
    Cart toCart(CartDTO cartDTO);
    CartItemDTO toItemDto(CartItems cartItems);
    CartItems toItemDto(CartItemDTO cartItemDTO);

}
