package com.gowsow.shiba.util;

import android.util.Log;

import java.net.ConnectException;
import java.net.UnknownServiceException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class NetworkUtils {
    private static final String TAG = NetworkUtils.class.getSimpleName();

    public enum OkHttpClientEnum {
        TIMEOUT5(5L),
        TIMEOUT10(10L),
        TIMEOUT15(15L),
        TIMEOUT25(25L);

        public Long getCallTimeout() {
            return callTimeout;
        }

        private Long callTimeout;

        private OkHttpClientEnum(Long callTimeout) {
            this.callTimeout = callTimeout;
        }
    }

    public static Response postJson(String json, String url, OkHttpClientEnum okHttpClientEnum) throws Exception {
        return postJson(json, url, new HashMap<String, String>(), okHttpClientEnum);
    }

    private static Response postJson(String json, String url, Map<String, String> headers,
                                     OkHttpClientEnum okHttpClientEnum) throws ConnectException {
        RequestBody body = RequestBody.create(MediaType.parse("application/json"), json);
        Request.Builder builder = new Request.Builder().url(url).post(body);
        if (headers != null) {
            for (String headerName : headers.keySet()) {
                builder.addHeader(headerName, headers.get(headerName));
            }
        }
        OkHttpClient okHttpClient = null;
        try {
            okHttpClient = new OkHttpClient().newBuilder().connectTimeout(okHttpClientEnum.getCallTimeout(), TimeUnit.SECONDS).build();
            Response response = okHttpClient.newCall(builder.build()).execute();
            return response;
        } catch (Exception e) {
            Log.e(TAG, "postJson error: " + Log.getStackTraceString(e));
            throw new ConnectException("connect to " + url + " error");
        } finally {
            if (okHttpClient != null) {
                okHttpClient.connectionPool().evictAll();
            }
        }
    }


}
