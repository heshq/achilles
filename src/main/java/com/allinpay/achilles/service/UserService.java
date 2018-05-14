package com.allinpay.achilles.service;

import com.allinpay.achilles.core.BusinessException;
import com.allinpay.achilles.domian.Request;
import com.allinpay.achilles.domian.User;
import com.allinpay.achilles.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

@Service
public class UserService {

    @Autowired
    private UserRepository repository;
    private WebClient webClient = WebClient.create();

    public Mono<User> findByUnionId(String unionId){
        if(StringUtils.isEmpty(unionId)){
            throw new BusinessException("用户唯一标识不能为空");
        }
        return repository.findByUnionId(unionId);
    }

    public Mono<User> findByOpenIdAndMerchantNo(String openId, String merchantNo){
        return repository.findByOpenIdAndMerchantNo(openId, merchantNo);
    }

    public Mono<User> registerToQuickPay(Request request, String url){
        return null;
//        Map<String, String> map = new TreeMap();
//        map.put("signType", "0");
//        map.put("merchantId", "008350253116062");
//        map.put("partnerUserId", "9493bc95d05d4ea28eb8891deb65a34a");
//        String signData = SecurityUtil.generateSignData(map, "1234567890", REGISTER_ORDER);
//        signData = "&" + signData + "&";
//        map.put("signMsg", SecurityUtil.md5(signData, true));
//        MultiValueMap<String, String> formData = new LinkedMultiValueMap<>();
//        map.forEach((k, v)->formData.add(k, v));
//        Mono.just(Rendering.view("quickPayLoading")
//                .model(map)
//                .build());
    }

    public Mono<User> save(User user){
        return repository.save(user);
    }

}
