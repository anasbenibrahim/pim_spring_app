package com.example.springproject.controller;

import com.example.springproject.dto.*;
import com.example.springproject.service.UserService;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/auth")
@CrossOrigin(origins = "*")
public class AuthController {
    
    @Autowired
    private UserService userService;
    
    @Autowired
    private ObjectMapper objectMapper;
    
    @PostMapping("/login")
    public ResponseEntity<?> login(@Valid @RequestBody LoginRequest request) {
        try {
            AuthResponse response = userService.login(request);
            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(new ErrorResponse(e.getMessage()));
        }
    }
    
    @PostMapping("/register/patient")
    public ResponseEntity<?> registerPatient(
            @RequestParam("data") String dataJson,
            @RequestPart(value = "image", required = false) MultipartFile imageFile) {
        try {
            RegisterPatientRequest request = objectMapper.readValue(dataJson, RegisterPatientRequest.class);
            // Validate the request
            if (request.getEmail() == null || request.getPassword() == null) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body(new ErrorResponse("Email and password are required"));
            }
            // Store registration data temporarily and send OTP
            userService.initiateRegistration(request.getEmail(), dataJson, "PATIENT");
            return ResponseEntity.ok(new MessageResponse("OTP code has been sent to your email. Please verify to complete registration."));
        } catch (DataIntegrityViolationException e) {
            String message = e.getMessage();
            if (message != null && (message.contains("email") || message.contains("uk_6dotkott2kjsp8vw4d0m25fb7"))) {
                return ResponseEntity.status(HttpStatus.CONFLICT)
                        .body(new ErrorResponse("Email already exists. Please use a different email or try logging in."));
            }
            return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body(new ErrorResponse("This information already exists in the system."));
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new ErrorResponse(e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new ErrorResponse("Registration failed: " + e.getMessage()));
        }
    }
    
    @PostMapping("/register/volontaire")
    public ResponseEntity<?> registerVolontaire(
            @RequestParam("data") String dataJson,
            @RequestPart(value = "image", required = false) MultipartFile imageFile) {
        try {
            RegisterVolontaireRequest request = objectMapper.readValue(dataJson, RegisterVolontaireRequest.class);
            if (request.getEmail() == null || request.getPassword() == null) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body(new ErrorResponse("Email and password are required"));
            }
            // Store registration data temporarily and send OTP
            userService.initiateRegistration(request.getEmail(), dataJson, "VOLONTAIRE");
            return ResponseEntity.ok(new MessageResponse("OTP code has been sent to your email. Please verify to complete registration."));
        } catch (DataIntegrityViolationException e) {
            String message = e.getMessage();
            if (message != null && (message.contains("email") || message.contains("uk_6dotkott2kjsp8vw4d0m25fb7"))) {
                return ResponseEntity.status(HttpStatus.CONFLICT)
                        .body(new ErrorResponse("Email already exists. Please use a different email or try logging in."));
            }
            return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body(new ErrorResponse("This information already exists in the system."));
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new ErrorResponse(e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new ErrorResponse("Registration failed: " + e.getMessage()));
        }
    }
    
    @PostMapping("/register/family")
    public ResponseEntity<?> registerFamilyMember(
            @RequestParam("data") String dataJson,
            @RequestPart(value = "image", required = false) MultipartFile imageFile) {
        try {
            RegisterFamilyMemberRequest request = objectMapper.readValue(dataJson, RegisterFamilyMemberRequest.class);
            if (request.getEmail() == null || request.getPassword() == null) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body(new ErrorResponse("Email and password are required"));
            }
            // Store registration data temporarily and send OTP
            userService.initiateRegistration(request.getEmail(), dataJson, "FAMILY_MEMBER");
            return ResponseEntity.ok(new MessageResponse("OTP code has been sent to your email. Please verify to complete registration."));
        } catch (DataIntegrityViolationException e) {
            String message = e.getMessage();
            if (message != null && (message.contains("email") || message.contains("uk_6dotkott2kjsp8vw4d0m25fb7"))) {
                return ResponseEntity.status(HttpStatus.CONFLICT)
                        .body(new ErrorResponse("Email already exists. Please use a different email or try logging in."));
            }
            return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body(new ErrorResponse("This information already exists in the system."));
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new ErrorResponse(e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new ErrorResponse("Registration failed: " + e.getMessage()));
        }
    }
    
    @PostMapping("/refresh")
    public ResponseEntity<?> refreshToken(@RequestBody RefreshTokenRequest request) {
        try {
            AuthResponse response = userService.refreshToken(request.getRefreshToken());
            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(new ErrorResponse(e.getMessage()));
        }
    }
    
    @GetMapping("/me")
    public ResponseEntity<?> getCurrentUser(@RequestHeader("Authorization") String token) {
        try {
            // Extract token from "Bearer <token>"
            String jwtToken = token.substring(7);
            Long userId = userService.getUserIdFromToken(jwtToken);
            UserResponse response = userService.getUserById(userId);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(new ErrorResponse("Invalid token"));
        }
    }
    
    @PostMapping("/forgot-password")
    public ResponseEntity<?> forgotPassword(@Valid @RequestBody ForgotPasswordRequest request) {
        try {
            userService.forgotPassword(request);
            return ResponseEntity.ok(new MessageResponse("OTP code has been sent to your email"));
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new ErrorResponse(e.getMessage()));
        }
    }
    
    @PostMapping("/verify-otp")
    public ResponseEntity<?> verifyOtp(@Valid @RequestBody VerifyOtpRequest request) {
        try {
            userService.verifyOtp(request);
            return ResponseEntity.ok(new MessageResponse("OTP verified successfully"));
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new ErrorResponse(e.getMessage()));
        }
    }
    
    @PostMapping("/verify-registration-otp")
    public ResponseEntity<?> verifyRegistrationOtp(
            @RequestParam("email") String email,
            @RequestParam("otpCode") String otpCode,
            @RequestParam("userRole") String userRole,
            @RequestPart(value = "image", required = false) MultipartFile imageFile) {
        try {
            AuthResponse response = userService.verifyRegistrationOtp(email, otpCode, userRole, imageFile);
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new ErrorResponse(e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new ErrorResponse("Registration verification failed: " + e.getMessage()));
        }
    }
    
    @PostMapping("/reset-password")
    public ResponseEntity<?> resetPassword(@Valid @RequestBody ResetPasswordRequest request) {
        try {
            userService.resetPassword(request);
            return ResponseEntity.ok(new MessageResponse("Password reset successfully"));
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new ErrorResponse(e.getMessage()));
        }
    }
    
    @PutMapping("/profile/patient")
    public ResponseEntity<?> updatePatientProfile(
            @RequestHeader("Authorization") String token,
            @RequestParam("data") String dataJson,
            @RequestPart(value = "image", required = false) MultipartFile imageFile) {
        try {
            String jwtToken = token.substring(7);
            Long userId = userService.getUserIdFromToken(jwtToken);
            UpdatePatientRequest request = objectMapper.readValue(dataJson, UpdatePatientRequest.class);
            UserResponse response = userService.updatePatient(userId, request, imageFile);
            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new ErrorResponse(e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new ErrorResponse("Failed to update profile: " + e.getMessage()));
        }
    }
    
    @PutMapping("/profile/volontaire")
    public ResponseEntity<?> updateVolontaireProfile(
            @RequestHeader("Authorization") String token,
            @RequestParam("data") String dataJson,
            @RequestPart(value = "image", required = false) MultipartFile imageFile) {
        try {
            String jwtToken = token.substring(7);
            Long userId = userService.getUserIdFromToken(jwtToken);
            UpdateVolontaireRequest request = objectMapper.readValue(dataJson, UpdateVolontaireRequest.class);
            UserResponse response = userService.updateVolontaire(userId, request, imageFile);
            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new ErrorResponse(e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new ErrorResponse("Failed to update profile: " + e.getMessage()));
        }
    }
    
    @PutMapping("/profile/family")
    public ResponseEntity<?> updateFamilyMemberProfile(
            @RequestHeader("Authorization") String token,
            @RequestParam("data") String dataJson,
            @RequestPart(value = "image", required = false) MultipartFile imageFile) {
        try {
            String jwtToken = token.substring(7);
            Long userId = userService.getUserIdFromToken(jwtToken);
            UpdateFamilyMemberRequest request = objectMapper.readValue(dataJson, UpdateFamilyMemberRequest.class);
            UserResponse response = userService.updateFamilyMember(userId, request, imageFile);
            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new ErrorResponse(e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new ErrorResponse("Failed to update profile: " + e.getMessage()));
        }
    }
    
    // Inner class for error responses
    public static class ErrorResponse {
        private String message;
        
        public ErrorResponse(String message) {
            this.message = message;
        }
        
        public String getMessage() {
            return message;
        }
        
        public void setMessage(String message) {
            this.message = message;
        }
    }
    
    // Inner class for success messages
    public static class MessageResponse {
        private String message;
        
        public MessageResponse(String message) {
            this.message = message;
        }
        
        public String getMessage() {
            return message;
        }
        
        public void setMessage(String message) {
            this.message = message;
        }
    }
}
