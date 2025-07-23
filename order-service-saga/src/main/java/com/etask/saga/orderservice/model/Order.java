package com.etask.saga.orderservice.model;

import java.util.List;

public record Order(String uuid, String customerId, List<Item> items, String status) {
    public Order withUuid(String newUuid) {
        return new Order(newUuid, this.customerId, this.items, this.status);
    }

    public Order withStatus(String newStatus) {
        return new Order(this.uuid, this.customerId, this.items, newStatus);
    }
}
