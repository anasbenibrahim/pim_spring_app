package com.example.springproject.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "temporary_registrations")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class TemporaryRegistration {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(nullable = false)
    private String email;
    
    @Column(nullable = false, columnDefinition = "TEXT")
    private String registrationData; // JSON string containing registration data
    
    @Column(nullable = false)
    private String userRole; // PATIENT, VOLONTAIRE, FAMILY_MEMBER
    
    @Column(nullable = false)
    private LocalDateTime expiresAt;
    
    @Column(nullable = false)
    private LocalDateTime createdAt;
    
    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
    }
}
