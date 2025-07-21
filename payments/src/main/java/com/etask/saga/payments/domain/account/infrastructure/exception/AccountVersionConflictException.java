package com.etask.saga.payments.domain.account.infrastructure.exception;

public class AccountVersionConflictException extends RuntimeException {
    public AccountVersionConflictException(String message) {
        super(message);
    }
}