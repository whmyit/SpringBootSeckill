package com.dxhy.common.generatepdf.entity;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.Date;

/**
 * Pdf发票Model（所有信息）
 * @author yaoxj
 * @time 2017年4月17日下午1:45:18
 */
@Getter
@Setter
@ToString
public class JAR_FPQZ_KJ {
	/**
	 * 作废标志
	 * 新加作废标志 0 正常 1 作废
	 */
	private String ZFBZ;
	/**
	 * 购买方：地址、电话
	 */
	private String GMF_DZDH;
	/**
	 * 购买方：银行账号
	 */
	private String GMF_YHZH;
	/**
	 * 购买方：名称
	 */
	private String GMF_MC;
	/**
	 * 购买方：纳税人识别号
	 */
	private String GMF_NSRSBH;
	/**
	 * 价税合计
	 */
	private String JSHJ;
	/**
	 * 合计金额
	 */
	private String HJJE;
	/**
	 * 合计税额
	 */
	private String HJSE;
	/**
	 * 开票类型
	 */
	private String KPLX;
	/**
	 * 备注
	 */
	private String BZ;
	/**
	 * 开票日期
	 */
	private Date KPRQ;
	/**
	 * 开票人
	 */
	private String KPR;
	/**
	 * 收款人
	 */
	private String SKR;
	/**
	 * 复核人
	 */
	private String FHR;
	/**
	 * 销售方：银行账户
	 */
	private String XSF_YHZH;
	/**
	 * 销售方：地址、电话
	 */
	private String XSF_DZDH;
	/**
	 * 销售方：名称
	 */
	private String XSF_MC;
	/**
	 * 销售方：纳税人识别号
	 */
	private String XSF_NSRSBH;
	/**
	 * 发票密文
	 */
	private String FP_MW;
	/**
	 * 校验码
	 */
	private String JYM;
	/**
	 * 二维码
	 */
	private String EWM;
	/**
	 * 发票代码
	 */
	private String FP_DM;
	/**
	 * 发票号码
	 */
	private String FP_HM;
	/**
	 * 机器编号
	 */
	private String JQBH;
	/**
	 * 编码表版本号
	 */
	private String BMB_BBH;
	/**
	 * 清单备注(多页PDF：1，否则：0)
	 */
	private String QD_BZ = "0";
	/**
	 * 清单发票项目名称
	 */
	private String QDXMMC;
	/**
	 * 签章ID
	 */
	private String QZID;
	/**
	 * 模版代码
	 */
	private String MBDM;
	/**
	 * 模板——A5
	 */
	private byte[] MB_A5;
	/**
	 * 模板列表
	 */
	private byte[] MB_LIST;
	private String IAC;
	/**
	 * 数据来源？
	 */
	private String SJLY;
	/**
	 * 代开标志:1
	 */
	private String DKBZ;
	/**
	 * 收购标志
	 */
	private String SGBZ;
	/**
	 * 对应正数发票代码
	 */
	private String YFP_DM;
	/**
	 * 对应正数发票号码
	 */
	private String YFP_HM;
	/**
	 * 发票明细
	 */
	private JAR_FPQZ_KJMX[] JAR_FPQZ_KJMXS;
	/**
	 * 发票种类
	 */
	private String FPZL;
    
    private String printPdfWaterMark;
    
    private String printPdfWaterMarkMsg;

}
