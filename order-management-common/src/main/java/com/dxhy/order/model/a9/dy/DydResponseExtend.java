package com.dxhy.order.model.a9.dy;

import com.dxhy.order.model.a9.ResponseBaseBeanExtend;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

/**
 *
 * @ClassName ：DyResponseExtend
 * @Description ：打印接口返回参数
 * @author ：杨士勇
 * @date ：2019年7月19日 下午9:18:39
 *
 *
 */
@Setter
@Getter
public class DydResponseExtend extends ResponseBaseBeanExtend{
	
	private List<DydResponseDetail> fpdyjs;
	
}
