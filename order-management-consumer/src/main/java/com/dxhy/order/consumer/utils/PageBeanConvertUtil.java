package com.dxhy.order.consumer.utils;

import cn.hutool.core.date.DateUtil;
import com.alibaba.fastjson.JSON;
import com.dxhy.order.constant.ConfigureConstant;
import com.dxhy.order.constant.OrderInfoEnum;
import com.dxhy.order.consumer.model.page.OrderListQuery;
import org.apache.commons.lang3.StringUtils;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 *
 * @ClassName ：PageBeanConvertUtil
 * @Description ：前端参数转换工具类
 * @author ：杨士勇
 * @date ：2019年7月11日 下午5:54:31
 *
 *
 */

public class PageBeanConvertUtil {
	
    
    
    public static Map<String, Object> convertToMap(OrderListQuery orderListQuery) {
    
        Map<String, Object> resultMap = new HashMap<>(10);
        resultMap.put("pageSize", orderListQuery.getPageSize());
        resultMap.put("currPage", orderListQuery.getCurrPage());
        
        //日期查询处理
        if(StringUtils.isNotBlank(orderListQuery.getStartTime())){
            Date parse = DateUtil.parse(orderListQuery.getStartTime(), "yyyy-MM-dd");
            parse = DateUtil.beginOfDay(parse);
            String startTime = DateUtil.format(parse, "yyyy-MM-dd HH:mm:ss");
            resultMap.put("startTime",startTime);


        }
        
        if(StringUtils.isNotBlank(orderListQuery.getEndTime())){
        	 Date parse = DateUtil.parse(orderListQuery.getEndTime(), "yyyy-MM-dd");
             parse = DateUtil.endOfDay(parse);
             String endTime = DateUtil.format(parse, "yyyy-MM-dd HH:mm:ss");
             resultMap.put("endTime", endTime);
        }
        
        if (StringUtils.isNotBlank(orderListQuery.getChbz())) {
            List<String> chList = JSON.parseArray(orderListQuery.getChbz(), String.class);
            if (chList.size() > 0) {
                resultMap.put("chbz", chList);
            }
        }
        
        if (StringUtils.isNotBlank(orderListQuery.getDdly())) {
            List<String> ddlyList = JSON.parseArray(orderListQuery.getDdly(), String.class);
            
            if (ddlyList.size() > 0) {
                resultMap.put("ddly", ddlyList);
            }
        }
        
        if (StringUtils.isNotBlank(orderListQuery.getDdzt())) {
            List<String> ddztList = JSON.parseArray(orderListQuery.getDdzt(), String.class);
            if(ddztList.size() > 0){
            	resultMap.put("ddzt", ddztList);
            }
           
        }
        
        if (StringUtils.isNotBlank(orderListQuery.getFpzldm())) {
            List<String> fpzldmList = JSON.parseArray(orderListQuery.getFpzldm(), String.class);
            if (fpzldmList.size() > 0) {
                if (fpzldmList.size() == 1) {
                    resultMap.put("fplx", fpzldmList.get(0));
                } else {
                    resultMap.put("fpzldm", fpzldmList);
                }
            }
        }
    
        if (StringUtils.isNotBlank(orderListQuery.getQdbz())) {
            List<String> qdbzList = JSON.parseArray(orderListQuery.getQdbz(), String.class);
            if (qdbzList.size() > 0) {
                resultMap.put("qdbz", qdbzList);
            }
        }
        
        if(StringUtils.isNotBlank(orderListQuery.getCyje())) {
            if (ConfigureConstant.STRING_2.equals(orderListQuery.getCyje())) {
                resultMap.put("diff", true);
            } else if (ConfigureConstant.STRING_1.equals(orderListQuery.getCyje())) {
                resultMap.put("nodiff", true);
            }
    
        }
        
        resultMap.put("ddh", orderListQuery.getDdh());
        resultMap.put("ywlx", orderListQuery.getYwlx());
        resultMap.put("zfbz", orderListQuery.getZfbz());
        resultMap.put("ywlxId", orderListQuery.getYwlx());
        resultMap.put("fphm", orderListQuery.getFphm());
        resultMap.put("fpzh", orderListQuery.getFpzh());
        resultMap.put("fpqh", orderListQuery.getFpqh());
        resultMap.put("fpdm", orderListQuery.getFpdm());
        resultMap.put("gmfmc", orderListQuery.getGhfmc());
        resultMap.put("kplx", orderListQuery.getKplx());
        resultMap.put("mdh", orderListQuery.getMdh());
        resultMap.put("sld", orderListQuery.getSld());
        resultMap.put("xhfmc", orderListQuery.getXhfmc());
        resultMap.put("minhjje", orderListQuery.getMinKphjje());
        resultMap.put("maxhjje", orderListQuery.getMaxKphjje());
        resultMap.put("kpr", orderListQuery.getKpr());
        resultMap.put("ghfNsrsbh", orderListQuery.getGhfNsrsbh());
        resultMap.put("tsbz", orderListQuery.getTsbz());
        resultMap.put("orderStatus", OrderInfoEnum.ORDER_VALID_STATUS_0.getKey());
        resultMap.put("orderBy", orderListQuery.getOrderBy());
        resultMap.put("kpzt", orderListQuery.getKpzt());
        resultMap.put("queryTime",orderListQuery.getQueryTime());
        resultMap.put("checkStatus",orderListQuery.getCheckStatus());
        return resultMap;
        
    }
    
    public static void main(String[] args) {
    	
    	  Date end = DateUtil.parse("2019-12-25","yyyy-MM-dd");
          Date endOfDay = DateUtil.endOfDay(end);
          String format = DateUtil.format(endOfDay,"yyyy-MM-dd HH:mm:ss");
          System.out.print(format);
          
		
	}
	
	

}
