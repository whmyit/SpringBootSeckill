package com.dxhy.order.model.fg;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
/**
 * 方格发票详情业务bean
 *
 * @author ZSC-DXHY
 * @date 创建时间: 2020-08-14 15:09
 */
@Getter
@Setter
public class InvoiceDetailQueueEntity implements Serializable {
	/**
	 * 商品行序号(SPHXH)
	 */
	private Long itemIndex;
	/**
	 * 商品名称(XMMC)
	 */
	private String itemName;
	/**
	 * 计量单位(XMDW)
	 */
	private String unitName;
	/**
	 * 规格型号(GGXH)
	 */
	private String specificationModel;
	/**
	 * 商品数量(XMSL)
	 */
	private Double itemCount;
	/**
	 * 商品单价(XMDJ)
	 */
	private Double itemUnitCost;
	/**
	 * (ID)
	 */
	private Long detailedId;
	
	/**
	 * 商品金额(XMJE)
	 */
	private Double itemAmount;
	
	/**
	 * 项目编码(XMBM)
	 */
	private String itemCode;
	/**
	 * 含税价标志(HSJBZ)
	 */
	private String listPriceKind;
	/**
	 * 税额(SE)
	 */
	private Double listTaxAmount;
	/**
	 * 税率(SL)
	 */
	private String infoTaxRate;
	/**
	 * 税目(SM)
	 */
	private String listTaxItem;
	/**
	 * 发票行性质(FPHXZ)
	 */
	private String invoiceLineProperty;
	/**
	 * 商品编码(SPBM)
	 */
	private String commodityCode;
	/**
	 * 自行编码(ZXBM)
	 */
	private String voluntarilyCode;
	/**
	 * 优惠政策标识(YHZCBS)
	 */
	private String incentiveFlag;
	/**
	 * 零税率标识(LSLBS)
	 */
	private String zeroTaxrateFlag;
	/**
	 * 增值税特殊管理(ZZSTSGL)
	 */
	private String addtaxManager;
	
	/**
	 * 扣除额(KCE)
	 */
	private Double deductionAmount;
}
