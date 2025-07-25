package com.koder.course.productreactive.api;

import com.koder.course.productreactive.exception.ProductNotFoundException;
import com.koder.course.productreactive.exception.TooManyRequestsException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.reactive.result.method.annotation.ResponseEntityExceptionHandler;

/**
 * Global exception handler for handling application-wide exceptions.
 * This class extends `ResponseEntityExceptionHandler` and provides
 * specific exception handling methods for various exception types.
 */
@RestControllerAdvice
public class GlobalExceptionHandler extends ResponseEntityExceptionHandler {

    /**
     * Handles generic exceptions that are not explicitly handled by other methods.
     * Constructs a `ProblemDetail` object with an HTTP 500 status and the exception message.
     *
     * @param ex The exception that was thrown.
     * @return A `ResponseEntity` containing the `ProblemDetail` with an HTTP 500 status.
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ProblemDetail> handleGenericException(Exception ex) {
        ProblemDetail problemDetail = ProblemDetail.forStatus(HttpStatus.INTERNAL_SERVER_ERROR);
        problemDetail.setTitle("Internal Server Error");
        problemDetail.setDetail(ex.getMessage());
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(problemDetail);
    }

    /**
     * Handles NullPointerExceptions by constructing a ProblemDetail object
     * with an HTTP 400 status and the exception message.
     *
     * @param ex The NullPointerException that was thrown.
     * @return A ResponseEntity containing the ProblemDetail with an HTTP 400 status.
     */
    @ExceptionHandler(NullPointerException.class)
    public ResponseEntity<ProblemDetail> handleNullPointerException(NullPointerException ex) {
        ProblemDetail problemDetail = ProblemDetail.forStatus(HttpStatus.BAD_REQUEST);
        problemDetail.setTitle("Null Pointer Exception");
        problemDetail.setDetail(ex.getMessage());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(problemDetail);
    }

    /**
     * Handles IllegalArgumentExceptions by constructing a ProblemDetail object
     * with an HTTP 400 status and the exception message.
     *
     * @param ex The IllegalArgumentException that was thrown.
     * @return A ResponseEntity containing the ProblemDetail with an HTTP 400 status.
     */
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ProblemDetail> handleIllegalArgumentException(IllegalArgumentException ex) {
        ProblemDetail problemDetail = ProblemDetail.forStatus(HttpStatus.BAD_REQUEST);
        problemDetail.setTitle("IllegalArgumentException");
        problemDetail.setDetail(ex.getMessage());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(problemDetail);
    }

    /**
     * Handles ProductNotFoundException by constructing a ProblemDetail object
     * with an HTTP 404 status and the exception message.
     *
     * @param ex The ProductNotFoundException that was thrown.
     * @return A ResponseEntity containing the ProblemDetail with an HTTP 404 status.
     */
    @ExceptionHandler(ProductNotFoundException.class)
    public ResponseEntity<ProblemDetail> handleProductNotFoundException(ProductNotFoundException ex) {
        ProblemDetail problemDetail = ProblemDetail.forStatus(HttpStatus.NOT_FOUND);
        problemDetail.setTitle("ProductNotFoundException");
        problemDetail.setDetail(ex.getMessage());
        problemDetail.setStatus(HttpStatus.NOT_FOUND);
        problemDetail.setProperty("errorCode", "PRODUCT_NOT_FOUND");
        problemDetail.setProperty("timestamp", System.currentTimeMillis());
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(problemDetail);
    }

    @ExceptionHandler(TooManyRequestsException.class)
    public ResponseEntity<ProblemDetail> handleTooManyRequestsException(TooManyRequestsException ex) {
        ProblemDetail problemDetail = ProblemDetail.forStatus(HttpStatus.TOO_MANY_REQUESTS);
        problemDetail.setTitle("TOO_MANY_REQUESTS");
        problemDetail.setDetail(ex.getMessage());
        problemDetail.setStatus(HttpStatus.TOO_MANY_REQUESTS);
        problemDetail.setProperty("errorCode", "TOO_MANY_REQUESTS");
        problemDetail.setProperty("timestamp", System.currentTimeMillis());
        return ResponseEntity.status(HttpStatus.TOO_MANY_REQUESTS).body(problemDetail);
    }
}
