package com.example.springproject.service;

import com.example.springproject.model.FamilyMember;
import com.example.springproject.model.Notification;
import com.example.springproject.model.Patient;
import com.example.springproject.model.User;
import com.example.springproject.repository.FamilyMemberRepository;
import com.example.springproject.repository.NotificationRepository;
import com.example.springproject.repository.PatientRepository;
import com.example.springproject.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class NotificationService {

    private final NotificationRepository notificationRepository;
    private final FamilyMemberRepository familyMemberRepository;
    private final PatientRepository patientRepository;
    private final UserRepository userRepository;

    public List<Notification> getNotificationsForUser(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));
        System.out.println("DEBUG: Fetching notifications for user ID: " + user.getId() + " (" + email + ")");
        return notificationRepository.findByUserIdOrderByCreatedAtDesc(user.getId());
    }

    @Transactional
    public void markAsRead(Long notificationId) {
        Notification notification = notificationRepository.findById(notificationId)
                .orElseThrow(() -> new RuntimeException("Notification not found"));
        notification.setRead(true);
        notificationRepository.save(notification);
    }

    @Transactional
    public void triggerRiskAlert(Long patientId, String riskLevel, double riskScore) {
        System.out.println("=== RISK ALERT TRIGGERED ===");
        System.out.println("DEBUG: Patient ID received: " + patientId);
        
        Patient patient = patientRepository.findById(patientId).orElse(null);
        
        if (patient == null) {
            System.out.println("DEBUG: ERROR - Patient with ID " + patientId + " NOT FOUND in DB!");
            return;
        }

        System.out.println("DEBUG: Patient found → ID=" + patient.getId() + ", Name=" + patient.getNom() + " " + patient.getPrenom());

        // Use direct ID-based query to bypass JPA entity comparison issues
        List<FamilyMember> familyMembers = familyMemberRepository.findByPatientId(patientId);
        System.out.println("DEBUG: Found " + familyMembers.size() + " family member(s)");

        if (familyMembers.isEmpty()) {
            System.out.println("DEBUG: No family members found for patient ID=" + patientId + ". Check family_members table patient_id column.");
            return;
        }

        String title = "Alerte de Risque : " + patient.getNom() + " " + patient.getPrenom();
        String content = String.format("Le système HopeUp a détecté un risque %s (Score: %.1f%%) pour votre proche. Veuillez le contacter.", 
                                        riskLevel.toLowerCase(), riskScore);

        for (FamilyMember family : familyMembers) {
            System.out.println("DEBUG: Saving notification for FamilyMember → ID=" + family.getId() + ", email=" + family.getEmail());
            Notification notification = Notification.builder()
                    .title(title)
                    .content(content)
                    .type("RISK_ALERT")
                    .user(family)
                    .read(false)
                    .build();
            notificationRepository.save(notification);
            System.out.println("DEBUG: Notification saved for user ID=" + family.getId());
        }
        System.out.println("=== RISK ALERT DONE ===");
    }
}
