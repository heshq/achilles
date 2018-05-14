package com.allinpay.achilles.core;

import java.util.UUID;

public class UUIDUtil {

    public static String newUUID(){
        return UUID.randomUUID().toString().replace("-", "");
    }

}
