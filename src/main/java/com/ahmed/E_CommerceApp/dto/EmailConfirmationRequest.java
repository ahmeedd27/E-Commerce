package com.ahmed.E_CommerceApp.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class EmailConfirmationRequest {
    private String email;
    private String confirmationCode;
}
