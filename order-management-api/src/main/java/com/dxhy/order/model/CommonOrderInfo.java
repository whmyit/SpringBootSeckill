package com.dxhy.order.model;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.util.List;


/**
 * 对接前端业务订单主体类
 *
 * @author ZSC-DXHY
 */
@Setter
@Getter
public class CommonOrderInfo implements Serializable {
    private OrderInfo orderInfo;
    private List<OrderItemInfo> orderItemInfo;
    private OrderProcessInfo processInfo;
    /**
     * 专票冲红接口,购货方已抵扣情况，有值时不走受理点和受理点名称校验
     */
    private String sjywly;
    /**
     * 开具红字增值税专用发票信息表编号
     */
    private String hzfpxxbbh;
    
    /**
     * 开票方式:0:自动开票;1:手动开票;2:静态码开票;3:动态码开票
     */
    private String kpfs;
    /**
     * 标识是购货方折扣还是冲红，有值时购货方折扣
     */
    private String flagbs;
    /**
     * 是否单税率
     */
    private boolean isSingleSl;
    /**
     * 终端标识
     */
    private String terminalCode;
    /**
     * 分机号
     */
    private String kpjh;
    /**
     * 是否是拆分后的订单 0 拆分后订单 1 合并后订单
     */
    private String isSplitOrder;
    /**
     * 原订单id
     */
    private String originOrderId;
    /**
     * 原订单processid
     */
    private String originProcessId;
    /**
     * 受理点id
     */
    private String sld;
    /**
     * 受理点名称
     */
    private String sldmc;
    
    /**
     * 批次税号校验
     */
    private String pcnsrsbh;
    
    /**
     * 是否开具发票（0是1否）
     */
    private String isMakeOutAnInvoice;
    /**
     * 用户id
     */
    private String userId;
    
    /**
     * 开票批次号
     */
    private String fpqqpch;
    
    /**
     * 开票流水号
     */
    private String kplsh;
    /**
     * 是否是异常订单编辑 1 异常订单编辑 其他正常订单编辑
     */
    private String isExceptionEdit;
}
