package com.dxhy.order.consumer.utils;

import cn.hutool.core.util.RandomUtil;
import com.dxhy.order.constant.ConfigureConstant;
import com.dxhy.order.constant.OrderInfoEnum;
import com.dxhy.order.consumer.config.SystemConfig;
import com.dxhy.order.consumer.modules.scaninvoice.model.PageQrcodeOrderInfo;
import com.dxhy.order.consumer.modules.scaninvoice.model.PageQrcodeOrderItemInfo;
import com.dxhy.order.model.*;
import com.dxhy.order.utils.DecimalCalculateUtil;
import com.dxhy.order.utils.StringUtil;
import org.apache.commons.lang3.StringUtils;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * @author ：杨士勇
 * @ClassName ：pageDataDealUtil
 * @Description ：前端数据展示处理
 * @date ：2018年9月26日 下午4:55:51
 */

public class PageDataDealUtil {
    private static final NumberFormat NF = NumberFormat.getPercentInstance();
    
    /**
     * @param @param list
     * @return void
     * @throws
     * @Title : dealOrderItemInfo
     * @Description ：将税率从小数转换为百分数的形式，便于前端展示
     */
    public static void dealOrderItemInfo(List<OrderItemInfo> orderItemInfos) {
        for (OrderItemInfo orderItemInfo : orderItemInfos) {
    
            /**
             * 处理折扣行单价和数量,不显示
             */
            if (StringUtils.isNotBlank(orderItemInfo.getFphxz()) && OrderInfoEnum.FPHXZ_CODE_1.getKey().equals(orderItemInfo.getFphxz())) {
                orderItemInfo.setXmdj("");
                orderItemInfo.setXmsl("");
            }
    
            /**
             * 处理金额,格式化金额
             */
            if (StringUtils.isNotBlank(orderItemInfo.getXmje())) {
                orderItemInfo.setXmje(DecimalCalculateUtil.decimalFormatToString(orderItemInfo.getXmje(), ConfigureConstant.INT_2));
            }
    
            /**
             * 处理税额,格式化税额
             */
            if (StringUtils.isNotBlank(orderItemInfo.getSe())) {
                orderItemInfo.setSe(DecimalCalculateUtil.decimalFormatToString(orderItemInfo.getSe(), ConfigureConstant.INT_2));
            }
            
            /**
             * 处理单价,保留非零位
             */
            if (StringUtils.isNotBlank(orderItemInfo.getXmdj())) {
                orderItemInfo.setXmdj(StringUtil.slFormat(DecimalCalculateUtil.decimalFormatToString(orderItemInfo.getXmdj(), ConfigureConstant.INT_8)));
            }
            
            /**
             * 处理数量,保留非零位
             */
            if (StringUtils.isNotBlank(orderItemInfo.getXmsl())) {
                orderItemInfo.setXmsl(StringUtil.slFormat(DecimalCalculateUtil.decimalFormatToString(orderItemInfo.getXmsl(), ConfigureConstant.INT_8)));
            }
            
            /**
             * 处理税率,按照百分比显示
             */
            if (StringUtils.isNotBlank(orderItemInfo.getSl())) {
                NF.setMaximumFractionDigits(3);
                orderItemInfo.setSl(orderItemInfo.getSl().contains("%") ? orderItemInfo.getSl() : NF.format(Double.valueOf(orderItemInfo.getSl())));
            }
    
        }
        
    }
    /**
     * bean转换
     */
    public static CommonOrderInfo pageToFpkjInfo(PageQrcodeOrderInfo pageQrcodeOrderInfo) {
        CommonOrderInfo commonOrderInfo = new CommonOrderInfo();
        OrderInfo orderInfo = new OrderInfo();
        orderInfo.setBz(pageQrcodeOrderInfo.getBz());
        orderInfo.setFhr(pageQrcodeOrderInfo.getFhr());
        orderInfo.setGhfDh(pageQrcodeOrderInfo.getGhfDh());
        orderInfo.setGhfDz(pageQrcodeOrderInfo.getGhfDz());
        orderInfo.setDdlx(OrderInfoEnum.ORDER_SOURCE_0.getKey());
        orderInfo.setKpjh(pageQrcodeOrderInfo.getKpjh());
        // 购货方邮箱
        orderInfo.setGhfEmail(pageQrcodeOrderInfo.getGhfEmail());
        orderInfo.setGhfMc(pageQrcodeOrderInfo.getGhfMc());
        orderInfo.setGhfNsrsbh(pageQrcodeOrderInfo.getGhfNsrsbh());
        orderInfo.setGhfSj(pageQrcodeOrderInfo.getGhfSj());
        //  银行账号是否需要拆分开
        orderInfo.setGhfYh(pageQrcodeOrderInfo.getGhfYh());
        orderInfo.setGhfZh(pageQrcodeOrderInfo.getGhfZh());
        orderInfo.setDdh(StringUtils.isBlank(pageQrcodeOrderInfo.getDdh()) ? RandomUtil.randomNumbers(12) : pageQrcodeOrderInfo.getDdh());
        orderInfo.setDdrq(new Date());
        orderInfo.setCreateTime(new Date());
        orderInfo.setUpdateTime(new Date());
        orderInfo.setBbmBbh(SystemConfig.bmbbbh);
        orderInfo.setSld(pageQrcodeOrderInfo.getSld());
        orderInfo.setKpjh(pageQrcodeOrderInfo.getKpjh());
        orderInfo.setSldMc(pageQrcodeOrderInfo.getSldmc());
        orderInfo.setQdBz(StringUtils.isNotBlank(pageQrcodeOrderInfo.getQdbz()) ? pageQrcodeOrderInfo.getQdbz() : OrderInfoEnum.QDBZ_CODE_0.getKey());
        if (pageQrcodeOrderInfo.getPageOrderItemInfo().length > 0) {
            orderInfo.setKpxm(pageQrcodeOrderInfo.getPageOrderItemInfo()[0].getXmmc());
        }
        orderInfo.setFpzlDm(pageQrcodeOrderInfo.getFplx());
        orderInfo.setKpr(pageQrcodeOrderInfo.getKpy());
        orderInfo.setSkr((pageQrcodeOrderInfo.getSky()));
        orderInfo.setKplx(pageQrcodeOrderInfo.getKplx());
        orderInfo.setYfpDm(pageQrcodeOrderInfo.getYfpdm());
        orderInfo.setYfpHm(pageQrcodeOrderInfo.getYfphm());
        orderInfo.setThdh(pageQrcodeOrderInfo.getDdh());
        // TODO 扫码开票抬头类型只有个人和企业
        orderInfo.setGhfQylx(pageQrcodeOrderInfo.getGhfqylx());
        orderInfo.setDkbz(OrderInfoEnum.DKBZ_0.getKey());

        orderInfo.setXhfNsrsbh(pageQrcodeOrderInfo.getXhfNsrsbh());
        orderInfo.setXhfDz(pageQrcodeOrderInfo.getXhfdz());
        orderInfo.setXhfDh(pageQrcodeOrderInfo.getXhfdh());
        orderInfo.setXhfYh(pageQrcodeOrderInfo.getXhfyh());
        orderInfo.setXhfZh(pageQrcodeOrderInfo.getXhfzh());
        orderInfo.setXhfMc(pageQrcodeOrderInfo.getXhfmc());
        orderInfo.setYwlx(pageQrcodeOrderInfo.getYwlx());
        orderInfo.setYwlxId(pageQrcodeOrderInfo.getYwlxId());
        orderInfo.setTqm(pageQrcodeOrderInfo.getTqm());
        orderInfo.setNsrmc(pageQrcodeOrderInfo.getXhfmc());
        orderInfo.setNsrsbh(pageQrcodeOrderInfo.getXhfNsrsbh());

        //处理开票合计金额

        double kphjje = 0.00;
        List<OrderItemInfo> orderItemInfos = new ArrayList<>();
        PageQrcodeOrderItemInfo[] pageOrderItemInfoArray = pageQrcodeOrderInfo.getPageOrderItemInfo();
        int i = 1;
        for (PageQrcodeOrderItemInfo pageOrderItemInfo : pageOrderItemInfoArray) {
            OrderItemInfo orderItemInfo = new OrderItemInfo();

            orderItemInfo.setXmdw(pageOrderItemInfo.getXmdw());
            orderItemInfo.setGgxh(pageOrderItemInfo.getGgxh());

            orderItemInfo.setXmsl("");
            orderItemInfo.setXmdj("");
            if (!StringUtils.isBlank(pageOrderItemInfo.getSl())) {
                orderItemInfo.setSl(StringUtil.formatSl(pageOrderItemInfo.getSl()));

            }
            orderItemInfo.setSpbm(pageOrderItemInfo.getSpbm());
            if (!StringUtils.isBlank(pageOrderItemInfo.getXmdj())) {
                orderItemInfo.setXmdj(DecimalCalculateUtil.decimalFormatToString(pageOrderItemInfo.getXmdj(), ConfigureConstant.INT_8));
            }


            if (!StringUtils.isBlank(pageOrderItemInfo.getXmje())) {
                orderItemInfo.setXmje(DecimalCalculateUtil.decimalFormatToString(pageOrderItemInfo.getXmje(), ConfigureConstant.INT_2));
            }
            orderItemInfo.setXmmc(pageOrderItemInfo.getXmmc());
            if (!StringUtils.isBlank(pageOrderItemInfo.getXmsl())) {
                orderItemInfo.setXmsl(DecimalCalculateUtil.decimalFormatToString(pageOrderItemInfo.getXmsl(), ConfigureConstant.INT_8));
            }
    
            orderItemInfo.setHsbz(pageOrderItemInfo.getHsbz());
            orderItemInfo.setFphxz(pageOrderItemInfo.getFphxz());
            orderItemInfo.setYhzcbs(pageOrderItemInfo.getYhzcbs());
            orderItemInfo.setZzstsgl(pageOrderItemInfo.getZzstsgl());
            orderItemInfo.setLslbs(pageOrderItemInfo.getLslbs());
            orderItemInfo.setSphxh(String.valueOf(i));
            orderItemInfo.setSe(pageOrderItemInfo.getXmse());
            orderItemInfo.setKce(pageOrderItemInfo.getKce());
            orderItemInfo.setXhfNsrsbh(orderInfo.getXhfNsrsbh());
            orderItemInfos.add(orderItemInfo);
            if (!StringUtils.isBlank(orderItemInfo.getXmje())) {
                kphjje += Double.parseDouble(orderItemInfo.getXmje());
            }
            i++;
        }
        if (pageOrderItemInfoArray.length <= ConfigureConstant.INT_2 && StringUtils.isNotBlank(pageOrderItemInfoArray[0].getKce())) {
            StringBuilder sb = new StringBuilder();
            sb.append(ConfigureConstant.STRING_CEZS_).append(pageOrderItemInfoArray[0].getKce()).append("。").append(orderInfo.getBz());
            orderInfo.setBz(sb.toString());
        }
        orderInfo.setKphjje(DecimalCalculateUtil.decimalFormatToString(String.valueOf(kphjje), ConfigureConstant.INT_2));
        commonOrderInfo.setOrderInfo(orderInfo);
        commonOrderInfo.setOrderItemInfo(orderItemInfos);
        return commonOrderInfo;

    }

    public static OrderQrcodeExtendInfo buildOrderQrcodeInfo(CommonOrderInfo pageToFpkjInfo) {
    
    
        Date now = new Date();
        OrderQrcodeExtendInfo orderQrcodeExtendInfo = new OrderQrcodeExtendInfo();
        orderQrcodeExtendInfo.setCreateTime(now);
        orderQrcodeExtendInfo.setFpqqlsh(pageToFpkjInfo.getOrderInfo().getFpqqlsh());
        orderQrcodeExtendInfo.setQuickResponseCodeType(OrderInfoEnum.QR_TYPE_1.getKey());
        orderQrcodeExtendInfo.setTqm(pageToFpkjInfo.getOrderInfo().getTqm());
        orderQrcodeExtendInfo.setUpdateTime(now);
        orderQrcodeExtendInfo.setXhfMc(pageToFpkjInfo.getOrderInfo().getXhfMc());
        orderQrcodeExtendInfo.setXhfNsrsbh(pageToFpkjInfo.getOrderInfo().getXhfNsrsbh());
        orderQrcodeExtendInfo.setKphjje(pageToFpkjInfo.getOrderInfo().getKphjje());
        orderQrcodeExtendInfo.setDdh(pageToFpkjInfo.getOrderInfo().getDdh());
        orderQrcodeExtendInfo.setEwmzt("0");
    
        orderQrcodeExtendInfo.setFpzlDm(pageToFpkjInfo.getOrderInfo().getFpzlDm());
        orderQrcodeExtendInfo.setZfzt("0");
        orderQrcodeExtendInfo.setCardStatus("0");
        orderQrcodeExtendInfo.setDataStatus("0");
        return orderQrcodeExtendInfo;
    
    
    }

    public static OrderOriginExtendInfo buildOriginOrder(CommonOrderInfo pageToFpkjInfo) {

        OrderOriginExtendInfo orderOrginOrder = new OrderOriginExtendInfo();
        orderOrginOrder.setCreateTime(new Date());
        orderOrginOrder.setUpdateTime(new Date());
        orderOrginOrder.setOrderId(pageToFpkjInfo.getOrderInfo().getId());
        orderOrginOrder.setFpqqlsh(pageToFpkjInfo.getOrderInfo().getFpqqlsh());
        orderOrginOrder.setOriginFpqqlsh(pageToFpkjInfo.getOrderInfo().getFpqqlsh());
        orderOrginOrder.setOriginOrderId(pageToFpkjInfo.getOrderInfo().getId());
        orderOrginOrder.setOriginDdh(pageToFpkjInfo.getOrderInfo().getDdh());
        orderOrginOrder.setXhfNsrsbh(pageToFpkjInfo.getOrderInfo().getXhfNsrsbh());
        return orderOrginOrder;
    }
    
}
