package com.ahmed.E_CommerceApp.dao;

import com.ahmed.E_CommerceApp.model.Comment;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface CommentRepo extends JpaRepository<Comment, Long> {

    // ─── Paginated comments for a product — JOIN FETCH user to avoid N+1 ──
    // NOTE: cannot use JOIN FETCH + Page together directly on collections
    // solution: fetch user (single object, not a collection) → safe with pagination
    @Query("""
        SELECT c FROM Comment c
        JOIN FETCH c.user
        WHERE c.product.id = :productId
        """)
    Page<Comment> findByProductId(@Param("productId") Long productId, Pageable pageable);

    // ─── Check ownership before delete ────────────────────────
    @Query("""
        SELECT c FROM Comment c
        JOIN FETCH c.user
        WHERE c.id = :id
        """)
    java.util.Optional<Comment> findByIdWithUser(@Param("id") Long id);
}