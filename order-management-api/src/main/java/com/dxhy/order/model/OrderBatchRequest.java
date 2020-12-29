package com.dxhy.order.model;

import lombok.Data;

import java.io.Serializable;
import java.util.Date;
/**
 * 订单批次请求信息
 *
 * @author ZSC-DXHY
 * @date 创建时间: 2020-08-14 15:22
 */
@Data
public class OrderBatchRequest implements Serializable{
    
    /**
     *批次id
     */
    private String id;
    
    /**
     * 订单请求批次号
     */
    private String ddqqpch;
    
    /**
     * 销售方纳税人识别号
     */
    private String xhfNsrsbh;
    
    /**
     * 企业开票方式(0:自动开票;1:手动开票),默认为0
     */
    private String kpfs;
    
    /**
     * 受理点id
     */
    private String sldid;
    
    /**
     * 开票机号
     */
    private String kpjh;
    
    /**
     * 发票种类代码(0:专票 2:普票41:卷票51:电子票)
     */
    private String fpzldm;
    
    /**
     * 是否是成品油(0:非成品油;1:成品油)
     */
    private String sfcpy;
    
    /**
     * 批次状态(0:未开票;1:开票中;2:开票成功;3:开票异常)
     */
    private String status;
    
    /**
     * 返回信息
     */
    private String message;
    
    /**
     * 扩展字段
     */
    private String kzzd;
    
    /**
     * 创建时间
     */
    private Date createTime;
    
    /**
     * 修改时间
     */
    private Date updateTime;
}
