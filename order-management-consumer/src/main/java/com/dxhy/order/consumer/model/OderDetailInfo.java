package com.dxhy.order.consumer.model;

import com.dxhy.order.model.OrderItemInfo;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;
import java.util.List;

/**
 * 订单详情信息
 *
 * @author ZSC-DXHY
 */
@Setter
@Getter
public class OderDetailInfo {


    private String orderId;

    private String orderProcessId;

    private String fpqqlsh;

    private String ddzt;
    
    private String ddly;

    private Date ddcjsj;

    private String ddh;

    private String parentDd;

    private String sonDd;

    private String fpzlDm;

    private String ywlx;
    
    private String ywlxId;

    private String sbyy;

    private String ghfMc;

    private String ghfNsrsbh;

    private String ghfDz;

    private String ghfDh;

    private String ghfYh;

    private String ghfZh;
    
    private String xhfMc;
    
    private String xhfNsrsbh;
    
    private String xhfDz;
    
    private String xhfDh;
    
    private String xhfYh;
    
    private String xhfZh;

    private String ghfQylx;

    private String ghfSj;

    private String ghfEmail;

    private String kphjje;

    private String mdh;

    private String se;

    private String kplx;

    /**
     * 发票代码
     */
    private String fpdm;

    private String jym;

    private String kprq;

    /**
     * 发票号码
     */
    private String fphm;

    /**
     * 开票流水号
     */
    private String kplsh;

    /**
     * 开票项目
     */
    private String kpxm;

    /**
     * 合计不含税金额
     */
    private String hjbshje;
    /**
     * 是否展示 0 是 1否
     */
    private String orderStatus;

    /**
     * 开票人
     */
    private String kpr;

    /**
     * 复核人
     */
    private String fhr;

    /**
     * 收款人
     */
    private String skr;

    /**
     * 备注
     */
    private String bz;

    /**
     * 冲红原因
     */
    private String chyy;
    /**
     * 清单标志
     */
    private String qdbz;
    /**
     * 审核状态
     */
    private String checkStatus;
    /**
     * 审核时间
     */
    private String checkTime;


    private List<OrderItemInfo> orderItemInfo;

    private List<PageOrderExt> pageOrderExts;


}
