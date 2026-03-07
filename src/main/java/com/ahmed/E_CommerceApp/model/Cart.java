package com.ahmed.E_CommerceApp.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Cart {
    @Id
    @GeneratedValue(strategy= GenerationType.IDENTITY)
    private Long id;

    @OneToOne
    @JoinColumn(name = "user_id" , nullable = false)
    private User user;

    @OneToMany(mappedBy = "cart" , cascade = CascadeType.ALL , orphanRemoval = true
    , fetch = FetchType.LAZY)
    private List<CartItems> items;

    private LocalDateTime updatedAt; //  Added: track last cart activity (abandoned cart logic)
}
