package com.example.springproject.dto;

import com.example.springproject.model.enums.ActivityStatus;
import com.example.springproject.model.enums.LifeRhythm;
import com.example.springproject.model.enums.Region;
import com.example.springproject.model.AddictionType;
import lombok.Data;

import java.time.LocalDate;
import java.util.Set;

@Data
public class OnboardingCompletionRequest {
    private Region region;
    private ActivityStatus activityStatus;
    private LifeRhythm lifeRhythm;
    private LocalDate sobrietyDate;
    private AddictionType addiction; // Optional, if they want to change it
    private Set<String> triggers;
    private Set<String> copingMechanisms;
    private Set<String> motivations;
}
