package com.dxhy.order.rabbitplugin.util;

/**
 * rabbitmq,监听状态
 *
 * @author ZSC-DXHY
 */

public enum LisenerStauts {
    
    /**
     * 监听存在
     */
    isExit(1, "监听已经存在"),
    
    /**
     * 设置
     */
    success(0, "设置成功");
    
    private final int code;
    private final String msg;
    
    LisenerStauts(int code, String msg) {
        this.code = code;
        this.msg = msg;
    }
    
    public int getCode() {
        return code;
    }
    
    public String getMsg() {
        return msg;
    }
}
