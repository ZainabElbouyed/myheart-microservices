package com.myheart.common.constants;

public class RabbitMQConstants {
    
    // Exchanges
    public static final String NOTIFICATION_EXCHANGE = "notification.exchange";
    public static final String BILLING_EXCHANGE = "billing.exchange";
    public static final String APPOINTMENT_EXCHANGE = "appointment.exchange";
    
    // Queues
    public static final String EMAIL_QUEUE = "email.queue";
    public static final String SMS_QUEUE = "sms.queue";
    public static final String PUSH_QUEUE = "push.queue";  // ← déjà présent
    public static final String INVOICE_QUEUE = "invoice.queue";
    public static final String PAYMENT_QUEUE = "payment.queue";
    
    // Routing Keys
    public static final String NOTIFICATION_EMAIL_ROUTING_KEY = "notification.email";
    public static final String NOTIFICATION_SMS_ROUTING_KEY = "notification.sms";
    // 🔴 AJOUTER CETTE LIGNE
    public static final String NOTIFICATION_PUSH_ROUTING_KEY = "notification.push";
    
    public static final String INVOICE_CREATED_ROUTING_KEY = "invoice.created";
    public static final String PAYMENT_PROCESSED_ROUTING_KEY = "payment.processed";
    public static final String APPOINTMENT_CREATED_ROUTING_KEY = "appointment.created";
}