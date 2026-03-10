package com.ahmed.E_CommerceApp.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

import java.time.LocalDateTime;


@Data
public class CommentDTO {
    private Long id;
    @NotBlank(message = "content is required")
    private String content;
    @Max(value=5)
    @Min(value=1)
    private Integer score;
    private Long userId;
    private LocalDateTime createdAt;
}
