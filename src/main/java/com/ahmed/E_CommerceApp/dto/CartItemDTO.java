package com.ahmed.E_CommerceApp.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CartItemDTO {
    private Long cartItemId;       // needed for update/remove operations
    private Long productId;
    private String productName;
    private String imageUrl;
    private BigDecimal price;      // price per unit at time of viewing
    private Integer quantity;
    private BigDecimal subtotal;   // price × quantity —  computed in service
}