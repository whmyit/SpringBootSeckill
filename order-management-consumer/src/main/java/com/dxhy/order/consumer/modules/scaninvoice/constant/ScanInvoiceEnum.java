package com.dxhy.order.consumer.modules.scaninvoice.constant;

/**
 * @ClassName ScanInvoiceEnum
 * @Author ysy
 * @Date 2020-05-05 16:39
 **/
public enum ScanInvoiceEnum {


    /**
     * 授权订单号前缀
     */
    INVOICE_SCAN_TYPE_01("01", "静态码"),
    INVOICE_SCAN_TYPE_02("02", "动态码"),
    HAVEN_GENERATE("1001","发票已开具"),
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

    ScanInvoiceEnum(String key, String value) {
        this.key = key;
        this.value = value;
    }

    public static ScanInvoiceEnum getCodeValue(int key) {

        for (ScanInvoiceEnum item : values()) {
            if (item.getKey().equals(key)) {
                return item;
            }
        }
        return null;
    }


}
