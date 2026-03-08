package com.ahmed.E_CommerceApp.dto;


import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class LoginResponse {
    private String token;
    private String type;
    private String email;
    private String role;
}