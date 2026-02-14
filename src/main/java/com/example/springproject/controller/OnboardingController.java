package com.example.springproject.controller;

import com.example.springproject.dto.OnboardingCompletionRequest;
import com.example.springproject.model.Patient;
import com.example.springproject.service.OnboardingService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.CrossOrigin;

@RestController
@RequestMapping("/api/v1/onboarding")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class OnboardingController {

    private final OnboardingService onboardingService;

    @PostMapping("/complete")
    public ResponseEntity<Patient> completeOnboarding(@RequestBody OnboardingCompletionRequest request) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String email = authentication.getName();
        
        Patient updatedPatient = onboardingService.completeOnboarding(email, request);
        return ResponseEntity.ok(updatedPatient);
    }
}
