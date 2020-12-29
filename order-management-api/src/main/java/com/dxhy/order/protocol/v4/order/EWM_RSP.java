package com.dxhy.order.protocol.v4.order;

import com.dxhy.order.protocol.v4.RESPONSEV4;
import lombok.Getter;
import lombok.Setter;

/**
 * @author ：杨士勇
 * @ClassName ：DYNAMIC_CODE_RSP
 * @Description ：
 * @date ：2020年2月18日 上午9:23:30
 */
@Getter
@Setter
public class EWM_RSP extends RESPONSEV4 {
	
	private String DTM;
	private String TQM;
	private String SXSJ;
	
}
