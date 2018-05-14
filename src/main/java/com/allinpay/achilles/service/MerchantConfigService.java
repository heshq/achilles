package com.allinpay.achilles.service;

import com.allinpay.achilles.core.BusinessException;
import com.allinpay.achilles.domian.MerchantConfig;
import com.allinpay.achilles.repository.MerchantConfigRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import reactor.core.publisher.Mono;

@Service
public class MerchantConfigService {

    @Autowired
    private MerchantConfigRepository repository;

    public Mono<MerchantConfig> save(MerchantConfig param){
        return repository.save(param);
    }

    public Mono<MerchantConfig> findByMerchantCode(String merchantCode){
        if(StringUtils.isEmpty(merchantCode)){
            throw new BusinessException("商户编号不能为空");
        }
        return repository.findByMerchantCode(merchantCode);
    }

}
