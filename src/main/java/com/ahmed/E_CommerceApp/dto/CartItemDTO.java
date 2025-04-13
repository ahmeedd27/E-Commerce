package com.ahmed.E_CommerceApp.dto;

import jakarta.validation.constraints.Positive;
import lombok.Data;

@Data
public class CartItemDTO {
    private Long id;
    private Long product;
    @Positive
    private Integer quantity;

}
