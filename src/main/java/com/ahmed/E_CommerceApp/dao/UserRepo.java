package com.ahmed.E_CommerceApp.dao;

import com.ahmed.E_CommerceApp.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepo extends JpaRepository<User , Long> {
    Optional<User> findByEmail(String email);
    Optional<User> findById(Long id);
}
