package com.koder.course.productreactive.validator;

import com.koder.course.productreactive.dto.ProductCommand;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.math.BigDecimal;
import java.util.UUID;

@Service
@Slf4j
public class ProductValidator {

    /**
     * Validates the ProductCommand for adding a new product.
     *
     * @param productCommand the ProductCommand to validate
     * @return a Mono that completes successfully if validation passes, or emits an error if validation fails
     */
    public Mono<Void> validateSaveProductCommand(ProductCommand productCommand) {

        return validateLength(productCommand.name(), 3, 256)
                .then(validateSpecialCharacters(productCommand.name()))
                .then(validateLength(productCommand.description(), 3, 5000))
                .then(validatePrice(productCommand.price()))
                .then(validateStockQuantity(productCommand.stockQuantity()))
                .then(Mono.defer(() -> {
                    if (productCommand.uuid() != null) {
                        return validateUUID(productCommand.uuid());
                    }
                    return Mono.empty();
                }));
    }

    /**
     * Validates the ProductCommand for updating an existing product.
     *
     * @param productCommand the ProductCommand to validate
     * @return a Mono that completes successfully if validation passes, or emits an error if validation fails
     */
    private Mono<Void> validateStockQuantity(Integer stockQuantity) {

        if (stockQuantity == null) {
            return Mono.error(new NullPointerException("Stock quantity cannot be null"));
        }

        if (stockQuantity < 0) {
            return Mono.error(new IllegalArgumentException("Stock quantity cannot be negative"));
        }

        if (stockQuantity > 1000000) {
            return Mono.error(new IllegalArgumentException("Stock quantity exceeds maximum allowed value"));
        }

        return Mono.empty();
    }

    /**
     * Validates the price of a product.
     *
     * @param price the price to validate
     * @return a Mono that completes successfully if validation passes, or emits an error if validation fails
     */
    private Mono<Void> validatePrice(BigDecimal price) {
        if (price == null) {
            return Mono.error(new NullPointerException("Price cannot be null"));
        }

        if (price.compareTo(BigDecimal.ZERO) < 0) {
            return Mono.error(new IllegalArgumentException("Price cannot be negative"));
        }

        if (price.compareTo(new BigDecimal("999999.99")) > 0) {
            return Mono.error(new IllegalArgumentException("Price exceeds maximum allowed value"));
        }

        if (price.scale() > 2) {
            return Mono.error(new IllegalArgumentException("Price cannot have more than 2 decimal places"));
        }

        return Mono.empty();
    }

    /**
     * Validates that the product name does not contain special characters.
     *
     * @param value the product name to validate
     * @return a Mono that completes successfully if validation passes, or emits an error if validation fails
     */
    private Mono<Void> validateSpecialCharacters(String value) {
        if (value == null) {
            return Mono.error(new NullPointerException("Value cannot be null"));
        }

        if (!value.matches("^[a-zA-Z0-9_\\- ]+$")) {
            return Mono.error(new IllegalArgumentException(
                    "Value contains invalid characters. Only alphanumeric, underscore, hyphen, and space are allowed."));
        }

        return Mono.empty();
    }

    /**
     * Validates the length of a string value.
     *
     * @param value the string value to validate
     * @param minLength the minimum allowed length
     * @param maxLength the maximum allowed length
     * @return a Mono that completes successfully if validation passes, or emits an error if validation fails
     */
    private Mono<Void> validateLength(String value, int minLength, int maxLength) {
        if (value == null) {
            return Mono.error(new NullPointerException("Value cannot be null"));
        }

        if (value.length() < minLength || value.length() > maxLength) {
            return Mono.error(new IllegalArgumentException(
                    String.format("Value length must be between %d and %d characters", minLength, maxLength)));
        }

        return Mono.empty();
    }

    /**
     * Validates the UUID format.
     *
     * @param uuid the UUID string to validate
     * @return a Mono that completes successfully if validation passes, or emits an error if validation fails
     */
    public Mono<Void> validateUUID(String uuid) {
        if (uuid == null) {
            return Mono.error(new NullPointerException("UUID cannot be null"));
        }

        return Mono.defer(() -> {
            try {
                UUID.fromString(uuid);
                return Mono.empty();
            } catch (IllegalArgumentException e) {
                return Mono.error(e);
            }
        });
    }
}
