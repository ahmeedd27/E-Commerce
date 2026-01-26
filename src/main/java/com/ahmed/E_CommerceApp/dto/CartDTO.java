package com.ahmed.E_CommerceApp.dto;

import lombok.Data;

import java.util.List;

@Data
public class CartDTO {
    private Long id;
    private Long user;
    private List<CartItemDTO> items;
}
