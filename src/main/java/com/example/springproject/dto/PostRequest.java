package com.example.springproject.dto;

import com.example.springproject.model.enums.MoodEmoji;
import com.example.springproject.model.enums.PostCategory;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class PostRequest {
    @NotBlank
    @Size(max = 2000, message = "Content cannot exceed 2000 characters")
    private String content;

    private String mediaUrl;

    @NotNull
    private PostCategory category;

    @NotNull
    private MoodEmoji moodEmoji;
}
