package com.ahmed.E_CommerceApp.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@AllArgsConstructor
public class ProductListDTO {
    private Long id;
    private String name;
    private BigDecimal price;
    private Integer quantity;
    private String imageUrl;
    private String categoryName;
}