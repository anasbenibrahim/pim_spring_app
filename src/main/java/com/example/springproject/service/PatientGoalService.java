package com.example.springproject.service;

import com.example.springproject.dto.*;
import com.example.springproject.model.*;
import com.example.springproject.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class PatientGoalService {

    private static final int[] LEVEL_THRESHOLDS = {0, 50, 150, 300, 500, 800, 1200, 1800, 2500, 3500};
    private static final String[] LEVEL_TITLES = {"Beginner", "Fighter", "Warrior", "Challenger", "Resilient", "Strong", "Champion", "Legend", "Freedom Hero", "Freedom Master"};
    private static final String[] MOTIVATIONAL_MESSAGES = {
        "You did it. Every step counts.",
        "You are stronger than you think.",
        "Every day without consumption is a victory.",
        "You are building your freedom, one decision at a time.",
        "The brain heals. You are giving it a chance.",
        "Step by step, you are moving forward.",
        "You chose health. Well done.",
        "Every goal achieved proves you can.",
        "You are not alone. And you are progressing.",
        "Today, you won. Keep going."
    };

    private static final String BADGE_FIRST_24H = "first_24h_clean";
    private static final String BADGE_7_DAYS = "seven_days_streak";
    private static final String BADGE_30_DAYS = "thirty_days_strong";
    private static final String BADGE_MIND_CONTROL = "mind_control_master";
    private static final String BADGE_REDUCTION_PRO = "reduction_pro";
    private static final String BADGE_CONSISTENCY_KING = "consistency_king";

    @Autowired
    private PatientGoalRepository goalRepository;
    @Autowired
    private GoalCheckInRepository checkInRepository;
    @Autowired
    private PatientRepository patientRepository;

    @Transactional
    public GoalResponse createGoal(Long patientId, CreateGoalRequest request) {
        Patient patient = patientRepository.findById(patientId)
                .orElseThrow(() -> new RuntimeException("Patient not found"));
        if (patient.getRole() != UserRole.PATIENT) {
            throw new RuntimeException("Only patients can create goals");
        }

        PatientGoal goal = new PatientGoal();
        goal.setPatient(patient);
        goal.setCategory(request.getCategory());
        goal.setTitle(request.getTitle());
        goal.setDifficulty(request.getDifficulty());
        goal.setTargetValue(request.getTargetValue());
        goal.setTargetUnit(request.getTargetUnit() != null ? request.getTargetUnit() : "days");
        goal.setInitialValue(request.getInitialValue());
        goal.setCurrentValue(0);
        goal.setStartDate(LocalDate.now());
        goal.setStatus(GoalStatus.IN_PROGRESS);

        goal = goalRepository.save(goal);
        return toResponse(goal);
    }

    public List<GoalResponse> getGoalsByPatient(Long patientId) {
        List<PatientGoal> goals = goalRepository.findByPatientIdOrderByCreatedAtDesc(patientId);
        return goals.stream().map(this::toResponse).collect(Collectors.toList());
    }

    public GoalResponse getGoalById(Long goalId, Long patientId) {
        PatientGoal goal = goalRepository.findById(goalId)
                .orElseThrow(() -> new RuntimeException("Goal not found"));
        if (!goal.getPatient().getId().equals(patientId)) {
            throw new RuntimeException("Goal not found");
        }
        // currentValue is set by addCheckIn (check-in count); do NOT overwrite with calendar days
        // (that was causing validation to fail: same-day check-in gave currentValue=1, then getGoalById
        // overwrote it to 0, so validateGoal saw 0 and rejected)
        return toResponse(goal);
    }

    @Transactional
    public GoalResponse addCheckIn(Long goalId, Long patientId, LocalDate date) {
        PatientGoal goal = goalRepository.findById(goalId)
                .orElseThrow(() -> new RuntimeException("Goal not found"));
        if (!goal.getPatient().getId().equals(patientId)) {
            throw new RuntimeException("Goal not found");
        }
        if (goal.getStatus() != GoalStatus.IN_PROGRESS) {
            throw new RuntimeException("Cannot check in for a completed goal");
        }

        if (checkInRepository.existsByGoalIdAndCheckInDate(goalId, date)) {
            return toResponse(goal); // already checked in
        }

        long dayIndex = ChronoUnit.DAYS.between(goal.getStartDate(), date) + 1;
        if (dayIndex < 1 || dayIndex > goal.getTargetValue()) {
            throw new RuntimeException("Invalid check-in date");
        }

        GoalCheckIn checkIn = new GoalCheckIn();
        checkIn.setGoal(goal);
        checkIn.setCheckInDate(date);
        checkIn.setDayIndex((int) dayIndex);
        checkInRepository.save(checkIn);

        goal.setCurrentValue((int) dayIndex);
        goal = goalRepository.save(goal);
        return toResponse(goal);
    }

    @Transactional
    public GamificationResponse validateGoal(Long goalId, Long patientId, ValidateGoalRequest request) {
        PatientGoal goal = goalRepository.findById(goalId)
                .orElseThrow(() -> new RuntimeException("Goal not found"));
        if (!goal.getPatient().getId().equals(patientId)) {
            throw new RuntimeException("Goal not found");
        }
        if (goal.getStatus() != GoalStatus.IN_PROGRESS) {
            throw new RuntimeException("Goal is already completed");
        }

        // Verify goal is achievable.
        // Time-based: use check-in count as source of truth (currentValue can be stale if getGoalById overwrote it).
        // Allow if check-ins >= target OR calendar days >= target.
        boolean canValidate = false;
        if (goal.getCategory() == GoalCategory.TIME_BASED) {
            long checkInCount = checkInRepository.findByGoalIdOrderByCheckInDateAsc(goal.getId()).size();
            long days = ChronoUnit.DAYS.between(goal.getStartDate(), LocalDate.now());
            canValidate = checkInCount >= goal.getTargetValue() || days >= goal.getTargetValue();
        } else {
            canValidate = goal.getCurrentValue() >= goal.getTargetValue();
        }
        if (!canValidate) {
            throw new RuntimeException("Goal target not yet reached. Keep going!");
        }

        goal.setStatus(GoalStatus.VALIDATED);
        goal.setValidatedAt(LocalDateTime.now());
        goal.setValidationNote(request != null ? request.getNote() : null);
        goalRepository.save(goal);

        Patient patient = goal.getPatient();
        int xp = goal.getDifficulty().getXpReward();
        patient.setTotalXp(patient.getTotalXp() + xp);

        Set<String> badges = patient.getUnlockedBadges() != null ? new HashSet<>(patient.getUnlockedBadges()) : new HashSet<>();

        // Check badge conditions
        if (goal.getCategory() == GoalCategory.TIME_BASED && goal.getTargetValue() == 1 && !badges.contains(BADGE_FIRST_24H)) {
            badges.add(BADGE_FIRST_24H); // First 24h Clean
        }
        if (goal.getCategory() == GoalCategory.TIME_BASED && goal.getTargetValue() >= 7 && !badges.contains(BADGE_7_DAYS)) {
            badges.add(BADGE_7_DAYS);
        }
        if (goal.getCategory() == GoalCategory.TIME_BASED && goal.getTargetValue() >= 30 && !badges.contains(BADGE_30_DAYS)) {
            badges.add(BADGE_30_DAYS);
        }
        if (goal.getCategory() == GoalCategory.ALTERNATIVE_BEHAVIOR) {
            long altCount = goalRepository.findByPatientIdAndStatus(patientId, GoalStatus.VALIDATED).stream()
                    .filter(g -> g.getCategory() == GoalCategory.ALTERNATIVE_BEHAVIOR).count();
            if (altCount >= 3 && !badges.contains(BADGE_MIND_CONTROL)) {
                badges.add(BADGE_MIND_CONTROL);
            }
        }
        if (goal.getCategory() == GoalCategory.REDUCTION_BASED) {
            long redCount = goalRepository.findByPatientIdAndStatus(patientId, GoalStatus.VALIDATED).stream()
                    .filter(g -> g.getCategory() == GoalCategory.REDUCTION_BASED).count();
            if (redCount >= 2 && !badges.contains(BADGE_REDUCTION_PRO)) {
                badges.add(BADGE_REDUCTION_PRO);
            }
        }
        long totalValidated = goalRepository.findByPatientIdAndStatus(patientId, GoalStatus.VALIDATED).size();
        if (totalValidated >= 5 && !badges.contains(BADGE_CONSISTENCY_KING)) {
            badges.add(BADGE_CONSISTENCY_KING);
        }

        patient.setUnlockedBadges(badges);
        patientRepository.save(patient);

        String message = MOTIVATIONAL_MESSAGES[new Random().nextInt(MOTIVATIONAL_MESSAGES.length)];
        GamificationResponse resp = buildGamificationResponse(patient);
        resp.setMotivationalMessage(message);
        return resp;
    }

    public GamificationResponse getGamification(Long patientId) {
        Patient patient = patientRepository.findById(patientId)
                .orElseThrow(() -> new RuntimeException("Patient not found"));
        return buildGamificationResponse(patient);
    }

    private GamificationResponse buildGamificationResponse(Patient patient) {
        int totalXp = patient.getTotalXp();
        int level = computeLevel(totalXp);
        int xpToNext = level < LEVEL_THRESHOLDS.length - 1
                ? LEVEL_THRESHOLDS[level + 1] - totalXp
                : 0;
        double progress = level < LEVEL_THRESHOLDS.length - 1
                ? (double) (totalXp - LEVEL_THRESHOLDS[level]) / (LEVEL_THRESHOLDS[level + 1] - LEVEL_THRESHOLDS[level])
                : 1.0;

        List<String> badges = patient.getUnlockedBadges() != null
                ? new ArrayList<>(patient.getUnlockedBadges())
                : List.of();

        return new GamificationResponse(
                totalXp,
                level + 1,
                LEVEL_TITLES[level],
                Math.max(0, xpToNext),
                Math.min(1.0, Math.max(0.0, progress)),
                badges,
                null
        );
    }

    private int computeLevel(int totalXp) {
        int level = 0;
        for (int i = 0; i < LEVEL_THRESHOLDS.length; i++) {
            if (totalXp >= LEVEL_THRESHOLDS[i]) level = i;
        }
        return level;
    }

    private GoalResponse toResponse(PatientGoal goal) {
        List<LocalDate> checkInDates = checkInRepository.findByGoalIdOrderByCheckInDateAsc(goal.getId())
                .stream().map(GoalCheckIn::getCheckInDate).collect(Collectors.toList());

        return new GoalResponse(
                goal.getId(),
                goal.getCategory(),
                goal.getTitle(),
                goal.getDifficulty(),
                goal.getTargetValue(),
                goal.getTargetUnit(),
                goal.getInitialValue(),
                goal.getCurrentValue(),
                goal.getStartDate(),
                goal.getValidatedAt(),
                goal.getValidationNote(),
                goal.getStatus(),
                goal.getCreatedAt(),
                goal.getDifficulty().getXpReward(),
                checkInDates
        );
    }
}
