package com.dxhy.order.model.a9.sld;

import com.dxhy.order.model.a9.ResponseBaseBean;
import lombok.Getter;
import lombok.Setter;

/**
 * 受理点库存响应
 *
 * @author ZSC-DXHY
 * @date 创建时间: 2020-08-14 15:39
 */
@Getter
@Setter
public class SldKcResponse extends ResponseBaseBean {
	
	private SldKcResponseExtend result;

}
