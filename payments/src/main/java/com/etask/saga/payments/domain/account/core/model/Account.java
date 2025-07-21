package com.etask.saga.payments.domain.account.core.model;

import com.etask.saga.payments.domain.account.infrastructure.entity.AccountEntity;
import com.etask.saga.payments.domain.account.infrastructure.entity.AccountEventType;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

@Getter
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class Account {

    private String customerId;

    private BigDecimal balance;

    private long version;

    private Instant lastUpdatedAt;

    public static Account from(List<AccountEntity> accountEntities) {

        if (accountEntities.isEmpty()) {
            throw new IllegalArgumentException("Cannot create Account from empty accountEntities");
        }

        AccountEntity createEvent = accountEntities.stream()
                .filter(accountEntity -> accountEntity.eventType() == AccountEventType.INIT)
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("No account creation event found"));

        String customerId = createEvent.customerId();
        BigDecimal balance = createEvent.initialBalance();

        AtomicReference<Instant> lastUpdated = new AtomicReference<>(createEvent.createdAt());

        balance = accountEntities.stream()
                .filter(accountEntity -> accountEntity.eventType() != AccountEventType.INIT)
                .peek(accountEntity -> lastUpdated.set(accountEntity.createdAt()))
                .reduce(balance, (currentBalance, accountEntity) -> switch (accountEntity.eventType()) {
                    case PAY, WITHDRAW, ON_HOLD -> currentBalance.subtract(accountEntity.amount());
                    case UN_PAY, DEPOSIT, COUPON, UN_HOLD -> currentBalance.add(accountEntity.amount());
                    default -> currentBalance;
                }, BigDecimal::add);

        return new Account(customerId, balance, accountEntities.size(), lastUpdated.get());
    }
}
