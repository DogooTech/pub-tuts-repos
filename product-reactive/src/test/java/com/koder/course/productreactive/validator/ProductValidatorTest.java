package com.koder.course.productreactive.validator;

import com.koder.course.productreactive.dto.ProductCommand;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.math.BigDecimal;
import java.util.UUID;

@ExtendWith(MockitoExtension.class)
class ProductValidatorTest {

    private final ProductValidator validator = new ProductValidator();

    @Nested
    @DisplayName("validateSaveProductCommand")
    class ValidateSaveProductCommand {

        @org.junit.jupiter.api.Test
        void completesWhenAllFieldsAreValid() {
            ProductCommand cmd = new ProductCommand(
                    UUID.randomUUID().toString(),
                    "ValidName",
                    "Valid description",
                    new BigDecimal("123.45"),
                    100
            );
            StepVerifier.create(validator.validateSaveProductCommand(cmd))
                    .verifyComplete();
        }

        @org.junit.jupiter.api.Test
        void emitsErrorWhenNameIsTooShort() {
            ProductCommand cmd = new ProductCommand(
                    UUID.randomUUID().toString(),
                    "ab",
                    "Valid description",
                    new BigDecimal("123.45"),
                    100
            );
            StepVerifier.create(validator.validateSaveProductCommand(cmd))
                    .expectError(IllegalArgumentException.class)
                    .verify();
        }

        @org.junit.jupiter.api.Test
        void emitsErrorWhenDescriptionIsTooLong() {
            String longDesc = "a".repeat(5001);
            ProductCommand cmd = new ProductCommand(
                    UUID.randomUUID().toString(),
                    "ValidName",
                    longDesc,
                    new BigDecimal("123.45"),
                    100
            );
            StepVerifier.create(validator.validateSaveProductCommand(cmd))
                    .expectError(IllegalArgumentException.class)
                    .verify();
        }

        @org.junit.jupiter.api.Test
        void emitsErrorWhenPriceIsNegative() {
            ProductCommand cmd = new ProductCommand(
                    UUID.randomUUID().toString(),
                    "ValidName",
                    "Valid description",
                    new BigDecimal("-1.00"),
                    100
            );
            StepVerifier.create(validator.validateSaveProductCommand(cmd))
                    .expectError(IllegalArgumentException.class)
                    .verify();
        }

        @org.junit.jupiter.api.Test
        void emitsErrorWhenStockQuantityIsNull() {
            ProductCommand cmd = new ProductCommand(
                    UUID.randomUUID().toString(),
                    "ValidName",
                    "Valid description",
                    new BigDecimal("123.45"),
                    null
            );
            StepVerifier.create(validator.validateSaveProductCommand(cmd))
                    .expectError(NullPointerException.class)
                    .verify();
        }

        @org.junit.jupiter.api.Test
        void completesWhenUuidIsNull() {
            ProductCommand cmd = new ProductCommand(
                    null,
                    "ValidName",
                    "Valid description",
                    new BigDecimal("123.45"),
                    100
            );
            StepVerifier.create(validator.validateSaveProductCommand(cmd))
                    .verifyComplete();
        }

        @org.junit.jupiter.api.Test
        void emitsErrorWhenUuidIsInvalid() {
            ProductCommand cmd = new ProductCommand(
                    "invalid-uuid",
                    "ValidName",
                    "Valid description",
                    new BigDecimal("123.45"),
                    100
            );
            StepVerifier.create(validator.validateSaveProductCommand(cmd))
                    .expectError(IllegalArgumentException.class)
                    .verify();
        }
    }

    @org.junit.jupiter.api.Test
    void validateUUIDEmitsErrorWhenNull() {
        StepVerifier.create(validator.validateUUID(null))
                .expectError(NullPointerException.class)
                .verify();
    }

    @org.junit.jupiter.api.Test
    void validateUUIDEmitsErrorWhenInvalid() {
        StepVerifier.create(validator.validateUUID("not-a-uuid"))
                .expectError(IllegalArgumentException.class)
                .verify();
    }

    @org.junit.jupiter.api.Test
    void validateUUIDCompletesWhenValid() {
        StepVerifier.create(validator.validateUUID(UUID.randomUUID().toString()))
                .verifyComplete();
    }
}