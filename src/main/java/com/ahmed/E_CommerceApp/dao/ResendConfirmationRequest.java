package com.ahmed.E_CommerceApp.dao;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class ResendConfirmationRequest {

    @NotBlank(message = "Email is mandatory")
    @Email(message = "Email format is invalid")
    private String email;
}