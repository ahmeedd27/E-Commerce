package com.ahmed.E_CommerceApp.service;

import com.ahmed.E_CommerceApp.dao.RefreshTokenRepo;
import com.ahmed.E_CommerceApp.exception.ResourceNotFoundException;
import com.ahmed.E_CommerceApp.model.RefreshToken;
import com.ahmed.E_CommerceApp.model.User;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class RefreshTokenService {

    private final RefreshTokenRepo refreshTokenRepo;

    @Value("${jwt.refresh-expiration}")
    private Long refreshExpiration; // in milliseconds

    @Transactional
    public RefreshToken createRefreshToken(User user) {
        // Delete any existing token for this user
        // One user = one refresh token at a time
        refreshTokenRepo.deleteByUser(user);

        RefreshToken refreshToken = RefreshToken.builder()
                .token(UUID.randomUUID().toString()) // random, unguessable string
                .user(user)
                .expiresAt(LocalDateTime.now()
                        .plusSeconds(refreshExpiration / 1000)) // convert ms to seconds
                .build();

        return refreshTokenRepo.save(refreshToken);
    }

    public RefreshToken validateRefreshToken(String token) {
        RefreshToken refreshToken = refreshTokenRepo.findByToken(token)
                .orElseThrow(() -> new ResourceNotFoundException(
                    "Invalid refresh token"
                ));

        // Token exists in DB but is it expired?
        if (refreshToken.isExpired()) {
            // Clean it up from DB
            refreshTokenRepo.delete(refreshToken);
            throw new IllegalStateException(
                "Refresh token expired. Please login again."
            );
        }

        return refreshToken;
    }

    @Transactional
    public void deleteByUser(User user) {
        refreshTokenRepo.deleteByUser(user);
    }
}