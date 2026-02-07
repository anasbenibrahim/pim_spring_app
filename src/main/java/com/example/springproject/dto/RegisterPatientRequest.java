package com.example.springproject.dto;

import com.example.springproject.model.AddictionType;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RegisterPatientRequest {
    
    @NotBlank(message = "Email is required")
    @Email(message = "Email should be valid")
    private String email;
    
    @NotBlank(message = "Password is required")
    @Size(min = 6, message = "Password must be at least 6 characters")
    private String password;
    
    @NotBlank(message = "Nom is required")
    private String nom;
    
    @NotBlank(message = "Prenom is required")
    private String prenom;
    
    @NotNull(message = "Age is required")
    @Min(value = 1, message = "Age must be at least 1")
    @Max(value = 150, message = "Age must be at most 150")
    private Integer age;
    
    @NotNull(message = "Date de naissance is required")
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    private LocalDate dateNaissance;
    
    @NotNull(message = "Sobriety date is required")
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    private LocalDate sobrietyDate;
    
    @NotNull(message = "Addiction type is required")
    private AddictionType addiction;
}
