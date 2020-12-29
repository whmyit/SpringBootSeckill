package com.dxhy.order.api;

import com.dxhy.order.model.CommonOrderInvoiceAndOrderMxInfo;
import com.dxhy.order.model.PageUtils;
import com.dxhy.order.model.SpecialInvoiceReversalItem;
import com.dxhy.order.model.entity.CommonSpecialInvoice;
import com.dxhy.order.model.entity.SpecialInvoiceReversalDownloadEntity;
import com.dxhy.order.model.entity.SpecialInvoiceReversalEntity;

import java.util.List;
import java.util.Map;

/**
 * 专票红字申请单业务处理
 *
 * @author ZSC-DXHY
 */
public interface ApiSpecialInvoiceReversalService {
    
    /**
     * 查询红字申请单列表
     *
     * @param params
     * @return
     */
    PageUtils querySpecialInvoiceReversals(Map<String, Object> params);
    
    /**
     * 处理红字申请单
     *
     * @param isEdit
     * @param commonSpecialInvoice
     * @return
     */
    Boolean processCommonSpecialInvoice(boolean isEdit, CommonSpecialInvoice commonSpecialInvoice);
    
    /**
     * 删除红字申请单
     *
     * @param id
     * @return
     */
    Boolean deleteSpecialInvoice(String id);
    
    /**
     * 查询红字申请单
     *
     * @param id
     * @return
     */
    SpecialInvoiceReversalEntity querySpecialInvoiceReversal(String id);
    
    /**
     * 查询红字申请单明细
     *
     * @param specialInvoiceReversalId
     * @return
     */
    List<SpecialInvoiceReversalItem> querySpecialInvoiceReversalItems(String specialInvoiceReversalId);
    
    /**
     * 批量查询红字申请单
     *
     * @param ids
     * @param excludeSubmitStatuses
     * @return
     */
    List<SpecialInvoiceReversalEntity> querySpecialInvoiceReversalsByIds(String[] ids, String[] excludeSubmitStatuses);
    
    /**
     * 批量查询红字申请单明细
     *
     * @param specialInvoiceReversalIds
     * @return
     */
    List<SpecialInvoiceReversalItem> querySpecialInvoiceReversalItemsBySirIds(String[] specialInvoiceReversalIds);
    
    /**
     * 查询红字申请单数据
     *
     * @return
     */
    List<String> querySpecialInvoiceReversalTaxpayerCodes();
    
    /**
     * 更新红字申请单
     *
     * @param specialInvoiceReversal
     * @return
     */
    int updateSpecialInvoiceReversal(SpecialInvoiceReversalEntity specialInvoiceReversal);
    
    /**
     * 根据申请单号查询红字申请单
     *
     * @param sqdh
     * @return
     */
    SpecialInvoiceReversalEntity selectSpecialInvoiceReversalBySqdqqlsh(String sqdh);
    
    /**
     * 根据信息表编号查询红字申请单
     *
     * @param submitCode
     * @return
     */
    SpecialInvoiceReversalEntity selectSpecialInvoiceReversalBySubmitCode(String submitCode);
    
    /**
     * 插入红字申请单
     *
     * @param specialInvoiceReversal
     * @return
     */
    int insertSpecialInvoiceReversal(SpecialInvoiceReversalEntity specialInvoiceReversal);
    
    /**
     * 插入红字申请单明细
     *
     * @param specialInvoiceReversalItem
     * @return
     */
    int insertSpecialInvoiceReversalItem(SpecialInvoiceReversalItem specialInvoiceReversalItem);
    
    /**
     * 根据发票代码号码查询红字申请单
     *
     * @param invoiceCode
     * @param invoiceNo
     * @return
     */
    CommonSpecialInvoice selectSpecialInvoiceReversalsAndItems(String invoiceCode, String invoiceNo);
    
    /**
     * 查询红字申请单总数量
     *
     * @param params
     * @return
     */
    Map<String, Object> querySpecialInvoiceReversalsCount(Map<String, Object> params);
    
    
    /**
     * 查询待上传的红票申请单数据
     *
     * @param sqbscqqlsh
     * @param nsrsbh
     * @return
     */
    SpecialInvoiceReversalEntity selectDscSpecialInvoiceReversalsBySqbscqqlsh(String sqbscqqlsh, String nsrsbh);
    
    
    /**
     * 方格接口   红票申请单上传   修改数据状态
     *
     * @param specialInvoiceReversalEntity
     */
    void getUploadRedInvoiceStatus(SpecialInvoiceReversalEntity specialInvoiceReversalEntity);
    
    /**
     * 方格接口   红票申请单下载   获取数据
     *
     * @param sqbxzqqpch
     * @param nsrsbh
     * @return
     */
    List<SpecialInvoiceReversalEntity> selectSpecialInvoiceReversalsBySqbxzqqpch(String sqbxzqqpch, String nsrsbh);
    
    /**
     * 方格接口   红票申请单下载  修改数据状态
     *
     * @param nsrsbh
     * @param sqbxzqqpch
     * @param sjzt
     */
    void updateDownloadRedInvoiceStatus(String nsrsbh, String sqbxzqqpch, String sjzt);
    
    /**
     * 红票申请单上传更新为待上传的数据
     *
     * @param specialInvoiceReversal
     */
    void update(SpecialInvoiceReversalEntity specialInvoiceReversal);
    
    
    /**
     * 根据sqbxzqqpch查询红字申请单下载记录
     *
     * @param sqbxzqqpch
     * @return
     */
    boolean getCountSpecialInvoiceReversalDownload(String sqbxzqqpch);
    
    /**
     * 保存红字申请单下载
     *
     * @param specialInvoiceReversalDownload
     * @return
     */
    boolean saveSpecialInvoiceReversalDownload(SpecialInvoiceReversalDownloadEntity specialInvoiceReversalDownload);
    
    /**
     * 根据sqbxzqqpch查询红字申请单下载
     *
     * @param sqbxzqqpch
     * @return
     */
    SpecialInvoiceReversalDownloadEntity getSpecialInvoiceReversalDownload(String sqbxzqqpch);
    
    /**
     * 处理蓝字发票数据
     *
     * @param yfpDm
     * @param yfpHm
     * @param shList
     * @return
     */
    CommonOrderInvoiceAndOrderMxInfo mergeBuleInvoiceInfo(String yfpDm, String yfpHm, List<String> shList);
}
