package com.dxhy.order.utils;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.regex.Pattern;

/**
 * 数学共用类
 *
 * @author ZSC-DXHY
 * @date 创建时间: 2018/7/18 20:47
 */
public class MathUtil {
    static final Pattern PATTERN = Pattern.compile("[0-9]*");
    
    public static double add(String value1, String value2) {
        BigDecimal b1 = new BigDecimal(value1);
        BigDecimal b2 = new BigDecimal(value2);
        return b1.add(b2).doubleValue();
    }
    
    public static double sub(String value1, String value2) {
        BigDecimal b1 = new BigDecimal(value1);
        BigDecimal b2 = new BigDecimal(value2);
        return b1.subtract(b2).doubleValue();
    }
    
    public static double mul(String value1, String value2) {
        BigDecimal b1 = new BigDecimal(value1);
        BigDecimal b2 = new BigDecimal(value2);
        return b1.multiply(b2).doubleValue();
    }
    
    public static int mul2(String value1, String value2) {
        BigDecimal b1 = new BigDecimal(value1);
        BigDecimal b2 = new BigDecimal(value2);
        return b1.multiply(b2).intValue();
    }
    
    public static double div(String value1, String value2, int len) {
        BigDecimal b1 = new BigDecimal(value1);
        BigDecimal b2 = new BigDecimal(value2);
        return b1.divide(b2, len, RoundingMode.HALF_UP).doubleValue();
    }
    
    public static boolean isNumeric(String string) {
        return PATTERN.matcher(string).matches();
    }
    
    
    public static double add(BigDecimal value1, BigDecimal value2) {
        return value1.add(value2).setScale(2, RoundingMode.HALF_UP).doubleValue();
    }
    
    public static double sub(BigDecimal value1, BigDecimal value2) {
        return value1.subtract(value2).setScale(2, RoundingMode.HALF_UP).doubleValue();
    }
    
    public static double mul(BigDecimal value1, BigDecimal value2) {
        return value1.multiply(value2).setScale(2, RoundingMode.HALF_UP).doubleValue();
    }
    
    public static double div(BigDecimal value1, BigDecimal value2) {
        return value1.divide(value2).setScale(2, RoundingMode.HALF_UP).doubleValue();
    }
}
