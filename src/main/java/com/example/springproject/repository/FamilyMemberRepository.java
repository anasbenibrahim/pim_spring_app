package com.example.springproject.repository;

import com.example.springproject.model.FamilyMember;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface FamilyMemberRepository extends JpaRepository<FamilyMember, Long> {
    @Query("SELECT fm FROM FamilyMember fm JOIN FETCH fm.patient WHERE fm.id = :id")
    Optional<FamilyMember> findByIdWithPatient(@Param("id") Long id);
}
