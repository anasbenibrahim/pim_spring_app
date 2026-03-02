package com.example.springproject.dto;

import lombok.Data;

import java.util.List;

@Data
public class AssessmentRequest {
    private String gender;
    private String healthGoal;
    private Integer moodLevel;
    private Integer sleepQuality;
    private Integer stressLevel;
    private Boolean soughtProfessionalHelp;
    private Boolean takingMedications;
    private String medications;
    private Boolean physicalDistress;
    private List<String> symptoms;
    private List<String> personalityTraits;
    private List<String> mentalHealthConcerns;
}
