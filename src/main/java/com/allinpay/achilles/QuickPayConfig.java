package com.allinpay.achilles;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@ConfigurationProperties(prefix = "quick.pay")
@PropertySource( name="quickpay.properties"
        ,value= {"classpath:quickpay.properties"}
        ,ignoreResourceNotFound=false,encoding="GBK")
public class QuickPayConfig {

    public static class Label{
        public static String INPUT_CHARSET = "inputCharset";
        public static String INPUT_CHARSET_UTF_8 = "1";
        public static String PICK_UP_URL = "pickupUrl";
        public static String RECEIVE_URL = "receiveUrl";
        public static String VERSION = "version";
        public static String VERSION_1 = "v1.0";
        public static String LANGUAGE = "language";
        public static String SIMPLE_CHINESE = "1";
        public static String SIGN_TYPE = "signType";
        public static String SIGN_WITH_MD5 = "0";
        public static String MERCHANT_ID = "merchantId";
        public static String ORDER_NO = "orderNo";
        public static String ORDER_AMOUNT = "orderAmount";
        public static String ORDER_CURRENCY = "orderCurrency";
        public static String CNY = "0";
        public static String ORDER_DATE_TIME = "orderDatetime";
        public static String PAY_TYPE = "payType";
        public static String PAY_TYPE_ALL = "0";
        public static String EXT1 = "ext1";
        public static String SIGN_MSG = "signMsg";
        public static String PARTNER_USER_ID = "partnerUserId";
    }

    private String serverUrl;
    private String regUri;
    private String payUri;
    private List<String> regOrder;
    private List<String> payOrder;
    private List<String> noticeOrder;
    private String pickupUrl;
    private String receiveUrl;

    public String getServerUrl() {
        return serverUrl;
    }

    public void setServerUrl(String serverUrl) {
        this.serverUrl = serverUrl;
    }

    public String getRegUri() {
        return regUri;
    }

    public void setRegUri(String regUri) {
        this.regUri = regUri;
    }

    public String getPayUri() {
        return payUri;
    }

    public void setPayUri(String payUri) {
        this.payUri = payUri;
    }

    public List<String> getRegOrder() {
        return regOrder;
    }

    public void setRegOrder(List<String> regOrder) {
        this.regOrder = regOrder;
    }

    public List<String> getPayOrder() {
        return payOrder;
    }

    public void setPayOrder(List<String> payOrder) {
        this.payOrder = payOrder;
    }

    public List<String> getNoticeOrder() {
        return noticeOrder;
    }

    public void setNoticeOrder(List<String> noticeOrder) {
        this.noticeOrder = noticeOrder;
    }

    public String getPickupUrl() {
        return pickupUrl;
    }

    public void setPickupUrl(String pickupUrl) {
        this.pickupUrl = pickupUrl;
    }

    public String getReceiveUrl() {
        return receiveUrl;
    }

    public void setReceiveUrl(String receiveUrl) {
        this.receiveUrl = receiveUrl;
    }
}
