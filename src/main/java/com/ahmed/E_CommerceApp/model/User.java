package com.ahmed.E_CommerceApp.model;

import com.ahmed.E_CommerceApp.model.enums.Role;
import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import lombok.*;
import java.util.List;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class User {
    @Id
    @GeneratedValue(strategy= GenerationType.IDENTITY)
    private Long id;
    @Email
    @NotBlank
    @NotEmpty
    private String email;
    @NotBlank
    @NotEmpty
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
    private boolean emailConfirmation;
    private String confirmationCode;




}