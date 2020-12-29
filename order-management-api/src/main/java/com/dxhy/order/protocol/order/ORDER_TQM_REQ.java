package com.dxhy.order.protocol.order;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

/**
 * 订单提取码请求
 *
 * @author ZSC-DXHY
 * @date 创建时间: 2020-08-14 18:17
 */
@Setter
@Getter
public class ORDER_TQM_REQ implements Serializable{
	
	private String TQM;
	
	private String NSRSBH;
	
	private String TYPE;
	
}
