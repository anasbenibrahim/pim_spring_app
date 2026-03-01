package com.example.springproject.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class CommentRequest {
    @NotBlank
    @Size(max = 500, message = "Comment cannot exceed 500 characters")
    private String content;
    
    private Long parentCommentId; // null if top-level comment
}
