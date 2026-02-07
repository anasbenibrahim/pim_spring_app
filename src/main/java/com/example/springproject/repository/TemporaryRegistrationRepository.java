package com.example.springproject.repository;

import com.example.springproject.model.TemporaryRegistration;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.Optional;

@Repository
public interface TemporaryRegistrationRepository extends JpaRepository<TemporaryRegistration, Long> {
    
    Optional<TemporaryRegistration> findByEmailAndUserRoleAndExpiresAtAfter(
            String email, 
            String userRole, 
            LocalDateTime now
    );
    
    @Modifying
    @Query("DELETE FROM TemporaryRegistration t WHERE t.expiresAt < :now")
    void deleteExpiredRegistrations(@Param("now") LocalDateTime now);
    
    @Modifying
    @Query("DELETE FROM TemporaryRegistration t WHERE t.email = :email AND t.userRole = :userRole")
    void deleteByEmailAndUserRole(@Param("email") String email, @Param("userRole") String userRole);
}
