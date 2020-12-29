package com.dxhy.order.consumer.utils;

import com.dxhy.order.constant.ConfigureConstant;
import lombok.extern.slf4j.Slf4j;

import java.io.UnsupportedEncodingException;

/**
 * @Author fankunfeng
 * @Date 2019-03-22 11:00:35
 * @Describe
 */
@Slf4j
public class GbkUtils {
    /**
     * 检验字符串GBK格式长度，如果获取长度异常返回Integer最大长度
     *
     * @param param
     * @return
     */
    public static int getGbkLength(String param) {
        try {
            return param.getBytes(ConfigureConstant.STRING_CHARSET_GBK).length;
        } catch (UnsupportedEncodingException e) {
            log.error("{}获取GBK格式长度异常！", param);
            
        }
        return Integer.MAX_VALUE;
    }
}
