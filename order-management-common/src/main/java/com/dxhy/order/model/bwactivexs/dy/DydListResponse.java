package com.dxhy.order.model.bwactivexs.dy;

import com.dxhy.order.model.a9.ResponseBaseBean;
import lombok.Getter;
import lombok.Setter;
/**
 * 打印点列表响应数据
 *
 * @author ZSC-DXHY
 * @date 创建时间: 2020-08-14 14:46
 */
@Setter
@Getter
public class DydListResponse extends ResponseBaseBean{
	
	private DydListResponseExtend content;
}
