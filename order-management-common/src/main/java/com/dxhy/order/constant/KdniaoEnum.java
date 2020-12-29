package com.dxhy.order.constant;

/**
 * 快递鸟枚举类
 *
 * @author ZSC-DXHY
 * @date 创建时间: 2020-08-14 18:15
 */
public enum KdniaoEnum {
    
    //物流状态：2-在途中,3-签收,4-问题件
    WGJ("0", "无轨迹"),
    YLJ("1", "已揽件"),
    ZTZ("2", "在途中"),
    YQS("3", "已签收"),
    WTJ("4", "问题件"),
    WQS("0", "未签收");
    
    private final String code;
    private final String msg;
    
    KdniaoEnum(String code, String msg) {
        this.code = code;
        this.msg = msg;
    }
    
    public String code() {
        return code;
    }
    
    public String msg() {
        return msg;
    }
}
