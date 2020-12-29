package com.dxhy.order.model.dto;

import com.alibaba.fastjson.annotation.JSONField;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

/**
 * 快递鸟物流
 *
 * @author ZSC-DXHY
 * @date 创建时间: 2020-08-14 18:16
 */
@Getter
@Setter
public class KdniaoTrace implements Serializable {
    /**
     * 时间
     */
    @JSONField(name = "AcceptTime")
    private String acceptTime;
    /**
     * 描述
     */
    @JSONField(name = "AcceptStation")
    private String acceptStation;
    /**
     * 备注
     */
    @JSONField(name = "Remark")
    private String remark;
}
