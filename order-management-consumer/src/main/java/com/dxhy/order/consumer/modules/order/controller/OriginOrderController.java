package com.dxhy.order.consumer.modules.order.controller;

import cn.hutool.core.date.DateUtil;
import com.dxhy.order.constant.ConfigureConstant;
import com.dxhy.order.constant.OrderInfoContentEnum;
import com.dxhy.order.constant.OrderManagementConstant;
import com.dxhy.order.consumer.annotation.SysLog;
import com.dxhy.order.consumer.model.page.OrderListQuery;
import com.dxhy.order.consumer.modules.order.service.OriginOrderService;
import com.dxhy.order.consumer.utils.PageBeanConvertUtil;
import com.dxhy.order.model.PageUtils;
import com.dxhy.order.model.R;
import com.dxhy.order.utils.JsonUtils;
import com.dxhy.order.utils.NsrsbhUtils;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.ServletResponse;
import java.io.IOException;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author ：杨士勇
 * @ClassName ：OriginOrderController
 * @Description ：原始订单控制层
 * @date ：2019年12月9日 下午2:21:54
 */

@Api(value = "原始订单", tags = {"订单模块"})
@RestController
@RequestMapping("/originOrder")
@Slf4j
public class OriginOrderController {
	
	
	private static final String LOGGER_MSG = "(原始订单)";
	
	private static final String PATTERN_JE = "^(([-1-9]\\d*)|([0]))(\\.(\\d){0,2})?$";
	
	@Resource
	private OriginOrderService originOrderService;
	
	/**
	 * @param @param  orderBatchQuery
	 * @param @return
	 * @return R
	 * @throws
	 * @Description ：原始订单列表
	 */
	@PostMapping("/originOrderList")
	@ApiOperation(value = "原始订单列表接口", notes = "原始订单管理-原始订单列表接口")
	@SysLog(operation = "原始订单列表接口", operationDesc = "原始订单列表接口", key = "原始订单列表")
	public R originOrderList(@RequestBody OrderListQuery orderBatchQuery) {
		
		log.info("{}原始订单列表接口，请求参数:{}", LOGGER_MSG, JsonUtils.getInstance().toJsonString(orderBatchQuery));
		try {
			if (StringUtils.isNotBlank(orderBatchQuery.getMinKphjje())
					&& StringUtils.isNotBlank(orderBatchQuery.getMaxKphjje())) {
				/**
				 * 判断金额 是否正确
				 */
				if (Double.parseDouble(orderBatchQuery.getMinKphjje()) > Double
						.parseDouble(orderBatchQuery.getMaxKphjje())) {
					log.error("{}开始金额不能大于结束金额", LOGGER_MSG);
					return R.error(OrderInfoContentEnum.INVOICE_FPKJ_JE_ERROR.getKey(),
							OrderInfoContentEnum.INVOICE_FPKJ_JE_ERROR.getMessage());
				}
			}
			
			Pattern pattern = Pattern.compile(PATTERN_JE);
			if (StringUtils.isNotBlank(orderBatchQuery.getMinKphjje())) {
				Matcher minMatch = pattern.matcher(orderBatchQuery.getMinKphjje());
				if (minMatch.matches() == false) {
					log.error("{}金额格式错误保留两位小数", LOGGER_MSG);
					return R.error(OrderInfoContentEnum.INVOICE_FPKJ_JE_ERROR.getKey(),
							OrderInfoContentEnum.INVOICE_FPKJ_JE_ERROR.getMessage());
				}
				
				if (Double.parseDouble(orderBatchQuery.getMinKphjje()) < ConfigureConstant.DOUBLE_PENNY_ZERO) {
					orderBatchQuery.setMinKphjje("0.00");
				}
				
			} else {
				//原始订单不查询红票
				orderBatchQuery.setMinKphjje("0.00");
			}
			
			if (StringUtils.isNotBlank(orderBatchQuery.getMaxKphjje())) {
				Matcher maxMatch = pattern.matcher(orderBatchQuery.getMaxKphjje());
				if (maxMatch.matches() == false) {
					log.error("{}金额格式错误保留两位小数", LOGGER_MSG);
					return R.error(OrderInfoContentEnum.INVOICE_FPKJ_JE_ERROR.getKey(),
							OrderInfoContentEnum.INVOICE_FPKJ_JE_ERROR.getMessage());
				}
				if (Double.parseDouble(orderBatchQuery.getMaxKphjje()) < ConfigureConstant.DOUBLE_PENNY_ZERO) {
					log.error("原始订单最大金额不能小于0");
					return R.error(OrderInfoContentEnum.INVOICE_FPKJ_JE_ERROR.getKey(), "原始订单最大金额不能小于0");
					
				}
			}
			
			if (StringUtils.isNotBlank((orderBatchQuery.getStartTime()))
					&& StringUtils.isNotBlank(orderBatchQuery.getEndTime())) {
				Date starttime = DateUtil.parse(orderBatchQuery.getStartTime(), "yyyy-MM-dd");
				Date endtime = DateUtil.parse(orderBatchQuery.getEndTime(), "yyyy-MM-dd");
				if (starttime.after(endtime)) {
					log.error("{}开始时间不能大于结束时间", LOGGER_MSG);
					return R.error(OrderInfoContentEnum.ORDER_TIME_ERROR.getKey(),
							OrderInfoContentEnum.ORDER_TIME_ERROR.getMessage());
				}
			}
			
			
			// 数据转换
			Map<String, Object> paramMap = PageBeanConvertUtil.convertToMap(orderBatchQuery);
			
			if (StringUtils.isBlank(orderBatchQuery.getXhfNsrsbh())) {
				log.error("{},请求税号为空!", LOGGER_MSG);
				return R.error(OrderInfoContentEnum.TAXCODE_ISNULL);
			}
			
			List<String> shList = NsrsbhUtils.transShListByXhfNsrsbh(orderBatchQuery.getXhfNsrsbh());
			
			paramMap.put("orderStatus", null);
			PageUtils queryOriginList = originOrderService.queryOriginList(paramMap, shList);
			return R.ok().put(OrderManagementConstant.CODE, OrderInfoContentEnum.SUCCESS.getKey())
					.put(OrderManagementConstant.MESSAGE, OrderInfoContentEnum.SUCCESS.getMessage())
					.put(OrderManagementConstant.DATA, queryOriginList);
		} catch (NumberFormatException e) {
			log.error("原始订单列表查询异常：{}", e);
			return R.error().put(OrderManagementConstant.CODE, ConfigureConstant.STRING_9999).put(OrderManagementConstant.MESSAGE, "列表查询异常!");
		}
		
	}
	
	/**
	 * @param @param  orderBatchQuery
	 * @param @return
	 * @return R
	 * @throws
	 * @Description ：票单比对列表
	 */
	@PostMapping("/originOrderCompareList")
	@ApiOperation(value = "票单比对列表", notes = "原始订单管理-票单比对列表")
	@SysLog(operation = "票单比对列表", operationDesc = "票单比对列表", key = "票单比对")
	public R originOrderCompareList(@RequestBody OrderListQuery orderBatchQuery) {
		
		log.info("{}原始订单列表接口，请求参数:{}", LOGGER_MSG, JsonUtils.getInstance().toJsonString(orderBatchQuery));
		try {
			if (StringUtils.isNotBlank(orderBatchQuery.getMinKphjje())
					&& StringUtils.isNotBlank(orderBatchQuery.getMaxKphjje())) {
				/**
				 * 判断金额 是否正确
				 */
				if (Double.parseDouble(orderBatchQuery.getMinKphjje()) > Double
						.parseDouble(orderBatchQuery.getMaxKphjje())) {
					log.error("{}开始金额不能大于结束金额", LOGGER_MSG);
					return R.error(OrderInfoContentEnum.INVOICE_FPKJ_JE_ERROR.getKey(),
							OrderInfoContentEnum.INVOICE_FPKJ_JE_ERROR.getMessage());
				}
			}
			
			Pattern pattern = Pattern.compile(PATTERN_JE);
			if (StringUtils.isNotBlank(orderBatchQuery.getMinKphjje())) {
				Matcher minMatch = pattern.matcher(orderBatchQuery.getMinKphjje());
				if (minMatch.matches() == false) {
					log.error("{}金额格式错误保留两位小数", LOGGER_MSG);
					return R.error(OrderInfoContentEnum.INVOICE_FPKJ_JE_ERROR.getKey(),
							OrderInfoContentEnum.INVOICE_FPKJ_JE_ERROR.getMessage());
				}
			}
			if (StringUtils.isNotBlank(orderBatchQuery.getMaxKphjje())) {
				Matcher maxMatch = pattern.matcher(orderBatchQuery.getMaxKphjje());
				if (maxMatch.matches() == false) {
					log.error("{}金额格式错误保留两位小数", LOGGER_MSG);
					return R.error(OrderInfoContentEnum.INVOICE_FPKJ_JE_ERROR.getKey(),
							OrderInfoContentEnum.INVOICE_FPKJ_JE_ERROR.getMessage());
				}
			}
			
			if (StringUtils.isBlank(orderBatchQuery.getStartTime())
					|| StringUtils.isBlank(orderBatchQuery.getEndTime())) {
				
			} else {
				Date starttime = DateUtil.parse(orderBatchQuery.getStartTime(), "yyyy-MM-dd");
				Date endtime = DateUtil.parse(orderBatchQuery.getEndTime(), "yyyy-MM-dd");
				if (starttime.after(endtime)) {
					log.error("{}开始时间不能大于结束时间", LOGGER_MSG);
					return R.error(OrderInfoContentEnum.ORDER_TIME_ERROR.getKey(),
							OrderInfoContentEnum.ORDER_TIME_ERROR.getMessage());
				}
			}
			// 数据转换
			Map<String, Object> paramMap = PageBeanConvertUtil.convertToMap(orderBatchQuery);
			
			if (StringUtils.isBlank(orderBatchQuery.getXhfNsrsbh())) {
				log.error("{},请求税号为空!", LOGGER_MSG);
				return R.error(OrderInfoContentEnum.TAXCODE_ISNULL);
			}
			
			List<String> shList = NsrsbhUtils.transShListByXhfNsrsbh(orderBatchQuery.getXhfNsrsbh());
			
			paramMap.put("orderStatus", null);
			PageUtils executeCompareOriginOrderAndInvoice = originOrderService.executeCompareOriginOrderAndInvoice(paramMap, shList);
			
			return R.ok().put(OrderManagementConstant.CODE, OrderInfoContentEnum.SUCCESS.getKey())
					.put(OrderManagementConstant.MESSAGE, OrderInfoContentEnum.SUCCESS.getMessage())
					.put(OrderManagementConstant.DATA, executeCompareOriginOrderAndInvoice);
		} catch (NumberFormatException e) {
			log.error("原始订单列表查询异常：{}", e);
			return R.error().put(OrderManagementConstant.CODE, ConfigureConstant.STRING_9999).put(OrderManagementConstant.MESSAGE, "列表查询异常!");
		}
	}
	
	/**
	 * @param @param  orderBatchQuery
	 * @param @return
	 * @return R
	 * @throws
	 * @Description ：票单比对列表金额统计
	 */
	
	@PostMapping("/originOrderCompareListCount")
	@ApiOperation(value = "票单比对列表统计", notes = "原始订单管理-票单比对列表统计")
	@SysLog(operation = "票单比对列表", operationDesc = "票单比对列表", key = "票单比对")
	public R originOrderCompareListCount(@RequestBody OrderListQuery orderBatchQuery) {
		
		log.info("{}原始订单列表接口，请求参数:{}", LOGGER_MSG, JsonUtils.getInstance().toJsonString(orderBatchQuery));
		try {
			if (StringUtils.isNotBlank(orderBatchQuery.getMinKphjje())
					&& StringUtils.isNotBlank(orderBatchQuery.getMaxKphjje())) {
				/**
				 * 判断金额 是否正确
				 */
				if (Double.parseDouble(orderBatchQuery.getMinKphjje()) > Double
						.parseDouble(orderBatchQuery.getMaxKphjje())) {
					log.error("{}开始金额不能大于结束金额", LOGGER_MSG);
					return R.error(OrderInfoContentEnum.INVOICE_FPKJ_JE_ERROR.getKey(),
							OrderInfoContentEnum.INVOICE_FPKJ_JE_ERROR.getMessage());
				}
			}
			
			Pattern pattern = Pattern.compile(PATTERN_JE);
			if (StringUtils.isNotBlank(orderBatchQuery.getMinKphjje())) {
				Matcher minMatch = pattern.matcher(orderBatchQuery.getMinKphjje());
				if (minMatch.matches() == false) {
					log.error("{}金额格式错误保留两位小数", LOGGER_MSG);
					return R.error(OrderInfoContentEnum.INVOICE_FPKJ_JE_ERROR.getKey(),
							OrderInfoContentEnum.INVOICE_FPKJ_JE_ERROR.getMessage());
				}
			}
			if (StringUtils.isNotBlank(orderBatchQuery.getMaxKphjje())) {
				Matcher maxMatch = pattern.matcher(orderBatchQuery.getMaxKphjje());
				if (maxMatch.matches() == false) {
					log.error("{}金额格式错误保留两位小数", LOGGER_MSG);
					return R.error(OrderInfoContentEnum.INVOICE_FPKJ_JE_ERROR.getKey(),
							OrderInfoContentEnum.INVOICE_FPKJ_JE_ERROR.getMessage());
				}
			}
			
			if (StringUtils.isNotBlank(orderBatchQuery.getStartTime())
					&& StringUtils.isNotBlank(orderBatchQuery.getEndTime())) {
				Date starttime = DateUtil.parse(orderBatchQuery.getStartTime(), "yyyy-MM-dd");
				Date endtime = DateUtil.parse(orderBatchQuery.getEndTime(), "yyyy-MM-dd");
				if (starttime.after(endtime)) {
					log.error("{}开始时间不能大于结束时间", LOGGER_MSG);
					return R.error(OrderInfoContentEnum.ORDER_TIME_ERROR.getKey(),
							OrderInfoContentEnum.ORDER_TIME_ERROR.getMessage());
				}
			}
			
			// 数据转换
			Map<String, Object> paramMap = PageBeanConvertUtil.convertToMap(orderBatchQuery);
			if (StringUtils.isBlank(orderBatchQuery.getXhfNsrsbh())) {
				log.error("{},请求税号为空!", LOGGER_MSG);
				return R.error(OrderInfoContentEnum.TAXCODE_ISNULL);
			}
			
			List<String> shList = NsrsbhUtils.transShListByXhfNsrsbh(orderBatchQuery.getXhfNsrsbh());
			paramMap.put("orderStatus", null);
			paramMap.put("ddzt", null);
			Map<String, String> resultMap = originOrderService.queryCompareOriginOrderAndInvoiceCounter(paramMap, shList);
			
			return R.ok().put(OrderManagementConstant.CODE, OrderInfoContentEnum.SUCCESS.getKey())
					.put(OrderManagementConstant.MESSAGE, OrderInfoContentEnum.SUCCESS.getMessage())
					.put(OrderManagementConstant.DATA, resultMap);
		} catch (NumberFormatException e) {
			log.error("原始订单列表查询异常：{}", e);
			return R.error().put(OrderManagementConstant.CODE, ConfigureConstant.STRING_9999).put(OrderManagementConstant.MESSAGE, "列表查询异常!");
		}
	}
	
	
	/**
	 * @param @param  originOrderId
	 * @param @return
	 * @return R
	 * @throws
	 * @Description ：票单比对详情
	 */
	@PostMapping("/originOrderCompareDetail")
	@ApiOperation(value = "票单比对详情", notes = "原始订单管理-票单比对详情")
	@SysLog(operation = "票单比对详情", operationDesc = "票单比对详情", key = "票单比对详情")
	public R originOrderCompareDetail(@ApiParam(name = "originOrderId", value = "原始订单id", required = true) @RequestParam(value = "originOrderId", required = true) String originOrderId,
	                                  @ApiParam(name = "xhfNsrsbh", value = "销货方纳税人识别号", required = true) @RequestParam(value = "xhfNsrsbh", required = true) String xhfNsrsbh) {
		
		log.info("{}原始订单详情接口，请求参数:{}", LOGGER_MSG, originOrderId);
		try {
			if (StringUtils.isBlank(xhfNsrsbh)) {
				log.error("{},请求税号为空!", LOGGER_MSG);
				return R.error(OrderInfoContentEnum.TAXCODE_ISNULL);
			}
			
			List<String> shList = NsrsbhUtils.transShListByXhfNsrsbh(xhfNsrsbh);
			// 数据转换
			Map<String, Object> queryOriginOrderDetail = originOrderService.queryOriginOrderDetail(originOrderId, shList);
			
			return R.ok().put(OrderManagementConstant.CODE, OrderInfoContentEnum.SUCCESS.getKey())
					.put(OrderManagementConstant.MESSAGE, OrderInfoContentEnum.SUCCESS.getMessage())
					.put(OrderManagementConstant.DATA, queryOriginOrderDetail);
		} catch (NumberFormatException e) {
			log.error("原始订单列表查询异常：{}", e);
			return R.error().put(OrderManagementConstant.CODE, ConfigureConstant.STRING_9999).put(OrderManagementConstant.MESSAGE, "列表查询异常!");
		}
	}
	
	/**
	 * @param @param  orderBatchQuery
	 * @param @return
	 * @return R
	 * @throws
	 * @throws
	 * @Title : exportOriginOrderCompare
	 * @Description ：票单比对导出
	 */
	@PostMapping("/exportOriginOrderCompare")
	@ApiOperation(value = "票单比对导出", notes = "原始订单管理-票单比对导出")
	@SysLog(operation = "票单比对导出", operationDesc = "票单比对导出", key = "票单比对导出")
	public R exportOriginOrderCompare(@RequestBody OrderListQuery orderBatchQuery, ServletResponse response) {
		
		log.info("{}原始订单列表接口，请求参数:{}", LOGGER_MSG, JsonUtils.getInstance().toJsonString(orderBatchQuery));
		try {
			if (StringUtils.isNotBlank(orderBatchQuery.getMinKphjje())
					&& StringUtils.isNotBlank(orderBatchQuery.getMaxKphjje())) {
				/**
				 * 判断金额 是否正确
				 */
				if (Double.parseDouble(orderBatchQuery.getMinKphjje()) > Double
						.parseDouble(orderBatchQuery.getMaxKphjje())) {
					log.error("{}开始金额不能大于结束金额", LOGGER_MSG);
					return R.error(OrderInfoContentEnum.INVOICE_FPKJ_JE_ERROR.getKey(),
							OrderInfoContentEnum.INVOICE_FPKJ_JE_ERROR.getMessage());
				}
			}
			
			Pattern pattern = Pattern.compile(PATTERN_JE);
			if (StringUtils.isNotBlank(orderBatchQuery.getMinKphjje())) {
				Matcher minMatch = pattern.matcher(orderBatchQuery.getMinKphjje());
				if (minMatch.matches() == false) {
					log.error("{}金额格式错误保留两位小数", LOGGER_MSG);
					return R.error(OrderInfoContentEnum.INVOICE_FPKJ_JE_ERROR.getKey(),
							OrderInfoContentEnum.INVOICE_FPKJ_JE_ERROR.getMessage());
				}
			}
			if (StringUtils.isNotBlank(orderBatchQuery.getMaxKphjje())) {
				Matcher maxMatch = pattern.matcher(orderBatchQuery.getMaxKphjje());
				if (maxMatch.matches() == false) {
					log.error("{}金额格式错误保留两位小数", LOGGER_MSG);
					return R.error(OrderInfoContentEnum.INVOICE_FPKJ_JE_ERROR.getKey(),
							OrderInfoContentEnum.INVOICE_FPKJ_JE_ERROR.getMessage());
				}
			}
			
			if (StringUtils.isBlank(orderBatchQuery.getStartTime())
					|| StringUtils.isBlank(orderBatchQuery.getEndTime())) {
				
			} else {
				Date starttime = DateUtil.parse(orderBatchQuery.getStartTime(), "yyyy-MM-dd");
				Date endtime = DateUtil.parse(orderBatchQuery.getEndTime(), "yyyy-MM-dd");
				if (starttime.after(endtime)) {
					log.error("{}开始时间不能大于结束时间", LOGGER_MSG);
					return R.error(OrderInfoContentEnum.ORDER_TIME_ERROR.getKey(),
							OrderInfoContentEnum.ORDER_TIME_ERROR.getMessage());
				}
			}
			// 数据转换
			Map<String, Object> paramMap = PageBeanConvertUtil.convertToMap(orderBatchQuery);
			
			if (StringUtils.isBlank(orderBatchQuery.getXhfNsrsbh())) {
				log.error("{},请求税号为空!", LOGGER_MSG);
				return R.error(OrderInfoContentEnum.TAXCODE_ISNULL);
			}
			
			List<String> shList = NsrsbhUtils.transShListByXhfNsrsbh(orderBatchQuery.getXhfNsrsbh());
			paramMap.put("orderStatus", null);
			paramMap.put("ddzt", null);
			originOrderService.exportCompareOriginOrderAndInvoice(paramMap, response.getOutputStream(), shList);
			
			return R.ok().put(OrderManagementConstant.CODE, OrderInfoContentEnum.SUCCESS.getKey())
					.put(OrderManagementConstant.MESSAGE, OrderInfoContentEnum.SUCCESS.getMessage())
					.put(OrderManagementConstant.DATA, new HashMap<String, Object>(5));
		} catch (NumberFormatException | IOException e) {
			log.error("原始订单列表查询异常：{}", e);
			return R.error().put(OrderManagementConstant.CODE, ConfigureConstant.STRING_9999).put(OrderManagementConstant.MESSAGE, "列表查询异常!");
		}
	}
	
}
