package com.ahmed.E_CommerceApp.model;

import com.ahmed.E_CommerceApp.model.enums.Role;
import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import lombok.*;
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

    @NotBlank // @NotBlank already implies @NotEmpty
    @Email
    private String email;

    @NotBlank // @NotBlank already implies @NotEmpty
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