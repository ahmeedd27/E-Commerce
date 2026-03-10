package com.ahmed.E_CommerceApp.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.Builder;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CommentDTO {
    private Long id;
    private String content;
    private Integer score;
    private Long userId;
    private String userEmail;    //  added — client needs to show who wrote it
    private LocalDateTime createdAt;
}