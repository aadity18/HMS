package com.hms.reservation.config;

import org.springframework.amqp.core.*;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitMQConfig {

    @Value("${rabbitmq.queue:notificationQueue}")
    private String queue;

    @Value("${rabbitmq.exchange:notificationExchange}")
    private String exchange;

    @Value("${rabbitmq.routingkey:notificationRoutingKey}")
    private String routingKey;

    @Bean
    public Queue notificationQueue() {
        return new Queue(queue, true);
    }

    @Bean
    public TopicExchange notificationExchange() {
        return new TopicExchange(exchange);
    }

    @Bean
    public Binding binding(Queue notificationQueue, TopicExchange notificationExchange) {
        return BindingBuilder.bind(notificationQueue)
                             .to(notificationExchange)
                             .with(routingKey);
    }

    @Bean
    public MessageConverter jsonMessageConverter() {
        return new Jackson2JsonMessageConverter();
    }
}