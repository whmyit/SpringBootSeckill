package com.dxhy.order.service.impl;

import com.dxhy.order.api.ApiVerifyBuyerManageInfo;
import com.dxhy.order.api.IValidateInterfaceOrder;
import com.dxhy.order.constant.OrderInfoContentEnum;
import com.dxhy.order.constant.OrderInfoEnum;
import com.dxhy.order.constant.OrderManagementConstant;
import com.dxhy.order.protocol.v4.buyermanage.GMFXXCX_REQ;
import com.dxhy.order.protocol.v4.buyermanage.GMFXXTB_REQ;
import com.dxhy.order.utils.CheckParamUtil;
import com.dxhy.order.utils.GBKUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.dubbo.config.annotation.Service;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

/**
 * @author: <a href="tivenninesongs@163.com">yaoxuguang</a>
 * @createDate: Created in 2020/4/21
 */
@Service
@Slf4j
public class ApiVerifyBuyerManageInfoImpl implements ApiVerifyBuyerManageInfo {
    @Resource
    private IValidateInterfaceOrder validateInterfaceOrder;

    @Override
    public Map<String,String> checkQueryBuyerRequestParam(GMFXXCX_REQ gmfxxcxReq) {
        Map<String, String> checkResultMap = new HashMap<>(10);
        String successCode = OrderInfoContentEnum.SUCCESS.getKey();
        checkResultMap.put(OrderManagementConstant.ERRORCODE, successCode);

        //入参对象是否为null
        if(Objects.isNull(gmfxxcxReq)){
            return validateInterfaceOrder.generateErrorMap("","",
                    OrderInfoContentEnum.BUYER_MESSAGE_QUERY_NULL);
        }

        //购买方编码
        checkResultMap = CheckParamUtil.checkParam(OrderInfoContentEnum.BUYER_MESSAGE_QUERY_GMFBM_ERROR_184001,
                gmfxxcxReq.getGMFBM());
        if (!successCode.equals(checkResultMap.get(OrderManagementConstant.ERRORCODE))) {
            return checkResultMap;
        }

        //销货方纳税人识别号
        checkResultMap = validateInterfaceOrder.checkNsrsbhParam(OrderInfoContentEnum.BUYER_MESSAGE_QUERY_XHFSBH_ERROR_184002,
                OrderInfoContentEnum.BUYER_MESSAGE_QUERY_XHFSBH_ERROR_184003,
                OrderInfoContentEnum.BUYER_MESSAGE_QUERY_XHFSBH_ERROR_184004,
                gmfxxcxReq.getXHFSBH());
        if (!successCode.equals(checkResultMap.get(OrderManagementConstant.ERRORCODE))) {
            return checkResultMap;
        }
    
        //销货方纳税人名称
        checkResultMap = CheckParamUtil.checkParam(OrderInfoContentEnum.BUYER_MESSAGE_QUERY_XHFMC_ERROR_184005,
                gmfxxcxReq.getXHFMC());
        if (!successCode.equals(checkResultMap.get(OrderManagementConstant.ERRORCODE))) {
            return checkResultMap;
        }
    
        //购买方纳税人识别号
        checkResultMap = validateInterfaceOrder.checkNsrsbhParam(OrderInfoContentEnum.BUYER_MESSAGE_QUERY_ERROR_184010,
                OrderInfoContentEnum.BUYER_MESSAGE_QUERY_ERROR_184011,
                OrderInfoContentEnum.BUYER_MESSAGE_QUERY_ERROR_184012,
                gmfxxcxReq.getGMFSBH());
        if (!successCode.equals(checkResultMap.get(OrderManagementConstant.ERRORCODE))) {
            return checkResultMap;
        }
    
        /**
         * 购买方名称
         */
    
        checkResultMap = CheckParamUtil.checkParam(OrderInfoContentEnum.BUYER_MESSAGE_QUERY_GMFMC_ERROR_184006,
                gmfxxcxReq.getGMFMC());
        if (!successCode.equals(checkResultMap.get(OrderManagementConstant.ERRORCODE))) {
            return checkResultMap;
        }
        if (StringUtils.isNotBlank(gmfxxcxReq.getGMFMC()) && !gmfxxcxReq.getGMFMC().matches("^[A-Za-z0-9\\u4e00-\\u9fa5]+$")) {
            return validateInterfaceOrder.generateErrorMap("", "",
                    OrderInfoContentEnum.BUYER_MESSAGE_QUERY_GMFMC_ERROR_184007);
        }
    
        //购方页数
        checkResultMap = CheckParamUtil.checkParam(OrderInfoContentEnum.BUYER_MESSAGE_QUERY_ERROR_184013, gmfxxcxReq.getYS());
        if (!successCode.equals(checkResultMap.get(OrderManagementConstant.ERRORCODE))) {
            return checkResultMap;
        }
    
        //购方个数
        checkResultMap = CheckParamUtil.checkParam(OrderInfoContentEnum.BUYER_MESSAGE_QUERY_ERROR_184014, gmfxxcxReq.getGS());
        if (!successCode.equals(checkResultMap.get(OrderManagementConstant.ERRORCODE))) {
            return checkResultMap;
        }
    
        //当前页码数
        if (StringUtils.isBlank(gmfxxcxReq.getYS()) ||
                Integer.parseInt(gmfxxcxReq.getYS()) <= 0) {
            return validateInterfaceOrder.generateErrorMap("", "",
                    OrderInfoContentEnum.BUYER_MESSAGE_QUERY_YS_ERROR_184008);
        }
    
        //每页显示个数
        if (StringUtils.isBlank(gmfxxcxReq.getGS()) ||
                Integer.parseInt(gmfxxcxReq.getGS()) <= 0 ||
                Integer.parseInt(gmfxxcxReq.getGS()) > 100) {
            return validateInterfaceOrder.generateErrorMap("", "",
                    OrderInfoContentEnum.BUYER_MESSAGE_QUERY_GS_ERROR_184009);
        }
    
        return checkResultMap;
    }

    @Override
    public Map<String,String> checkSyncBuyerRequestParam(GMFXXTB_REQ gmfxxtbReq) {
        Map<String, String> checkResultMap = new HashMap<>(10);
        String successCode = OrderInfoContentEnum.SUCCESS.getKey();
        checkResultMap.put(OrderManagementConstant.ERRORCODE, successCode);

        //入参对象是否为null
        if(Objects.isNull(gmfxxtbReq)){
            return validateInterfaceOrder.generateErrorMap("","",
                    OrderInfoContentEnum.BUYER_MESSAGE_SYNC_NULL);
        }

        //购买方编码
        checkResultMap = CheckParamUtil.checkParam(OrderInfoContentEnum.BUYER_MESSAGE_SYNC_GMFBM_ERROR_183001,
                gmfxxtbReq.getGMFBM());
        if (!successCode.equals(checkResultMap.get(OrderManagementConstant.ERRORCODE))) {
            return checkResultMap;
        }

        //销货方纳税人识别号
        checkResultMap = validateInterfaceOrder.checkNsrsbhParam(OrderInfoContentEnum.BUYER_MESSAGE_SYNC_XHFSBH_ERROR_183002,
                OrderInfoContentEnum.BUYER_MESSAGE_SYNC_XHFSBH_ERROR_183003,
                OrderInfoContentEnum.BUYER_MESSAGE_SYNC_XHFSBH_ERROR_183004,
                gmfxxtbReq.getXHFSBH());
        if (!successCode.equals(checkResultMap.get(OrderManagementConstant.ERRORCODE))) {
            return checkResultMap;
        }

        //销货方纳税人名称
        checkResultMap = CheckParamUtil.checkParam(OrderInfoContentEnum.BUYER_MESSAGE_SYNC_XHFMC_ERROR_183005,
                gmfxxtbReq.getXHFMC());
        if (!successCode.equals(checkResultMap.get(OrderManagementConstant.ERRORCODE))) {
            return checkResultMap;
        }

        //购买方类型
        checkResultMap = CheckParamUtil.checkParam(OrderInfoContentEnum.BUYER_MESSAGE_SYNC_GMFLX_ERROR_183006,
                gmfxxtbReq.getGMFLX());
        if (!OrderInfoContentEnum.SUCCESS.getKey().equals(checkResultMap.get(OrderManagementConstant.ERRORCODE))) {
            return checkResultMap;
        }
        if (!OrderInfoEnum.GHF_QYLX_01.getKey().equals(gmfxxtbReq.getGMFLX())
                && !OrderInfoEnum.GHF_QYLX_02.getKey().equals(gmfxxtbReq.getGMFLX())
                && !OrderInfoEnum.GHF_QYLX_03.getKey().equals(gmfxxtbReq.getGMFLX())
                && !OrderInfoEnum.GHF_QYLX_04.getKey().equals(gmfxxtbReq.getGMFLX())) {
            return validateInterfaceOrder.generateErrorMap("", "",
                    OrderInfoContentEnum.BUYER_MESSAGE_SYNC_GMFLX_ERROR_183007);
        }
    
        /**
         * 购买方税号
         */
        checkResultMap = validateInterfaceOrder.checkNsrsbhParam(OrderInfoContentEnum.BUYER_MESSAGE_SYNC_GMFSBH_ERROR_183020,
                OrderInfoContentEnum.BUYER_MESSAGE_SYNC_GMFSBH_ERROR_183009,
                OrderInfoContentEnum.BUYER_MESSAGE_SYNC_GMFSBH_ERROR_183010,
                gmfxxtbReq.getGMFSBH());
        if (!successCode.equals(checkResultMap.get(OrderManagementConstant.ERRORCODE))) {
            return checkResultMap;
        }
        //购买方类型”为企业时(即01),购方税号需要必填
        if (StringUtils.equals(OrderInfoEnum.GHF_QYLX_01.getKey(), gmfxxtbReq.getGMFLX())) {
            //购买方纳税人识别号
            checkResultMap = validateInterfaceOrder.checkNsrsbhParam(OrderInfoContentEnum.BUYER_MESSAGE_SYNC_GMFSBH_ERROR_183008,
                    OrderInfoContentEnum.BUYER_MESSAGE_SYNC_GMFSBH_ERROR_183009,
                    OrderInfoContentEnum.BUYER_MESSAGE_SYNC_GMFSBH_ERROR_183010,
                    gmfxxtbReq.getGMFSBH());
            if (!successCode.equals(checkResultMap.get(OrderManagementConstant.ERRORCODE))) {
                return checkResultMap;
            }
        
        
        }
    
        //购买方名称
        checkResultMap = CheckParamUtil.checkParam(OrderInfoContentEnum.BUYER_MESSAGE_SYNC_GMFMC_ERROR_183011,
                gmfxxtbReq.getGMFMC());
        if (!successCode.equals(checkResultMap.get(OrderManagementConstant.ERRORCODE))) {
            return checkResultMap;
        }
        if (!gmfxxtbReq.getGMFMC().matches("^[A-Za-z0-9\\u4e00-\\u9fa5]+$")) {
            return validateInterfaceOrder.generateErrorMap("", "",
                    OrderInfoContentEnum.BUYER_MESSAGE_SYNC_GMFMC_ERROR_183012);
        }
    
        //购买方地址
        if (StringUtils.isNotBlank(gmfxxtbReq.getGMFDZ())) {
            checkResultMap = CheckParamUtil.checkParam(OrderInfoContentEnum.BUYER_MESSAGE_SYNC_GMFDZ_ERROR_183013,
                    gmfxxtbReq.getGMFDZ());
            if (!successCode.equals(checkResultMap.get(OrderManagementConstant.ERRORCODE))) {
                return checkResultMap;
            }
        }

        //购买方电话
        if(StringUtils.isNotBlank(gmfxxtbReq.getGMFDH())){
            checkResultMap = CheckParamUtil.checkParam(OrderInfoContentEnum.BUYER_MESSAGE_SYNC_GMFDH_ERROR_183014,
                    gmfxxtbReq.getGMFDH());
            if (!successCode.equals(checkResultMap.get(OrderManagementConstant.ERRORCODE))) {
                return checkResultMap;
            }
        }

        //地址和电话总长度超过100位
        if(StringUtils.isNotBlank(gmfxxtbReq.getGMFDZ()) && StringUtils.isNotBlank(gmfxxtbReq.getGMFDH())
          && (GBKUtil.getGBKLength(gmfxxtbReq.getGMFDZ()) + GBKUtil.getGBKLength(gmfxxtbReq.getGMFDH())) > 100){
            return validateInterfaceOrder.generateErrorMap("","",
                    OrderInfoContentEnum.BUYER_MESSAGE_SYNC_GMFDZ_GMFZH_ERROR_183015);
        }

        //购买方银行名称
        if(StringUtils.isNotBlank(gmfxxtbReq.getGMFYH())){
            checkResultMap = CheckParamUtil.checkParam(OrderInfoContentEnum.BUYER_MESSAGE_SYNC_GMFDZ_GMFDH_ERROR_183016,
                    gmfxxtbReq.getGMFYH());
            if (!successCode.equals(checkResultMap.get(OrderManagementConstant.ERRORCODE))) {
                return checkResultMap;
            }
        }

        //购买方银行账号
        if(StringUtils.isNotBlank(gmfxxtbReq.getGMFZH())){
            checkResultMap = CheckParamUtil.checkParam(OrderInfoContentEnum.BUYER_MESSAGE_SYNC_GMFDZ_GMFYH_ERROR_183017,
                    gmfxxtbReq.getGMFZH());
            if (!successCode.equals(checkResultMap.get(OrderManagementConstant.ERRORCODE))) {
                return checkResultMap;
            }
        }

        //购买方银行名称和账号总长度超过100位
        if(StringUtils.isNotBlank(gmfxxtbReq.getGMFYH()) && StringUtils.isNotBlank(gmfxxtbReq.getGMFZH())
                && (GBKUtil.getGBKLength(gmfxxtbReq.getGMFYH()) + GBKUtil.getGBKLength(gmfxxtbReq.getGMFZH())) > 100){
            return validateInterfaceOrder.generateErrorMap("","",
                    OrderInfoContentEnum.BUYER_MESSAGE_SYNC_GMFDZ_GMFYH_GMFDH_ERROR_183018);
        }
    
        //操作类型
        if (StringUtils.isNotBlank(gmfxxtbReq.getCZLX()) && !StringUtils.equals("0", gmfxxtbReq.getCZLX())
                && !StringUtils.equals("1", gmfxxtbReq.getCZLX())
                && !StringUtils.equals("2", gmfxxtbReq.getCZLX())) {
            return validateInterfaceOrder.generateErrorMap("", "",
                    OrderInfoContentEnum.BUYER_MESSAGE_SYNC_CZLX_ERROR_183019);
        }
    
        //备注
        checkResultMap = CheckParamUtil.checkParam(OrderInfoContentEnum.BUYER_MESSAGE_SYNC_ERROR_183021,
                gmfxxtbReq.getBZ());
        if (!successCode.equals(checkResultMap.get(OrderManagementConstant.ERRORCODE))) {
            return checkResultMap;
        }
    
        return checkResultMap;
    }
}
