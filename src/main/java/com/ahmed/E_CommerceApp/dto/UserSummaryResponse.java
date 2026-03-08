package com.ahmed.E_CommerceApp.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

// dto/UserSummaryResponse.java
@Data
@AllArgsConstructor
@Builder
public class UserSummaryResponse {
    private Long id;
    private String email;
    private String phoneNumber;
    private String role;
}