package com.etask.saga.payments.domain.account.infrastructure.exception;

public class AccountSaveException extends RuntimeException {
    public AccountSaveException(String message, Throwable cause) {
        super(message, cause);
    }
}
