package com.allinpay.achilles.controller;

import com.allinpay.achilles.domian.MerchantConfig;
import com.allinpay.achilles.service.MerchantConfigService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.reactive.result.view.Rendering;
import reactor.core.publisher.Mono;

@Controller
public class OrderController {

    private static final Logger logger = LoggerFactory.getLogger(OrderController.class);
   @Autowired
   private MerchantConfigService configService;

   @GetMapping(value = "orderPage/{merchantCode}")
   public Mono<Rendering> initOrderPage(@PathVariable("merchantCode") String merchantCode){
      if(StringUtils.isEmpty(merchantCode)){
          return Mono.just(Rendering.view(Constant.ERROR_PAGE)
                  .modelAttribute(Constant.ERROR_MESSAGE_LABEL, "商户编码不能为空")
                  .build());
      }
      Mono<MerchantConfig> merchantConfig = configService.findByMerchantCode(merchantCode);
      return merchantConfig
              .flatMap(data->{
                 if(data.getQrParameter() == null && data.getQuickPayParameter() == null){
                    return Mono.just(Rendering.view(Constant.ERROR_PAGE)
                            .modelAttribute(Constant.ERROR_MESSAGE_LABEL, "商户还未绑定支付产品")
                            .build());
                 }else{
                     return Mono.just(Rendering.view(Constant.ORDER_PAGE)
                             .modelAttribute("merchantConfig", data)
                             .build());
                 }
              }).switchIfEmpty(Mono.just(Rendering.view(Constant.ERROR_PAGE)
                      .modelAttribute(Constant.ERROR_MESSAGE_LABEL, "找不到此商户")
                      .build()));
   }

}
