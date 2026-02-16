package com.example.springproject.service;

import com.example.springproject.dto.CreateObjectifRequest;
import com.example.springproject.dto.ObjectifResponse;
import com.example.springproject.model.MoodType;
import com.example.springproject.model.Patient;
import com.example.springproject.model.PatientObjectif;
import com.example.springproject.repository.PatientObjectifRepository;
import com.example.springproject.repository.PatientRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class PatientObjectifService {

    @Autowired
    private PatientObjectifRepository objectifRepository;

    @Autowired
    private PatientRepository patientRepository;

    @Transactional
    public ObjectifResponse createObjectif(Long patientId, CreateObjectifRequest request) {
        Patient patient = patientRepository.findById(patientId)
                .orElseThrow(() -> new RuntimeException("Patient not found"));

        LocalDate objectifDate = request.getObjectifDate();
        LocalDate today = LocalDate.now();

        if (objectifDate.isAfter(today)) {
            throw new RuntimeException("Cannot add a track for a future date. Maximum allowed date is today.");
        }
        if (objectifRepository.existsByPatientIdAndObjectifDate(patientId, objectifDate)) {
            throw new RuntimeException("You already have a track for this day. Only one track per day is allowed.");
        }

        PatientObjectif objectif = new PatientObjectif();
        objectif.setPatient(patient);
        objectif.setObjectifDate(objectifDate);
        objectif.setMood(request.getMood());
        objectif.setConsumed(request.getConsumed());
        objectif.setNotes(request.getNotes());

        objectif = objectifRepository.save(objectif);
        return mapToResponse(objectif);
    }

    public List<ObjectifResponse> getObjectifsByPatient(Long patientId) {
        return objectifRepository.findByPatientIdOrderByObjectifDateDesc(patientId)
                .stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    public ObjectifResponse getObjectifById(Long id, Long patientId) {
        PatientObjectif objectif = objectifRepository.findByIdAndPatientId(id, patientId)
                .orElseThrow(() -> new RuntimeException("Objectif not found"));
        return mapToResponse(objectif);
    }

    @Transactional
    public void deleteObjectif(Long id, Long patientId) {
        if (!objectifRepository.existsById(id)) {
            throw new RuntimeException("Objectif not found");
        }
        objectifRepository.findByIdAndPatientId(id, patientId)
                .orElseThrow(() -> new RuntimeException("Objectif not found or access denied"));
        objectifRepository.deleteById(id);
    }

    private ObjectifResponse mapToResponse(PatientObjectif objectif) {
        return new ObjectifResponse(
                objectif.getId(),
                objectif.getObjectifDate(),
                objectif.getMood(),
                objectif.getConsumed(),
                objectif.getNotes(),
                objectif.getCreatedAt()
        );
    }
}
