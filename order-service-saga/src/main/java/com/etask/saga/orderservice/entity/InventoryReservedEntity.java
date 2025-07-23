package com.etask.saga.orderservice.entity;

import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

import java.time.Instant;

@Table("inventory_reserved")
public record InventoryReservedEntity(
        @Id
        Long id,
        String orderId,
        String productId,
        long quality,
        InventoryStatus status,
        Instant createdAt,
        Instant updatedAt
) {}
