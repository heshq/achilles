package com.allinpay.achilles.domian;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Date;

@Document(collection = "Deal")
public class Deal {

    public static final String WAIT_FOR_PAY = "0";
    public static final String SERVER_RESPONSE = "1";
    public static final String TRAN_FAILED = "2";
    public static final String TRAN_TYPE_WECHAT = "0";
    public static final String TRAN_TYPE_ALIPAY = "1";
    public static final String TRAN_TYPE_QUICK_PAY = "2";

    @Id
    private String id;
    private String tranType;
    private String merchantNo;
    private String orderNo;//商户订单号
    private String reqSn;//通联流水号
    private String channelSn;//渠道流水号
    private String amount;
    private Date tranDate;
    private Date tranFinishDate;
    private String randomStr;
    private String orderTitle;
    private String remark;
    private Date validTime;
    private String limitPay;
    private String tranStatus;
    private String retCode;
    private String retMsg;
    private String qrCodeSource;
    private String oldReqSn;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTranType() {
        return tranType;
    }

    public void setTranType(String tranType) {
        this.tranType = tranType;
    }

    public String getMerchantNo() {
        return merchantNo;
    }

    public void setMerchantNo(String merchantNo) {
        this.merchantNo = merchantNo;
    }

    public String getOrderNo() {
        return orderNo;
    }

    public void setOrderNo(String orderNo) {
        this.orderNo = orderNo;
    }

    public String getReqSn() {
        return reqSn;
    }

    public void setReqSn(String reqSn) {
        this.reqSn = reqSn;
    }

    public String getChannelSn() {
        return channelSn;
    }

    public void setChannelSn(String channelSn) {
        this.channelSn = channelSn;
    }

    public String getAmount() {
        return amount;
    }

    public void setAmount(String amount) {
        this.amount = amount;
    }

    public Date getTranDate() {
        return tranDate;
    }

    public void setTranDate(Date tranDate) {
        this.tranDate = tranDate;
    }

    public Date getTranFinishDate() {
        return tranFinishDate;
    }

    public void setTranFinishDate(Date tranFinishDate) {
        this.tranFinishDate = tranFinishDate;
    }

    public String getRandomStr() {
        return randomStr;
    }

    public void setRandomStr(String randomStr) {
        this.randomStr = randomStr;
    }

    public String getOrderTitle() {
        return orderTitle;
    }

    public void setOrderTitle(String orderTitle) {
        this.orderTitle = orderTitle;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    public Date getValidTime() {
        return validTime;
    }

    public void setValidTime(Date validTime) {
        this.validTime = validTime;
    }

    public String getLimitPay() {
        return limitPay;
    }

    public void setLimitPay(String limitPay) {
        this.limitPay = limitPay;
    }

    public String getTranStatus() {
        return tranStatus;
    }

    public void setTranStatus(String tranStatus) {
        this.tranStatus = tranStatus;
    }

    public String getRetCode() {
        return retCode;
    }

    public void setRetCode(String retCode) {
        this.retCode = retCode;
    }

    public String getRetMsg() {
        return retMsg;
    }

    public void setRetMsg(String retMsg) {
        this.retMsg = retMsg;
    }

    public String getQrCodeSource() {
        return qrCodeSource;
    }

    public void setQrCodeSource(String qrCodeSource) {
        this.qrCodeSource = qrCodeSource;
    }

    public String getOldReqSn() {
        return oldReqSn;
    }

    public void setOldReqSn(String oldReqSn) {
        this.oldReqSn = oldReqSn;
    }
}
