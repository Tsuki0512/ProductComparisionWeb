package com.example.productcomparisionweb.service.impl;

import com.example.productcomparisionweb.service.EmailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class EmailServiceImpl implements EmailService {

    @Autowired
    private JavaMailSender mailSender;

    @Value("${spring.mail.properties.from}")
    private String from;

    @Override
    public boolean sendSimpleEmail(String to, String subject, String content) {
        try {
            System.out.println("\n=== Starting email sending process ===");
            System.out.println("From: " + from);
            System.out.println("To: " + to);
            System.out.println("Subject: " + subject);
            
            SimpleMailMessage message = new SimpleMailMessage();
            message.setFrom(from);
            message.setTo(to);
            message.setSubject(subject);
            message.setText(content);
            
            System.out.println("Attempting to send email...");
            mailSender.send(message);
            System.out.println("Email sent successfully");
            System.out.println("=== Email sending process completed ===\n");
            return true;
        } catch (Exception e) {
            System.out.println("\n=== Error in email sending process ===");
            System.out.println("To: " + to);
            System.out.println("Error message: " + e.getMessage());
            System.out.println("Stack trace:");
            e.printStackTrace();
            System.out.println("=== Error details end ===\n");
            return false;
        }
    }
} 