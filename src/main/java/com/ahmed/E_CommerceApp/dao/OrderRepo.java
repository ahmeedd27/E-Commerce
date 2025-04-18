package com.ahmed.E_CommerceApp.dao;

import com.ahmed.E_CommerceApp.model.Order;
import com.ahmed.E_CommerceApp.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface OrderRepo extends JpaRepository<Order, Long> {
    @Query("SELECT o FROM Order o WHERE o.user.id=:userId")
    List<Order> findByUserId(Long userId);

}
