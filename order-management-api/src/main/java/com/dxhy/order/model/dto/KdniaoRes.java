package com.dxhy.order.model.dto;

import com.alibaba.fastjson.annotation.JSONField;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.util.List;

/**
 * 快递鸟响应数据
 *
 * @author ZSC-DXHY
 * @date 创建时间: 2020-08-14 18:16
 */
@Getter
@Setter
public class KdniaoRes implements Serializable {
    /**
     * 用户ID
     */
    @JSONField(name = "EBusinessID")
    private String eBusinessID;
    /**
     * 订单编号
     */
    @JSONField(name = "OrderCode")
    private String orderCode;
    /**
     * 快递公司编码
     */
    @JSONField(name = "ShipperCode")
    private String shipperCode;
    /**
     * 物流运单号
     */
    @JSONField(name = "LogisticCode")
    private String logisticCode;
    /**
     * 成功与否
     */
    @JSONField(name = "Success")
    private boolean success;
    /**
     * 物流状态：2-在途中,3-签收,4-问题件
     */
    @JSONField(name = "State")
    private String state;
    /**
     * 失败原因
     */
    @JSONField(name = "Reason")
    private String reason;
    /**
     * 追踪信息
     */
    @JSONField(name = "Traces")
    private List<KdniaoTrace> traces;
    
}
