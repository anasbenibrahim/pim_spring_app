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

        // Use patientRepository to get the actual Patient entity (important for JOINED
        // inheritance)
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

    @Transactional
    public Patient completeAssessment(String email, com.example.springproject.dto.ProfileAssessmentRequest request) {
        log.info("Completing assessment for user: {}", email);

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));

        Patient patient = patientRepository.findById(user.getId())
                .orElseThrow(() -> new IllegalArgumentException("User is not a patient"));

        if (request.getGender() != null)
            patient.setGender(request.getGender());
        if (request.getHealthGoal() != null)
            patient.setHealthGoal(request.getHealthGoal());
        if (request.getMoodLevel() != null)
            patient.setMoodLevel(request.getMoodLevel());
        if (request.getSleepQuality() != null)
            patient.setSleepQuality(request.getSleepQuality());
        if (request.getStressLevel() != null)
            patient.setStressLevel(request.getStressLevel());
        if (request.getSoughtProfessionalHelp() != null)
            patient.setSoughtProfessionalHelp(request.getSoughtProfessionalHelp());
        if (request.getTakingMedications() != null)
            patient.setTakingMedications(request.getTakingMedications());
        if (request.getMedications() != null)
            patient.setMedications(request.getMedications());
        if (request.getPhysicalDistress() != null)
            patient.setPhysicalDistress(request.getPhysicalDistress());

        if (request.getSymptoms() != null) {
            if (patient.getSymptoms() == null)
                patient.setSymptoms(new java.util.HashSet<>());
            patient.getSymptoms().clear();
            patient.getSymptoms().addAll(request.getSymptoms());
        }
        if (request.getPersonalityTraits() != null) {
            if (patient.getPersonalityTraits() == null)
                patient.setPersonalityTraits(new java.util.HashSet<>());
            patient.getPersonalityTraits().clear();
            patient.getPersonalityTraits().addAll(request.getPersonalityTraits());
        }
        if (request.getMentalHealthConcerns() != null) {
            if (patient.getMentalHealthConcerns() == null)
                patient.setMentalHealthConcerns(new java.util.HashSet<>());
            patient.getMentalHealthConcerns().clear();
            patient.getMentalHealthConcerns().addAll(request.getMentalHealthConcerns());
        }

        // Compute completion score
        int score = 0;
        int total = 10;
        if (patient.getGender() != null)
            score++;
        if (patient.getHealthGoal() != null)
            score++;
        if (patient.getMoodLevel() != null)
            score++;
        if (patient.getSleepQuality() != null)
            score++;
        if (patient.getStressLevel() != null)
            score++;
        if (patient.getSoughtProfessionalHelp() != null)
            score++;
        if (patient.getTakingMedications() != null)
            score++;
        if (patient.getPhysicalDistress() != null)
            score++;
        if (patient.getPersonalityTraits() != null && !patient.getPersonalityTraits().isEmpty())
            score++;
        if (patient.getMentalHealthConcerns() != null && !patient.getMentalHealthConcerns().isEmpty())
            score++;

        patient.setProfileCompletionScore((score * 100) / total);
        patient.setHasCompletedAssessment(score == total);

        return patientRepository.save(patient);
    }
}
