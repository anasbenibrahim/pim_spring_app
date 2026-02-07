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
        if (mailSender == null) {
            throw new RuntimeException("Email service is not configured. Please check your MAILER_DSN or mail configuration.");
        }
        
        // Try to get email from spring.mail.username, fallback to MAIL_USERNAME env var
        String senderEmail = fromEmail;
        if (senderEmail == null || senderEmail.isEmpty()) {
            senderEmail = System.getProperty("MAIL_USERNAME");
            if (senderEmail == null || senderEmail.isEmpty()) {
                senderEmail = System.getenv("MAIL_USERNAME");
            }
        }
        
        if (senderEmail == null || senderEmail.isEmpty()) {
            throw new RuntimeException("Email sender address is not configured. Please set spring.mail.username or MAIL_USERNAME in .env file.");
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
            throw new RuntimeException("Failed to send email: " + e.getMessage());
        }
    }
}
