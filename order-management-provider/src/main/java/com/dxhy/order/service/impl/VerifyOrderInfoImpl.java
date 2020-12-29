package com.dxhy.order.service.impl;

import cn.hutool.core.date.DateUtil;
import com.dxhy.order.api.ApiVerifyOrderInfo;
import com.dxhy.order.constant.ConfigureConstant;
import com.dxhy.order.constant.OrderInfoContentEnum;
import com.dxhy.order.constant.OrderInfoEnum;
import com.dxhy.order.constant.OrderManagementConstant;
import com.dxhy.order.model.CommonOrderInfo;
import com.dxhy.order.model.OrderInfo;
import com.dxhy.order.model.OrderItemInfo;
import com.dxhy.order.protocol.v4.order.DDMXXX;
import com.dxhy.order.protocol.v4.order.DDTXX;
import com.dxhy.order.protocol.v4.order.DDZXX;
import com.dxhy.order.utils.CheckParamUtil;
import com.dxhy.order.utils.DecimalCalculateUtil;
import com.dxhy.order.utils.MathUtil;
import com.dxhy.order.utils.ValidateUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.dubbo.config.annotation.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author ：杨士勇
 * @ClassName ：VerifyOrderInfoImpl
 * @Description ：
 * @date ：2020年2月26日 上午11:04:20
 */
@Service
@Slf4j
public class VerifyOrderInfoImpl implements ApiVerifyOrderInfo {

	@Override
	public Map<String, String> verifyDynamicOrderInfo(DDZXX orderInfo) {
		
		DDTXX ddtxx = orderInfo.getDDTXX();
		List<DDMXXX> ddmxxx = orderInfo.getDDMXXX();
		// 基础信息校验
		Map<String, String> resultMap = checkCommonOrderHead(ddtxx, ddmxxx);
		
		if (!OrderInfoContentEnum.SUCCESS.getKey().equals(resultMap.get(OrderManagementConstant.ERRORCODE))) {
			return resultMap;
		}
		
		
		// 订单主体-发票种类代码合法性(只能为0:专票;2:普票;41:卷票;51:电子票)
		
		//订单请求发票类型合法性
		if (StringUtils.isNotBlank(ddtxx.getFPLXDM())
				&& !OrderInfoEnum.ORDER_INVOICE_TYPE_004.getKey().equals(ddtxx.getFPLXDM())
				&& !OrderInfoEnum.ORDER_INVOICE_TYPE_007.getKey().equals(ddtxx.getFPLXDM())
				&& !OrderInfoEnum.ORDER_INVOICE_TYPE_0.getKey().equals(ddtxx.getFPLXDM())
				&& !OrderInfoEnum.ORDER_INVOICE_TYPE_2.getKey().equals(ddtxx.getFPLXDM())
				&& !OrderInfoEnum.ORDER_INVOICE_TYPE_51.getKey().equals(ddtxx.getFPLXDM())
				&& !OrderInfoEnum.ORDER_INVOICE_TYPE_026.getKey().equals(ddtxx.getFPLXDM())) {
			return generateErrorMap(OrderInfoContentEnum.CHECK_ISS7PRI_107004);
		}
		
		return resultMap;
	}

	private Map<String, String> checkCommonOrderHead(DDTXX ddtxx,
	                                                 List<DDMXXX> ddmxxxes) {
		
		// 声明校验结果map
		Map<String, String> checkResultMap = new HashMap<>(10);
		checkResultMap.put(OrderManagementConstant.ERRORCODE, OrderInfoContentEnum.SUCCESS.getKey());
		// 1.数据非空和长度校验
		if (ddtxx == null) {
			return generateErrorMap(OrderInfoContentEnum.HANDLE_ISSUE_202004);
		}
		
		if (CollectionUtils.isEmpty(ddmxxxes)) {
			return generateErrorMap(OrderInfoContentEnum.HANDLE_ISSUE_202009);
		}
		
		/**
		 * 订单主体-订单请求流水号
		 */
		
		checkResultMap = CheckParamUtil.checkParam(OrderInfoContentEnum.CHECK_ISS7PRI_107014,
				ddtxx.getDDQQLSH());
		if (!OrderInfoContentEnum.SUCCESS.getKey().equals(checkResultMap.get(OrderManagementConstant.ERRORCODE))) {
			return checkResultMap;
		}

		/**
		 * 订单主体-纳税人识别号
		 */
		checkResultMap = checkNsrsbhParam(OrderInfoContentEnum.CHECK_ISS7PRI_107016,
				OrderInfoContentEnum.CHECK_ISS7PRI_107017, OrderInfoContentEnum.CHECK_ISS7PRI_107163,
				ddtxx.getNSRSBH());
		if (!OrderInfoContentEnum.SUCCESS.getKey().equals(checkResultMap.get(OrderManagementConstant.ERRORCODE))) {
			return checkResultMap;
		}
		/**
		 * 订单主体-纳税人名称
		 */
		checkResultMap = CheckParamUtil.checkParam(OrderInfoContentEnum.CHECK_ISS7PRI_107018,
				ddtxx.getNSRMC());
		if (!OrderInfoContentEnum.SUCCESS.getKey().equals(checkResultMap.get(OrderManagementConstant.ERRORCODE))) {
			return checkResultMap;
		}
		
		/**
		 * 订单主体-开票类型
		 */
		checkResultMap = CheckParamUtil.checkParam(OrderInfoContentEnum.CHECK_ISS7PRI_107020,
				ddtxx.getKPLX());
		if (!OrderInfoContentEnum.SUCCESS.getKey().equals(checkResultMap.get(OrderManagementConstant.ERRORCODE))) {
			return checkResultMap;
		}
		
		// 订单主体-开票类型合法性(开票类型只能为0和1：0蓝字发票；1红字发票)
		if (!OrderInfoEnum.ORDER_BILLING_INVOICE_TYPE_0.getKey().equals(ddtxx.getKPLX())
				&& !OrderInfoEnum.ORDER_BILLING_INVOICE_TYPE_1.getKey().equals(ddtxx.getKPLX())) {
			return generateErrorMap(OrderInfoContentEnum.CHECK_ISS7PRI_107021);
		}

		/**
		 * 订单主体-编码表版本号
		 */
		checkResultMap = CheckParamUtil.checkParam(OrderInfoContentEnum.CHECK_ISS7PRI_107097,
				ddtxx.getBMBBBH());
		if (!OrderInfoContentEnum.SUCCESS.getKey().equals(checkResultMap.get(OrderManagementConstant.ERRORCODE))) {
			return checkResultMap;
		}

		/**
		 * 订单主体-销售方纳税人识别号
		 */
		checkResultMap = checkNsrsbhParam(OrderInfoContentEnum.CHECK_ISS7PRI_107022,
				OrderInfoContentEnum.CHECK_ISS7PRI_107017, OrderInfoContentEnum.CHECK_ISS7PRI_107163,
				ddtxx.getXHFSBH());
		if (!OrderInfoContentEnum.SUCCESS.getKey().equals(checkResultMap.get(OrderManagementConstant.ERRORCODE))) {
			return checkResultMap;
		}

		/**
		 * 订单主体-销售方纳税人名称
		 */
		checkResultMap = CheckParamUtil.checkParam(OrderInfoContentEnum.CHECK_ISS7PRI_107024,
				ddtxx.getXHFMC());
		if (!OrderInfoContentEnum.SUCCESS.getKey().equals(checkResultMap.get(OrderManagementConstant.ERRORCODE))) {
			return checkResultMap;
		}

		/**
		 * 校验销方税号为必填, 其他销方信息为非必填,如果填写进行合法性校验, 校验地址+电话总长度不能大于100
		 * 校验银行名称+帐号总长度不能大于100
		 */

		/**
		 * 订单主体-销售方地址
		 */
		checkResultMap = CheckParamUtil.checkParam(OrderInfoContentEnum.CHECK_ISS7PRI_107282,
				ddtxx.getXHFDZ());
		if (!OrderInfoContentEnum.SUCCESS.getKey().equals(checkResultMap.get(OrderManagementConstant.ERRORCODE))) {
			return checkResultMap;
		}

		/**
		 * 订单主体-销售方电话
		 */
		checkResultMap = CheckParamUtil.checkParam(OrderInfoContentEnum.CHECK_ISS7PRI_107283,
				ddtxx.getXHFDH());
		if (!OrderInfoContentEnum.SUCCESS.getKey().equals(checkResultMap.get(OrderManagementConstant.ERRORCODE))) {
			return checkResultMap;
		}

		/**
		 * 订单主体-销售方银行
		 */
		checkResultMap = CheckParamUtil.checkParam(OrderInfoContentEnum.CHECK_ISS7PRI_107284,
				ddtxx.getXHFYH());
		if (!OrderInfoContentEnum.SUCCESS.getKey().equals(checkResultMap.get(OrderManagementConstant.ERRORCODE))) {
			return checkResultMap;
		}

		/**
		 * 订单主体-销售方帐号
		 */
		checkResultMap = CheckParamUtil.checkParam(OrderInfoContentEnum.CHECK_ISS7PRI_107285,
				ddtxx.getXHFZH());
		if (!OrderInfoContentEnum.SUCCESS.getKey().equals(checkResultMap.get(OrderManagementConstant.ERRORCODE))) {
			return checkResultMap;
		}
		
		/**
		 * 订单主体-销售方地址和电话总长度 TODO 由于企业区分不开地址电话,所以校验支持地址电话总长度100,默认应该是85
		 */
		String dzDh = StringUtils.isBlank(ddtxx.getXHFDZ()) ? ""
				: ddtxx.getXHFDZ()
				+ (StringUtils.isBlank(ddtxx.getXHFDH()) ? "" : ddtxx.getXHFDH());
		checkResultMap = CheckParamUtil.checkParam(OrderInfoContentEnum.CHECK_ISS7PRI_107267, dzDh);
		if (!OrderInfoContentEnum.SUCCESS.getKey().equals(checkResultMap.get(OrderManagementConstant.ERRORCODE))) {
			return checkResultMap;
		}
		
		/**
		 * 订单主体-销售方银行和帐号总长度 TODO 由于企业区分不开银行帐号,所以校验支持银行帐号总长度100,默认应该是85
		 */
		String yhZh = StringUtils.isBlank(ddtxx.getXHFYH()) ? ""
				: ddtxx.getXHFYH()
				+ (StringUtils.isBlank(ddtxx.getXHFZH()) ? "" : ddtxx.getXHFZH());
		checkResultMap = CheckParamUtil.checkParam(OrderInfoContentEnum.CHECK_ISS7PRI_107268, yhZh);
		if (!OrderInfoContentEnum.SUCCESS.getKey().equals(checkResultMap.get(OrderManagementConstant.ERRORCODE))) {
			return checkResultMap;
		}
		
		/**
		 * 订单主体-开票人
		 */
		checkResultMap = CheckParamUtil.checkParam(OrderInfoContentEnum.CHECK_ISS7PRI_107044,
				ddtxx.getKPR());
		if (!OrderInfoContentEnum.SUCCESS.getKey().equals(checkResultMap.get(OrderManagementConstant.ERRORCODE))) {
			return checkResultMap;
		}

		/**
		 * 订单主体-开票人
		 */

		 checkResultMap = CheckParamUtil.checkParam(OrderInfoContentEnum.CHECK_ISS7PRI_107044, ddtxx.getKPR());
		 if (!OrderInfoContentEnum.SUCCESS.getKey().equals(checkResultMap.get(

		 	OrderManagementConstant.ERRORCODE))) { return checkResultMap;
		 }


		/**
		 * 订单主体-收款人
		 */
		checkResultMap = CheckParamUtil.checkParam(OrderInfoContentEnum.CHECK_ISS7PRI_107046,
				ddtxx.getSKR());
		if (!OrderInfoContentEnum.SUCCESS.getKey().equals(checkResultMap.get(OrderManagementConstant.ERRORCODE))) {
			return checkResultMap;
		}

		/**
		 * 订单主体-复核人
		 */
		checkResultMap = CheckParamUtil.checkParam(OrderInfoContentEnum.CHECK_ISS7PRI_107048,
				ddtxx.getFHR());
		if (!OrderInfoContentEnum.SUCCESS.getKey().equals(checkResultMap.get(OrderManagementConstant.ERRORCODE))) {
			return checkResultMap;
		}
		
		/**
		 * 订单主体-订单号
		 */
		checkResultMap = CheckParamUtil.checkParam(OrderInfoContentEnum.CHECK_ISS7PRI_107067,
				ddtxx.getDDH());
		if (!OrderInfoContentEnum.SUCCESS.getKey().equals(checkResultMap.get(OrderManagementConstant.ERRORCODE))) {
			return checkResultMap;
		}

		/**
		 * 订单主体-订单日期
		 */
		checkResultMap = CheckParamUtil.checkParam(OrderInfoContentEnum.CHECK_ISS7PRI_107068,
				ddtxx.getDDSJ() == null ? "" : ddtxx.getDDSJ());
		if (!OrderInfoContentEnum.SUCCESS.getKey().equals(checkResultMap.get(OrderManagementConstant.ERRORCODE))) {
			return checkResultMap;
		}

		/**
		 * 发票主体-门店号
		 */
		checkResultMap = CheckParamUtil.checkParam(OrderInfoContentEnum.CHECK_ISS7PRI_107293,
				ddtxx.getMDH());
		if (!OrderInfoContentEnum.SUCCESS.getKey().equals(checkResultMap.get(OrderManagementConstant.ERRORCODE))) {
			return checkResultMap;
		}

		/**
		 * 订单主体-价税合计
		 */
		checkResultMap = CheckParamUtil.checkParam(OrderInfoContentEnum.CHECK_ISS7PRI_107141,
				ddtxx.getJSHJ());
		if (!OrderInfoContentEnum.SUCCESS.getKey().equals(checkResultMap.get(OrderManagementConstant.ERRORCODE))) {
			return checkResultMap;
		}
		// 价税合计金额不能为0或者0.00
		if (ConfigureConstant.STRING_0.equals(ddtxx.getJSHJ())
				|| ConfigureConstant.STRING_000.equals(ddtxx.getJSHJ())
				|| ConfigureConstant.INT_2 != ValidateUtil.checkNumberic(ddtxx.getJSHJ())) {
			return generateErrorMap(OrderInfoContentEnum.CHECK_ISS7PRI_107066);
		}
		// 开票类型为0(蓝票)时,金额必须大于0
		boolean result = (StringUtils.isNotBlank(ddtxx.getKPLX())
				&& !OrderInfoEnum.ORDER_BILLING_INVOICE_TYPE_0.getKey().equals(ddtxx.getKPLX()))
				|| ConfigureConstant.DOUBLE_PENNY_ZERO >= new BigDecimal(ddtxx.getJSHJ()).doubleValue();
		if (result) {
			return generateErrorMap(OrderInfoContentEnum.CHECK_ISS7PRI_107080);
		}
		
		/**
		 * 订单主体-合计金额
		 */
		checkResultMap = CheckParamUtil.checkParam(OrderInfoContentEnum.CHECK_ISS7PRI_107142,
				ddtxx.getHJJE());
		if (!OrderInfoContentEnum.SUCCESS.getKey().equals(checkResultMap.get(OrderManagementConstant.ERRORCODE))) {
			return checkResultMap;
		}
		// 合计金额为不为0时,需要保证金额为小数点后两位
		if (ConfigureConstant.DOUBLE_PENNY_ZERO != new BigDecimal(ddtxx.getHJJE()).doubleValue()
				&& ConfigureConstant.INT_2 != ValidateUtil.checkNumberic(ddtxx.getHJJE())) {
			return generateErrorMap(OrderInfoContentEnum.CHECK_ISS7PRI_107135);
		}
		
		/**
		 * 订单主体-合计税额
		 */
		checkResultMap = CheckParamUtil.checkParam(OrderInfoContentEnum.CHECK_ISS7PRI_107143,
				ddtxx.getHJSE());
		if (!OrderInfoContentEnum.SUCCESS.getKey().equals(checkResultMap.get(OrderManagementConstant.ERRORCODE))) {
			return checkResultMap;
		}
		// 合计金额为不为0时,需要保证金额为小数点后两位
		if (ConfigureConstant.DOUBLE_PENNY_ZERO != new BigDecimal(ddtxx.getHJSE()).doubleValue()
				&& ConfigureConstant.INT_2 != ValidateUtil.checkNumberic(ddtxx.getHJSE())) {
			return generateErrorMap(OrderInfoContentEnum.CHECK_ISS7PRI_107136);
		}

		if (ConfigureConstant.MAX_ITEM_LENGTH <= ddmxxxes.size()) {
			return generateErrorMap(OrderInfoContentEnum.INVOICE_AUTO_NUMBER);
		}

		/**
		 * 金额关系合法性校验
		 */
		if (!StringUtils.isBlank(ddtxx.getJSHJ()) && !StringUtils.isBlank(ddtxx.getHJSE())
				&& !StringUtils.isBlank(ddtxx.getHJJE())) {
			
			double differ = MathUtil.sub(ddtxx.getJSHJ(),
					String.valueOf(MathUtil.add(ddtxx.getHJJE(), ddtxx.getHJSE())));
			// 如果误差值等于含税金额,说明是含税金额不作校验,如果是尾插不等于0,校验返回
			if (DecimalCalculateUtil.decimalFormatToString(ddtxx.getJSHJ(), ConfigureConstant.INT_2).equals(
					DecimalCalculateUtil.decimalFormatToString(String.valueOf(differ), ConfigureConstant.INT_2))) {
				
			} else if (ConfigureConstant.DOUBLE_PENNY_ZERO != differ) {
				checkResultMap = generateErrorMap(OrderInfoContentEnum.INVOICE_JSHJ_ERROR);
				return checkResultMap;
			}
			
		}
		
		/**
		 * 明细行数据与发票头数据进行校验
		 */
		BigDecimal kphjje = new BigDecimal(ddtxx.getJSHJ());
		BigDecimal sumKphjje = BigDecimal.ZERO;
		for (int j = 0; j < ddmxxxes.size(); j++) {
			Map<String, String> checkItemResultMap = checkCommonOrderItemsV3(ddmxxxes.get(j),
					ddmxxxes.size());
			if (!OrderInfoContentEnum.SUCCESS.getKey()
					.equals(checkItemResultMap.get(OrderManagementConstant.ERRORCODE))) {
				return checkItemResultMap;
			}
			
			if (OrderInfoEnum.HSBZ_1.getKey().equals(ddmxxxes.get(j).getHSBZ())) {
				sumKphjje = sumKphjje.add(new BigDecimal(ddmxxxes.get(j).getJE()));
			} else {
				sumKphjje = sumKphjje.add(new BigDecimal(ddmxxxes.get(j).getJE()))
						.add(new BigDecimal(ddmxxxes.get(j).getSE()));
			}
		}
		
		if (kphjje.subtract(sumKphjje).abs().compareTo(BigDecimal.ZERO) > 0) {

			return generateErrorMap(OrderInfoContentEnum.PRICE_TAX_SEPARATION_NE_KPHJJE);
		}

		return checkResultMap;
	}

	public Map<String, String> generateErrorMap(OrderInfoContentEnum orderInfoContentEnum) {
		Map<String, String> errorMap = new HashMap<>(2);
		errorMap.put(OrderManagementConstant.ERRORCODE, orderInfoContentEnum.getKey());
		errorMap.put(OrderManagementConstant.ERRORMESSAGE, orderInfoContentEnum.getMessage());
		log.error("数据校验结果码为:{},校验结果信息为:{}", orderInfoContentEnum.getKey(), orderInfoContentEnum.getMessage());
		return errorMap;
	}

	/**
	 * 校验税号规则
	 *
	 * @param nsrsbh
	 * @return
	 */
	public static Map<String, String> checkNsrsbhParam(OrderInfoContentEnum contentEnum,
			OrderInfoContentEnum contentEnum1, OrderInfoContentEnum contentEnum2, String nsrsbh) {

		Map<String, String> checkResultMap = new HashMap<>(10);
		checkResultMap.put(OrderManagementConstant.ERRORCODE, OrderInfoContentEnum.SUCCESS.getKey());

		checkResultMap = CheckParamUtil.checkParam(contentEnum, nsrsbh);
		if (!OrderInfoContentEnum.SUCCESS.getKey().equals(checkResultMap.get(OrderManagementConstant.ERRORCODE))) {
			return checkResultMap;
		}
		if (!StringUtils.isBlank(nsrsbh)) {
			// 是否包含空格
			if (nsrsbh.contains(" ")) {
				checkResultMap.put(OrderManagementConstant.ERRORCODE,
						OrderInfoContentEnum.CHECK_ISS7PRI_107164.getKey());
				checkResultMap.put(OrderManagementConstant.ERRORMESSAGE,
						OrderInfoContentEnum.CHECK_ISS7PRI_107164.getMessage());
				return checkResultMap;
			}
			// 判断税号长度合法性问题,长度必须15,17,18,20位
			if (ConfigureConstant.INT_15 != ValidateUtil.getStrBytesLength(nsrsbh)
					&& ConfigureConstant.INT_17 != ValidateUtil.getStrBytesLength(nsrsbh)
					&& ConfigureConstant.INT_18 != ValidateUtil.getStrBytesLength(nsrsbh)
					&& ConfigureConstant.INT_20 != ValidateUtil.getStrBytesLength(nsrsbh)) {
				checkResultMap.put(OrderManagementConstant.ERRORCODE, contentEnum1.getKey());
				checkResultMap.put(OrderManagementConstant.ERRORMESSAGE, contentEnum1.getMessage());
				return checkResultMap;
			}
			// 纳税人识别号需要全部大写
			if (!ValidateUtil.isAcronym(nsrsbh)) {
				checkResultMap.put(OrderManagementConstant.ERRORCODE, contentEnum2.getKey());
				checkResultMap.put(OrderManagementConstant.ERRORMESSAGE, contentEnum2.getMessage());
				return checkResultMap;
			}
		}

		return checkResultMap;
	}

	/**
	 * 校验请求订单批次数据信息中的订单明细信息正确性与合法性
	 *
	 * @param orderItemInfo
	 * @return
	 */
	private Map<String, String> checkCommonOrderItemsV3(DDMXXX orderItemInfo, int itemLength) {
		Map<String, String> checkResultMap = new HashMap<>(10);
		checkResultMap.put(OrderManagementConstant.ERRORCODE, OrderInfoContentEnum.SUCCESS.getKey());
		
		/**
		 * 订单明细信息-商品行序号
		 */
		checkResultMap = CheckParamUtil.checkParam(OrderInfoContentEnum.CHECK_ISS7PRI_107058, orderItemInfo.getXH());
		if (!OrderInfoContentEnum.SUCCESS.getKey().equals(checkResultMap.get(OrderManagementConstant.ERRORCODE))) {
			return checkResultMap;
		}
		
		/**
		 * 订单明细信息-规格型号
		 */
		checkResultMap = CheckParamUtil.checkParam(OrderInfoContentEnum.CHECK_ISS7PRI_107059, orderItemInfo.getGGXH());
		if (!OrderInfoContentEnum.SUCCESS.getKey().equals(checkResultMap.get(OrderManagementConstant.ERRORCODE))) {
			return checkResultMap;
		}
		
		/**
		 * 订单明细信息-发票行性质
		 */
		checkResultMap = CheckParamUtil.checkParam(OrderInfoContentEnum.CHECK_ISS7PRI_107055, orderItemInfo.getFPHXZ());
		if (!OrderInfoContentEnum.SUCCESS.getKey().equals(checkResultMap.get(OrderManagementConstant.ERRORCODE))) {
			return checkResultMap;
		}
		// 发票行性质只能为:0正常行、1折扣行、2被折扣行、6清单红字发票
		if (!OrderInfoEnum.FPHXZ_CODE_0.getKey().equals(orderItemInfo.getFPHXZ())
				&& !OrderInfoEnum.FPHXZ_CODE_1.getKey().equals(orderItemInfo.getFPHXZ())
				&& !OrderInfoEnum.FPHXZ_CODE_2.getKey().equals(orderItemInfo.getFPHXZ())
				&& !OrderInfoEnum.FPHXZ_CODE_6.getKey().equals(orderItemInfo.getFPHXZ())) {
			return generateErrorMap(OrderInfoContentEnum.CHECK_ISS7PRI_107056);
		}

		// 商品编码非必传
		checkResultMap = CheckParamUtil.checkParam(OrderInfoContentEnum.CHECK_ISS7PRI_107100, orderItemInfo.getSPBM());
		if (!OrderInfoContentEnum.SUCCESS.getKey().equals(checkResultMap.get(OrderManagementConstant.ERRORCODE))) {
			return checkResultMap;
		}
		// 税率必传
		if (StringUtils.isBlank(orderItemInfo.getSL())) {
			return generateErrorMap(OrderInfoContentEnum.CHECK_ISS7PRI_107146);
		}
		
		/**
		 * 订单明细信息-项目名称
		 */
		checkResultMap = CheckParamUtil.checkParam(OrderInfoContentEnum.CHECK_ISS7PRI_107057, orderItemInfo.getXMMC());
		if (!OrderInfoContentEnum.SUCCESS.getKey().equals(checkResultMap.get(OrderManagementConstant.ERRORCODE))) {
			return checkResultMap;
		}
		
		/**
		 * 订单明细信息-项目单位
		 */
		checkResultMap = CheckParamUtil.checkParam(OrderInfoContentEnum.CHECK_ISS7PRI_107060, orderItemInfo.getDW());
		if (!OrderInfoContentEnum.SUCCESS.getKey().equals(checkResultMap.get(OrderManagementConstant.ERRORCODE))) {
			return checkResultMap;
		}
		
		/**
		 * 订单明细信息-扣除额
		 */
		checkResultMap = CheckParamUtil.checkParam(OrderInfoContentEnum.CHECK_ISS7PRI_107061, orderItemInfo.getKCE());
		if (!OrderInfoContentEnum.SUCCESS.getKey().equals(checkResultMap.get(OrderManagementConstant.ERRORCODE))) {
			return checkResultMap;
		}
		/**
		 * 非折扣行扣除额发票明细只能是一行 折扣行发票最多只能是两行
		 */
		if(StringUtils.isNotBlank(orderItemInfo.getKCE())){
			if(OrderInfoEnum.FPHXZ_CODE_0.getKey().equals(orderItemInfo.getFPHXZ()) && itemLength > 1) {
				checkResultMap.put(OrderManagementConstant.ERRORCODE, OrderInfoContentEnum.INVOICE_MX_INFO_KCE_ERROR_144122.getKey());
				checkResultMap.put(OrderManagementConstant.ERRORMESSAGE, OrderInfoContentEnum.INVOICE_MX_INFO_KCE_ERROR_144122.getMessage());
				return checkResultMap;
			}
			if(!OrderInfoEnum.FPHXZ_CODE_0.getKey().equals(orderItemInfo.getFPHXZ()) && itemLength > 2){
				checkResultMap.put(OrderManagementConstant.ERRORCODE, OrderInfoContentEnum.INVOICE_MX_INFO_KCE_ERROR_144123.getKey());
				checkResultMap.put(OrderManagementConstant.ERRORMESSAGE, OrderInfoContentEnum.INVOICE_MX_INFO_KCE_ERROR_144123.getMessage());
				return checkResultMap;
			}
		}

		/**
		 * 订单明细信息-项目金额
		 */
		checkResultMap = CheckParamUtil.checkParam(OrderInfoContentEnum.CHECK_ISS7PRI_107145, orderItemInfo.getJE());
        if (!OrderInfoContentEnum.SUCCESS.getKey().equals(checkResultMap.get(OrderManagementConstant.ERRORCODE))) {
            return checkResultMap;
        }
		// 项目金额不能为0或者0.00
		if (ConfigureConstant.STRING_0.equals(orderItemInfo.getJE())
				|| ConfigureConstant.STRING_000.equals(orderItemInfo.getJE())) {
			return generateErrorMap(OrderInfoContentEnum.CHECK_ISS7PRI_107081);
		}
		// 合计金额为不为0时,需要保证金额为小数点后两位
		if (ConfigureConstant.DOUBLE_PENNY_ZERO != new BigDecimal(orderItemInfo.getJE()).doubleValue()
				&& ConfigureConstant.INT_2 != ValidateUtil.checkNumberic(orderItemInfo.getJE())) {
			return generateErrorMap(OrderInfoContentEnum.CHECK_ISS7PRI_107062);
		}
		
		/**
		 * 订单明细信息-项目税额
		 */
		checkResultMap = CheckParamUtil.checkParam(OrderInfoContentEnum.CHECK_ISS7PRI_107134, orderItemInfo.getSE());
		if (!OrderInfoContentEnum.SUCCESS.getKey().equals(checkResultMap.get(OrderManagementConstant.ERRORCODE))) {
			return checkResultMap;
		}
		// 合计税额为不为0时,需要保证税额为小数点后两位
		if (!StringUtils.isBlank(orderItemInfo.getSE())
				&& ConfigureConstant.DOUBLE_PENNY_ZERO != new BigDecimal(orderItemInfo.getSE()).doubleValue()
				&& ConfigureConstant.INT_2 != ValidateUtil.checkNumberic(orderItemInfo.getSE())) {
			return generateErrorMap(OrderInfoContentEnum.CHECK_ISS7PRI_107133);
		}
		
		/**
		 * 订单明细信息-项目数量
		 */
		checkResultMap = CheckParamUtil.checkParam(OrderInfoContentEnum.CHECK_ISS7PRI_107051, orderItemInfo.getSPSL());
		if (!OrderInfoContentEnum.SUCCESS.getKey().equals(checkResultMap.get(OrderManagementConstant.ERRORCODE))) {
			return checkResultMap;
		}
		
		/**
		 * 订单明细信息-项目单价
		 */
		checkResultMap = CheckParamUtil.checkParam(OrderInfoContentEnum.CHECK_ISS7PRI_107149, orderItemInfo.getDJ());
		if (!OrderInfoContentEnum.SUCCESS.getKey().equals(checkResultMap.get(OrderManagementConstant.ERRORCODE))) {
			return checkResultMap;
		}
		if (StringUtils.isNotBlank(orderItemInfo.getDJ()) && !orderItemInfo.getDJ().matches("\\d+?[.]?\\d{0,8}")) {
			return generateErrorMap(OrderInfoContentEnum.CHECK_ISS7PRI_107152);
		}
		
		/**
		 * 订单明细信息-自行编码
		 */
		checkResultMap = CheckParamUtil.checkParam(OrderInfoContentEnum.CHECK_ISS7PRI_107063, orderItemInfo.getZXBM());
		if (!OrderInfoContentEnum.SUCCESS.getKey().equals(checkResultMap.get(OrderManagementConstant.ERRORCODE))) {
			return checkResultMap;
		}
		
		/**
		 * 订单明细信息-含税标志
		 */
		checkResultMap = CheckParamUtil.checkParam(OrderInfoContentEnum.CHECK_ISS7PRI_107064, orderItemInfo.getHSBZ());
		if (!OrderInfoContentEnum.SUCCESS.getKey().equals(checkResultMap.get(OrderManagementConstant.ERRORCODE))) {
			return checkResultMap;
		}
		// 含税标志只能为0和1：0表示都不含税,1表示都含税
		if (!OrderInfoEnum.HSBZ_1.getKey().equals(orderItemInfo.getHSBZ())
				&& !OrderInfoEnum.HSBZ_0.getKey().equals(orderItemInfo.getHSBZ())) {
			return generateErrorMap(OrderInfoContentEnum.CHECK_ISS7PRI_107065);
		}
		// 含税标志为0时,税额不能为空
		if (OrderInfoEnum.HSBZ_0.getKey().equals(orderItemInfo.getHSBZ())
				&& StringUtils.isBlank(orderItemInfo.getHSBZ())) {
			return generateErrorMap(OrderInfoContentEnum.CHECK_ISS7PRI_107139);
		}

		/**
		 * 订单明细信息-商品编码
		 */
		checkResultMap = CheckParamUtil.checkParam(OrderInfoContentEnum.CHECK_ISS7PRI_107100, orderItemInfo.getSPBM());
		if (!OrderInfoContentEnum.SUCCESS.getKey().equals(checkResultMap.get(OrderManagementConstant.ERRORCODE))) {
			return checkResultMap;
		}
		// 商品编码必须为19位数字
		if (StringUtils.isNotBlank(orderItemInfo.getSPBM())) {
			boolean spbm = false;
			for (int j = 0; j < orderItemInfo.getSPBM().length(); j++) {
				char c = orderItemInfo.getSPBM().charAt(j);
				if ((c < '0' || c > '9')) {
					spbm = true;
				}
			}
			if (spbm) {
				return generateErrorMap(OrderInfoContentEnum.CHECK_ISS7PRI_107101);
			}
		}

		/**
		 * 订单明细信息-增值税特殊管理
		 */
		checkResultMap = CheckParamUtil.checkParam(OrderInfoContentEnum.CHECK_ISS7PRI_107105,
				orderItemInfo.getZZSTSGL());
		if (!OrderInfoContentEnum.SUCCESS.getKey().equals(checkResultMap.get(OrderManagementConstant.ERRORCODE))) {
			return checkResultMap;
		}

		/**
		 * 订单明细信息-优惠政策标识
		 */
		checkResultMap = CheckParamUtil.checkParam(OrderInfoContentEnum.CHECK_ISS7PRI_107103,
				orderItemInfo.getYHZCBS());
		if (!OrderInfoContentEnum.SUCCESS.getKey().equals(checkResultMap.get(OrderManagementConstant.ERRORCODE))) {
			return checkResultMap;
		}
		// 优惠政策标识只能为0或1,0:不使用,1:使用
		if (!OrderInfoEnum.YHZCBS_0.getKey().equals(orderItemInfo.getYHZCBS())
				&& !OrderInfoEnum.YHZCBS_1.getKey().equals(orderItemInfo.getYHZCBS())) {
			return generateErrorMap(OrderInfoContentEnum.CHECK_ISS7PRI_107102);
		}
		// 优惠政策标识为1时;
		if (ConfigureConstant.STRING_1.equals(orderItemInfo.getYHZCBS())) {
			if (StringUtils.isBlank(orderItemInfo.getZZSTSGL())) {
				return generateErrorMap(OrderInfoContentEnum.CHECK_ISS7PRI_107104);
			}
			// 订单明细信息中YHZCBS(优惠政策标识)为1, 且税率为0, 则LSLBS只能根据实际情况选择"0或1或2"中的一种,
			// 不能选择3, 且ZZSTSGL内容也只能写与0/1/2对应的"出口零税/免税/不征税
			if (!StringUtils.isBlank(orderItemInfo.getSL()) && ConfigureConstant.STRING_0.equals(orderItemInfo.getSL())
					&& !OrderInfoEnum.LSLBS_0.getKey().equals(orderItemInfo.getLSLBS())
					&& !OrderInfoEnum.LSLBS_1.getKey().equals(orderItemInfo.getLSLBS())
					&& !OrderInfoEnum.LSLBS_2.getKey().equals(orderItemInfo.getLSLBS())
					&& (StringUtils.isBlank(orderItemInfo.getZZSTSGL()))) {
				return generateErrorMap(OrderInfoContentEnum.CHECK_ISS7PRI_107132);
			}
			
		}
		if (OrderInfoEnum.YHZCBS_0.getKey().equals(orderItemInfo.getYHZCBS())) {
			if (!StringUtils.isBlank(orderItemInfo.getZZSTSGL())) {
				return generateErrorMap(OrderInfoContentEnum.CHECK_ISS7PRI_107106);
			}
		}
		
		/**
		 * 订单明细信息-零税率标识
		 */
		if (!StringUtils.isBlank(orderItemInfo.getLSLBS())
				&& !OrderInfoEnum.LSLBS_0.getKey().equals(orderItemInfo.getLSLBS())
				&& !OrderInfoEnum.LSLBS_1.getKey().equals(orderItemInfo.getLSLBS())
				&& !OrderInfoEnum.LSLBS_2.getKey().equals(orderItemInfo.getLSLBS())
				&& !OrderInfoEnum.LSLBS_3.getKey().equals(orderItemInfo.getLSLBS())) {
			return generateErrorMap(OrderInfoContentEnum.CHECK_ISS7PRI_107138);
		}
		
		/**
		 * 税率非空时,逻辑判断
		 */
		if (!StringUtils.isBlank(orderItemInfo.getSL())) {
			/**
			 * 增值税特殊管理不为空,不为不征税,不为免税,不为出口零税逻辑处理 如果是按5%简易征收需要保证税率为0.05
			 * 如果是按3%简易征收需要保证税率为0.03 如果是简易征收需要保证税率为0.03或0.04或0.05
			 * 如果是按5%简易征收减按1.5%计征需要保证税率为0.015
			 */
			if ((!StringUtils.isBlank(orderItemInfo.getZZSTSGL()))
					&& (!ConfigureConstant.STRING_BZS.equals(orderItemInfo.getZZSTSGL()))
					&& (!ConfigureConstant.STRING_MS.equals(orderItemInfo.getZZSTSGL()))
					&& (!ConfigureConstant.STRING_CKLS.equals(orderItemInfo.getZZSTSGL()))) {
				if (orderItemInfo.getZZSTSGL().contains(ConfigureConstant.STRING_ERROR_PERCENT)) {
					return generateErrorMap(OrderInfoContentEnum.COMMODITY_MESSAGE_SYNC_ERROR_173033);
				}
				switch (orderItemInfo.getZZSTSGL()) {
					case ConfigureConstant.STRING_JYZS5:
						if (!ConfigureConstant.STRING_005.equals(orderItemInfo.getSL())) {
							return generateErrorMap(OrderInfoContentEnum.CHECK_ISS7PRI_107108);
						}
						break;
					case ConfigureConstant.STRING_JYZS3:
						if (!ConfigureConstant.STRING_003.equals(orderItemInfo.getSL())) {
							return generateErrorMap(OrderInfoContentEnum.CHECK_ISS7PRI_107109);
						}
						break;
					case ConfigureConstant.STRING_JYZS:
						if (!ConfigureConstant.STRING_003.equals(orderItemInfo.getSL())
								|| !ConfigureConstant.STRING_004.equals(orderItemInfo.getSL())
								|| !ConfigureConstant.STRING_005.equals(orderItemInfo.getSL())) {
							return generateErrorMap(OrderInfoContentEnum.CHECK_ISS7PRI_107110);
						}
						break;
					case ConfigureConstant.STRING_JYZS5_1:
						if (!ConfigureConstant.STRING_0015.equals(orderItemInfo.getSL())) {
							return generateErrorMap(OrderInfoContentEnum.CHECK_ISS7PRI_107111);
						}
						
						break;
					default:
						break;
				}
				
			}
			
			// 零税率标识不为空,税率必须为0
			if ((!StringUtils.isBlank(orderItemInfo.getLSLBS()))
					&& (ConfigureConstant.DOUBLE_PENNY_ZERO != new BigDecimal(orderItemInfo.getSL()).doubleValue())) {
				return generateErrorMap(OrderInfoContentEnum.CHECK_ISS7PRI_107112);
			}
			// 零税率标识为空,税率不能为0
			if ((StringUtils.isBlank(orderItemInfo.getLSLBS())) && (new BigDecimal(ConfigureConstant.DOUBLE_PENNY_ZERO)
					.doubleValue() == new BigDecimal(orderItemInfo.getSL()).doubleValue())) {
				return generateErrorMap(OrderInfoContentEnum.CHECK_ISS7PRI_107113);
			}
			
		}
		
		// 订单明细信息中零税率标识为0/1/2, 但增值税特殊管理内容不为'出口零税/免税/不征税';
		boolean result1 = StringUtils.isBlank(orderItemInfo.getZZSTSGL())
				&& (OrderInfoEnum.LSLBS_0.getKey().equals(orderItemInfo.getLSLBS())
				|| OrderInfoEnum.LSLBS_1.getKey().equals(orderItemInfo.getLSLBS())
				|| OrderInfoEnum.LSLBS_2.getKey().equals(orderItemInfo.getLSLBS()));
		if (result1) {
			return generateErrorMap(OrderInfoContentEnum.CHECK_ISS7PRI_107114);
		}
		
		if (OrderInfoEnum.LSLBS_0.getKey().equals(orderItemInfo.getLSLBS())
				&& !ConfigureConstant.STRING_CKLS.equals(orderItemInfo.getZZSTSGL())) {
			return generateErrorMap(OrderInfoContentEnum.CHECK_ISS7PRI_107114);
		}
		if (OrderInfoEnum.LSLBS_1.getKey().equals(orderItemInfo.getLSLBS())
				&& !ConfigureConstant.STRING_MS.equals(orderItemInfo.getZZSTSGL())) {
			return generateErrorMap(OrderInfoContentEnum.CHECK_ISS7PRI_107114);
		}
		if (OrderInfoEnum.LSLBS_2.getKey().equals(orderItemInfo.getLSLBS())
				&& !ConfigureConstant.STRING_BZS.equals(orderItemInfo.getZZSTSGL())) {
			return generateErrorMap(OrderInfoContentEnum.CHECK_ISS7PRI_107114);
		}
		boolean result2 = OrderInfoEnum.LSLBS_3.getKey().equals(orderItemInfo.getLSLBS())
				&& (!StringUtils.isBlank(orderItemInfo.getZZSTSGL())
				|| !(OrderInfoEnum.YHZCBS_0.getKey().equals(orderItemInfo.getYHZCBS())));
		if (result2) {
			return generateErrorMap(OrderInfoContentEnum.CHECK_ISS7PRI_107140);
		}
		
		return checkResultMap;
	}
	
	
	private Map<String, String> checkCommonOrderItemsV3(OrderItemInfo orderItemInfo, int itemLength, String fpzldm) {
		Map<String, String> checkResultMap = new HashMap<>(10);
		checkResultMap.put(OrderManagementConstant.ERRORCODE, OrderInfoContentEnum.SUCCESS.getKey());
		
		/**
		 * 订单明细信息-商品行序号
		 */
		checkResultMap = CheckParamUtil.checkParam(OrderInfoContentEnum.CHECK_ISS7PRI_107058, orderItemInfo.getSphxh());
		if (!OrderInfoContentEnum.SUCCESS.getKey().equals(checkResultMap.get(OrderManagementConstant.ERRORCODE))) {
			return checkResultMap;
		}

        /**
         * 订单明细信息-规格型号
         */
        checkResultMap = CheckParamUtil.checkParam(OrderInfoContentEnum.CHECK_ISS7PRI_107059, orderItemInfo.getGgxh());
        if (!OrderInfoContentEnum.SUCCESS.getKey().equals(checkResultMap.get(OrderManagementConstant.ERRORCODE))) {
            return checkResultMap;
        }

        /**
         * 订单明细信息-发票行性质
         */
        checkResultMap = CheckParamUtil.checkParam(OrderInfoContentEnum.CHECK_ISS7PRI_107055, orderItemInfo.getFphxz());
        if (!OrderInfoContentEnum.SUCCESS.getKey().equals(checkResultMap.get(OrderManagementConstant.ERRORCODE))) {
            return checkResultMap;
        }
        // 发票行性质只能为:0正常行、1折扣行、2被折扣行、6清单红字发票
        if (!OrderInfoEnum.FPHXZ_CODE_0.getKey().equals(orderItemInfo.getFphxz())
                && !OrderInfoEnum.FPHXZ_CODE_1.getKey().equals(orderItemInfo.getFphxz())
                && !OrderInfoEnum.FPHXZ_CODE_2.getKey().equals(orderItemInfo.getFphxz())
                && !OrderInfoEnum.FPHXZ_CODE_6.getKey().equals(orderItemInfo.getFphxz())) {
            return generateErrorMap(OrderInfoContentEnum.CHECK_ISS7PRI_107056);
        }

        // 商品编码非必传
        checkResultMap = CheckParamUtil.checkParam(OrderInfoContentEnum.CHECK_ISS7PRI_107100, orderItemInfo.getSpbm());
        if (!OrderInfoContentEnum.SUCCESS.getKey().equals(checkResultMap.get(OrderManagementConstant.ERRORCODE))) {
            return checkResultMap;
        }
        // 税率必传
        if (StringUtils.isBlank(orderItemInfo.getSl())) {
            return generateErrorMap(OrderInfoContentEnum.CHECK_ISS7PRI_107146);
        }

        /**
         * 订单明细信息-项目名称
         */
        checkResultMap = CheckParamUtil.checkParam(OrderInfoContentEnum.CHECK_ISS7PRI_107057, orderItemInfo.getXmmc());
        if (!OrderInfoContentEnum.SUCCESS.getKey().equals(checkResultMap.get(OrderManagementConstant.ERRORCODE))) {
            return checkResultMap;
        }

        /**
         * 订单明细信息-项目单位
         */
        checkResultMap = CheckParamUtil.checkParam(OrderInfoContentEnum.CHECK_ISS7PRI_107060, orderItemInfo.getXmdw());
        if (!OrderInfoContentEnum.SUCCESS.getKey().equals(checkResultMap.get(OrderManagementConstant.ERRORCODE))) {
            return checkResultMap;
        }

        /**
         * 订单明细信息-扣除额
         */
        checkResultMap = CheckParamUtil.checkParam(OrderInfoContentEnum.CHECK_ISS7PRI_107061, orderItemInfo.getKce());
        if (!OrderInfoContentEnum.SUCCESS.getKey().equals(checkResultMap.get(OrderManagementConstant.ERRORCODE))) {
            return checkResultMap;
        }

        /**
         * 订单明细信息-项目金额
         */
        checkResultMap = CheckParamUtil.checkParam(OrderInfoContentEnum.CHECK_ISS7PRI_107145, orderItemInfo.getXmje());
        if (!OrderInfoContentEnum.SUCCESS.getKey().equals(checkResultMap.get(OrderManagementConstant.ERRORCODE))) {
            return checkResultMap;
        }
        // 项目金额不能为0或者0.00
        if (ConfigureConstant.STRING_0.equals(orderItemInfo.getXmje())
                || ConfigureConstant.STRING_000.equals(orderItemInfo.getXmje())) {
            return generateErrorMap(OrderInfoContentEnum.CHECK_ISS7PRI_107081);
        }
        // 合计金额为不为0时,需要保证金额为小数点后两位
        if (ConfigureConstant.DOUBLE_PENNY_ZERO != new BigDecimal(orderItemInfo.getXmje()).doubleValue()
                && ConfigureConstant.INT_2 != ValidateUtil.checkNumberic(orderItemInfo.getXmje())) {
            return generateErrorMap(OrderInfoContentEnum.CHECK_ISS7PRI_107062);
        }

        /**
         * 订单明细信息-项目税额
         */
        checkResultMap = CheckParamUtil.checkParam(OrderInfoContentEnum.CHECK_ISS7PRI_107134, orderItemInfo.getSe());
        if (!OrderInfoContentEnum.SUCCESS.getKey().equals(checkResultMap.get(OrderManagementConstant.ERRORCODE))) {
            return checkResultMap;
        }
        // 合计税额为不为0时,需要保证税额为小数点后两位
        if (!StringUtils.isBlank(orderItemInfo.getSe())
                && ConfigureConstant.DOUBLE_PENNY_ZERO != new BigDecimal(orderItemInfo.getSe()).doubleValue()
                && ConfigureConstant.INT_2 != ValidateUtil.checkNumberic(orderItemInfo.getSe())) {
            return generateErrorMap(OrderInfoContentEnum.CHECK_ISS7PRI_107133);
        }

        /**
         * 订单明细信息-项目数量
         */
        checkResultMap = CheckParamUtil.checkParam(OrderInfoContentEnum.CHECK_ISS7PRI_107051, orderItemInfo.getXmsl());
        if (!OrderInfoContentEnum.SUCCESS.getKey().equals(checkResultMap.get(OrderManagementConstant.ERRORCODE))) {
            return checkResultMap;
        }

        /**
         * 订单明细信息-项目单价
         */
        checkResultMap = CheckParamUtil.checkParam(OrderInfoContentEnum.CHECK_ISS7PRI_107149, orderItemInfo.getXmdj());
        if (!OrderInfoContentEnum.SUCCESS.getKey().equals(checkResultMap.get(OrderManagementConstant.ERRORCODE))) {
            return checkResultMap;
        }
        if (StringUtils.isNotBlank(orderItemInfo.getXmdj()) && !orderItemInfo.getXmdj().matches("\\d+?[.]?\\d{0,8}")) {
            return generateErrorMap(OrderInfoContentEnum.CHECK_ISS7PRI_107152);
        }

        /**
         * 订单明细信息-自行编码
         */
        checkResultMap = CheckParamUtil.checkParam(OrderInfoContentEnum.CHECK_ISS7PRI_107063, orderItemInfo.getZxbm());
        if (!OrderInfoContentEnum.SUCCESS.getKey().equals(checkResultMap.get(OrderManagementConstant.ERRORCODE))) {
            return checkResultMap;
        }

        /**
         * 订单明细信息-含税标志
         */
        checkResultMap = CheckParamUtil.checkParam(OrderInfoContentEnum.CHECK_ISS7PRI_107064, orderItemInfo.getHsbz());
        if (!OrderInfoContentEnum.SUCCESS.getKey().equals(checkResultMap.get(OrderManagementConstant.ERRORCODE))) {
            return checkResultMap;
        }
        // 含税标志只能为0和1：0表示都不含税,1表示都含税
        if (!OrderInfoEnum.HSBZ_1.getKey().equals(orderItemInfo.getHsbz())
                && !OrderInfoEnum.HSBZ_0.getKey().equals(orderItemInfo.getHsbz())) {
            return generateErrorMap(OrderInfoContentEnum.CHECK_ISS7PRI_107065);
        }
        // 含税标志为0时,税额不能为空
        if (OrderInfoEnum.HSBZ_0.getKey().equals(orderItemInfo.getHsbz())
                && StringUtils.isBlank(orderItemInfo.getHsbz())) {
            return generateErrorMap(OrderInfoContentEnum.CHECK_ISS7PRI_107139);
        }

        /**
         * 订单明细信息-商品编码
         */
        checkResultMap = CheckParamUtil.checkParam(OrderInfoContentEnum.CHECK_ISS7PRI_107100, orderItemInfo.getSpbm());
        if (!OrderInfoContentEnum.SUCCESS.getKey().equals(checkResultMap.get(OrderManagementConstant.ERRORCODE))) {
            return checkResultMap;
        }
        // 商品编码必须为19位数字
        if (StringUtils.isNotBlank(orderItemInfo.getSpbm())) {
            boolean spbm = false;
            for (int j = 0; j < orderItemInfo.getSpbm().length(); j++) {
                char c = orderItemInfo.getSpbm().charAt(j);
                if ((c < '0' || c > '9')) {
                    spbm = true;
                }
            }
            if (spbm) {
                return generateErrorMap(OrderInfoContentEnum.CHECK_ISS7PRI_107101);
            }
        }

        /**
         * 订单明细信息-增值税特殊管理
         */
        checkResultMap = CheckParamUtil.checkParam(OrderInfoContentEnum.CHECK_ISS7PRI_107105,
                orderItemInfo.getZzstsgl());
        if (!OrderInfoContentEnum.SUCCESS.getKey().equals(checkResultMap.get(OrderManagementConstant.ERRORCODE))) {
            return checkResultMap;
        }

        /**
         * 订单明细信息-优惠政策标识
         */
        checkResultMap = CheckParamUtil.checkParam(OrderInfoContentEnum.CHECK_ISS7PRI_107103,
                orderItemInfo.getYhzcbs());
        if (!OrderInfoContentEnum.SUCCESS.getKey().equals(checkResultMap.get(OrderManagementConstant.ERRORCODE))) {
            return checkResultMap;
        }
        // 优惠政策标识只能为0或1,0:不使用,1:使用
        if (!OrderInfoEnum.YHZCBS_0.getKey().equals(orderItemInfo.getYhzcbs())
                && !OrderInfoEnum.YHZCBS_1.getKey().equals(orderItemInfo.getYhzcbs())) {
            return generateErrorMap(OrderInfoContentEnum.CHECK_ISS7PRI_107102);
        }
        // 优惠政策标识为1时;
        if (ConfigureConstant.STRING_1.equals(orderItemInfo.getYhzcbs())) {
            if (StringUtils.isBlank(orderItemInfo.getZzstsgl())) {
                return generateErrorMap(OrderInfoContentEnum.CHECK_ISS7PRI_107104);
            }
            // 订单明细信息中YHZCBS(优惠政策标识)为1, 且税率为0, 则LSLBS只能根据实际情况选择"0或1或2"中的一种,
            // 不能选择3, 且ZZSTSGL内容也只能写与0/1/2对应的"出口零税/免税/不征税
            if (!StringUtils.isBlank(orderItemInfo.getSl()) && ConfigureConstant.STRING_0.equals(orderItemInfo.getSl())
                    && !OrderInfoEnum.LSLBS_0.getKey().equals(orderItemInfo.getLslbs())
                    && !OrderInfoEnum.LSLBS_1.getKey().equals(orderItemInfo.getLslbs())
                    && !OrderInfoEnum.LSLBS_2.getKey().equals(orderItemInfo.getLslbs())
                    && (StringUtils.isBlank(orderItemInfo.getZzstsgl()))) {
                return generateErrorMap(OrderInfoContentEnum.CHECK_ISS7PRI_107132);
            }

        }
        if (OrderInfoEnum.YHZCBS_0.getKey().equals(orderItemInfo.getYhzcbs())) {
            if (!StringUtils.isBlank(orderItemInfo.getZzstsgl())) {
                return generateErrorMap(OrderInfoContentEnum.CHECK_ISS7PRI_107106);
            }
        }

        /**
         * 订单明细信息-零税率标识
         */
        if (!StringUtils.isBlank(orderItemInfo.getLslbs())
                && !OrderInfoEnum.LSLBS_0.getKey().equals(orderItemInfo.getLslbs())
                && !OrderInfoEnum.LSLBS_1.getKey().equals(orderItemInfo.getLslbs())
                && !OrderInfoEnum.LSLBS_2.getKey().equals(orderItemInfo.getLslbs())
                && !OrderInfoEnum.LSLBS_3.getKey().equals(orderItemInfo.getLslbs())) {
            return generateErrorMap(OrderInfoContentEnum.CHECK_ISS7PRI_107138);
        }

        /**
         * 税率非空时,逻辑判断
         */
        if (!StringUtils.isBlank(orderItemInfo.getSl())) {
            /**
             * 增值税特殊管理不为空,不为不征税,不为免税,不为出口零税逻辑处理 如果是按5%简易征收需要保证税率为0.05
             * 如果是按3%简易征收需要保证税率为0.03 如果是简易征收需要保证税率为0.03或0.04或0.05
             * 如果是按5%简易征收减按1.5%计征需要保证税率为0.015
             */
            if ((!StringUtils.isBlank(orderItemInfo.getZzstsgl()))
                    && (!ConfigureConstant.STRING_BZS.equals(orderItemInfo.getZzstsgl()))
                    && (!ConfigureConstant.STRING_MS.equals(orderItemInfo.getZzstsgl()))
                    && (!ConfigureConstant.STRING_CKLS.equals(orderItemInfo.getZzstsgl()))) {
	            if (orderItemInfo.getZzstsgl().contains(ConfigureConstant.STRING_ERROR_PERCENT)) {
		            return generateErrorMap(OrderInfoContentEnum.COMMODITY_MESSAGE_SYNC_ERROR_173033);
	            }
	            switch (orderItemInfo.getZzstsgl()) {
		            case ConfigureConstant.STRING_JYZS5:
			            if (!ConfigureConstant.STRING_005.equals(orderItemInfo.getSl())) {
				            return generateErrorMap(OrderInfoContentEnum.CHECK_ISS7PRI_107108);
			            }
			            break;
		            case ConfigureConstant.STRING_JYZS3:
			            if (!ConfigureConstant.STRING_003.equals(orderItemInfo.getSl())) {
				            return generateErrorMap(OrderInfoContentEnum.CHECK_ISS7PRI_107109);
			            }
			            break;
		            case ConfigureConstant.STRING_JYZS:
			            if (!ConfigureConstant.STRING_003.equals(orderItemInfo.getSl())
					            || !ConfigureConstant.STRING_004.equals(orderItemInfo.getSl())
					            || !ConfigureConstant.STRING_005.equals(orderItemInfo.getSl())) {
				            return generateErrorMap(OrderInfoContentEnum.CHECK_ISS7PRI_107110);
			            }
			            break;
		            case ConfigureConstant.STRING_JYZS5_1:
			            if (!ConfigureConstant.STRING_0015.equals(orderItemInfo.getSl())) {
				            return generateErrorMap(OrderInfoContentEnum.CHECK_ISS7PRI_107111);
			            }
			
			            break;
		            default:
			            break;
	            }
	
            }
	
	        // 零税率标识不为空,税率必须为0
	        if ((!StringUtils.isBlank(orderItemInfo.getLslbs()))
			        && (ConfigureConstant.DOUBLE_PENNY_ZERO != new BigDecimal(orderItemInfo.getSl()).doubleValue())) {
		        return generateErrorMap(OrderInfoContentEnum.CHECK_ISS7PRI_107112);
	        }
	        // 零税率标识为空,税率不能为0
	        if ((StringUtils.isBlank(orderItemInfo.getLslbs())) && (new BigDecimal(ConfigureConstant.DOUBLE_PENNY_ZERO)
			        .doubleValue() == new BigDecimal(orderItemInfo.getSl()).doubleValue())) {
		        return generateErrorMap(OrderInfoContentEnum.CHECK_ISS7PRI_107113);
	        }
	        /**
	         * 税率不为空时,如果是专票,并且税率为0,提示错误,专票不可以开具0税率发票
	         */
	        boolean result = OrderInfoEnum.ORDER_INVOICE_TYPE_0.getKey().equals(fpzldm) && ConfigureConstant.STRING_000.equals(new BigDecimal(orderItemInfo.getSl()).setScale(ConfigureConstant.INT_2, RoundingMode.HALF_UP).toPlainString());
	
	        if (result) {
		        return generateErrorMap(OrderInfoContentEnum.CHECK_ISS7PRI_107118);
	        }
	
        }
	
	    // 订单明细信息中零税率标识为0/1/2, 但增值税特殊管理内容不为'出口零税/免税/不征税';
	    boolean result3 = StringUtils.isBlank(orderItemInfo.getZzstsgl())
			    && (OrderInfoEnum.LSLBS_0.getKey().equals(orderItemInfo.getLslbs())
			    || OrderInfoEnum.LSLBS_1.getKey().equals(orderItemInfo.getLslbs())
			    || OrderInfoEnum.LSLBS_2.getKey().equals(orderItemInfo.getLslbs()));
	    if (result3) {
		    return generateErrorMap(OrderInfoContentEnum.CHECK_ISS7PRI_107114);
	    }
	
	    if (OrderInfoEnum.LSLBS_0.getKey().equals(orderItemInfo.getLslbs())
			    && !ConfigureConstant.STRING_CKLS.equals(orderItemInfo.getLslbs())) {
		    return generateErrorMap(OrderInfoContentEnum.CHECK_ISS7PRI_107114);
	    }
	    if (OrderInfoEnum.LSLBS_1.getKey().equals(orderItemInfo.getLslbs())
			    && !ConfigureConstant.STRING_MS.equals(orderItemInfo.getZzstsgl())) {
		    return generateErrorMap(OrderInfoContentEnum.CHECK_ISS7PRI_107114);
	    }
	    if (OrderInfoEnum.LSLBS_2.getKey().equals(orderItemInfo.getLslbs())
			    && !ConfigureConstant.STRING_BZS.equals(orderItemInfo.getZzstsgl())) {
		    return generateErrorMap(OrderInfoContentEnum.CHECK_ISS7PRI_107114);
	    }
	    boolean result4 = OrderInfoEnum.LSLBS_3.getKey().equals(orderItemInfo.getLslbs())
			    && (!StringUtils.isBlank(orderItemInfo.getZzstsgl())
			    || !(OrderInfoEnum.YHZCBS_0.getKey().equals(orderItemInfo.getYhzcbs())));
	    if (result4) {
		    return generateErrorMap(OrderInfoContentEnum.CHECK_ISS7PRI_107140);
	    }
	
	    return checkResultMap;
    }


	@Override
	public Map<String, String> verifyDynamicEwmInfo(CommonOrderInfo commonOrder) {

        OrderInfo orderInfo = commonOrder.getOrderInfo();
        List<OrderItemInfo> orderItemInfoList = commonOrder.getOrderItemInfo();
        // 基础信息校验
        Map<String, String> resultMap = checkCommonOrderHead(orderInfo,orderItemInfoList);

		if (!OrderInfoContentEnum.SUCCESS.getKey().equals(resultMap.get(OrderManagementConstant.ERRORCODE))) {
			return resultMap;
		}


		// 订单主体-发票种类代码合法性(只能为0:专票;2:普票;41:卷票;51:电子票)

		//订单请求发票类型合法性
		if (StringUtils.isNotBlank(orderInfo.getFpzlDm())
				&& !OrderInfoEnum.ORDER_INVOICE_TYPE_004.getKey().equals(orderInfo.getFpzlDm())
				&& !OrderInfoEnum.ORDER_INVOICE_TYPE_007.getKey().equals(orderInfo.getFpzlDm())
				&& !OrderInfoEnum.ORDER_INVOICE_TYPE_0.getKey().equals(orderInfo.getFpzlDm())
				&& !OrderInfoEnum.ORDER_INVOICE_TYPE_2.getKey().equals(orderInfo.getFpzlDm())
				&& !OrderInfoEnum.ORDER_INVOICE_TYPE_51.getKey().equals(orderInfo.getFpzlDm())
				&& !OrderInfoEnum.ORDER_INVOICE_TYPE_026.getKey().equals(orderInfo.getFpzlDm())) {
			return generateErrorMap(OrderInfoContentEnum.CHECK_ISS7PRI_107004);
		}

		return resultMap;

	}

    private Map<String,String> checkCommonOrderHead(OrderInfo orderInfo, List<OrderItemInfo> orderItemInfoList) {

        // 声明校验结果map
        Map<String, String> checkResultMap = new HashMap<>(10);
        checkResultMap.put(OrderManagementConstant.ERRORCODE, OrderInfoContentEnum.SUCCESS.getKey());
        // 1.数据非空和长度校验
        if (orderInfo == null) {
	        return generateErrorMap(OrderInfoContentEnum.HANDLE_ISSUE_202004);
        }

        if (CollectionUtils.isEmpty(orderItemInfoList)) {
	        return generateErrorMap(OrderInfoContentEnum.HANDLE_ISSUE_202009);
        }

        /**
         * 订单主体-订单请求流水号
         */

        checkResultMap = CheckParamUtil.checkParam(OrderInfoContentEnum.CHECK_ISS7PRI_107014,
                orderInfo.getFpqqlsh());
        if (!OrderInfoContentEnum.SUCCESS.getKey().equals(checkResultMap.get(OrderManagementConstant.ERRORCODE))) {
            return checkResultMap;
        }

        /**
         * 订单主体-纳税人识别号
         */
        checkResultMap = checkNsrsbhParam(OrderInfoContentEnum.CHECK_ISS7PRI_107016,
                OrderInfoContentEnum.CHECK_ISS7PRI_107017, OrderInfoContentEnum.CHECK_ISS7PRI_107163,
                orderInfo.getFpqqlsh());
        if (!OrderInfoContentEnum.SUCCESS.getKey().equals(checkResultMap.get(OrderManagementConstant.ERRORCODE))) {
            return checkResultMap;
        }
        /**
         * 订单主体-纳税人名称
         */
        checkResultMap = CheckParamUtil.checkParam(OrderInfoContentEnum.CHECK_ISS7PRI_107018,
                orderInfo.getNsrmc());
        if (!OrderInfoContentEnum.SUCCESS.getKey().equals(checkResultMap.get(OrderManagementConstant.ERRORCODE))) {
            return checkResultMap;
        }

        /**
         * 订单主体-开票类型
         */
        checkResultMap = CheckParamUtil.checkParam(OrderInfoContentEnum.CHECK_ISS7PRI_107020,
                orderInfo.getKplx());
        if (!OrderInfoContentEnum.SUCCESS.getKey().equals(checkResultMap.get(OrderManagementConstant.ERRORCODE))) {
            return checkResultMap;
        }

        // 订单主体-开票类型合法性(开票类型只能为0和1：0蓝字发票；1红字发票)
        if (!OrderInfoEnum.ORDER_BILLING_INVOICE_TYPE_0.getKey().equals(orderInfo.getKplx())
                && !OrderInfoEnum.ORDER_BILLING_INVOICE_TYPE_1.getKey().equals(orderInfo.getKplx())) {
            return generateErrorMap(OrderInfoContentEnum.CHECK_ISS7PRI_107021);
        }

        /**
         * 订单主体-编码表版本号
         */
        checkResultMap = CheckParamUtil.checkParam(OrderInfoContentEnum.CHECK_ISS7PRI_107097,
                orderInfo.getBbmBbh());
        if (!OrderInfoContentEnum.SUCCESS.getKey().equals(checkResultMap.get(OrderManagementConstant.ERRORCODE))) {
            return checkResultMap;
        }

        /**
         * 订单主体-销售方纳税人识别号
         */
        checkResultMap = checkNsrsbhParam(OrderInfoContentEnum.CHECK_ISS7PRI_107022,
                OrderInfoContentEnum.CHECK_ISS7PRI_107017, OrderInfoContentEnum.CHECK_ISS7PRI_107163,
                orderInfo.getXhfNsrsbh());
        if (!OrderInfoContentEnum.SUCCESS.getKey().equals(checkResultMap.get(OrderManagementConstant.ERRORCODE))) {
            return checkResultMap;
        }

        /**
         * 订单主体-销售方纳税人名称
         */
        checkResultMap = CheckParamUtil.checkParam(OrderInfoContentEnum.CHECK_ISS7PRI_107024,
                orderInfo.getXhfMc());
        if (!OrderInfoContentEnum.SUCCESS.getKey().equals(checkResultMap.get(OrderManagementConstant.ERRORCODE))) {
            return checkResultMap;
        }

        /**
         * 校验销方税号为必填, 其他销方信息为非必填,如果填写进行合法性校验, 校验地址+电话总长度不能大于100
         * 校验银行名称+帐号总长度不能大于100
         */

        /**
         * 订单主体-销售方地址
         */
        checkResultMap = CheckParamUtil.checkParam(OrderInfoContentEnum.CHECK_ISS7PRI_107282,
                orderInfo.getXhfDz());
        if (!OrderInfoContentEnum.SUCCESS.getKey().equals(checkResultMap.get(OrderManagementConstant.ERRORCODE))) {
            return checkResultMap;
        }

        /**
         * 订单主体-销售方电话
         */
        checkResultMap = CheckParamUtil.checkParam(OrderInfoContentEnum.CHECK_ISS7PRI_107283,
                orderInfo.getXhfDh());
        if (!OrderInfoContentEnum.SUCCESS.getKey().equals(checkResultMap.get(OrderManagementConstant.ERRORCODE))) {
            return checkResultMap;
        }

        /**
         * 订单主体-销售方银行
         */
        checkResultMap = CheckParamUtil.checkParam(OrderInfoContentEnum.CHECK_ISS7PRI_107284,
                orderInfo.getXhfYh());
        if (!OrderInfoContentEnum.SUCCESS.getKey().equals(checkResultMap.get(OrderManagementConstant.ERRORCODE))) {
            return checkResultMap;
        }

        /**
         * 订单主体-销售方帐号
         */
        checkResultMap = CheckParamUtil.checkParam(OrderInfoContentEnum.CHECK_ISS7PRI_107285,
                orderInfo.getXhfZh());
        if (!OrderInfoContentEnum.SUCCESS.getKey().equals(checkResultMap.get(OrderManagementConstant.ERRORCODE))) {
            return checkResultMap;
        }

        /**
         * 订单主体-销售方地址和电话总长度 TODO 由于企业区分不开地址电话,所以校验支持地址电话总长度100,默认应该是85
         */
	    String dzDh = StringUtils.isBlank(orderInfo.getXhfDz()) ? ""
			    : orderInfo.getXhfDz()
			    + (StringUtils.isBlank(orderInfo.getXhfDh()) ? "" : orderInfo.getXhfDh());
	    checkResultMap = CheckParamUtil.checkParam(OrderInfoContentEnum.CHECK_ISS7PRI_107267, dzDh);
        if (!OrderInfoContentEnum.SUCCESS.getKey().equals(checkResultMap.get(OrderManagementConstant.ERRORCODE))) {
            return checkResultMap;
        }

        /**
         * 订单主体-销售方银行和帐号总长度 TODO 由于企业区分不开银行帐号,所以校验支持银行帐号总长度100,默认应该是85
         */
	    String yhZh = StringUtils.isBlank(orderInfo.getXhfYh()) ? ""
			    : orderInfo.getXhfYh()
			    + (StringUtils.isBlank(orderInfo.getXhfZh()) ? "" : orderInfo.getXhfZh());
	    checkResultMap = CheckParamUtil.checkParam(OrderInfoContentEnum.CHECK_ISS7PRI_107268, yhZh);
        if (!OrderInfoContentEnum.SUCCESS.getKey().equals(checkResultMap.get(OrderManagementConstant.ERRORCODE))) {
            return checkResultMap;
        }

        /**
         * 订单主体-开票人
         */
        checkResultMap = CheckParamUtil.checkParam(OrderInfoContentEnum.CHECK_ISS7PRI_107044,
                orderInfo.getKpr());
        if (!OrderInfoContentEnum.SUCCESS.getKey().equals(checkResultMap.get(OrderManagementConstant.ERRORCODE))) {
            return checkResultMap;
        }

        /**
         * 订单主体-开票人
         */

        checkResultMap = CheckParamUtil.checkParam(OrderInfoContentEnum.CHECK_ISS7PRI_107044, orderInfo.getKpr());
        if (!OrderInfoContentEnum.SUCCESS.getKey().equals(checkResultMap.get(

                OrderManagementConstant.ERRORCODE))) { return checkResultMap;
        }


        /**
         * 订单主体-收款人
         */
        checkResultMap = CheckParamUtil.checkParam(OrderInfoContentEnum.CHECK_ISS7PRI_107046,
                orderInfo.getSkr());
        if (!OrderInfoContentEnum.SUCCESS.getKey().equals(checkResultMap.get(OrderManagementConstant.ERRORCODE))) {
            return checkResultMap;
        }

        /**
         * 订单主体-复核人
         */
        checkResultMap = CheckParamUtil.checkParam(OrderInfoContentEnum.CHECK_ISS7PRI_107048,
                orderInfo.getFhr());
        if (!OrderInfoContentEnum.SUCCESS.getKey().equals(checkResultMap.get(OrderManagementConstant.ERRORCODE))) {
            return checkResultMap;
        }

        /**
         * 订单主体-订单号
         */
        checkResultMap = CheckParamUtil.checkParam(OrderInfoContentEnum.CHECK_ISS7PRI_107067,
                orderInfo.getDdh());
        if (!OrderInfoContentEnum.SUCCESS.getKey().equals(checkResultMap.get(OrderManagementConstant.ERRORCODE))) {
            return checkResultMap;
        }

        /**
         * 订单主体-订单日期
         */
        checkResultMap = CheckParamUtil.checkParam(OrderInfoContentEnum.CHECK_ISS7PRI_107068,
                orderInfo.getDdrq() == null ? "" : DateUtil.format(orderInfo.getDdrq(),"yyyy-MM-dd HH:mm:ss"));
        if (!OrderInfoContentEnum.SUCCESS.getKey().equals(checkResultMap.get(OrderManagementConstant.ERRORCODE))) {
            return checkResultMap;
        }

        /**
         * 发票主体-门店号
         */
        checkResultMap = CheckParamUtil.checkParam(OrderInfoContentEnum.CHECK_ISS7PRI_107293,
                orderInfo.getMdh());
        if (!OrderInfoContentEnum.SUCCESS.getKey().equals(checkResultMap.get(OrderManagementConstant.ERRORCODE))) {
            return checkResultMap;
        }

        /**
         * 订单主体-价税合计
         */
        checkResultMap = CheckParamUtil.checkParam(OrderInfoContentEnum.CHECK_ISS7PRI_107141,
                orderInfo.getKphjje());
	    if (!OrderInfoContentEnum.SUCCESS.getKey().equals(checkResultMap.get(OrderManagementConstant.ERRORCODE))) {
		    return checkResultMap;
	    }
	    // 价税合计金额不能为0或者0.00
	    if (ConfigureConstant.STRING_0.equals(orderInfo.getKphjje())
			    || ConfigureConstant.STRING_000.equals(orderInfo.getKphjje())
			    || ConfigureConstant.INT_2 != ValidateUtil.checkNumberic(orderInfo.getKphjje())) {
		    return generateErrorMap(OrderInfoContentEnum.CHECK_ISS7PRI_107066);
	    }
	    // 开票类型为0(蓝票)时,金额必须大于0
	    boolean result5 = (StringUtils.isNotBlank(orderInfo.getKplx())
			    && !OrderInfoEnum.ORDER_BILLING_INVOICE_TYPE_0.getKey().equals(orderInfo.getKplx()))
			    || ConfigureConstant.DOUBLE_PENNY_ZERO >= new BigDecimal(orderInfo.getKphjje()).doubleValue();
	    if (result5) {
		    return generateErrorMap(OrderInfoContentEnum.CHECK_ISS7PRI_107080);
	    }
	
	    /**
	     * 订单主体-合计金额
	     */
	    checkResultMap = CheckParamUtil.checkParam(OrderInfoContentEnum.CHECK_ISS7PRI_107142, orderInfo.getKphjje());
	    if (!OrderInfoContentEnum.SUCCESS.getKey().equals(checkResultMap.get(OrderManagementConstant.ERRORCODE))) {
		    return checkResultMap;
	    }
        // 合计金额为不为0时,需要保证金额为小数点后两位
        if (ConfigureConstant.DOUBLE_PENNY_ZERO != new BigDecimal(orderInfo.getKphjje()).doubleValue()
                && ConfigureConstant.INT_2 != ValidateUtil.checkNumberic(orderInfo.getKphjje())) {
            return generateErrorMap(OrderInfoContentEnum.CHECK_ISS7PRI_107135);
        }

        /**
         * 订单主体-合计税额
         */
        /*checkResultMap = CheckParamUtil.checkParam(OrderInfoContentEnum.CHECK_ISS7PRI_107143,
                orderInfo.getHjse());
        if (!OrderInfoContentEnum.SUCCESS.getKey().equals(checkResultMap.get(OrderManagementConstant.ERRORCODE))) {
            return checkResultMap;
        }
        // 合计金额为不为0时,需要保证金额为小数点后两位
        if (ConfigureConstant.DOUBLE_PENNY_ZERO != new BigDecimal(orderInfo.getHjse()).doubleValue()
                && ConfigureConstant.INT_2 != ValidateUtil.checkNumberic(orderInfo.getHjse())) {
            return generateErrorMap(OrderInfoContentEnum.CHECK_ISS7PRI_107136);
        }*/

        if (ConfigureConstant.MAX_ITEM_LENGTH <= orderItemInfoList.size()) {
            return generateErrorMap(OrderInfoContentEnum.INVOICE_AUTO_NUMBER);
        }

        /**
         * 金额关系合法性校验
         */
        if (!StringUtils.isBlank(orderInfo.getKphjje()) && !StringUtils.isBlank(orderInfo.getHjbhsje())
                && !StringUtils.isBlank(orderInfo.getHjse())) {

            double differ = MathUtil.sub(orderInfo.getKphjje(),
                    String.valueOf(MathUtil.add(orderInfo.getHjbhsje(), orderInfo.getHjse())));
            // 如果误差值等于含税金额,说明是含税金额不作校验,如果是尾插不等于0,校验返回
            if (DecimalCalculateUtil.decimalFormatToString(orderInfo.getKphjje(), ConfigureConstant.INT_2).equals(
                    DecimalCalculateUtil.decimalFormatToString(String.valueOf(differ), ConfigureConstant.INT_2))) {

            } else if (ConfigureConstant.DOUBLE_PENNY_ZERO != differ) {
                checkResultMap = generateErrorMap(OrderInfoContentEnum.INVOICE_JSHJ_ERROR);
                return checkResultMap;
            }

        }

        /**
         * 明细行数据与发票头数据进行校验
         */
        BigDecimal kphjje = new BigDecimal(orderInfo.getKphjje());
        BigDecimal sumKphjje = BigDecimal.ZERO;
        for (int j = 0; j < orderItemInfoList.size(); j++) {
	        Map<String, String> checkItemResultMap = checkCommonOrderItemsV3(orderItemInfoList.get(j),
			        orderItemInfoList.size(), orderInfo.getFpzlDm());
            if (!OrderInfoContentEnum.SUCCESS.getKey()
                    .equals(checkItemResultMap.get(OrderManagementConstant.ERRORCODE))) {
                return checkItemResultMap;
            }

            if (OrderInfoEnum.HSBZ_1.getKey().equals(orderItemInfoList.get(j).getHsbz())) {
                sumKphjje = sumKphjje.add(new BigDecimal(orderItemInfoList.get(j).getXmje()));
            } else {
                sumKphjje = sumKphjje.add(new BigDecimal(orderItemInfoList.get(j).getXmje()))
                        .add(new BigDecimal(orderItemInfoList.get(j).getSe()));
            }
        }

        if (kphjje.subtract(sumKphjje).abs().compareTo(BigDecimal.ZERO) > 0) {

            return generateErrorMap(OrderInfoContentEnum.PRICE_TAX_SEPARATION_NE_KPHJJE);
        }

        return checkResultMap;


    }


}
