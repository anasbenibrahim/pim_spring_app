package com.example.springproject.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Set;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProfileAssessmentRequest {
    private String gender;
    private String healthGoal;
    private Integer moodLevel;
    private Integer sleepQuality;
    private Integer stressLevel;
    private Boolean soughtProfessionalHelp;
    private Boolean takingMedications;
    private String medications;
    private Boolean physicalDistress;
    private Set<String> symptoms;
    private Set<String> personalityTraits;
    private Set<String> mentalHealthConcerns;
}
