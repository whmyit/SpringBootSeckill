package com.dxhy.common.generatepdf.util;


import com.dxhy.common.generatepdf.exception.CustomException;

import java.util.ArrayList;
import java.util.List;
/**
 * 字符串工具类
 *
 * @author ZSC-DXHY
 * @date 创建时间: 2020-08-14 18:20
 */
public class StringUtil {
    public static String[] substringToArry(String text, int length)
            throws CustomException {
        if (text == null) {
            return null;
        }
        List strList = new ArrayList();
        try {
            StringBuilder sb = new StringBuilder();
            double currentLength = 0.0D;
            double totalLength = 0.0D;
            for (char c : text.toCharArray()) {
                totalLength += 1.0D;
                currentLength += getLength(c);
                if (currentLength <= length) {
                    sb.append(c);
                    if ((currentLength == getLength(text)) || (totalLength == text.length())) {
                        strList.add(sb.toString());
                    }
                } else {
                    strList.add(sb.toString());
                    if (currentLength == length) {
                        currentLength = 0.0D;
                    } else {
                        sb = new StringBuilder();
                        sb.append(c);
                        if ((currentLength == getLength(text)) || (totalLength == text.length())) {
                            strList.add(sb.toString());
                        }
                        currentLength = getLength(c);
                    }
                }
            }
        } catch (Exception e) {
            throw new CustomException(1004, new StringBuilder().append("截取字符串失败：").append(e.getMessage()).append("(").append(text).append(")").toString(), e);
        }
        String[] strs = new String[strList.size()];
        return (String[]) strList.toArray(strs);
    }

    public static double getLength(String s) {
        double i = 0.0D;
        for (char c : s.toCharArray()) {
            i += getLength(c);
        }
        return i;
    }

    public static double getLength(char c) {
        double i = 0.0D;
        String match = "";
        if (String.valueOf(c).matches(match)) {
            if (String.valueOf(c).matches("^[0-9]+$")) {
                i += 1.0D;
            } else {
                i += 1.11D;
            }
        } else {
            i += 2.0D;
        }
        return i;
    }

    /**
     * length为GBK长度
     *
     * @param str
     * @param length
     * @return
     * @throws CustomException
     */
    public static String substring(String str, int length) throws CustomException {
        if (null == str) {
            return "";
        }
        StringBuilder sb = new StringBuilder();
        try {
            if (str.getBytes("GBK").length <= length) {
                return str;
            }
            double currentLength = 0.0D;
            char[] cs = str.toCharArray();
            int i = 0;
            while ((currentLength += String.valueOf(cs[i]).getBytes("GBK").length) <= length) {
                sb.append(cs[(i++)]);
            }
        } catch (Exception e) {
            throw new CustomException(1148, "截取字符串失败：" + e.getMessage() + "(" + str + ")", e);
        }
        return sb.toString();
    }

    public static String addZero(String str, int strLength) {
        int strLen = null == str ? 0 : str.length();
        if ((strLen > 0) && (strLen < strLength)) {
            while (strLen < strLength) {
                StringBuilder sb = new StringBuilder();
                sb.append("0").append(str);
                str = sb.toString();
                strLen = str.length();
            }
        }
        return str;
    }

    public static boolean isEmpty(String src) {
        return src == null || src.isEmpty();
    }
}
