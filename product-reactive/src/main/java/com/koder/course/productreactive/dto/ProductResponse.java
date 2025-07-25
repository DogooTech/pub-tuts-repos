package com.koder.course.productreactive.dto;

import lombok.Builder;

import java.math.BigDecimal;
import java.time.Instant;

@Builder
public record ProductResponse(String uuid,
                              String name,
                              String description,
                              BigDecimal price,
                              Integer stockQuantity,
                              Instant createdAt,
                              Instant updatedAt
) { }
