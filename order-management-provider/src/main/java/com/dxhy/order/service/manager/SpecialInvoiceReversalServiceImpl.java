package com.dxhy.order.service.manager;

import com.dxhy.order.api.*;
import com.dxhy.order.constant.*;
import com.dxhy.order.dao.SpecialInvoiceReversalDao;
import com.dxhy.order.dao.SpecialInvoiceReversalDownloadDao;
import com.dxhy.order.dao.SpecialInvoiceReversalItemMapper;
import com.dxhy.order.model.*;
import com.dxhy.order.model.entity.CommonSpecialInvoice;
import com.dxhy.order.model.entity.SpecialInvoiceReversalDownloadEntity;
import com.dxhy.order.model.entity.SpecialInvoiceReversalEntity;
import com.dxhy.order.utils.JsonUtils;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import lombok.extern.slf4j.Slf4j;
import com.dxhy.order.utils.PriceTaxSeparationUtil;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.dubbo.config.annotation.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * 红字申请单业务处理层
 *
 * @author ZSC-DXHY
 * @date 创建时间: 2020-08-14 15:40
 */
@Slf4j
@Service
public class SpecialInvoiceReversalServiceImpl implements ApiSpecialInvoiceReversalService {
    
    @Resource
    private SpecialInvoiceReversalDao specialInvoiceReversalDao;
    
    @Resource
    private SpecialInvoiceReversalItemMapper specialInvoiceReversalItemMapper;
    
    @Resource
    private SpecialInvoiceReversalDownloadDao specialInvoiceReversalDownloadDao;
    
    @Resource
    private ApiInvoiceCommonService apiInvoiceCommonService;
    
    @Resource
    private ApiFangGeInterfaceService apiFangGeInterfaceService;
    
    @Resource
    private ApiRushRedInvoiceRequestInfoService invoiceReqService;
    
    @Resource
    private ApiOrderInvoiceInfoService apiOrderInvoiceInfoService;
    
    
    /**
     * 分页查询
     *
     * @param params
     * @return
     */
    @Override
    public PageUtils querySpecialInvoiceReversals(Map<String, Object> params) {
        int pageSize = Integer.parseInt((String) params.get("limit"));
        int currPage = Integer.parseInt((String) params.get("page"));
        PageHelper.startPage(currPage, pageSize);
        List<SpecialInvoiceReversalEntity> list = specialInvoiceReversalDao.selectSpecialInvoiceReversals(params);
        PageInfo<SpecialInvoiceReversalEntity> pageInfo = new PageInfo<>(list);
        PageUtils page = new PageUtils(pageInfo.getList(), (int) pageInfo.getTotal(), pageInfo.getPageSize(), pageInfo.getPageNum());
        return page;
    }
    
    
    /**
     * 删除或者 更新红字申请单
     *
     * @param isEdit
     * @param commonSpecialInvoice
     * @return
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public Boolean processCommonSpecialInvoice(boolean isEdit, CommonSpecialInvoice commonSpecialInvoice) {
        Boolean isSuccess = true;
    
        if (isEdit) {
            //红字申请单编辑
            // 删除红字申请单 此处改为逻辑删除
            specialInvoiceReversalItemMapper.deleteSpecialInvoiceReversalItems(commonSpecialInvoice.getSpecialInvoiceReversalEntity().getId());
        
        
            //更新红字申请单信息
            Integer isEditSuccess = specialInvoiceReversalDao.updateByPrimaryKeySelective(commonSpecialInvoice.getSpecialInvoiceReversalEntity());
            if (isEditSuccess <= 0) {
                return false;
            }
        
        } else {
            int addResult = specialInvoiceReversalDao.insertSelective(commonSpecialInvoice.getSpecialInvoiceReversalEntity());
            if (addResult <= 0) {
                return false;
            }
        }
    
        for (SpecialInvoiceReversalItem specialInvoiceReversalItem : commonSpecialInvoice.getSpecialInvoiceReversalItemEntities()) {
            int result = specialInvoiceReversalItemMapper.insertSelective(specialInvoiceReversalItem);
            if (result <= 0) {
                return false;
            }
        }
    
        return isSuccess;
    }
    
    @Override
    public SpecialInvoiceReversalEntity querySpecialInvoiceReversal(String id) {
        
        SpecialInvoiceReversalEntity query = new SpecialInvoiceReversalEntity();
        query.setId(id);
        return specialInvoiceReversalDao.selectSpecialInvoiceReversal(query);
    }
    
    
    @Override
    public List<SpecialInvoiceReversalItem> querySpecialInvoiceReversalItems(String specialInvoiceReversalId) {
        
        return specialInvoiceReversalItemMapper.selectItemListBySpecialInvoiceReversalId(specialInvoiceReversalId);
    }
    
    @Override
    public Boolean deleteSpecialInvoice(String id) {
    
        //物理删除 修改为逻辑删除
        SpecialInvoiceReversalEntity update = new SpecialInvoiceReversalEntity();
        update.setId(id);
        update.setDataStatus("1");
        int i = specialInvoiceReversalDao.updateByPrimaryKeySelective(update);
        return i > 0;
    }
    
    @Override
    public List<SpecialInvoiceReversalEntity> querySpecialInvoiceReversalsByIds(String[] ids, String[] excludeSubmitStatuses) {
        return specialInvoiceReversalDao.selectSpecialInvoiceReversalsByIds(ids, excludeSubmitStatuses);
    }
    
    @Override
    public List<SpecialInvoiceReversalItem> querySpecialInvoiceReversalItemsBySirIds(
            String[] specialInvoiceReversalIds) {
    
        List<SpecialInvoiceReversalItem> itemList = new ArrayList<>();
        for (String id : specialInvoiceReversalIds) {
            final List<SpecialInvoiceReversalItem> specialInvoiceReversalItems = specialInvoiceReversalItemMapper.selectItemListBySpecialInvoiceReversalId(id);
            itemList.addAll(specialInvoiceReversalItems);
        }
        return itemList;
    }
    
    /**
     * 查询红字申请单定时任务数据
     *
     * @return
     */
    @Override
    public List<String> querySpecialInvoiceReversalTaxpayerCodes() {
        return specialInvoiceReversalDao.selectSpecialInvoiceReversalTaxpayerCodes();
    }
    
    
    @Override
    public int updateSpecialInvoiceReversal(SpecialInvoiceReversalEntity specialInvoiceReversal) {
        
        return specialInvoiceReversalDao.updateByPrimaryKeySelective(specialInvoiceReversal);
    }
    
    @Override
    public SpecialInvoiceReversalEntity selectSpecialInvoiceReversalBySqdqqlsh(String sqdqqlsh) {
        
        SpecialInvoiceReversalEntity specialInvoiceReversalEntity = new SpecialInvoiceReversalEntity();
        specialInvoiceReversalEntity.setSqdscqqlsh(sqdqqlsh);
        return specialInvoiceReversalDao.selectSpecialInvoiceReversal(specialInvoiceReversalEntity);
    }
    
    @Override
    public SpecialInvoiceReversalEntity selectSpecialInvoiceReversalBySubmitCode(String xxbbh) {
    
        SpecialInvoiceReversalEntity query = new SpecialInvoiceReversalEntity();
        query.setXxbbh(xxbbh);
        return specialInvoiceReversalDao.selectSpecialInvoiceReversal(query);
    }
    
    @Override
    public int insertSpecialInvoiceReversal(SpecialInvoiceReversalEntity specialInvoiceReversal) {
        return specialInvoiceReversalDao.insertSelective(specialInvoiceReversal);
    }
    
    @Override
    public int insertSpecialInvoiceReversalItem(SpecialInvoiceReversalItem specialInvoiceReversalItem) {
        return specialInvoiceReversalItemMapper.insertSelective(specialInvoiceReversalItem);
    }
    
    /**
     * 查询 对应蓝字发票代码 号码的发票 已审核通过的明细信息
     *
     * @param invoiceCode
     * @param invoiceNo
     * @return
     */
    @Override
    public CommonSpecialInvoice selectSpecialInvoiceReversalsAndItems(String invoiceCode, String invoiceNo) {
        
        
        CommonSpecialInvoice comon = new CommonSpecialInvoice();
        
        SpecialInvoiceReversalEntity qurey = new SpecialInvoiceReversalEntity();
        qurey.setYfpHm(invoiceNo);
        qurey.setYfpDm(invoiceCode);
        qurey.setStatusCode(OrderInfoEnum.SPECIAL_INVOICE_STATUS_TZD0000.getKey());
        List<SpecialInvoiceReversalEntity> specialInvoiceReversalEntities = specialInvoiceReversalDao.selectSpecialInvoiceReversalList(qurey);
        
        List<SpecialInvoiceReversalItem> resultList = new ArrayList<>();
        if (CollectionUtils.isNotEmpty(specialInvoiceReversalEntities)) {
            comon.setSpecialInvoiceReversalEntity(specialInvoiceReversalEntities.get(0));
            for (SpecialInvoiceReversalEntity entity : specialInvoiceReversalEntities) {
                List<SpecialInvoiceReversalItem> specialInvoiceReversalItems = specialInvoiceReversalItemMapper.selectItemListBySpecialInvoiceReversalId(entity.getId());
                if (CollectionUtils.isNotEmpty(specialInvoiceReversalItems)) {
                    resultList.addAll(specialInvoiceReversalItems);
                }
            }
        }
        comon.setSpecialInvoiceReversalItemEntities(resultList);
        
        return comon;
    }
    
    /**
     * 列表数据统计
     *
     * @param params
     * @return
     */
    @Override
    public Map<String, Object> querySpecialInvoiceReversalsCount(Map<String, Object> params) {
        return specialInvoiceReversalDao.selectSpecialInvoiceReversalsCount(params);
        
    }
    
    /**
     * 查询待上传的红票申请单数据
     *
     * @param sqbscqqlsh
     * @param nsrsbh
     * @return
     */
    @Override
    public SpecialInvoiceReversalEntity selectDscSpecialInvoiceReversalsBySqbscqqlsh(String sqbscqqlsh, String nsrsbh) {
        return specialInvoiceReversalDao.selectDscSpecialInvoiceReversalsBySqbscqqlsh(sqbscqqlsh, nsrsbh);
    }
    
    
    /**
     * 方格接口   红票申请单上传   修改数据状态
     *
     * @param specialInvoiceReversalEntity
     */
    @Override
    public void getUploadRedInvoiceStatus(SpecialInvoiceReversalEntity specialInvoiceReversalEntity) {
        specialInvoiceReversalDao.getUploadRedInvoiceStatus(specialInvoiceReversalEntity);
    }
    
    /**
     * 方格接口   红票申请单下载   获取数据
     *
     * @param sqbxzqqpch
     * @param nsrsbh
     * @return
     */
    @Override
    public List<SpecialInvoiceReversalEntity> selectSpecialInvoiceReversalsBySqbxzqqpch(String sqbxzqqpch, String nsrsbh) {
        return specialInvoiceReversalDao.selectSpecialInvoiceReversalsBySqbxzqqpch(sqbxzqqpch, nsrsbh);
    }
    
    /**
     * 方格接口   红票申请单下载  修改数据状态
     *
     * @param nsrsbh
     * @param sqbxzqqpch
     * @param sjzt
     */
    @Override
    public void updateDownloadRedInvoiceStatus(String nsrsbh, String sqbxzqqpch, String sjzt) {
        specialInvoiceReversalDownloadDao.updateDownloadRedInvoiceStatus(nsrsbh, sqbxzqqpch, sjzt);
    
    }
    
    /**
     * 红票申请单上传更新为待上传的数据
     *
     * @param specialInvoiceReversal
     */
    @Override
    public void update(SpecialInvoiceReversalEntity specialInvoiceReversal) {
        specialInvoiceReversalDao.updateByPrimaryKeySelective(specialInvoiceReversal);
    }
    
    @Override
    public boolean getCountSpecialInvoiceReversalDownload(String sqbxzqqpch) {
        int countSpecialInvoiceReversalDownload = specialInvoiceReversalDownloadDao.getCountSpecialInvoiceReversalDownload(sqbxzqqpch);
        return countSpecialInvoiceReversalDownload > 0;
    }
    
    @Override
    public boolean saveSpecialInvoiceReversalDownload(SpecialInvoiceReversalDownloadEntity specialInvoiceReversalDownload) {
        int result = specialInvoiceReversalDownloadDao.insertSpecialInvoiceReversalDownload(specialInvoiceReversalDownload);
        return result > 0;
    }
    
    @Override
    public SpecialInvoiceReversalDownloadEntity getSpecialInvoiceReversalDownload(String sqbxzqqpch) {
        return specialInvoiceReversalDownloadDao.getSpecialInvoiceReversalDownload(sqbxzqqpch);
    }
    
    @Override
    public CommonOrderInvoiceAndOrderMxInfo mergeBuleInvoiceInfo(String yfpDm, String yfpHm, List<String> shList) {
        
        CommonOrderInvoiceAndOrderMxInfo commonOrderInvoiceAndOrderMxInfo = apiOrderInvoiceInfoService.selectOrderInvoiceInfoByFpdmFphmAndNsrsbh(yfpDm, yfpHm, shList);
        if (commonOrderInvoiceAndOrderMxInfo == null) {
            log.error("未查询到原蓝票信息！发票代码：{} ，发票号码：{}", yfpDm, yfpHm);
            return null;
        }
        
        //发票开具状态校验
        String invoiceStatus = commonOrderInvoiceAndOrderMxInfo.getOrderInvoiceInfo().getKpzt();
        if (!OrderInfoEnum.INVOICE_STATUS_2.getKey().equals(invoiceStatus)) {
            log.error("查询发票信息，开票状态不等于成功！发票代码：{} ，发票号码：{}，原蓝票信息：{}", yfpDm, yfpHm, JsonUtils.getInstance().toJsonString(commonOrderInvoiceAndOrderMxInfo));
            return null;
        }
        
        CommonOrderInfo commonOrderInfo = new CommonOrderInfo();
        OrderInfo sourceOrderInfo = commonOrderInvoiceAndOrderMxInfo.getOrderInfo();
        sourceOrderInfo.setHjbhsje("-" + sourceOrderInfo.getHjbhsje());
        sourceOrderInfo.setHjse("-" + sourceOrderInfo.getHjse());
        sourceOrderInfo.setKphjje("-" + sourceOrderInfo.getKphjje());
        sourceOrderInfo.setKplx(OrderInfoEnum.INVOICE_BILLING_TYPE_1.getKey());
        commonOrderInfo.setOrderInfo(sourceOrderInfo);
        List<OrderItemInfo> sourceOrderItems = commonOrderInvoiceAndOrderMxInfo.getOrderItemList();
        for (OrderItemInfo sourceOrderItem : sourceOrderItems) {
            if (OrderInfoEnum.FPHXZ_CODE_1.getKey().equals(sourceOrderItem.getFphxz())) {
                //折扣行
                sourceOrderItem.setXmje(new BigDecimal(sourceOrderItem.getXmje()).negate().toString());
                sourceOrderItem.setSe(new BigDecimal(sourceOrderItem.getSe()).negate().toString());
                if (!StringUtils.isBlank(sourceOrderItem.getXmsl())) {
                    sourceOrderItem.setXmsl(new BigDecimal(sourceOrderItem.getXmsl()).negate().toString());
                }
            } else {
        
                sourceOrderItem.setXmje(new BigDecimal(sourceOrderItem.getXmje()).negate().toString());
                sourceOrderItem.setSe(new BigDecimal(sourceOrderItem.getSe()).negate().toString());
                if (!StringUtils.isBlank(sourceOrderItem.getXmsl())) {
                    sourceOrderItem.setXmsl(new BigDecimal(sourceOrderItem.getXmsl()).negate().toString());
                }
        
                if (StringUtils.isNotBlank(sourceOrderItem.getKce())) {
                    sourceOrderItem.setKce(new BigDecimal(sourceOrderItem.getKce()).negate().toString());
                }
        
            }
        }
        commonOrderInfo.setOrderItemInfo(sourceOrderItems);
        /**
         * 数据需要先进行价税分离才可以使用原来的红票合并折扣行代码进行判断
         */
        TaxSeparateConfig config = new TaxSeparateConfig();
        config.setDealSeType("1");
        config.setSingleSlSeparateType("2");
        try {
            commonOrderInfo = PriceTaxSeparationUtil.taxSeparationService(commonOrderInfo, config);
        } catch (OrderSeparationException e) {
            log.error("价税分离失败失败！请求报文:{}", JsonUtils.getInstance().toJsonString(commonOrderInfo));
            return null;
        }
        Map<String, Object> volidateOrder = invoiceReqService.itemMerge(commonOrderInfo);
        if (OrderInfoContentEnum.SUCCESS.getKey().equals(volidateOrder.get(OrderManagementConstant.ERRORCODE))) {
            commonOrderInfo = (CommonOrderInfo) volidateOrder.get("data");
            commonOrderInvoiceAndOrderMxInfo.setOrderInfo(commonOrderInfo.getOrderInfo());
            commonOrderInvoiceAndOrderMxInfo.setOrderItemList(commonOrderInfo.getOrderItemInfo());
            return commonOrderInvoiceAndOrderMxInfo;
        } else {
            log.error("合并商品折扣行失败！请求报文：" + JsonUtils.getInstance().toJsonString(commonOrderInfo) + "，返回报文：" + JsonUtils.getInstance().toJsonString(volidateOrder));
            return null;
        }
    }
}
