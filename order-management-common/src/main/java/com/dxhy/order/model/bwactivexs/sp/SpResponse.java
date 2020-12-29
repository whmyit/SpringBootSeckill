package com.dxhy.order.model.bwactivexs.sp;

import com.dxhy.order.model.a9.ResponseBaseBean;
import lombok.Getter;
import lombok.Setter;

import java.util.List;
/**
 * 税盘响应信息
 *
 * @author ZSC-DXHY
 * @date 创建时间: 2020-08-14 15:41
 */
@Setter
@Getter
public class SpResponse extends ResponseBaseBean{
	private List<SpXx> content;
}
