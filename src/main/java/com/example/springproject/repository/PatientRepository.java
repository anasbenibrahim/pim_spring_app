package com.example.springproject.repository;

import com.example.springproject.model.Patient;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface PatientRepository extends JpaRepository<Patient, Long> {
    Optional<Patient> findByReferralKey(String referralKey);
    boolean existsByReferralKey(String referralKey);
}
