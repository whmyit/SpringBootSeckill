package com.dxhy.order.utils;

import cn.hutool.core.util.RandomUtil;
import com.dxhy.order.constant.ConfigureConstant;
import com.dxhy.order.model.*;
import com.dxhy.order.protocol.order.ORDER_INVOICE_HEAD;
import com.dxhy.order.protocol.order.ORDER_INVOICE_ITEM;
import com.dxhy.order.protocol.v4.order.*;
import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * 业务bean转换
 *
 * @author ZSC-DXHY
 * @date 创建时间: 2019/7/23 18:10
 */
public class BeanTransitionUtils {
    
    
    /**
     * 订单数据和发票数据对外协议bean转换
     *
     * @param orderInfo
     * @param orderInvoiceInfo
     * @return
     */
    public static ORDER_INVOICE_HEAD transitionORDER_INVOICE_HEAD(OrderInfo orderInfo, OrderInvoiceInfo orderInvoiceInfo) {
        ORDER_INVOICE_HEAD orderInvoiceHead = new ORDER_INVOICE_HEAD();
        orderInvoiceHead.setFPQQLSH(orderInfo.getFpqqlsh());
        orderInvoiceHead.setNSRSBH(orderInfo.getNsrsbh());
        orderInvoiceHead.setNSRMC(orderInfo.getNsrmc());
        orderInvoiceHead.setKPLX(orderInfo.getKplx());
        orderInvoiceHead.setBMB_BBH(orderInfo.getBbmBbh());
        orderInvoiceHead.setXSF_NSRSBH(orderInfo.getNsrsbh());
        orderInvoiceHead.setXSF_MC(orderInfo.getXhfMc());
        orderInvoiceHead.setXSF_DZ(orderInfo.getXhfDz());
        orderInvoiceHead.setXSF_DH(orderInfo.getXhfDh());
        orderInvoiceHead.setXSF_YHZH(orderInfo.getXhfYh());
        orderInvoiceHead.setGMF_NSRSBH(orderInfo.getGhfNsrsbh());
        orderInvoiceHead.setGMF_MC(orderInfo.getGhfMc());
        orderInvoiceHead.setGMF_DZ(orderInfo.getGhfDz());
        orderInvoiceHead.setGMF_QYLX(orderInfo.getGhfQylx());
        orderInvoiceHead.setGMF_SF(orderInfo.getGhfSf());
        orderInvoiceHead.setGMF_GDDH(orderInfo.getGhfDh());
        orderInvoiceHead.setGMF_SJ(orderInfo.getGhfSj());
        orderInvoiceHead.setGMF_WX("");
        orderInvoiceHead.setGMF_EMAIL(orderInfo.getGhfEmail());
        orderInvoiceHead.setGMF_YHZH(orderInfo.getGhfYh());
        orderInvoiceHead.setKPR(orderInfo.getKpr());
        orderInvoiceHead.setSKR(orderInfo.getSkr());
        orderInvoiceHead.setFHR(orderInfo.getFhr());
        orderInvoiceHead.setYFP_DM(orderInfo.getYfpDm());
        orderInvoiceHead.setYFP_HM(orderInfo.getYfpHm());
        orderInvoiceHead.setQD_BZ(orderInfo.getQdBz());
        orderInvoiceHead.setQDXMMC(orderInfo.getQdXmmc());
        orderInvoiceHead.setJSHJ(orderInfo.getKphjje());
        orderInvoiceHead.setHJJE(orderInfo.getHjbhsje());
        orderInvoiceHead.setHJSE(orderInfo.getHjse());
        orderInvoiceHead.setBZ(orderInfo.getBz());
        orderInvoiceHead.setPYDM(orderInfo.getPydm());
        orderInvoiceHead.setCHYY(orderInfo.getChyy());
        orderInvoiceHead.setTSCHBZ(orderInfo.getTschbz());
        orderInvoiceHead.setKPJH(orderInfo.getKpjh());
        orderInvoiceHead.setSLD(orderInfo.getSld());
        orderInvoiceHead.setFPZLDM(orderInfo.getFpzlDm());
        orderInvoiceHead.setDDH(orderInfo.getDdh());
        orderInvoiceHead.setTHDH(orderInfo.getThdh());
        orderInvoiceHead.setDDDATE(DateUtilsLocal.getYMDHMIS(orderInfo.getDdrq()));
        // TODO: 2018/9/21 后期考虑添加订单类型和订单状态等数据.
        if (orderInvoiceInfo != null) {
            orderInvoiceHead.setJQBH(orderInvoiceInfo.getJqbh());
            orderInvoiceHead.setFP_DM(orderInvoiceInfo.getFpdm());
            orderInvoiceHead.setFP_HM(orderInvoiceInfo.getFphm());
            orderInvoiceHead.setKPRQ(orderInvoiceInfo.getKprq() == null ? "" : DateUtilsLocal.getYMDHMIS(orderInvoiceInfo.getKprq()));
            orderInvoiceHead.setJYM(orderInvoiceInfo.getJym());
            orderInvoiceHead.setFWM(orderInvoiceInfo.getFwm());
        }
        //特殊处理,需要调用接口进行填写
        orderInvoiceHead.setPDF_FILE("");
    
        orderInvoiceHead.setBYZD1("");
        orderInvoiceHead.setBYZD2("");
        orderInvoiceHead.setBYZD3("");
        orderInvoiceHead.setBYZD4("");
        orderInvoiceHead.setBYZD5("");
        return orderInvoiceHead;
    }
    
    /**
     * 订单数据和发票数据对外协议bean转换
     *
     * @param orderInfo
     * @param orderInvoiceInfo
     * @return
     */
    public static DDFPXX transitionORDER_INVOICE_INFOV3(OrderInfo orderInfo, OrderProcessInfo orderProcessInfo, OrderInvoiceInfo orderInvoiceInfo) {
        DDFPXX orderInvoiceInfo1 = new DDFPXX();
        orderInvoiceInfo1.setDDQQLSH(orderInfo.getFpqqlsh());
        orderInvoiceInfo1.setNSRSBH(orderInfo.getNsrsbh());
        orderInvoiceInfo1.setNSRMC(orderInfo.getNsrmc());
        orderInvoiceInfo1.setKPLX(orderInfo.getKplx());
        orderInvoiceInfo1.setBMBBBH(orderInfo.getBbmBbh());
        orderInvoiceInfo1.setXHFSBH(orderInfo.getNsrsbh());
        orderInvoiceInfo1.setXHFMC(orderInfo.getXhfMc());
        orderInvoiceInfo1.setXHFDZ(orderInfo.getXhfDz());
        orderInvoiceInfo1.setXHFDH(orderInfo.getXhfDh());
        orderInvoiceInfo1.setXHFYH(orderInfo.getXhfYh());
        orderInvoiceInfo1.setXHFZH(orderInfo.getXhfZh());
        orderInvoiceInfo1.setGMFSBH(orderInfo.getGhfNsrsbh());
        orderInvoiceInfo1.setGMFBM(orderInfo.getGhfId());
        orderInvoiceInfo1.setGMFMC(orderInfo.getGhfMc());
        orderInvoiceInfo1.setGMFDZ(orderInfo.getGhfDz());
        orderInvoiceInfo1.setGMFLX(orderInfo.getGhfQylx());
        orderInvoiceInfo1.setGMFSF(orderInfo.getGhfSf());
        orderInvoiceInfo1.setGMFDH(orderInfo.getGhfDh());
        orderInvoiceInfo1.setGMFSJH(orderInfo.getGhfSj());
        orderInvoiceInfo1.setGMFDZYX(orderInfo.getGhfEmail());
        orderInvoiceInfo1.setGMFYH(orderInfo.getGhfYh());
        orderInvoiceInfo1.setGMFZH(orderInfo.getGhfZh());
        orderInvoiceInfo1.setKPR(orderInfo.getKpr());
        orderInvoiceInfo1.setSKR(orderInfo.getSkr());
        orderInvoiceInfo1.setFHR(orderInfo.getFhr());
        orderInvoiceInfo1.setYFPDM(orderInfo.getYfpDm());
        orderInvoiceInfo1.setYFPHM(orderInfo.getYfpHm());
        orderInvoiceInfo1.setQDBZ(orderInfo.getQdBz());
        orderInvoiceInfo1.setQDXMMC(orderInfo.getQdXmmc());
        orderInvoiceInfo1.setJSHJ(orderInfo.getKphjje());
        orderInvoiceInfo1.setHJJE(orderInfo.getHjbhsje());
        orderInvoiceInfo1.setHJSE(orderInfo.getHjse());
        orderInvoiceInfo1.setBZ(orderInfo.getBz());
        orderInvoiceInfo1.setCHYY(orderInfo.getChyy());
        orderInvoiceInfo1.setTSCHBZ(orderInfo.getTschbz());
        orderInvoiceInfo1.setKPJH(orderInfo.getKpjh());
        orderInvoiceInfo1.setKPZD(orderInfo.getSld());
        orderInvoiceInfo1.setFPLXDM(orderInfo.getFpzlDm());
        orderInvoiceInfo1.setDDH(orderInfo.getDdh());
        orderInvoiceInfo1.setTHDH(orderInfo.getThdh());
        orderInvoiceInfo1.setDDSJ(DateUtilsLocal.getYMDHMIS(orderInfo.getDdrq()));
        orderInvoiceInfo1.setYWLX(orderProcessInfo.getYwlx());
        orderInvoiceInfo1.setKPFS(orderProcessInfo.getKpfs());
        
        // TODO: 2018/9/21 后期考虑添加订单类型和订单状态等数据.
        orderInvoiceInfo1.setJQBH(orderInvoiceInfo.getJqbh());
        orderInvoiceInfo1.setFPDM(orderInvoiceInfo.getFpdm());
        orderInvoiceInfo1.setFPHM(orderInvoiceInfo.getFphm());
        orderInvoiceInfo1.setKPRQ(orderInvoiceInfo.getKprq() == null ? "" : DateUtilsLocal.getYMDHMIS(orderInvoiceInfo.getKprq()));
        orderInvoiceInfo1.setJYM(orderInvoiceInfo.getJym());
        orderInvoiceInfo1.setFWM(orderInvoiceInfo.getFwm());
        orderInvoiceInfo1.setPDFZJL(orderInvoiceInfo.getPdfUrl());
        //特殊处理,需要调用接口进行填写
        orderInvoiceInfo1.setTQM(orderInfo.getTqm());
        orderInvoiceInfo1.setDTM("");
        orderInvoiceInfo1.setPDFDZ("");
        
        orderInvoiceInfo1.setBYZD1("");
        orderInvoiceInfo1.setBYZD2("");
        orderInvoiceInfo1.setBYZD3("");
        orderInvoiceInfo1.setBYZD4("");
        orderInvoiceInfo1.setBYZD5("");
        return orderInvoiceInfo1;
    }
    
    /**
     * 订单数据和发票数据对外明细协议bean转换
     *
     * @param orderItemInfos
     * @return
     */
    public static List<ORDER_INVOICE_ITEM> transitionORDER_INVOICE_ITEM(List<OrderItemInfo> orderItemInfos) {
        List<ORDER_INVOICE_ITEM> orderInvoiceItems = new ArrayList<>();
        for (OrderItemInfo itemInfo : orderItemInfos) {
            ORDER_INVOICE_ITEM orderInvoiceItem = new ORDER_INVOICE_ITEM();
            OrderItemInfo orderItemInfo = itemInfo;
            orderInvoiceItem.setXMXH(orderItemInfo.getSphxh());
            orderInvoiceItem.setFPHXZ(orderItemInfo.getFphxz());
            orderInvoiceItem.setSPBM(orderItemInfo.getSpbm());
            orderInvoiceItem.setZXBM(orderItemInfo.getZxbm());
            orderInvoiceItem.setYHZCBS(orderItemInfo.getYhzcbs());
            orderInvoiceItem.setLSLBS(orderItemInfo.getLslbs());
            orderInvoiceItem.setZZSTSGL(orderItemInfo.getZzstsgl());
            orderInvoiceItem.setXMMC(orderItemInfo.getXmmc());
            orderInvoiceItem.setGGXH(orderItemInfo.getGgxh());
            orderInvoiceItem.setDW(orderItemInfo.getXmdw());
            orderInvoiceItem.setXMSL(orderItemInfo.getXmsl());
            orderInvoiceItem.setXMDJ(orderItemInfo.getXmdj());
            orderInvoiceItem.setXMJE(orderItemInfo.getXmje());
            orderInvoiceItem.setHSBZ(orderItemInfo.getHsbz());
            orderInvoiceItem.setSL(orderItemInfo.getSl());
            orderInvoiceItem.setSE(orderItemInfo.getSe());
            orderInvoiceItem.setKCE(orderItemInfo.getKce());
            orderInvoiceItem.setBYZD1(orderItemInfo.getByzd1());
            orderInvoiceItem.setBYZD2(orderItemInfo.getByzd2());
            orderInvoiceItem.setBYZD3(orderItemInfo.getByzd3());
            orderInvoiceItems.add(orderInvoiceItem);
        }
        
        return orderInvoiceItems;
    }
    
    
    /**
     * 订单数据和发票数据对外明细协议bean转换
     *
     * @param orderItemInfos
     * @return
     */
    public static List<DDMXXX> transitionORDER_INVOICE_ITEMV3(List<OrderItemInfo> orderItemInfos) {
        List<DDMXXX> orderInvoiceItems = new ArrayList<>();
        for (OrderItemInfo itemInfo : orderItemInfos) {
            DDMXXX orderInvoiceItem = new DDMXXX();
            OrderItemInfo orderItemInfo = itemInfo;
            orderInvoiceItem.setXH(orderItemInfo.getSphxh());
            orderInvoiceItem.setFPHXZ(orderItemInfo.getFphxz());
            orderInvoiceItem.setSPBM(orderItemInfo.getSpbm());
            orderInvoiceItem.setZXBM(orderItemInfo.getZxbm());
            orderInvoiceItem.setYHZCBS(orderItemInfo.getYhzcbs());
            orderInvoiceItem.setLSLBS(orderItemInfo.getLslbs());
            orderInvoiceItem.setZZSTSGL(orderItemInfo.getZzstsgl());
            orderInvoiceItem.setXMMC(orderItemInfo.getXmmc());
            orderInvoiceItem.setGGXH(orderItemInfo.getGgxh());
            orderInvoiceItem.setDW(orderItemInfo.getXmdw());
            orderInvoiceItem.setSPSL(orderItemInfo.getXmsl());
            orderInvoiceItem.setDJ(orderItemInfo.getXmdj());
            orderInvoiceItem.setJE(orderItemInfo.getXmje());
            orderInvoiceItem.setHSBZ(orderItemInfo.getHsbz());
            orderInvoiceItem.setSL(orderItemInfo.getSl());
            orderInvoiceItem.setSE(orderItemInfo.getSe());
            orderInvoiceItem.setKCE(orderItemInfo.getKce());
            orderInvoiceItem.setBYZD1(orderItemInfo.getByzd1());
            orderInvoiceItem.setBYZD2(orderItemInfo.getByzd2());
            orderInvoiceItem.setBYZD3(orderItemInfo.getByzd3());
            orderInvoiceItems.add(orderInvoiceItem);
        }
    
        return orderInvoiceItems;
    }
    
    
    /**
     * 发票数据转换成推送数据
     *
     * @param invoiceInfo
     * @param fpqqpch
     * @return
     */
    public static InvoicePush transitionInvoicePush(OrderInvoiceInfo invoiceInfo, String fpqqpch) {
        InvoicePush invoicePush = new InvoicePush();
        invoicePush.setFPQQPCH(fpqqpch);
        invoicePush.setFPQQLSH(invoiceInfo.getFpqqlsh());
        invoicePush.setKPLSH(invoiceInfo.getKplsh());
        invoicePush.setJQBH(invoiceInfo.getJqbh());
        invoicePush.setDDH(invoiceInfo.getDdh());
        invoicePush.setJYM(invoiceInfo.getJym());
        invoicePush.setFWM(invoiceInfo.getFwm());
        invoicePush.setEWM(invoiceInfo.getEwm());
        invoicePush.setFP_DM(invoiceInfo.getFpdm());
        invoicePush.setFP_HM(invoiceInfo.getFphm());
        invoicePush.setKPRQ(invoiceInfo.getKprq());
        invoicePush.setHJBHSJE(Double.valueOf(invoiceInfo.getHjbhsje()));
        invoicePush.setKPHJSE(Double.valueOf(invoiceInfo.getKphjje()));
        invoicePush.setPDF_URL(invoiceInfo.getPdfUrl());
        invoicePush.setNSRSBH(invoiceInfo.getXhfNsrsbh());
        return invoicePush;
    }
    
    
    /**
     * 对外订单数据转换为订单业务数据
     *
     * @param ddzxx
     * @return
     */
    public static CommonOrderInfo transitionCommonOrderInfoV3(DDZXX ddzxx) {
        CommonOrderInfo commonOrderInfo = new CommonOrderInfo();
        
        OrderInfo orderInfo = transitionOrderInfoV3(ddzxx.getDDTXX());
        
        List<OrderItemInfo> orderItemInfos = transitionOrderItemInfoV3(ddzxx.getDDMXXX(), orderInfo.getXhfNsrsbh());
        
        /**
         * 默认开票项目为明细行首行数据
         */
        orderInfo.setKpxm(orderItemInfos.get(0).getXmmc());
        
        commonOrderInfo.setOrderInfo(orderInfo);
        commonOrderInfo.setOrderItemInfo(orderItemInfos);
        
        return commonOrderInfo;
    }
    
    
    /**
     * 自动开票接口数据转换--订单主体信息V3
     *
     * @param ddtxx
     * @return
     */
    public static OrderInfo transitionOrderInfoV3(DDTXX ddtxx) {
        
        OrderInfo orderInfo = new OrderInfo();
        orderInfo.setFpqqlsh(ddtxx.getDDQQLSH());
        //如果订单号为空的话， 自动生成20位的订单号
        orderInfo.setDdh(StringUtils.isBlank(ddtxx.getDDH()) ? RandomUtil.randomNumbers(12) : ddtxx.getDDH());
        orderInfo.setThdh(ddtxx.getTHDH());
        orderInfo.setGhfId(ddtxx.getGMFBM());
        orderInfo.setDdlx(ConfigureConstant.STRING_0);
        Date ddrq = StringUtils.isBlank(ddtxx.getDDSJ()) ? new Date() : DateUtilsLocal.getDefaultDate_yyyy_MM_dd_HH_mm_ss(ddtxx.getDDSJ());
        if (ddrq == null) {
            ddrq = new Date();
        }
        orderInfo.setDdrq(ddrq);
        orderInfo.setDsptbm("");
        orderInfo.setNsrsbh(ddtxx.getNSRSBH());
        orderInfo.setNsrmc(ddtxx.getNSRMC());
        orderInfo.setNsrdzdah(ddtxx.getNSRSBH());
        orderInfo.setSwjgDm("");
        // TODO: 2018/10/25 代开标志默认为0
        orderInfo.setDkbz(ConfigureConstant.STRING_0);
        /**
         * 外层进行补全,使用明细行第一行商品名称作为开票项目
         */
        orderInfo.setKpxm("");
        orderInfo.setBbmBbh(ddtxx.getBMBBBH());
        orderInfo.setXhfMc(ddtxx.getXHFMC());
        orderInfo.setXhfNsrsbh(ddtxx.getXHFSBH());
        orderInfo.setXhfDz(ddtxx.getXHFDZ());
        orderInfo.setXhfDh(ddtxx.getXHFDH());
        // TODO: 2018/10/25 前期使用银行字段存储银行帐号
        if (StringUtils.isNotBlank(ddtxx.getXHFYH())) {
            orderInfo.setXhfYh(ddtxx.getXHFYH());
        }
        if (StringUtils.isNotBlank(ddtxx.getXHFZH())) {
            orderInfo.setXhfZh(ddtxx.getXHFZH());
        }
        orderInfo.setGhfQylx(ddtxx.getGMFLX());
        orderInfo.setGhfSf(ddtxx.getGMFSF());
        orderInfo.setGhfMc(ddtxx.getGMFMC());
        orderInfo.setGhfNsrsbh(ddtxx.getGMFSBH());
        orderInfo.setGhfDz(ddtxx.getGMFDZ());
        orderInfo.setGhfDh(ddtxx.getGMFDH());
        // TODO: 2018/10/25 前期使用银行字段存储银行帐号
        if (StringUtils.isNotBlank(ddtxx.getGMFYH())) {
            orderInfo.setGhfYh(ddtxx.getGMFYH());
        }
        if (StringUtils.isNotBlank(ddtxx.getGMFZH())) {
            orderInfo.setGhfZh(ddtxx.getGMFZH());
        }
        orderInfo.setGhfSj(ddtxx.getGMFSJH());
        orderInfo.setGhfEmail(ddtxx.getGMFDZYX());
        orderInfo.setHyDm("");
        orderInfo.setHyMc("");
        orderInfo.setKpr(ddtxx.getKPR());
        orderInfo.setSkr(ddtxx.getSKR());
        orderInfo.setFhr(ddtxx.getFHR());
        orderInfo.setKplx(ddtxx.getKPLX());
        /**
         * 外层进行补全,使用最外层的发票类型作为种类代码
         */
        orderInfo.setFpzlDm("");
        orderInfo.setYfpDm(ddtxx.getYFPDM());
        orderInfo.setYfpHm(ddtxx.getYFPHM());
        orderInfo.setChyy(ddtxx.getCHYY());
        orderInfo.setTschbz(ddtxx.getTSCHBZ());
        // TODO: 2018/10/25 操作代码默认为10
        orderInfo.setCzdm("10");
        orderInfo.setQdBz(ddtxx.getQDBZ());
        orderInfo.setQdXmmc(ddtxx.getQDXMMC());
        orderInfo.setKphjje(ddtxx.getJSHJ());
        orderInfo.setHjbhsje(ddtxx.getHJJE());
        orderInfo.setHjse(ddtxx.getHJSE());
        // TODO: 2018/10/25 后期考虑添加以下字段:门店号,业务类型,推送地址
        orderInfo.setMdh("");
        orderInfo.setYwlx("");
        
        /**
         * 外层进行补全,使用最外层的数据进行填充
         */
        orderInfo.setKpjh("");
        orderInfo.setSld("");
        orderInfo.setSldMc("");
        orderInfo.setTqm(ddtxx.getTQM());
        
        orderInfo.setBz(ddtxx.getBZ());
        orderInfo.setCreateTime(new Date());
        orderInfo.setUpdateTime(new Date());
        orderInfo.setByzd1(ddtxx.getBYZD1());
        orderInfo.setByzd2(ddtxx.getBYZD2());
        orderInfo.setByzd3(ddtxx.getBYZD3());
        orderInfo.setByzd4(ddtxx.getBYZD4());
        orderInfo.setByzd5(ddtxx.getBYZD5());
        return orderInfo;
    }
    
    
    /**
     * 自动开票接口数据转换--订单明细信息
     *
     * @param ddmxxxes
     * @return
     */
    public static List<OrderItemInfo> transitionOrderItemInfoV3(List<DDMXXX> ddmxxxes, String xhfNsrsbh) {
        List<OrderItemInfo> orderItemInfos = new ArrayList<>();
        for (int i = 0; i < ddmxxxes.size(); i++) {
            OrderItemInfo orderItemInfo = new OrderItemInfo();
            DDMXXX orderInvoiceItem = ddmxxxes.get(i);
            orderItemInfo.setSphxh(StringUtils.isBlank(orderInvoiceItem.getXH()) ? String.valueOf(i + 1) : orderInvoiceItem.getXH());
            orderItemInfo.setXmmc(orderInvoiceItem.getXMMC());
            orderItemInfo.setXmdw(orderInvoiceItem.getDW());
            orderItemInfo.setGgxh(orderInvoiceItem.getGGXH());
            
            orderItemInfo.setXmdj(orderInvoiceItem.getDJ());
            orderItemInfo.setXmsl(orderInvoiceItem.getSPSL());
            
            orderItemInfo.setHsbz(orderInvoiceItem.getHSBZ());
            orderItemInfo.setFphxz(orderInvoiceItem.getFPHXZ());
            orderItemInfo.setSpbm(orderInvoiceItem.getSPBM());
            orderItemInfo.setZxbm(orderInvoiceItem.getZXBM());
            orderItemInfo.setYhzcbs(orderInvoiceItem.getYHZCBS());
            orderItemInfo.setLslbs(orderInvoiceItem.getLSLBS());
            orderItemInfo.setZzstsgl(orderInvoiceItem.getZZSTSGL());
            orderItemInfo.setKce(orderInvoiceItem.getKCE());
            orderItemInfo.setXmje(orderInvoiceItem.getJE());
            orderItemInfo.setSl(orderInvoiceItem.getSL());
            orderItemInfo.setSe(orderInvoiceItem.getSE());
            orderItemInfo.setXhfNsrsbh(xhfNsrsbh);
            orderItemInfo.setWcje("0.00");
            orderItemInfo.setByzd1(orderInvoiceItem.getBYZD1());
            orderItemInfo.setByzd2(orderInvoiceItem.getBYZD2());
            orderItemInfo.setByzd3(orderInvoiceItem.getBYZD3());
            orderItemInfo.setCreateTime(new Date());
            orderItemInfos.add(orderItemInfo);
        }
        return orderItemInfos;
    }
    
    /**
     * 订单数据和发票数据对外拆分合并关系协议bean转换
     *
     * @param orderProcessInfos 订单处理-业务bean集合{@link OrderProcessInfo}
     * @return java.util.List<com.dxhy.order.protocol.order.ORDER_EXTENSION_INFO>
     * @author <a href="tivenninesongs@163.com">yaoxuguang</a>
     * @date 2020/4/10
     */
    public static List<DDKZXX> transitionORDER_EXTENSION_INFOS(List<OrderProcessInfo> orderProcessInfos) {
        List<DDKZXX> orderExtensionInfos = new ArrayList<>();
        orderProcessInfos.forEach(orderProcessInfo -> {
            DDKZXX orderExtensionInfo = new DDKZXX();
            orderExtensionInfo.setDDQQLSH(orderProcessInfo.getFpqqlsh());
            orderExtensionInfo.setDDH(orderProcessInfo.getDdh());
            orderExtensionInfo.setDDLX(orderProcessInfo.getDdlx());
            orderExtensionInfo.setBYZD1("");
            orderExtensionInfo.setBYZD2("");
            orderExtensionInfo.setBYZD3("");
            orderExtensionInfos.add(orderExtensionInfo);
        });
        return orderExtensionInfos;
    }
    
    
}
