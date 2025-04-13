package com.ahmed.E_CommerceApp.dao;

import com.ahmed.E_CommerceApp.model.Order;
import com.ahmed.E_CommerceApp.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface OrderRepo extends JpaRepository<Order, Long> {
}
