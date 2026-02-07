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
    
    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private AddictionType addiction;
    
    @Column(name = "referral_key", unique = true, nullable = false)
    private String referralKey;
    
    public Patient(User user, LocalDate dateNaissance, LocalDate sobrietyDate, AddictionType addiction, String referralKey) {
        super(user.getId(), user.getEmail(), user.getPassword(), user.getNom(), 
              user.getPrenom(), user.getAge(), user.getRole(), user.getProfileImageUrl(), 
              user.getCreatedAt(), user.getUpdatedAt());
        this.dateNaissance = dateNaissance;
        this.sobrietyDate = sobrietyDate;
        this.addiction = addiction;
        this.referralKey = referralKey;
    }
}
