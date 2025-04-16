package com.ahmed.E_CommerceApp.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class LoginRequest {
    @NotEmpty(message = "should not being empty")
    @NotBlank(message = "not mandatory")
    @Email(message="email is not formatted")
    private String email;
    @NotEmpty(message = "should not being empty")
    @NotBlank(message = "not mandatory")
    @Size(min=8 , message="Password should b characters long minimum")
    private String password;
}
