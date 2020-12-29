package com.dxhy.order.model.bwactivexs.dy;

import com.dxhy.order.model.a9.RequestBaseBean;
import lombok.Getter;
import lombok.Setter;

import java.util.List;
/**
 * 打印点请求信息
 *
 * @author ZSC-DXHY
 * @date 创建时间: 2020-08-14 14:45
 */
@Setter
@Getter
public class DydListRequest extends RequestBaseBean{
	private List<String> nsrsbhs;
}
