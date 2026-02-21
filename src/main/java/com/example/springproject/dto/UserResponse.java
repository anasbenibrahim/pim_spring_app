package com.example.springproject.dto;

import com.example.springproject.model.UserRole;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserResponse {
    private Long id;
    private String email;
    private String nom;
    private String prenom;
    private Integer age;
    private UserRole role;
    private String profileImageUrl;
    private LocalDateTime createdAt;
    private String referralCode; // Only for patients
    private Boolean hasCompletedOnboarding;
    private int profileCompletionScore;
    private Boolean hasCompletedAssessment;
    private String addiction;
    private java.time.LocalDate sobrietyDate;
    // Patient information for family members
    private Long patientId;
    private String patientNom;
    private String patientPrenom;
}
