package com.allinpay.achilles.repository;

import com.allinpay.achilles.domian.MerchantConfig;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import reactor.core.publisher.Mono;

public interface MerchantConfigRepository extends ReactiveMongoRepository<MerchantConfig, String> {

    Mono<MerchantConfig> findByMerchantCode(String merchantCode);

}
