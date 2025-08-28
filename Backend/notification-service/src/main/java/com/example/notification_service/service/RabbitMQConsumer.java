package com.example.notification_service.service;

import com.example.notification_service.dto.NotificationMessage;

import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

@Component
public class RabbitMQConsumer {

    private final EmailService emailService;

    public RabbitMQConsumer(EmailService emailService) {
        this.emailService = emailService;
    }

    @RabbitListener(queues = "${rabbitmq.queue}")
    public void consume(NotificationMessage message) {
        emailService.sendEmail(message);
    }
}