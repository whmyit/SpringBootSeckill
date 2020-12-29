package com.dxhy.order.constant;

import java.util.ArrayList;
import java.util.List;

/**
 * 纳税人队列枚举类
 *
 * @author ZSC-DXHY
 */
public enum NsrQueueEnum {
    
    /**
     * 纳税人队列数据
     */
    FPKJ_MESSAGE("0", "order_fpkj_message"),
    YXTS_MESSAGE("1", "order_yxts_message"),
    INVALID_MESSAGE("2", "order_invalid_message"),
    DELAY_MESSAGE("3", "order_delay_message"),
    PUSH_MESSAGE("4", "order_push_message"),
    INSERT_CARD_MESSAGE("5", "insert_card_message"),

    /**
     * 上传下载回推
     */
    UPLOAD_DOWNLOAD_MESSAGE("51", "upload_download_message"),
    /**
     * 打印回推
     */
    PRINT_MESSAGE("52", "print_message");
    
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
    
    NsrQueueEnum(String key, String value) {
        this.key = key;
        this.value = value;
    }
    
    public static NsrQueueEnum getCodeValue(String key) {
        
        for (NsrQueueEnum item : values()) {
            if (item.getKey().equals(key)) {
                return item;
            }
        }
        return null;
    }
    
    public static List<String> getValues() {
        
        List<String> resultList = new ArrayList<>();
        for (NsrQueueEnum item : values()) {
            resultList.add(item.getValue());
        }
        return resultList;
    }
    
    
}
