package com.dxhy.order.model;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.util.Date;


/**
 * 订单处理-业务bean
 *
 * @author zsc
 */
@Setter
@Getter
public class OrderProcessInfo implements Serializable {
    /**
     * 订单处理表主键
     */
    private String id;

    /**
     * 订单表id
     */
    private String orderInfoId;
    
    /**
     * 订单请求批次号
     */
    private String ddqqpch;
    
    /**
     * 发票请求流水号
     */
    private String fpqqlsh;
    
    /**
     * 订单号
     */
    private String ddh;
    
    /**
     * 提取码
     */
    private String tqm;
    
    /**
     * 开票合计金额
     */
    private String kphjje;
    
    /**
     * 合计不含税金额
     */
    private String hjbhsje;

    /**
     * 开票税额
     */
    private String kpse;

    /**
     * 发票种类代码
     */
    private String fpzlDm;

    /**
     * 购货方名称
     */
    private String ghfMc;

    /**
     * 购货方纳税人识别号
     */
    private String ghfNsrsbh;

    /**
     * 开票项目
     */
    private String kpxm;

    /**
     * 订单创建时间
     */
    private Date ddcjsj;

    /**
     * 订单类型
     */
    private String ddlx;

    /**
     * 订单状态
     */
    private String ddzt;

    /**
     * 订单来源
     */
    private String ddly;

    /**
     * 业务类型(区分企业业务线),可以企业自定义
     */
    private String ywlx;
    
    /**
     * 业务类型Id
     */
    private String ywlxId;
    
    /**
     * 企业开票方式(0:自动开票;1:手动开票;2:静态码开票;3:动态码开票),默认为0
     */
    private String kpfs;

    /**
     * 失败原因
     */
    private String sbyy;

    /**
     * 订单是否有效状态
     */
    private String orderStatus;
    /**
     * 异常订单编辑状态
     */
    private String editStatus;
    /**
     * 审核状态
     */
    private String checkStatus;
    /**
     * 审核时间
     */
    private Date checkTime;
    /**
     * 创建时间
     */
    private Date createTime;

    /**
     * 修改时间
     */
    private Date updateTime;

    /**
     * 销货方纳税人识别号
     */
    private String xhfNsrsbh;
    
    /**
     * 销货方名称
     */
    private String xhfMc;
    /**
     * 方格数据状态
     */
    private String fgStatus;
    /**
     * 接口协议类型 0：http 1:webservice
     */
    private String protocolType;

}
