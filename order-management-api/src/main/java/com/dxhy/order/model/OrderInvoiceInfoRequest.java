package com.dxhy.order.model;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

/**
 * activex 开票完成时候更新实体
 * @author xueanna
 */
@Setter
@Getter
public class OrderInvoiceInfoRequest implements Serializable {

    /**
     * 订单id
     */
    private String orderInfoid;
    private String processId;
    /**
     * 订单请求唯一流水号
     */
    private String ddqqlsh;

    /**
    * 机器编号
    */
    private String jqbh;

    private String ddh;
    /**
     * 校验码
     */
    private String jym;

    /**
     * 防伪码
     */
    private String fwm;

    /**
     * 二维码
     */
    private String ewm;

    /**
     * 发票代码
     */
    private String fpdm;

    /**
     * 发票号码
     */
    private String fphm;

    /**
     * 开票日期
     */
    private String kprq;

    /**
     * 发票种类代码
     */
    private String fpzldm;

    /**
     * 合计不含税金额
     */
    private String hjbhsje;

    /**
     * 开票税额
     */
    private String kpse;

    /**
     * 开票状态
     */
    private String kpzt;

    /**
    * 失败原因
    */
    private String sbyy;

    private String sld;

    private String sldMc;
}
