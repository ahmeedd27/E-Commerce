package com.ahmed.E_CommerceApp.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class EmailConfirmationRequest {
    private String email;
    private String confirmationCode;
}
