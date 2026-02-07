package com.example.springproject.repository;

import com.example.springproject.model.Otp;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.Optional;

@Repository
public interface OtpRepository extends JpaRepository<Otp, Long> {
    
    Optional<Otp> findByEmailAndCodeAndUsedFalseAndExpiresAtAfter(String email, String code, LocalDateTime now);
    
    @Modifying
    @Query("UPDATE Otp o SET o.used = true WHERE o.email = :email AND o.used = false")
    void markAllAsUsedByEmail(@Param("email") String email);
    
    @Modifying
    @Query("DELETE FROM Otp o WHERE o.expiresAt < :now")
    void deleteExpiredOtps(@Param("now") LocalDateTime now);
}
