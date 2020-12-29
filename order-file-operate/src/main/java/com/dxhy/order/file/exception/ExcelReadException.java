package com.dxhy.order.file.exception;


/**
 * 表格导入异常
 *
 * @author ZSC-DXHY
 * @date 创建时间: 2020-08-14 14:48
 */
public class ExcelReadException  extends Exception{

    /**
     * 返回错误code
     */
    private String code;
    /**
     * 返回错误信息
     */
    private String message;


    public ExcelReadException() {
        super();
    }

    public ExcelReadException(String code, String message) {
        super();
        this.code = code;
        this.message = message;
    }

    /**
     * 获取code值
     */
    public String getCode() {
        return code;
    }

    /**
     * 设置code
     */
    public void setCode(String code) {
        this.code = code;
    }

    /**
     * 获取错误信息
     */
    @Override
    public String getMessage() {
        return message;
    }

    /**
     * 设置错误信息
     */
    public void setMessage(String message) {
        this.message = message;
    }

}
