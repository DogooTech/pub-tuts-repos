package com.koder.course.productreactive.util;

import com.koder.course.productreactive.exception.DatabaseOperationException;
import org.slf4j.Logger;
import org.springframework.dao.DataAccessResourceFailureException;
import org.springframework.dao.RecoverableDataAccessException;
import org.springframework.dao.TransientDataAccessException;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClientException;
import reactor.core.publisher.Mono;
import reactor.util.retry.Retry;

import java.io.IOException;
import java.time.Duration;

@Component
public class RetryUtil {

    private static final int DEFAULT_MAX_RETRIES = 3;
    private static final Duration DEFAULT_INITIAL_BACKOFF = Duration.ofMillis(100);
    private static final Duration DEFAULT_MAX_BACKOFF = Duration.ofSeconds(1);
    private static final Duration GLOBAL_TIMEOUT = Duration.ofSeconds(2); // Global timeout for the entire operation

    public <T> Mono<T> applyRetry(Mono<T> mono, String operation, Logger log) {
        return configureRetry(mono, operation, log, DEFAULT_MAX_RETRIES,
                DEFAULT_INITIAL_BACKOFF, DEFAULT_MAX_BACKOFF);
    }

    public <T> Mono<T> applyRetry(Mono<T> mono, String operation, Logger log,
                                  int maxRetries, Duration initialBackoff, Duration maxBackoff) {
        return configureRetry(mono, operation, log, maxRetries, initialBackoff, maxBackoff);
    }

    private <T> Mono<T> configureRetry(Mono<T> mono, String operation, Logger log,
                                       int maxRetries, Duration initialBackoff, Duration maxBackoff) {
        return mono.retryWhen(Retry.backoff(maxRetries, initialBackoff)
                .maxBackoff(maxBackoff)
                .filter(this::isTransientDatabaseException)
                .doBeforeRetry(signal -> log.warn("Retrying {} (Attempt: {}). Error: {}",
                        operation, signal.totalRetries() + 1, signal.failure().getMessage()))
                .onRetryExhaustedThrow((spec, signal) -> {
                    log.error("Failed to {} after {} retries", operation, signal.totalRetries());
                    return new DatabaseOperationException(
                            "Database operation failed after multiple retries", signal.failure());
                }))
                .onErrorMap(ex -> {
                    if (ex instanceof java.util.concurrent.TimeoutException) {
                        log.error("Operation {} timed out after {} seconds", operation, GLOBAL_TIMEOUT.toSeconds());
                        return new DatabaseOperationException("Operation timed out", ex);
                    }
                    return ex;
                });
    }

    private boolean isTransientDatabaseException(Throwable throwable) {

        // Check for connection refused and other network errors
        if (throwable instanceof java.net.ConnectException ||
                (throwable instanceof WebClientException &&
                        (throwable.getMessage() != null && throwable.getMessage().contains("Connection refused")))) {
            return true;
        }

        return throwable instanceof TransientDataAccessException ||
                throwable instanceof DataAccessResourceFailureException ||
                throwable instanceof RecoverableDataAccessException ||
                throwable instanceof IOException ||
                // More selective with WebClientException - don't include all types
                (throwable instanceof WebClientException &&
                        !(throwable instanceof org.springframework.web.reactive.function.client.WebClientResponseException)) ||
                // Check cause recursively
                throwable.getCause() != null && isTransientDatabaseException(throwable.getCause());
    }
}