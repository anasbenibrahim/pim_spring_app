package com.example.springproject.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "game_logs")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class GameLog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(nullable = false)
    private String gameType;

    @CreationTimestamp
    private LocalDateTime timestamp;

    @ElementCollection
    @CollectionTable(name = "game_log_raw_data", joinColumns = @JoinColumn(name = "game_log_id"))
    @Column(name = "value")
    @Builder.Default
    private List<Integer> rawData = new ArrayList<>();

    @Column(nullable = false)
    private String derivedState; // "Anxious", "Fatigued", or "Balanced"
}
