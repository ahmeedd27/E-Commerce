package com.ahmed.E_CommerceApp.dao;

import com.ahmed.E_CommerceApp.model.Cart;
import com.ahmed.E_CommerceApp.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CartRepo extends JpaRepository<Cart, Long> {
    @Query("SELECT c FROM Cart c WHERE c.user.id=:userId")
    Optional<Cart> findByUserId(@Param("userId") Long id);
}
