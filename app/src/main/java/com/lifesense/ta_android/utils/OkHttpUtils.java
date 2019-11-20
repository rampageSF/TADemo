package com.lifesense.ta_android.utils;

import android.util.Log;

import com.lifesense.lshybird.api.ApiUtils;
import com.lifesense.lshybird.api.LsRequest;
import com.lifesense.lshybird.api.LsResponse;

import java.io.IOException;
import java.security.cert.X509Certificate;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

/**
 * Created by Sinyi.liu on 2019/9/4.
 */
public class OkHttpUtils {

    private volatile static OkHttpUtils sInstance;

    public static OkHttpUtils getInstance() {
        if (sInstance == null) {
            synchronized (OkHttpUtils.class) {
                if (sInstance == null) {
                    sInstance = new OkHttpUtils();
                }
            }
        }
        return sInstance;
    }

    private OkHttpClient mOkHttpClient;


    private OkHttpUtils() {
        OkHttpClient.Builder builder = new OkHttpClient.Builder();
        ignoreTrustAllCerts(builder);
        mOkHttpClient = builder.build();
    }

    /**
     * 测试需要 抓包调试，先忽略证书，正式环境可去掉
     * @param builder
     */
    private void ignoreTrustAllCerts(OkHttpClient.Builder builder) {
        builder.sslSocketFactory(createSSLSocketFactory())
                .hostnameVerifier(new HostnameVerifier() {
                    @Override
                    public boolean verify(String hostname, SSLSession session) {
                        return true;
                    }
                });
    }


    public void send(LsRequest lsRequest, final ApiUtils.IApiResultCallback iApiResultCallback) {


        Request.Builder okRequestBuilder = null;
        if (lsRequest.getMethod() == LsRequest.HTTP_GET) {
            okRequestBuilder = new Request.Builder().url(lsRequest.getUrl()).get();
        } else if (lsRequest.getMethod() == LsRequest.HTTP_POST) {
            RequestBody requestBody = RequestBody.create(
                    MediaType.parse("application/json; charset=utf-8"), lsRequest.getBody());
            Request.Builder builderRequest = new Request.Builder();
            builderRequest.url(lsRequest.getUrl());
            builderRequest.post(requestBody);
            okRequestBuilder = builderRequest;
        } else {
            //目前就get 和 post
            return;
        }
        Map<String, String> headers = lsRequest.getHeader();
        if (headers != null && !headers.isEmpty()) {
            Set<Map.Entry<String, String>> entries = headers.entrySet();
            for (Map.Entry<String, String> entry : entries) {
                okRequestBuilder.addHeader(entry.getKey(), entry.getValue());
            }
        }
        Log.d("OkHttpUtils", "send: " + lsRequest);
        Request okRequest = okRequestBuilder.build();
        Call call = mOkHttpClient.newCall(okRequest);
        call.enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                if (iApiResultCallback != null) {
                    iApiResultCallback.onApiFail(e);
                }
                Log.d("OkHttpUtils", "onFailure: " + e);

            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String data = response.body().string();
                Log.d("OkHttpUtils", "onResponse: " + response.toString() + " data:" + data);

                if (iApiResultCallback != null) {
                    LsResponse lsResponse = LsResponse.build(response.code(), data
                            , response.headers().toMultimap());
                    iApiResultCallback.onApiSucceed(lsResponse);
                }
            }
        });

    }


    private SSLSocketFactory createSSLSocketFactory() {
        SSLSocketFactory ssfFactory = null;

        try {
            SSLContext sc = SSLContext.getInstance("TLS");
            sc.init(null, new TrustManager[]{new TrustAllCerts()}, null);
            ssfFactory = sc.getSocketFactory();
        } catch (Exception e) {
        }

        return ssfFactory;
    }

    public static class TrustAllCerts implements X509TrustManager {
        @Override
        public void checkClientTrusted(X509Certificate[] chain, String authType) {
        }

        @Override
        public void checkServerTrusted(X509Certificate[] chain, String authType) {
        }

        @Override
        public X509Certificate[] getAcceptedIssuers() {
            return new X509Certificate[0];
        }

    }


}
