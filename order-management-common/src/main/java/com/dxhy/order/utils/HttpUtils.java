package com.dxhy.order.utils;

import cn.hutool.http.HttpRequest;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * @author ZSC-DXHY
 */
@Slf4j
public class HttpUtils {


    /**
     * 执行post请求
     *
     * @param url
     * @param paramMap
     * @return
     * @throws IOException
     */
    public static String doPost(String url, Map<String, ?> paramMap) {
        Map<String, Object> requestMap = new HashMap<>(paramMap);
        return HttpRequest.post(url).form(requestMap).execute().body();
    }
    
    public static String doPost(String url, String request) {
        return HttpRequest.post(url).body(request).execute().body();
    }
    
    public static String doPostWithHeader(String url, String data, Map<String, String> header) {
        return HttpRequest.post(url).addHeaders(header).body(data).execute().body();
    }
    
    public static String doGetWithHeader(String url, String data, Map<String, String> header) {
        return HttpRequest.get(url).addHeaders(header).body(data).execute().body();
    }
    
    public static String doGet(String url, String request) {
        return HttpRequest.get(url).body(request).execute().body();
    }
    
}
