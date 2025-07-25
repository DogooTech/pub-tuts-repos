package com.koder.course.productreactive.repo;

import com.koder.course.productreactive.entity.ProductEntity;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;

@Repository
public interface ProductRepository extends ReactiveCrudRepository<ProductEntity, Long> {

    Mono<ProductEntity> findByUuid(String uuid);
}
