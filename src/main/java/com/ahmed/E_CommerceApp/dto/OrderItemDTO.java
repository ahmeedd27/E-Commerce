package com.ahmed.E_CommerceApp.dto;

import jakarta.validation.constraints.Positive;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class OrderItemDTO {
    private Long id;
    private Long product;
    @Positive
    private Integer quantity;
    @Positive
    private BigDecimal price;
}
