package com.etask.saga.payments.domain.account.core.ports.outgoing;

import com.etask.saga.base.model.OrderEvent;
import reactor.core.publisher.Mono;

public interface AccountDatabase {

    Mono<OrderEvent> payOrder(OrderEvent orderEvent);

    Mono<OrderEvent> unPayOrder(OrderEvent orderEvent);

    Mono<OrderEvent> holdAmountOrder(OrderEvent orderEvent);

}
