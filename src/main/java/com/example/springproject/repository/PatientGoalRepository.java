package com.example.springproject.repository;

import com.example.springproject.model.GoalStatus;
import com.example.springproject.model.PatientGoal;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PatientGoalRepository extends JpaRepository<PatientGoal, Long> {
    List<PatientGoal> findByPatientIdOrderByCreatedAtDesc(Long patientId);
    List<PatientGoal> findByPatientIdAndStatus(Long patientId, GoalStatus status);
}
