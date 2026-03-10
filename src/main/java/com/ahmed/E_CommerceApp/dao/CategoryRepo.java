package com.ahmed.E_CommerceApp.dao;

import com.ahmed.E_CommerceApp.model.Category;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CategoryRepo extends JpaRepository<Category, Long> {

    // ─── Check name uniqueness before create/update ────────────
    boolean existsByNameIgnoreCase(String name);

    // ─── Same name check but excluding current category (for update) ──
    boolean existsByNameIgnoreCaseAndIdNot(String name, Long id);

    // ─── Fetch all categories with product count in one query ──
    // Using SIZE() — JPQL function that counts the collection
    @Query("""
        SELECT c FROM Category c
        LEFT JOIN FETCH c.products
        ORDER BY c.name ASC
        """)
    List<Category> findAllWithProducts();

    // ─── Single category with products ─────────────────────────
    @Query("""
        SELECT c FROM Category c
        LEFT JOIN FETCH c.products p
        WHERE c.id = :id
        """)
    Optional<Category> findByIdWithProducts(@Param("id") Long id);
}