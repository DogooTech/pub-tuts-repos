package com.etask.saga.orderservice.streams.supplier;

import com.etask.saga.orderservice.model.Order;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.Message;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.stereotype.Component;

import java.util.LinkedList;
import java.util.Objects;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.function.Supplier;

//@Component
@Slf4j
public class SupplierService {

    // Use a thread-safe queue implementation
    private final BlockingQueue<Order> orders = new LinkedBlockingQueue<>();

    // Method to add orders safely
    public void addOrder(Order order) {
        orders.add(order);
    }

    //@Bean
    public Supplier<Message<Order>> orderBuySupplier() {
        return () -> {
            if (orders.peek() != null) {
                Message<Order> o = MessageBuilder
                        .withPayload(orders.peek())
                        .setHeader(KafkaHeaders.KEY, Objects.requireNonNull(orders.poll()).uuid())
                        .build();
                log.info("Order: {}", o.getPayload());
                return o;
            } else {
                return null;
            }
        };
    }

}
