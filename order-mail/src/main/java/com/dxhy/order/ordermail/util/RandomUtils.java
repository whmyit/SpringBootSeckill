package com.dxhy.order.ordermail.util;

import org.springframework.util.StringUtils;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.UUID;


/**
 * 随机数、 随机字符串工具类
 *
 * @author Acmen
 */
public class RandomUtils {

    public static final String LETTER = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ";

    public static final String NUMBERIC = "0123456789";

    public static final String NUMBERIC_EXCEPT_ZERO = "123456789";

    public static final String ALPHABETIC = "0123456789abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ";

    public static final String ALPHABETIC2 = "0123456789abcdefghijklmnopqrstuvwxyz";

    /**
     * 返回一个定长字符串(包含字母和数字)
     *
     * @param length 随机字符串长
     * @return 随机字符串
     */
    public static String generateMixString(int length) {
        StringBuilder sb = new StringBuilder();
        Random random = new Random();
        for (int i = 0; i < length; i++) {
            sb.append(ALPHABETIC2.charAt(random.nextInt(ALPHABETIC2.length() - 1)));
        }
        return sb.toString();
    }

    /**
     * 返回纯字母字符串(只包含字母)
     *
     * @param length 随机字符串长
     * @return 随机字符串
     */
    public static String generatePureString(int length) {
        StringBuilder sb = new StringBuilder();
        Random random = new Random();
        for (int i = 0; i < length; i++) {
            sb.append(random.nextInt(LETTER.length()));
        }
        return sb.toString();
    }

    /**
     * 返回纯大写字母字符串
     *
     * @param length 随机字符串长
     * @return 随机字符串
     */
    public static String generateUpperString(int length) {
        return generatePureString(length).toUpperCase();
    }

    /**
     * 返回纯小写字母字符串
     *
     * @param length 随机字符串长
     * @return 随机字符串
     */
    public static String generateLowerString(int length) {
        return generatePureString(length).toLowerCase();
    }

    /**
     * 返回数字字符串
     *
     * @param length 随机字符串长
     * @return 随机字符串
     */
    public static String generateNumString(int length) {
        StringBuilder sb = new StringBuilder();
        Random random = new Random();
        for (int i = 0; i < length; i++) {
            sb.append(random.nextInt(NUMBERIC.length()));
        }
        return sb.toString();
    }

    /**
     * 返回单个数字
     *
     * @return 随机字符串
     */
    public static int generateNumber() {
        Random random = new Random();
        return random.nextInt(NUMBERIC.length());
    }

    public static int generatePosition() {
        Random random = new Random();
        return random.nextInt(NUMBERIC_EXCEPT_ZERO.length());
    }

    /**
     * 范围  1--n1
     */
    public static String random(int number1) {
        return random(1, number1);
    }

    /**
     * 范围  n1--n2
     */
    public static String random(int number1, int number2) {
        return String.valueOf((int) (Math.random() * number2 + number1));
    }
    
    /**
     * emial A 账号 P 密码 集合字符串
     * ;;是账号密码整体  $$ 是分开账号密码
     * @param emailAPStr
     * @return
     * TODO
     * MathUtil.java
     * author wangruwei
     * 2019年12月16日
     */
    public static Map<String,String> randomEmail(String emailAPStr){
    	if(!StringUtils.isEmpty(emailAPStr)){
    		String[] split = emailAPStr.split(";;");
    		
    		Random r = new Random();
			int randomInt = r.nextInt(split.length);
			
			String ApStr = split[randomInt];
			
			if(!StringUtils.isEmpty(ApStr)){
				String[] ApSz = ApStr.split("~~");
				Map<String,String> map = new HashMap<String,String>();
				map.put("fromEmail", ApSz[0]);
				map.put("password", ApSz[1]);
				return map;
			}else{
				return null;
			}
    	}
    	else{
    		return null;
    	}
    }
    
    
    
    public static void main(String[] args) {
		System.out.print("4826af10caa24cea894f4e23c4a185b0001".length());
		System.out.print(UUID.randomUUID().toString().replaceAll("-", "").length());
		
		String emailAPStr = "";
		String[] split = emailAPStr.split(";;");
		System.out.println(split[0]);
		System.out.println(split.length);
		Map<String,Object> map = null;
		
		
		String a = "43320318@qq.com~~oqtgtywshvpqbhed";
		
		System.out.println(a.split("~~")[0]+"/"+a.split("~~")[1]);
		
		Map<String, String> randomEmail = randomEmail("43320318@qq.com$$oqtgtywshvpqbhed");
		System.out.println(randomEmail);
	}
}
