// services/billing-service/src/main/java/com/myheart/billing/config/RabbitMQConfig.java
package com.myheart.billing.config;

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
    public TopicExchange billingExchange() {
        return new TopicExchange(RabbitMQConstants.BILLING_EXCHANGE);
    }
    
    @Bean
    public Queue invoiceQueue() {
        return QueueBuilder.durable(RabbitMQConstants.INVOICE_QUEUE).build();
    }
    
    @Bean
    public Queue paymentQueue() {
        return QueueBuilder.durable(RabbitMQConstants.PAYMENT_QUEUE).build();
    }
    
    @Bean
    public Binding invoiceBinding() {
        return BindingBuilder
                .bind(invoiceQueue())
                .to(billingExchange())
                .with(RabbitMQConstants.INVOICE_CREATED_ROUTING_KEY);
    }
    
    @Bean
    public Binding paymentBinding() {
        return BindingBuilder
                .bind(paymentQueue())
                .to(billingExchange())
                .with(RabbitMQConstants.PAYMENT_PROCESSED_ROUTING_KEY);
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