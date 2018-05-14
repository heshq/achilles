package com.allinpay.achilles.core;

public class BusinessException extends RuntimeException {

    private int errorCode;

    public BusinessException(String message){
        this(ErrorCode.SYSTEM_ERROR, message);
    }

    public BusinessException(int errorCode, String errMsg){
        super(errMsg);
        this.errorCode = errorCode;
    }

}
