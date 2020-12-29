package com.dxhy.order.model.entity;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

/**
 * 描述信息:本地库存
 *
 * @author 谢元强
 * @date Created on 2018-12-10
 */
@Getter
@Setter
public class OilEntity implements Serializable {
    /**
     * 纳税人识别号
     */
    private String nsrsbh;
    /**
     * 纳税人名称
     */
    private String nsrmc;
    /**
     * 分机号
     */
    private String fjh;
    /**
     * 成品油商品和服务税收分类编码
     */
    private String spbm;
    /**
     * 成品油商品和服务名称
     */
    private String spbmmc;
    /**
     * 数量，单位为升
     */
    private String sl;
    
}
