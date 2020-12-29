package com.dxhy.order.protocol.order;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
/**
 * 提取码明细信息
 *
 * @author ZSC-DXHY
 * @date 创建时间: 2020-08-14 15:45
 */
@Getter
@Setter
public class TQM_ORDER_ITEM_IFNO implements Serializable {
    
    private String XMXH;
    private String FPHXZ;
    private String SPBM;
    private String ZXBM;
    private String YHZCBS;
    private String LSLBS;
    private String ZZSTSGL;
    private String XMMC;
    private String GGXH;
	private String DW;
	private String XMSL;
	private String XMDJ;
	private String XMJE;
	private String HSBZ;
	private String SL;
	private String SE;
	private String KCE;
	private String BYZD1;
	private String BYZD2;
	private String BYZD3;

}
