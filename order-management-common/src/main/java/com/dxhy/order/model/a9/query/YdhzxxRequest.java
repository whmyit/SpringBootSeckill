package com.dxhy.order.model.a9.query;

import com.dxhy.order.model.a9.RequestBaseBean;
import lombok.Getter;
import lombok.Setter;

/**
 *
 * @ClassName ：ydhzxxRequest
 * @Description ：月度汇总信息请求bean
 * @author ：杨士勇
 * @date ：2019年7月19日 下午9:43:11
 *
 *
 */
@Setter
@Getter
public class YdhzxxRequest extends RequestBaseBean{
	private String xhfNsrsbh;
	private String xhfMc;
	private String xhfDzdh;
	private String xhfYhzh;
	private String fjh;
	private String year;
	private String month;
	private String fpzlDm;
	private String xnsbh;
}
