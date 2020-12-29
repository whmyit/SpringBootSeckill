package com.dxhy.order.model.a9.sld;

import com.dxhy.order.model.a9.RequestBaseBean;
import lombok.Getter;
import lombok.Setter;
/**
 * 查询金税盘信息请求
 *
 * @author ZSC-DXHY
 * @date 创建时间: 2020-08-14 15:29
 */
@Getter
@Setter
public class QueryJspxxV2Request extends RequestBaseBean{

	private String fjh;
	private String fpzlDm;
	private String id;
	private String nsrsbh;
	private String qysj;
	private String zdh;

}
