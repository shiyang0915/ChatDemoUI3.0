package com.hyphenate.fluter.robot_utils;

import android.util.Log;


import com.alibaba.fastjson.JSON;

import java.io.IOException;
import java.util.Map;
import java.util.Set;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class OkHttpRequestUtils {

    private static OkHttpClient okHttpClient = new OkHttpClient();

    public static void sendGetRequest(String url) {
        Request request = new Request.Builder().url(url).get().build();
        final Call call = okHttpClient.newCall(request);
        call.enqueue(new Callback() {

            @Override
            public void onFailure(Call call, IOException e) {

            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {

            }
        });
    }


    public static void sendPostRequest(String url, Map<String, String> requestMap,
                                       final RequestCommand.RequestListener listener, final Class clazz) {
        FormBody.Builder builder = new FormBody.Builder();
        Set<String> keySet = requestMap.keySet();
        for (String key : keySet) {
            builder.add(key, requestMap.get(key));
        }
        final Request request = new Request.Builder()
                .url(url)
                .post(builder.build())
                .build();
        Call call = okHttpClient.newCall(request);
        call.enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                listener.failure(e.getMessage());
                Log.i("shiyangTest", "onFailure: " + e.getMessage());
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String data = null;
                data = response.body().string();
                if (clazz == null) {
                    listener.onResponse(data);
                } else {
                    listener.onResponse(JSON.parseObject(data, clazz));
                }
//                {"code":1,"msg":"\u6210\u529f","data":[]}
                Log.i("shiyangTest", data);
                System.out.println(data);
            }
        });

    }


}
