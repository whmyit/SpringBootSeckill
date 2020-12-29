package com.dxhy.order.utils;

import com.alibaba.fastjson.JSON;
import com.dxhy.order.constant.Constant;
import com.dxhy.order.model.Nsrsbh;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 业务bean转换
 *
 * @author ZSC-DXHY
 * @date 创建时间: 2019/7/23 18:10
 */
@Slf4j
public class NsrsbhUtils {
    private final static String LOGGER_MSG = "(税号转换工具类)";
    /**
     * C48纳税人识别号赋值操作
     *
     * @param nsrsbhList
     * @return
     */
    public static Nsrsbh[] getC48Nsrsbhs(List<String> nsrsbhList) {
        if (!nsrsbhList.isEmpty()) {
            Nsrsbh[] nsrsbhs = new Nsrsbh[nsrsbhList.size()];
            for (int i = 0; i < nsrsbhList.size(); i++) {
                Nsrsbh nsrsbh = new Nsrsbh();
                nsrsbh.setNsrsbh(nsrsbhList.get(i));
                nsrsbhs[i] = nsrsbh;
            }
            return nsrsbhs;
        } else {
            Nsrsbh[] nsrsbhs = new Nsrsbh[1];
            Nsrsbh nsrsbh = new Nsrsbh();
            nsrsbh.setNsrsbh("");
            nsrsbhs[0] = nsrsbh;
            return nsrsbhs;
        }
        
    }
    
    /**
     * C48纳税人识别号赋值操作
     *
     * @param nsrsbhList
     * @return
     */
    public static Nsrsbh[] getC48NsrsbhsByArray(String[] nsrsbhList) {
    
        /**
         * 去除税号数组中为空的数据
         */
        nsrsbhList = getNsrsbhList(nsrsbhList);
    
    
        if (nsrsbhList.length > 0) {
            Nsrsbh[] nsrsbhs = new Nsrsbh[nsrsbhList.length];
            for (int i = 0; i < nsrsbhList.length; i++) {
                Nsrsbh nsrsbh = new Nsrsbh();
                nsrsbh.setNsrsbh(nsrsbhList[i]);
                nsrsbhs[i] = nsrsbh;
            }
            return nsrsbhs;
        } else {
            Nsrsbh[] nsrsbhs = new Nsrsbh[0];
    
            return nsrsbhs;
        }
        
    }
    
    public static String[] getNsrsbhList(String[] nsrsbhList) {
        /**
         * 去除税号数组中为空的数据
         */
        if (nsrsbhList.length > 0) {
            List<String> nsrsbhArrayList = Arrays.asList(nsrsbhList);
            nsrsbhArrayList = nsrsbhArrayList.stream().filter(StringUtils::isNotBlank).distinct().collect(Collectors.toList());
            nsrsbhList = nsrsbhArrayList.toArray(new String[nsrsbhArrayList.size()]);
        }
        
        return nsrsbhList;
    }
    
    /**
     * 把单税号转换为一个长度的数组
     *
     * @param nsrsbh
     * @return
     */
    public static List<String> transShListByNsrsbh(String nsrsbh) {
        List<String> shList = new ArrayList<>();
        shList.add(nsrsbh);
        log.debug("{}转换之前数据为:{},转换之后数据为:{}", LOGGER_MSG, nsrsbh, JsonUtils.getInstance().toJsonString(shList));
        return shList;
    }
    
    /**
     * 把数组税号字符串转换为一个的数组
     * 判断入参是否为空,如果为空返回空数组进行全局查询
     * 判断是否包含数组符号,如果包含数组符号就按照数据进行解析
     * 如果不包含数组符号就按照单个税号进行解析
     *
     * @param xhfNsrsbh
     * @return
     */
    public static List<String> transShListByXhfNsrsbh(String xhfNsrsbh) {
        List<String> shList = new ArrayList<>();
        if (StringUtils.isNotBlank(xhfNsrsbh)) {
            if (xhfNsrsbh.contains(Constant.CHARSET_1)) {
                shList = JSON.parseArray(xhfNsrsbh, String.class);
                shList = shList.stream().filter(StringUtils::isNotBlank).distinct().collect(Collectors.toList());
            } else {
                shList.add(xhfNsrsbh);
            }
        
        } else {
            shList = null;
        }
    
        log.debug("{}转换之前数据为:{},转换之后数据为:{}", LOGGER_MSG, xhfNsrsbh, JsonUtils.getInstance().toJsonString(shList));
        return shList;
    }
    
}
