package com.ahmed.E_CommerceApp.dao;

import com.ahmed.E_CommerceApp.model.Cart;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CartRepo extends JpaRepository<Cart, Long> {

    // ─── Load full cart with items + products in one query ────
    // user is @OneToOne (single object) → JOIN FETCH safe with single result
    @Query("""
        SELECT c FROM Cart c
        LEFT JOIN FETCH c.items i
        LEFT JOIN FETCH i.product
        WHERE c.user.id = :userId
        """)
    Optional<Cart> findByUserIdWithItems(@Param("userId") Long userId);
}