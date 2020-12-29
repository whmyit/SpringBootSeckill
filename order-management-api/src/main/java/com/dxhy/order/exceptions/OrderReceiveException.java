package com.dxhy.order.exceptions;

import com.dxhy.order.constant.OrderInfoContentEnum;
import lombok.Getter;
import lombok.Setter;

/**
 * 订单异常公用类
 *
 * @author ZSC-DXHY
 */
@Setter
@Getter
public class OrderReceiveException extends Exception {
    private static final long serialVersionUID = 1L;
    /**
     * 返回错误code
     */
    private String code;
    /**
     * 返回错误信息
     */
    private String message;

    public OrderReceiveException() {
        super();
    }

    public OrderReceiveException(String code, String message) {
        super();
        this.code = code;
        this.message = message;
    }

    public OrderReceiveException(OrderInfoContentEnum orderInfoContentEnum) {
        super();
        this.code = orderInfoContentEnum.getKey();
        this.message = orderInfoContentEnum.getMessage();
    }
    
    
    /**
     * 定义商品编码异常信息实体
     *
     * @param orderInfoContentEnum 所有业务统一返回参数枚举
     * @param sphxh                商品行序号
     * @return OrderReceiveException
     * @author <a href="tivenninesongs@163.com">yaoxuguang</a>
     * @date 2020/4/14
     */
    public OrderReceiveException(OrderInfoContentEnum orderInfoContentEnum, String sphxh) {
        this.code = orderInfoContentEnum.getKey();
        this.message = "第" + sphxh + "行，" + orderInfoContentEnum.getMessage();
    }
}
