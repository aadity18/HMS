package com.hms.reservation.service;

import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.hms.reservation.dto.NotificationMessage;

@Service
public class NotificationPublisher {

    @Autowired
    private RabbitTemplate rabbitTemplate;

    @Value("${rabbitmq.exchange}")
    private String exchange;

    @Value("${rabbitmq.routingkey}")
    private String routingKey;

    public void sendNotification(String email, String subject, String body) {
        NotificationMessage message = new NotificationMessage();
        message.setTo(email);
        message.setSubject(subject);
        message.setBody(body);

        rabbitTemplate.convertAndSend(exchange, routingKey, message);
    }
}
