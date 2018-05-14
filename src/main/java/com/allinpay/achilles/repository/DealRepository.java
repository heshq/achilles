package com.allinpay.achilles.repository;

import com.allinpay.achilles.domian.Deal;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;

@Repository
public interface DealRepository extends ReactiveMongoRepository<Deal, String> {

    Mono<Deal> findByReqSn(String reqSn);

}
