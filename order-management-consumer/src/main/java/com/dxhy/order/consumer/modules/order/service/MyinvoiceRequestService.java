package com.dxhy.order.consumer.modules.order.service;

import com.dxhy.order.consumer.model.myinovice.SynSellerInfoRequest;
import com.dxhy.order.model.R;

import java.util.Map;

/**
 * @author ：杨士勇
 * @ClassName ：MyinvoiceRequestService
 * @Description ：调用我的发票相关接口
 * @date ：2019年11月12日 上午11:19:52
 */

public interface MyinvoiceRequestService {
    
    
    /**
     * 同步销方信息的接口
     *
     * @param sellerInfo
     * @param url
     * @return
     */
    R synSellerInfo(SynSellerInfoRequest sellerInfo, String url);
    
    /**
     * 我的发票获取授权url的接口
     *
     * @param orderNo
     * @param money
     * @param redirectUrl
     * @param timestapm
     * @param appid
     * @return
     */
    Map<String, Object> getAuthUrlFromWxService(String orderNo, String money, String redirectUrl, String timestapm, String appid);
    
    /**
     * 获取id是否授权成功
     *
     * @param orderNo
     * @param appid
     * @return
     */
    Map<String, Object> getAuthStatus(String orderNo, String appid);
    
}
