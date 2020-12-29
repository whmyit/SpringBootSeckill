package com.dxhy.order.model.entity;

import com.dxhy.invoice.protocol.sk.doto.response.Jspxxcx;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.util.List;

/**
 * 开票点管理 entity
 *
 * @author Dear
 */
@Setter
@Getter
public class InvoiceDotEntity implements Serializable {
	/**
	 * 开票点ID
	 */
	private String kpdId;
	/**
	 * 开票点名称
	 */
	private String kpdMc;
	/**
	 * 纳税人识别号
	 */
	private String nsrsbh;
	/**
	 * 机器编号
	 */
	private String jqbh;
	/**
	 * 分机号
	 */
	private String fjh;
	/**
	 * 创建人
	 */
	private String cjr;
	/**
	 * 备注
	 */
	private String bz;
	/**
	 * 纳税人识别号
	 */
	private String nsrmc;
	/**
	 * 纳税人识别号
	 */
	private String cpylx;
	private List<Jspxxcx> jspxxcxList;
	
}
