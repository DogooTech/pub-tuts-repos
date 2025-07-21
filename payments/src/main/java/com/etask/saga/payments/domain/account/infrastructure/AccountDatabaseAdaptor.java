package com.etask.saga.payments.domain.account.infrastructure;

import com.etask.saga.base.model.Order;
import com.etask.saga.base.model.OrderEvent;
import com.etask.saga.base.model.OrderEventStatus;
import com.etask.saga.payments.domain.account.core.model.Account;
import com.etask.saga.payments.domain.account.core.ports.outgoing.AccountDatabase;
import com.etask.saga.payments.domain.account.infrastructure.entity.AccountEntity;
import com.etask.saga.payments.domain.account.infrastructure.entity.AccountEventType;
import com.etask.saga.payments.domain.account.infrastructure.exception.AccountSaveException;
import com.etask.saga.payments.domain.account.infrastructure.exception.AccountVersionConflictException;
import com.etask.saga.payments.domain.account.infrastructure.repository.AccountRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;
import reactor.util.retry.Retry;

import java.math.BigDecimal;
import java.time.Duration;

@Component
@RequiredArgsConstructor
@Slf4j
public class AccountDatabaseAdaptor implements AccountDatabase {

    private final AccountRepository accountRepository;

    @Override
    public Mono<OrderEvent> payOrder(OrderEvent orderEvent) {

        // First attempt PAY, then if successful, attempt UN_HOLD
        return attemptToPayWithRetry(orderEvent.order(), AccountEventType.PAY, 3)
                .flatMap(success -> {
                    if (!success) {
                        return Mono.just(orderEvent.withEventType(OrderEventStatus.ACCEPTED_FAILED));
                    }
                    // Only if PAY succeeded, attempt UN_HOLD
                    return attemptToPayWithRetry(orderEvent.order(), AccountEventType.UN_HOLD, 3)
                            .map(unHoldResult -> unHoldResult
                                    ? orderEvent.withEventType(OrderEventStatus.ACCEPTED)
                                    : orderEvent.withEventType(OrderEventStatus.ACCEPTED_FAILED)
                            );
                })
                .onErrorResume(e -> {
                    log.error("Error processing payment for order: {}", orderEvent.order().uuid(), e);
                    return Mono.just(orderEvent.withEventType(OrderEventStatus.ACCEPTED_FAILED));
                });
    }

    @Override
    public Mono<OrderEvent> unPayOrder(OrderEvent orderEvent) {
        return attemptToPayWithRetry(orderEvent.order(), AccountEventType.UN_HOLD, 3)
                .flatMap(result -> {
                    if (result) {
                        return Mono.just(orderEvent.withEventType(OrderEventStatus.REJECTED));
                    } else {
                        return Mono.just(orderEvent.withEventType(OrderEventStatus.REJECTED_FAILED));
                    }
                })
                .onErrorResume(e -> {
                    log.error("Error processing payment for order: {}", orderEvent.order().uuid(), e);
                    return Mono.just(orderEvent.withEventType(OrderEventStatus.REJECTED_FAILED));
                });
    }

    @Override
    public Mono<OrderEvent> holdAmountOrder(OrderEvent orderEvent) {
        return attemptToPayWithRetry(orderEvent.order(), AccountEventType.ON_HOLD, 3)
                .flatMap(result -> {
                    if (result) {
                        return Mono.just(orderEvent.withEventType(OrderEventStatus.ACCEPT));
                    } else {
                        return Mono.just(orderEvent.withEventType(OrderEventStatus.REJECT));
                    }
                })
                .onErrorResume(e -> {
                    log.error("Error processing payment for order: {}", orderEvent.order().uuid(), e);
                    return Mono.just(orderEvent.withEventType(OrderEventStatus.REJECT));
                });
    }

    private Mono<Boolean> attemptToPayWithRetry(Order order, AccountEventType eventType, int maxRetries) {
        return attemptToPay(order, eventType)
                .retryWhen(Retry.from(companion -> companion
                        .filter(retrySignal ->
                                retrySignal.failure() instanceof AccountVersionConflictException ||
                                        retrySignal.failure() instanceof AccountSaveException
                        )
                        .flatMap(retrySignal -> {
                            long attempt = retrySignal.totalRetries() + 1;
                            if (attempt > maxRetries) {
                                return Mono.error(retrySignal.failure());
                            }

                            long delayMillis;
                            if (retrySignal.failure() instanceof AccountVersionConflictException) {
                                // Shorter delay for version conflicts
                                delayMillis = 50 * attempt;
                                log.warn("Version conflict detected, retrying in {}ms. Attempt: {}/{}",
                                        delayMillis, attempt, maxRetries);
                            } else {
                                // Longer delay for save exceptions
                                delayMillis = 200 * attempt;
                                log.warn("Save exception detected, retrying in {}ms. Attempt: {}/{}",
                                        delayMillis, attempt, maxRetries);
                            }

                            return Mono.delay(Duration.ofMillis(delayMillis));
                        })
                ));
    }

    private BigDecimal getCostFromOrder(Order order) {
        return order.items().stream()
                .map(product -> BigDecimal.valueOf(product.cost()).multiply(BigDecimal.valueOf(product.quality())))
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    private Mono<Boolean> attemptToPay(Order order, AccountEventType accountEventType) {
        return accountRepository.findByCustomerIdOrderByVersion(order.customerId())
                .collectList()
                .flatMap(accounts -> {
                    if (accounts.isEmpty()) {
                        log.info("No account found for customerId: {}", order.customerId());
                        return Mono.just(false);
                    }

                    BigDecimal totalCost = getCostFromOrder(order);

                    Account account = Account.from(accounts);

                    long expectedVersion = account.getVersion() + 1;

                    if (account.getBalance().compareTo(totalCost) < 0) {
                        log.info("Insufficient balance for customerId: {}", order.customerId());
                        return Mono.just(false);
                    }

                    AccountEntity entity =
                            AccountEntity.createNewForOrder(
                                    order.uuid(),
                                    order.customerId(),
                                    totalCost,
                                    accountEventType,
                                    expectedVersion);

                    return accountRepository.findByCustomerIdAndVersion(order.customerId(), expectedVersion)
                            .hasElements()
                            .flatMap(exist -> {
                                if (exist) {
                                    log.info("Version conflict detected for customerId: {}", order.customerId());
                                    return Mono.error(new AccountVersionConflictException("Version conflict detected for customerId: " + order.customerId()));
                                }

                                return accountRepository.save(entity.withEventType(accountEventType))
                                        .doOnSuccess(saved ->  log.info("Account updated successfully for customerId: {}", order.customerId()))
                                        .thenReturn(true)
                                        .onErrorResume(e -> {
                                            log.error("Error saving account for customerId: {}", order.customerId(), e);
                                            return Mono.error(new AccountSaveException("Error saving account for customerId: " + order.customerId(), e));
                                        });
                            });

                });
    }
}
