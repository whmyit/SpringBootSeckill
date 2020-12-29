package com.dxhy.order.consumer;

import com.dxhy.order.constant.ConfigureConstant;
import com.dxhy.order.model.OrderInfo;
import com.dxhy.order.utils.DecimalCalculateUtil;
import com.dxhy.order.utils.JsonUtils;
import org.apache.commons.beanutils.BeanUtilsBean2;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Arrays;

public class SetPropertiesTest {
    
    
    private static final String[] randomStr = new String[]{"a", "b", "c", "d", "e", "f", "g", "h", "i", "j", "k", "l", "m", "n", "o", "p", "q", "r", "s", "t", "u", "v", "w", "x", "y", "z"};
    private static final int[] randomInt = new int[]{0, 1, 2, 3, 4, 5, 6, 7, 8, 9};
    
    
    //Field[] fields = invoiceExcel.getClass().getDeclaredFields();
    
    
    public static Object setProperties(Object obj) throws IllegalAccessException, IllegalArgumentException, InvocationTargetException, NoSuchMethodException, SecurityException {
        
        //Field[] fields = invoiceExcel.getClass().getDeclaredFields();
        Field[] declaredFields = obj.getClass().getDeclaredFields();

        for (Field field : declaredFields) {
            if (field.getClass().isInstance(Arrays.class)) {
                System.out.println("true");
            }
            String getMethodName = "set" + field.getName().substring(0, 1).toUpperCase() + field.getName().substring(1);
            Class<? extends Object> tCls = obj.getClass();
            Method getMethod = tCls.getMethod(getMethodName, String.class);
            getMethod.invoke(obj, nextString());

        }
        return obj;


    }

    public static String nextString() {
        int length = (int) Math.round(Math.random() * 20);
        StringBuilder randStr = new StringBuilder();
        for (int i = 0; i < length; i++) {
            int round = (int) Math.round(Math.random() * 25);
    
            randStr.append(randomStr[round]);

        }

        System.out.println(randStr);
        return randStr.toString();
    }

    public static int nextInt() {
        return (int) Math.round(Math.random() * 10000);
    }
    
    public static void main(String[] args) throws InvocationTargetException, IllegalAccessException {
        int length = (int) Math.round(Math.random() * 20);
        StringBuilder randStr = new StringBuilder();
        for (int i = 0; i < length; i++) {
            int round = (int) Math.round(Math.random() * 25);
    
            randStr.append(randomStr[round]);

        }

        System.out.println(randStr);
    
        double bhsje = DecimalCalculateUtil.div(Double.parseDouble("1443"), (1 + Double.parseDouble("0.16")));
        System.err.println(bhsje);
    
        System.out.println(new BigDecimal("123.1251231231233234342").setScale(2, RoundingMode.HALF_UP));
        System.out.println(DecimalCalculateUtil.round(Double.valueOf("123.1251231231233234342"), 2));
    
        System.out.println(new BigDecimal("1.22").divide(new BigDecimal("1220")).setScale(2, BigDecimal.ROUND_UP).doubleValue());
    
    
        System.out.println(Double.parseDouble("12.000000"));
        System.out.println(Double.valueOf("12.123123123123"));
    
        double xmdj = DecimalCalculateUtil.div(Double.parseDouble("100.00"), Double.parseDouble("10.00"), 8);
        System.out.println(DecimalCalculateUtil.decimalFormat(xmdj, 2));
        System.out.println(new BigDecimal("12.0000").setScale(ConfigureConstant.INT_2, RoundingMode.HALF_UP).toString());
        
        OrderInfo newOrderInfo = new OrderInfo();
        newOrderInfo.setGhfId("343333");
        OrderInfo oldOrderInfo = new OrderInfo();
        oldOrderInfo.setDdlx("123");
        oldOrderInfo.setGhfId("1111111");
//        BeanUtils.copyProperties(newOrderInfo,oldOrderInfo );
//        org.apache.commons.beanutils.BeanUtils.copyProperties(oldOrderInfo,newOrderInfo);
        
        BeanUtilsBean2.getInstance().copyProperties(oldOrderInfo, newOrderInfo);
        System.out.println("oldOrderInfo" + JsonUtils.getInstance().toJsonString(oldOrderInfo));
        System.out.println("newOrderInfo" + JsonUtils.getInstance().toJsonString(newOrderInfo));
        
    }


}
