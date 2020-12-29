package com.dxhy.order.model.a9.sld;

import com.dxhy.order.model.a9.ResponseBaseBeanExtend;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

/**
 *
 * @ClassName ：SldReponseBeanExtend
 * @Description ：
 * @author ：杨士勇
 * @date ：2019年7月18日 下午1:36:10
 *
 *
 */
@Setter
@Getter
public class SldReponseBeanExtend extends ResponseBaseBeanExtend{
	
	private List<SldXx> slds;
}
