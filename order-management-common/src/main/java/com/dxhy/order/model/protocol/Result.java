package com.dxhy.order.model.protocol;

import com.dxhy.order.constant.ConfigurerInfo;

import java.util.HashMap;
import java.util.Map;

/**
 * 返回数据
 *
 * @author csf
 */
public class Result extends HashMap<String, Object> {
    
    private static final long serialVersionUID = 1L;
    
    public Result() {
    }
    
    
    public static Result error(ResponseStatus responseStatus) {
        Result r = new Result();
        r.put(ConfigurerInfo.RESPONSESTATUS, responseStatus);
        return r;
    }
    
    
    public static Result error(ResponseStatus responseStatus, ResponseData responseData) {
        Result r = new Result();
        r.put(ConfigurerInfo.RESPONSEDATA, responseData);
        r.put(ConfigurerInfo.RESPONSESTATUS, responseStatus);
        return r;
    }
    
    public static Result ok(ResponseStatus responseStatus) {
        Result r = new Result();
        r.put(ConfigurerInfo.RESPONSESTATUS, responseStatus);
        return r;
    }
    
    public static Result ok(ResponseStatus responseStatus, ResponseData responseData) {
        Result r = new Result();
        r.put(ConfigurerInfo.RESPONSEDATA, responseData);
        r.put(ConfigurerInfo.RESPONSESTATUS, responseStatus);
        return r;
    }
    
    public static Result ok(Map<String, Object> map) {
        Result r = new Result();
        r.putAll(map);
        return r;
    }
    
    public static Result ok() {
        return new Result();
    }
    
    public static Result ok(String contet) {
        Result r = new Result();
        ResponseData responseData = new ResponseData("0", "0", contet);
        ResponseStatus responseStatus = new ResponseStatus("0000", "API调用成功");
        r.put(ConfigurerInfo.RESPONSEDATA, responseData);
        r.put(ConfigurerInfo.RESPONSESTATUS, responseStatus);
        return r;
    }
    
    @Override
    public Result put(String key, Object value) {
        super.put(key, value);
        return this;
    }
}
