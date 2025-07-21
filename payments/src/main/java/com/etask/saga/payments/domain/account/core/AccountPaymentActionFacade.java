package com.etask.saga.payments.domain.account.core;

import com.etask.saga.base.model.OrderEvent;
import com.etask.saga.base.model.OrderEventService;
import com.etask.saga.base.model.OrderEventStatus;
import com.etask.saga.payments.domain.account.core.ports.incoming.AccountPaymentAction;
import com.etask.saga.payments.domain.account.core.ports.outgoing.AccountDatabase;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

@Component
@RequiredArgsConstructor
@Slf4j
public class AccountPaymentActionFacade implements AccountPaymentAction {

    private final AccountDatabase accountDatabase;

    @Override
    public Mono<OrderEvent> handle(OrderEvent orderEvent) {

        return switch (orderEvent.eventStatus()) {
            case INIT -> accountDatabase.holdAmountOrder(orderEvent);
            case ACCEPT -> accountDatabase.payOrder(orderEvent);
            case REJECT -> orderEvent.rejectedServices().contains(OrderEventService.ACCOUNT_SERVICE)
                    ? Mono.just(orderEvent.withEventType(OrderEventStatus.REJECTED))
                    : accountDatabase.unPayOrder(orderEvent);

            default -> Mono.just(orderEvent);
        };
    }
}
