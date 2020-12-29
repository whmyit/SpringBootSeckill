package com.dxhy.order.model.a9.query;

import com.dxhy.order.model.a9.ResponseBaseBean;
import lombok.Getter;
import lombok.Setter;

/**
 * 纳税人登记信息响应
 *
 * @author ZSC-DXHY
 * @date 创建时间: 2020-08-14 14:44
 */
@Setter
@Getter
public class DjxxResponse extends ResponseBaseBean{
	
	private DjxxResponseExtend result;

}
