package com.ahmed.E_CommerceApp.dto;


import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class UserSummaryResponse {
    private Long id;
    private String email;
    private String phoneNumber;
    private String role;
}