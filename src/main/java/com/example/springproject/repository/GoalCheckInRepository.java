package com.example.springproject.repository;

import com.example.springproject.model.GoalCheckIn;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface GoalCheckInRepository extends JpaRepository<GoalCheckIn, Long> {
    List<GoalCheckIn> findByGoalIdOrderByCheckInDateAsc(Long goalId);
    Optional<GoalCheckIn> findByGoalIdAndCheckInDate(Long goalId, LocalDate date);
    boolean existsByGoalIdAndCheckInDate(Long goalId, LocalDate date);
}
