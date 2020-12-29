package com.dxhy.common.generatepdf.util;

import com.dxhy.common.generatepdf.entity.JAR_FPQZ_KJ;
import com.dxhy.common.generatepdf.entity.JAR_FPQZ_KJMX;
import com.dxhy.common.generatepdf.exception.CustomException;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

import java.math.BigDecimal;
/**
 * 校验工具类
 *
 * @author ZSC-DXHY
 * @date 创建时间: 2020-08-14 15:46
 */
@Slf4j
public class ValidateUtil {
	/**
	 * pdf校验:发票信息检查
     *
	 * @param kj
	 * @throws CustomException
	 */
	public static void validatePdf(JAR_FPQZ_KJ kj) throws CustomException {
        log.info("{}入参{}", kj.toString());
        if (StringUtils.isNotBlank(kj.getZFBZ()) && !"0".equals(kj.getZFBZ()) && !"1".equals(kj.getZFBZ())) {
	        throw new CustomException(1999, "作废标志只能是0或者1(" + kj.getZFBZ() + ")");
        }
		JAR_FPQZ_KJMX[] mxs = kj.getJAR_FPQZ_KJMXS();
        if ((null == mxs) || (mxs.length == 0)) {
            throw new CustomException(1012, "发票明细不能为空");
        }
		if ((!StringUtils.isBlank(kj.getGMF_DZDH()))
				&& (checkLength(kj.getGMF_DZDH(), 100))) {
			throw new CustomException(1013, new StringBuilder()
					.append("购买方地址和电话长度最大不能超过100(").append(kj.getGMF_DZDH())
					.append(")").toString());
		}
		if ((!StringUtils.isBlank(kj.getGMF_YHZH()))
				&& (checkLength(kj.getGMF_YHZH(), 100))) {
			throw new CustomException(1014, new StringBuilder()
					.append("购买方银行帐号长度最大不能超过100(").append(kj.getGMF_YHZH())
					.append(")").toString());
		}
		if (!StringUtils.isBlank(kj.getGMF_MC())) {
            if (checkLength(kj.getGMF_MC(), 100)) {
                throw new CustomException(1015, new StringBuilder()
                        .append("购买方名称长度最大不能超过100(").append(kj.getGMF_MC())
                        .append(")").toString());
            }
        } else {
            throw new CustomException(1016, "购买方名称必填");
        }
		
		if ((!StringUtils.isBlank(kj.getGMF_NSRSBH()))
				&& (checkBytesLength(kj.getGMF_NSRSBH(), 20))) {
			throw new CustomException(1017, new StringBuilder()
					.append("购买方识别号长度最大不能超过20个字节(").append(kj.getGMF_NSRSBH())
					.append(")").toString());
		}
		if (StringUtils.isBlank(kj.getKPLX())) {
			throw new CustomException(1019, "开票类型必填");
		}
		if (null == kj.getKPRQ()) {
			throw new CustomException(1103, "开票日期必填");
		}
//		if (StringUtils.isBlank(kj.getKPR())) {
//			throw new CustomException(1022, "开票人必填");
//		}
		
		if ((!StringUtils.isBlank(kj.getXSF_YHZH()))
				&& (checkLength(kj.getXSF_YHZH(), 100))) {
			throw new CustomException(1025, new StringBuilder()
					.append("销售方银行帐号长度最大不能超过100(").append(kj.getXSF_YHZH())
					.append(")").toString());
		}
		if ((!StringUtils.isBlank(kj.getXSF_DZDH()))
				&& (checkLength(kj.getXSF_DZDH(), 100))) {
			throw new CustomException(1026, new StringBuilder()
					.append("销售方地址电话长度最大不能超过100(").append(kj.getXSF_DZDH())
					.append(")").toString());
		}
		if (!StringUtils.isBlank(kj.getXSF_MC())) {
            if (checkLength(kj.getXSF_MC(), 100)) {
                throw new CustomException(1028, new StringBuilder()
                        .append("销售方名称长度最大不能超过100(").append(kj.getXSF_MC())
                        .append(")").toString());
            }
        } else {
            throw new CustomException(1029, "销售方名称必填");
        }
		
		if (!StringUtils.isBlank(kj.getXSF_NSRSBH())) {
            if (checkBytesLength(kj.getXSF_NSRSBH(), 20)) {
                throw new CustomException(1030, new StringBuilder()
                        .append("销售方纳税人识别号长度最大不能超过20个字节(")
                        .append(kj.getXSF_NSRSBH()).append(")").toString());
            }
        } else {
            throw new CustomException(1031, "销售方纳税人识别号必填");
        }

		/*if (!StringUtils.isBlank(kj.getFP_MW())) {
			if ((!checkLengthEquals(kj.getFP_MW(), 108))
					&& (!checkLengthEquals(kj.getFP_MW(), 112)))
				throw new CustomException(1032, new StringBuilder()
						.append("发票密文长度有误(").append(kj.getFP_MW()).append(")")
						.toString());
		} else
			throw new CustomException(1033, "发票密文必填");
		*/
		/*if (!StringUtils.isBlank(kj.getJYM())) {
			if (!checkLengthEquals(kj.getJYM(), 20))
				throw new CustomException(1034, new StringBuilder()
						.append("校验码长度有误(").append(kj.getJYM()).append(")")
						.toString());
		} else
			throw new CustomException(1035, "校验码必填");
		*/
		if (!StringUtils.isBlank(kj.getFP_DM())) {
			if ((!checkLengthEquals(kj.getFP_DM(), 10))
                    && (!checkLengthEquals(kj.getFP_DM(), 12))) {
                throw new CustomException(1036, new StringBuilder()
                        .append("发票代码长度有误,长度应为10或12位(").append(kj.getFP_DM())
                        .append(")").toString());
            }
            if ((!isNumber(kj.getFP_DM())) || (kj.getFP_DM().contains("."))) {
                throw new CustomException(1150, new StringBuilder()
                        .append("发票代码必须是10位或12位的数字(").append(kj.getFP_DM())
                        .append(")").toString());
            }
		} else {
			throw new CustomException(1037, "发票代码必填");
		}
		if (!StringUtils.isBlank(kj.getFP_HM())) {
            if (checkLength(kj.getFP_HM(), 8)) {
                throw new CustomException(1038, new StringBuilder()
                        .append("发票号码长度有误,长度应为8位(").append(kj.getFP_HM())
                        .append(")").toString());
            }
            if ((!isNumber(kj.getFP_HM())) || (kj.getFP_HM().contains("."))) {
                throw new CustomException(1151, new StringBuilder()
                        .append("发票号码必须是8位的数字(").append(kj.getFP_HM())
                        .append(")").toString());
            }
		} else {
			throw new CustomException(1039, "发票号码必填");
		}
		if (!StringUtils.isBlank(kj.getYFP_DM())) {
            if ((!checkLengthEquals(kj.getYFP_DM(), 10))
                    && (!checkLengthEquals(kj.getYFP_DM(), 12))) {
                throw new CustomException(1027, new StringBuilder()
                        .append("原发票代码长度有误,长度应为10或12位(").append(kj.getYFP_DM())
                        .append(")").toString());
            }
            if ((!isNumber(kj.getYFP_DM()))
                    || (kj.getYFP_DM().contains("."))) {
                throw new CustomException(1154, new StringBuilder()
                        .append("原发票代码必须是10位或12位的数字(").append(kj.getYFP_DM())
                        .append(")").toString());
            }
		} else if (InvoiceGenUtil.KPLX_HZFP.equals(kj.getKPLX()) && !"0".equals(kj.getFPZL())) {
			throw new CustomException(1134, new StringBuilder()
					.append("红字发票原发票代码必填(备注:").append(kj.getBZ()).append(")")
					.toString());
		}
		if (!StringUtils.isBlank(kj.getYFP_HM())) {
            if (checkLength(kj.getYFP_HM(), 8)) {
                throw new CustomException(1121, new StringBuilder()
                        .append("原发票号码长度有误,长度应为8位(").append(kj.getYFP_HM())
                        .append(")").toString());
            }
            if ((!isNumber(kj.getYFP_HM()))
                    || (kj.getYFP_HM().contains("."))) {
                throw new CustomException(1155, new StringBuilder()
                        .append("原发票号码必须是8位的数字(").append(kj.getYFP_HM())
                        .append(")").toString());
            }
		} else if (InvoiceGenUtil.KPLX_HZFP.equals(kj.getKPLX()) && !"0".equals(kj.getFPZL())) {
			throw new CustomException(1137, new StringBuilder()
					.append("红字发票原发票号码必填(备注:").append(kj.getBZ()).append(")")
					.toString());
		}
		/*if (!StringUtils.isBlank(kj.getJQBH())) {
			if (!checkLengthEquals(kj.getJQBH(), 12))
				throw new CustomException(1040, new StringBuilder()
						.append("机器编号长度有误(").append(kj.getJQBH()).append(")")
						.toString());
		} else
			throw new CustomException(1041, "机器编号必填");
*/
		if (!StringUtils.isBlank(kj.getJSHJ())) {
			if (!isNumber(kj.getJSHJ())) {
				throw new CustomException(1061, new StringBuilder()
						.append("价税合计必须是合法数字(").append(kj.getJSHJ())
						.append(")").toString());
			}
		} else {
			throw new CustomException(1063, "价税合计必填");
		}
		if (!StringUtils.isBlank(kj.getHJJE())) {
			if (!isNumber(kj.getHJJE())) {
				throw new CustomException(1042, new StringBuilder()
						.append("合计金额必须是合法数字(").append(kj.getHJJE())
						.append(")").toString());
			}
		} else {
			throw new CustomException(1043, "合计金额必填");
		}
		if (!StringUtils.isBlank(kj.getHJSE())) {
			if (!isNumber(kj.getHJSE())) {
				throw new CustomException(1044, new StringBuilder()
						.append("合计税额必须是合法数字(").append(kj.getHJSE())
						.append(")").toString());
			}
		} else {
			throw new CustomException(1045, "合计税额必填");
		}
		if ((!StringUtils.isBlank(kj.getDKBZ()))
				&& (!InvoiceGenUtil.DKBZ_ZKFP.equals(kj.getDKBZ()))
				&& (!InvoiceGenUtil.DKBZ_DKFP.equals(kj.getDKBZ()))) {
			throw new CustomException(1130, new StringBuilder()
					.append("代开标志必须是0或1(").append(kj.getDKBZ()).append(")")
					.toString());
		}
		if (!StringUtils.isBlank(kj.getSGBZ())) {
            if (!InvoiceGenUtil.SGBZ_SGFP.equals(kj.getSGBZ())) {
                throw new CustomException(1131, new StringBuilder()
                        .append("收购标志有误(").append(kj.getSGBZ()).append(")")
                        .toString());
            }
            if (InvoiceGenUtil.DKBZ_DKFP.equals(kj.getDKBZ())) {
                throw new CustomException(1133, new StringBuilder()
                        .append("代开发票不允许开具收购发票(").append(kj.getDKBZ())
                        .append(")").toString());
            }
		}
		if (!StringUtils.isBlank(kj.getBZ())) {
			if (InvoiceGenUtil.KPLX_HZFP.equals(kj.getKPLX())) {
				/*String a = new StringBuilder().append("对应正数发票代码:")
						.append(kj.getYFP_DM()).toString();
				String b = new StringBuilder().append("号码:")
						.append(StringUtil.addZero(kj.getYFP_HM(), 8))
						.toString();

				if ((kj.getBZ().indexOf(a) == -1)
						|| (kj.getBZ().indexOf(b) == -1))
					throw new CustomException(1108, new StringBuilder()
							.append("红字发票备注需注明对应正数发票代码")
							.append(null == kj.getYFP_DM() ? ""
									: new StringBuilder().append(":")
											.append(kj.getYFP_DM()).toString())
							.append("号码")
							.append(null == kj.getYFP_HM() ? ""
									: new StringBuilder()
											.append(":")
											.append(StringUtil.addZero(
													kj.getYFP_HM(), 8))
											.toString()).append("(")
							.append(kj.getBZ()).append(")").toString());*/
			}
			if ((InvoiceGenUtil.DKBZ_DKFP.equals(kj.getDKBZ()))
					&& !kj.getBZ().contains("代开企业税号:") && !kj.getBZ().contains("代开企业名称:")) {
				throw new CustomException(1141, new StringBuilder()
						.append("代开发票备注需注明代开企业税号与代开企业名称(").append(kj.getBZ())
						.append(")").toString());
			}
		} else if (InvoiceGenUtil.KPLX_HZFP.equals(kj.getKPLX())) {
 			throw new CustomException(1098, "红字发票备注必填");
		}
		
		if (!StringUtils.isBlank(kj.getQDXMMC())) {
            if (checkLength(kj.getQDXMMC(), 90)) {
                throw new CustomException(1105, new StringBuilder()
                        .append("清单发票项目名称长度最大不能超过90(").append(kj.getQDXMMC())
                        .append(")").toString());
            }
		} else if (InvoiceGenUtil.QDBZ_QZQD.equals(kj.getQD_BZ())) {
			throw new CustomException(1106, "强制清单时，清单发票项目必填");
		}
		if ((!StringUtils.isBlank(kj.getBMB_BBH()))
				&& (checkLength(kj.getBMB_BBH(), 20))) {
			throw new CustomException(1097, new StringBuilder()
					.append("编码表版本号长度最大不能超过20(").append(kj.getBMB_BBH())
					.append(")").toString());
		}
		if (!StringUtils.isBlank(kj.getQZID())) {
            if (!checkLengthEquals(kj.getQZID(), 32)) {
                throw new CustomException(1099, new StringBuilder()
                        .append("签章ID长度有误(").append(kj.getQZID()).append(")")
                        .toString());
            }
        } else {
            throw new CustomException(1100, "签章ID必填");
        }
		
		if ((!StringUtils.isBlank(kj.getMBDM()))
                && kj.getMBDM().length() != 6) {
			throw new CustomException(1104, new StringBuilder()
					.append("模版代码长度有误(").append(kj.getMBDM()).append(")")
					.toString());
		}

	/*	if (ArithUtil.compareTo(
				ArithUtil.fdd(kj.getHJJE(), 2).add(
						ArithUtil.fdd(kj.getHJSE(), 2)),
				ArithUtil.fdd(kj.getJSHJ(), 2), BigDecimal.ZERO) == 1)
			throw new CustomException(1083, new StringBuilder().append("合计金额(")
					.append(kj.getHJJE()).append(")加合计税额(")
					.append(kj.getHJSE()).append(")不等于价税合计(")
					.append(kj.getJSHJ()).append(")").toString());*/
		BigDecimal hjxmje = BigDecimal.ZERO;
		BigDecimal hjse1 = BigDecimal.ZERO;
		BigDecimal hjse2 = BigDecimal.ZERO;
		String hjxmjeStr = "";
		String hjseStr1 = "";
		String hjseStr2 = "";
		for (int i = 0; i < mxs.length; i++) {
			BigDecimal kce = BigDecimal.ZERO;
			if (null == mxs[i]) {
				throw new CustomException(1110, new StringBuilder().append("第")
						.append(i + 1).append("行项目信息必填").toString());
			}
			if ((!StringUtils.isBlank(mxs[i].getXMSL()))
					&& (!isNumber(mxs[i].getXMSL()))) {
				throw new CustomException(1060, new StringBuilder().append("第")
						.append(i + 1).append("行项目数量必须是合法数字(")
						.append(mxs[i].getXMSL()).append(")").toString());
			}
			
			if ((!StringUtils.isBlank(mxs[i].getXMDJ()))
					&& (!isNumber(mxs[i].getXMDJ()))) {
				throw new CustomException(1062, new StringBuilder().append("第")
						.append(i + 1).append("行项目单价必须是合法数字(")
						.append(mxs[i].getXMDJ()).append(")").toString());
			}
			
			if (!StringUtils.isBlank(mxs[i].getXMJE())) {
                if (!isNumber(mxs[i].getXMJE())) {
                    throw new CustomException(1046, new StringBuilder()
                            .append("第").append(i + 1).append("行项目金额必须是合法数字(")
                            .append(mxs[i].getXMJE()).append(")").toString());
                }
				/*if (new BigDecimal(mxs[i].getXMJE()).compareTo(BigDecimal.ZERO) == 0)
					throw new CustomException(1047, new StringBuilder()
							.append("第").append(i + 1).append("行项目金额不能为0(")
							.append(mxs[i].getXMJE()).append(")").toString());*/
			} else {
				throw new CustomException(1048, new StringBuilder().append("第")
						.append(i + 1).append("行项目金额必填").toString());
			}
			
			boolean result = (!StringUtils.isBlank(mxs[i].getXMSL()))
					&& (!StringUtils.isBlank(mxs[i].getXMDJ()))
					&& ((new BigDecimal(mxs[i].getXMSL())
					.compareTo(BigDecimal.ZERO) != 0) || (new BigDecimal(
					mxs[i].getXMDJ()).compareTo(BigDecimal.ZERO) != 0))
					&& (ArithUtil.compareTo(
					ArithUtil.fdd(
							new BigDecimal(mxs[i].getXMSL())
									.multiply(new BigDecimal(mxs[i]
											.getXMDJ())), 2), ArithUtil
							.fdd(mxs[i].getXMJE(), 2), new BigDecimal(
							"0.01")) == 1);
			if (result) {
				BigDecimal wc1 = ArithUtil
						.fdd(new BigDecimal(mxs[i].getXMSL()).multiply(new BigDecimal(
								"0.0000005")), 2).abs();
				BigDecimal wc2 = ArithUtil
						.fdd(new BigDecimal(mxs[i].getXMDJ()).multiply(new BigDecimal(
								"0.0000005")), 2).abs();
				BigDecimal wc = wc1.compareTo(wc2) == 1 ? wc1 : wc2;
				if (ArithUtil
						.compareTo(ArithUtil.fdd(
								new BigDecimal(mxs[i].getXMSL())
										.multiply(new BigDecimal(mxs[i]
												.getXMDJ())), 2), ArithUtil
								.fdd(mxs[i].getXMJE(), 2), wc) == 1) {
					/*throw new CustomException(1064, new StringBuilder()
							.append("第").append(i + 1).append("行项目数量(")
							.append(mxs[i].getXMSL()).append(")乘以项目单价(")
							.append(mxs[i].getXMDJ()).append(")不等于项目金额(")
							.append(mxs[i].getXMJE()).append(")").toString());*/
				}

			}
			
			if (!StringUtils.isBlank(mxs[i].getSL())) {
                if (!isNumber(mxs[i].getSL())) {
                    throw new CustomException(1049, new StringBuilder()
                            .append("第").append(i + 1).append("行税率必须是合法数字(")
                            .append(mxs[i].getSL()).append(")").toString());
                }
				if (new BigDecimal(mxs[i].getSL()).compareTo(BigDecimal.ZERO) == -1) {
					throw new CustomException(1050, new StringBuilder()
							.append("第").append(i + 1).append("行税率不能小于0(")
							.append(mxs[i].getSL()).append(")").toString());
				}
                if (new BigDecimal(mxs[i].getSL()).compareTo(BigDecimal.ONE) != -1) {
                    throw new CustomException(1051, new StringBuilder()
                            .append("第").append(i + 1).append("行税率必须小于1(")
                            .append(mxs[i].getSL()).append(")").toString());
                }
			} else {
				throw new CustomException(1052, new StringBuilder().append("第")
						.append(i + 1).append("行税率必填").toString());
			}
			
			if (!StringUtils.isBlank(mxs[i].getSE())) {
				if (!isNumber(mxs[i].getSE())) {
					throw new CustomException(1053, new StringBuilder()
							.append("第").append(i + 1).append("行税额必须是合法数字(")
							.append(mxs[i].getSE()).append(")").toString());
				}
			} else {
				throw new CustomException(1054, new StringBuilder().append("第")
						.append(i + 1).append("行税额必填").toString());
			}
			
			if (StringUtils.isBlank(mxs[i].getXMMC())) {
				throw new CustomException(1059, new StringBuilder().append("第")
						.append(i + 1).append("行项目名称必填").toString());
			}
			
			if ((!StringUtils.isBlank(mxs[i].getFPHXZ()))
					&& (!checkLengthEquals(mxs[i].getFPHXZ(), 1))) {
				throw new CustomException(1122, new StringBuilder().append("第")
						.append(i + 1).append("行发票行性质长度有误(")
						.append(mxs[i].getFPHXZ()).append(")").toString());
			}
			
			if ((!StringUtils.isBlank(mxs[i].getSPBM()))
					&& (checkLength(mxs[i].getSPBM(), 19))) {
				throw new CustomException(1124, new StringBuilder().append("第")
						.append(i + 1).append("行商品编码长度最大不能超过19(")
						.append(mxs[i].getSPBM()).append(")").toString());
			}
			
			if ((!StringUtils.isBlank(mxs[i].getZXBM()))
					&& (checkLength(mxs[i].getZXBM(), 20))) {
				throw new CustomException(1125, new StringBuilder().append("第")
						.append(i + 1).append("行自行编码长度最大不能超过20(")
						.append(mxs[i].getZXBM()).append(")").toString());
			}
			
			
			if (!StringUtils.isBlank(mxs[i].getKCE())) {
				try {
					kce = ArithUtil.fdd(mxs[i].getKCE(), 2).multiply(
							i == 0 ? BigDecimal.ONE : BigDecimal.ZERO);
				} catch (NumberFormatException e) {
					throw new CustomException(1087, new StringBuilder()
							.append("第").append(i + 1).append("行扣除额必须是合法数字(")
							.append(mxs[i].getKCE()).append(")").toString());
				}
				if (i == 0) {
					if (new BigDecimal(mxs[i].getXMJE()).abs()
							.subtract(kce.abs()).compareTo(BigDecimal.ZERO) == -1) {
						throw new CustomException(1086, new StringBuilder()
								.append("扣除额(").append(mxs[i].getKCE())
								.append(")不能大于项目金额(").append(mxs[i].getXMJE())
								.append(")。").toString());
					}

					if (InvoiceGenUtil.KPLX_LZFP.equals(kj.getKPLX())) {
                        if (kce.compareTo(BigDecimal.ZERO) == -1) {
                            throw new CustomException(1116, new StringBuilder()
                                    .append("差额征税蓝字发票，扣除额不能为负数(")
                                    .append(mxs[i].getKCE()).append(")")
                                    .toString());
                        }
						if (mxs.length == 2) {
							if (new BigDecimal(mxs[1].getXMJE())
                                    .compareTo(BigDecimal.ZERO) != -1) {
                                throw new CustomException(1117,
                                        "差额征税蓝字发票，第二行商品行只能为折扣行");
                            }
						} else if (mxs.length > 2) {
							throw new CustomException(1118,
									"差额征税蓝字发票，商品行数量不能大于2行");
						}
						if (kj.getBZ().indexOf(
								new StringBuilder()
										.append("差额征税：")
										.append(ArithUtil.fdd(mxs[i].getKCE(),
                                                2, 2)).append("。").toString()) != 0) {
                            throw new CustomException(1136,
                                    new StringBuilder()
                                            .append("差额征税蓝字发票，备注需注明差额征税：")
                                            .append(ArithUtil.fdd(
                                                    mxs[i].getKCE(), 2, 2))
                                            .append("。(").append(kj.getBZ())
                                            .append(")").toString());
                        }
					} else if (InvoiceGenUtil.KPLX_HZFP.equals(kj.getKPLX())) {
						if (kce.compareTo(BigDecimal.ZERO) == 1) {
							throw new CustomException(1119, new StringBuilder()
									.append("差额征税红字发票，扣除额不能为正数(")
									.append(mxs[i].getKCE()).append(")")
									.toString());
						}
						if (mxs.length > 2) {
							throw new CustomException(1120,
									"差额征税红字发票，商品行数量不能大于2行");
						}
						if (kj.getBZ().indexOf("差额征税") != 0) {
							throw new CustomException(1135, new StringBuilder()
									.append("差额征税红字发票，备注需注明差额征税。(")
									.append(kj.getBZ()).append(")").toString());
						}
					}
				}
			} else if ((i == 0) && (!StringUtils.isBlank(kj.getBZ()))
					&& (kj.getBZ().contains("差额征税："))) {
				throw new CustomException(1149, new StringBuilder()
						.append("差额征税发票，扣除额不能为空(代码:").append(kj.getFP_DM())
						.append(",号码:").append(kj.getFP_HM()).append(",备注:")
						.append(kj.getBZ()).append(")").toString());
			}

			if (ArithUtil.compareTo(ArithUtil.fdd(
					ArithUtil.fdd(mxs[i].getXMJE(), 2).subtract(kce)
							.multiply(new BigDecimal(mxs[i].getSL())), 2),
					ArithUtil.fdd(mxs[i].getSE(), 2), InvoiceGenUtil.KPLX_LZFP
							.equals(kj.getKPLX()) ? new BigDecimal("0.06")
							: new BigDecimal("1.27")) == 1) {
				throw new CustomException(
						1055,
						new StringBuilder()
								.append("第")
								.append(i + 1)
								.append("行开票类型(")
								.append(kj.getKPLX())
								.append(")[项目金额(")
								.append(mxs[i].getXMJE())
								.append(")")
								.append(kce.compareTo(BigDecimal.ZERO) != 0 ? new StringBuilder()
										.append("-扣除额(")
										.append(mxs[i].getKCE()).append(")")
										.toString()
										: "").append("]乘以税率(")
								.append(mxs[i].getSL()).append(")不等于税额(")
								.append(mxs[i].getSE()).append(")").toString());
			}
			if (InvoiceGenUtil.KPLX_LZFP.equals(kj.getKPLX())) {
				if (new BigDecimal(kj.getHJJE()).compareTo(BigDecimal.ZERO) != 1) {
					throw new CustomException(1093, new StringBuilder()
							.append("蓝票的合计金额必须大于0(").append(kj.getHJJE())
							.append(")").toString());
				}

			} else if (InvoiceGenUtil.KPLX_HZFP.equals(kj.getKPLX())) {
                if (new BigDecimal(kj.getHJJE()).compareTo(BigDecimal.ZERO) != -1) {
                    throw new CustomException(1094, "红票的合计金额必须小于0");
                }
				if (new BigDecimal(mxs[i].getXMJE()).compareTo(BigDecimal.ZERO) == 1) {
					throw new CustomException(1089, new StringBuilder()
							.append("第").append(i + 1).append("行红票项目金额不能大于0")
							.toString());
				}
			} else {
				throw new CustomException(1018, new StringBuilder()
						.append("开票类型数据非法(").append(kj.getKPLX()).append(")")
						.toString());
			}
			hjxmje = hjxmje.add(ArithUtil.fdd(mxs[i].getXMJE(), 2));
			hjxmjeStr = new StringBuilder().append(hjxmjeStr)
					.append(mxs[i].getXMJE()).append("+").toString();
			hjse1 = hjse1.add(new BigDecimal(mxs[i].getXMJE()).subtract(kce)
					.multiply(new BigDecimal(mxs[i].getSL())));
			hjseStr1 = new StringBuilder().append(hjseStr1).append("(")
					.append(mxs[i].getXMJE()).append("-")
					.append(kce.toPlainString()).append(")*")
					.append(mxs[i].getSL()).append("+").toString();
			hjse2 = hjse2.add(ArithUtil.fdd(mxs[i].getSE(), 2));
			hjseStr2 = new StringBuilder().append(hjseStr2)
					.append(mxs[i].getSE()).append("+").toString();
		}
//		if (ArithUtil.compareTo(ArithUtil.fdd(kj.getHJJE(), 2), hjxmje,
//				new BigDecimal("0.01")) == 1)
//			throw new CustomException(1090, new StringBuilder()
//					.append("合计金额(")
//					.append(kj.getHJJE())
//					.append(")不等于商品行项目金额之和(")
//					.append(hjxmje)
//					.append("=")
//					.append(mxs.length < 15 ? hjxmjeStr.substring(0,
//							hjxmjeStr.length() - 1) : new StringBuilder()
//							.append(mxs.length).append("行项目金额之和").toString())
//					.append(")").toString());
//		if (ArithUtil.compareTo(ArithUtil.fdd(kj.getHJSE(), 2),
//				ArithUtil.fdd(hjse1, 2), new BigDecimal("1.27")) == 1)
//			throw new CustomException(1091, new StringBuilder()
//					.append("合计税额(")
//					.append(kj.getHJSE())
//					.append(")不等于商品行税额之和(")
//					.append(hjse1)
//					.append("=")
//					.append(mxs.length < 15 ? hjseStr1.substring(0,
//							hjseStr1.length() - 1) : new StringBuilder()
//							.append(mxs.length).append("行税额之和").toString())
//					.append(")").toString());
	}
	
	private static String checkZzstsglSl(String zzstsgl, String sl) {
		if (("不征税".equals(zzstsgl)) || ("免税".equals(zzstsgl))
				|| ("出口零税".equals(zzstsgl))) {
			if (new BigDecimal(sl).compareTo(BigDecimal.ZERO) != 0) {
				return "0%";
			}
		} else if ("按5%简易征收".equals(zzstsgl)) {
			if (new BigDecimal(sl).compareTo(new BigDecimal("0.05")) != 0) {
				return "5%";
			}
		} else if ("按3%简易征收".equals(zzstsgl)) {
            if (new BigDecimal(sl).compareTo(new BigDecimal("0.03")) != 0) {
                return "3%";
            }
		} else if ("简易征收".equals(zzstsgl)) {
			if ((new BigDecimal(sl).compareTo(new BigDecimal("0.03")) != 0)
					&& (new BigDecimal(sl).compareTo(new BigDecimal("0.04")) != 0)
                    && (new BigDecimal(sl).compareTo(new BigDecimal("0.05")) != 0)) {
                return "3%或4%或5%";
            }
		} else if (("按5%简易征收减按1.5%计征".equals(zzstsgl))
				&& (new BigDecimal(sl).compareTo(new BigDecimal("0.015")) != 0)) {
			return "1.5%";
		}
		return "success";
	}

	private static boolean checkLength(String parmaterStr, int maxLength) {
		return parmaterStr.length() > maxLength;
	}

	private static boolean checkBytesLength(String parmaterStr, int maxLength) {
		try {
            if (parmaterStr.getBytes("GBK").length > maxLength) {
                return true;
            }
		} catch (Exception e) {
			return true;
		}
		return false;
	}

	private static boolean checkLengthEquals(String parmaterStr, int maxLength) {
		try {
            if (parmaterStr.getBytes("GBK").length == maxLength) {
                return true;
            }
		} catch (Exception e) {
			return true;
		}
		return false;
	}

	private static boolean isNumber(String value) {
		try {
			new BigDecimal(value);
			return true;
		} catch (NumberFormatException e) {
		}
		return false;
	}
	
	public static boolean isZkh(String xmmc) {
		return (xmmc.indexOf("折扣") == 0) || (xmmc.indexOf("折扣行数") == 0);
	}
	
	public static String getSlStr(String spbmBb, String zzstsgl)
			throws CustomException {
		if (!StringUtils.isBlank(spbmBb)) {
			if (StringUtils.isBlank(zzstsgl)) {
				return "0%";
			}
			if ("出口零税".equals(zzstsgl)) {
				return "0%";
			}
			if ("免税".equals(zzstsgl)) {
				return "免税";
			}
			if ("不征税".equals(zzstsgl)) {
				return "不征税";
			}
			throw new CustomException(1109, new StringBuilder().append("没有(")
					.append(zzstsgl).append(")优惠政策").toString());
		}

		return "***";
	}
}
