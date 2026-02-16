package com.example.orderprocessor.model;

import lombok.*;
import java.time.Instant;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OrderPlacedEvent {

    private String orderId;     // Unique identifier for the order
    private String productId;   // Identifier for the product
    private Integer quantity;   // Quantity of the product
    private String customerId;  // Identifier of the customer
    private Instant timestamp;  // ISO 8601 format
}
