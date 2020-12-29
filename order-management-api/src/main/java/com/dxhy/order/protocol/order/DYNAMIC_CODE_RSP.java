package com.dxhy.order.protocol.order;

import lombok.Getter;
import lombok.Setter;

/**
 * @author ：杨士勇
 * @ClassName ：DYNAMIC_CODE_RSP
 * @date ：2020年2月18日 上午9:23:30
 */
@Getter
@Setter
public class DYNAMIC_CODE_RSP {
	
	private String STATUS_CODE;
	private String STATUS_MESSAGE;
	private String DYNAMIC_CODE_URL;
	private String TQM;
	private String DISABLED_TIME;
	
}
