package com.dxhy.order.ordermail.util;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.serializer.SerializeConfig;
import com.alibaba.fastjson.serializer.SerializerFeature;

/**
 * @author: chendognzhi
 * @Date: 2017年10月17日
 * @Description: json转换工具
 */
public class JsonUtils {

    private static final SerializeConfig SERIALIZE_CONFIG = new SerializeConfig();

    private JsonUtils() {
    }


    private static JsonUtils helper = new JsonUtils();

    public static JsonUtils getInstance() {
        return helper;
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

}
