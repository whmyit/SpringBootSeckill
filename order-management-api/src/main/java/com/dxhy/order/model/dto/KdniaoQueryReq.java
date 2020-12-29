package com.dxhy.order.model.dto;

import com.alibaba.fastjson.annotation.JSONField;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

/**
 * 快递鸟请求
 *
 * @author ZSC-DXHY
 * @date 创建时间: 2020-08-14 15:18
 */
@Getter
@Setter
public class KdniaoQueryReq implements Serializable {
    /**
     * 快递公司编码
     */
    @JSONField(name = "ShipperCode")
    private String expCode;
    /**
     * 快递单号
     */
    @JSONField(name = "LogisticCode")
    private String expNo;
    /**
     * 订单号
     */
    @JSONField(name = "OrderCode")
    private String orderCode;
}
