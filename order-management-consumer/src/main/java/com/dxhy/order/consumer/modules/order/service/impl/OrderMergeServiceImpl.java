package com.dxhy.order.consumer.modules.order.service.impl;

import com.dxhy.order.api.ApiInvoiceService;
import com.dxhy.order.api.ApiOrderInfoService;
import com.dxhy.order.api.ApiOrderProcessService;
import com.dxhy.order.api.ApiTaxEquipmentService;
import com.dxhy.order.constant.*;
import com.dxhy.order.consumer.modules.order.service.IGenerateReadyOpenOrderService;
import com.dxhy.order.consumer.modules.order.service.IOrderMergeService;
import com.dxhy.order.consumer.utils.InterfaceResponseUtils;
import com.dxhy.order.model.*;
import com.dxhy.order.utils.NsrsbhUtils;
import com.dxhy.order.utils.OrderMergeUtil;
import com.dxhy.order.utils.PriceTaxSeparationUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.dubbo.config.annotation.Reference;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.io.UnsupportedEncodingException;
import java.math.BigDecimal;
import java.util.*;

/**
 * 订单合并业务实现
 *
 * @author 陈玉航
 * @version 1.0 Created on 2018年8月3日 下午2:47:41
 */
@Service
@Slf4j
public class OrderMergeServiceImpl implements IOrderMergeService {
    private static final String LOGGER_MSG = "(订单合并业务层)";
    
    @Reference
    private ApiOrderInfoService apiOrderInfoService;
    
    @Reference
    private ApiInvoiceService invoiceService;
    
    @Reference
    private ApiOrderProcessService apiOrderProcessService;
    
    @Reference
    private ApiTaxEquipmentService apiTaxEquipmentService;
    
    @Resource
    private IGenerateReadyOpenOrderService iGenerateReadyOpenOrderService;
    
    /**
     * 订单合并
     *
     * @author: 陈玉航
     * @date: Created on 2018年8月3日 下午2:49:35
     */
    @Override
    public R orderMerge(List<Map> idList, String isMergeSameOrderItem) {
        
        try {
            List<CommonOrderInfo> common = apiOrderInfoService.queryOrderInfoByOrderIds(idList);
            // 订单合并信息校验
            if (CollectionUtils.isEmpty(common)) {
                log.error("合并的订单信息查询到的数据为空!");
                return R.error(OrderMergeErrorMessageEnum.ORDER_MERGER_ORDERINFO_NULL_ERROR.getKey(),
                        OrderMergeErrorMessageEnum.ORDER_MERGER_ORDERINFO_NULL_ERROR.getValue());
            }
            R r = checkMergeOrderInfo(common);
            // 校验失败 返回错误信息
            if (!OrderInfoContentEnum.SUCCESS.getKey().equals(r.get(OrderManagementConstant.CODE))) {
                return r;
            }
            OrderMergeConfig config = new OrderMergeConfig();
            config.setIsMergeSameItem(isMergeSameOrderItem);
            
            CommonOrderInfo orderMerge = OrderMergeUtil.orderMerge(common, config);
            
            List<String> shList = NsrsbhUtils.transShListByNsrsbh(common.get(0).getOrderInfo().getXhfNsrsbh());
            //查询原始订单的订单来源
            OrderProcessInfo selectByOrderId = apiOrderProcessService.selectByOrderId(common.get(0).getOrderInfo().getId(), shList);


            //合并完同类明细项的发票重新价税分离
            if(ConfigureConstant.STRING_0.equals(config.getIsMergeSameItem())){

                for(OrderItemInfo orderItem : orderMerge.getOrderItemInfo()){

                    if(OrderInfoEnum.HSBZ_0.getKey().equals(orderItem.getHsbz())){
                        BigDecimal mxjshj = new BigDecimal(orderItem.getXmje()).add(new BigDecimal(orderItem.getSe()));
                        orderItem.setXmje(mxjshj.toPlainString());
                        orderItem.setHsbz(OrderInfoEnum.HSBZ_1.getKey());
                    }

                }
                orderMerge = PriceTaxSeparationUtil.taxSeparationService(orderMerge,new TaxSeparateConfig());
            }

            //合并后的订单保存
            orderMerge.getOrderInfo().setDdlx(OrderInfoEnum.ORDER_TYPE_2.getKey());
            orderMerge.getOrderInfo().setId("");
            orderMerge.getOrderInfo().setProcessId("");
            orderMerge.getOrderInfo().setFpqqlsh("");
            
           
            OrderProcessInfo orderProcessInfo = new OrderProcessInfo();
            orderProcessInfo.setDdzt(OrderInfoEnum.ORDER_STATUS_2.getKey());
            orderProcessInfo.setDdlx(OrderInfoEnum.ORDER_TYPE_2.getKey());
            orderProcessInfo.setDdly(selectByOrderId.getDdly());
            orderProcessInfo.setDdcjsj(new Date());
            orderMerge.setProcessInfo(orderProcessInfo);
            boolean b = iGenerateReadyOpenOrderService.saveOrderMergeInfo(orderMerge);
            if (b) {
    			return R.ok();
            } else {
                log.error("订单合并后保存失败!");
                return R.error("订单合并后保存失败!");
            }
            
		} catch (OrderMergeException e) {
            log.error("订单合并异常，异常信息是:{}",e);
			return R.error(e.getCode(),e.getMessage());
		} catch (OrderSeparationException e){
            log.error("订单合并异常，异常信息是:{}",e);
            return R.error(e.getCode(),e.getMessage());
        }
    }
    
    /**
     * 订单合并数据校验
     *
     * @author: 陈玉航
     * @date: Created on 2018年8月3日 下午3:07:22
     */
    @Override
    public R orderMergeCheck(String[] grov, List<String> shList) {
        log.info("{} 订单合并数据校验开始", LOGGER_MSG);
        if (grov == null || grov.length == 0) {
            log.error("{} 待处理数据为空", LOGGER_MSG);
            return InterfaceResponseUtils.buildReturnInfo(OrderInfoContentEnum.GENERATE_READY_ORDER_DATA_ERROR, null);
        }
        log.info("{} 根据订单号批量查询订单信息", LOGGER_MSG);
        //根据订单号批量查询订单信息
        List<CommonOrderInfo> orderInfo = apiOrderInfoService.batchQueryOrderInfoByOrderIds(Arrays.asList(grov), shList);
        
        if (orderInfo == null || orderInfo.size() == 0 || orderInfo.get(0).getOrderInfo() == null || orderInfo.get(0).getOrderItemInfo() == null) {
            log.info("{} 没有查到需要处理的订单", LOGGER_MSG);
            return InterfaceResponseUtils.buildReturnInfo(OrderInfoContentEnum.MERG_ORDER_NULL_ERROR, null);
        }
        /**
        * 判断订单来源是否为扫码开票
        */
        R r = checkSweepCodeMakeOutOnInvoice(orderInfo);
        if (!ConfigureConstant.STRING_0000.equals(r.get(OrderManagementConstant.CODE))) {
            return r;
        }

        /**
         * 综合判断数据是否符合条件,
         */
        r = checkMergeOrderInfo(orderInfo);
        if (!ConfigureConstant.STRING_0000.equals(r.get(OrderManagementConstant.CODE))) {
            return r;
        }
        //判断购方信息是否一致
        return checkgfxx(orderInfo);
    }
    /**
    * 判断合并订单是否为扫码开票
    */
    private R checkSweepCodeMakeOutOnInvoice(List<CommonOrderInfo>  orderInfo){
        boolean isQrCodeMakeOutOnInvoice = false;
        for (CommonOrderInfo info : orderInfo) {
            if (OrderInfoEnum.ORDER_SOURCE_5.getKey().equals(info.getProcessInfo().getDdly()) || OrderInfoEnum.ORDER_SOURCE_6.getKey().equals(info.getProcessInfo().getDdly())) {
                isQrCodeMakeOutOnInvoice = true;
                break;
            }
        }
        if (isQrCodeMakeOutOnInvoice) {
            //订单来源为扫码开票
            return R.error("扫码开票的订单不能合并");
        }
        return R.ok();
    }
    
    private List<R> hbts(List<CommonOrderInfo> orderInfo) {
        List<R> list = new ArrayList<>();
        //业务类型
        List<String> ywlxList = new ArrayList<>();
        //门店编号
        List<String> mdbhList = new ArrayList<>();
        StringBuilder sb = new StringBuilder();
        for (CommonOrderInfo commonOrderInfo : orderInfo) {
    
            OrderInfo oi = commonOrderInfo.getOrderInfo();
            if (!StringUtils.isBlank(oi.getYwlx())) {
                ywlxList.add(oi.getYwlx());
            }
            if (!StringUtils.isBlank(oi.getMdh())) {
                mdbhList.add(oi.getMdh());
            }
            sb.append(oi.getBz());
        }
        //门店编号
        if (isNeedTs(mdbhList)) {
            log.info("{} 门店编号不同，返回前端提示是否合并", LOGGER_MSG);
            R hbts = R.error(OrderInfoContentEnum.MERG_ORDER_MDBH_ERROR);
            list.add(hbts);
        }
        //业务类型
        if (isNeedTs(ywlxList)) {
            log.info("{} 业务类型不同，返回前端提示是否合并", LOGGER_MSG);
            R hbts = R.error(OrderInfoContentEnum.MERG_ORDER_YWLX_ERROR);
            list.add(hbts);
        }
        
        //校验长度
        int strLength = 0;
        try {
            strLength = sb.toString().getBytes(ConfigureConstant.STRING_CHARSET_GBK).length;
        } catch (UnsupportedEncodingException e) {
            // TODO 后期考虑异常情况
        }
        
        if (strLength > ConfigureConstant.INT_150) {
            R hbts = R.error(OrderInfoContentEnum.ORDER_MERGE_BZ_OVERLENGTH);
            list.add(hbts);
        }
        
        
        //是否存在同类项明细
        log.info("{} 是否存在同类项明细合并", LOGGER_MSG);
        Map<String, String> map = new HashMap<>(10);
        List<String> mxList = new ArrayList<>();
        for (OrderItemInfo oi : getXmmx(orderInfo)) {
            String key = null;
            if (Double.parseDouble(oi.getXmje()) < 0) {
                continue;
            } else {
                key = oi.getXmmc() + oi.getGgxh() + oi.getSl() + oi.getXmdj();
            }
        
            if (map.containsKey(key)) {
            
                if (!ConfigureConstant.STRING_2.equals(map.get(key))) {
                    mxList.add(oi.getXmmc() + ":" + (oi.getGgxh() == null ? "" : oi.getGgxh()) + "," + oi.getSl() + "," + (oi.getXmdj() == null ? "" : oi.getXmdj()));
                }
                map.put(key, "2");
            } else {
                map.put(key, ConfigureConstant.STRING_1);
            }
        }
        
        if (mxList.size() > 0) {
            log.info("{} 存在同类项明细合并", LOGGER_MSG);
            StringBuilder str = new StringBuilder();
            for (int i = 0; i < mxList.size(); i++) {
                if (i == (mxList.size() - 1)) {
                    str.append(mxList.get(i));
                } else {
                    str.append(mxList.get(i)).append("、");
                }
            }
            
            log.info("{} 存在的同类项明细：{}", LOGGER_MSG, str);
            R hbts = R.error(OrderInfoContentEnum.MERG_ORDER_TLMXXTS_ERROR.getKey(), str.toString());
            list.add(hbts);
        } else {
            log.info("{} 不存在同类项明细合并", LOGGER_MSG);
        }
        return list;
    }
    
    /**
     * 获取项目明细
     *
     * @param grov
     * @return List<OrderItemInfo>
     * @author: 陈玉航
     * @date: Created on 2018年8月6日 上午11:23:02
     */
    private List<OrderItemInfo> getXmmx(List<CommonOrderInfo> grov) {
        List<OrderItemInfo> orderInfo = new ArrayList<>();
        for (CommonOrderInfo commonOrderInfo : grov) {
            List<OrderItemInfo> oi = commonOrderInfo.getOrderItemInfo();
            orderInfo.addAll(oi);
        }
        return orderInfo;
    }
    
    private boolean isNeedTs(List<String> list) {
        boolean flag = false;
        if (list == null || list.size() == 0) {
            flag = false;
        } else {
            String str = list.get(0);
            for (String string : list) {
                if (!str.equals(string)) {
                    flag = true;
                    break;
                }
            }
        }
        return flag;
    }
    
    /**
     * 判断购方信息是否一致
     *
     * @param orderInfo
     * @return R
     * @author: 陈玉航
     * @date: Created on 2018年8月3日 下午4:18:24
     */
    private R checkgfxx(List<CommonOrderInfo> orderInfo) {
        //是否全空
        boolean allNull = true;
        //是否全不为空
        boolean allNotNull = true;
        for (CommonOrderInfo commonOrderInfo : orderInfo) {
            OrderInfo oi = commonOrderInfo.getOrderInfo();
            List<String> shList = new ArrayList<>();
            shList.add(oi.getXhfNsrsbh());
            //购方信息是否全空，判断依据：购方信息全为空则为空，有一项不为空则视为不为空
            //购买方企业类型不参与校验
            //经产品（王旭）确认暂时这样处理
            OrderProcessInfo selectByPrimaryKey = apiOrderProcessService.selectOrderProcessInfoByProcessId(commonOrderInfo.getOrderInfo().getProcessId(), shList);
            if (OrderInfoEnum.ORDER_VALID_STATUS_1.getKey().equals(selectByPrimaryKey.getOrderStatus())) {
                return R.error(OrderInfoContentEnum.ORDER_MERGE_ORDER_STATUS_ERROR);
            }
            if (isgfxxnull(oi)) {
                allNotNull = false;
            } else {
                allNull = false;
            }
        }
    
        if (allNull) {
            //购方信息全为空，允许合并
            List<R> hbts = hbts(orderInfo);
            if (hbts.size() > 0) {
                log.info("{} 购方信息全为空，需要返回前端提示", LOGGER_MSG);
                return R.setCodeAndMsg(OrderInfoContentEnum.ORDER_MERGE_TS_REPEAT, orderInfo).put(ConfigureConstant.STRING_ORDER_MERGE_TS, hbts);
            } else {
                log.info("{} 购方信息全为空，不需要返回前端提示", LOGGER_MSG);
                return R.setCodeAndMsg(OrderInfoContentEnum.SUCCESS, orderInfo);
            }
        }
    
        if (allNotNull) {
            //购方信息全部不为空，判断购方信息是否一致，判断依据：除购方企业类型外，所有购方信息相同
            //经产品（王旭）确认暂时这样处理
            if (isgfxxequal(orderInfo)) {
                List<R> hbts = hbts(orderInfo);
                if (hbts.size() > 0) {
                    log.info("{} 购方信息全部不为空且全部相同，允许合并，需要返回前端提示", LOGGER_MSG);
                    return R.setCodeAndMsg(OrderInfoContentEnum.ORDER_MERGE_TS_REPEAT, orderInfo).put(ConfigureConstant.STRING_ORDER_MERGE_TS, hbts);
                } else {
                    log.info("{} 购方信息全部不为空且全部相同，允许合并，不需要返回前端提示", LOGGER_MSG);
                    return R.setCodeAndMsg(OrderInfoContentEnum.SUCCESS, orderInfo);
                }
            } else {
                log.info("{} 购方信息全部不为空且存在不同，不允许合并", LOGGER_MSG);
                return InterfaceResponseUtils.buildReturnInfo(OrderInfoContentEnum.ORDER_MERGE_GFXX_DIFFERENT_ERROR, null);
            }
        }
    
        if ((!allNull) && (!allNotNull)) {
            //购方信息不全为空，判断购方信息非空的订单中购方信息是否一致，如果一致返回前端弹框提示是否合并
            //取购房信息不为空的订单
            List<CommonOrderInfo> gfxxNotNullOrder = new ArrayList<>();
            for (CommonOrderInfo commonOrderInfo : orderInfo) {
                OrderInfo oi = commonOrderInfo.getOrderInfo();
                if (!isgfxxnull(oi)) {
                    gfxxNotNullOrder.add(commonOrderInfo);
                }
            }
            //判断其中不为空的购方信息是否一致
            if (isgfxxequal(gfxxNotNullOrder)) {
                //补全为空的部分的购方信息
                completiongfxx(orderInfo, gfxxNotNullOrder.get(0).getOrderInfo());
                List<R> hbts = hbts(orderInfo);
                R h = R.error(OrderInfoContentEnum.ORDER_MERGE_GFXX_NOTNULL_ERROR);
                hbts.add(h);
                log.info("{} 购方信息不全部为空且不为空的部分购方信息相同，返回前端提示是否合并，需要返回前端提示", LOGGER_MSG);
                return R.setCodeAndMsg(OrderInfoContentEnum.ORDER_MERGE_TS_REPEAT, orderInfo).put(ConfigureConstant.STRING_ORDER_MERGE_TS, hbts);
            } else {
                log.info("{} 购方信息不全部为空且不为空的部分购方信息存在不同，不允许合并", LOGGER_MSG);
                return InterfaceResponseUtils.buildReturnInfo(OrderInfoContentEnum.ORDER_MERGE_GFXX_DIFFERENT_ERROR, null);
            }
        }
        log.info("{} 购方信息校验成功，允许合并", LOGGER_MSG);
        //判断项目明细，多条明细f
        //购方信息全为空，允许合并
        return R.setCodeAndMsg(OrderInfoContentEnum.SUCCESS, orderInfo);
    }
    
    /**
     * 补全为空的部分的购方信息
     *
     * @param orderInfo
     * @author: 陈玉航
     * @date: Created on 2018年8月3日 下午6:01:20
     */
    private void completiongfxx(List<CommonOrderInfo> orderInfo, OrderInfo order) {
        
        String ghfmc = order.getGhfMc();
        String ghfdh = order.getGhfDh();
        String ghfdz = order.getGhfDz();
        String ghfyx = order.getGhfEmail();
        String ghfsh = order.getGhfNsrsbh();
        String ghfzh = order.getGhfZh();
        String ghfsj = order.getGhfSj();
        String ghfyh = order.getGhfYh();
    
        for (CommonOrderInfo commonOrderInfo : orderInfo) {
            OrderInfo oi = commonOrderInfo.getOrderInfo();
            if (isgfxxnull(oi)) {
                oi.setGhfDh(ghfdh);
                oi.setGhfDz(ghfdz);
                oi.setGhfEmail(ghfyx);
                oi.setGhfMc(ghfmc);
                oi.setGhfNsrsbh(ghfsh);
                oi.setGhfSj(ghfsj);
                oi.setGhfYh(ghfyh);
                oi.setGhfZh(ghfzh);
            }
        }
    
    }
    
    /**
     * 判断购方信息是否一致
     *
     * @param orderInfo
     * @return boolean
     * @author: 陈玉航
     * @date: Created on 2018年8月3日 下午5:25:25
     */
    private boolean isgfxxequal(List<CommonOrderInfo> orderInfo) {
        boolean flag = true;
        
        String ghfmc = StringUtils.isBlank(orderInfo.get(0).getOrderInfo().getGhfMc()) ? "" : orderInfo.get(0).getOrderInfo().getGhfMc();
        String ghfdh = StringUtils.isBlank(orderInfo.get(0).getOrderInfo().getGhfDh()) ? "" : orderInfo.get(0).getOrderInfo().getGhfDh();
        String ghfdz = StringUtils.isBlank(orderInfo.get(0).getOrderInfo().getGhfDz()) ? "" : orderInfo.get(0).getOrderInfo().getGhfDz();
        String ghfyx = StringUtils.isBlank(orderInfo.get(0).getOrderInfo().getGhfEmail()) ? "" : orderInfo.get(0).getOrderInfo().getGhfEmail();
        String ghfsh = StringUtils.isBlank(orderInfo.get(0).getOrderInfo().getGhfNsrsbh()) ? "" : orderInfo.get(0).getOrderInfo().getGhfNsrsbh();
        String ghfzh = StringUtils.isBlank(orderInfo.get(0).getOrderInfo().getGhfZh()) ? "" : orderInfo.get(0).getOrderInfo().getGhfZh();
        String ghfsj = StringUtils.isBlank(orderInfo.get(0).getOrderInfo().getGhfSj()) ? "" : orderInfo.get(0).getOrderInfo().getGhfSj();
        String ghfyh = StringUtils.isBlank(orderInfo.get(0).getOrderInfo().getGhfYh()) ? "" : orderInfo.get(0).getOrderInfo().getGhfYh();
        for (CommonOrderInfo commonOrderInfo : orderInfo) {
            OrderInfo oi = commonOrderInfo.getOrderInfo();
            if (!(ghfmc.equals(StringUtils.isBlank(oi.getGhfMc()) ? "" : oi.getGhfMc())
                    && ghfdh.equals(StringUtils.isBlank(oi.getGhfDh()) ? "" : oi.getGhfDh())
                    && ghfdz.equals(StringUtils.isBlank(oi.getGhfDz()) ? "" : oi.getGhfDz())
                    && ghfyx.equals(StringUtils.isBlank(oi.getGhfEmail()) ? "" : oi.getGhfEmail())
                    && ghfsh.equals(StringUtils.isBlank(oi.getGhfNsrsbh()) ? "" : oi.getGhfNsrsbh())
                    && ghfzh.equals(StringUtils.isBlank(oi.getGhfZh()) ? "" : oi.getGhfZh())
                    && ghfsj.equals(StringUtils.isBlank(oi.getGhfSj()) ? "" : oi.getGhfSj())
                    && ghfyh.equals(StringUtils.isBlank(oi.getGhfYh()) ? "" : oi.getGhfYh()))) {
                flag = false;
                break;
            }
        }
        return flag;
    }
    
    /**
     * 购方信息是否全空
     *
     * @return boolean
     * @author: 陈玉航
     * @date: Created on 2018年8月3日 下午5:13:10
     */
    private boolean isgfxxnull(OrderInfo oi) {
        return StringUtils.isBlank(oi.getGhfDh())
                && StringUtils.isBlank(oi.getGhfDz())
                && StringUtils.isBlank(oi.getGhfEmail())
                && StringUtils.isBlank(oi.getGhfMc())
                && StringUtils.isBlank(oi.getGhfNsrsbh())
                && StringUtils.isBlank(oi.getGhfSj())
                && StringUtils.isBlank(oi.getGhfYh())
                && StringUtils.isBlank(oi.getGhfZh());
    }
    
    /**
     * 校验订单合并数据
     *
     * @param orderInfo
     * @return
     */
    private R checkMergeOrderInfo(List<CommonOrderInfo> orderInfo) {
    
        /**
         * 优先判断发票是否一致
         * 其次判断开票类型是否一致
         * 再次判断是否超过明细行数量
         * 最后判断是否超过限额
         *
         * 明细行逻辑:
         *  判断需要合并的数据明细行数量合并后是否超过限制
         * 1.电票明细行最多不大于100行
         * 2.纸票明细行最多不超过1000行
         */
        //取到第一个订单的发票类型
        String fplx = orderInfo.get(0).getOrderInfo().getFpzlDm();
        String kplx = orderInfo.get(0).getOrderInfo().getKplx();
        String ddly = orderInfo.get(0).getProcessInfo().getDdly();
        String xhfnsrsbh = orderInfo.get(0).getOrderInfo().getXhfNsrsbh();
        int itemCount = 0;
        int dpmxSl = 0;
        boolean isCpySpbm = false;
        if (OrderInfoEnum.QDBZ_CODE_4.getKey().equals(orderInfo.get(0).getOrderInfo().getQdBz())) {
            isCpySpbm = true;
        }
        //校验购方信息是否一致
        String gfInfo = (orderInfo.get(0).getOrderInfo().getGhfMc() == null ? "" : orderInfo.get(0).getOrderInfo().getGhfMc())
                + (orderInfo.get(0).getOrderInfo().getGhfNsrsbh() == null ? "" :  orderInfo.get(0).getOrderInfo().getGhfNsrsbh())
                + (orderInfo.get(0).getOrderInfo().getGhfDz() == null ? "" : orderInfo.get(0).getOrderInfo().getGhfDz())
                + (orderInfo.get(0).getOrderInfo().getGhfDh() == null ? "" : orderInfo.get(0).getOrderInfo().getGhfDh())
                + (orderInfo.get(0).getOrderInfo().getGhfYh() == null ? "" : orderInfo.get(0).getOrderInfo().getGhfYh())
                + (orderInfo.get(0).getOrderInfo().getGhfZh() == null ? "" : orderInfo.get(0).getOrderInfo().getGhfZh())
                + (orderInfo.get(0).getOrderInfo().getGhfSj() == null ? "" : orderInfo.get(0).getOrderInfo().getGhfSj())
                + (orderInfo.get(0).getOrderInfo().getGhfEmail() == null ? "" : orderInfo.get(0).getOrderInfo().getGhfEmail());


        for (CommonOrderInfo commonOrderInfo : orderInfo) {
            itemCount += commonOrderInfo.getOrderItemInfo().size();
            if (!fplx.equals(commonOrderInfo.getOrderInfo().getFpzlDm())) {
                log.info("{} 所选订单发票类型不一致，不允许合并", LOGGER_MSG);
                return R.error(OrderInfoContentEnum.ORDER_MERGE_FPLX_DIFFERENT_ERROR);
            }
    
            boolean result = (isCpySpbm && !OrderInfoEnum.QDBZ_CODE_4.getKey().equals(commonOrderInfo.getOrderInfo().getQdBz()))
                    || (!isCpySpbm && OrderInfoEnum.QDBZ_CODE_4.getKey().equals(commonOrderInfo.getOrderInfo().getQdBz()));
            if (result) {
                log.warn("成品油订单和非成品油订单不允许合并");
                return R.error(OrderInfoContentEnum.ORDER_MERGE_CPY_ERROR);
            }
    
            //购方信息校验
    
            if (!xhfnsrsbh.equals(commonOrderInfo.getOrderInfo().getXhfNsrsbh())) {
                log.error("订单合并，销方信息不一致");
                return R.error(OrderInfoContentEnum.ORDER_MERGE_XFXX_NOTNULL_ERROR);
            	
            }
    
            if (!commonOrderInfo.getOrderInfo().getKplx().equals(kplx)) {
            	 log.info("{} 所选订单开票类型不一致，不允许合并", LOGGER_MSG);
                 return R.error(OrderInfoContentEnum.ORDER_MERGE_KPLX_ERROR);
            }
    
            if (!commonOrderInfo.getProcessInfo().getDdly().equals(ddly)) {
                log.info("{} 所选订单来源不一致，不允许合并", LOGGER_MSG);
                return R.error(OrderInfoContentEnum.ORDER_MERGE_DDLY_ERROR);
            }
            
    
            if ((OrderInfoEnum.ORDER_INVOICE_TYPE_51.getKey().equals(commonOrderInfo.getOrderInfo().getFpzlDm())) || isCpySpbm) {
                dpmxSl += commonOrderInfo.getOrderItemInfo().size();
            }

            String gf  = (commonOrderInfo.getOrderInfo().getGhfMc() == null ? "" : commonOrderInfo.getOrderInfo().getGhfMc())
                    + (commonOrderInfo.getOrderInfo().getGhfNsrsbh() == null ? "" :  commonOrderInfo.getOrderInfo().getGhfNsrsbh())
                    + (commonOrderInfo.getOrderInfo().getGhfDz() == null ? "" : commonOrderInfo.getOrderInfo().getGhfDz())
                    + (commonOrderInfo.getOrderInfo().getGhfDh() == null ? "" : commonOrderInfo.getOrderInfo().getGhfDh())
                    + (commonOrderInfo.getOrderInfo().getGhfYh() == null ? "" : commonOrderInfo.getOrderInfo().getGhfYh())
                    + (commonOrderInfo.getOrderInfo().getGhfZh() == null ? "" : commonOrderInfo.getOrderInfo().getGhfZh())
                    + (commonOrderInfo.getOrderInfo().getGhfSj() == null ? "" : commonOrderInfo.getOrderInfo().getGhfSj())
                    + (commonOrderInfo.getOrderInfo().getGhfEmail() == null ? "" : commonOrderInfo.getOrderInfo().getGhfEmail());
            if(!gf.equals(gfInfo)){
                return R.error(OrderInfoContentEnum.ORDER_MERGE_GFXX_DIFFERENT_ERROR);
            }
        }
    
        if (dpmxSl > ConfigureConstant.ELE_MAX_ITEM_LENGTH) {
            log.info("{}电票订单明细数量超过100条", LOGGER_MSG);
            return R.error(OrderInfoContentEnum.MERG_ORDER_ITEMCOUNT_ERROR_51);
        }
        
        if (isCpySpbm && dpmxSl > ConfigureConstant.SPECIAL_MAX_ITEM_LENGTH) {
            log.warn("成品油的订单明细行合并后超过8行");
            return R.error(OrderInfoContentEnum.ORDER_MERGE_CPY_MX_OVER_8_ERROR);
        }
    
        if (OrderInfoEnum.ORDER_INVOICE_TYPE_51.getKey().equals(orderInfo.get(0).getOrderInfo().getFpzlDm()) && itemCount >= ConfigureConstant.ELE_MAX_ITEM_LENGTH) {
            log.error("{}订单合并数量超过最大长度,详细内容:{}", LOGGER_MSG, OrderInfoContentEnum.MERG_ORDER_ITEMCOUNT_ERROR_51.getMessage());
            return R.error(OrderInfoContentEnum.MERG_ORDER_ITEMCOUNT_ERROR_51);
        }
    
        if (OrderInfoEnum.ORDER_INVOICE_TYPE_2.getKey().equals(orderInfo.get(0).getOrderInfo().getFpzlDm()) && itemCount >= ConfigureConstant.MAX_ITEM_LENGTH) {
            log.error("{}订单合并数量超过最大长度,详细内容:{}", LOGGER_MSG, OrderInfoContentEnum.MERG_ORDER_ITEMCOUNT_ERROR_0_2.getMessage());
            return R.error(OrderInfoContentEnum.MERG_ORDER_ITEMCOUNT_ERROR_0_2);
        } else if (OrderInfoEnum.ORDER_INVOICE_TYPE_0.getKey().equals(orderInfo.get(0).getOrderInfo().getFpzlDm()) && itemCount >= ConfigureConstant.MAX_ITEM_LENGTH) {
            log.error("{}订单合并数量超过最大长度,详细内容:{}", LOGGER_MSG, OrderInfoContentEnum.MERG_ORDER_ITEMCOUNT_ERROR_0_2.getMessage());
            return R.error(OrderInfoContentEnum.MERG_ORDER_ITEMCOUNT_ERROR_0_2);
        }
        //判断是否超过限额的逻辑先去掉
       /* *//**
         * 查询用户设备信息
         *//*
        String terminalCode = OrderInfoEnum.TAX_EQUIPMENT_C48.getKey();
        TaxEquipmentInfo taxEquipmentInfo = new TaxEquipmentInfo();
        taxEquipmentInfo.setXhfNsrsbh(nsrsbh);
        List<TaxEquipmentInfo> queryTaxEquipment = apiTaxEquipmentService.queryTaxEquipment(taxEquipmentInfo);
        if(!queryTaxEquipment.isEmpty() && StringUtils.isNotBlank(queryTaxEquipment.get(0).getSksbCode())){
        	terminalCode = queryTaxEquipment.get(0).getSksbCode();
        }
        
        *//**
         * 查询限额
         * 逻辑流程：首先去redis获取用户的限额信息 如果redis中没有缓存用户的限额信息的话 去底层获取用户的限额信息缓存redis redis中的缓存是件30分钟
         *//*
        InvoiceQuotaEntity queryInvoiceQuotaInfoFromRedis =  redisCacheService.queryInvoiceQuotaInfoFromRedis(nsrsbh, fplx, terminalCode);
        if(queryInvoiceQuotaInfoFromRedis == null){
        	 log.error("限额为0，不允许开票");
            return R.error(OrderInfoContentEnum.READY_MERGE_QUOTA_NULL_ERROR);
        }
        
        double quota = Double.parseDouble(queryInvoiceQuotaInfoFromRedis.getInvoiceAmount());
        
        log.info("{} 限额：{}", LOGGER_MSG, quota);
        if (BigDecimal.ZERO.equals(BigDecimal.valueOf(quota).setScale(0, RoundingMode.HALF_UP))) {
            log.error("限额为0，不允许开票");
            return R.error(OrderInfoContentEnum.READY_MERGE_QUOTA_NULL_ERROR);
        }
        
        if (total > quota) {
            log.info("{} 订单金额加和超过发票限额，不允许合并", LOGGER_MSG);
            return R.error(OrderInfoContentEnum.ORDER_MERGE_QUOTA_ERROR);
        }
    */
        return R.ok();
    }
    
}
