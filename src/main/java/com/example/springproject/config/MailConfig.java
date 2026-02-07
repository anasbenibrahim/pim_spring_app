package com.example.springproject.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;

import java.net.URI;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.Properties;

@Configuration
public class MailConfig {
    
    @Value("${MAILER_DSN:}")
    private String mailerDsn;
    
    @Value("${spring.mail.host:smtp.gmail.com}")
    private String host;
    
    @Value("${spring.mail.port:587}")
    private int port;
    
    @Value("${spring.mail.username:}")
    private String username;
    
    @Value("${spring.mail.password:}")
    private String password;
    
    @Bean
    public JavaMailSender javaMailSender() {
        JavaMailSenderImpl mailSender = new JavaMailSenderImpl();
        
        String finalHost = host;
        int finalPort = port;
        String finalUsername = username;
        String finalPassword = password;
        
        // Parse MAILER_DSN if provided (format: smtp://username:password@host:port?encryption=tls&auth_mode=login)
        if (mailerDsn != null && !mailerDsn.isEmpty()) {
            try {
                MailConfigConfig parsed = parseMailerDsn(mailerDsn);
                finalHost = parsed.host != null ? parsed.host : host;
                finalPort = parsed.port != -1 ? parsed.port : port;
                finalUsername = parsed.username != null ? parsed.username : username;
                finalPassword = parsed.password != null ? parsed.password : password;
            } catch (Exception e) {
                // If parsing fails, use individual properties
                System.err.println("Failed to parse MAILER_DSN, using individual properties: " + e.getMessage());
            }
        }
        
        mailSender.setHost(finalHost);
        mailSender.setPort(finalPort);
        mailSender.setUsername(finalUsername);
        mailSender.setPassword(finalPassword);
        
        Properties props = mailSender.getJavaMailProperties();
        props.put("mail.transport.protocol", "smtp");
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.starttls.enable", "true");
        props.put("mail.smtp.starttls.required", "true");
        props.put("mail.debug", "false");
        
        return mailSender;
    }
    
    private MailConfigConfig parseMailerDsn(String dsn) {
        try {
            URI uri = new URI(dsn);
            
            MailConfigConfig config = new MailConfigConfig();
            
            // Extract host
            config.host = uri.getHost();
            
            // Extract port
            config.port = uri.getPort() != -1 ? uri.getPort() : 587;
            
            // Extract username and password from userInfo
            if (uri.getUserInfo() != null) {
                String[] userInfo = uri.getUserInfo().split(":");
                if (userInfo.length >= 1) {
                    config.username = URLDecoder.decode(userInfo[0], StandardCharsets.UTF_8);
                }
                if (userInfo.length >= 2) {
                    config.password = URLDecoder.decode(userInfo[1], StandardCharsets.UTF_8);
                }
            }
            
            return config;
        } catch (Exception e) {
            throw new RuntimeException("Invalid MAILER_DSN format", e);
        }
    }
    
    private static class MailConfigConfig {
        String host;
        int port = -1;
        String username;
        String password;
    }
}
