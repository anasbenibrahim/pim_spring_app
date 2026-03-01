package com.example.springproject.dto;

import com.example.springproject.model.enums.MoodEmoji;
import com.example.springproject.model.enums.PostCategory;
import com.example.springproject.model.enums.PostStatus;
import com.example.springproject.model.UserRole;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class PostResponse {
    private Long id;
    private String content;
    private String mediaUrl;
    private PostCategory category;
    private MoodEmoji moodEmoji;
    private String pseudonym;
    private PostStatus status;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private UserRole authorRole;
    // We intentionally EXACTLY EXCLUDE real User data for safety.
}
