package com.dxhy.order.consumer.openapi.service;


import com.dxhy.order.exceptions.OrderReceiveException;
import com.dxhy.order.model.*;
import com.dxhy.order.model.protocol.Result;
import com.dxhy.order.protocol.v4.order.DDPCXX_REQ;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;

/**
 * @Description: 订单对外接口业务层接口
 * @author: chengyafu
 * @date: 2018年8月13日 下午4:48:28
 */
public interface ICommonInterfaceService {
    
    /**
     * 统计校验
     *
     * @param interfaceVersion
     * @param interfaceName
     * @param timestamp
     * @param nonce
     * @param secretId
     * @param signature
     * @param encryptCode
     * @param zipCode
     * @param content
     * @return
     */
    Result checkInterfaceParam(String interfaceVersion, String interfaceName, String timestamp, String nonce, String secretId, String signature, String encryptCode, String zipCode, String content);
    
    /**
     * 统一鉴权方法
     *
     * @return
     */
    Result auth(String timeStamp
            ,String nonce
            ,String secretId
            ,String reqSign
            ,String encryptCode
            ,String zipCode
            ,String content);
    
    /**
     * 商品明细数据补全商品名称简码
     *
     * @param orderItemInfo
     * @param xhfNsrsbh
     * @param qdbz
     * @param terminal
     * @throws OrderReceiveException
     */
    void dealOrderItem(List<OrderItemInfo> orderItemInfo, String xhfNsrsbh, String qdbz, String terminal) throws OrderReceiveException;
    
    /**
     * 处理受理点
     * 获取sldid,当sldid为-1时,先调用querySld接口,获取所有受理点列表
     * 再根据sldid 调用selectSldJspxx接口 获取对应的分机号
     * 最后分别赋值sldid（sldid）,kpjh（fjh）
     *
     * @param sldid
     * @param fpzldm
     * @param nsrsbh
     * @param qdbz
     * @param terminal
     * @return
     */
    R dealWithSldStartV3(String sldid, String fpzldm, String nsrsbh, String qdbz, String terminal);
    
    /**
     * 校验数据是否重复
     *
     * @param ddpcxxReq
     * @return
     */
    R checkOrderInfoIsRepeat(DDPCXX_REQ ddpcxxReq);
    
    /**
     * 校验A9受理点
     *
     * @param ddpcxxReq
     * @param sldid
     * @param terminal
     * @return
     */
    R checkSldInfoA9(DDPCXX_REQ ddpcxxReq, String sldid, String terminal);
    
    /**
     * 业务类型信息采集
     *
     * @param ywlx
     * @param nsrsbh
     * @param xhfmc
     * @return
     */
    String yesxInfoCollect(String ywlx, String nsrsbh, String xhfmc);
    
    
    /**
     * 构建基础订单数据,主要处理的数据为:订单处理表,订单处理扩展表,订单发票表,
     * 订单表和订单明细表数据需要预先维护
     * 注意:订单类型,订单来源,订单状态等需要外层自动补全
     * 根据枚举类型判断对应业务,然后判断生成对应表数据.
     *
     * @param orderInfo
     * @param orderItemInfoList
     * @param orderProcessInfo
     * @param orderInvoiceInfo
     */
    void buildInsertOrderData(OrderInfo orderInfo, List<OrderItemInfo> orderItemInfoList, OrderProcessInfo orderProcessInfo, OrderInvoiceInfo orderInvoiceInfo);
    
    /**
     * 查询套餐余量
     *
     * @param taxpayerCode
     * @return
     */
    com.dxhy.order.model.R mealAllowance(String taxpayerCode);
    
    /**
     * 查询流水号状态
     *
     * @param ddqqlsh
     * @return
     */
    String getDdqqlshRedisStatus(String ddqqlsh);
    
    /**
     * 添加流水号状态
     *
     * @param ddqqlsh
     * @param status
     */
    void setDdqqlshRedisStatus(String ddqqlsh, String status);
}
