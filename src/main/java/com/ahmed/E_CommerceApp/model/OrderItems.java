package com.ahmed.E_CommerceApp.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class OrderItems {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @ManyToOne
    @JoinColumn(name = "order_id" , nullable = false)
    private Order order;
    @ManyToOne
    @JoinColumn(name = "product_id" , nullable = false)
    private Product product;
    private Integer quantity;
    private BigDecimal price;
}
