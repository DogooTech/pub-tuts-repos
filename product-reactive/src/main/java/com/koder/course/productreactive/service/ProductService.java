package com.koder.course.productreactive.service;

import com.koder.course.productreactive.dto.ProductCommand;
import com.koder.course.productreactive.dto.ProductResponse;
import com.koder.course.productreactive.mapping.ProductMapper;
import com.koder.course.productreactive.repo.ProductRepository;
import com.koder.course.productreactive.util.RetryUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.math.BigDecimal;
import java.time.Instant;

@Service
@RequiredArgsConstructor
@Slf4j
public class ProductService {

    private final ProductRepository productRepository;
    private final ProductMapper productMapper;
    private final RetryUtil retryUtil;

    public Mono<ProductResponse> saveProduct(ProductCommand command) {
        return productRepository
                .save(productMapper.toEntity(command))
                .map(productMapper::toResponse)
                .as(mono -> retryUtil.applyRetry(mono, "save product", log))
                .doOnError(error -> log.error("Error saving product: {}", error.getMessage()))
                .onErrorMap(error -> new RuntimeException("Custom mapped exception", error))
                .onErrorResume(error -> {
                    log.info("Fallback logic triggered");
                    return Mono.just(new ProductResponse(
                            "fallback-id",
                            "fallback-name",
                            "fallback-description",
                            BigDecimal.ZERO,
                            0,
                            Instant.now(),
                            Instant.now()
                    ));
                })
                .onErrorReturn(new ProductResponse(
                        "default-id",
                        "default-name",
                        "default-description",
                        BigDecimal.ZERO,
                        0,
                        Instant.now(),
                        Instant.now()
                ));
    }
}
