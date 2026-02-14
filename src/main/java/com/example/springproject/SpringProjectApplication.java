package com.example.springproject;

import io.github.cdimascio.dotenv.Dotenv;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class SpringProjectApplication {

    public static void main(String[] args) {
        // Load .env file before Spring Boot starts
        try {
            // Try to load .env file from current directory or parent directory
            // Use directory("./") and directory("../") to cover common launch scenarios (IDE root vs module root)
            Dotenv dotenv = Dotenv.configure()
                    .directory("./")
                    .ignoreIfMissing() 
                    .load();
            
            if (dotenv.entries().isEmpty()) {
                 // Try parent directory if current is empty
                 dotenv = Dotenv.configure()
                    .directory("../")
                    .ignoreIfMissing()
                    .load();
            }
            
            // Set system properties from .env file so Spring Boot can read them
            if (dotenv.get("MAIL_HOST") != null) {
                System.setProperty("MAIL_HOST", dotenv.get("MAIL_HOST"));
            }
            if (dotenv.get("MAIL_PORT") != null) {
                System.setProperty("MAIL_PORT", dotenv.get("MAIL_PORT"));
            }
            if (dotenv.get("MAIL_USERNAME") != null) {
                String mailUsername = dotenv.get("MAIL_USERNAME");
                System.setProperty("MAIL_USERNAME", mailUsername);
                System.setProperty("spring.mail.username", mailUsername);
                System.out.println("Loaded MAIL_USERNAME from .env: " + mailUsername);
            } else {
                System.out.println("Warning: MAIL_USERNAME not found in .env file");
            }
            if (dotenv.get("MAIL_PASSWORD") != null) {
                System.setProperty("MAIL_PASSWORD", dotenv.get("MAIL_PASSWORD"));
                System.setProperty("spring.mail.password", dotenv.get("MAIL_PASSWORD"));
                System.out.println("Loaded MAIL_PASSWORD from .env (password hidden)");
            } else {
                System.out.println("Warning: MAIL_PASSWORD not found in .env file");
            }
            if (dotenv.get("MAILER_DSN") != null) {
                System.setProperty("MAILER_DSN", dotenv.get("MAILER_DSN"));
            }
        } catch (Exception e) {
            System.err.println("Warning: Could not load .env file: " + e.getMessage());
            System.err.println("Make sure .env file exists in the Spring directory or set environment variables manually.");
        }
        
        SpringApplication.run(SpringProjectApplication.class, args);
    }
}
