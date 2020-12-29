package com.dxhy.order.model.bwactivexs.kpd;

import com.dxhy.order.model.a9.ResponseBaseBean;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

/**
 * 开票点响应
 *
 * @author ZSC-DXHY
 * @date 创建时间: 2020-08-14 15:19
 */
@Setter
@Getter
public class KpdResponse extends ResponseBaseBean{
	private List<KpdXx> content;
}
