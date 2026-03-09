// dao/RefreshTokenRepo.java
package com.ahmed.E_CommerceApp.dao;

import com.ahmed.E_CommerceApp.model.RefreshToken;
import com.ahmed.E_CommerceApp.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface RefreshTokenRepo extends JpaRepository<RefreshToken, Long> {

    Optional<RefreshToken> findByToken(String token);

    // Delete old token when user logs in again
    void deleteByUser(User user);
}