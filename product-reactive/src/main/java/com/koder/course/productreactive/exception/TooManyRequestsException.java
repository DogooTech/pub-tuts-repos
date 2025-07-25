package com.koder.course.productreactive.exception;

public class TooManyRequestsException extends RuntimeException{

    public TooManyRequestsException(String message, Throwable cause) {
        super(message, cause);
    }

    public TooManyRequestsException(String message) {
        super(message);
    }
}
