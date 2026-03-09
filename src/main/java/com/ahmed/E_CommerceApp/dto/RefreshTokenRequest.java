package com.ahmed.E_CommerceApp.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class RefreshTokenRequest {

    @NotBlank(message = "Refresh token is mandatory")
    private String refreshToken;
}