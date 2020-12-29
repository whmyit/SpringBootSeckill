package com.dxhy.order.service;

import com.dxhy.order.model.email.AccessTokenBean;
import com.dxhy.order.model.message.GlobalInfo;
import com.dxhy.order.model.message.OpenApiResponse;

/**
 * @author ：杨士勇
 * @ClassName ：OpenApiService
 * @Description ：openApi 接口
 * @date ：2020年4月16日 上午9:20:03
 */

public interface OpenApiService {
    
    /**
     * 获取accessToken
     *
     * @return
     */
    AccessTokenBean getAccessToken();
    
    
    /**
     * 调用第三方openapi接口数据,
     * URL传递不需要token,内部数据自动获取token进行添加.
     *
     * @param globalInfo
     * @param urlWithOutToken
     * @return
     */
    OpenApiResponse sendRequest(GlobalInfo globalInfo, String urlWithOutToken);
    
    
}
