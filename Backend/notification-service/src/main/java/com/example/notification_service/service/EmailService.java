package com.example.notification_service.service;

import com.example.notification_service.dto.NotificationMessage;
import com.example.notification_service.model.Notification;
import com.example.notification_service.repository.NotificationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class EmailService {

    @Autowired
    private JavaMailSender javaMailSender;

    @Autowired
    private NotificationRepository notificationRepository;

    public void sendEmail(NotificationMessage message) {
        SimpleMailMessage mail = new SimpleMailMessage();
        mail.setTo(message.getTo());
        mail.setSubject(message.getSubject());
        mail.setText(message.getBody());
        try {
            javaMailSender.send(mail);
            System.out.println("Email sent successfully!");
        } catch (Exception e) {
            System.out.println("Failed to send email: " + e.getMessage());
            e.printStackTrace();
        }

        Notification notification = new Notification();
        notification.setRecipient(message.getTo());
        notification.setSubject(message.getSubject());
        notification.setBody(message.getBody());
        notification.setSentAt(LocalDateTime.now());

        notificationRepository.save(notification);
    }
}