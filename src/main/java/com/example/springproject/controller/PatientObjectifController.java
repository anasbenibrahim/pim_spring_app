package com.example.springproject.controller;

import com.example.springproject.dto.CreateObjectifRequest;
import com.example.springproject.dto.ObjectifResponse;
import com.example.springproject.dto.WeeklyAchievementResponse;
import com.example.springproject.service.JwtService;
import com.example.springproject.service.PatientObjectifService;
import com.example.springproject.service.WeeklyAchievementService;
import com.example.springproject.repository.PatientRepository;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/objectifs")
@CrossOrigin(origins = "*")
public class PatientObjectifController {

    @Autowired
    private PatientObjectifService objectifService;

    @Autowired
    private WeeklyAchievementService achievementService;

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
            throw new RuntimeException("Only patients can manage objectifs");
        }
        return userId;
    }

    @PostMapping
    public ResponseEntity<?> createObjectif(
            @RequestHeader("Authorization") String authHeader,
            @Valid @RequestBody CreateObjectifRequest request) {
        try {
            Long patientId = getPatientIdFromToken(authHeader);
            ObjectifResponse response = objectifService.createObjectif(patientId, request);
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new AuthController.ErrorResponse(e.getMessage()));
        }
    }

    @GetMapping
    public ResponseEntity<?> getMyObjectifs(@RequestHeader("Authorization") String authHeader) {
        try {
            Long patientId = getPatientIdFromToken(authHeader);
            List<ObjectifResponse> objectifs = objectifService.getObjectifsByPatient(patientId);
            return ResponseEntity.ok(objectifs);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new AuthController.ErrorResponse(e.getMessage()));
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getObjectif(
            @RequestHeader("Authorization") String authHeader,
            @PathVariable Long id) {
        try {
            Long patientId = getPatientIdFromToken(authHeader);
            ObjectifResponse response = objectifService.getObjectifById(id, patientId);
            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new AuthController.ErrorResponse(e.getMessage()));
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteObjectif(
            @RequestHeader("Authorization") String authHeader,
            @PathVariable Long id) {
        try {
            Long patientId = getPatientIdFromToken(authHeader);
            objectifService.deleteObjectif(id, patientId);
            return ResponseEntity.ok().build();
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new AuthController.ErrorResponse(e.getMessage()));
        }
    }

    @GetMapping("/achievement/weekly")
    public ResponseEntity<?> getWeeklyAchievement(
            @RequestHeader("Authorization") String authHeader,
            @RequestParam(required = false) String weekStart) {
        try {
            Long patientId = getPatientIdFromToken(authHeader);
            LocalDate weekStartDate = weekStart != null
                    ? LocalDate.parse(weekStart)
                    : WeeklyAchievementService.getWeekStart(LocalDate.now());
            WeeklyAchievementResponse response = achievementService.computeWeeklyAchievement(patientId, weekStartDate);
            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new AuthController.ErrorResponse(e.getMessage()));
        }
    }
}
