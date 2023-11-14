package com.cogent.common.utils;

import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONObject;
import com.cogent.common.exception.ServiceException;
import com.google.common.base.Stopwatch;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import okhttp3.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

/**
 * @Author:wangke
 * @Date:2023/5/8/00810:32
 */
@Slf4j
@Component
public class HttpUtil {

    public static String MEDIAKIT_BASE_URL;
    public static String MEDIAKIT_SECRET;
    public static String HOST_IP;


    private static final OkHttpClient httpClient = new OkHttpClient.Builder()
            .connectTimeout(30, TimeUnit.SECONDS)
            .readTimeout(30, TimeUnit.SECONDS)
            .writeTimeout(30, TimeUnit.SECONDS)
            .callTimeout(60, TimeUnit.SECONDS)
            .build();


    @Value("${mediakit.secret}")
    public void setMediaKitSecret(String mediaKitSecret) {
        MEDIAKIT_SECRET = mediaKitSecret;
    }

    @Value("${mediakit.base_url}")
    public void setMediaKitBaseUrl(String mediaKitBaseUrl) {
        MEDIAKIT_BASE_URL = mediaKitBaseUrl;
    }

    @Value("${HOST_IP}")
    public void setHostIP(String hostIP) {
        HOST_IP = hostIP;
    }



    public static OkHttpClient getHttpClient() {
        return httpClient;
    }



    /**
     * 通用的post请求，没有token，key这种的
     *
     * @param url
     * @param requestBody
     * @return
     */
    public static JSONObject postRequest(String url, JSONObject requestBody) {
        Stopwatch started = Stopwatch.createStarted();
        RequestBody body = RequestBody.create(MediaType.parse("application/json"), requestBody.toString());
        Request request = new Request.Builder().url(url).post(body)
                .addHeader("Content-Type", "application/json")
                .build();

        try (Response response = httpClient.newCall(request).execute()) {
            if (response.code() == 500) {
                log.error("cost:{}ms  request:{}  requestBody:{}", started.elapsed(TimeUnit.MILLISECONDS), request, requestBody);
                return new JSONObject();
            } else {
                assert response.body() != null;
                JSONObject resp = null;
                try {
                    resp = JSONObject.parseObject(response.body().string());
                } catch (IOException e) {
                    throw new ServiceException(url + "此url的请求异常");
                }
                started.stop();
                log.info("cost:{}ms " +
                        "request:{} " +
                        "requestBody:{} " +
                        "response:{} ", started.elapsed(TimeUnit.MILLISECONDS), request, requestBody, resp.toString());
                return resp;
            }
        } catch (IOException e) {
            throw new ServiceException(url + "此url的请求异常");
        }
    }



    /**
     * mediaKit 的所有请求都是get
     *
     * @param url
     * @return
     */
    public static JSONObject getMediaKitRequest(String url, Map<String, String> param) {
        Stopwatch started = Stopwatch.createStarted();
        HttpUrl.Builder urlBuilder = Objects.requireNonNull(HttpUrl.parse(url)).newBuilder();
        // 这个map参数只放除了key之外的参数，比如uuid等等
        param.forEach(urlBuilder::addQueryParameter);
        // 接着放入secret
        urlBuilder.addQueryParameter("secret", HttpUtil.MEDIAKIT_SECRET);
        String urlWithParam = urlBuilder.build().toString();

        Request request = new Request.Builder()
                .get()
                .url(urlWithParam)
                .build();
        JSONObject resp = null;
        try (Response response = httpClient.newCall(request).execute()) {
            resp = JSONObject.parseObject(response.body().string());
            started.stop();
            log.info("cost:{}ms " +
                    "request:{} " +
                    "response:{} ", started.elapsed(TimeUnit.MILLISECONDS), request, resp.toString());
            return resp;
        } catch (IOException e) {
            throw new ServiceException("mediakit 请求异常");
        }
    }

    public static JSONObject getRequest(String url, Map<String, String> param) {
        Stopwatch started = Stopwatch.createStarted();
        HttpUrl.Builder urlBuilder = Objects.requireNonNull(HttpUrl.parse(url)).newBuilder();
        param.forEach(urlBuilder::addQueryParameter);
        String urlWithParam = urlBuilder.build().toString();

        Request request = new Request.Builder()
                .get()
                .url(urlWithParam)
                .build();
        JSONObject resp = null;
        try (Response response = httpClient.newCall(request).execute()) {
            resp = JSONObject.parseObject(response.body().string());
            started.stop();
            log.info("cost:{}ms " +
                    "request:{} " +
                    "response:{} ", started.elapsed(TimeUnit.MILLISECONDS), request, resp.toString());
            return resp;
        } catch (IOException e) {
            throw new ServiceException(url + " 请求异常");
        }
    }
}