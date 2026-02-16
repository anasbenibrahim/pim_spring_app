package com.example.springproject.service;

import com.example.springproject.dto.WeeklyAchievementResponse;
import com.example.springproject.model.AchievementBadge;
import com.example.springproject.model.MoodType;
import com.example.springproject.model.PatientObjectif;
import com.example.springproject.repository.PatientObjectifRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class WeeklyAchievementService {

    @Autowired
    private PatientObjectifRepository objectifRepository;

    /**
     * Calcule le badge hebdomadaire pour un patient.
     * Règles:
     * - Si 4+ jours consommés dans la semaine -> REBOND
     * - Si 4+ jours abstinents: humeur dominante happy/calm -> CHAMPION, anxious -> COURAGEUX, sad -> REBOND
     * - Si < 4 jours avec données -> pas de badge cohérent, on retourne REBOND par défaut
     */
    public WeeklyAchievementResponse computeWeeklyAchievement(Long patientId, LocalDate weekStart) {
        LocalDate weekEnd = weekStart.plusDays(6);

        List<PatientObjectif> objectifs = objectifRepository
                .findByPatientIdAndObjectifDateBetween(patientId, weekStart, weekEnd);

        // Grouper par date (un objectif par jour, prendre le dernier si plusieurs)
        Map<LocalDate, PatientObjectif> byDate = objectifs.stream()
                .collect(Collectors.toMap(PatientObjectif::getObjectifDate, o -> o, (a, b) -> b));

        int consumedDays = (int) byDate.values().stream().filter(PatientObjectif::getConsumed).count();
        int abstinentDays = (int) byDate.values().stream().filter(o -> !o.getConsumed()).count();
        int totalDaysWithData = byDate.size();

        AchievementBadge badge;
        String label;
        String description;

        if (consumedDays >= 4) {
            badge = AchievementBadge.REBOND;
            label = "Bounce Back";
            description = "4+ days with consumption this week. Every day is a new chance.";
        } else if (abstinentDays >= 4) {
            List<MoodType> abstinentMoods = byDate.values().stream()
                    .filter(o -> !o.getConsumed())
                    .map(PatientObjectif::getMood)
                    .collect(Collectors.toList());

            MoodType dominantMood = getDominantMood(abstinentMoods);

            if (dominantMood == MoodType.HAPPY || dominantMood == MoodType.CALM) {
                badge = AchievementBadge.CHAMPION;
                label = "Champion";
                description = "Great week! 4+ days abstinent with a positive mood.";
            } else if (dominantMood == MoodType.ANXIOUS) {
                badge = AchievementBadge.COURAGEUX;
                label = "Courageous";
                description = "4+ days abstinent despite anxiety. You're making progress!";
            } else {
                badge = AchievementBadge.REBOND;
                label = "Bounce Back";
                description = "4+ days abstinent. Your mood was tough - every day counts.";
            }
        } else {
            badge = AchievementBadge.REBOND;
            label = "Bounce Back";
            description = "Not enough data this week. Keep recording your goals.";
        }

        return new WeeklyAchievementResponse(
                badge,
                label,
                description,
                weekStart,
                weekEnd,
                abstinentDays,
                consumedDays,
                totalDaysWithData
        );
    }

    private MoodType getDominantMood(List<MoodType> moods) {
        if (moods.isEmpty()) return MoodType.CALM;
        Map<MoodType, Long> counts = moods.stream()
                .collect(Collectors.groupingBy(m -> m, Collectors.counting()));
        return counts.entrySet().stream()
                .max(Map.Entry.comparingByValue())
                .map(Map.Entry::getKey)
                .orElse(MoodType.CALM);
    }

    /**
     * Retourne le lundi de la semaine contenant la date donnée.
     */
    public static LocalDate getWeekStart(LocalDate date) {
        return date.with(DayOfWeek.MONDAY);
    }
}
