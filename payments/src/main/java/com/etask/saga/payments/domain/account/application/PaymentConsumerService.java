package com.etask.saga.payments.domain.account.application;

import com.etask.saga.base.model.OrderEvent;
import com.etask.saga.base.model.OrderEventService;
import com.etask.saga.base.model.OrderEventStatus;
import com.etask.saga.payments.common.config.StreamBindingsProperties;
import com.etask.saga.payments.domain.account.core.ports.incoming.AccountPaymentAction;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.streams.kstream.KStream;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.function.Function;

@Service
@RequiredArgsConstructor
@Slf4j
public class PaymentConsumerService {

    private final AccountPaymentAction accountPaymentAction;

    private final StreamBindingsProperties properties;

    @Bean
    public Function<KStream<String, OrderEvent>, KStream<String, OrderEvent>> paymentOrders() {

        return this::processPaymentOrders;
    }

    private KStream<String, OrderEvent> processPaymentOrders(KStream<String, OrderEvent> inputStream) {
        // Process the events and store the resulting stream
        KStream<String, OrderEvent> processedStream = inputStream
                .peek((key, event) -> log.info("Received payment event for processing: key={}, event={}", key, event))
                .mapValues(event -> {
                    log.info("Handling payment for event: {}", event);
                    OrderEvent result = accountPaymentAction.handle(event).block();

                    assert result != null;

                    if (result.eventStatus() == OrderEventStatus.REJECT) {
                        result = result.withRejectedServices(List.of(OrderEventService.ACCOUNT_SERVICE));
                    }

                    log.info("Result after handling payment: {}", result);
                    return result;
                });

        // Branch the stream based on event status
        KStream<String, OrderEvent>[] branches = processedStream.branch(
                (key, value) -> value.eventStatus() == OrderEventStatus.ACCEPTED
                        || value.eventStatus() == OrderEventStatus.ACCEPTED_FAILED,
                (key, value) -> value.eventStatus() == OrderEventStatus.REJECTED ||
                        value.eventStatus() == OrderEventStatus.REJECTED_FAILED,
                (key, value) -> value.eventStatus() == OrderEventStatus.ACCEPT ||
                        value.eventStatus() == OrderEventStatus.REJECT
        );

        // Send each branch to the appropriate topic
        branches[0].to(properties.getPaymentOrdersOut0().getDestination());
        branches[1].to(properties.getPaymentOrdersOut1().getDestination());
        branches[2].to(properties.getPaymentOrdersOut2().getDestination());

        return processedStream.filter((k, v) -> false);
    }

}
