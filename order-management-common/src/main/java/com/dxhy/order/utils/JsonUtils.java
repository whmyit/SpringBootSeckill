package com.dxhy.order.utils;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.serializer.SerializeConfig;
import com.alibaba.fastjson.serializer.SerializerFeature;
import com.alibaba.fastjson.serializer.SimpleDateFormatSerializer;
import com.alibaba.fastjson.util.TypeUtils;

import java.sql.Timestamp;
import java.util.Date;
import java.util.List;

/**
 * @author: chendognzhi
 * @Date: 2017年10月17日
 * @Description: json转换工具
 */
public class JsonUtils {
    private JsonUtils() {
    }
    
    private static final SerializeConfig SERIALIZE_CONFIG = new SerializeConfig();
    
    static {
        SERIALIZE_CONFIG.put(Date.class, new SimpleDateFormatSerializer("yyyy-MM-dd HH:mm:ss"));
        SERIALIZE_CONFIG.put(Timestamp.class, new SimpleDateFormatSerializer("yyyy-MM-dd HH:mm:ss"));
        /**
         * 设置json格式化第一个字母大写
         */
        TypeUtils.compatibleWithJavaBean = true;
    }
    
    private static final JsonUtils HELPER = new JsonUtils();
    
    public static JsonUtils getInstance() {
        return HELPER;
    }
    
    /**
     * parseObject(json转换为java bean)
     */
    public <T> T parseObject(String text, Class<T> clazz) {
        return JSON.parseObject(text, clazz);
    }
    
    /**
     * toJsonString(对象转换为json字符串)
     */
    public <T> String toJsonString(T obj) {
        return toJsonString(obj, SERIALIZE_CONFIG);
    }
    
    /**
     * toJsonString(对象转换为自定义格式的json字符串)
     */
    public <T> String toJsonString(T obj, SerializeConfig serializeConfig) {
        return JSON.toJSONString(obj, serializeConfig, SerializerFeature.DisableCircularReferenceDetect);
    }
    
    /**
     * 把json串转为指定的对象
     */
    public <T> T fromJson(String str, Class<T> clazz) {
        return JSON.parseObject(str, clazz);
    }
    
    /**
     * 把对象转换 为json字符串 null的字符串转换为""
     */
    
    public <T> String toJsonStringNullToEmpty(T obj) {
        return JSON.toJSONString(obj, SerializerFeature.WriteNullStringAsEmpty);
    }
    
    /**
     * json字符串转list
     */
    public <T> T jsonToList(String text, Class<T> clazz) {
        List<T> aList4 = JSON.parseArray(text, clazz);
        return (T) aList4;
    }
    
}
