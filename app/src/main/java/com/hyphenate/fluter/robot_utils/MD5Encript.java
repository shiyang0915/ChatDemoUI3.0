package com.hyphenate.fluter.robot_utils;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class MD5Encript {

    public static final String PWD = "JDKFIiur278$92";


    /**
     * @param psd
     * @param salt
     * @return MD5加密后的字符串
     * @Des 得到相应的一个MD5加密后的字符串
     */
    public static String encoder(String psd, String salt) {
        try {
            // 1.指定加密算法
            MessageDigest digest = MessageDigest.getInstance("MD5");
            // 2.将需要加密的字符串转化成byte类型的数据，然后进行哈希过程
            byte[] md5Str = digest.digest(psd.getBytes());
            String md5String = getStr(md5Str);

            byte[] bs = digest.digest((salt + md5String + salt).getBytes());
            String sign = getStr(bs);
            // 3.遍历bs,让其生成32位字符串，固定写法

            // 4.拼接字符串

            return sign;
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static String getStr(byte[] bs){
        StringBuffer stingBuffer = new StringBuffer();
        for (byte b : bs) {
            int i = b & 0xff;
            String hexString = Integer.toHexString(i);
            if (hexString.length() < 2) {
                hexString = "0" + hexString;
            }
            stingBuffer.append(hexString);
        }
        return stingBuffer.toString();
    }
}
