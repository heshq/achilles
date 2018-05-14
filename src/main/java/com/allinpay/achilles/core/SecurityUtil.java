package com.allinpay.achilles.core;


import org.springframework.util.StringUtils;

import java.security.MessageDigest;
import java.util.List;
import java.util.Map;

public class SecurityUtil {

    public static String md5(String data, boolean upperCase){
        String result = null;
        try {
            MessageDigest md = MessageDigest.getInstance("MD5");
            result = bytes2HexString(md.digest(data.getBytes("UTF-8")));
        } catch (Exception e) {
            e.printStackTrace();
            throw new SecurityException("MD5 运算失败");
        }
        if(!StringUtils.isEmpty(result)){
            if(upperCase){
                return result.toUpperCase();
            }else{
                return result.toLowerCase();
            }
        }else{
            return result;
        }
    }

    static String md5(String aData) throws SecurityException {
        String resultString = null;
        try {
            MessageDigest md = MessageDigest.getInstance("MD5");
            resultString = bytes2HexString(md.digest(aData.getBytes("UTF-8")));
        } catch (Exception e) {
            e.printStackTrace();
            throw new SecurityException("MD5 运算失败");
        }
        return resultString;
    }

    public static String bytes2HexString(byte[] b) {
        String ret = "";
        for (int i = 0; i < b.length; i++) {
            String hex = Integer.toHexString(b[i] & 0xFF);
            if (hex.length() == 1) {
                hex = '0' + hex;
            }
            ret += hex.toUpperCase();
        }
        return ret;
    }

    public static String generateSignData(Map<String, String> data, String key, List<String> order){
        StringBuilder sb = new StringBuilder();
        order.stream()
                .filter(k->data.containsKey(k))
                .forEach(k->sb.append("&").append(k).append("=").append(data.get(k)));
        sb.append("&").append("key").append("=").append(key);
        return sb.substring(1);
    }

}
