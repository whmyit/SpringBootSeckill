package com.dxhy.order.model.readyopen;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.io.Serializable;

/**
 * 商品编码为空实体类
 *
 * @author ZSC-DXHY
 */
@Setter
@Getter
@ToString
public class SPBMNullInfoVO implements Serializable {

    /**
     * 订单号
     */
    private String ddh;

    /**
     * 项目名称
     */
    private String xmmc;

    /**
     * 消息
     */
    private String message;
}
