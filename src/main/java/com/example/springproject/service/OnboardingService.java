package com.example.springproject.service;

import com.example.springproject.dto.OnboardingCompletionRequest;
import com.example.springproject.model.Patient;
import com.example.springproject.model.User;
import com.example.springproject.repository.PatientRepository;
import com.example.springproject.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class OnboardingService {

    private final PatientRepository patientRepository;
    private final UserRepository userRepository;

    @Transactional
    public Patient completeOnboarding(String email, OnboardingCompletionRequest request) {
        log.info("Completing onboarding for user: {}", email);
        log.info("Request data: {}", request);
        
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));

        // Use patientRepository to get the actual Patient entity (important for JOINED inheritance)
        Patient patient = patientRepository.findById(user.getId())
                .orElseThrow(() -> new IllegalArgumentException("User with email " + email + " is not a patient"));

        if (request.getRegion() != null) {
            patient.setRegion(request.getRegion());
        }
        
        if (request.getActivityStatus() != null) {
            patient.setActivityStatus(request.getActivityStatus());
        }
        
        if (request.getLifeRhythm() != null) {
            patient.setLifeRhythm(request.getLifeRhythm());
        }
        
        if (request.getSobrietyDate() != null) {
            patient.setSobrietyDate(request.getSobrietyDate());
        }
        
        if (request.getAddiction() != null) {
            patient.setAddiction(request.getAddiction());
        }
        
        if (request.getTriggers() != null) {
            if (patient.getTriggers() == null) {
                patient.setTriggers(new java.util.HashSet<>());
            }
            patient.getTriggers().clear();
            patient.getTriggers().addAll(request.getTriggers());
        }
        
        if (request.getCopingMechanisms() != null) {
            if (patient.getCopingMechanisms() == null) {
                patient.setCopingMechanisms(new java.util.HashSet<>());
            }
            patient.getCopingMechanisms().clear();
            patient.getCopingMechanisms().addAll(request.getCopingMechanisms());
        }
        
        if (request.getMotivations() != null) {
            if (patient.getMotivations() == null) {
                patient.setMotivations(new java.util.HashSet<>());
            }
            patient.getMotivations().clear();
            patient.getMotivations().addAll(request.getMotivations());
        }

        patient.setHasCompletedOnboarding(true);

        return patientRepository.save(patient);
    }
}
