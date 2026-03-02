package com.example.springproject.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "patient_goals")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class PatientGoal {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "patient_id", nullable = false)
    private Patient patient;

    @Enumerated(EnumType.STRING)
    @Column(name = "category", nullable = false)
    private GoalCategory category;

    @Column(name = "title", nullable = false)
    private String title;

    @Enumerated(EnumType.STRING)
    @Column(name = "difficulty", nullable = false)
    private GoalDifficulty difficulty;

    @Column(name = "target_value")
    private Integer targetValue; // e.g. 7 for 7 days, 30 for 30 days

    @Column(name = "target_unit")
    private String targetUnit; // e.g. "days", "cigarettes", "hours", "sessions"

    @Column(name = "initial_value")
    private Integer initialValue; // for reduction: e.g. 10 cigarettes

    @Column(name = "current_value", nullable = false)
    private Integer currentValue = 0;

    @Column(name = "start_date", nullable = false)
    private LocalDate startDate;

    @Column(name = "validated_at")
    private LocalDateTime validatedAt;

    @Column(name = "validation_note")
    private String validationNote;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private GoalStatus status = GoalStatus.IN_PROGRESS;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
    }
}
