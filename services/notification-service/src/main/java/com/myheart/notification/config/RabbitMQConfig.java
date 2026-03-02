// services/notification-service/src/main/java/com/myheart/notification/config/RabbitMQConfig.java
package com.myheart.notification.config;

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
    public TopicExchange notificationExchange() {
        return new TopicExchange(RabbitMQConstants.NOTIFICATION_EXCHANGE);
    }
    
    @Bean
    public TopicExchange billingExchange() {
        return new TopicExchange(RabbitMQConstants.BILLING_EXCHANGE);
    }
    
    @Bean
    public Queue emailQueue() {
        return QueueBuilder.durable(RabbitMQConstants.EMAIL_QUEUE).build();
    }
    
    @Bean
    public Queue smsQueue() {
        return QueueBuilder.durable(RabbitMQConstants.SMS_QUEUE).build();
    }
    
    @Bean
    public Queue pushQueue() {
        return QueueBuilder.durable(RabbitMQConstants.PUSH_QUEUE).build();
    }
    
    @Bean
    public Binding emailBinding() {
        return BindingBuilder
                .bind(emailQueue())
                .to(notificationExchange())
                .with(RabbitMQConstants.NOTIFICATION_EMAIL_ROUTING_KEY);
    }
    
    @Bean
    public Binding smsBinding() {
        return BindingBuilder
                .bind(smsQueue())
                .to(notificationExchange())
                .with(RabbitMQConstants.NOTIFICATION_SMS_ROUTING_KEY);
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