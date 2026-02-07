package com.example.springproject.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "family_members")
@Data
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
@PrimaryKeyJoinColumn(name = "user_id")
public class FamilyMember extends User {
    
    @Column(name = "referral_key", nullable = false)
    private String referralKey;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "patient_id", nullable = false)
    private Patient patient;
    
    public FamilyMember(User user, String referralKey, Patient patient) {
        super(user.getId(), user.getEmail(), user.getPassword(), user.getNom(), 
              user.getPrenom(), user.getAge(), user.getRole(), user.getProfileImageUrl(), 
              user.getCreatedAt(), user.getUpdatedAt());
        this.referralKey = referralKey;
        this.patient = patient;
    }
}
