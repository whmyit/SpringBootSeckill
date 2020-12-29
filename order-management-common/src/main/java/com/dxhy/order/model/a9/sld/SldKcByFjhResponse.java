package com.dxhy.order.model.a9.sld;

import com.dxhy.order.model.a9.ResponseBaseBean;
import lombok.Getter;
import lombok.Setter;

/**
 * 受理点库存信息查询
 *
 * @author ZSC-DXHY
 * @date 创建时间: 2020-08-14 15:37
 */
@Setter
@Getter
public class SldKcByFjhResponse extends ResponseBaseBean{
	
	private SldKcByFjhResponseExtend result;

}
