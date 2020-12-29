package com.dxhy.order.constant;

import com.dxhy.order.constant.OrderMergeErrorMessageEnum;

/**
 *
 * @ClassName ：OrderMergeException
 * @Description ：订单合并自定义异常信息
 * @author ：杨士勇
 * @date ：2019年9月20日 下午2:20:26
 *
 *
 */

public class OrderMergeException extends Exception{
    
    
    private static final long serialVersionUID = 1L;
    /**
     * 返回错误code
     */
    private String code;
    /**
     * 返回错误信息
     */
    private String message;

    private Object data;


    public Object getData() {
        return data;
    }

    public void setData(Object data) {
        this.data = data;
    }

    public OrderMergeException() {
        super();
    }

    public OrderMergeException(String code, String message, Object data) {
        super();
        this.code = code;
        this.message = message;
        this.data = data;
    }
    
    public OrderMergeException(OrderMergeErrorMessageEnum orderMergeErrorMessageEnum) {
        super();
        this.code = orderMergeErrorMessageEnum.getKey();
        this.message = orderMergeErrorMessageEnum.getValue();
        this.data = "";
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
