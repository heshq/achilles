package com.allinpay.achilles.core;

import com.alibaba.fastjson.JSON;
import org.springframework.util.StringUtils;

import java.util.Map;
import java.util.TreeMap;

public class Message extends TreeMap<String, String> {

    public static final String RET_CODE = "retcode";
    public static final String RET_MSG = "retmsg";
    public static final String SUCCESS = "success";
    public static final String SIGN = "sign";
    public static final String MERCHANT_NO = "cusid";
    public static final String APP_ID = "appid";
    public static final String VERSION = "version";
    public static final String DEFAULT_VERSION = "11";
    public static final String REQ_SN = "reqsn";
    public static final String PAY_TYPE = "paytype";
    private static final String WE_CHAT_QR_CODE = "W01";
    public static final String RANDOM_STR = "randomstr";
    public static final String TRAN_AMOUNT = "trxamt";
    public static final String ORDER_TITLE = "body";
    public static final String REMARK = "remark";
    public static final String VALID_TIME = "validtime";
    public static final String NOTIFY_URL = "notify_url";
    public static final String KEY = "key";
    public static final String LIMIT_PAY = "limit_pay";
    public static final String QR_CODE = "payinfo";

    public static Message newQRCodePay(String merchantNo, String appId, String key){
        Message request = new Message();
        request.put(MERCHANT_NO, merchantNo);
        request.put(APP_ID, appId);
        request.put(VERSION, DEFAULT_VERSION);
        request.put(REQ_SN, UUIDUtil.newUUID());
        request.put(PAY_TYPE, WE_CHAT_QR_CODE);
        request.put(RANDOM_STR, UUIDUtil.newUUID());
        request.put(KEY, key);
        return request;
    }

    public static Message parseString(String str){
        if(StringUtils.isEmpty(str)){
            throw new BusinessException("解析报文出错，无法将此字符串解析为JSON:" + str);
        }
        Map<String, String> data = (Map<String, String>)JSON.parse(str);
        Message msg = new Message();
        data.forEach((key, val)->msg.put(key, val));
        return msg;
    }

    public void setTranAmount(String amountStr){
        this.put(TRAN_AMOUNT, amountStr);
    }

    public String getTranAmount(){
        return this.get(TRAN_AMOUNT);
    }

    public String getMerchantNo(){
        return this.get(MERCHANT_NO);
    }

    public String getReqSn(){
        return this.get(REQ_SN);
    }

    public String getRandomStr(){
        return this.get(RANDOM_STR);
    }

    public void setOrderTitle(String orderTitle){
        this.put(ORDER_TITLE, orderTitle);
    }

    public String getOrderTitle(){
        return this.get(ORDER_TITLE);
    }

    public void setRemark(String remark){
        this.put(REMARK, remark);
    }

    public String getRemark(){
        return this.get(REMARK);
    }

    public void setValidTime(String validTime){
        this.put(VALID_TIME, validTime);
    }

    public String getValidTime(){
        return this.get(VALID_TIME);
    }

    public void setNotifyUrl(String notifyUrl){
        this.put(NOTIFY_URL, notifyUrl);
    }

    public String getNotifyUrl(){
        return this.get(NOTIFY_URL);
    }

    public void setLimitPay(String limitPay){
        this.put(LIMIT_PAY, limitPay);
    }

    public String getLimitPay(){
        return this.get(LIMIT_PAY);
    }

    public String getRetCode(){
        return this.get(RET_CODE);
    }

    public String getRetMsg(){
        return this.get(RET_MSG);
    }

    public String getQrCode(){
        return this.get(QR_CODE);
    }

    public boolean isSuccess(){
        return Message.SUCCESS.equalsIgnoreCase(this.getRetCode());
    }

    private static final String SIGN_LABEL = "sign";
    private static final String SEPARATOR = "&";
    private static final String EQUAL_SYMBOL = "=";

    private String getSignData(){
        StringBuilder sb = new StringBuilder();
        this.forEach((key, value)-> {
            if (!StringUtils.isEmpty(value) && !SIGN_LABEL.equalsIgnoreCase(key)){
                sb.append(SEPARATOR).append(key).append(EQUAL_SYMBOL).append(value);
            }
        });
        //remove first &
        return sb.substring(1);
    }

    public void sign(){
        String sign = SecurityUtil.md5(this.getSignData());
        this.put(SIGN, sign);
    }

    public boolean validSign() throws Exception{
        if(!this.isEmpty()){
            if(!this.containsKey(SIGN))
                return false;
            String sign = this.get(SIGN);
            String mySign = SecurityUtil.md5(this.getSignData());
            return sign.toLowerCase().equals(mySign.toLowerCase());
        }else{
            return false;
        }
    }
}
