package com.dxhy.order.consumer.modules.fiscal.service.bwactivexs.impl;

import com.dxhy.order.consumer.config.OpenApiConfig;
import com.dxhy.order.consumer.modules.fiscal.service.bwactivexs.SldManagerServiceBw;
import com.dxhy.order.model.bwactivexs.dy.DydListRequest;
import com.dxhy.order.model.bwactivexs.dy.DydListResponse;
import com.dxhy.order.model.bwactivexs.server.SkServerRequest;
import com.dxhy.order.model.bwactivexs.server.SkServerResponse;
import com.dxhy.order.utils.HttpInvoiceRequestUtil;
import com.dxhy.order.utils.HttpUtils;
import com.dxhy.order.utils.JsonUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Map;
/**
 * 受理点管理百旺业务层
 *
 * @author ZSC-DXHY
 * @date 创建时间: 2020-08-14 15:39
 */
@Service
@Slf4j
public class SldManagerServiceBwImpl implements SldManagerServiceBw {
    
    @Override
    public SkServerResponse queryServerInfo(SkServerRequest request) {
        SkServerResponse response;
        
        // 设置请求头参数
        Map<String, String> header = HttpInvoiceRequestUtil.getRequestHead(request.getTerminalCode());
        log.debug("获取服务器信息接口，url:{} 入参:{}", OpenApiConfig.queryServerByKpdIdAndNsrsbh,
                JsonUtils.getInstance().toJsonString(request));
        String result = HttpUtils.doPostWithHeader(OpenApiConfig.queryServerByKpdIdAndNsrsbh,
                JsonUtils.getInstance().toJsonStringNullToEmpty(request), header);
        log.debug("获取服务器信息接口，出参:{}", result);
        // 返回参数解析成对象
        response = JsonUtils.getInstance().parseObject(result, SkServerResponse.class);
		return response;
	}

	@Override
	public DydListResponse queryDydXxList(DydListRequest request) {
        DydListResponse response;
        // 设置请求头参数
        Map<String, String> header = HttpInvoiceRequestUtil.getRequestHead(request.getTerminalCode());
        log.debug("获取打印点信息接口，url:{} 入参:{}", OpenApiConfig.queryDydxxcxListBw,
                JsonUtils.getInstance().toJsonString(request));
        String result = HttpUtils.doPostWithHeader(OpenApiConfig.queryDydxxcxListBw,
                JsonUtils.getInstance().toJsonStringNullToEmpty(request), header);
        log.debug("获取打印点信息接口，出参:{}", result);
        // 返回参数解析成对象
        response = JsonUtils.getInstance().parseObject(result, DydListResponse.class);
        return response;
	}

}
