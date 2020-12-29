package com.dxhy.order.utils;

import lombok.extern.slf4j.Slf4j;

import java.io.*;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

/**
 * @author: wangyang
 * @date: 2020/5/8 11:25
 * @description
 */
@Slf4j
public class PropertiesUtil {

    public static Properties props;
    static{
        loadProps();
    }

    synchronized static private void loadProps(){
        log.info("开始加载properties文件内容.......");
        props = new Properties();
        InputStream in = null;
        BufferedReader bf = null;
        try {
            //通过类加载器进行获取properties文件流
            in = PropertiesUtil.class.getClassLoader().getResourceAsStream("config/mailSend.properties");
            //解决读取到的中文乱码问题
            bf = new BufferedReader(new InputStreamReader(in));
            props.load(bf);
        } catch (FileNotFoundException e) {
            log.error("mailSend.properties文件未找到",e);
        } catch (IOException e) {
            log.error("出现IOException",e);
        } finally {
            try {
                if(null != in) {
                    in.close();
                }

                if(null != bf){
                    bf.close();
                }
            } catch (IOException e) {
                log.error("mailSend.properties文件流关闭出现异常",e);
            }
        }
        log.info("加载properties文件内容完成...........");
    }

    public static String getProperty(String key){
        if(null == props) {
            loadProps();
        }
        return props.getProperty(key);
    }

    public static String getProperty(String key, String defaultValue) {
        if(null == props) {
            loadProps();
        }
        return props.getProperty(key, defaultValue);
    }

    public static Map<String,String> properties2Map(){
        Map<String, String> map = new HashMap<>(5);

        Set<Map.Entry<Object,Object>> entrySet = props.entrySet();
        for (Map.Entry<Object, Object> entry : entrySet) {
            map.put((String) entry.getKey(), (String) entry.getValue());
        }
        return map;
    }
}
