package com.dxhy.order.model;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

/**
* @Description: 商品税收信息初始化参数
* @Author:xueanna
* @Date:2019/9/23
*/
@Setter
@Getter
public class CommodityCodeInfo implements Serializable {
    /**
     * 商品id
     */
    private String id;
    /**
     * 税收编码
     */
    private String ssbm;
    
    private String xhfNsrsbh;
}
