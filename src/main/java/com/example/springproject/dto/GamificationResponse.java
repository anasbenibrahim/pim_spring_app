package com.example.springproject.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class GamificationResponse {
    private int totalXp;
    private int level;
    private String levelTitle;
    private int xpToNextLevel;
    private double progressToNextLevel; // 0.0 to 1.0
    private List<String> unlockedBadges;
    private String motivationalMessage;
}
