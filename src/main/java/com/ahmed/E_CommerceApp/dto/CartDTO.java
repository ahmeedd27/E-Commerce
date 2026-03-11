package com.ahmed.E_CommerceApp.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CartDTO {
    private Long id;
    private List<CartItemDTO> items;
    private BigDecimal totalPrice;   // sum of all subtotals — computed in service
    private LocalDateTime updatedAt;
}