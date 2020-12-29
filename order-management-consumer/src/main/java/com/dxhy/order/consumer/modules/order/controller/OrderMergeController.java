package com.dxhy.order.consumer.modules.order.controller;

import com.alibaba.fastjson.JSON;
import com.dxhy.order.constant.OrderInfoContentEnum;
import com.dxhy.order.consumer.annotation.SysLog;
import com.dxhy.order.consumer.modules.order.service.IOrderMergeService;
import com.dxhy.order.consumer.utils.InterfaceResponseUtils;
import com.dxhy.order.model.R;
import com.dxhy.order.utils.JsonUtils;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Map;

/**
 * 订单合并
 *
 * @author 陈玉航
 * @version 1.0 Created on 2018年8月2日 下午5:59:51
 */
@Api(value = "订单合并", tags = {"订单模块"})
@RestController
@RequestMapping("/orderMerge")
@Slf4j
public class OrderMergeController {
	
	private static final String LOGGER_MSG = "(订单合并接口)";
	
	@Resource
	private IOrderMergeService orderMergeService;
	
	
	/**
	 * 订单合并数据校验
	 *
	 * @return
	 * @author: 陈玉航
	 * @date: Created on 2018年7月31日 上午10:51:48
	 */
	@ApiOperation(value = "订单合并数据校验", notes = "订单合并-订单合并数据校验")
	@PostMapping("/mergeCheck")
	@SysLog(operation = "订单合并校验rest接口", operationDesc = "多个订单合并一个订单校验数据", key = "订单合并")
	public R orderMergeCheck(@ApiParam(name = "xhfNsrsbh", value = "用户接口访问凭证", required = true) @RequestParam("xhfNsrsbh") String xhfNsrsbh,
	                         @ApiParam(name = "idList", value = "合并数据ID列表", required = true) @RequestParam("idList") String idList) {
		log.info("{} 订单合并数据校验:nsrsbh:{},idList:{}", LOGGER_MSG, xhfNsrsbh, idList);
		R vo;
		
		List<String> shList = JsonUtils.getInstance().fromJson(xhfNsrsbh, List.class);
		String[] ids = JsonUtils.getInstance().fromJson(idList, String[].class);
		vo = orderMergeService.orderMergeCheck(ids, shList);
		return vo;
	}
	
	/**
	 * 订单合并
	 *
	 * @param request
	 * @return
	 * @author: 陈玉航
	 * @date: Created on 2018年7月31日 上午10:51:48
	 */
	@ApiOperation(value = "订单合并", notes = "订单合并-原始订单合并")
	@PostMapping("/merge")
	@SysLog(operation = "订单合并rest接口", operationDesc = "多个订单合并一个订单", key = "订单合并")
	public R orderMerge(HttpServletRequest request,
	                    @ApiParam(name = "idList", value = "合并数据ID列表,销方税号,合并标识", required = true) @RequestBody String idList) {
		try {
			if (StringUtils.isBlank(idList)) {
				log.error("{},请求税号为空!", LOGGER_MSG);
				return R.error(OrderInfoContentEnum.TAXCODE_ISNULL);
			}
			
			Map map = JsonUtils.getInstance().parseObject(idList, Map.class);
			String isMergeSameOrderItem = map.get("isMergeSameItem").toString();
			List<Map> idList1 = JSON.parseArray(map.get("idList").toString(), Map.class);
			
			// 调用业务层处理数据
			return orderMergeService.orderMerge(idList1, isMergeSameOrderItem);
		} catch (Exception e) {
			log.error("{} 订单合并接口处理异常:{}", LOGGER_MSG, e);
			return InterfaceResponseUtils.buildErrorInfo(OrderInfoContentEnum.RECEIVE_FAILD);
		}
		
	}
	
}
