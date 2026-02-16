package com.example.springproject.repository;

import com.example.springproject.model.PatientObjectif;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface PatientObjectifRepository extends JpaRepository<PatientObjectif, Long> {
    List<PatientObjectif> findByPatientIdOrderByObjectifDateDesc(Long patientId);
    List<PatientObjectif> findByPatientIdAndObjectifDateBetween(Long patientId, LocalDate start, LocalDate end);
    Optional<PatientObjectif> findByIdAndPatientId(Long id, Long patientId);
    boolean existsByPatientIdAndObjectifDate(Long patientId, LocalDate objectifDate);
}
