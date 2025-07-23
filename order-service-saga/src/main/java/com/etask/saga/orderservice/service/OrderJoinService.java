package com.etask.saga.orderservice.service;

import com.etask.saga.orderservice.model.OrderEvent;
import com.etask.saga.orderservice.model.OrderEventStatus;
import com.etask.saga.orderservice.model.SourceEnum;
import org.springframework.stereotype.Service;

@Service
public class OrderJoinService {

    public OrderEvent firstJoin(OrderEvent stock, OrderEvent delivery) {

        OrderEvent o = new OrderEvent(stock.uuid(), stock.eventStatus(), stock.order(), SourceEnum.ORDER_SERVICE);

        if (stock.eventStatus().equals(OrderEventStatus.ACCEPT)
                && delivery.eventStatus().equals(OrderEventStatus.ACCEPT)) {
            o = o.withEventType(OrderEventStatus.CONFIRMED);
        } else {
            o = o.withEventType(OrderEventStatus.REJECTED);
        }

        return o;
    }

    public OrderEvent nextJoin(OrderEvent stockAndDelivery, OrderEvent payment) {

        OrderEvent o = new OrderEvent(stockAndDelivery.uuid(), stockAndDelivery.eventStatus(), stockAndDelivery.order(), SourceEnum.ORDER_SERVICE);

        if (stockAndDelivery.eventStatus().equals(OrderEventStatus.CONFIRMED)
                && payment.eventStatus().equals(OrderEventStatus.ACCEPT)) {
            o = o.withEventType(OrderEventStatus.CONFIRMED);
        } else {
            o = o.withEventType(OrderEventStatus.REJECTED);
        }

        return o;
    }

}
