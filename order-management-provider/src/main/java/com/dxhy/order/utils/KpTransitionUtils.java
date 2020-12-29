package com.dxhy.order.utils;

import com.dxhy.order.constant.ConfigureConstant;
import com.dxhy.order.constant.OrderInfoEnum;
import com.dxhy.order.model.OrderInfo;
import com.dxhy.order.model.OrderItemInfo;
import com.dxhy.order.model.a9.kp.CommonInvoiceDetail;
import com.dxhy.order.model.a9.kp.CommonInvoiceHead;
import com.dxhy.order.model.a9.kp.CommonInvoiceOrder;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

import java.math.BigDecimal;
import java.util.List;

/**
 * @author ：杨士勇
 * @ClassName ：BeanTransition
 * @Description ：bean转换工具类
 * @date ：2018年10月15日 下午2:19:37
 */
@Slf4j
public class KpTransitionUtils {
    private static final String LOGGER_MSG = "(对象转换工具类)";
    
    /**
     * 获取订单批次开票数据
     *
     * @param orderInfo
     * @return
     */
    public static CommonInvoiceOrder getCOMMON_INVOICE_ORDER(OrderInfo orderInfo) {
        CommonInvoiceOrder commonInvoiceOrder = new CommonInvoiceOrder();
        commonInvoiceOrder.setDDH(orderInfo.getDdh());
        commonInvoiceOrder.setTHDH(orderInfo.getThdh());
        commonInvoiceOrder.setDDDATE(DateUtilsLocal.getYMDHMIS(orderInfo.getDdrq()));
        return commonInvoiceOrder;
    }
    
    /**
     * 订单业务bean转换为底层接口协议bean
     *
     * @param orderInfo
     * @return
     */
    public static CommonInvoiceHead transitionCOMMON_INVOICE_HEAD(OrderInfo orderInfo) {
        CommonInvoiceHead commonInvoiceHead = new CommonInvoiceHead();
    
        /**
         * 外层进行赋值,发票请求流水号需要按照一定规则进行生成,对应订单数据中的开票流水号
         */
        commonInvoiceHead.setFPQQLSH(orderInfo.getFpqqlsh());
        commonInvoiceHead.setNSRSBH(orderInfo.getXhfNsrsbh());
        commonInvoiceHead.setNSRMC(orderInfo.getXhfMc());
        commonInvoiceHead.setKPLX(orderInfo.getKplx());
        commonInvoiceHead.setBMB_BBH(orderInfo.getBbmBbh());
        commonInvoiceHead.setXSF_NSRSBH(orderInfo.getXhfNsrsbh());
        commonInvoiceHead.setXSF_MC(orderInfo.getXhfMc());
        commonInvoiceHead.setXSF_DZ(orderInfo.getXhfDz());
        commonInvoiceHead.setXSF_DH(orderInfo.getXhfDh());
    
        if (orderInfo.getXhfYh() != null && orderInfo.getXhfZh() != null) {
            commonInvoiceHead.setXSF_YHZH(orderInfo.getXhfYh() + orderInfo.getXhfZh());
        } else if (orderInfo.getXhfYh() != null) {
            commonInvoiceHead.setXSF_YHZH(orderInfo.getXhfYh());
        } else if (orderInfo.getXhfZh() != null) {
            commonInvoiceHead.setXSF_YHZH(orderInfo.getXhfZh());
        } else {
            commonInvoiceHead.setXSF_YHZH("");
        }
    
        commonInvoiceHead.setGMF_NSRSBH(StringUtils.isBlank(orderInfo.getGhfNsrsbh()) ? "" : orderInfo.getGhfNsrsbh());
        commonInvoiceHead.setGMF_MC(orderInfo.getGhfMc());
        commonInvoiceHead.setGMF_DZ(StringUtils.isBlank(orderInfo.getGhfDz()) ? "" : orderInfo.getGhfDz());
        commonInvoiceHead.setGMF_QYLX(orderInfo.getGhfQylx());
        commonInvoiceHead.setGMF_SF(StringUtils.isBlank(orderInfo.getGhfSf()) ? "" : orderInfo.getGhfSf());
        commonInvoiceHead.setGMF_GDDH(StringUtils.isBlank(orderInfo.getGhfDh()) ? "" : orderInfo.getGhfDh());
        commonInvoiceHead.setGMF_SJ(StringUtils.isBlank(orderInfo.getGhfSj()) ? "" : orderInfo.getGhfSj());
        // TODO: 2018/10/26 后期进行补全
        commonInvoiceHead.setGMF_WX("");
        commonInvoiceHead.setGMF_EMAIL(orderInfo.getGhfEmail());
        if (orderInfo.getGhfYh() != null && orderInfo.getGhfZh() != null) {
            commonInvoiceHead.setGMF_YHZH(orderInfo.getGhfYh() + orderInfo.getGhfZh());
        } else if (orderInfo.getGhfYh() != null) {
            commonInvoiceHead.setGMF_YHZH(orderInfo.getGhfYh());
        } else if (orderInfo.getGhfZh() != null) {
            commonInvoiceHead.setGMF_YHZH(orderInfo.getGhfZh());
        } else {
            commonInvoiceHead.setGMF_YHZH("");
        }
        commonInvoiceHead.setKPR(orderInfo.getKpr());
        commonInvoiceHead.setSKR(orderInfo.getSkr());
        commonInvoiceHead.setFHR(orderInfo.getFhr());
        commonInvoiceHead.setYFP_DM(orderInfo.getYfpDm());
        commonInvoiceHead.setYFP_HM(orderInfo.getYfpHm());
        commonInvoiceHead.setQD_BZ(orderInfo.getQdBz());
        commonInvoiceHead.setQDXMMC(orderInfo.getQdXmmc());
        commonInvoiceHead.setJSHJ(orderInfo.getKphjje());
        commonInvoiceHead.setHJJE(orderInfo.getHjbhsje());
        commonInvoiceHead.setHJSE(orderInfo.getHjse());
        /**
         * 由于红票数据,需要外层进行处理
         */
        commonInvoiceHead.setBZ(orderInfo.getBz());
        commonInvoiceHead.setPYDM(orderInfo.getPydm());
        commonInvoiceHead.setCHYY(orderInfo.getChyy());
        commonInvoiceHead.setTSCHBZ(orderInfo.getTschbz());
        commonInvoiceHead.setBYZD1(orderInfo.getByzd1());
        commonInvoiceHead.setBYZD2(orderInfo.getByzd2());
        commonInvoiceHead.setBYZD3(orderInfo.getByzd3());
        commonInvoiceHead.setBYZD4(orderInfo.getByzd4());
        commonInvoiceHead.setBYZD5(orderInfo.getByzd5());
    
        return commonInvoiceHead;
    }
    
    
    /**
     * 订单详情业务bean转换为底层接口协议bean
     *
     * @param orderItemInfos
     * @return
     */
    public static CommonInvoiceDetail[] transitionCOMMON_INVOICE_DETAIL(List<OrderItemInfo> orderItemInfos) {
        CommonInvoiceDetail[] commonInvoiceDetails = new CommonInvoiceDetail[orderItemInfos.size()];
        for (int g = 0; g < orderItemInfos.size(); g++) {
            CommonInvoiceDetail commonInvoiceDetail = new CommonInvoiceDetail();
            OrderItemInfo orderItemInfo = orderItemInfos.get(g);
            if (StringUtils.isBlank(orderItemInfo.getSphxh()) || ConfigureConstant.STRING_0.equals(orderItemInfo.getSphxh())) {
                commonInvoiceDetail.setXMXH(g + 1);
            } else {
                commonInvoiceDetail.setXMXH(Integer.parseInt(orderItemInfo.getSphxh()));
            }
            commonInvoiceDetail.setFPHXZ(orderItemInfo.getFphxz());
            commonInvoiceDetail.setSPBM(orderItemInfo.getSpbm());
            commonInvoiceDetail.setZXBM(orderItemInfo.getZxbm());
            commonInvoiceDetail.setYHZCBS(orderItemInfo.getYhzcbs());
            commonInvoiceDetail.setLSLBS(orderItemInfo.getLslbs());
            commonInvoiceDetail.setZZSTSGL(orderItemInfo.getZzstsgl());
            commonInvoiceDetail.setXMMC(orderItemInfo.getXmmc());
            commonInvoiceDetail.setGGXH(orderItemInfo.getGgxh());
            commonInvoiceDetail.setDW(orderItemInfo.getXmdw());
            commonInvoiceDetail.setXMDJ(orderItemInfo.getXmdj());
            commonInvoiceDetail.setXMSL(orderItemInfo.getXmsl());
            commonInvoiceDetail.setXMJE(orderItemInfo.getXmje());
            /**
             * 底层没有项目编码,赋值为空
             */
            commonInvoiceDetail.setXMBM("");
            commonInvoiceDetail.setHSBZ(orderItemInfo.getHsbz());
            commonInvoiceDetail.setSL(orderItemInfo.getSl());
            commonInvoiceDetail.setSE(orderItemInfo.getSe());
            /**
             * 如果单价和数量都为空,则备用字段1赋值为0,
             * 备用字段为1是单价和数量必填
             */
            if (StringUtils.isBlank(orderItemInfo.getXmdj()) && StringUtils.isBlank(orderItemInfo.getXmsl())) {
                commonInvoiceDetail.setBYZD1(ConfigureConstant.STRING_0);
            } else {
                commonInvoiceDetail.setBYZD1(orderItemInfo.getByzd1());
            }
    
            commonInvoiceDetail.setBYZD2(orderItemInfo.getByzd2());
            commonInvoiceDetail.setBYZD3(orderItemInfo.getByzd3());
            commonInvoiceDetails[g] = commonInvoiceDetail;
        }
        return commonInvoiceDetails;
    }
    
    /**
     * 订单详情业务bean转换为底层接口协议bean，合并红字清单明细超8行
     *
     * @param orderItemInfos
     * @return
     */
    public static CommonInvoiceDetail[] transitionCOMMON_INVOICE_DETAIL_Merge(List<OrderItemInfo> orderItemInfos) {
        CommonInvoiceDetail[] commonInvoiceDetails = new CommonInvoiceDetail[1];
        CommonInvoiceDetail commonInvoiceDetail = new CommonInvoiceDetail();
        double xmje = 0D;
        double se = 0D;
        boolean flagSl = false;
        String sl = orderItemInfos.get(0).getSl();
        boolean flagSpbm = false;
        String spbm = orderItemInfos.get(0).getSpbm();
        for (OrderItemInfo orderItemInfo : orderItemInfos) {
            if (!sl.equals(orderItemInfo.getSl())) {
                flagSl = true;
            }
            if (!spbm.equals(orderItemInfo.getSpbm())) {
                flagSpbm = true;
            }
            double valueOf = Double.parseDouble(orderItemInfo.getXmje());
            xmje = DecimalCalculateUtil.add(xmje, valueOf);
            double valueOf2 = Double.parseDouble(orderItemInfo.getSe());
            se = DecimalCalculateUtil.add(se, valueOf2);
        }
    
        commonInvoiceDetail.setFPHXZ(OrderInfoEnum.FPHXZ_CODE_6.getKey());
        commonInvoiceDetail.setXMXH(1);
        commonInvoiceDetail.setSL(orderItemInfos.get(0).getSl());
        if (flagSl) {
            commonInvoiceDetail.setSL("");
        }
        commonInvoiceDetail.setSPBM(orderItemInfos.get(0).getSpbm());
        if (flagSpbm) {
            commonInvoiceDetail.setSPBM("");
        }
        commonInvoiceDetail.setGGXH("");
        commonInvoiceDetail.setDW("");
        commonInvoiceDetail.setXMSL("");
        commonInvoiceDetail.setXMDJ("");
        commonInvoiceDetail.setZXBM("");
        if (StringUtils.isNotBlank(orderItemInfos.get(0).getSl()) && BigDecimal.ZERO.compareTo(new BigDecimal(orderItemInfos.get(0).getSl())) == 0) {
            commonInvoiceDetail.setYHZCBS(orderItemInfos.get(0).getYhzcbs());
            commonInvoiceDetail.setLSLBS(orderItemInfos.get(0).getLslbs());
            commonInvoiceDetail.setZZSTSGL(orderItemInfos.get(0).getZzstsgl());
        } else {
            commonInvoiceDetail.setYHZCBS(OrderInfoEnum.YHZCBS_0.getKey());
            commonInvoiceDetail.setLSLBS("");
            commonInvoiceDetail.setZZSTSGL("");
        }
    
        commonInvoiceDetail.setXMMC(ConfigureConstant.XJZSXHQD);
        commonInvoiceDetail.setXMJE(String.valueOf(xmje).contains("-") ? String.valueOf(xmje) : ("-" + xmje));
        // TODO 传来的item金额都是查询蓝票的金额，都是价税分离后的，含税标志为0不含税
        commonInvoiceDetail.setHSBZ(OrderInfoEnum.HSBZ_0.getKey());
        commonInvoiceDetail.setSE(String.valueOf(se).contains("-") ? String.valueOf(se) : ("-" + se));
        commonInvoiceDetails[0] = commonInvoiceDetail;
        return commonInvoiceDetails;
    }
    
}
