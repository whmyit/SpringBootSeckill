package com.dxhy.order.consumer.modules.supplychain.constant;

import java.util.ArrayList;
import java.util.List;

/**
 * 供应链错误信息枚举类
 *
 * @author ZSC-DXHY
 * @date 创建时间: 2020-08-14 15:43
 */
public enum SupplyChainErrorMsgEnum {
    
    
    /**
     * 供应链通用枚举类
     */
    UNKONW_ERROR("9999", "未知异常!"),
    DDH_REPEAT("9001", "订单号：%s 已存在!"),
    ORDER_INFO_NOT_EXIST("9002", "此请求流水号:%s的数据不存在!"),
    UPDATE_CHECK_STATUS_FALILD("9003", "更新订单审核状态失败!"),
    XHF_NSRSBH_NOTEXIST("9004", "订单号：%s 销方信息不存在!"),
    
    SUCCESS("0000", "处理成功"),
    ;
    
    /**
     * key
     */
    private final String key;
    
    /**
     * 值
     */
    private final String value;
    
    public String getKey() {
        return key;
    }
    
    public String getValue() {
        return value;
    }
    
    SupplyChainErrorMsgEnum(String key, String value) {
        this.key = key;
        this.value = value;
    }
    
    public static SupplyChainErrorMsgEnum getCodeValue(String key) {
        
        for (SupplyChainErrorMsgEnum item : values()) {
            if (item.getKey().equals(key)) {
                return item;
            }
        }
        return null;
    }
    
    public static List<String> getValues() {
        
        List<String> resultList = new ArrayList<>();
        for (SupplyChainErrorMsgEnum item : values()) {
            resultList.add(item.getValue());
        }
        return resultList;
    }
    
    
}
