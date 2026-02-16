package com.example.orderprocessor.model;

import lombok.*;
import java.time.Instant;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OrderProcessedEvent {

    private String orderId;     // Unique identifier for the order
    private String status;      // Expected: "PROCESSED"
    private Instant processedAt; // ISO 8601 format
}
