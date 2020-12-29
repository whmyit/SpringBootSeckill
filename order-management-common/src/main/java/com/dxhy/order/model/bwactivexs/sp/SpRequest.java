package com.dxhy.order.model.bwactivexs.sp;

import com.dxhy.order.model.a9.RequestBaseBean;
import lombok.Getter;
import lombok.Setter;

import java.util.List;
/**
 * 税盘请求
 *
 * @author ZSC-DXHY
 * @date 创建时间: 2020-08-14 15:41
 */
@Getter
@Setter
public class SpRequest extends RequestBaseBean{
	private List<String> nsrsbhs;
}
