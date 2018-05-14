package com.allinpay.achilles.domian;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "MerchantConfig")
public class MerchantConfig {

    @Id
    private String id;
    private String merchantCode;
    private String merchantName;
    private QRParameter qrParameter = new QRParameter();
    private QuickPayParameter quickPayParameter = new QuickPayParameter();

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getMerchantCode() {
        return merchantCode;
    }

    public void setMerchantCode(String merchantCode) {
        this.merchantCode = merchantCode;
    }

    public String getMerchantName() {
        return merchantName;
    }

    public void setMerchantName(String merchantName) {
        this.merchantName = merchantName;
    }

    public QRParameter getQrParameter() {
        return qrParameter;
    }

    public void setQrParameter(QRParameter qrParameter) {
        this.qrParameter = qrParameter;
    }

    public QuickPayParameter getQuickPayParameter() {
        return quickPayParameter;
    }

    public void setQuickPayParameter(QuickPayParameter quickPayParameter) {
        this.quickPayParameter = quickPayParameter;
    }
}
