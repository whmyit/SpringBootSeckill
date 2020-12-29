package com.dxhy.order.file.common;/**
 * Created by thinkpad on 2020-06-12.
 */

/**
 * @ClassName ExcelImportErrorMessageEnum
 * @Author ysy
 * @Date 2020-06-12 16:15
 **/
public enum ExcelImportErrorMessageEnum {

    /**
     * 校验成功
     */
    SUCCESSCODE("0000", "处理成功"),
    /**
     * 备注
     */
    RECEIVE_DATA_FAILD("9999", "数据校验失败"),
    /**
     * 文件类型错误
     */
    ORDERINFO_FILEPREFIX_ERROR_9100("9100", "文件类型错误"),
    /**
     * 订单导入Excel读取错误
     */
    ORDERINFO_EXCEL_READERROR_9101("9101", "excel读取错误"),
    /**
     * excel表头不能超过100
     */
    ORDERINFO_HEAD_OVERLIMIT_1902("9102", "文件类型错误"),
    /**
     * excel表头不能为空
     */
    ORDERINFO_HEAD_OVERLIMIT_1903("9103", "第%s列excel表头不能为空"),
    /**
     * excel模板错误
     */
    ORDERINFO_TEMPLATE_ERROR_9104("9104", "excel模板错误"),

    ORDERINFO_PARAM_ERROR_9105("9105", "excel导出参数错误!"),

    ORDERINFO_PARAM_NOT_SUPPORT_9106("9106", "excel实体中的参数类型只能为String!"),



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

    ExcelImportErrorMessageEnum(String key, String value) {
        this.key = key;
        this.value = value;
    }

    public static ExcelImportErrorMessageEnum getCodeValue(String key) {

        for (ExcelImportErrorMessageEnum item : values()) {
            if (item.getKey().equals(key)) {
                return item;
            }
        }
        return null;
    }
}
