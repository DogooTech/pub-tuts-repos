package com.etask.saga.orderservice.streams.consumer;

import com.etask.saga.orderservice.model.OrderEvent;
import com.etask.saga.orderservice.service.OrderJoinService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.streams.kstream.JoinWindows;
import org.apache.kafka.streams.kstream.KStream;
import org.apache.kafka.streams.kstream.StreamJoined;
import org.springframework.context.annotation.Bean;
import org.springframework.kafka.support.serializer.JsonSerde;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.util.function.Function;

@Component
@Slf4j
@RequiredArgsConstructor
public class OrderSagaService {

    private final OrderJoinService orderJoinService;

    @Bean
    public Function<KStream<String, OrderEvent>,
                Function<KStream<String, OrderEvent>,
                        Function<KStream<String, OrderEvent>, KStream<String, OrderEvent>>>> orderEventSaga() {

        JsonSerde<OrderEvent> orderSerde = new JsonSerde<>(OrderEvent.class);

        return stocks ->
                deliveries ->
                        payments -> {

            log.info("Starting order saga processing");
            JoinWindows joinWindow = JoinWindows.ofTimeDifferenceWithNoGrace(Duration.ofSeconds(10));

            log.debug("Joining stock and delivery streams");
            KStream<String, OrderEvent> stockAndDeliveryJoin =
                    stocks.join(
                            deliveries,
                            orderJoinService::firstJoin,
                            joinWindow,
                            StreamJoined.with(Serdes.String(), orderSerde, orderSerde));

            log.debug("Joining result with payment stream");
            KStream<String, OrderEvent> result =
                    stockAndDeliveryJoin.join(
                            payments,
                            orderJoinService::nextJoin,
                            joinWindow,
                            StreamJoined.with(Serdes.String(), orderSerde, orderSerde));

            log.info("Order saga processing complete");
            return result;
        };
    }
}
