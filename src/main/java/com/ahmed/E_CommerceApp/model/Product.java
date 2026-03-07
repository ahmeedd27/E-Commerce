package com.ahmed.E_CommerceApp.model;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString(exclude = {"comments", "category"})
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@Entity
public class Product {
    @EqualsAndHashCode.Include
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;
    private String description;
    private Integer quantity;
    private BigDecimal price;
     private String imageUrl;

    @ManyToOne
    @JoinColumn(name = "category_id")
    private Category category; //  Added: every product belongs to a category and every category has many products


    @OneToMany(mappedBy = "product" , cascade = CascadeType.ALL , fetch = FetchType.LAZY
             , orphanRemoval = true)
     private List<Comment> comments;
}
