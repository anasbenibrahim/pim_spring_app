package com.example.springproject.dto;

import com.example.springproject.model.GoalCategory;
import com.example.springproject.model.GoalDifficulty;
import com.example.springproject.model.GoalStatus;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class GoalResponse {

    private Long id;
    private GoalCategory category;
    private String title;
    private GoalDifficulty difficulty;
    private Integer targetValue;
    private String targetUnit;
    private Integer initialValue;
    private Integer currentValue;
    private LocalDate startDate;
    private LocalDateTime validatedAt;
    private String validationNote;
    private GoalStatus status;
    private LocalDateTime createdAt;
    private Integer xpReward;
    private List<LocalDate> checkInDates; // days with check-ins
}
