package com.dxhy.order.model;


import com.dxhy.order.constant.OrderInfoContentEnum;
import com.dxhy.order.constant.OrderManagementConstant;

import java.util.HashMap;
import java.util.Map;

/**
 * 返回数据
 *
 * @author ZSC-DXHY
 */
public class R extends HashMap<String, Object> {
    private static final long serialVersionUID = 1L;

    public R() {
        put(OrderManagementConstant.CODE, "0000");
        put(OrderManagementConstant.MESSAGE, "success");
    }

    public R(boolean userMessage) {
        if(userMessage) {
            put(OrderManagementConstant.CODE, "0000");
            put(OrderManagementConstant.ALL_MESSAGE, "success");
        }else {
            put(OrderManagementConstant.CODE, "9999");
            put(OrderManagementConstant.ALL_MESSAGE, "failure");
        }
    }

    public static R setCodeAndMsg(OrderInfoContentEnum orderInfoContentEnum, Object data) {
        R r = new R();
        r.put(OrderManagementConstant.CODE, orderInfoContentEnum.getKey());
        r.put(OrderManagementConstant.MESSAGE, orderInfoContentEnum.getMessage());
        r.put(OrderManagementConstant.DATA, data);
        return r;
    }

    public static R error() {
        return error(OrderInfoContentEnum.INTERNAL_SERVER_ERROR.getKey(), "未知异常，请联系管理员");
    }

    public static R error(String msg) {
        return error(OrderInfoContentEnum.INTERNAL_SERVER_ERROR.getKey(), msg);
    }

    public static R error(String string, String msg) {
        R r = new R();
        r.put(OrderManagementConstant.CODE, string);
        r.put(OrderManagementConstant.MESSAGE, msg);
        return r;
    }

    public static R error(OrderInfoContentEnum orderInfoContentEnum) {
        R r = new R();
        r.put(OrderManagementConstant.CODE, orderInfoContentEnum.getKey());
        r.put(OrderManagementConstant.MESSAGE, orderInfoContentEnum.getMessage());
        return r;
    }

    public static R ok(Map<String, Object> map) {
        R r = new R();
        r.putAll(map);
        return r;
    }
    
    public static R ok(String msg) {
        return error(OrderInfoContentEnum.SUCCESS.getKey(), msg);
    }
    
    public static R ok() {
        return new R();
    }

    @Override
    public R put(String key, Object value) {
        super.put(key, value);
        return this;
    }
}
