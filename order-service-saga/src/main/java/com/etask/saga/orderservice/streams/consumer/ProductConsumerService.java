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
public class ProductConsumerService {

    @Bean
    public Function<KStream<String, OrderEvent>, KStream<String, OrderEvent>> productOrders() {

        return this::processProductOrders;
    }

    private KStream<String, OrderEvent> processProductOrders(KStream<String, OrderEvent> inputStream) {
        return inputStream
                .mapValues(this::processProductEvent)
                .filter((key, value) -> shouldSendToOutputTopic(value));
    }

    private OrderEvent processProductEvent(OrderEvent event) {
        log.info("Processing product order: {}", event);

        event = event.withSource(SourceEnum.PRODUCT_SERVICE);

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
            log.info("Skipping product order: {}", event);
        }

        return shouldSend;
    }
}
