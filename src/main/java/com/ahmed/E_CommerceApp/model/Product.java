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
@Table(name = "product", indexes = {
        @Index(name = "idx_product_price",    columnList = "price"),
        @Index(name = "idx_product_category", columnList = "category_id")
        // idx_product_name comes back as FULLTEXT index later
})
public class Product {

    @EqualsAndHashCode.Include
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)               // fix: DB-level constraint, not just DTO validation
    private String name;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String description;

    @Column(nullable = false)
    private Integer quantity;

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal price;

    private String imageUrl;                // nullable — image is optional on creation

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id")
    private Category category;

    @OneToMany(mappedBy = "product", cascade = CascadeType.ALL,
               fetch = FetchType.LAZY, orphanRemoval = true)
    private List<Comment> comments;
}