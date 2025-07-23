package com.etask.saga.orderservice.model;

public record OrderEvent(String uuid, OrderEventStatus eventStatus, Order order, SourceEnum source) {
    public OrderEvent withEventType(OrderEventStatus eventStatus) {
        return new OrderEvent(this.uuid, eventStatus, this.order, this.source);
    }

    //with source
    public OrderEvent withSource(SourceEnum newSource) {
        return new OrderEvent(this.uuid, this.eventStatus, this.order, newSource);
    }

}
