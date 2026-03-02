package com.example.springproject.service;

import com.example.springproject.dto.*;
import com.example.springproject.dto.UpdatePatientRequest;
import com.example.springproject.dto.UpdateVolontaireRequest;
import com.example.springproject.dto.UpdateFamilyMemberRequest;
import com.example.springproject.model.*;
import com.example.springproject.repository.*;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Random;
import java.util.UUID;

@Service
public class UserService {
    
    @Autowired
    private UserRepository userRepository;
    
    @Autowired
    private PatientRepository patientRepository;
    
    @Autowired
    private VolontaireRepository volontaireRepository;
    
    @Autowired
    private FamilyMemberRepository familyMemberRepository;
    
    @Autowired
    private PasswordEncoder passwordEncoder;
    
    @Autowired
    private JwtService jwtService;
    
    @Autowired
    private FileStorageService fileStorageService;
    
    @Autowired
    private OtpRepository otpRepository;
    
    @Autowired
    private EmailService emailService;
    
    @Autowired
    private TemporaryRegistrationRepository temporaryRegistrationRepository;
    
    @Autowired
    private ObjectMapper objectMapper;

    @jakarta.annotation.PostConstruct
    public void init() {
        System.out.println("==================================================");
        System.out.println("HARDCODED OTP MODE ACTIVE: OTP WILL ALWAYS BE 111111");
        System.out.println("PLEASE RESTART BACKEND IF YOU DO NOT SEE THIS MESSAGE");
        System.out.println("==================================================");
    }
    
    @Transactional
    public AuthResponse registerPatient(RegisterPatientRequest request, MultipartFile imageFile) {
        // Check if email already exists
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new RuntimeException("Email already exists");
        }
        
        // Generate referral key
        String referralKey = generateReferralKey();
        
        // Create patient directly (Patient extends User)
        Patient patient = new Patient();
        patient.setEmail(request.getEmail());
        patient.setPassword(passwordEncoder.encode(request.getPassword()));
        patient.setNom(request.getNom());
        patient.setPrenom(request.getPrenom());
        patient.setAge(request.getAge());
        patient.setRole(UserRole.PATIENT);
        patient.setDateNaissance(request.getDateNaissance());
        // These can be null initially (completed in Onboarding)
        patient.setSobrietyDate(request.getSobrietyDate());
        patient.setAddiction(request.getAddiction());
        patient.setReferralKey(referralKey);
        
        // Handle image upload
        if (imageFile != null && !imageFile.isEmpty()) {
            try {
                String imageUrl = fileStorageService.storeFile(imageFile, null);
                patient.setProfileImageUrl(imageUrl);
            } catch (Exception e) {
                throw new RuntimeException("Failed to upload image: " + e.getMessage());
            }
        }
        
        // Save patient (this will also create the User record due to JOINED inheritance)
        patient = patientRepository.save(patient);
        
        // Generate tokens
        String accessToken = jwtService.generateAccessToken(patient.getEmail(), patient.getId(), patient.getRole().name());
        String refreshToken = jwtService.generateRefreshToken(patient.getEmail(), patient.getId());
        
        AuthResponse response = new AuthResponse();
        response.setAccessToken(accessToken);
        response.setRefreshToken(refreshToken);
        response.setEmail(patient.getEmail());
        response.setRole(patient.getRole());
        response.setUserId(patient.getId());
        response.setMessage("Patient registered successfully");
        
        return response;
    }
    
    @Transactional
    public AuthResponse registerVolontaire(RegisterVolontaireRequest request, MultipartFile imageFile) {
        // Check if email already exists
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new RuntimeException("Email already exists");
        }
        
        // Create volontaire directly (Volontaire extends User)
        Volontaire volontaire = new Volontaire();
        volontaire.setEmail(request.getEmail());
        volontaire.setPassword(passwordEncoder.encode(request.getPassword()));
        volontaire.setNom(request.getNom());
        volontaire.setPrenom(request.getPrenom());
        volontaire.setAge(request.getAge());
        volontaire.setRole(UserRole.VOLONTAIRE);
        
        // Handle image upload
        if (imageFile != null && !imageFile.isEmpty()) {
            try {
                String imageUrl = fileStorageService.storeFile(imageFile, null);
                volontaire.setProfileImageUrl(imageUrl);
            } catch (Exception e) {
                throw new RuntimeException("Failed to upload image: " + e.getMessage());
            }
        }
        
        // Save volontaire (this will also create the User record due to JOINED inheritance)
        volontaire = volontaireRepository.save(volontaire);
        
        // Generate tokens
        String accessToken = jwtService.generateAccessToken(volontaire.getEmail(), volontaire.getId(), volontaire.getRole().name());
        String refreshToken = jwtService.generateRefreshToken(volontaire.getEmail(), volontaire.getId());
        
        AuthResponse response = new AuthResponse();
        response.setAccessToken(accessToken);
        response.setRefreshToken(refreshToken);
        response.setEmail(volontaire.getEmail());
        response.setRole(volontaire.getRole());
        response.setUserId(volontaire.getId());
        response.setMessage("Volontaire registered successfully");
        
        return response;
    }
    
    @Transactional
    public AuthResponse registerFamilyMember(RegisterFamilyMemberRequest request, MultipartFile imageFile) {
        // Check if email already exists
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new RuntimeException("Email already exists");
        }
        
        // Find patient by referral key
        Patient patient = patientRepository.findByReferralKey(request.getReferralKey())
                .orElseThrow(() -> new RuntimeException("Invalid referral key"));
        
        // Create family member directly (FamilyMember extends User)
        FamilyMember familyMember = new FamilyMember();
        familyMember.setEmail(request.getEmail());
        familyMember.setPassword(passwordEncoder.encode(request.getPassword()));
        familyMember.setNom(request.getNom());
        familyMember.setPrenom(request.getPrenom());
        // Set default age to 0 if not provided (database requires NOT NULL)
        // TODO: Update database schema to make age nullable for family members
        familyMember.setAge(0);
        familyMember.setRole(UserRole.FAMILY_MEMBER);
        familyMember.setReferralKey(request.getReferralKey());
        familyMember.setPatient(patient);
        
        // Handle image upload
        if (imageFile != null && !imageFile.isEmpty()) {
            try {
                String imageUrl = fileStorageService.storeFile(imageFile, null);
                familyMember.setProfileImageUrl(imageUrl);
            } catch (Exception e) {
                throw new RuntimeException("Failed to upload image: " + e.getMessage());
            }
        }
        
        // Save family member (this will also create the User record due to JOINED inheritance)
        familyMember = familyMemberRepository.save(familyMember);
        
        // Generate tokens
        String accessToken = jwtService.generateAccessToken(familyMember.getEmail(), familyMember.getId(), familyMember.getRole().name());
        String refreshToken = jwtService.generateRefreshToken(familyMember.getEmail(), familyMember.getId());
        
        AuthResponse response = new AuthResponse();
        response.setAccessToken(accessToken);
        response.setRefreshToken(refreshToken);
        response.setEmail(familyMember.getEmail());
        response.setRole(familyMember.getRole());
        response.setUserId(familyMember.getId());
        response.setMessage("Family member registered successfully");
        
        return response;
    }
    
    public AuthResponse login(LoginRequest request) {
        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new RuntimeException("Invalid email or password"));
        
        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new RuntimeException("Invalid email or password");
        }
        
        // Generate tokens
        String accessToken = jwtService.generateAccessToken(user.getEmail(), user.getId(), user.getRole().name());
        String refreshToken = jwtService.generateRefreshToken(user.getEmail(), user.getId());
        
        AuthResponse response = new AuthResponse();
        response.setAccessToken(accessToken);
        response.setRefreshToken(refreshToken);
        response.setEmail(user.getEmail());
        response.setRole(user.getRole());
        response.setUserId(user.getId());
        response.setMessage("Login successful");
        
        return response;
    }
    
    public UserResponse getUserById(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));
        
        UserResponse response = new UserResponse();
        response.setId(user.getId());
        response.setEmail(user.getEmail());
        response.setNom(user.getNom());
        response.setPrenom(user.getPrenom());
        response.setAge(user.getAge());
        response.setRole(user.getRole());
        response.setProfileImageUrl(user.getProfileImageUrl());
        response.setCreatedAt(user.getCreatedAt());
        
        // If user is a patient, include referral code
        if (user.getRole() == UserRole.PATIENT) {
            Patient patient = patientRepository.findById(userId)
                    .orElse(null);
            if (patient != null) {
                response.setReferralCode(patient.getReferralKey());
                response.setHasCompletedOnboarding(patient.isHasCompletedOnboarding());
                response.setAddiction(patient.getAddiction() != null ? patient.getAddiction().name() : null);
                response.setSobrietyDate(patient.getSobrietyDate());
            }
        }
        
        // If user is a family member, include patient information
        if (user.getRole() == UserRole.FAMILY_MEMBER) {
            FamilyMember familyMember = familyMemberRepository.findByIdWithPatient(userId)
                    .orElse(null);
            if (familyMember != null) {
                // Patient is eagerly fetched via JOIN FETCH
                Patient patient = familyMember.getPatient();
                if (patient != null) {
                    response.setPatientId(patient.getId());
                    response.setPatientNom(patient.getNom());
                    response.setPatientPrenom(patient.getPrenom());
                }
            }
        }
        
        return response;
    }
    
    @Transactional
    public UserResponse updatePatient(Long userId, UpdatePatientRequest request, MultipartFile imageFile) {
        Patient patient = patientRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("Patient not found"));
        
        // Update basic user fields
        patient.setNom(request.getNom());
        patient.setPrenom(request.getPrenom());
        patient.setAge(request.getAge());
        
        // Date de naissance, sobriety date, and addiction are not updatable
        // They remain unchanged from registration
        
        // Handle image upload
        if (imageFile != null && !imageFile.isEmpty()) {
            try {
                // Delete old image if exists
                if (patient.getProfileImageUrl() != null && !patient.getProfileImageUrl().isEmpty()) {
                    fileStorageService.deleteFile(patient.getProfileImageUrl());
                }
                // Store new image
                String imageUrl = fileStorageService.storeFile(imageFile, userId);
                patient.setProfileImageUrl(imageUrl);
            } catch (Exception e) {
                throw new RuntimeException("Failed to upload image: " + e.getMessage());
            }
        }
        
        patient = patientRepository.save(patient);
        
        // Return updated user response
        return getUserById(userId);
    }
    
    @Transactional
    public UserResponse updateVolontaire(Long userId, UpdateVolontaireRequest request, MultipartFile imageFile) {
        Volontaire volontaire = volontaireRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("Volontaire not found"));
        
        // Update basic user fields
        volontaire.setNom(request.getNom());
        volontaire.setPrenom(request.getPrenom());
        volontaire.setAge(request.getAge());
        
        // Handle image upload
        if (imageFile != null && !imageFile.isEmpty()) {
            try {
                // Delete old image if exists
                if (volontaire.getProfileImageUrl() != null && !volontaire.getProfileImageUrl().isEmpty()) {
                    fileStorageService.deleteFile(volontaire.getProfileImageUrl());
                }
                // Store new image
                String imageUrl = fileStorageService.storeFile(imageFile, userId);
                volontaire.setProfileImageUrl(imageUrl);
            } catch (Exception e) {
                throw new RuntimeException("Failed to upload image: " + e.getMessage());
            }
        }
        
        volontaire = volontaireRepository.save(volontaire);
        
        // Return updated user response
        return getUserById(userId);
    }
    
    @Transactional
    public UserResponse updateFamilyMember(Long userId, UpdateFamilyMemberRequest request, MultipartFile imageFile) {
        FamilyMember familyMember = familyMemberRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("Family member not found"));
        
        // Update basic user fields
        familyMember.setNom(request.getNom());
        familyMember.setPrenom(request.getPrenom());
        
        // Handle image upload
        if (imageFile != null && !imageFile.isEmpty()) {
            try {
                // Delete old image if exists
                if (familyMember.getProfileImageUrl() != null && !familyMember.getProfileImageUrl().isEmpty()) {
                    fileStorageService.deleteFile(familyMember.getProfileImageUrl());
                }
                // Store new image
                String imageUrl = fileStorageService.storeFile(imageFile, userId);
                familyMember.setProfileImageUrl(imageUrl);
            } catch (Exception e) {
                throw new RuntimeException("Failed to upload image: " + e.getMessage());
            }
        }
        
        familyMember = familyMemberRepository.save(familyMember);
        
        // Return updated user response
        return getUserById(userId);
    }
    
    public AuthResponse refreshToken(String refreshToken) {
        try {
            String email = jwtService.extractEmail(refreshToken);
            Long userId = jwtService.extractUserId(refreshToken);
            
            User user = userRepository.findByEmail(email)
                    .orElseThrow(() -> new RuntimeException("User not found"));
            
            if (!jwtService.validateToken(refreshToken, email)) {
                throw new RuntimeException("Invalid refresh token");
            }
            
            // Generate new tokens
            String newAccessToken = jwtService.generateAccessToken(user.getEmail(), user.getId(), user.getRole().name());
            String newRefreshToken = jwtService.generateRefreshToken(user.getEmail(), user.getId());
            
            AuthResponse response = new AuthResponse();
            response.setAccessToken(newAccessToken);
            response.setRefreshToken(newRefreshToken);
            response.setEmail(user.getEmail());
            response.setRole(user.getRole());
            response.setUserId(user.getId());
            response.setMessage("Token refreshed successfully");
            
            return response;
        } catch (Exception e) {
            throw new RuntimeException("Invalid refresh token");
        }
    }
    
    public Long getUserIdFromToken(String token) {
        return jwtService.extractUserId(token);
    }
    
    private String generateReferralKey() {
        String key;
        do {
            key = UUID.randomUUID().toString().replace("-", "").substring(0, 16).toUpperCase();
        } while (patientRepository.existsByReferralKey(key));
        return key;
    }
    
    @Transactional
    public void forgotPassword(ForgotPasswordRequest request) {
        // Check if user exists
        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new RuntimeException("User not found with this email"));
        
        // Mark all previous OTPs as used
        otpRepository.markAllAsUsedByEmail(request.getEmail());
        
        // Generate 6-digit OTP
        String otpCode = generateOtpCode();
        
        // Create OTP entity
        Otp otp = new Otp();
        otp.setEmail(request.getEmail());
        otp.setCode(otpCode);
        otp.setExpiresAt(LocalDateTime.now().plusMinutes(10)); // OTP expires in 10 minutes
        otp.setUsed(false);
        
        otpRepository.save(otp);
        
        // Send OTP via email
        emailService.sendOtpEmail(request.getEmail(), otpCode);
    }
    
    public boolean verifyOtp(VerifyOtpRequest request) {
        Otp otp = otpRepository.findByEmailAndCodeAndUsedFalseAndExpiresAtAfter(
                request.getEmail(), 
                request.getOtpCode(), 
                LocalDateTime.now()
        ).orElseThrow(() -> new RuntimeException("Invalid or expired OTP code"));
        
        return true;
    }
    
    @Transactional
    public void resetPassword(ResetPasswordRequest request) {
        // Verify passwords match
        if (!request.getNewPassword().equals(request.getConfirmPassword())) {
            throw new RuntimeException("Passwords do not match");
        }
        
        // Verify OTP
        Otp otp = otpRepository.findByEmailAndCodeAndUsedFalseAndExpiresAtAfter(
                request.getEmail(), 
                request.getOtpCode(), 
                LocalDateTime.now()
        ).orElseThrow(() -> new RuntimeException("Invalid or expired OTP code"));
        
        // Find user
        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new RuntimeException("User not found"));
        
        // Update password
        user.setPassword(passwordEncoder.encode(request.getNewPassword()));
        userRepository.save(user);
        
        // Mark OTP as used
        otp.setUsed(true);
        otpRepository.save(otp);
        
        // Mark all other OTPs for this email as used
        otpRepository.markAllAsUsedByEmail(request.getEmail());
    }
    
    private String generateOtpCode() {
        // Random random = new Random();
        // return String.format("%06d", random.nextInt(1000000));
        return "111111"; // HARDCODED FOR DEV
    }
    
    // Temporary registration methods for OTP verification
    @Transactional
    public void initiateRegistration(String email, String registrationDataJson, String userRole) {
        System.out.println("Starting registration for: " + email + ", role: " + userRole);
        // Check if email already exists
        if (userRepository.existsByEmail(email)) {
            System.out.println("Error: Email already exists: " + email);
            throw new RuntimeException("Email already exists");
        }
        
        // Delete any existing temporary registration for this email and role
        temporaryRegistrationRepository.deleteByEmailAndUserRole(email, userRole);
        
        // Store registration data temporarily
        TemporaryRegistration tempReg = new TemporaryRegistration();
        tempReg.setEmail(email);
        tempReg.setRegistrationData(registrationDataJson);
        tempReg.setUserRole(userRole);
        tempReg.setExpiresAt(LocalDateTime.now().plusMinutes(10)); // Expires in 10 minutes
        temporaryRegistrationRepository.save(tempReg);
        System.out.println("Temp registration saved.");
        
        // Mark all previous OTPs as used
        otpRepository.markAllAsUsedByEmail(email);
        
        // Generate 6-digit OTP
        String otpCode = generateOtpCode();
        System.out.println("Generated OTP: " + otpCode);
        
        // Create OTP entity
        Otp otp = new Otp();
        otp.setEmail(email);
        otp.setCode(otpCode);
        otp.setExpiresAt(LocalDateTime.now().plusMinutes(10)); // OTP expires in 10 minutes
        otp.setUsed(false);
        otpRepository.save(otp);
        System.out.println("OTP saved to DB: " + otp.getId());
        
        // Send OTP via email
        emailService.sendOtpEmail(email, otpCode);
    }
    
    @Transactional
    public AuthResponse verifyRegistrationOtp(String email, String otpCode, String userRole, MultipartFile imageFile) {
        System.out.println("Verifying OTP for: " + email + ", Code: " + otpCode + ", Role: " + userRole + ", Time: " + LocalDateTime.now());
        // Verify OTP
        Otp otp = otpRepository.findByEmailAndCodeAndUsedFalseAndExpiresAtAfter(
                email, 
                otpCode, 
                LocalDateTime.now()
        ).orElseThrow(() -> {
            System.out.println("OTP NOT FOUND or EXPIRED.");
            return new RuntimeException("Invalid or expired OTP code");
        });
        System.out.println("OTP Found: " + otp.getId());
        
        
        // Get temporary registration data
        TemporaryRegistration tempReg = temporaryRegistrationRepository
                .findByEmailAndUserRoleAndExpiresAtAfter(email, userRole, LocalDateTime.now())
                .orElseThrow(() -> new RuntimeException("Registration data expired or not found"));
        
        // Mark OTP as used
        otp.setUsed(true);
        otpRepository.save(otp);
        
        // Mark all other OTPs for this email as used
        otpRepository.markAllAsUsedByEmail(email);
        
        // Parse registration data and complete registration
        AuthResponse response;
        try {
            if ("PATIENT".equals(userRole)) {
                RegisterPatientRequest request = objectMapper.readValue(
                        tempReg.getRegistrationData(), 
                        RegisterPatientRequest.class
                );
                response = registerPatient(request, imageFile);
            } else if ("VOLONTAIRE".equals(userRole)) {
                RegisterVolontaireRequest request = objectMapper.readValue(
                        tempReg.getRegistrationData(), 
                        RegisterVolontaireRequest.class
                );
                response = registerVolontaire(request, imageFile);
            } else if ("FAMILY_MEMBER".equals(userRole)) {
                RegisterFamilyMemberRequest request = objectMapper.readValue(
                        tempReg.getRegistrationData(), 
                        RegisterFamilyMemberRequest.class
                );
                response = registerFamilyMember(request, imageFile);
            } else {
                throw new RuntimeException("Invalid user role");
            }
            
            // Delete temporary registration after successful registration
            temporaryRegistrationRepository.delete(tempReg);
            
            return response;
        } catch (Exception e) {
            throw new RuntimeException("Failed to complete registration: " + e.getMessage());
        }
    }
}
