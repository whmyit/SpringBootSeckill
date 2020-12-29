package com.dxhy.order.consumer.model;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

/**
 * @author ：杨士勇
 * @ClassName ：Ydxx
 * @Description :发票汇总表
 * @date ：2020年1月19日 上午10:02:22
 */
@Getter
@Setter
public class Ydxx {
	
	
	private String fjh;
	private String fpfpfs;
	private String fpzlDm;
	private String fsfpkjfs;
	private String fsfpzffs;
	private String gjfpfs;
	private Hjjes hjjes;
	private String nsrsbh;
	private String qckcfs;
	private String qmkcfs;
	private String shfpfs;
	private String ssqj;
	private String thfpfs;
	
	private List<Yhzxx> yhzxxs;
	
	private String zbrq;
	private String zsfpkjfs;
	private String zsfpzffs;
}
