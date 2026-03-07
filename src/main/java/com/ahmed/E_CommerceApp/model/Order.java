package com.ahmed.E_CommerceApp.model;

import com.ahmed.E_CommerceApp.model.enums.OrderStatus;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString(exclude = {"items", "payment", "user"})
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@Entity
public class Order {

    @EqualsAndHashCode.Include
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String address;

    private String phoneNumber; // Kept here too — delivery contact can differ from user's default

    private BigDecimal totalPrice; //  Added: intentional de_normalization for fast reads

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL, fetch = FetchType.LAZY
            , orphanRemoval = true)
    private List<OrderItems> items;

    @OneToOne(mappedBy = "order", cascade = CascadeType.ALL)
    private Payment payment; //  Added: link to payment

    @Enumerated(EnumType.STRING)
    private OrderStatus status;

    private LocalDateTime createdAt;


}