package com.hyphenate.fluter.robot_utils;

import android.util.Log;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Set;


public class RequestCommand {

    //返回数据成功
    public static final int RESPONSE_SUCCESS = 1;


    public interface RequestListener {
        void onResponse(Object o);

        void failure(String message);
    }


    /**
     * 登录接口
     */
    public static final String URL_LOGIN = "http://authtest.chinasosmart.com/login/butler_login";


    /**
     * 修改密码
     */
    public static final String UPDATE_PWD = "http://authtest.chinasosmart.com/index/index";


    /**
     * 意见反馈
     */
    public static final String SUGGESTION_BACK = "http://authtest.chinasosmart.com/index/index";


    /**
     * 通用的接口（除了登录接口，其余接口的地址一致）
     */
    public static final String ALL_COMMON = "http://authtest.chinasosmart.com/index/index";


    /**
     * 根据请求参数获得签名
     *
     * @param map
     * @return
     */
    public static String getSign(Map<String, String> map) {
        Set<String> keySet = map.keySet();
        List<String> keyList = new ArrayList<>();
        for (String key : keySet) {
            keyList.add(key);
        }
        Collections.sort(keyList, new Comparator<String>() {
            @Override
            public int compare(String o1, String o2) {
                return o1.compareTo(o2);
            }
        });
        StringBuffer sb = new StringBuffer();
        for (String key : keyList) {
            try {
                sb.append(key).append("=").append(URLEncoder.encode(map.get(key), "UTF-8")).append("&").toString();
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
        }
        String totalData = sb.substring(0, sb.length() - 1);
        String md5Data = MD5Encript.encoder(totalData, MD5Encript.PWD);
        Log.i("shiyangTag", "totalData: " + totalData);
        Log.i("shiyangTag", "md5Data: " + md5Data);
        return md5Data;
    }


}
