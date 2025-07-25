package com.koder.course.productreactive.entity;

import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

import java.math.BigDecimal;
import java.time.Instant;

@Table("products")
public record ProductEntity(
        @Id
        Long id,
        String uuid,
        String name,
        String description,
        BigDecimal price,
        Integer stockQuantity,
        Instant createdAt,
        Instant updatedAt
) {

    // Constructor with all fields
    public ProductEntity(Long id,
                         String uuid,
                         String name,
                         String description,
                         BigDecimal price,
                         Integer stockQuantity,
                         Instant createdAt,
                         Instant updatedAt) {
        this.id = id;
        this.uuid = uuid;
        this.name = name;
        this.description = description;
        this.price = price;
        this.stockQuantity = stockQuantity;
        this.createdAt = createdAt != null ? createdAt : Instant.now();
        this.updatedAt = updatedAt != null ? updatedAt : Instant.now();
    }

    //with name
    public ProductEntity withName(String name) {
        return new ProductEntity(id, uuid, name, description, price, stockQuantity, createdAt, updatedAt);
    }

    //with description
    public ProductEntity withDescription(String description) {
        return new ProductEntity(id, uuid, name, description, price, stockQuantity, createdAt, updatedAt);
    }

    public ProductEntity withPrice(BigDecimal price) {
        return new ProductEntity(id, uuid, name, description, price, stockQuantity, createdAt, updatedAt);
    }

    public ProductEntity withStockQuantity(Integer stockQuantity) {
        return new ProductEntity(id, uuid, name, description, price, stockQuantity, createdAt, updatedAt);
    }

    //with updatedAt
    public ProductEntity withUpdatedAt(Instant updatedAt) {
        return new ProductEntity(id, uuid, name, description, price, stockQuantity, createdAt, updatedAt);
    }
}
