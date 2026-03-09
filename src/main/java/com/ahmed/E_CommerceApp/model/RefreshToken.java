package com.ahmed.E_CommerceApp.model;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class RefreshToken {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // The actual token string — must be unique
    @Column(nullable = false, unique = true)
    private String token;

    // Which user owns this token
    @OneToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    // When this token expires
    @Column(nullable = false)
    private LocalDateTime expiresAt;


    public boolean isExpired() {
        return LocalDateTime.now().isAfter(expiresAt);
    }
}