package com.ahmed.E_CommerceApp.model;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString(exclude = {"order", "product"})
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@Entity
public class OrderItems {
    @EqualsAndHashCode.Include
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
