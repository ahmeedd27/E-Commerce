package com.ahmed.E_CommerceApp.dao;

import com.ahmed.E_CommerceApp.dto.ProductListDTO;
import com.ahmed.E_CommerceApp.model.Product;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.Optional;

@Repository
// fix: added JpaSpecificationExecutor — enables the ProductSpec search to work
public interface ProductRepo extends JpaRepository<Product, Long> {

    // ─── List query — no comments, projects into lightweight DTO ──
    @Query("""
        SELECT new com.ahmed.E_CommerceApp.dto.ProductListDTO(
            p.id, p.name, p.price, p.quantity, p.imageUrl, p.category.name
        )
        FROM Product p
        LEFT JOIN p.category c
        """)
    Page<ProductListDTO> getProductsWithoutComments(Pageable pageable);

    @Query("""
    SELECT new com.ahmed.E_CommerceApp.dto.ProductListDTO(
        p.id, p.name, p.price, p.quantity, p.imageUrl, p.category.name
    )
    FROM Product p
    LEFT JOIN p.category c
    WHERE (:name     IS NULL OR LOWER(p.name) LIKE LOWER(CONCAT('%', :name, '%')))
    AND   (:minPrice IS NULL OR p.price >= :minPrice)
    AND   (:maxPrice IS NULL OR p.price <= :maxPrice)
    AND   (:catId    IS NULL OR p.category.id = :catId)
    """)
    Page<ProductListDTO> searchProducts(
            @Param("name")     String name,
            @Param("minPrice") BigDecimal minPrice,
            @Param("maxPrice") BigDecimal maxPrice,
            @Param("catId")    Long categoryId,
            Pageable pageable
    );

    // ─── Single product — fetch comments eagerly to avoid N+1 ─────
    @Query("""
        SELECT p FROM Product p
        LEFT JOIN FETCH p.comments c
        LEFT JOIN FETCH c.user
        LEFT JOIN FETCH p.category
        WHERE p.id = :id
        """)
    Optional<Product> findByIdWithComments(@Param("id") Long id);
}