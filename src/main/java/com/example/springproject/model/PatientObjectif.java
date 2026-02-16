package com.example.springproject.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "patient_objectifs")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class PatientObjectif {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "patient_id", nullable = false)
    private Patient patient;

    @Column(name = "objectif_date", nullable = false)
    private LocalDate objectifDate;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private MoodType mood;

    @Column(name = "consumed", nullable = false)
    private Boolean consumed;

    @Column(name = "notes")
    private String notes;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
    }
}
