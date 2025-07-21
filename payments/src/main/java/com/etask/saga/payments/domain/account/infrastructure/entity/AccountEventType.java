package com.etask.saga.payments.domain.account.infrastructure.entity;

public enum AccountEventType {
    INIT,
    PAY,
    UN_PAY,
    WITHDRAW,
    DEPOSIT,
    ON_HOLD,
    UN_HOLD,
    COUPON
}
