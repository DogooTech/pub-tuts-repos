package com.etask.saga.orderservice.streams.consumer;

import com.etask.saga.orderservice.model.OrderEvent;
import com.etask.saga.orderservice.model.OrderEventStatus;
import com.etask.saga.orderservice.model.SourceEnum;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.streams.kstream.KStream;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

import java.util.function.Function;


@Component
@Slf4j
public class StockConsumerService {

    @Bean
    public Function<KStream<String, OrderEvent>, KStream<String, OrderEvent>> stockOrders() {

        return this::processStockOrders;
    }

    private KStream<String, OrderEvent> processStockOrders(KStream<String, OrderEvent> inputStream) {
        return inputStream
                .mapValues(this::processStockEvent)
                .filter((key, value) -> shouldSendToOutputTopic(value));
    }

    private OrderEvent processStockEvent(OrderEvent event) {
        log.info("Processing processStockEvent : {}", event);

        event = event.withSource(SourceEnum.INVENTORY_SERVICE);

        if (event.eventStatus().equals(OrderEventStatus.CREATED)) {
            event = event.withEventType(OrderEventStatus.ACCEPT);
        }

        return event;
    }

    private boolean shouldSendToOutputTopic(OrderEvent event) {
        // Add your conditions here to control message sending
        boolean shouldSend = event.order() != null &&
                (event.eventStatus().equals(OrderEventStatus.ACCEPT)
                        || event.eventStatus().equals(OrderEventStatus.REJECT));

        if (!shouldSend) {
            log.info("Skipping stock order: {}", event);
        }

        return shouldSend;
    }

}
