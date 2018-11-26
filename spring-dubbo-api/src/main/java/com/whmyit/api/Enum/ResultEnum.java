package com.whmyit.api.Enum;

/**
 * @Author: whmyit@163.com
 * @Description: 异常返回结果枚举
 * @Date: Created in 17:22  2018/10/30
 */
public enum ResultEnum  {
    UNKONW_ERROR(-1,"未知错误"),
    RES_PARAM_NULL(0000,"缺少请求参数"),
    SUCCESS(0,"成功"),
    UNKONW_RES_ERROR(-2, "请求错误"),
    LOG_ERROR(-1, "【系统异常】{ }"),
    RES_ERROR(-2, "【请求错误】{ }"),
    ;
    private Integer code;
    private String  msg;

    ResultEnum(Integer code,String msg){
        this.code=code;
        this.msg = msg;
    }

    public Integer getCode() {
        return code;
    }

    public String getMsg() {
        return msg;
    }
}
