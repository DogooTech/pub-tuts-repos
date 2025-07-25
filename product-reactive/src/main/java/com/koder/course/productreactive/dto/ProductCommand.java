package com.koder.course.productreactive.dto;

import java.math.BigDecimal;

public record ProductCommand(String uuid,
                             String name,
                             String description,
                             BigDecimal price,
                             Integer stockQuantity
) {

    //with UUID
    public ProductCommand withUuid(String uuid) {
        return new ProductCommand(uuid, name, description, price, stockQuantity);
    }
}
