package com.allinpay.achilles.repository;

import com.allinpay.achilles.domian.User;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import reactor.core.publisher.Mono;

public interface UserRepository extends ReactiveMongoRepository<User, String> {

    Mono<User> findByUnionId(String unionId);

    Mono<User> findByOpenIdAndMerchantNo(String openId, String merchantNo);

}
