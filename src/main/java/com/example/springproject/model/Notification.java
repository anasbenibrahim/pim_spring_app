package com.example.springproject.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

@Entity
@Table(name = "notifications")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Notification {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(nullable = false)
    private String content;
    
    @Column(nullable = false)
    private String title;
    
    @Column(nullable = false)
    private String type; // e.g., "RISK_ALERT", "SYSTEM", "REMINDER"
    
    @Column(name = "is_read")
    private boolean read = false;
    
    @Column(name = "created_at")
    private LocalDateTime createdAt;
    
    @JsonIgnore  // Don't serialize the full User object (password, lazy collections, etc.)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;
    
    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
    }
}
