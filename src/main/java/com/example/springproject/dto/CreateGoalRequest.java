package com.example.springproject.dto;

import com.example.springproject.model.GoalCategory;
import com.example.springproject.model.GoalDifficulty;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CreateGoalRequest {

    @NotNull(message = "Category is required")
    private GoalCategory category;

    @NotBlank(message = "Title is required")
    private String title;

    @NotNull(message = "Difficulty is required")
    private GoalDifficulty difficulty;

    @NotNull(message = "Target value is required")
    private Integer targetValue;

    private String targetUnit; // "days", "cigarettes", "hours", "sessions", etc.

    private Integer initialValue; // for reduction goals: starting value
}
