package com.allinpay.achilles.core;

public interface ErrorCode {

    int SYSTEM_ERROR = 0x2000;
    int MERCHANT_NOT_EXIST = 0x2001;
    int USER_NOT_EXIST = 0x2002;
    int MERCHANT_HAS_NO_PERMISSION = 0x2003;

}
