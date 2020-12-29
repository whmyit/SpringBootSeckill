package com.dxhy.order.model.a9.dy;

import com.dxhy.order.model.a9.RequestBaseBean;
import lombok.Getter;
import lombok.Setter;

import java.util.List;
/**
 * 打印请求
 *
 * @author ZSC-DXHY
 * @date 创建时间: 2020-08-14 14:46
 */
@Setter
@Getter
public class DyRequest extends RequestBaseBean{
	
	private String dyjId;
	private String dyjMc;
	private String dypch;
	private String sbj;
	private String zbj;
	private String fpbs;
	private String dylx;
	private String spotKey;
	private List<DyRequestExtend> invoicePrintPackageDetailList;
	
}
