package com.dxhy.order.model;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

/**
 * @Description 集团税编参数
 * @Author xueanna
 * @Date 2019/9/17 17:29
 */
@Setter
@Getter
public class GroupTaxClassCodeParam implements Serializable {
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
    * 从第几条开始
    */
    private String offset;

    /**
    * 每页多少条
    */
    private String limit;

    /**
    * 匹配状态
    */
    private String ppzt;
    /**
     * 共享标识
     */
    private String gxbs;
    /**
     * 数据状态
     */
    private String sjzt;
    /**
     * 差异标识
     */
    private String cybs;
    /**
    * 纳税人识别号
    */
    private String nsrsbh;
    /**
    * 销方税号
    */
    private String xhfNsrsbh;

    /**
    * 组织结构id
    */
    private String deptId;

}
