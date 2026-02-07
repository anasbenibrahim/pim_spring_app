package com.example.springproject.repository;

import com.example.springproject.model.Volontaire;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface VolontaireRepository extends JpaRepository<Volontaire, Long> {
}
