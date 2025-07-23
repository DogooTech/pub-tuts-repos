package com.etask.saga.orderservice.entity;

import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

import java.time.Instant;

@Table("delivery")
public record DeliveryEntity(
    @Id
    Long id,
    String orderId,
    String deliveryStatus,
    String address,
    String recipientName,
    String contactNumber,
    Instant scheduledDeliveryTime,
    Instant createdAt,
    Instant updatedAt
) { }
