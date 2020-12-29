package com.dxhy.order.api;

import com.dxhy.order.model.*;

import java.util.List;
import java.util.Map;

/**
 * 静态码数据业务处理层
 *
 * @author ZSC-DXHY
 * @date 创建时间: 2020-08-14 10:34
 */
public interface ApiQuickCodeInfoService {
    
    /**
     * 保存静态二维码信息
     *
     * @param qrcodeInfo
     * @param itemList
     * @param extList
     * @return
     */
    boolean saveQrcodeInfo(QuickResponseCodeInfo qrcodeInfo, List<QuickResponseCodeItemInfo> itemList,
                           List<InvoiceTypeCodeExt> extList);
    
    /**
     * 查询静态二维码列表
     *
     * @param map
     * @param shList
     * @return
     */
    PageUtils queryQrCodeList(Map map, List<String> shList);
    
    /**
     * 查询二维码详情
     *
     * @param qrCodeId
     * @param xhfNsrsbh
     * @return
     */
    QuickResponseCodeInfo queryQrCodeDetail(String qrCodeId, List<String> xhfNsrsbh);
    
    /**
     * 查询二维码明细信息
     *
     * @param qrCodeId
     * @param xhfNsrsbh
     * @return
     */
    List<QuickResponseCodeItemInfo> queryQrCodeItemListByQrcodeId(String qrCodeId, List<String> xhfNsrsbh);
    
    /**
     * 查询发票种类信息
     *
     * @param qrCodeId
     * @param xhfNsrsbh
     * @return
     */
    List<InvoiceTypeCodeExt> queryInvoiceTypeByQrcodeId(String qrCodeId, List<String> xhfNsrsbh);
    
    /**
     * 查询二维码信息
     *
     * @param tqm
     * @param shList
     * @param qrCodeType
     * @return
     */
    QuickResponseCodeInfo queryQrCodeDetailByTqm(String tqm, List<String> shList, String qrCodeType);
    
    /**
     * 查询二维码配置信息
     *
     * @param paramMap
     * @return
     */
    EwmConfigInfo queryEwmConfigInfo(Map<String, Object> paramMap);
    
    /**
     * 新增二维码配置
     *
     * @param ewmConfig
     * @param ewmConfigItemList
     * @return
     */
    boolean addEwmConfigInfo(EwmConfigInfo ewmConfig, List<EwmConfigItemInfo> ewmConfigItemList);
    
    /**
     * 更新二维码配置
     *
     * @param ewmConfig
     * @param ewmConfigItemList
     * @return
     */
    boolean updateEwmConfigInfo(EwmConfigInfo ewmConfig, List<EwmConfigItemInfo> ewmConfigItemList);
    
    /**
     * 查询二维码配置明细
     *
     * @param id
     * @return
     */
    List<EwmConfigItemInfo> queryEwmConfigItemInfoById(String id);
    
    /**
     * 更新二维码详情
     *
     * @param quickInfo
     * @param shList
     * @return
     */
    boolean updateEwmDetailInfo(QuickResponseCodeInfo quickInfo, List<String> shList);
    
    /**
     * 更新购方信息
     *
     * @param pageToFpkjInfo
     * @param shList
     * @return
     */
    boolean updateGhfInfo(CommonOrderInfo pageToFpkjInfo, List<String> shList);
    
    /**
     * 查询公众号配置
     *
     * @param xhfNsrsbh
     * @return
     */
    EwmGzhConfig queryGzhEwmConfig(String xhfNsrsbh);
    
    /**
     * 更新静态码信息
     *
     * @param qrCodeInfo
     * @param itemList
     * @param extList
     * @return
     */
    R updateStaticEwmInfo(QuickResponseCodeInfo qrCodeInfo, List<QuickResponseCodeItemInfo> itemList, List<InvoiceTypeCodeExt> extList);
}
