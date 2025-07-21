package com.etask.saga.payments.domain.account.infrastructure.entity;

import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

import java.math.BigDecimal;
import java.time.Instant;

@Table("account")
public record AccountEntity(
        @Id
        Long id,
        String refId,
        AccountReferenceType accountReferenceType,
        String customerId,
        BigDecimal initialBalance,
        BigDecimal amount,
        AccountEventType eventType,
        Long version,
        Instant createdAt) {

    /// Factory method to create a new AccountEntity
    public static AccountEntity createNewForOrder(String refId,
                                                  String customerId,
                                                  BigDecimal amount,
                                                  AccountEventType eventType,
                                                  Long version) {
        return new AccountEntity(
                null,
                refId,
                AccountReferenceType.ORDER,
                customerId,
                BigDecimal.ZERO,
                amount,
                eventType,
                version,
                Instant.now()
        );
    }

    //with version
    public AccountEntity withVersion(Long version) {
        return new AccountEntity(
                this.id,
                this.refId,
                this.accountReferenceType,
                this.customerId,
                this.initialBalance,
                this.amount,
                this.eventType,
                version,
                this.createdAt
        );
    }

    //with AccountEventType
    public AccountEntity withEventType(AccountEventType eventType) {
        return new AccountEntity(
                this.id,
                this.refId,
                this.accountReferenceType,
                this.customerId,
                this.initialBalance,
                this.amount,
                eventType,
                this.version,
                this.createdAt
        );
    }
}
