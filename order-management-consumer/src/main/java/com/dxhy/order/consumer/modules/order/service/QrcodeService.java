package com.dxhy.order.consumer.modules.order.service;

import com.dxhy.order.consumer.model.page.PageEwmConfigInfo;
import com.dxhy.order.consumer.model.page.QrcodeOrderInfo;
import com.dxhy.order.consumer.modules.scaninvoice.model.PageQrcodeOrderInfo;
import com.dxhy.order.model.PageUtils;
import com.dxhy.order.model.R;

import java.util.List;
import java.util.Map;

/**
 * 二维码数据层
 *
 * @author ZSC-DXHY
 * @date 创建时间: 2020-08-14 11:54
 */
public interface QrcodeService {
    
    /**
     * 保存二维码信息
     *
     * @param qrcodeOrderInfo
     * @return
     */
    boolean saveQrcodeInfo(QrcodeOrderInfo qrcodeOrderInfo);
    
    /**
     * 二维码列表
     *
     * @param paramMap
     * @param shList
     * @return
     */
    PageUtils queryQrCodeList(Map paramMap, List<String> shList);
    
    /**
     * 二维码详情
     *
     * @param qrcodeId
     * @param xhfNsrsbh
     * @return
     */
    Map<String, Object> queryQrCodeDetail(String qrcodeId, List<String> xhfNsrsbh);
    
    /**
     * 二维码图片
     *
     * @param qrcodeId
     * @param type
     * @param xhfNsrsbh
     * @param backGround
     * @return
     */
    Map<String, Object> queryQrCodeImg(String qrcodeId, String type, List<String> xhfNsrsbh, String backGround);
    
    /**
     * 动态码列表接口
     *
     * @param paramMap
     * @param shList
     * @return
     */
    PageUtils queryDynamicQrcodeList(Map<String, Object> paramMap, List<String> shList);
    
    /**
     * 查询二维码配置信息接口
     *
     * @param xhfNsrsbh
     * @return
     */
    Map<String, Object> queryEwmConfigInfo(Map<String, Object> xhfNsrsbh);
    
    /**
     * 添加二维码配置信息接口
     *
     * @param ewmConfig
     * @return
     */
    boolean addEwmConfigInfo(PageEwmConfigInfo ewmConfig);
    
    /**
     * 更新二维码配置信息接口
     *
     * @param ewmConfig
     * @return
     */
    boolean updateEwmConfigInfo(PageEwmConfigInfo ewmConfig);
    
    /**
     * 查询二维码详细信息
     *
     * @param fpqqlsh
     * @param xhfNsrsbh
     * @return
     */
    Map<String, Object> queryEwmDetailByFpqqlsh(String fpqqlsh, List<String> xhfNsrsbh);
    
    /**
     * 更新二维码信息
     *
     * @param idList
     * @return
     */
    boolean updateEwmDetailInfo(List<Map> idList);
    
    /**
     * 更新静态码数据
     *
     * @param qrcodeOrderInfo
     * @return
     */
    R updateStaticEwmInfo(QrcodeOrderInfo qrcodeOrderInfo);
    
    /**
     * 删除静态码数据
     *
     * @param qrcodeId
     * @return
     */
    R deleteStaticEwmInfo(List<Map> qrcodeId);
    
    /**
     * 生成动态码
     *
     * @param pageQrcodeOrderInfo
     * @return
     */
    R generateDynamicQrCode(PageQrcodeOrderInfo pageQrcodeOrderInfo);
    
    /**
     * 查询动态码信息
     *
     * @param qrcodeId
     * @param type
     * @param shList
     * @param backGround
     * @return
     */
    Map<String, Object> queryQrcodeAndInvoiceDateil(String qrcodeId, String type, List<String> shList, String backGround);
    
    /**
     * 是否存在
     *
     * @param paramMap
     * @param shList
     * @return
     */
    boolean isExistNoAuditOrder(Map<String, Object> paramMap, List<String> shList);
}
