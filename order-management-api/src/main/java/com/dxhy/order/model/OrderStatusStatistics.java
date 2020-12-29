package com.dxhy.order.model;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.util.Date;

/**
 * 订单统计-业务bean
 *
 * @author ZSC-DXHY
 * @date 创建时间: 2020-08-14 15:26
 */
@Setter
@Getter
public class OrderStatusStatistics implements Serializable {
    /**
     * 订单统计表主键
     */
    private String id;

    /**
     * 订单号
     */
    private String ddh;

    /**
     * 订单时间
     */
    private Date ddrq;

    /**
     * 接受时间
     */
    private Date jssj;

    /**
     *  发票类型 0.增值税专票 2 增值税普通纸质发票 51 增值税普通电子发票
     */
    private String fpzldm;

    /**
     *  购方名称
     */
    private String ghfmc;

    /**
     * 购方税号
     */
    private  String ghfnsrsbh;

    /**
     * 开票金额
     */
    private String kphjje;

    /**
     * 开票税额
     */
    private String kpse;

    /**
     * 开票状态 (0:初始化;1:开票中;2:开票成功;3:开票失败;)
     */
    private String kpzt;

    /**
     * 开票失败原因
     */
    private String sbyy;

    /**
     * 发票推送状态 0 未推送 1 推送成功 2推送失败
     */
    private String pushstatus;

    /**
     * 销货方名称
     */
    private String xhfMc;
}
