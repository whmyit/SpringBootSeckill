package com.dxhy.order.constant;

import com.dxhy.order.response.StatusCodes;

/**
 * 通用code定义
 *
 * @author ZSC-DXHY
 * @date 创建时间: 2020-08-14 15:33
 */
public class ResponseStatusCodes extends StatusCodes {
    
    /**
     * 商品编码导入
     */
    public static String PRODUCT_PRODUCT_NAME = "3505";
    
    /**
     * 可冲红余额不足(1001)
     */
    public static String SPECIAL_INVOICE_REVERSAL_INSUFFICIENT_FUNDS = "1001";
    
    /**
     * 专票冲红原因为空(1002)
     */
    public static String SPECIAL_INVOICE_REVERSAL_REASON_IS_BLANK = "1002";
    
    /**
     * 发票代码号码是空(1003)
     */
    public static String INVOICE_CODE_AND_NO_IS_BLANK = "1003";
    
    /**
     * 销方信息是空(1004)
     */
    public static String SELLER_INFO_IS_BLANK = "1004";
    
    /**
     * 购方信息是空(1005)
     */
    public static String BUYER_INFO_IS_BLANK = "1005";
    
    /**
     * 开票点是空(1006)
     */
    public static String ACCESS_POINT_ID_IS_BLANK = "1006";
    
    /**
     * 当前提交状态不允许修改(1007)
     */
    public static String CURRENT_SUBMIT_STATUS_NOT_ALLOW_EDIT = "1007";
    
    /**
     * 申请单编号为空(1008)
     */
    public static String SPECIAL_INVOICE_REVERSAL_CODE_IS_BLANK = "1008";
    
    /**
     * 发票信息验证失败(1009)
     */
    public static String INVOICE_VALIDATION_FAIL = "1009";
    
    /**
     * 发票剩余可冲红金额不足(1010)
     */
    public static String INVOICE_REMAIN_REVERSAL_AMOUNT_INSUFFICIENT = "1010";
    
    /**
     * 同步红色申请单信息失败(1011)
     */
    public static String SYNC_SPECIAL_INVOICE_REVERSAL_FAIL = "1011";
    
    /**
     * 红色申请单信息不存在(1012)
     */
    public static String SPECIAL_INVOICE_REVERSAL_NOT_FOUND = "1012";
    
    /**
     * 发票已开具(1013)
     */
    public static String INVOICE_ALREADY_EXIST = "1013";
    
    /**
     * 发票开具请求已提交(1014)
     */
    public static String INVOICE_ALREADY_SUBMIT = "1014";
    
    /**
     * 红色申请单未审核通过(1015)
     */
    public static String SPECIAL_INVOICE_REVERSAL_NOT_AUDITED_PRASSED = "1015";
    
    /**
     * 红色申请单已经审核通过(1016)
     */
    public static String SPECIAL_INVOICE_REVERSAL_ALREADY_AUDITED_PRASSED = "1016";
    
    /**
     * 红色申请单商品信息为空(1017)
     */
    public static String SPECIAL_INVOICE_REVERSAL_ITEM_IS_NULL = "1017";
    
    /**
     * 红色申请单商品金额无效(1018)
     */
    public static String SPECIAL_INVOICE_REVERSAL_ITEM_AMOUNT_ILLEGAL = "1018";
    
    /**
     * 原发票信息非法，只能为纸票、专票(1019)
     */
    public static String SPECIAL_INVOICE_REVERSAL_INVOICE_IS_ILLEGAL = "1019";
    
    /**
     * 红色申请单不允许包含税(1020)
     */
    public static String SPECIAL_INVOICE_REVERSAL_ITEM_NOT_ALLOWED_INCLUDE_TAX = "1020";
    
    /**
     * 红色申请单商品税率为空，不允许有多条明细(1021)
     */
    public static String SPECIAL_INVOICE_REVERSAL_ITEM_TAX_RATE_IS_NULL_NOT_ALLOWED_MULTIPLE_ITEM = "1021";
    
    /**
     * 红色申请单商品税率为空，单价和数量必须为空(1022)
     */
    public static String SPECIAL_INVOICE_REVERSAL_ITEM_TAX_RATE_IS_NULL_UNIT_PRICE_AND_QUANTITY_MUST_IS_NULL = "1022";
    
    /**
     * 开票点信息未找到(1023)
     */
    public static String ACCESS_POINT_NOT_FOUND = "1023";
    
    /**
     * 开票人信息未找到(1024)
     */
    public static String DRAWER_NOT_FOUND = "1024";
    
    /**
     * 金税盘信息未找到(2501)
     */
    public static String TAX_PLATE_NOT_FOUND = "2501";
    
    /**
     * 征税期不允许退回发票(2503)
     */
    public static String LOCKUP_PERIOD_NOT_ALLOWED_RETURNED = "2503";
    
    /**
     * 客户信息未维护
     */
    public static String CUSTOMER_INFORMATION_NOT_MAINTAINED = "2505";
    
    /**
     * 客户信息不全面
     */
    public static String CUSTOMER_NOT_COMPEREHENSIVE = "2504";
    
    /**
     * 红字申请单已开具
     */
    public static String HZSQDYKJ = "2506";

    public static final String  SPECIAL_INVOICE_REVERSAL_AMOUNT_ERROR = "2507";

}
