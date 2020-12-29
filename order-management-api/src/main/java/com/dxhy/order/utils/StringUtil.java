package com.dxhy.order.utils;

import com.dxhy.order.constant.ConfigureConstant;
import com.dxhy.order.constant.OrderInfoContentEnum;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.DateFormatUtils;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.text.DecimalFormat;
import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author ：杨士勇
 * @ClassName ：StringUtils
 * @Description ：字符串工具类
 * @date ：2018年10月23日 下午4:38:27
 */
public class StringUtil {
    
    private static final DecimalFormat DECIMAL_FORMAT = new DecimalFormat("0000000");

    private static final Pattern CHAR_PATTERN = Pattern.compile("[\u4e00-\u9fa5]");

    public static String replaceBrackets(String param){
        if (StringUtils.isNotBlank(param)) {
            // 括号处理
            param = param.replace("（", "(");
            param = param.replace("）", ")");
        }
        return param;
    }
    /**
     * 默认替换\r\n\t
     * fankunfeng
     * @param param
     * @return
     */
    public static String replaceStr(String param) {
        return replaceStr(param, true);
    }

    /**
     * 对param进行特殊处理
     * 1.替换 中文（ 为 英文(
     * 2.替换特殊字符为空格
     * 3. \r \t \n 替换为空格（备注除外）
     * fankunfeng
     */
    public static String replaceStr(String param, boolean flag) {

        if (StringUtils.isNotBlank(param)) {
            // 括号处理
//            param = param.replace("（", "(");
//            param = param.replace("）", ")");
            // \t \r \n处理
            if (flag) {
                param = param.replace("\r", "");
                param = param.replace("\t", "");
                param = param.replace("\n", "");
            }
            param = GBKUtil.replaceX(param);
        }
        return param;
    }
    
    /**
     * 根据字节长度截取字符串
     *
     * @param orignal
     * @param start
     * @param count
     * @return
     */
    public static String substringByte(String orignal, int start, int count) {
        
        //如果目标字符串为空，则直接返回，不进入截取逻辑；
        if (orignal == null || "".equals(orignal)) {
            return orignal;
        }
        
        //截取Byte长度必须>0
        if (count <= 0) {
            return orignal;
        }

        //截取的起始字节数必须比
        if (start < 0) {
            start = 0;
        }

        //目标char Pull buff缓存区间；
        StringBuffer buff = new StringBuffer();
        try {
            //截取字节起始字节位置大于目标String的Byte的length则返回空值
            if (start >= getStringByteLenths(orignal)) {
                return null;
            }
            // int[] arrlen=getByteLenArrays(orignal);
            int len = 0;
            char c;

            //遍历String的每一个Char字符，计算当前总长度
            //如果到当前Char的的字节长度大于要截取的字符总长度，则跳出循环返回截取的字符串。
            for (int i = 0; i < orignal.toCharArray().length; i++) {
                c = orignal.charAt(i);
                //当起始位置为0时候
                if (start == 0) {
                    len += String.valueOf(c).getBytes("gbk").length;
                    if (len <= count) {
                        buff.append(c);
                    } else {
                        break;
                    }
                } else {
                    //截取字符串从非0位置开始
                    len += String.valueOf(c).getBytes("gbk").length;
                    if (len >= start && len <= start + count) {
                        buff.append(c);
                    }
                    if (len > start + count) {
                        break;
                    }
                }
            }
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        //返回最终截取的字符结果;
        //创建String对象，传入目标char Buff对象
        return new String(buff);
    }

    /**
     * 根据gbk获取byte数组获取String字符串的长度
     *
     * @param args
     * @return
     * @throws UnsupportedEncodingException
     */
    public static int getStringByteLenths(String args) throws UnsupportedEncodingException {
        return StringUtils.isNotBlank(args) ? args.getBytes("gbk").length : 0;
    }
    
    public static String subStringByByte(String str, int len) throws IOException {
        byte[] buf = str.getBytes("gbk");
        int count = 0;
        for (int x = len - 1; x >= 0; x--) {
            if (buf[x] < 0) {
                count++;
            } else {
                break;
            }
        }
        if (count % ConfigureConstant.INT_2 == 0) {
            return new String(buf, 0, len, "gbk");
        } else {
            return new String(buf, 0, len - 1, "gbk");
        }
    }

    /**
     * 保留小数点后有效位(最多8位，最少两位），如果小数点后位数不够两位补零，例如1.00，1.20
     *
     * @param args
     * @return String
     * @author: 陈玉航
     * @date: Created on 2019年1月7日 下午7:56:40
     */
    public static String jeFormat(String args) {
        DecimalFormat df = new DecimalFormat("0.00######");
        if (StringUtils.isBlank(args)) {
            return args;
        }
        String num = df.format(Double.parseDouble(args));
        return num;
    }

    public static String slFormat(String args) {
        DecimalFormat df1 = new DecimalFormat("0.########");
        if (StringUtils.isBlank(args)) {
            return args;
        }
        String num = df1.format(Double.parseDouble(args));
        return num;
    }

    /**
     *
     * <p>
     * 字符串首字母大写
     * </p>
     *
     * @param str
     * @return String
     * @author: 赵睿
     * @date: Created on 2016年4月18日 下午2:21:10
     */
    public static String upperFirst(String str) {
        char[] chars = str.toCharArray();
        if (Character.isLowerCase(chars[0])) {
            chars[0] -= 32;
        }
        return new String(chars);
    }

    /**
     * <p>
     * 根据split切割，取tag之前的元素
     * </p>
     * 前置条件，需要校验该输入是否存在tag
     *
     * @param input 输入
     * @param tag   标签
     * @param split 分割符
     * @return String 响应信息
     * @author 赵睿 2016年8月2日上午8:57:58
     */
    public static String getBefore(String input, String tag, String split) {
        String[] strs = input.split(split);
        int i;
        for (i = 0; i < strs.length; i++) {
            if (strs[i].equalsIgnoreCase(tag)) {
                break;
            }
        }
        input = strs[i - 1];
        return input;
    }
    
    /***
     *
     * <p>
     * 通过StringBuilder拼接字符串
     * </p>
     *
     * @param args
     * @return String
     * @author 赵睿
     * @date Created on 2016年5月30日 上午10:15:11
     */
    public static String stringConcatenation(String... args) {

        StringBuilder builder = new StringBuilder();
        for (String string : args) {
            builder.append(string);
        }

        return builder.toString();
    }

    /***
     * 匹配是否需要补全商品简码
     * ture为需要补全
     * false为不需要补全
     * @param spmc
     * @return
     */
    public static boolean checkStr(String spmc, String spjc) {
    
        /**
         * 1.判断商品是否以星号开头
         * 2.判断商品名称中是否包含2个以上星号
         * 3.截取前两个星号中间的简称
         * 4.判断简称为汉字
         * 5.判断简称超过4个字节.简称最小为2个汉字
         * 6.都符合条件返回为true,已经包含商品简称,不需要补全
         */
        //默认不需要补填
        boolean bl = true;
        if (spmc.indexOf(ConfigureConstant.STRING_STAR) == 0) {
            String[] split = spmc.split("\\*");
            if (split.length > ConfigureConstant.INT_2) {
                String jm = split[1];
            
            
                //判断简码是否为汉字
                if (checkName(jm)) {
                    try {
                        if (jm.getBytes(ConfigureConstant.STRING_CHARSET_GBK).length >= ConfigureConstant.INT_2) {
                            /**
                             * 判断简码和商品简称是否一致,如果一致不需要补全简称,如果不一致需要补全简称
                             */
                            if (StringUtils.isNotBlank(spjc) && !jm.equals(spjc)) {
        
                            } else {
                                bl = false;
                            }
    
                        }
                    } catch (UnsupportedEncodingException e) {

                    }
                }
            }
        }
        return bl;
    }

    /**
     * 校验是否为汉字,
     * true为汉字
     * false为非汉字
     *
     * @param name
     * @return
     */
    public static boolean checkName(String name) {
        Matcher m = CHAR_PATTERN.matcher(name);
        return m.find();
    }

    /**
     * @param @param  spbm
     * @param @return
     * @return String
     * @throws
     * @Title : fillZero
     * @Description ：末位补零
     */
    public static String fillZero(String spbm,int length) {
        StringBuilder sb = new StringBuilder();
        sb.append(spbm);
        for(int i = spbm.length(); i<length; i++){
            sb.append("0");
        }
        return sb.toString();
    }

    /**
     * 税率格式化
     *
     * @param sl
     * @return
     */
    public static String formatSl(String sl) {
        DecimalFormat slFormat = new DecimalFormat("######0.00#");
        String resultSl = "";
        if (StringUtils.isBlank(sl)) {
        
        } else {
            if (sl.contains(ConfigureConstant.STRING_PERCENT)) {
                resultSl = slFormat.format(Double.parseDouble(sl.replace(ConfigureConstant.STRING_PERCENT, "")) / 100);
            } else if (ConfigureConstant.STRING_BZS.equals(sl) || ConfigureConstant.STRING_MS.equals(sl) || ConfigureConstant.STRING_0.equals(sl)) {
                resultSl = "0.00";
            } else {
                resultSl = slFormat.format(Double.parseDouble(sl));
            }
        }
        
        return resultSl;
    }
    
    public static String getBz(String bz) {
        //校验长度
        int strLength = 0;
        try {
            strLength = bz.getBytes(ConfigureConstant.STRING_CHARSET_GBK).length;
        } catch (UnsupportedEncodingException e) {
            // TODO 后期考虑异常情况
        }
        
        if (strLength > ConfigureConstant.INT_150) {
            bz = StringUtil.substringByte(bz, 0, ConfigureConstant.INT_150);
            
        }
        if (bz.endsWith(ConfigureConstant.STRING_SEMICOLON)) {
            bz = bz.substring(0, bz.length() - 1);
        }
        return bz;
    }
    
    public static String getMergeDdh(String type) {
        Date date = new Date();
        String str = DateFormatUtils.format(date, ConfigureConstant.DATE_FORMAT_DATE_YMD);
        String ddh = type + str + DECIMAL_FORMAT.format(Math.random() * 1000000);
        if (StringUtils.isNotBlank(ddh) && ddh.length() > OrderInfoContentEnum.STRING_FPKJ_DDH.getMaxLength()) {
            ddh = ddh.substring(ConfigureConstant.INT_0, OrderInfoContentEnum.STRING_FPKJ_DDH.getMaxLength());
        }
        return ddh;
    
    }
    
}
