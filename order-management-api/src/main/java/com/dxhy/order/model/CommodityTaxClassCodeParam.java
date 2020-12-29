package com.dxhy.order.model;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

/**
* @Description: 商品同步集团税编信息
* @Author:xueanna
* @Date:2019/9/23
*/
@Setter
@Getter
public class CommodityTaxClassCodeParam implements Serializable {
    /**
    * 商品分组id
    */
    private String groupId;
    /**
    * 商品名称
    */
    private String spmc;
    /**
    * 商品编码
    */
    private String spbm;
    /**
    * 税收名称
    */
    private String ssmc;
    /**
    * 税收编码
    */
    private String ssbm;
    /**
    * 税收简称
    */
    private String ssjc;
    /**
    * 商品id
    */
    private String id;
}
