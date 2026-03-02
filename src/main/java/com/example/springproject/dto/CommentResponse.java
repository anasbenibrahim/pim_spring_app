package com.example.springproject.dto;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
public class CommentResponse {
    private Long id;
    private String content;
    private String pseudonym;
    private Long postId;
    private Long parentCommentId;
    private LocalDateTime createdAt;
    private List<CommentResponse> replies;
}
