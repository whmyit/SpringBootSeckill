package com.dxhy.order.consumer.generateinvoice;

import com.aisino.certreq.Position;
import com.aisino.certreq.SignPdf;
import com.dxhy.common.generatepdf.PDFBuilder;
import com.dxhy.common.generatepdf.constant.PdfTemplateEnum;
import com.dxhy.common.generatepdf.entity.JAR_FPQZ_KJ;
import com.dxhy.common.generatepdf.entity.JAR_FPQZ_KJMX;
import com.dxhy.common.generatepdf.exception.CustomException;
import com.dxhy.common.generatepdf.util.InvoiceGenUtil;
import com.dxhy.common.generatepdf.util.ValidateUtil;
import com.dxhy.order.constant.ConfigureConstant;
import com.dxhy.order.constant.OrderInfoEnum;
import com.dxhy.order.constant.OrderManagementConstant;
import com.dxhy.order.consumer.config.SystemConfig;
import com.dxhy.order.model.*;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.*;

/**
 * pdf生成入口类
 * @author yaoxj
 * @time 2017年4月26日上午11:39:27
 * test
 */
@Slf4j
public class PDFProducer {
	

	/**
	 * pdf 模板拼装
	 *
	 * @param jarFpqzKj
	 * @return
	 */
	public static Map<String, Object> buildPdf(JAR_FPQZ_KJ jarFpqzKj) {
		Map qzMap = new HashMap(5);
		byte[] fileByte = null;
		int qdsize = 0;
		try {
			Map pdfMap = PDFBuilder.bulidPdfA5(jarFpqzKj);
			if (null == pdfMap) {
				qzMap.put(OrderManagementConstant.CODE, Integer.valueOf(1092));
				qzMap.put("msg", "生成PDF为空");
				qzMap.put("pdfFile", fileByte);
				log.error("生成PDF为空");
                return qzMap;
            }
			fileByte = (byte[]) pdfMap.get("fileByte");
			qdsize = ((Integer) pdfMap.get("qdsize")).intValue();
			if ((null == fileByte) || (fileByte.length == 0)) {
                qzMap.put(OrderManagementConstant.CODE, Integer.valueOf(1092));
                qzMap.put("msg", "生成PDF为空");
                qzMap.put("pdfFile", fileByte);
                log.error("生成PDF为空");
                return qzMap;
            }
		} catch (CustomException e) {
            qzMap.put(OrderManagementConstant.CODE, Integer.valueOf(e.getCode()));
            qzMap.put("msg", e.getMessage());
            qzMap.put("exception", e);
            qzMap.put("pdfFile", fileByte);
            log.error("异常:{}", e);
            return qzMap;
        } catch (Throwable e) {
            qzMap.put(OrderManagementConstant.CODE, Integer.valueOf(9991));
            qzMap.put("msg", "生成PDF失败：" + e);
            qzMap.put("exception", e);
            qzMap.put("pdfFile", fileByte);
            log.error("异常:{}", e);
            return qzMap;
        }
        qzMap.put(OrderManagementConstant.CODE, Integer.valueOf(1000));
        qzMap.put("msg", "生成PDF成功");
        qzMap.put("qdsize", Integer.valueOf(qdsize));
        qzMap.put("pdfFile", fileByte);
        return qzMap;
    }

	/**
	 * pdf签章
	 * @param fileByte
	 * @param sealId
	 * @param qdsize
	 * @return
	 */
	public static Map<String, Object> signPdf(byte[] fileByte, String sealId,
											  int qdsize) {
        Map qzMap = new HashMap(5);
        try {
            fileByte = sign(fileByte, sealId, qdsize);
        } catch (Throwable e) {
            e.printStackTrace();
            qzMap.put(OrderManagementConstant.CODE, Integer.valueOf(9992));
            qzMap.put("msg", "签章失败：" + e);
            qzMap.put("exception", e);
            qzMap.put("pdfFile", fileByte);
            return qzMap;
        }
        qzMap.put(OrderManagementConstant.CODE, Integer.valueOf(1000));
        qzMap.put("msg", "签章成功");
        qzMap.put("pdfFile", fileByte);
        return qzMap;
    }
	/**
	 * 签章
	 * @param pdf
	 * @param sealId
	 * @param qdsize
	 * @return
	 * @throws Throwable
	 */
	public static byte[] sign(byte[] pdf, String sealId, int qdsize)
			throws Throwable {
		return signList(pdf, sealId, InvoiceGenUtil.QZPAGEINDEX,
				InvoiceGenUtil.QZLEFT, InvoiceGenUtil.QZTOP,
				InvoiceGenUtil.QZRIGHT, InvoiceGenUtil.QZBOTTOM, "sign",
				"location", qdsize);
	}

	/**
	 * 签章list
	 * @param pdf
	 * @param sealId
	 * @param page
	 * @param left
	 * @param top
	 * @param width
	 * @param height
	 * @param reason
	 * @param location
	 * @param qdsize
	 * @return
	 * @throws Throwable
	 */
	private static byte[] signList(byte[] pdf, String sealId, int page,
								   int left, int top, int width, int height, String reason,
								   String location, int qdsize) throws Throwable {
		Position[] s = new Position[qdsize + 1];
		s[0] = new Position(1, left, top, width, height);
		for (int i = 1; i <= qdsize; i++) {
			s[i] = new Position(i + 1, left - 392, top + 22, width, height);
		}
		return SignPdf.signEx(pdf, sealId, page, (float)left, (float)top, (float)width, (float)height,
				reason, location, s);
	}

	/**
	 * pdf生成
	 * @param jar_fpqz_kj
	 * @return
	 */
	public static Map<String, Object> pdfProduce(JAR_FPQZ_KJ jar_fpqz_kj) {
        Map<String, Object> qzMap = new HashMap<>(5);
        byte[] pdfFile = null;
        int qdsize = 0;
        try {
            ValidateUtil.validatePdf(jar_fpqz_kj);
        } catch (CustomException e) {
            qzMap.put(OrderManagementConstant.CODE, Integer.valueOf(e.getCode()));
            qzMap.put("msg", e.getMessage());
            log.error("异常信息为：{}", e);
            qzMap.put("exception", e);
            return qzMap;
        } catch (Throwable e) {
            qzMap.put(OrderManagementConstant.CODE, Integer.valueOf(9991));
            log.error("异常信息为：{}", e);
            qzMap.put("msg", "生成PDF失败：" + e);
            qzMap.put("exception", e);
            return qzMap;
        }
        qzMap = buildPdf(jar_fpqz_kj);
        int code = ((Integer) qzMap.get(OrderManagementConstant.CODE)).intValue();
        if (code == 1000) {
            pdfFile = (byte[]) qzMap.get("pdfFile");
            qdsize = ((Integer) qzMap.get("qdsize")).intValue();
            // qzMap = signPdf(pdfFile, jar_fpqz_kj.getQZID(), qdsize);
        } else {
	        qzMap.put(OrderManagementConstant.CODE, Integer.valueOf(code));
	        qzMap.put("msg", qzMap.get("msg"));
	        qzMap.put("exception", qzMap.get("exception"));
	        return qzMap;
        }
		return qzMap;
	}

	/**
	 * @Title: PDFProducer createPDF
	 * @Description: 创建发票版式文件
	 * @param dqbm 地区编码 , invoice 发票数据
	 * @return
	 * @throws IOException
	 * @throws ParseException
	 */

	/**
	 * 转意发票种类
	 * @param fpzl
	 * @return
	 */
	private static String formatFPZL(String fpzl){
		switch (fpzl) {
			case "0":
				return "z";
			case "2":
				return "p";
			case "12":
				return "j";
			case "51":
				return "d";
			default:
				return null;
		}

	}


	/**
	 * 测试入口-main
	 * @param args
	 * @throws IOException
	 */
	public static void test(String[] args) throws IOException {
		JAR_FPQZ_KJ jar_fpqz_kj = new JAR_FPQZ_KJ();

//		jar_fpqz_kj.setGMF_DZDH("北京市海淀区数码大厦A座30层");
		jar_fpqz_kj.setGMF_DZDH("中国建设银行 111133141115211322中国建设银行 111133141115211322");
//		jar_fpqz_kj.setGMF_YHZH("海淀区中关村南大街59号18楼  021-39328876");
		jar_fpqz_kj.setGMF_YHZH("中国建设银行 111133141115211322中国建设银行 111133141115211322");
		jar_fpqz_kj.setGMF_MC("大象慧云信息技术有限公司");
		jar_fpqz_kj.setGMF_NSRSBH("91110108MA004CPN95");
		jar_fpqz_kj.setJSHJ("-16.48");
		jar_fpqz_kj.setHJJE("-16.48");
		jar_fpqz_kj.setHJSE("0");
		jar_fpqz_kj.setKPLX("1");
		jar_fpqz_kj.setBZ("123\n123\nsdf\n阿萨德发送到分\n安全网破诶入侵颇尔UR皮欧打开就发连接阿道夫骄傲坡度反扑阿的看法结案率亏大发了科几打破以请问普瑞\n钱");
		jar_fpqz_kj.setKPRQ(new Date());
		jar_fpqz_kj.setKPR("苏宁易购撒大苏打");
		jar_fpqz_kj.setSKR("管理员的根深蒂固");
		jar_fpqz_kj.setFHR("管理员反倒是开发");
//		jar_fpqz_kj.setXSF_YHZH("中国建设银行 111133141115211322");
		jar_fpqz_kj.setXSF_YHZH("中国建设银行 111133141115211322中国建设银行 111133141115211322");
//		jar_fpqz_kj.setXSF_DZDH("南京市");
		jar_fpqz_kj.setXSF_DZDH("中国建设银行 111133141115211322中国建设银行 111133141115211322");
		jar_fpqz_kj.setXSF_MC("苏宁易购电子商务有限公司");
		jar_fpqz_kj.setXSF_NSRSBH("91110112575938948G");

		jar_fpqz_kj.setFP_MW("279+9>*6+57</5571196<02*0555+++13*+521/787<714+*153/1*9*+04378/->74+13//92/0-2<3-<155/85+13*+521/787<774*531");
		jar_fpqz_kj.setJYM("56944149623511400854");
		jar_fpqz_kj.setEWM("");
		jar_fpqz_kj.setFP_DM("011001600111");
		jar_fpqz_kj.setFP_HM("11735396");
		jar_fpqz_kj.setJQBH("161565171869");
		jar_fpqz_kj.setBMB_BBH("1");
		//jar_fpqz_kj.setQD_BZ("0");
		jar_fpqz_kj.setQD_BZ("1");
		jar_fpqz_kj.setQDXMMC("详见货物清单");
		jar_fpqz_kj.setQZID("4169455300015209000000030000c927");


		//模板
		jar_fpqz_kj.setMBDM(formatFPZL("51") + "/1100");
//		jar_fpqz_kj.setMBDM("0000");
		jar_fpqz_kj.setIAC("");
		jar_fpqz_kj.setSJLY("09");
		jar_fpqz_kj.setDKBZ("0");
		jar_fpqz_kj.setSGBZ("Y");
		jar_fpqz_kj.setYFP_DM("011001600111");
		jar_fpqz_kj.setYFP_HM("11735396");
		jar_fpqz_kj.setFPZL("10");

		JAR_FPQZ_KJMX jar_fpqz_kjmx = new JAR_FPQZ_KJMX();
		jar_fpqz_kjmx.setXMMC("中国建设银行 111133141115211322中国建设银行 111133141115211322");
//		jar_fpqz_kjmx.setXMMC("【苏宁易购超市】仲景原味香菇酱210G 调味品 调料 下饭菜饭菜");
//		jar_fpqz_kjmx.setXMMC("【苏宁易购超市】仲景原味香菇酱210G 调味品 调料 下饭菜");
//		jar_fpqz_kjmx.setXMMC("【苏宁易购超市】仲景原味香菇酱210G 调味品 调料 下");
//		jar_fpqz_kjmx.setXMMC("【苏宁易购超市】仲景原味香菇酱21");
		jar_fpqz_kjmx.setDW("");
		jar_fpqz_kjmx.setGGXH("");
		jar_fpqz_kjmx.setXMSL("1");
		jar_fpqz_kjmx.setXMDJ("1");
		jar_fpqz_kjmx.setXMJE("1");
		jar_fpqz_kjmx.setSL("0");
		jar_fpqz_kjmx.setSE("0");
		jar_fpqz_kjmx.setFPHXZ("0");
		jar_fpqz_kjmx.setSPBM("1234567890123456789");
		jar_fpqz_kjmx.setZXBM("");
		jar_fpqz_kjmx.setYHZCBS("0");
		jar_fpqz_kjmx.setLSLBS("");
		jar_fpqz_kjmx.setZZSTSGL("");
		jar_fpqz_kjmx.setKCE("");

		JAR_FPQZ_KJMX jar_fpqz_kjmx2 = new JAR_FPQZ_KJMX();
		jar_fpqz_kjmx2.setXMMC("*水果*君不见黄河之水天上来君不见黄河阿萨德法撒旦发射点发射得分之水天上来君不见黄河之水天上来君不见黄河之水天上来_OJ3");
//		jar_fpqz_kjmx2.setXMMC("【苏宁易购超市】仲景原味香菇酱210G 调味品 调料 下饭菜");
		jar_fpqz_kjmx2.setDW("");
		jar_fpqz_kjmx2.setGGXH("");
		jar_fpqz_kjmx2.setXMSL("");
		jar_fpqz_kjmx2.setXMDJ("");
		jar_fpqz_kjmx2.setXMJE("-2.06");
		jar_fpqz_kjmx2.setSL("0.17");
		jar_fpqz_kjmx2.setSE("-0.35");
		jar_fpqz_kjmx2.setFPHXZ("0");
		jar_fpqz_kjmx2.setSPBM("1234567890123456789");
		jar_fpqz_kjmx2.setZXBM("");
		jar_fpqz_kjmx2.setYHZCBS("0");
		jar_fpqz_kjmx2.setLSLBS("");
		jar_fpqz_kjmx2.setZZSTSGL("");
		jar_fpqz_kjmx2.setKCE("");

		JAR_FPQZ_KJMX jar_fpqz_kjmx3 = new JAR_FPQZ_KJMX();
		jar_fpqz_kjmx3.setXMMC("配送费");
		jar_fpqz_kjmx3.setDW("");
		jar_fpqz_kjmx3.setGGXH("");
		jar_fpqz_kjmx3.setXMSL("1");
		jar_fpqz_kjmx3.setXMDJ("1.05128205");
		jar_fpqz_kjmx3.setXMJE("1.05");
		jar_fpqz_kjmx3.setSL("0.17");
		jar_fpqz_kjmx3.setSE("0.18");
		jar_fpqz_kjmx3.setFPHXZ("0");
		jar_fpqz_kjmx3.setSPBM("1234567890123456789");
		jar_fpqz_kjmx3.setZXBM("");
		jar_fpqz_kjmx3.setYHZCBS("0");
		jar_fpqz_kjmx3.setLSLBS("");
		jar_fpqz_kjmx3.setZZSTSGL("");
		jar_fpqz_kjmx3.setKCE("");

		JAR_FPQZ_KJMX jar_fpqz_kjmx4 = new  JAR_FPQZ_KJMX();
		jar_fpqz_kjmx4.setXMMC("配送费");
		jar_fpqz_kjmx4.setDW("");
		jar_fpqz_kjmx4.setGGXH("");
		jar_fpqz_kjmx4.setXMSL("");
		jar_fpqz_kjmx4.setXMDJ("");
		jar_fpqz_kjmx4.setXMJE("-0.21");
		jar_fpqz_kjmx4.setSL("0.17");
		jar_fpqz_kjmx4.setSE("-0.04");
		jar_fpqz_kjmx4.setFPHXZ("0");
		jar_fpqz_kjmx4.setSPBM("1234567890123456789");
		jar_fpqz_kjmx4.setZXBM("");
		jar_fpqz_kjmx4.setYHZCBS("0");
		jar_fpqz_kjmx4.setLSLBS("");
		jar_fpqz_kjmx4.setZZSTSGL("");
		jar_fpqz_kjmx4.setKCE("");
		JAR_FPQZ_KJMX[] list = new JAR_FPQZ_KJMX[10];
		
		for (int i = 0; i < 10; i++) {
            list[i] = jar_fpqz_kjmx2;
        }
        jar_fpqz_kj.setJAR_FPQZ_KJMXS(list);
        try {
            ValidateUtil.validatePdf(jar_fpqz_kj);
        } catch (CustomException e) {
            e.printStackTrace();
        }
        
        Map map = pdfProduce(jar_fpqz_kj);
        if (1000 == ((Integer) map.get(OrderManagementConstant.CODE)).intValue()) {
            FileOutputStream file = new FileOutputStream(new File("d:\\4-line-ba" + new Random().nextInt(10000) + ".pdf"));
//				FileOutputStream file = new FileOutputStream(new File("F:\\123.pdf"));
            file.write((byte[]) map.get("pdfFile"));
            file.flush();
            file.close();
            System.out.println("succ");
        } else {
            System.out.println(map.get("msg"));
        }

	}
	/**
	 * 生成pdf数据
	 */
	public static R createPdf(CommonOrderInvoiceAndOrderMxInfo commonOrder) {
        
        JAR_FPQZ_KJ convertInvocie2Fpqz = convertInvocie2Fpqz(commonOrder);
        
        Map map = pdfProduce(convertInvocie2Fpqz);
        if (1000 == ((Integer) map.get(OrderManagementConstant.CODE)).intValue()) {
            return R.ok().put(OrderManagementConstant.CODE, "0000").put("data", map.get("pdfFile"));
        } else {
            return R.error();
        }
        
        
    }
    /**
     * 根据发票种类代码获取地区编码
     */
	public static String getAddressCode(String fpzldm,String fpdm){
		String dqdm = null;
		switch(fpzldm){
		case "0":
			dqdm = fpdm.substring(0,4);
			dqdm = findDqbm(dqdm);
			break;
		case "2":
			if(fpdm.length() == 10){
				dqdm = fpdm.substring(0,4);
			}else{
				dqdm = fpdm.substring(1,5);
			}
			dqdm = findDqbm(dqdm);
			break;
		case "51":
			dqdm = fpdm.substring(1,5);
			dqdm = findDqbm(dqdm);
			break;
		default:
			log.error("发票信息转换为pdf，未知的发票种类代码:{}",fpzldm);
			break;
		}
	    return dqdm;
		
	}
	/**
     * @param @param  dqdm
     * @param @return
     * @return String
     * @throws
     * @Title : findDqbm
     * @Description : 获取模板信息
     */
	
	private static String findDqbm(String dqdm) {
		if(PdfTemplateEnum.BEIJING_1100.code().equals(dqdm) || PdfTemplateEnum.FUJIAN_XIAMEN_3502.code().equals(dqdm)
				|| PdfTemplateEnum.GUANGDONG_SHENZHEN_4403.code().equals(dqdm) || PdfTemplateEnum.LIAONING_DALIAN_2102.code().equals(dqdm)
				|| PdfTemplateEnum.SHANDONG_QINGDAO_3702.code().equals(dqdm) || PdfTemplateEnum.TIANJIN_1200.code().equals(dqdm)
				|| PdfTemplateEnum.ZHEJIANG_NINGBO_3302.code().equals(dqdm)){
			return dqdm;
			
		}else{
			String provinceCode = dqdm.substring(0,2);
			PdfTemplateEnum codeValue = PdfTemplateEnum.getCodeValue(provinceCode);
			if(codeValue == null){
				return PdfTemplateEnum.DEFAULT_0000.templateCode();
			}else{
				return codeValue.templateCode();
			}
		}
	}

	/**
	 * 订单数据到生成pdf的对象转换
	 *
	 */
	public static JAR_FPQZ_KJ convertInvocie2Fpqz(CommonOrderInvoiceAndOrderMxInfo commonOrder) {
		OrderInfo orderInfo = commonOrder.getOrderInfo();
		OrderInvoiceInfo orderInvoiceInfo = commonOrder.getOrderInvoiceInfo();
		List<OrderItemInfo> orderItemList = commonOrder.getOrderItemList();

		JAR_FPQZ_KJ jar_fpqz_kj = new JAR_FPQZ_KJ();
		jar_fpqz_kj.setGMF_DZDH(orderInfo.getGhfDz() == null ? ""
				: orderInfo.getGhfDz() + (orderInfo.getGhfDh() == null ? "" : orderInfo.getGhfDh()));
		jar_fpqz_kj.setGMF_YHZH(orderInfo.getGhfYh() == null ? ""
				: orderInfo.getGhfYh() + (orderInfo.getGhfZh() == null ? "" : orderInfo.getGhfZh()));
		jar_fpqz_kj.setGMF_MC(orderInfo.getGhfMc() == null ? "" : orderInfo.getGhfMc());
		jar_fpqz_kj.setGMF_NSRSBH(orderInfo.getGhfNsrsbh());
		jar_fpqz_kj.setJSHJ(orderInfo.getKphjje());
		jar_fpqz_kj.setHJJE(orderInfo.getHjbhsje());
		jar_fpqz_kj.setHJSE(orderInfo.getHjse());
		jar_fpqz_kj.setKPLX(orderInfo.getKplx());
		if (OrderInfoEnum.ORDER_BILLING_INVOICE_TYPE_1.getKey().equals(orderInfo.getKplx())) {
			if(StringUtils.isNotBlank(orderInfo.getBz())){
				if(orderInfo.getBz().startsWith("对应正数发票代码")){
					jar_fpqz_kj.setBZ(orderInfo.getBz());
				}else{
					jar_fpqz_kj.setBZ("对应正数发票代码:" + orderInfo.getYfpDm() + "号码:"
							+ orderInfo.getYfpHm()
					+ (orderInfo.getBz() == null ? "" : orderInfo.getBz()));
				}
				
			}else{
				
				if (OrderInfoEnum.ORDER_INVOICE_TYPE_0.getKey().equals(orderInfo.getFpzlDm())) {
					orderInfo.setBz(ConfigureConstant.STRING_HZBZ + orderInvoiceInfo.getHzxxbbh());
				}else{
					orderInfo.setBz("对应正数发票代码:" + orderInfo.getYfpDm() + "号码:"
							+ orderInfo.getYfpHm()
					+ (orderInfo.getBz() == null ? "" : orderInfo.getBz()));
				}
				jar_fpqz_kj.setBZ(orderInfo.getBz());
			}
			
		}
		jar_fpqz_kj.setBZ(orderInfo.getBz() == null ? "" : orderInfo.getBz());
		jar_fpqz_kj.setKPRQ(orderInvoiceInfo.getKprq());
		jar_fpqz_kj.setKPR(orderInfo.getKpr());
		jar_fpqz_kj.setSKR(orderInfo.getSkr());
		jar_fpqz_kj.setFHR(orderInfo.getFhr());
		jar_fpqz_kj.setXSF_YHZH(orderInfo.getXhfYh() == null ? ""
				: orderInfo.getXhfYh() + (orderInfo.getXhfZh() == null ? "" : orderInfo.getXhfZh()));
		jar_fpqz_kj.setXSF_DZDH(orderInfo.getXhfDz() == null ? ""
				: orderInfo.getXhfDz() + (orderInfo.getXhfDh() == null ? "" : orderInfo.getXhfDh()));
		jar_fpqz_kj.setXSF_MC(orderInfo.getXhfMc());
		jar_fpqz_kj.setXSF_NSRSBH(orderInfo.getXhfNsrsbh());

		jar_fpqz_kj.setFP_MW(orderInvoiceInfo.getFwm());
		jar_fpqz_kj.setJYM(orderInvoiceInfo.getJym());
		jar_fpqz_kj.setEWM(orderInvoiceInfo.getEwm());
		jar_fpqz_kj.setFP_DM(orderInvoiceInfo.getFpdm());
		jar_fpqz_kj.setFP_HM(orderInvoiceInfo.getFphm());
		jar_fpqz_kj.setJQBH(orderInvoiceInfo.getJqbh());
		jar_fpqz_kj.setBMB_BBH(orderInfo.getBbmBbh());
		// jar_fpqz_kj.setQD_BZ(orderInvoiceInfo.getQdbz());
		jar_fpqz_kj.setQDXMMC("（详见销货清单）");
		jar_fpqz_kj.setQZID("4169455300015209000000030000c927");

		// 模板 根据发票种类代码获取
		jar_fpqz_kj.setMBDM(formatFPZL(orderInvoiceInfo.getFpzlDm()) + "/"
				+ getAddressCode(orderInvoiceInfo.getFpzlDm(), orderInvoiceInfo.getFpdm()));
		// jar_fpqz_kj.setMBDM(formatFPZL(orderInvoiceInfo.getFpzlDm()) +
		// "/0000");
		// jar_fpqz_kj.setMBDM("0000");
		jar_fpqz_kj.setIAC("");
		jar_fpqz_kj.setSJLY("09");
		jar_fpqz_kj.setDKBZ(orderInfo.getDkbz() == null ? "" : orderInfo.getDkbz());
		String sgbz = "";
		if (OrderInfoEnum.QDBZ_CODE_2.getKey().equals(orderInfo.getQdBz()) || OrderInfoEnum.QDBZ_CODE_3.getKey().equals(orderInfo.getQdBz())) {
			sgbz = InvoiceGenUtil.SGBZ_SGFP;
		}
		jar_fpqz_kj.setSGBZ(sgbz);
		jar_fpqz_kj.setYFP_DM(orderInfo.getYfpDm());
		jar_fpqz_kj.setYFP_HM(orderInfo.getYfpHm());
		jar_fpqz_kj.setFPZL(orderInfo.getFpzlDm());
		if(!"4".equals(orderInfo.getQdBz())){
			if (orderItemList.size() > 8) {
				jar_fpqz_kj.setQD_BZ("1");
			} else {
				jar_fpqz_kj.setQD_BZ("0");
			}
		} else {
			jar_fpqz_kj.setQD_BZ(orderInfo.getQdBz());
		}
		// jar_fpqz_kj.setQD_BZ("0");
		// jar_fpqz_kj.setQD_BZ("1");
		/**
		 * 作废标志赋值,如果作废标志为1表示已作废,其他为未作废
		 */
		String zfbz = ConfigureConstant.STRING_0;
		if (StringUtils.isNotBlank(orderInvoiceInfo.getZfBz()) && OrderInfoEnum.INVALID_INVOICE_1.getKey().equals(orderInvoiceInfo.getZfBz())) {
			zfbz = ConfigureConstant.STRING_1;
		}
		jar_fpqz_kj.setZFBZ(zfbz);
		jar_fpqz_kj.setPrintPdfWaterMark(SystemConfig.printPdfWaterMark);
		jar_fpqz_kj.setPrintPdfWaterMarkMsg(SystemConfig.printPdfWaterMarkMsg);
		JAR_FPQZ_KJMX[] list = new JAR_FPQZ_KJMX[orderItemList.size()];
		int i = 0;
		for (OrderItemInfo orderItem : orderItemList) {
			JAR_FPQZ_KJMX kjmx = new JAR_FPQZ_KJMX();
			kjmx.setFPHXZ(orderItem.getFphxz());
			kjmx.setGGXH(orderItem.getGgxh());
			kjmx.setKCE(orderItem.getKce());
			kjmx.setLSLBS(orderItem.getLslbs());
			kjmx.setSE(orderItem.getSe());
			kjmx.setSL(orderItem.getSl());
			kjmx.setSPBM(orderItem.getSpbm());
			
			kjmx.setXMJE(orderItem.getXmje());
			kjmx.setXMMC(orderItem.getXmmc());
            if (!"1".equals(orderItem.getFphxz())) {
                kjmx.setXMDJ(orderItem.getXmdj());
                kjmx.setXMSL(orderItem.getXmsl());
                kjmx.setDW(orderItem.getXmdw());
            } else {
                if ("4".equals(jar_fpqz_kj.getQD_BZ())) {
                    kjmx.setGGXH("");
                }
            }
			kjmx.setYHZCBS(orderItem.getYhzcbs());
			kjmx.setZXBM(orderItem.getZxbm());
			kjmx.setZZSTSGL(orderItem.getZzstsgl());
			list[i] = kjmx;
			i++;
		}

		jar_fpqz_kj.setJAR_FPQZ_KJMXS(list);

		return jar_fpqz_kj;

	}
    public static void main(String[] args) throws IOException {
    	/*String str = "0123456789";
    	System.out.println(str.substring(1,5));*/
    	test(new String[1]);
    	System.out.println("1111".substring(0, 1));
		
	}
}
