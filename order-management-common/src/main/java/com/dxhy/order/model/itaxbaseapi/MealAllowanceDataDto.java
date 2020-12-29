package com.dxhy.order.model.itaxbaseapi;

import lombok.Getter;
import lombok.Setter;


/**
 * 发票余量内部实体
 *
 * @author ZSC-DXHY
 * @date 创建时间: 2020-08-14 15:20
 */
@Getter
@Setter
public class MealAllowanceDataDto {
    /**
    * 计费项ID
    */
    private String chargeId;
    /**
    * 计费项名称
    */
    private String chargeName;
    /**
    * 标准单价
    */
    private String normalPrice;
    /**
    * 套餐数量
    */
    private String chargeNumber;
    /**
    * 单位
    */
    private String chargeUnit;
    /**
    * 超出套餐报价
    */
    private String beyondPrice;
    /**
    * 已用数量
    */
    private String usedNumber;
    /**
    * 模板名称
    */
    private String templateName;

}
