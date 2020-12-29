package com.dxhy.order.api;

import com.dxhy.order.constant.OrderInfoContentEnum;
import com.dxhy.order.model.CommonOrderInfo;
import com.dxhy.order.model.OrderInfo;
import com.dxhy.order.protocol.v4.order.DDFPZXX;
import com.dxhy.order.protocol.v4.order.DDPCXX_REQ;

import java.util.List;
import java.util.Map;

/**
 * 校验订单开票接口
 *
 * @author ZSC-DXHY
 */
public interface IValidateInterfaceOrder {
    
    /**
     * 订单主体校验-调用购方信息校验
     *
     * @param orderInfo
     * @param kpfs
     * @param terminalCode
     * @return
     */
    Map<String, String> checkGhfParam(OrderInfo orderInfo, String kpfs, String terminalCode);
    
    /**
     * 订单主体校验-内部使用,不对外使用
     *
     * @param commonOrderInfo
     * @return
     */
    List<Map<String, String>> checkInvParam(CommonOrderInfo commonOrderInfo);
    
    /**
     * 订单税号校验-内部使用,不对外使用
     *
     * @param contentEnum
     * @param contentEnum1
     * @param contentEnum2
     * @param nsrsbh
     * @return
     */
    Map<String, String> checkNsrsbhParam(OrderInfoContentEnum contentEnum, OrderInfoContentEnum contentEnum1, OrderInfoContentEnum contentEnum2, String nsrsbh);
    
    /**
     * 订单校验返回-内部使用,不对外使用
     *
     * @param fpqqlsh
     * @param errorMsg
     * @param orderInfoContentEnum
     * @return
     */
    Map<String, String> generateErrorMap(String fpqqlsh, String errorMsg, OrderInfoContentEnum orderInfoContentEnum);
    
    /**
     * 新版本开票接口参数校验工具类
     * 支持新税控长度校验,透传terminalCode,特殊字段根据terminalCode进行判断,后期考虑扩展
     *
     * @param commonOrderReq
     * @param secretId
     * @param terminalCode
     * @return
     */
    Map<String, String> checkInterfaceParamV3(DDPCXX_REQ commonOrderReq, String secretId, String terminalCode);
    
    /**
     * 校验订单发票总信息协议bean
     *
     * @param ddfpzxx 订单发票全数据返回协议bean
     * @return java.util.Map<java.lang.String, java.lang.String>
     * @author <a href="tivenninesongs@163.com">yaoxuguang</a>
     * @date 2020/4/16
     */
    Map<String, String> checkCommonDdffzxx(DDFPZXX ddfpzxx);
    
    /**
     * 校验购方信息
     *
     * @param orderInfo
     * @param key
     * @return
     */
    Map<String, String> checkGhfInfo(OrderInfo orderInfo, String key);
    
    /**
     * 订单发票全数据：参数校验时，当以查询的数据库数据为准时，调用此接口进行设置
     *
     * @param ddfpzxx 订单发票全数据协议bean
     * @return com.dxhy.order.protocol.v4.order.DDFPZXX
     * @author <a href="tivenninesongs@163.com">yaoxuguang</a>
     * @date 2020/5/21
     */
    DDFPZXX setDatabaseValueToddfpzxx(DDFPZXX ddfpzxx);
}
