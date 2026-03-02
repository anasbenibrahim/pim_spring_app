package com.example.springproject.dto;

import com.example.springproject.model.enums.ReactionType;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ReactionResponse {
    private ReactionType reactionType;
    private long count;
    private boolean userReacted; // true if the current user reacted with this type
}
