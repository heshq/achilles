package com.allinpay.achilles;


import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = "qr.code")
@PropertySource( name="qrcode.properties"
        ,value= {"classpath:qrcode.properties"}
        ,ignoreResourceNotFound=false,encoding="GBK")
public class QRCodeConfig {

    private String payUrl;
    private String notifyUrl;

    public String getPayUrl() {
        return payUrl;
    }

    public void setPayUrl(String payUrl) {
        this.payUrl = payUrl;
    }

    public String getNotifyUrl() {
        return notifyUrl;
    }

    public void setNotifyUrl(String notifyUrl) {
        this.notifyUrl = notifyUrl;
    }
}
