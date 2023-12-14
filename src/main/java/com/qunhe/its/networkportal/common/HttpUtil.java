package com.qunhe.its.networkportal.common;

import okhttp3.*;

import java.io.IOException;
import java.util.Map;

public class HttpUtil {

    private final OkHttpClient client;

    public HttpUtil() {
        this.client = new OkHttpClient();
    }

    public String doGet(String url, Map<String, String> headers) throws IOException {
        Request.Builder requestBuilder = new Request.Builder().url(url);

        for (Map.Entry<String, String> entry : headers.entrySet()) {
            requestBuilder.addHeader(entry.getKey(), entry.getValue());
        }

        Request request = requestBuilder.build();

        try (Response response = client.newCall(request).execute()) {
            return response.body().string();
        }
    }

    public String doPostJson(String url, String json, Map<String, String> headers) throws IOException {
        MediaType JSON = MediaType.parse("application/json; charset=utf-8");
        RequestBody requestBody = RequestBody.create(json, JSON);

        Request.Builder requestBuilder = new Request.Builder()
                .url(url)
                .post(requestBody);

        for (Map.Entry<String, String> entry : headers.entrySet()) {
            requestBuilder.addHeader(entry.getKey(), entry.getValue());
        }

        Request request = requestBuilder.build();

        try (Response response = client.newCall(request).execute()) {
            return response.body().string();
        }
    }
}
