package com.allinpay.achilles.controller;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.allinpay.achilles.QuickPayConfig;
import com.allinpay.achilles.core.BusinessException;
import com.allinpay.achilles.core.ErrorCode;
import com.allinpay.achilles.core.SecurityUtil;
import com.allinpay.achilles.core.UUIDUtil;
import com.allinpay.achilles.domian.MerchantConfig;
import com.allinpay.achilles.domian.Request;
import com.allinpay.achilles.domian.User;
import com.allinpay.achilles.service.MerchantConfigService;
import com.allinpay.achilles.service.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.result.view.Rendering;
import reactor.core.publisher.Mono;

import javax.annotation.PostConstruct;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Map;
import java.util.TreeMap;

import static com.allinpay.achilles.QuickPayConfig.Label;

@Controller
@RequestMapping("quickPay")
public class QuickPayController {

    @Autowired
    private UserService userService;
    @Autowired
    private MerchantConfigService configService;
    @Autowired
    private QuickPayConfig quickPayConfig;
    private WebClient webClient;
    private static Logger logger = LoggerFactory.getLogger(QuickPayController.class);

    @PostConstruct
    public void init(){
        webClient = WebClient.create(quickPayConfig.getServerUrl());
    }

    @RequestMapping("order")
    public Mono<Rendering> order(Request request){
        //金额格式是否正确
        Mono<MerchantConfig> merchantConfigMono = configService.findByMerchantCode(request.getMerchantCode());
        String val = null;
        try {
            Double doubleVal = Double.parseDouble(request.getAmount());
            //金额转换为分
            val = String.valueOf(Double.valueOf(doubleVal * 100).intValue());
        }catch (NumberFormatException ex){
            return merchantConfigMono
                    .flatMap(data->Mono.just(Rendering.view(Constant.ORDER_PAGE)
                            .modelAttribute(Constant.ERROR_MESSAGE_LABEL, "金额格式不正确")
                            .modelAttribute("merchantConfig", data)
                            .build()));
        }
        final String amountStr = val;
        //查询商户是否存在
        //商户是否开通了网关权限
        //查看用户是否已注册，未注册，则开始返回错误页面
        //发起订单
        return merchantConfigMono
                .switchIfEmpty(Mono.error(new BusinessException(ErrorCode.MERCHANT_NOT_EXIST, "商户编码不能为空!")))
                .flatMap(data-> findByOpenIdAndMerchantNo(request.getOpenId(), data))
                .switchIfEmpty(Mono.error(new BusinessException(ErrorCode.USER_NOT_EXIST, "该用户未注册!")))
                .zipWith(merchantConfigMono)
                .flatMap(data->Mono.just(
                        createQuickPayRequest(data.getT2()
                                , amountStr
                                , data.getT1().getAllInPayId()))
                ).flatMap(data->Mono.just(Rendering.view(Constant.LOADING_PAGE)
                        .model(data)
                        .build()))
                .switchIfEmpty(Mono.just(Rendering.view(Constant.ERROR_PAGE)
                        .modelAttribute(Constant.ERROR_MESSAGE_LABEL, "商户编码不能为空")
                        .build()));
    }

    private Mono<User> findByOpenIdAndMerchantNo(String openId, MerchantConfig merchantConfig){
        if(merchantConfig == null){
            return Mono.error(new BusinessException(ErrorCode.MERCHANT_NOT_EXIST, "商户不存在"));
        }else if(merchantConfig.getQuickPayParameter() == null
                || StringUtils.isEmpty(merchantConfig.getQuickPayParameter().getMerchantNo())){
            return Mono.error(new BusinessException(ErrorCode.MERCHANT_HAS_NO_PERMISSION, "商户未开通网关支付权限"));
        }
        return userService.findByOpenIdAndMerchantNo(openId, merchantConfig.getQuickPayParameter().getMerchantNo());
    }

    private Map<String, String> createQuickPayRequest(MerchantConfig config, String amount, String userId){
        if(config == null){
            throw new BusinessException(ErrorCode.MERCHANT_NOT_EXIST, "商户编码不能为空!");
        }else if(config.getQuickPayParameter() == null){
            throw new BusinessException(ErrorCode.MERCHANT_HAS_NO_PERMISSION, "商户无快捷支付权限");
        }
        Map<String, String> map = new TreeMap();
        map.put(Label.INPUT_CHARSET, Label.INPUT_CHARSET_UTF_8);
        map.put(Label.PICK_UP_URL, quickPayConfig.getPickupUrl());
        map.put(Label.RECEIVE_URL, quickPayConfig.getReceiveUrl());
        map.put(Label.VERSION, Label.VERSION_1);
        map.put(Label.LANGUAGE, Label.SIMPLE_CHINESE);
        map.put(Label.SIGN_TYPE, Label.SIGN_WITH_MD5);
        map.put(Label.MERCHANT_ID, config.getQuickPayParameter().getMerchantNo());
        map.put(Label.ORDER_NO, UUIDUtil.newUUID());
        map.put(Label.ORDER_AMOUNT, amount);
        map.put(Label.ORDER_CURRENCY, Label.CNY);
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyyMMddHHmmss");
        LocalDateTime now = LocalDateTime.now();
        map.put(Label.ORDER_DATE_TIME, dtf.format(now));
        map.put(Label.PAY_TYPE, Label.PAY_TYPE_ALL);
        map.put(Label.EXT1, "<USER>" + userId + "</USER>");
        String signData = SecurityUtil.generateSignData(map, config.getQuickPayParameter().getMd5Key(), quickPayConfig.getPayOrder());
        map.put(Label.SIGN_MSG, SecurityUtil.md5(signData, true));
        map.put("payUrl", quickPayConfig.getServerUrl() + quickPayConfig.getPayUri());
        logger.info("generate pay parameter:\n{}", JSONObject.toJSON(map));
        return map;
    }

    @RequestMapping("register")
    public Mono<String> register(Request request){
        //用户是否已注册，已注册不再重新注册
        //注册返回成功，
        //注册返回已注册
        //注册返回失败,失败则不显示网关支付选项
        Mono<MerchantConfig> merchantConfigMono = configService
                .findByMerchantCode(request.getMerchantCode())
                .switchIfEmpty(Mono.error(new BusinessException(ErrorCode.MERCHANT_NOT_EXIST, "商户编码不存在")));
        return merchantConfigMono.flatMap(data ->userService
                .findByOpenIdAndMerchantNo(request.getOpenId()
                        ,data.getQuickPayParameter().getMerchantNo()))
                .defaultIfEmpty(new User())
                .zipWith(merchantConfigMono)
                .flatMap(data->{
                    if(!StringUtils.isEmpty(data.getT1().getId())){
                        return Mono.just("success");
                    }else{
                        return registerUser(data.getT2(), request.getOpenId());
                    }
                });
    }

    private Mono<String> registerUser(MerchantConfig merchantConfig, String openId){
        Map<String, String> map = new TreeMap();
        map.put(Label.SIGN_TYPE, Label.SIGN_WITH_MD5);
        map.put(Label.MERCHANT_ID, merchantConfig.getQuickPayParameter().getMerchantNo());
        String userId = UUIDUtil.newUUID();
        map.put(Label.PARTNER_USER_ID, userId);
        String signData = SecurityUtil.generateSignData(map
                , merchantConfig.getQuickPayParameter().getMd5Key()
                , quickPayConfig.getRegOrder());
        signData = "&" + signData + "&";
        map.put(Label.SIGN_MSG, SecurityUtil.md5(signData, true));
        logger.info("register user to quick pay\n:{}", JSONObject.toJSON(map));
        MultiValueMap<String, String> formData = new LinkedMultiValueMap<>();
        map.forEach((k, v)->formData.add(k, v));
        return webClient.post().uri(quickPayConfig.getRegUri())
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .body(BodyInserters.fromFormData(formData))
                .retrieve()
                .bodyToMono(String.class)
                .flatMap(response->{
                    logger.info("receive message:\n{}", response);
                    JSONObject jsonObject = JSON.parseObject(response);
                    //用户注册成功
                    if("0000".equals(jsonObject.getString("resultCode"))) {
                        //设置并保存
                        User user = new User();
                        user.setOpenId(openId);
                        user.setUserCode(userId);
                        user.setAllInPayId((String)jsonObject.get("userId"));
                        user.setMerchantNo(merchantConfig.getQuickPayParameter().getMerchantNo());
                        return userService
                                .save(user)
                                .flatMap(userRecord->Mono.just("success"));
                    }else{
                        logger.error("register failed, error message:\n{}", response);
                        return Mono.just("error");
                    }
                });
    }

}
