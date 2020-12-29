package com.dxhy.order.consumer.modules.itaxmsg.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.dxhy.order.constant.ConfigureConstant;
import com.dxhy.order.constant.OrderManagementConstant;
import com.dxhy.order.consumer.config.OpenApiConfig;
import com.dxhy.order.consumer.modules.itaxmsg.service.IItaxMsgService;
import com.dxhy.order.utils.HttpUtils;
import com.dxhy.order.utils.JsonUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestMethod;

import java.util.HashMap;
import java.util.Map;

/**
 * 发送大B业务层
 *
 * @author ZSC-DXHY
 * @date 创建时间: 2020-08-14 15:28
 */
@Slf4j
@Service
public class ItaxMsgServiceImpl implements IItaxMsgService {
    
    
    /**
     * @param infoTitle   消息主题 ：余票预警
     * @param messageInfo 消息主题
     * @param messType    消息类型 1.审批消息2.预警消息3业务提醒4通知公告
     * @param user        用户id
     * @Description i-Tax发送消息
     * @Author xieyuanq iang
     * @Date 16:56 2018-11-23
     */
    @Override
    public Boolean sessMessageToTax(String infoTitle, String messageInfo, String messType, String user, String deptId) {
        log.info("i-Tax发送消息 参数{}");
        Map<String, Object> map = new HashMap(10);
        map.put("infoTitle", infoTitle);
        map.put("info", messageInfo);
        map.put("infoType", messType);
        map.put("deptId", deptId);
        String[] users = new String[]{user};
        map.put("userIds", users);
        String data = JSONObject.toJSONString(map);
        log.info("i-Tax发送消息 参数{}", data);
        String respResult = null;
        try {
            log.info("请求的URL:{},参数：{}", OpenApiConfig.systemMessagePush, map);
            //http请求头添加
            Map<String,String> requestHead = new HashMap<>(5);
            requestHead.put("Content-Type","application/json");
            respResult = HttpUtils.doPostWithHeader(OpenApiConfig.systemMessagePush, data,requestHead);
            log.info("Rest返回：{}", respResult);
            
            if (null != respResult && !respResult.isEmpty()) {
                Map map1 = JsonUtils.getInstance().parseObject(respResult, Map.class);
                String code = map1.get(OrderManagementConstant.CODE).toString();
                if (ConfigureConstant.STRING_0000.equals(code)) {
                    return true;
                } else {
                    printInfoLog("i-Tax系统-返回失败", OpenApiConfig.systemMessagePush, RequestMethod.POST.toString(), map, respResult);
                }
            } else {
                printInfoLog("i-Tax系统-返回为空", OpenApiConfig.systemMessagePush, RequestMethod.POST.toString(), map, "");
            }
        } catch (Exception e) {
            log.error("i-Tax系统-调用异常，请求地址：" + OpenApiConfig.systemMessagePush + "，请求方式：" + RequestMethod.POST.toString() +
                    "，请求报文 " + JsonUtils.getInstance().toJsonString(map) + "，返回报文：" + respResult, e);
        }
        return false;
    }
    
    private void printInfoLog(String desc, String url, String method, Map<String, ?> reqParams, String respParams) {
        log.info(desc + "，请求地址：" + url + "，请求方式：" + method + "，请求报文 " + JsonUtils.getInstance().toJsonString(reqParams) + "，返回报文：" + respParams);
    }
    
}
