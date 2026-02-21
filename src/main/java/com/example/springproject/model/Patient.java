package com.example.springproject.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Entity
@Table(name = "patients")
@Data
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
@PrimaryKeyJoinColumn(name = "user_id")
public class Patient extends User {

    @Column(name = "date_naissance")
    private LocalDate dateNaissance;

    @Column(name = "sobriety_date")
    private LocalDate sobrietyDate;

    @Column(nullable = true)
    @Enumerated(EnumType.STRING)
    private AddictionType addiction;

    @Column(name = "referral_key", unique = true, nullable = false)
    private String referralKey;

    @Enumerated(EnumType.STRING)
    @Column(name = "region")
    private com.example.springproject.model.enums.Region region;

    @Enumerated(EnumType.STRING)
    @Column(name = "activity_status")
    private com.example.springproject.model.enums.ActivityStatus activityStatus;

    @Enumerated(EnumType.STRING)
    @Column(name = "life_rhythm")
    private com.example.springproject.model.enums.LifeRhythm lifeRhythm;

    @ElementCollection
    @CollectionTable(name = "patient_triggers", joinColumns = @JoinColumn(name = "patient_id"))
    @Column(name = "trigger_name")
    private java.util.Set<String> triggers = new java.util.HashSet<>();

    @ElementCollection
    @CollectionTable(name = "patient_coping_mechanisms", joinColumns = @JoinColumn(name = "patient_id"))
    @Column(name = "mechanism")
    private java.util.Set<String> copingMechanisms = new java.util.HashSet<>();

    @ElementCollection
    @CollectionTable(name = "patient_motivations", joinColumns = @JoinColumn(name = "patient_id"))
    @Column(name = "motivation")
    private java.util.Set<String> motivations = new java.util.HashSet<>();

    @Column(name = "has_completed_onboarding")
    private Boolean hasCompletedOnboarding = false;

    // ─── Assessment Fields ───
    @Column(name = "gender")
    private String gender;

    @Column(name = "health_goal")
    private String healthGoal;

    @Column(name = "mood_level")
    private Integer moodLevel;

    @Column(name = "sleep_quality")
    private Integer sleepQuality;

    @Column(name = "stress_level")
    private Integer stressLevel;

    @Column(name = "sought_professional_help")
    private Boolean soughtProfessionalHelp;

    @Column(name = "taking_medications")
    private Boolean takingMedications;

    @Column(name = "medications")
    private String medications;

    @Column(name = "physical_distress")
    private Boolean physicalDistress;

    @ElementCollection
    @CollectionTable(name = "patient_symptoms", joinColumns = @JoinColumn(name = "patient_id"))
    @Column(name = "symptom")
    private java.util.Set<String> symptoms = new java.util.HashSet<>();

    @ElementCollection
    @CollectionTable(name = "patient_personality_traits", joinColumns = @JoinColumn(name = "patient_id"))
    @Column(name = "trait")
    private java.util.Set<String> personalityTraits = new java.util.HashSet<>();

    @ElementCollection
    @CollectionTable(name = "patient_mental_health_concerns", joinColumns = @JoinColumn(name = "patient_id"))
    @Column(name = "concern")
    private java.util.Set<String> mentalHealthConcerns = new java.util.HashSet<>();

    @Column(name = "profile_completion_score")
    private Integer profileCompletionScore = 0;

    @Column(name = "has_completed_assessment")
    private Boolean hasCompletedAssessment = false;

    public Patient(User user, LocalDate dateNaissance, LocalDate sobrietyDate, AddictionType addiction,
            String referralKey) {
        super(user.getId(), user.getEmail(), user.getPassword(), user.getNom(),
                user.getPrenom(), user.getAge(), user.getRole(), user.getProfileImageUrl(),
                user.getCreatedAt(), user.getUpdatedAt());
        this.dateNaissance = dateNaissance;
        this.sobrietyDate = sobrietyDate;
        this.addiction = addiction;
        this.referralKey = referralKey;
    }
}
