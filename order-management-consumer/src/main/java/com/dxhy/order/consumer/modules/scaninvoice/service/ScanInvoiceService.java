package com.dxhy.order.consumer.modules.scaninvoice.service;

import com.dxhy.order.consumer.modules.scaninvoice.model.PageQrcodeOrderInfo;
import com.dxhy.order.model.R;

import java.util.List;

/**
 * @author ：杨士勇
 * @ClassName ：ScanInvoiceService
 * @Description ：
 * @date ：2020年4月14日 上午9:28:12
 */

public interface ScanInvoiceService {
    
    /**
     * 根据提取码获取订单数据
     *
     * @param tqm
     * @param shList
     * @param type
     * @param openId
     * @return
     */
    R queryOrderInfoByTqmAndNsrsbh(String tqm, List<String> shList, String type, String openId);
    
    /**
     * 更新订单信息
     *
     * @param orderInfoList
     * @return
     */
    R updateOrderInfo(PageQrcodeOrderInfo orderInfoList);
    
    /**
     * 授权信息查看
     *
     * @param succOrderId
     * @return
     */
    R authOrderInvoice(String succOrderId);
    
    /**
     * 更新订单
     *
     * @param failOrderId
     * @return
     */
    R updateFaildOrder(String failOrderId);
    
    /**
     * 获取二维码公众号信息
     *
     * @param tqm
     * @param nsrsbh
     * @param type
     * @return
     */
    R getEwmGzhConfig(String tqm, String nsrsbh, String type);
    
    /**
     * 获取鉴权信息
     *
     * @param pageQrcodeOrderInfo
     * @return
     */
    R getAuthUrlAndUpdateOrderInfo(PageQrcodeOrderInfo pageQrcodeOrderInfo);
    
    /**
     * 查询订单信息
     *
     * @param tqm
     * @param shList
     * @return
     */
    R queryOrderInfoByTqm(String tqm, List<String> shList);
    
    /**
     * 订单合并
     *
     * @param lshList
     * @param shList
     * @return
     */
    R getMergeOrderAuthUrl(List<String> lshList, List<String> shList);
}
