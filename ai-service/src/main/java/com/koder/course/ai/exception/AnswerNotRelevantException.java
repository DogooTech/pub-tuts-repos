package com.koder.course.ai.exception;

public class AnswerNotRelevantException extends RuntimeException {
    public AnswerNotRelevantException() {
        super();
    }

    public AnswerNotRelevantException(String message) {
        super(message);
    }

    public AnswerNotRelevantException(String message, Throwable cause) {
        super(message, cause);
    }

    public AnswerNotRelevantException(Throwable cause) {
        super(cause);
    }
}
