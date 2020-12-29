package com.dxhy.order.utils;

import com.dxhy.order.constant.Constant;
import lombok.extern.slf4j.Slf4j;

import java.math.BigDecimal;

/**
 * 字符串处理
 *
 * @author ZSC-DXHY
 * @date 创建时间: 2020-08-14 15:42
 */
@Slf4j
public class StringConvertUtils {
    
    
    /**
     * @description 科学计数法转换成字符串金额
     * @author fangyibai
     * @date 2019/6/29
     * @return java.lang.String
     */
    public static String convertScientificNotation(String value){
        String res = null;
        try {
            BigDecimal bigDecimal = new BigDecimal(value);
            res = bigDecimal.toPlainString();
        } catch (Exception e) {
            log.error("{},科学计数法转换成字符串金额错误,{}",value,e.getMessage(),e);
        }
        return res;
    }
    
    /**
     * @param @param  str
     * @param @return
     * @return String
     * @throws
     * @Title : removeLastZero
     * @Description ： 去掉小数后面没有意义的0 如果未整数后边补两个0
     */
    public static String removeLastZero(String str) {
        int defaultLength = 2;
        if (!str.contains(Constant.STRING_POINT)) {
            StringBuilder sb = new StringBuilder();
            sb.append(str).append(".").append("00");
            return sb.toString();
        }
    
        str = str.replaceAll("0+?$", "");
        int length = str.substring(str.indexOf('.') + 1).length();
    
        if (length < defaultLength) {
            StringBuilder sb = new StringBuilder();
            sb.append(str);
            for (int i = length; i < defaultLength; i++) {
                sb.append("0");
            }
            str = sb.toString();
        }
        return str.replaceAll("[.]$", "");
    }
    
}
