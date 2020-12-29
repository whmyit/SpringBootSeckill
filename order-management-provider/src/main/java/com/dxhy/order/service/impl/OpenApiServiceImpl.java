package com.dxhy.order.service.impl;

import com.dxhy.order.config.OpenApiConfig;
import com.dxhy.order.constant.ConfigureConstant;
import com.dxhy.order.constant.Constant;
import com.dxhy.order.model.email.AccessTokenBean;
import com.dxhy.order.model.message.GlobalInfo;
import com.dxhy.order.model.message.OpenApiResponse;
import com.dxhy.order.service.OpenApiService;
import com.dxhy.order.utils.HttpUtils;
import com.dxhy.order.utils.JsonUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

/**
 * @author ：杨士勇
 * @ClassName ：OpenApiServiceImpl
 * @Description ：
 * @date ：2020年4月16日 上午9:21:01
 */

@Service
@Slf4j
public class OpenApiServiceImpl implements OpenApiService {
    private static final String LOGGER_MSG = "(第三方统一接口服务openApi)";
    
    @Override
    public AccessTokenBean getAccessToken() {
        
        Map<String, String> beanToMap = getTokenRequest();
        
        // 拼接请求路径
        String requestUrl = OpenApiConfig.OPENAPI_TOKEN + ConfigureConstant.TOKEN;
        
        log.debug("{}调用token接口开始,URL:{},请求参数为:{}", LOGGER_MSG, requestUrl, JsonUtils.getInstance().toJsonString(beanToMap));
        String result = HttpUtils.doPost(requestUrl, beanToMap);
        log.debug("{}调用token接口返回参数:{}", LOGGER_MSG, result);
        
        return JsonUtils.getInstance().parseObject(result, AccessTokenBean.class);
    }
    
    
    public static Map<String, String> getTokenRequest() {
        Map<String, String> requestMap = new HashMap<>(4);
        requestMap.put("client_id", OpenApiConfig.TOKEN_CLIENT_ID);
        requestMap.put("client_secret", OpenApiConfig.TOKEN_CLIENT_SECRET);
        requestMap.put("grant_type", OpenApiConfig.TOKEN_GRANT_TYPE);
        requestMap.put("scope", OpenApiConfig.TOKEN_SCOPE);
        return requestMap;
    }
    
    @Override
    public OpenApiResponse sendRequest(GlobalInfo globalInfo, String urlWithOutToken) {
        
        if (StringUtils.isNotBlank(urlWithOutToken) && !urlWithOutToken.contains(Constant.OPENAPIACCESSTOKEN)) {
            urlWithOutToken = urlWithOutToken + Constant.OPENAPIACCESSTOKEN + getAccessToken().getAccess_token();
        }
        String requestParam = JsonUtils.getInstance().toJsonString(globalInfo);
        log.debug("{},请求地址为:{},请求参数为:{}", LOGGER_MSG, urlWithOutToken, requestParam);
        String doPost = HttpUtils.doPost(urlWithOutToken, requestParam);
        log.debug("{}请求地址为:{},返回参数为:{}", LOGGER_MSG, urlWithOutToken, doPost);
        return JsonUtils.getInstance().parseObject(doPost, OpenApiResponse.class);
    }
    
}
