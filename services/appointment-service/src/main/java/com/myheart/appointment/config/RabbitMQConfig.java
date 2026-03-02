// services/appointment-service/src/main/java/com/myheart/appointment/config/RabbitMQConfig.java
package com.myheart.appointment.config;

import com.myheart.common.constants.RabbitMQConstants;
import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitMQConfig {
    
    @Bean
    public TopicExchange appointmentExchange() {
        return new TopicExchange("appointment.exchange");
    }
    
    @Bean
    public TopicExchange notificationExchange() {
        return new TopicExchange(RabbitMQConstants.NOTIFICATION_EXCHANGE);
    }
    
    @Bean
    public TopicExchange billingExchange() {
        return new TopicExchange(RabbitMQConstants.BILLING_EXCHANGE);
    }
    
    @Bean
    public MessageConverter messageConverter() {
        return new Jackson2JsonMessageConverter();
    }
    
    @Bean
    public AmqpTemplate rabbitTemplate(ConnectionFactory connectionFactory) {
        RabbitTemplate rabbitTemplate = new RabbitTemplate(connectionFactory);
        rabbitTemplate.setMessageConverter(messageConverter());
        return rabbitTemplate;
    }
}