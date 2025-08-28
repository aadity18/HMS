package com.hms.reservation.notification;

import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Component;

import com.hms.reservation.dto.NotificationMessage;

@Component
public class EmailNotificationListener {

    private final JavaMailSender mailSender;

    public EmailNotificationListener(JavaMailSender mailSender) {
        this.mailSender = mailSender;
    }

    @RabbitListener(queues = "${rabbitmq.queue:notificationQueue}")
    public void handleEmail(NotificationMessage msg) {
        SimpleMailMessage mail = new SimpleMailMessage();
        mail.setTo(msg.getTo());
        mail.setSubject(msg.getSubject());
        mail.setText(msg.getBody());
        mailSender.send(mail);
    }
}