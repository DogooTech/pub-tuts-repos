package com.etask.saga.payments.domain.account.infrastructure.repository;

import com.etask.saga.payments.domain.account.infrastructure.entity.AccountEntity;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;

@Repository
public interface AccountRepository extends ReactiveCrudRepository<AccountEntity, Long> {

    Flux<AccountEntity> findByCustomerIdOrderByVersion(String customerId);

    Flux<AccountEntity> findByCustomerIdAndVersion(String customerId, long version);
}
