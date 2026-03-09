package com.ahmed.E_CommerceApp.model;

import com.ahmed.E_CommerceApp.model.enums.Role;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@Getter
@Setter
@ToString(exclude = {"orders", "cart", "comments"}) // exclude relationships
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class User {
    @EqualsAndHashCode.Include
    @Id
    @GeneratedValue(strategy= GenerationType.IDENTITY)
    private Long id;


    private String email;

    private String password;

    private String phoneNumber; //  Added: user's default phone number

    @Enumerated(EnumType.STRING)
    private Role role;

    @OneToMany(mappedBy = "user" , cascade = CascadeType.ALL , fetch = FetchType.LAZY)
    private List<Order> orders;

    @OneToOne(mappedBy = "user" , cascade = CascadeType.ALL)
    private Cart cart;

    @OneToMany(mappedBy = "user" , cascade = CascadeType.ALL ,
            fetch = FetchType.LAZY , orphanRemoval = true)
    private List<Comment> comments;

    @OneToOne(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private RefreshToken refreshToken;

    private boolean emailConfirmation;

    private String confirmationCode;

    private LocalDateTime confirmationCodeExpiresAt;
}