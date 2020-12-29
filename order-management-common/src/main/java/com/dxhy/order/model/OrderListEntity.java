package com.dxhy.order.model;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

/**
 * 单据列表展示
 *
 * @author 陈玉航
 * @version 1.0 Created on 2018年12月12日 上午9:47:26
 */
@Getter
@Setter
public class OrderListEntity implements Serializable {
    
    /**
     * processId
     */
    private String processId;
    
    /**
     * 单据号
     */
    private String ddh;
    
    /**
     * 开票合计金额
     */
    private String kphjje;
    
    /**
     * 合计税额
     */
    private String hjse;
    
    /**
     * 订单状态（0:初始化;1:拆分后;2:合并后;3:待开具;4:开票中;5:开票成功;6.开票失败;7.冲红成功;8.冲红失败;9.冲红中;10,自动开票中）
     */
    private String ddzt;
    
    /**
     * 销货方名称
     */
    private String xhfMc;
    
    /**
     * 销货方税号
     */
    private String xfsh;
    
    /**
     * 购货方名称
     */
    private String ghfMc;
    
    /**
     * orderId
     */
    private String orderId;
    
    /**
     * 发票请求流水号
     */
    private String fpqqlsh;
    
    /**
     * 创建时间
     */
    private String createTime;
    
    /**
     * 订单批次id
     */
    private String orderBatchId;
    
    /**
     * 订单企业类型 0个人  1企业
     */
    private String ddqylx;
    
    /**
     * 清单标志0：根据项目名称字数，自动产生清单，保持目前逻辑不变 1：取清单对应票面内容字段打印到发票票面上，将项目信息 XMXX 打印到清单上。  默认为 0。1 暂不支持。
     */
    private String qdbz;
    
    /**
     * 排序字段
     */
    private String sortColumn;
    
    /**
     * 开票点名称
     */
    private String kpdmc;
    
    /**
     * 申请单号
     */
    private String sqdh;
}
