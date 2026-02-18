package com.example.orderprocessor.service;

import com.example.orderprocessor.model.Order;
import com.example.orderprocessor.model.OrderPlacedEvent;
import com.example.orderprocessor.model.OrderStatus;
import com.example.orderprocessor.repository.OrderRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
public class OrderProcessingService {

    private final OrderRepository orderRepository;

    @Transactional
    public void processOrderPlacedEvent(OrderPlacedEvent event) {

        log.info("Received OrderPlacedEvent for orderId={}", event.getOrderId());

        Order order = orderRepository.findById(event.getOrderId())
                .orElse(new Order());

        order.setId(event.getOrderId());
        order.setCustomerId(event.getCustomerId());
        order.setProductId(event.getProductId());
        order.setQuantity(event.getQuantity());
        order.setStatus(OrderStatus.PROCESSED);

        // 🔥 THIS IS IMPORTANT
        orderRepository.save(order);

        log.info("Order {} processed successfully", event.getOrderId());
    }
}
