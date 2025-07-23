package com.etask.saga.orderservice.entity;

import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

import java.time.Instant;

@Table("inventory")
public record InventoryEntity(
        @Id
        Long id,
        String productId,
        String productName,
        long initAvailableQuantity,
        Instant createdAt,
        Instant updatedAt
) {
}
