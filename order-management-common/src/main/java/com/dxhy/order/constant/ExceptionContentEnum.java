package com.dxhy.order.constant;
/**
 * 异常枚举
 *
 * @author ZSC-DXHY
 * @date 创建时间: 2020-08-14 14:49
 */
public enum ExceptionContentEnum {
    
    /**
     * 所有业务统一返回参数
     * 规范:
     * 格式:key使用6位字符串表示,"000000"
     * CHECK_ISS7PRI_107006("107006", "批次信息中纳税人识别号长度只有15位，17位，18位，20位，其他长度不合法"),
     * 前两位字符串代表的业务场景,
     */
    
    
    /**
     * 统配适应
     */
    
    SUCCESS("0000", "请求成功"),
    SUCCESS_1000("1000", "订单数据获取成功"),
    HZSQD_UPLOAD_ERROR_999999("999999", "红字申请单上传结果解析异常"),
    QUERY_INVOICE_ERROR_999999("999999", "开票结果解析异常"),
    QUERY_INVOICE_ERROR1_999999("999999", "开票结果查询解析异常"),
    
    /**
     * 方格调用底层接口异常
     */
    FG_INVOICE_ERROR_9990("9990", "调用方格底层接口异常"),
    
    SUCCESS_000000("000000", "数据成功");
    
    /**
     * key值
     */
    private final String key;
    
    /**
     * 对应Message
     */
    private final String message;
    
    public static ExceptionContentEnum getCodeValue(String key) {
        for (ExceptionContentEnum item : values()) {
            if (item.getKey().equals(key)) {
                return item;
            }
        }
        return null;
    }
    
    
    ExceptionContentEnum(String key, String message) {
        this.key = key;
        this.message = message;
    }
    
    public String getKey() {
        return this.key;
    }
    
    public String getMessage() {
        return this.message;
    }
    
}
