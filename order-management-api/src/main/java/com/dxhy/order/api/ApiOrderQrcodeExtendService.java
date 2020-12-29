package com.dxhy.order.api;

import com.dxhy.order.model.CommonOrderInfo;
import com.dxhy.order.model.OrderQrcodeExtendInfo;
import com.dxhy.order.model.PageUtils;

import java.util.List;
import java.util.Map;

/**
 * @author ：杨士勇
 * @ClassName ：ApiOrderQrcodeExtendService
 * @Description ：
 * @date ：2020年4月10日 下午1:49:55
 */
public interface ApiOrderQrcodeExtendService {
    
    /**
     * 根据订单请求流水号 和 销方税号 查询二维码信息
     *
     * @param ddqqlsh
     * @param shList
     * @return
     */
    OrderQrcodeExtendInfo queryQrCodeDetailByDdqqlshAndNsrsbh(String ddqqlsh, List<String> shList);
    
    /**
     * 保存二维码信息
     *
     * @param commonOrderToQrCodeInfo
     * @return
     */
    boolean saveQrcodeInfo(OrderQrcodeExtendInfo commonOrderToQrCodeInfo);
    
    /**
     * 查询二维码列表
     *
     * @param paramMap
     * @param shList
     * @return
     */
    PageUtils queryDynamicQrCodeList(Map<String, Object> paramMap, List<String> shList);
    
    /**
     * 查询二维码详情
     *
     * @param fpqqlsh
     * @param xhfNsrsbh
     * @return
     */
    Map<String, Object> queryEwmDetailByFpqqlsh(String fpqqlsh, List<String> xhfNsrsbh);
    
    /**
     * 更新二维码信息
     *
     * @param quickInfo
     * @param shList
     * @param pageToFpkjInfo
     * @return
     */
    boolean updateEwmDetailInfo(OrderQrcodeExtendInfo quickInfo, List<String> shList, CommonOrderInfo pageToFpkjInfo);
    
    /**
     * 更新二维码状态
     *
     * @param idList
     * @return
     */
    boolean updateEwmDetailInfoByIds(List<Map> idList);
    
    /**
     * 根据动态码查询二维码信息
     *
     * @param tqm
     * @param shList
     * @param type
     * @return
     */
    OrderQrcodeExtendInfo queryQrCodeDetailByTqm(String tqm, List<String> shList, String type);
    
    /**
     * 根据授权id查询二维码信息
     *
     * @param succOrderId
     * @param shList
     * @return
     */
    OrderQrcodeExtendInfo queryQrCodeDetailByAuthOrderId(String succOrderId, List<String> shList);
    
    /**
     * 根据id查询二维码信息
     *
     * @param qrcodeId
     * @param xhfNsrsbh
     * @return
     */
    OrderQrcodeExtendInfo queryQrcodeDetailById(String qrcodeId, List<String> xhfNsrsbh);
    
    /**
     * 根据二维码查询二维码和发票信息
     *
     * @param qrcodeId
     * @param shList
     * @return
     */
    Map<String, Object> queryQrcodeAndInvoiceDetail(String qrcodeId, List<String> shList);
    
    /**
     * 查询是否存在未审核订单
     *
     * @param paramMap
     * @param shList
     * @return
     */
    boolean isExistNoAuditOrder(Map<String, Object> paramMap, List<String> shList);
    
    /**
     * 查询所有开票异常的数据
     *
     * @param paramMap
     * @param shList
     * @return
     */
    List<OrderQrcodeExtendInfo> selectOrderQrcodeExtendInfoForTask(Map<String, Object> paramMap, List<String> shList);
}
