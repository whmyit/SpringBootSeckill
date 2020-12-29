package com.dxhy.order.api;

import com.dxhy.order.protocol.CommonRequestParam;

import java.util.Map;

/**
 * 接口相关通用工具类
 *
 * @author ZSC-DXHY
 */
public interface ICommonDisposeService {
    
    /**
     * 数据解密
     *
     * @param param
     * @return
     */
    String commonDecrypt(CommonRequestParam param);
    
    
    /**
     * 数据加密
     *
     * @param param
     * @return
     */
    String commonEncrypt(CommonRequestParam param);
    
    
    /**
     * 根据secretId读取对应的secretKey
     * 或者根据税号读取对应的secretId
     *
     * @param secretId
     * @return
     */
    String getAuthMap(String secretId);
    
    
    /**
     * 根据参数,获取http请求的URL
     *
     * @param nsrsbh
     * @param zipCode
     * @param encryptCode
     * @param content
     * @param url
     * @param interfaceType
     * @return
     */
    Map<String, String> getRequestParameter(String nsrsbh, String zipCode, String encryptCode, String content, String url, String interfaceType);
    
}
