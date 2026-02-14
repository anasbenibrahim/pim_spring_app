package com.example.springproject.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class EmailService {
    
    @Autowired
    private JavaMailSender mailSender;
    
    @Value("${spring.mail.username:}")
    private String fromEmail;
    
    public void sendOtpEmail(String toEmail, String otpCode) {
        // Try to get email from spring.mail.username, fallback to MAIL_USERNAME env var
        String senderEmail = fromEmail;
        if (senderEmail == null || senderEmail.isEmpty()) {
            senderEmail = System.getProperty("MAIL_USERNAME");
            if (senderEmail == null || senderEmail.isEmpty()) {
                senderEmail = System.getenv("MAIL_USERNAME");
            }
        }
        
        // DEVELOPMENT MODE BYPASS: If email is not configured, log OTP to console
        if (senderEmail == null || senderEmail.isEmpty()) {
            System.out.println("==================================================");
            System.out.println("WARNING: Email sender not configured. DEVELOPMENT MODE.");
            System.out.println("To: " + toEmail);
            System.out.println("OTP Code: " + otpCode);
            System.out.println("==================================================");
            return; // Exit without sending email
        }
        
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom(senderEmail);
        message.setTo(toEmail);
        message.setSubject("Password Reset OTP Code");
        message.setText("Your password reset OTP code is: " + otpCode + "\n\n" +
                       "This code will expire in 10 minutes.\n\n" +
                       "If you did not request this code, please ignore this email.");
        
        try {
            mailSender.send(message);
        } catch (Exception e) {
            System.err.println("Failed to send email: " + e.getMessage());
            // Fallback for development if sending fails
            System.out.println("==================================================");
            System.out.println("FALLBACK: Failed to send email. OTP logged below:");
            System.out.println("OTP Code: " + otpCode);
            System.out.println("==================================================");
        }
    }
}
