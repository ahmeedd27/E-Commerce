package com.ahmed.E_CommerceApp.model;

import jakarta.persistence.*;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString(exclude = {"cart", "product"})
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@Entity
public class CartItems {
    @EqualsAndHashCode.Include
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @ManyToOne
    @JoinColumn(name = "cart_id" , nullable = false)
    private Cart cart;
    @ManyToOne
    @JoinColumn(name = "product_id" , nullable = false)
    private Product product;

    private Integer quantity;

}
