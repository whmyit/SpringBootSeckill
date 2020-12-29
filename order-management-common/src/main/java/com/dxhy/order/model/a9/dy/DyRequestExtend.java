package com.dxhy.order.model.a9.dy;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
/**
 * 打印请求扩展
 *
 * @author ZSC-DXHY
 * @date 创建时间: 2020-08-14 14:46
 */
@Getter
@Setter
public class DyRequestExtend implements Serializable {
	
	private String kpzdbs;
	private String fpzlDm;
	private String fpdm;
	private String fpqh;
	private String fpzh;
	private String nsrsbh;
	private String fjh;
	private String sldId;
	
	/**
	 * c48使用参数
	 */
	private String fpqqlsh;
	/**
	 * 方格使用
	 */
	private String ddqqlsh;
	
}
