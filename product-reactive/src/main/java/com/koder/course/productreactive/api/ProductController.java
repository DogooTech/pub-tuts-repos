package com.koder.course.productreactive.api;

import com.koder.course.productreactive.dto.ProductCommand;
import com.koder.course.productreactive.dto.ProductResponse;
import com.koder.course.productreactive.service.ProductService;
import com.koder.course.productreactive.validator.ProductValidator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

@RestController
@Slf4j
@RequiredArgsConstructor
public class ProductController {

    private final ProductService productService;
    private final ProductValidator productValidator;

    @PostMapping("/api/v1/products")
    public Mono<ResponseEntity<ProductResponse>> addProduct(ProductCommand command) {

        return productValidator.validateSaveProductCommand(command).then(
                productService.saveProduct(command)
                        .map(ResponseEntity::ok)
                        .onErrorResume(e -> {
                            log.error("Error adding product: {}", e.getMessage());
                            return Mono.just(ResponseEntity.status(500).build());
                        })
        );
    }
}
