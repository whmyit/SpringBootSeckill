package com.dxhy.order.rabbitplugin.util;

import org.springframework.util.Assert;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * 基础工具类
 *
 * @author ZSC-DXHY
 * @date 创建时间: 2020-08-14 15:46
 */
public class Utils {
    
    public static String linkString(String[] strs) {
        Assert.notNull(strs, "method linkString param is null");
        if (strs.length == 1) {
            return strs[0];
        } else {
            StringBuilder sb = new StringBuilder();
            for (String str : strs) {
                sb.append("[").append(str).append("]");
            }
            return sb.toString();
        }
    }
    
    
    public static String toString(Date date) {
        if (date == null) {
            return null;
        }
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        return format.format(date);
    }
    
    public static String toString(StackTraceElement[] stackTrace) {
        StringBuilder buf = new StringBuilder();
        for (StackTraceElement item : stackTrace) {
            buf.append(item.toString());
            buf.append("\n");
        }
        return buf.toString();
    }
    
}
