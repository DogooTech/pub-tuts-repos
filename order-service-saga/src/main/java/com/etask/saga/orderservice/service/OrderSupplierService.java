package com.etask.saga.orderservice.service;

import com.etask.saga.orderservice.model.OrderEvent;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.Message;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.stereotype.Component;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.function.Supplier;

@Component
@Slf4j
public class OrderSupplierService {

    private final BlockingQueue<OrderEvent> orders = new LinkedBlockingQueue<>();

    public void addOrder(OrderEvent order) {
        orders.add(order);
    }

    @Bean
    public Supplier<Message<OrderEvent>> orderEventSupplier() {
        return () -> {
            OrderEvent event = orders.peek();
            if (event != null) {
                OrderEvent order = orders.poll();
                Message<OrderEvent> message = MessageBuilder
                        .withPayload(event)
                        .setHeader(KafkaHeaders.KEY, order.uuid())
                        .build();
                log.info("Order: {}", message.getPayload());
                return message;
            }
            return null;
        };
    }
}
