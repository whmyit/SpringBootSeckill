package com.dxhy.order.constant;

import com.dxhy.order.constant.OrderSplitErrorMessageEnum;
/**
 * 订单拆分异常
 *
 * @author ZSC-DXHY
 * @date 创建时间: 2020-08-14 15:25
 */
public class OrderSplitException extends Exception {

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

	public OrderSplitException() {
		super();
	}

	public OrderSplitException(String code, String message, Object data) {
		super();
		this.code = code;
		this.message = message;
		this.data = data;
	}

	public OrderSplitException(String code, String message) {
		super();
		this.code = code;
		this.message = message;
	}

	public OrderSplitException(OrderSplitErrorMessageEnum orderSplitErrorMessageEnum) {
		super();
		this.code = orderSplitErrorMessageEnum.getKey();
		this.message = orderSplitErrorMessageEnum.getValue();
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
