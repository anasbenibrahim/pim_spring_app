package com.example.springproject.dto;

import com.example.springproject.model.enums.ReactionType;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class ReactionRequest {
    @NotNull
    private ReactionType reactionType;
}
