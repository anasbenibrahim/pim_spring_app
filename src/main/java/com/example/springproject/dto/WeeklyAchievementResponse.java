package com.example.springproject.dto;

import com.example.springproject.model.AchievementBadge;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class WeeklyAchievementResponse {

    private AchievementBadge badge;
    private String badgeLabel;
    private String badgeDescription;
    private LocalDate weekStart;
    private LocalDate weekEnd;
    private int abstinentDays;
    private int consumedDays;
    private int totalDaysWithData;
}
