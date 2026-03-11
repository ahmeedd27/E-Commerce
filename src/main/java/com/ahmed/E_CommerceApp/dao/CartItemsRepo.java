package com.ahmed.E_CommerceApp.dao;

import com.ahmed.E_CommerceApp.model.CartItems;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CartItemsRepo extends JpaRepository<CartItems, Long> {

    // ─── Find existing item in cart by product — for add logic ─
    @Query("""
        SELECT ci FROM CartItems ci
        WHERE ci.cart.id = :cartId
        AND ci.product.id = :productId
        """)
    Optional<CartItems> findByCartIdAndProductId(
        @Param("cartId")    Long cartId,
        @Param("productId") Long productId
    );
}