package com.dxhy.order.service.impl;

import com.dxhy.order.api.ApiVerifyCommodityCode;
import com.dxhy.order.api.IValidateInterfaceOrder;
import com.dxhy.order.constant.ConfigureConstant;
import com.dxhy.order.constant.OrderInfoContentEnum;
import com.dxhy.order.constant.OrderInfoEnum;
import com.dxhy.order.constant.OrderManagementConstant;
import com.dxhy.order.protocol.v4.commodity.SPXXCX_REQ;
import com.dxhy.order.protocol.v4.commodity.SPXXTB_REQ;
import com.dxhy.order.utils.CheckParamUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.dubbo.config.annotation.Service;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

/**
 * @author: <a href="tivenninesongs@163.com">yaoxuguang</a>
 * @createDate: Created in 2020/4/21
 */
@Service
@Slf4j
public class ApiVerifyCommodityCodeImpl implements ApiVerifyCommodityCode {
    @Resource
    private IValidateInterfaceOrder validateInterfaceOrder;

    @Override
    public Map<String, String> checkQueryCommodityRequestParam(SPXXCX_REQ spxxcxReq) {
        Map<String, String> checkResultMap = new HashMap<>(10);
        String successCode = OrderInfoContentEnum.SUCCESS.getKey();
        checkResultMap.put(OrderManagementConstant.ERRORCODE, successCode);
    
        //入参对象是否为null
        if (Objects.isNull(spxxcxReq)) {
            return validateInterfaceOrder.generateErrorMap("", "",
                    OrderInfoContentEnum.COMMODITY_MESSAGE_QUERY_NULL);
        }
    
        //商品ID校验
        checkResultMap = CheckParamUtil.checkParam(OrderInfoContentEnum.COMMODITY_MESSAGE_QUERY_ERROR_174007, spxxcxReq.getSPID());
        if (!successCode.equals(checkResultMap.get(OrderManagementConstant.ERRORCODE))) {
            return checkResultMap;
        }
    
        //销货方纳税人识别号
        checkResultMap = validateInterfaceOrder.checkNsrsbhParam(OrderInfoContentEnum.COMMODITY_MESSAGE_QUERY_XHFSBH_ERROR_174001,
                OrderInfoContentEnum.COMMODITY_MESSAGE_QUERY_XHFSBH_ERROR_174002,
                OrderInfoContentEnum.COMMODITY_MESSAGE_QUERY_XHFSBH_ERROR_174003,
                spxxcxReq.getXHFSBH());
        if (!successCode.equals(checkResultMap.get(OrderManagementConstant.ERRORCODE))) {
            return checkResultMap;
        }
    
        //销货方纳税人名称
        checkResultMap = CheckParamUtil.checkParam(OrderInfoContentEnum.COMMODITY_MESSAGE_QUERY_XHFMC_ERROR_174004, spxxcxReq.getXHFMC());
        if (!successCode.equals(checkResultMap.get(OrderManagementConstant.ERRORCODE))) {
            return checkResultMap;
        }
    
        //项目名称
        checkResultMap = CheckParamUtil.checkParam(OrderInfoContentEnum.COMMODITY_MESSAGE_QUERY_ERROR_174010, spxxcxReq.getXMMC());
        if (!successCode.equals(checkResultMap.get(OrderManagementConstant.ERRORCODE))) {
            return checkResultMap;
        }
    
        //商品页数
        checkResultMap = CheckParamUtil.checkParam(OrderInfoContentEnum.COMMODITY_MESSAGE_QUERY_ERROR_174008, spxxcxReq.getYS());
        if (!successCode.equals(checkResultMap.get(OrderManagementConstant.ERRORCODE))) {
            return checkResultMap;
        }
    
        //商品个数
        checkResultMap = CheckParamUtil.checkParam(OrderInfoContentEnum.COMMODITY_MESSAGE_QUERY_ERROR_174009, spxxcxReq.getGS());
        if (!successCode.equals(checkResultMap.get(OrderManagementConstant.ERRORCODE))) {
            return checkResultMap;
        }
    
        //当前页码数
        if (StringUtils.isBlank(spxxcxReq.getYS()) ||
                Integer.parseInt(spxxcxReq.getYS()) <= 0) {
            return validateInterfaceOrder.generateErrorMap("", "",
                    OrderInfoContentEnum.COMMODITY_MESSAGE_QUERY_YS_ERROR_174005);
        }
    
        //每页显示个数
        if (StringUtils.isBlank(spxxcxReq.getGS()) ||
                Integer.parseInt(spxxcxReq.getGS()) <= 0 ||
                Integer.parseInt(spxxcxReq.getGS()) > 100) {
            return validateInterfaceOrder.generateErrorMap("", "",
                    OrderInfoContentEnum.COMMODITY_MESSAGE_QUERY_GS_ERROR_174006);
        }
        return checkResultMap;
    }

    @Override
    public Map<String, String> checkSyncCommodityRequestParam(SPXXTB_REQ spxxtbReq) {
        Map<String, String> checkResultMap = new HashMap<>(10);
        String successCode = OrderInfoContentEnum.SUCCESS.getKey();
        checkResultMap.put(OrderManagementConstant.ERRORCODE, successCode);

        /*
         * 入参对象是否为null
         */
        if (Objects.isNull(spxxtbReq)) {
            return validateInterfaceOrder.generateErrorMap("", "",
                    OrderInfoContentEnum.COMMODITY_MESSAGE_SYNC_NULL);
        }

        /*
         * 商品对应的ID
         */
        checkResultMap = CheckParamUtil.checkParam(OrderInfoContentEnum.COMMODITY_MESSAGE_SYNC_SPID_ERROR_173001, spxxtbReq.getSPID());
        if (!successCode.equals(checkResultMap.get(OrderManagementConstant.ERRORCODE))) {
            return checkResultMap;
        }

        /*
         * 销货方纳税人识别号
         */
        checkResultMap = validateInterfaceOrder.checkNsrsbhParam(OrderInfoContentEnum.COMMODITY_MESSAGE_SYNC_XHFSBH_ERROR_173002,
                OrderInfoContentEnum.COMMODITY_MESSAGE_SYNC_XHFSBH_ERROR_173003,
                OrderInfoContentEnum.COMMODITY_MESSAGE_SYNC_XHFSBH_ERROR_173004,
                spxxtbReq.getXHFSBH());
        if (!successCode.equals(checkResultMap.get(OrderManagementConstant.ERRORCODE))) {
            return checkResultMap;
        }

        /*
         * 销货方纳税人名称
         */
        checkResultMap = CheckParamUtil.checkParam(OrderInfoContentEnum.COMMODITY_MESSAGE_SYNC_XHFMC_ERROR_173005,
                spxxtbReq.getXHFMC());
        if (!successCode.equals(checkResultMap.get(OrderManagementConstant.ERRORCODE))) {
            return checkResultMap;
        }
    
        //商品税收分类编码
        checkResultMap = CheckParamUtil.checkParam(OrderInfoContentEnum.COMMODITY_MESSAGE_SYNC_SPBM_ERROR_173006,
                spxxtbReq.getSPBM());
        if (!successCode.equals(checkResultMap.get(OrderManagementConstant.ERRORCODE))) {
            return checkResultMap;
        }
    
        //自行编码
        checkResultMap = CheckParamUtil.checkParam(OrderInfoContentEnum.COMMODITY_MESSAGE_SYNC_ERROR_173026,
                spxxtbReq.getZXBM());
        if (!successCode.equals(checkResultMap.get(OrderManagementConstant.ERRORCODE))) {
            return checkResultMap;
        }
    
        /*
         * 优惠政策标识
         */
        checkResultMap = CheckParamUtil.checkParam(OrderInfoContentEnum.COMMODITY_MESSAGE_SYNC_YHZCBS_ERROR_173007,
                spxxtbReq.getYHZCBS());
        if (!OrderInfoContentEnum.SUCCESS.getKey().equals(checkResultMap.get(OrderManagementConstant.ERRORCODE))) {
            return checkResultMap;
        }
        //优惠政策标识只能为0或1,0:不使用,1:使用
        if (!OrderInfoEnum.YHZCBS_0.getKey().equals(spxxtbReq.getYHZCBS()) &&
                !OrderInfoEnum.YHZCBS_1.getKey().equals(spxxtbReq.getYHZCBS())) {
            return validateInterfaceOrder.generateErrorMap("", "",
                    OrderInfoContentEnum.COMMODITY_MESSAGE_SYNC_YHZCBS_ERROR_173008);
        }
        //优惠政策标识为1时;
        if (ConfigureConstant.STRING_1.equals(spxxtbReq.getYHZCBS())) {
            if (StringUtils.isBlank(spxxtbReq.getZZSTSGL())) {
                return validateInterfaceOrder.generateErrorMap("", "",
                        OrderInfoContentEnum.COMMODITY_MESSAGE_SYNC_YHZCBS_ERROR_173009);
            }
            //订单明细信息中YHZCBS(优惠政策标识)为1, 且税率为0, 则LSLBS只能根据实际情况选择"0或1或2"中的一种, 不能选择3, 且ZZSTSGL内容也只能写与0/1/2对应的"出口零税/免税/不征税
            if (!StringUtils.isBlank(spxxtbReq.getSL()) && ConfigureConstant.STRING_0.equals(spxxtbReq.getSL())
                    && !OrderInfoEnum.LSLBS_0.getKey().equals(spxxtbReq.getLSLBS())
                    && !OrderInfoEnum.LSLBS_1.getKey().equals(spxxtbReq.getLSLBS())
                    && !OrderInfoEnum.LSLBS_2.getKey().equals(spxxtbReq.getLSLBS())
                    && (StringUtils.isBlank(spxxtbReq.getZZSTSGL()))) {
                return validateInterfaceOrder.generateErrorMap("", "",
                        OrderInfoContentEnum.COMMODITY_MESSAGE_SYNC_YHZCBS_ERROR_173010);
            }
        }
        if (OrderInfoEnum.YHZCBS_0.getKey().equals(spxxtbReq.getYHZCBS())) {
            if (!StringUtils.isBlank(spxxtbReq.getZZSTSGL())) {
                return validateInterfaceOrder.generateErrorMap("", "",
                        OrderInfoContentEnum.COMMODITY_MESSAGE_SYNC_YHZCBS_ERROR_173011);
            }
        }
    
        /*
         * 订单明细信息-零税率标识
         */
        if (!StringUtils.isBlank(spxxtbReq.getLSLBS()) && !OrderInfoEnum.LSLBS_0.getKey().equals(spxxtbReq.getLSLBS())
                && !OrderInfoEnum.LSLBS_1.getKey().equals(spxxtbReq.getLSLBS())
                && !OrderInfoEnum.LSLBS_2.getKey().equals(spxxtbReq.getLSLBS())
                && !OrderInfoEnum.LSLBS_3.getKey().equals(spxxtbReq.getLSLBS())) {
            return validateInterfaceOrder.generateErrorMap("", "",
                    OrderInfoContentEnum.COMMODITY_MESSAGE_SYNC_LSLBS_ERROR_173012);
        }
    
        /*
         * 增值税特殊管理
         */
        checkResultMap = CheckParamUtil.checkParam(OrderInfoContentEnum.COMMODITY_MESSAGE_SYNC_ERROR_173031,
                spxxtbReq.getZZSTSGL());
        if (!OrderInfoContentEnum.SUCCESS.getKey().equals(checkResultMap.get(OrderManagementConstant.ERRORCODE))) {
            return checkResultMap;
        }
    
        /*
         * 项目名称
         */
        checkResultMap = CheckParamUtil.checkParam(OrderInfoContentEnum.COMMODITY_MESSAGE_SYNC_XMMC_ERROR_173013,
                spxxtbReq.getXMMC());
        if (!OrderInfoContentEnum.SUCCESS.getKey().equals(checkResultMap.get(OrderManagementConstant.ERRORCODE))) {
            return checkResultMap;
        }

        /*
         * 含税标志
         */
        checkResultMap = CheckParamUtil.checkParam(OrderInfoContentEnum.COMMODITY_MESSAGE_SYNC_HSBZ_ERROR_173014,
                spxxtbReq.getHSBZ());
        if (!OrderInfoContentEnum.SUCCESS.getKey().equals(checkResultMap.get(OrderManagementConstant.ERRORCODE))) {
            return checkResultMap;
        }
        //含税标志只能为0和1：0表示都不含税,1表示都含税
        if (!OrderInfoEnum.HSBZ_1.getKey().equals(spxxtbReq.getHSBZ())
                && !OrderInfoEnum.HSBZ_0.getKey().equals(spxxtbReq.getHSBZ())) {
            return validateInterfaceOrder.generateErrorMap("", "",
                    OrderInfoContentEnum.COMMODITY_MESSAGE_SYNC_HSBZ_ERROR_173015);
        }
    
        /*
         * 税率
         */
        checkResultMap = CheckParamUtil.checkParam(OrderInfoContentEnum.COMMODITY_MESSAGE_SYNC_ERROR_173030,
                spxxtbReq.getSL());
        if (!OrderInfoContentEnum.SUCCESS.getKey().equals(checkResultMap.get(OrderManagementConstant.ERRORCODE))) {
            return checkResultMap;
        }
    
        /*
         * 税率
         */
        if (StringUtils.isBlank(spxxtbReq.getSL())) {
            return validateInterfaceOrder.generateErrorMap("", "",
                    OrderInfoContentEnum.COMMODITY_MESSAGE_SYNC_SL_ERROR_173016);
        } else {
            /*
             * 增值税特殊管理不为空,不为不征税,不为免税,不为出口零税逻辑处理
             * 如果是按5%简易征收需要保证税率为0.05
             * 如果是按3%简易征收需要保证税率为0.03
             * 如果是简易征收需要保证税率为0.03或0.04或0.05
             * 如果是按5%简易征收减按1.5%计征需要保证税率为0.015
             */
            boolean result = (!spxxtbReq.getSL().contains(ConfigureConstant.STRING_POINT) && !ConfigureConstant.STRING_0.equals(spxxtbReq.getSL())) || spxxtbReq.getSL().contains(ConfigureConstant.STRING_PERCENT);
            if (result) {
                return validateInterfaceOrder.generateErrorMap("", "",
                        OrderInfoContentEnum.COMMODITY_MESSAGE_SYNC_ERROR_173032);
            }
            if ((!StringUtils.isBlank(spxxtbReq.getZZSTSGL())) &&
                    (!ConfigureConstant.STRING_BZS.equals(spxxtbReq.getZZSTSGL())) &&
                    (!ConfigureConstant.STRING_MS.equals(spxxtbReq.getZZSTSGL())) &&
                    (!ConfigureConstant.STRING_CKLS.equals(spxxtbReq.getZZSTSGL()))) {
                if (spxxtbReq.getZZSTSGL().contains(ConfigureConstant.STRING_ERROR_PERCENT)) {
                    return validateInterfaceOrder.generateErrorMap("", "",
                            OrderInfoContentEnum.COMMODITY_MESSAGE_SYNC_ERROR_173033);
                }
    
                switch (spxxtbReq.getZZSTSGL()) {
                    case ConfigureConstant.STRING_JYZS5:
                        if (!ConfigureConstant.STRING_005.equals(spxxtbReq.getSL())) {
                            return validateInterfaceOrder.generateErrorMap("", "",
                                    OrderInfoContentEnum.COMMODITY_MESSAGE_SYNC_SL_ERROR_173017);
                        }
                        break;
                    case ConfigureConstant.STRING_JYZS3:
                        if (!ConfigureConstant.STRING_003.equals(spxxtbReq.getSL())) {
                            return validateInterfaceOrder.generateErrorMap("", "",
                                    OrderInfoContentEnum.COMMODITY_MESSAGE_SYNC_SL_ERROR_173018);
                        }
                        break;
                    case ConfigureConstant.STRING_JYZS:
                        if (!ConfigureConstant.STRING_003.equals(spxxtbReq.getSL()) ||
                                !ConfigureConstant.STRING_004.equals(spxxtbReq.getSL()) ||
                                !ConfigureConstant.STRING_005.equals(spxxtbReq.getSL())) {
                            return validateInterfaceOrder.generateErrorMap("", "",
                                    OrderInfoContentEnum.COMMODITY_MESSAGE_SYNC_SL_ERROR_173019);
                        }
                        break;
                    case ConfigureConstant.STRING_JYZS5_1:
                        if (!ConfigureConstant.STRING_0015.equals(spxxtbReq.getSL())) {
                            return validateInterfaceOrder.generateErrorMap("", "",
                                    OrderInfoContentEnum.INVOICE_MX_INFO_SL_ERROR_144086);
                        }
                        break;
                    default:
                        break;
                }
            }
            //零税率标识不为空,税率必须为0
            if ((!StringUtils.isBlank(spxxtbReq.getLSLBS())) &&
                    (ConfigureConstant.DOUBLE_PENNY_ZERO != new BigDecimal(spxxtbReq.getSL()).doubleValue())) {
                return validateInterfaceOrder.generateErrorMap("", "",
                        OrderInfoContentEnum.COMMODITY_MESSAGE_SYNC_SL_ERROR_173021);
            }
            //零税率标识为空,税率不能为0
            if ((StringUtils.isBlank(spxxtbReq.getLSLBS())) &&
                    (new BigDecimal(ConfigureConstant.DOUBLE_PENNY_ZERO).doubleValue() == new BigDecimal(spxxtbReq.getSL()).doubleValue())) {
                return validateInterfaceOrder.generateErrorMap("", "",
                        OrderInfoContentEnum.COMMODITY_MESSAGE_SYNC_SL_ERROR_173022);
            }
        }
        //订单明细信息中零税率标识为0/1/2, 但增值税特殊管理内容不为'出口零税/免税/不征税';
        boolean result1 = StringUtils.isBlank(spxxtbReq.getZZSTSGL()) &&
                (OrderInfoEnum.LSLBS_0.getKey().equals(spxxtbReq.getLSLBS()) ||
                        OrderInfoEnum.LSLBS_1.getKey().equals(spxxtbReq.getLSLBS()) ||
                        OrderInfoEnum.LSLBS_2.getKey().equals(spxxtbReq.getLSLBS()));
        if (result1) {
            return validateInterfaceOrder.generateErrorMap("", "",
                    OrderInfoContentEnum.COMMODITY_MESSAGE_SYNC_SL_ERROR_173023);
        }
        if (OrderInfoEnum.LSLBS_0.getKey().equals(spxxtbReq.getLSLBS()) &&
                !ConfigureConstant.STRING_CKLS.equals(spxxtbReq.getZZSTSGL())) {
            return validateInterfaceOrder.generateErrorMap("", "",
                    OrderInfoContentEnum.COMMODITY_MESSAGE_SYNC_SL_ERROR_173023);
        }
        if (OrderInfoEnum.LSLBS_1.getKey().equals(spxxtbReq.getLSLBS()) &&
                !ConfigureConstant.STRING_MS.equals(spxxtbReq.getZZSTSGL())) {
            return validateInterfaceOrder.generateErrorMap("", "",
                    OrderInfoContentEnum.COMMODITY_MESSAGE_SYNC_SL_ERROR_173023);
        }
        if (OrderInfoEnum.LSLBS_2.getKey().equals(spxxtbReq.getLSLBS()) &&
                !ConfigureConstant.STRING_BZS.equals(spxxtbReq.getZZSTSGL())) {
            return validateInterfaceOrder.generateErrorMap("", "",
                    OrderInfoContentEnum.COMMODITY_MESSAGE_SYNC_SL_ERROR_173023);
        }
        boolean result2 = OrderInfoEnum.LSLBS_3.getKey().equals(spxxtbReq.getLSLBS())
                && (!StringUtils.isBlank(spxxtbReq.getZZSTSGL())
                || !(OrderInfoEnum.YHZCBS_0.getKey().equals(spxxtbReq.getYHZCBS())));
        if (result2) {
            return validateInterfaceOrder.generateErrorMap("", "",
                    OrderInfoContentEnum.COMMODITY_MESSAGE_SYNC_SL_ERROR_173024);
        }
    
        /**
         * 规格型号
         */
        checkResultMap = CheckParamUtil.checkParam(OrderInfoContentEnum.COMMODITY_MESSAGE_SYNC_ERROR_173027,
                spxxtbReq.getGGXH());
        if (!OrderInfoContentEnum.SUCCESS.getKey().equals(checkResultMap.get(OrderManagementConstant.ERRORCODE))) {
            return checkResultMap;
        }
    
        /**
         * 单位
         */
        checkResultMap = CheckParamUtil.checkParam(OrderInfoContentEnum.COMMODITY_MESSAGE_SYNC_ERROR_173028,
                spxxtbReq.getDW());
        if (!OrderInfoContentEnum.SUCCESS.getKey().equals(checkResultMap.get(OrderManagementConstant.ERRORCODE))) {
            return checkResultMap;
        }
    
        /**
         * 单价
         */
        checkResultMap = CheckParamUtil.checkParam(OrderInfoContentEnum.COMMODITY_MESSAGE_SYNC_ERROR_173029,
                spxxtbReq.getDJ());
        if (!OrderInfoContentEnum.SUCCESS.getKey().equals(checkResultMap.get(OrderManagementConstant.ERRORCODE))) {
            return checkResultMap;
        }
    
        /**
         * 规格型号
         */
        checkResultMap = CheckParamUtil.checkParam(OrderInfoContentEnum.COMMODITY_MESSAGE_SYNC_ERROR_173027,
                spxxtbReq.getGGXH());
        if (!OrderInfoContentEnum.SUCCESS.getKey().equals(checkResultMap.get(OrderManagementConstant.ERRORCODE))) {
            return checkResultMap;
        }
        //操作类型
        boolean result3 = StringUtils.isBlank(spxxtbReq.getCZLX())
                || (!StringUtils.equals("0", spxxtbReq.getCZLX())
                && !StringUtils.equals("1", spxxtbReq.getCZLX())
                && !StringUtils.equals("2", spxxtbReq.getCZLX()));
        if (result3) {
            return validateInterfaceOrder.generateErrorMap("", "",
                    OrderInfoContentEnum.COMMODITY_MESSAGE_SYNC_CZLX_ERROR_173025);
        }
        return checkResultMap;
    }
    
}
