package com.etask.saga.payments.domain.account.core.ports.incoming;

import com.etask.saga.base.model.OrderEvent;
import reactor.core.publisher.Mono;

public interface AccountPaymentAction {
    Mono<OrderEvent> handle(OrderEvent orderEvent);
}
