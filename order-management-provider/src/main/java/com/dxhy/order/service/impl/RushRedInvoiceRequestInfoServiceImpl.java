package com.dxhy.order.service.impl;

import com.dxhy.order.api.ApiRushRedInvoiceRequestInfoService;
import com.dxhy.order.constant.ConfigureConstant;
import com.dxhy.order.constant.OrderInfoContentEnum;
import com.dxhy.order.constant.OrderInfoEnum;
import com.dxhy.order.constant.OrderManagementConstant;
import com.dxhy.order.model.CommonOrderInfo;
import com.dxhy.order.model.OrderInfo;
import com.dxhy.order.model.OrderItemInfo;
import com.dxhy.order.utils.DecimalCalculateUtil;
import com.dxhy.order.utils.JsonUtils;
import com.dxhy.order.utils.MathUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.dubbo.config.annotation.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 专票冲红申请单业务处理接口
 *
 * @author ZSC-DXHY
 */
@Slf4j
@Service
public class RushRedInvoiceRequestInfoServiceImpl implements ApiRushRedInvoiceRequestInfoService {

    private static final String LOGGER_MSG = "(专票冲红申请单处理)";

    @Override
    public List<OrderItemInfo> redInvoiceMerge(List<OrderItemInfo> xmmx) {
        List<OrderItemInfo> protocolProjectBeannew = null;
        if (xmmx != null && xmmx.size() > 0) {
            List<OrderItemInfo> mxlist = new ArrayList<>();
            // 项目总金额
            double xmzje = 0.00;
            // 项目总税额
            double xmzse = 0.00;
            // 第一个折扣
            double firstzk = 0.17D;
            for (int i = 0; i < xmmx.size(); i++) {
                final OrderItemInfo xm = xmmx.get(i);
                //折扣行不做处理
                if (OrderInfoEnum.ORDER_LINE_TYPE_1.getKey().equals(xm.getFphxz())) {
                    continue;
                }
                if (!StringUtils.isBlank(xm.getXmsl()) && !xm.getXmsl().startsWith("-")) {
                    xm.setXmsl("-" + DecimalCalculateUtil.decimalFormatToString(xm.getXmsl(), ConfigureConstant.INT_8));
                }
                // 折扣行数
                int zkhs = 10000;
                // 折扣行数后面的count值
                int zkhscount = 0;
                //如果有折扣这样的字样就为true
                Boolean zks = false;
                //for循环获取折扣行的折扣率,和折扣行数的行数值,并获取折扣行的金额和税额
                for (int j = i + 1; j < xmmx.size(); j++) {
                    final OrderItemInfo eachBeanj = xmmx.get(j);
                    if (OrderInfoEnum.ORDER_LINE_TYPE_1.getKey().equals(eachBeanj.getFphxz())) {
                        zkhs = j;
                        zks = true;
                        zkhscount = 1;
                        xmzje = Math.abs(Double.parseDouble(String.valueOf(eachBeanj.getXmje())));
                        xmzse = Math.abs(Double.parseDouble(String.valueOf(eachBeanj.getSe())));
                        break;
                    }
                }


    
                if (zks && zkhs - i == zkhscount) {
                    //折扣行对应的商品行的处理
                    //单行折扣处理逻辑:把折扣行合并到被折扣行中,格式化金额和税额,反算单价
                    double xmje = Double.parseDouble(DecimalCalculateUtil.decimalFormat(MathUtil.add(String.valueOf(-1 * Math.abs(Double.parseDouble(String.valueOf(xm.getXmje())))), String.valueOf(xmzje)), 2));
                    double se = Double.parseDouble(DecimalCalculateUtil.decimalFormat(MathUtil.add(String.valueOf(-1 * Math.abs(Double.parseDouble(String.valueOf(xm.getSe())))), String.valueOf(xmzse)), 2));
    
                    if (!StringUtils.isBlank(xm.getXmdj()) && !StringUtils.isBlank(xm.getXmsl())) {
                        double xmdj = MathUtil.div(String.valueOf(-1 * Math.abs(MathUtil.add(xm.getXmje(), String.valueOf(xmzje)))), String.valueOf(xm.getXmsl()).replace("-", ""), 8);
                        xm.setXmdj(DecimalCalculateUtil.decimalFormatToString(String.valueOf(xmdj), ConfigureConstant.INT_8));
                    }
                    xm.setXmje(DecimalCalculateUtil.decimalFormatToString(String.valueOf(xmje), ConfigureConstant.INT_2));
                    xm.setSe(DecimalCalculateUtil.decimalFormatToString(String.valueOf(se), ConfigureConstant.INT_2));
                    //合并折扣行之后红票发票行性质（FPHXZ）必须为0
                    xm.setFphxz(OrderInfoEnum.ORDER_LINE_TYPE_0.getKey());
                } else if (i < zkhs - zkhscount) {
                    //非折扣行的处理
                    double xmje = Double.parseDouble(DecimalCalculateUtil.decimalFormat(-1 * Math.abs(Double.parseDouble(String.valueOf(xm.getXmje()))), 2));
                    double se = Double.parseDouble(DecimalCalculateUtil.decimalFormat(-1 * Math.abs(Double.parseDouble(String.valueOf(xm.getSe()))), 2));
                    if (!StringUtils.isBlank(xm.getXmdj())) {
                        double xmdj = Math.abs(Double.parseDouble(xm.getXmdj()));
                        xm.setXmdj(DecimalCalculateUtil.decimalFormatToString(String.valueOf(xmdj), ConfigureConstant.INT_8));
                    }
    
                    xm.setXmje(DecimalCalculateUtil.decimalFormatToString(String.valueOf(xmje), ConfigureConstant.INT_2));
                    xm.setSe(DecimalCalculateUtil.decimalFormatToString(String.valueOf(se), ConfigureConstant.INT_2));
    
    
                    firstzk = 0.00;
                }
    
                if (!StringUtils.isBlank(xm.getXmdj()) && xm.getXmdj().startsWith("-")) {
                    xm.setXmdj(xm.getXmdj().replace("-", ""));
                }
                /*
                 * 修改:折扣为100%和小于100%时，红票合并
                 */
                if (i != zkhs && firstzk <= 1) {
                    mxlist.add(xm);
                }
            }
            protocolProjectBeannew = new ArrayList<>();
            for (OrderItemInfo orderItemInfo : mxlist) {
                protocolProjectBeannew.add(orderItemInfo);
            }
        }
        return protocolProjectBeannew;
    }

    /**
     * 红票订单预处理--处理折扣行合并数据
     *
     * @param commonOrderInfo
     * @return
     */
	@Override
    public Map<String, Object> itemMerge(CommonOrderInfo commonOrderInfo) {
        log.debug("{}接收红票订单合并折扣行数据:{}", LOGGER_MSG, JsonUtils.getInstance().toJsonString(commonOrderInfo));
        Map<String, Object> checkResultMap = new HashMap<>(5);
        checkResultMap.put(OrderManagementConstant.ERRORCODE, OrderInfoContentEnum.SUCCESS.getKey());
        try {

            /**
             * 合并
             */
            List<OrderItemInfo> redInvoiceMerge = this.redInvoiceMerge(commonOrderInfo.getOrderItemInfo());
            commonOrderInfo.setOrderItemInfo(this.processRedInvoiceDetailForCheckListSumAmount(redInvoiceMerge, commonOrderInfo.getOrderInfo()));
            checkResultMap.put(OrderManagementConstant.ERRORMESSAGE, OrderInfoContentEnum.SUCCESS.getMessage());
            checkResultMap.put(OrderManagementConstant.DATA, commonOrderInfo);
            log.debug("{}红票订单合并折扣行返回数据:{}", LOGGER_MSG, JsonUtils.getInstance().toJsonString(commonOrderInfo));
        } catch (Exception e) {
            log.error("{}红票订单合并折扣行接口处理异常:{}", LOGGER_MSG, e);
            checkResultMap.put(OrderManagementConstant.ERRORCODE, OrderInfoContentEnum.INTERNAL_SERVER_ERROR.getKey());
            checkResultMap.put(OrderManagementConstant.ERRORMESSAGE, OrderInfoContentEnum.INTERNAL_SERVER_ERROR.getMessage());

        }
        return checkResultMap;
    }

    /**
     * 红票合并折扣行之后校验红票明细行累计金额之和是否和合计不含税金额相等,不相等的话,进行调整
     *
     * @param orderItemInfos
     * @param orderInfo
     * @return
     */
    @Override
    public List<OrderItemInfo> processRedInvoiceDetailForCheckListSumAmount(List<OrderItemInfo> orderItemInfos, final OrderInfo orderInfo) {//获取发票头信息中的合计不含税金额
        boolean flag = false;
        double hjbhsje = Double.parseDouble(orderInfo.getHjbhsje());
        //获取发票头信息中的合计税额
        double hjse = Double.parseDouble(orderInfo.getHjse());
        //明细行总金额
        double mxzje = 0.00;
        //明细行总税额
        double mxzse = 0.00;
        //合计不含税金额和明细总金额的差值
        double differje = 0.00;
        //合计税额和明细总税额的差值
        double differse = 0.00;
        if (orderItemInfos != null && orderItemInfos.size() > 0) {
            for (final OrderItemInfo orderItemInfo : orderItemInfos) {
                mxzje += DecimalCalculateUtil.decimalFormatToDouble(orderItemInfo.getXmje(), 2);
                mxzse += DecimalCalculateUtil.decimalFormatToDouble(orderItemInfo.getSe(), 2);
            }
            differje = Double.parseDouble(DecimalCalculateUtil.decimalFormat(Math.abs(Math.abs(hjbhsje) - Math.abs(mxzje)), 2));
            differse = Double.parseDouble(DecimalCalculateUtil.decimalFormat(Math.abs(Math.abs(hjse) - Math.abs(mxzse)), 2));
            //如果合计不含税金额和明细总金额的差值 大于0 并且税额差值大于0,进行金额调整
            if (differje != 0 || differse != 0) {
                /**
                 * 倒着循环,去商品行中做金额调整,如果当前行金额调整之后小于0,调整完成,跳出循环,如果循环到第一行,调整金额仍大于等于0 ,抛异常
                 */
                for (int i = orderItemInfos.size() - 1; i >= 0; i--) {
                    double newXmje = Double.parseDouble(orderItemInfos.get(i).getXmje()) - differje;
                    double newSe = Double.parseDouble(orderItemInfos.get(i).getSe()) - differse;
            
                    //如果当前行金额够调整,进行调整,如果不够就直接抛异常

                    if (newXmje < 0) {
                        orderItemInfos.get(i).setXmje(String.valueOf(DecimalCalculateUtil.decimalFormat(newXmje, 2)));
                        orderItemInfos.get(i).setSe(String.valueOf(DecimalCalculateUtil.decimalFormat(newSe, 2)));
                        break;
                    } else if (i == 0) {
                        if (newXmje >= 0) {
                            flag = true;
                        }
                    }
                }
            }
        }
        if (flag) {
            return null;
        }
        return orderItemInfos;
    }

}
