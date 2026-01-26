package com.ahmed.E_CommerceApp.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import lombok.Data;
import lombok.NonNull;

@Data
public class CommentDTO {
    private Long id;
    @NotEmpty(message = "content is required")
    @NotBlank(message = "content is required")
    private String content;
    @Max(value=5)
    @Min(value=1)
    private Integer score;
    private Long user;
}
