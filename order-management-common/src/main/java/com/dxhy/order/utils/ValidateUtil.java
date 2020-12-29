package com.dxhy.order.utils;


import com.dxhy.order.constant.Constant;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

import java.io.UnsupportedEncodingException;
import java.math.BigDecimal;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 数据校验和格式化共用类
 *
 * @author ZSC-DXHY
 * @date 创建时间: 2018/7/18 20:48
 */
@Slf4j
public class ValidateUtil {
    
    private static final Pattern NUMBER_PATTERN = Pattern.compile("[0-9]+");
    
    
    private ValidateUtil() {
    }
    
    
    /**
     * 获取字符串长度
     *
     * @param source
     * @return
     */
    public static int getStrBytesLength(String source) {
        int length = 0;
        if (StringUtils.isBlank(source)) {
            return length;
        }
        try {
            length = source.getBytes("GBK").length;
        } catch (UnsupportedEncodingException e) {
        
        }
        return length;
    }
    
    public static boolean isAcronym(String word) {
        for (int i = 0; i < word.length(); i++) {
            char c = word.charAt(i);
            if (Character.isLowerCase(c)) {
                return false;
            }
        }
        return true;
    }
    
    public static int checkNumberic(String n) {
        int len = -1;
        if (n.lastIndexOf(Constant.STRING_POINT) == 0) {
            return len;
        }
        BigDecimal bd = new BigDecimal(n);
        String s = bd.toPlainString();
        int index = s.lastIndexOf(Constant.STRING_POINT);
        if (index > -1) {
            len = s.substring(index + 1).length();
        }
        return len;
    }
    
    public static boolean isNumeric(String str) {
        Matcher isNum = NUMBER_PATTERN.matcher(str);
        
        return isNum.matches();
    }
    
    
}
