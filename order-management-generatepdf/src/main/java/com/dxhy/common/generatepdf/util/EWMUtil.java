package com.dxhy.common.generatepdf.util;

import com.aisino.pojo.QrcodeBean;
import com.aisino.qrcode.util.QrcodeUtil;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * 二维码工具类
 *
 * @author yaoxj
 * @time 2017年4月15日上午10:45:48
 */
public class EWMUtil {
	/**
	 * 生成二维码
	 * @param fpdm 发票代码
	 * @param fphm 发票号码
	 * @param hjbhsje 合计不含税金额
	 * @param kprq 开票日期
	 * @param jym 校验码
	 * @return
	 * @throws Exception
	 */
	public static String generateTwoDimCode(String fpdm, String fphm,
			String hjbhsje, Date kprq, String jym, String invoiceKindCode) throws Exception {
        
        QrcodeBean qrcodeBean = new QrcodeBean();
        qrcodeBean.setHEAD_version("01");
        switch (invoiceKindCode) {
            case "0":
                qrcodeBean.setEXPRESS_eInvoiceType("01");
                break;
            case "2":
                qrcodeBean.setEXPRESS_eInvoiceType("04");
                break;
            case "51":
                qrcodeBean.setEXPRESS_eInvoiceType("10");
                break;
            case "01":
                qrcodeBean.setEXPRESS_eInvoiceType("01");
                break;
            case "04":
                qrcodeBean.setEXPRESS_eInvoiceType("04");
                break;
            case "10":
                qrcodeBean.setEXPRESS_eInvoiceType("10");
                break;
            default:
                /**
                 * 默认电子发票
                 */
                qrcodeBean.setEXPRESS_eInvoiceType("10");
                break;
        }
        qrcodeBean.setEXPRESS_eInvoiceCode(fpdm);
        qrcodeBean.setEXPRESS_EInvoiceNo(fphm);
        
        hjbhsje = ArithUtil.fdd(hjbhsje, 2, 2);
        qrcodeBean.setEXPRESS_billingAmount(hjbhsje);
        
        SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMdd");
        String dateString = formatter.format(kprq);
        qrcodeBean.setEXPRESS_billingDate(dateString);
        qrcodeBean.setEXPRESS_checkCode(jym);
        
        return QrcodeUtil.generationQrcode(qrcodeBean);
    }
}
