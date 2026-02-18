package com.example.orderprocessor.service;

import com.example.orderprocessor.model.OrderPlacedEvent;
import com.rabbitmq.client.Channel;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class OrderEventListener {

    private final OrderProcessingService orderProcessingService;

    @RabbitListener(
            queues = "order.placed.queue",
            containerFactory = "rabbitListenerContainerFactory"
    )
    public void handleOrderPlacedEvent(
            @Payload OrderPlacedEvent event,
            Channel channel,
            org.springframework.amqp.core.Message amqpMessage
    ) throws Exception {

        long deliveryTag = amqpMessage.getMessageProperties().getDeliveryTag();

        try {

            orderProcessingService.processOrderPlacedEvent(event);

            // ACK on success
            channel.basicAck(deliveryTag, false);
            log.info("Message ACKed successfully");

        } catch (IllegalArgumentException e) {

            // Permanent error → send to DLQ
            log.error("Permanent error. Sending to DLQ: {}", e.getMessage());
            channel.basicNack(deliveryTag, false, false);

        } catch (Exception e) {

            // Transient error → requeue
            log.error("Transient error. Requeuing message: {}", e.getMessage());
            channel.basicNack(deliveryTag, false, true);
        }
    }
}
