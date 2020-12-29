package com.dxhy.order.model.a9.query;

import com.dxhy.order.model.a9.ResponseBaseBeanExtend;
import lombok.Getter;
import lombok.Setter;

import java.util.List;
/**
 * 登记信息扩展
 *
 * @author ZSC-DXHY
 * @date 创建时间: 2020-08-14 14:45
 */
@Setter
@Getter
public class DjxxResponseExtend extends ResponseBaseBeanExtend{
	
	private List<Djxx> djxxcxList;

}
