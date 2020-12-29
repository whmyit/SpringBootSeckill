package com.dxhy.order.model.a9.dy;


import com.dxhy.order.model.a9.RequestBaseBean;
import lombok.Getter;
import lombok.Setter;

/**
 *
 * @ClassName ：DyRequst
 * @Description ：打印请求bean
 * @author ：杨士勇
 * @date ：2019年7月19日 下午8:24:26
 *
 *
 */
@Setter
@Getter
public class DydListRequst extends RequestBaseBean{
	
	private String nsrsbh;
	private String dyjzt;
	private String dydMc;

}
