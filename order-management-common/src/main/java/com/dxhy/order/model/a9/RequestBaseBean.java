package com.dxhy.order.model.a9;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

/**
 * @author ：杨士勇
 * @ClassName ：RequestBaseBean
 * @Description ：请求参数基础类
 * @date ：2019年7月26日 上午9:52:15
 */
@Setter
@Getter
public class RequestBaseBean implements Serializable {
	
	private String terminalCode;
	
}
