package com.dxhy.order.response;

/**
 * 通用状态
 *
 * @author ZSC-DXHY
 * @date 创建时间: 2020-08-14 15:41
 */
public class StatusCodes {
    
    /**
     * 处理成功(0000)
     */
    public static String SUCCESS = "0000";
    
    /**
     * 处理失败(0001)
     */
    public static String FAIL = "0001";
    
    /**
     * 处理异常(0002)
     */
    public static String ERROR = "0002";
    /**
     * 参数为null
     */
    public static String IS_NULL = "0003";
    /**
     * 唯一性参数重复
     */
    public static String IS_REPEAT = "0004";
    /**
     * 参数过长
     */
    public static String IS_EXCESSLENGTH = "0005";
    
    /**
     * 参数不符合规范
     */
    public static String NON_CONFORMITY = "0006";
    /**
     * 数据不存在
     */
    public static String NON_EXISTENT = "0007";
    
    /**
     * 数据不允许删除(0008)
     */
    public static String DATA_NOT_ALLOW_DELETE = "0008";
    
    /**
     * 发票数据不存在(0009)
     */
    public static String INVOICE_NOT_FOUND = "0009";
    
    /**
     * 响应数据为空(0010)
     */
    public static String RESPONSE_IS_NULL = "0010";
    
    /**
     * 参数非法(0011)
     */
    public static String IS_ILLEGAL = "0011";
}
