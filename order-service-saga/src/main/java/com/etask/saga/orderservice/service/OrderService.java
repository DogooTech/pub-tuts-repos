package com.etask.saga.orderservice.service;

import com.etask.saga.orderservice.model.Order;
import com.etask.saga.orderservice.model.OrderEvent;
import com.etask.saga.orderservice.model.OrderEventStatus;
import com.etask.saga.orderservice.model.SourceEnum;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
public class OrderService {
    private final OrderSupplierService supplierService;

    public Mono<Order> createOrder(Order order) {
        Order preparedOrder = prepareOrder(order);
        OrderEvent event = createOrderEvent(preparedOrder);
        supplierService.addOrder(event);
        return Mono.just(preparedOrder);
    }

    private OrderEvent createOrderEvent(Order order) {
        return new OrderEvent(order.uuid(), OrderEventStatus.CREATED, order, SourceEnum.ORDER_SERVICE);
    }

    private Order prepareOrder(Order order) {
        Order result = order;
        if (result.uuid() == null) {
            result = result.withUuid(java.util.UUID.randomUUID().toString());
        }
        if (result.status() == null) {
            result = result.withStatus("NEW");
        }
        return result;
    }
}
