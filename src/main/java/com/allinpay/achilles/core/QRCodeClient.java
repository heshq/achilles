package com.allinpay.achilles.core;

import com.allinpay.achilles.QRCodeConfig;
import com.allinpay.achilles.domian.MerchantConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import javax.annotation.PostConstruct;

@Component
public class QRCodeClient {

    @Autowired
    private QRCodeConfig qrCodeConfig;
    private WebClient webClient;

    @PostConstruct
    public void init(){
        webClient = WebClient.create(qrCodeConfig.getPayUrl());
    }

    public Mono<Message> createRequest(Mono<MerchantConfig> config, String amount, String remark){
        return config.flatMap((data)->{
            Message message = Message.newQRCodePay(data.getQrParameter().getMerchantNo()
                    , data.getQrParameter().getAppId()
                    , data.getQrParameter().getAppKey());
            message.setTranAmount(amount);
            message.setOrderTitle("");
            message.setValidTime("");
            message.setRemark(remark);
            message.setNotifyUrl("www.baidu.com");
            message.setLimitPay("");
            message.sign();
            return Mono.just(message);
        });
    }

    public Mono<Message> send(Message request){
        MultiValueMap<String, String> formData = new LinkedMultiValueMap<>();
        request.forEach((k, v)->formData.add(k, v));
        return webClient.post()
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .body(BodyInserters.fromFormData(formData))
                .retrieve()
                .bodyToMono(String.class)
                .flatMap(str-> Mono.just(Message.parseString(str)));
    }

}
