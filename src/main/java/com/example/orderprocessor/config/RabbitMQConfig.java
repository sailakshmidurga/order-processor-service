package com.example.orderprocessor.config;

import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.config.SimpleRabbitListenerContainerFactory;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.HashMap;
import java.util.Map;

@Configuration
public class RabbitMQConfig {

    // ==============================
    // Exchange Names
    // ==============================
    public static final String ORDER_EVENTS_EXCHANGE = "order.events";
    public static final String DLX_ORDER_EVENTS_EXCHANGE = "dlx.order.events";

    // ==============================
    // Queue Names
    // ==============================
    public static final String ORDER_PLACED_QUEUE = "order.placed.queue";
    public static final String ORDER_DLQ = "order.dlq";

    // ==============================
    // Routing Keys
    // ==============================
    public static final String ORDER_PLACED_ROUTING_KEY = "order.placed";

    // =====================================================
    // 1️⃣ Main Exchange
    // =====================================================
    @Bean
    public TopicExchange orderEventsExchange() {
        return new TopicExchange(ORDER_EVENTS_EXCHANGE);
    }

    // =====================================================
    // 2️⃣ Dead Letter Exchange
    // =====================================================
    @Bean
    public TopicExchange deadLetterExchange() {
        return new TopicExchange(DLX_ORDER_EVENTS_EXCHANGE);
    }

    // =====================================================
    // 3️⃣ Main Queue (with DLX configured)
    // =====================================================
    @Bean
    public Queue orderPlacedQueue() {
        Map<String, Object> args = new HashMap<>();
        args.put("x-dead-letter-exchange", DLX_ORDER_EVENTS_EXCHANGE);
        args.put("x-dead-letter-routing-key", ORDER_PLACED_ROUTING_KEY);

        return new Queue(ORDER_PLACED_QUEUE, true, false, false, args);
    }

    // =====================================================
    // 4️⃣ Dead Letter Queue
    // =====================================================
    @Bean
    public Queue deadLetterQueue() {
        return new Queue(ORDER_DLQ, true);
    }

    // =====================================================
    // 5️⃣ Binding Main Queue → Main Exchange
    // =====================================================
    @Bean
    public Binding bindOrderPlacedQueue() {
        return BindingBuilder
                .bind(orderPlacedQueue())
                .to(orderEventsExchange())
                .with(ORDER_PLACED_ROUTING_KEY);
    }

    // =====================================================
    // 6️⃣ Binding DLQ → DLX
    // =====================================================
    @Bean
    public Binding bindDeadLetterQueue() {
        return BindingBuilder
                .bind(deadLetterQueue())
                .to(deadLetterExchange())
                .with(ORDER_PLACED_ROUTING_KEY);
    }

    // =====================================================
    // 7️⃣ JSON Message Converter (VERY IMPORTANT)
    // =====================================================
    @Bean
    public Jackson2JsonMessageConverter jackson2JsonMessageConverter() {
        return new Jackson2JsonMessageConverter();
    }

    // =====================================================
    // 8️⃣ RabbitTemplate with JSON Support
    // =====================================================
    @Bean
    public RabbitTemplate rabbitTemplate(
            ConnectionFactory connectionFactory,
            Jackson2JsonMessageConverter messageConverter) {

        RabbitTemplate rabbitTemplate = new RabbitTemplate(connectionFactory);
        rabbitTemplate.setMessageConverter(messageConverter);
        return rabbitTemplate;
    }

    // =====================================================
    // 9️⃣ Listener Container Factory (Manual ACK)
    // =====================================================
    @Bean
    public SimpleRabbitListenerContainerFactory rabbitListenerContainerFactory(
            ConnectionFactory connectionFactory,
            Jackson2JsonMessageConverter messageConverter) {

        SimpleRabbitListenerContainerFactory factory =
                new SimpleRabbitListenerContainerFactory();

        factory.setConnectionFactory(connectionFactory);
        factory.setAcknowledgeMode(AcknowledgeMode.MANUAL);
        factory.setMessageConverter(messageConverter);

        return factory;
    }
}
