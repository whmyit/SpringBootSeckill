package com.dxhy.order.consumer.modules.manager.constant;

import lombok.Getter;

/**
 * 表格导入枚举类
 *
 * @author ZSC-DXHY
 * @date 创建时间: 2020-08-14 15:23
 */
@Getter
public enum CommodityEnum {
    
    /**
     * 企业名称
     */
    COMMODITY_ENTERPRISE_NAME("企业名称*", "enterpriseName"),
    
    COMMODITY_TAXPAYER("企业税号*", "xhfNsrsbh"),
    
    COMMODITY_GROUP_NAME("商品分组名称", "groupName"),
    
    COMMODITY_ITEM_NAME("商品名称*", "merchandiseName"),
    
    COMMODITY_ITEM_ENCODE("商品编码", "encoding"),

    COMMODITY_ITEM_TYPE("规格型号", "specificationModel"),

    COMMODITY_ITEM_UNIT("计量单位", "meteringUnit"),

    COMMODITY_ITEM_UNIT_PRICE("单价", "unitPrice"),

    COMMODITY_ITEM_DESCRIBE("描述", "description"),

    COMMODITY_ITEM_TAX_NAME("税收名称", "taxClassificationName"),

    COMMODITY_ITEM_TAX_CLASS_CODE("税收编码", "taxClassCode"),

    COMMODITY_ITEM_TAX_SIMPLE_NAME("税收简称", "taxClassAbbreviation"),

    COMMODITY_ITEM_IS_TAX_BENEFITS("享受优惠政策", "enjoyPreferentialPolicies"),

    COMMODITY_ITEM_TAX_BENEFITS_TYPE("优惠政策类型", "preferentialPoliciesType"),

    COMMODITY_ITEM_TAX_INCLUSIVE_TAG("含税价标志", "taxLogo"),

    COMMODITY_ITEM_TAX_RATE("税率", "taxRate"),

    ;


    private final String key;
    
    private final String value;


    CommodityEnum(String key, String value) {
        this.key = key;
        this.value = value;
    }


    public static CommodityEnum getCodeValue(int key) {

        for (CommodityEnum item : values()) {
            if (item.getKey().equals(key)) {
                return item;
            }
        }
        return null;
    }
}
