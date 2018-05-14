package com.allinpay.achilles.controller;

import com.allinpay.achilles.domian.MerchantConfig;
import com.allinpay.achilles.service.MerchantConfigService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("mc")
public class MerchantConfigController {

    @Autowired
    private MerchantConfigService service;

//    @GetMapping("save")
//    public Mono<MerchantConfig> test(){
//        MerchantConfig config = new MerchantConfig();
//        config.setMerchantCode("5bb8e6798bb8459898191484804c8224");
//        config.setMerchantNo("269393054995025");
//        config.setMerchantName("政融");
//        config.setAppId("00008787");
//        config.setAppKey("fqr135995255771");
//        return service.save(config);
//    }

}
