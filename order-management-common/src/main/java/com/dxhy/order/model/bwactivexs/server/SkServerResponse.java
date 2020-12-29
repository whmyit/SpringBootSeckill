package com.dxhy.order.model.bwactivexs.server;

import com.dxhy.order.model.a9.ResponseBaseBean;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

/**
 * 开票点查询响应
 *
 * @author ZSC-DXHY
 * @date 创建时间: 2020-08-14 15:37
 */
@Setter
@Getter
public class SkServerResponse extends ResponseBaseBean{
	
	private List<SkServeXx> content;
	
}
