package com.example.springproject.controller;

import com.example.springproject.model.Notification;
import com.example.springproject.service.NotificationService;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/notifications")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class NotificationController {

    private final NotificationService notificationService;

    @GetMapping
    public ResponseEntity<List<Notification>> getMyNotifications() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String email = authentication.getName();
        return ResponseEntity.ok(notificationService.getNotificationsForUser(email));
    }

    @PatchMapping("/{id}/read")
    public ResponseEntity<Void> markAsRead(@PathVariable Long id) {
        notificationService.markAsRead(id);
        return ResponseEntity.ok().build();
    }

    /**
     * Endpoint called by n8n to trigger a risk alert notification for family members.
     * In a production environment, this should be secured with an API Key or internal IP restriction.
     */
    @PostMapping("/risk-alert")
    public ResponseEntity<Void> triggerRiskAlert(@RequestBody RiskAlertRequest request) {
        notificationService.triggerRiskAlert(request.getPatientId(), request.getRiskLevel(), request.getRiskScore());
        return ResponseEntity.ok().build();
    }

    @Data
    public static class RiskAlertRequest {
        private Long patientId;
        private String riskLevel;
        private double riskScore;
    }
}
