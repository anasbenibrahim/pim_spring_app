package com.example.springproject.controller;

import com.example.springproject.dto.*;
import com.example.springproject.repository.PatientRepository;
import com.example.springproject.service.JwtService;
import com.example.springproject.service.PatientGoalService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/v1/goals")
@CrossOrigin(origins = "*")
public class PatientGoalController {

    @Autowired
    private PatientGoalService goalService;
    @Autowired
    private JwtService jwtService;
    @Autowired
    private PatientRepository patientRepository;

    private Long getPatientIdFromToken(String authHeader) {
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            throw new RuntimeException("Invalid or missing token");
        }
        String jwt = authHeader.substring(7);
        Long userId = jwtService.extractUserId(jwt);
        if (userId == null) {
            throw new RuntimeException("Invalid token");
        }
        if (!patientRepository.existsById(userId)) {
            throw new RuntimeException("Only patients can manage goals");
        }
        return userId;
    }

    @PostMapping
    public ResponseEntity<?> createGoal(
            @RequestHeader("Authorization") String authHeader,
            @Valid @RequestBody CreateGoalRequest request) {
        try {
            Long patientId = getPatientIdFromToken(authHeader);
            GoalResponse response = goalService.createGoal(patientId, request);
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new AuthController.ErrorResponse(e.getMessage()));
        }
    }

    @GetMapping
    public ResponseEntity<?> getMyGoals(@RequestHeader("Authorization") String authHeader) {
        try {
            Long patientId = getPatientIdFromToken(authHeader);
            List<GoalResponse> goals = goalService.getGoalsByPatient(patientId);
            return ResponseEntity.ok(goals);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new AuthController.ErrorResponse(e.getMessage()));
        }
    }

    @GetMapping("/gamification")
    public ResponseEntity<?> getGamification(@RequestHeader("Authorization") String authHeader) {
        try {
            Long patientId = getPatientIdFromToken(authHeader);
            GamificationResponse response = goalService.getGamification(patientId);
            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new AuthController.ErrorResponse(e.getMessage()));
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getGoal(
            @RequestHeader("Authorization") String authHeader,
            @PathVariable Long id) {
        try {
            Long patientId = getPatientIdFromToken(authHeader);
            GoalResponse response = goalService.getGoalById(id, patientId);
            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new AuthController.ErrorResponse(e.getMessage()));
        }
    }

    @PostMapping("/{id}/check-in")
    public ResponseEntity<?> addCheckIn(
            @RequestHeader("Authorization") String authHeader,
            @PathVariable Long id,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
        try {
            Long patientId = getPatientIdFromToken(authHeader);
            LocalDate checkInDate = date != null ? date : LocalDate.now();
            GoalResponse response = goalService.addCheckIn(id, patientId, checkInDate);
            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new AuthController.ErrorResponse(e.getMessage()));
        }
    }

    @PostMapping("/{id}/validate")
    public ResponseEntity<?> validateGoal(
            @RequestHeader("Authorization") String authHeader,
            @PathVariable Long id,
            @RequestBody(required = false) ValidateGoalRequest request) {
        try {
            Long patientId = getPatientIdFromToken(authHeader);
            GamificationResponse response = goalService.validateGoal(id, patientId, request);
            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new AuthController.ErrorResponse(e.getMessage()));
        }
    }
}
