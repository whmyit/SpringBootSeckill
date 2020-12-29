package com.dxhy.order.consumer.modules.invoice.controller;

import com.dxhy.order.api.ApiTaxEquipmentService;
import com.dxhy.order.api.ValidateOrderInfo;
import com.dxhy.order.constant.OrderInfoContentEnum;
import com.dxhy.order.constant.OrderInfoEnum;
import com.dxhy.order.constant.OrderManagementConstant;
import com.dxhy.order.consumer.annotation.SysLog;
import com.dxhy.order.consumer.model.page.PageSld;
import com.dxhy.order.consumer.modules.invoice.service.InvoiceService;
import com.dxhy.order.consumer.modules.order.service.MakeOutAnInvoiceService;
import com.dxhy.order.consumer.openapi.service.ICommonInterfaceService;
import com.dxhy.order.model.CommonOrderInfo;
import com.dxhy.order.model.OrderInvoiceInfoRequest;
import com.dxhy.order.model.R;
import com.dxhy.order.model.a9.kp.CommonInvoiceStatus;
import com.dxhy.order.utils.JsonUtils;
import com.dxhy.order.utils.NsrsbhUtils;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.dubbo.config.annotation.Reference;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author ：杨士勇
 * @ClassName ：InvoiceController
 * @Description ：待开订单开具发票
 * @date 2018-08-10
 */
@Api(value = "订单开票", tags = {"发票模块"})
@RestController
@RequestMapping("/invoice")
@Slf4j
public class InvoiceController {
	
	private static final String LOGGER_MSG = "(待开单据开具发票接口)";
	
	@Reference
	private ValidateOrderInfo validateOrderInfo;
	
	@Resource
	private MakeOutAnInvoiceService makeOutAnInvoiceService;
	
	@Resource
	private InvoiceService invoiceService;
	
	
	@Resource
	private ICommonInterfaceService iCommonInterfaceService;
	
	
	@Reference
	private ApiTaxEquipmentService apiTaxEquipmentService;
	
	
	/**
	 * activex 专用
	 * 开票完成之后修改订单状态
	 */
	@ApiOperation(value = "发票开具完成更新发票信息-ActiveX", notes = "订单开票-发票开具完成更新发票信息")
	@PostMapping("/updateInvoice")
	@SysLog(operation = "发票开具完成更新发票信息", operationDesc = "发票开具完成更新发票信息", key = "开票")
	public R invoiceSave(@RequestBody OrderInvoiceInfoRequest invoiceInfo, @ApiParam(name = "xhfNsrsbh", value = "销货方纳税人识别号", required = true) @RequestParam(value = "xhfNsrsbh", required = true) String xhfNsrsbh) {
		log.info("{},active开票完成之后保存发票信息,参数：{}", LOGGER_MSG, invoiceInfo);
		if (StringUtils.isBlank(xhfNsrsbh)) {
			log.error("{},请求税号为空!", LOGGER_MSG);
			return R.error(OrderInfoContentEnum.TAXCODE_ISNULL);
		}
		
		List<String> shList = NsrsbhUtils.transShListByXhfNsrsbh(xhfNsrsbh);
		R r = makeOutAnInvoiceService.updateOrderInvoiceInfo(invoiceInfo, shList);
		return r;
	}
    
    
    /**
     * 待开单据开票接口
     *
     * @param paperOrderIdArray
     * @param specialOrderIdArray
     * @param eleOrderIdArray
     * @param isAll
     * @param paperReceivePoint
     * @param specialReceivePoint
     * @param paperReceiveMc
     * @param specialReceiveMc-*
     * @return
     */
    @ApiOperation(value = "订单开票", notes = "订单开票-订单开具发票的接口")
    @PostMapping("/batchInvoice")
    @SysLog(operation = "开票", operationDesc = "订单开具发票接口", key = "开票服务")
    public R acceptByEnterprise(
		    @ApiParam(name = "paperOrderIdArray", value = "纸票订单号数组", required = false) @RequestParam(value = "paperOrderIdArray", required = false) String paperOrderIdArray,
		    @ApiParam(name = "specialOrderIdArray", value = "专票订单号数组", required = false) @RequestParam(value = "specialOrderIdArray", required = false) String specialOrderIdArray,
		    @ApiParam(name = "eleOrderIdArray", value = "发票订单号数组", required = false) @RequestParam(value = "eleOrderIdArray", required = false) String eleOrderIdArray,
		    @ApiParam(name = "isAll", value = "0 全部开票 1 勾选开票", required = true) @RequestParam(value = "isAll", required = true) String isAll,
		    @ApiParam(name = "paperReceivePoint", value = "纸票开票点", required = false) @RequestParam(value = "paperReceivePoint", required = false) String paperReceivePoint,
		    @ApiParam(name = "specialReceivePoint", value = "专票开票点", required = false) @RequestParam(value = "specialReceivePoint", required = false) String specialReceivePoint,
		    @ApiParam(name = "paperReceiveMc", value = "纸票开票点", required = false) @RequestParam(value = "paperReceiveMc", required = false) String paperReceiveMc,
		    @ApiParam(name = "specialReceiveMc", value = "专票开票点", required = false) @RequestParam(value = "specialReceiveMc", required = false) String specialReceiveMc,
		    @ApiParam(name = "xhfNsrsbh", value = "销方税号", required = false) @RequestParam(value = "xhfNsrsbh", required = false) String xhfNsrsbh,
		    @ApiParam(name = "userId", value = "用户id", required = false) @RequestParam(value = "userId", required = false) String userId) {
	    R r;
	    try {
		
		    if (StringUtils.isBlank(xhfNsrsbh)) {
			    log.error("{},请求税号为空!", LOGGER_MSG);
			    return R.error(OrderInfoContentEnum.TAXCODE_ISNULL);
		    }
		
		    List<String> shList = NsrsbhUtils.transShListByXhfNsrsbh(xhfNsrsbh);
		
		    String[] paperArray = StringUtils.isBlank(paperOrderIdArray) ? null
				    : JsonUtils.getInstance().parseObject(paperOrderIdArray, String[].class);
		    String[] specialArray = StringUtils.isBlank(specialOrderIdArray) ? null
				    : JsonUtils.getInstance().parseObject(specialOrderIdArray, String[].class);
		    String[] eleArray = StringUtils.isBlank(eleOrderIdArray) ? null
				    : JsonUtils.getInstance().parseObject(eleOrderIdArray, String[].class);
		
		    // 受理点按照税号 发票种类代码传值
		    Map<String, PageSld> paramMap = new HashMap<>(5);
		    PageSld sld = new PageSld();
		    sld.setSldid(paperReceivePoint);
		    sld.setSldmc(paperReceiveMc);
		    sld.setFjh("");
		    paramMap.put("_" + OrderInfoEnum.ORDER_INVOICE_TYPE_2.getKey(), sld);
		    sld = new PageSld();
		    sld.setSldid(specialReceivePoint);
		    sld.setSldmc(specialReceiveMc);
		    paramMap.put("_" + OrderInfoEnum.ORDER_INVOICE_TYPE_0.getKey(), sld);
		    r = invoiceService.batchInvoiceById(paperArray, specialArray, eleArray, paramMap, userId, shList);
		
		    return r;
	    } catch (Exception e) {
		    log.error("{}待开订单开具接口，参数转换异常e:{}", LOGGER_MSG, e);
		    return R.error().put(OrderManagementConstant.MESSAGE, "未知异常，请联系管理员！");
	    }
    }

	/**
	 * 订单编辑后开票
	 * 优税小助手调用
	 *
	 * @return
	 */
	@ApiOperation(value = "订单编辑后开票", notes = "订单开票-订单编辑后开票")
	@PostMapping("/updateOrderAndInvoice")
	@SysLog(operation = "编辑订单rest接口", operationDesc = "订单编辑", key = "订单编辑")
	public R updateOrderAndInvoice(
			@ApiParam(name = "orderInfo", value = "订单信息", required = true) @RequestBody CommonOrderInfo commonOrder) {
		// 数据非空校验校验
		if (commonOrder == null || commonOrder.getOrderInfo() == null
				|| CollectionUtils.isEmpty(commonOrder.getOrderItemInfo())) {
			return R.error().put(OrderManagementConstant.CODE, OrderInfoContentEnum.PARAM_NULL.getKey())
					.put(OrderManagementConstant.MESSAGE, OrderInfoContentEnum.PARAM_NULL.getMessage());
		}

		//添加订单编辑后开票的来源
		commonOrder.setSjywly(OrderInfoEnum.READY_ORDER_SJLY_4.getKey());
		String terminalCode = apiTaxEquipmentService.getTerminalCode(commonOrder.getOrderInfo().getXhfNsrsbh());
		
		Map<String, PageSld> paramMap = new HashMap<>(5);
		if (!OrderInfoEnum.ORDER_INVOICE_TYPE_51.getKey().equals(commonOrder.getOrderInfo().getFpzlDm())) {
			if (StringUtils.isBlank(commonOrder.getSld())) {

				
				R result = iCommonInterfaceService.dealWithSldStartV3("", commonOrder.getOrderInfo().getFpzlDm(), commonOrder.getOrderInfo().getXhfNsrsbh(), commonOrder.getOrderInfo().getQdBz(), terminalCode);
				log.debug("受理点查询成功!");
				if (!OrderInfoContentEnum.SUCCESS.getKey().equals(String.valueOf(result.get(OrderManagementConstant.CODE)))) {
					return R.error().put(OrderManagementConstant.MESSAGE, "无可用受理点");
				} else {
					PageSld sld = new PageSld();
					sld.setSldid(String.valueOf(result.get("sldid")));
					sld.setSldmc(String.valueOf(result.get("sldmc")));
					paramMap.put("_" + commonOrder.getOrderInfo().getFpzlDm(), sld);
				}
			} else {
				PageSld sld = new PageSld();
				sld.setSldid(commonOrder.getSld());
				sld.setSldmc(commonOrder.getSldmc());
				paramMap.put("_" + commonOrder.getOrderInfo().getFpzlDm(), sld);

			}

		} else {
			if (OrderInfoEnum.TAX_EQUIPMENT_FGBW.getKey().equals(terminalCode) || OrderInfoEnum.TAX_EQUIPMENT_FGHX.getKey().equals(terminalCode) || OrderInfoEnum.TAX_EQUIPMENT_FGUKEY.getKey().equals(terminalCode)) {
				R result = iCommonInterfaceService.dealWithSldStartV3("", commonOrder.getOrderInfo().getFpzlDm(), commonOrder.getOrderInfo().getXhfNsrsbh(), commonOrder.getOrderInfo().getQdBz(), terminalCode);
				log.debug("受理点查询成功!");
				if (!OrderInfoContentEnum.SUCCESS.getKey().equals(String.valueOf(result.get(OrderManagementConstant.CODE)))) {
					return R.error().put(OrderManagementConstant.MESSAGE, "无可用受理点");
				} else {
					PageSld sld = new PageSld();
					sld.setSldid(String.valueOf(result.get("sldid")));
					sld.setSldmc(String.valueOf(result.get("sldmc")));
					paramMap.put("_" + commonOrder.getOrderInfo().getFpzlDm(), sld);
				}
			}
		}

		//组装受理点参数
		List<CommonOrderInfo> paramList = new ArrayList<>();
		paramList.add(commonOrder);
		List<String> shList = new ArrayList<>();
		shList.add(commonOrder.getOrderInfo().getXhfNsrsbh());
		R r = invoiceService.updateAndInvoice(paramList, paramMap, commonOrder.getUserId(), shList);
		return r;
	}

	/**
	 * 订单编辑后开票
	 *
	 * @return
	 */
	@ApiOperation(value = "查询同步底层异常订单是否已开具", notes = "订单开票-查询底层开票结果")
	@PostMapping("/queryInvoiceFinalResult")
	@SysLog(operation = "查询同步底层异常订单是否已开具", operationDesc = "订单开票-查询底层开票结果", key = "查询底层开票结果")
	public R qureyInvoiceFinalResult(@ApiParam(value =  "发票请求流水号",name = "发票请求流水号") @RequestParam(value = "fpqqlsh") String fpqqlsh,
									 @ApiParam(value =  "销货方纳税人识别号",name = "销货方纳税人识别号") @RequestParam(value = "xhfNsrsbh") String xhfNsrsbh) {
		
		// 数据非空校验校验
		try {
			if (StringUtils.isBlank(fpqqlsh) || StringUtils.isBlank(xhfNsrsbh)) {
                return R.error().put(OrderManagementConstant.CODE, OrderInfoContentEnum.PARAM_NULL.getKey())
                        .put(OrderManagementConstant.MESSAGE, OrderInfoContentEnum.PARAM_NULL.getMessage());
            }

			CommonInvoiceStatus commonInvoiceStatus = invoiceService.queryInvoiceStatus(fpqqlsh, xhfNsrsbh);

			//返回结果转换
			if (!OrderInfoContentEnum.SUCCESS.getKey().equals(commonInvoiceStatus.getStatusCode())) {
                return R.error().put(OrderManagementConstant.MESSAGE, "查询发票状态失败!");
            } else {
                // 2101换流水号,其他都不换,2001时提示开票失败,不允许编辑,不换流水号直接重试.
                if (OrderInfoEnum.INVOICE_QUERY_STATUS_2101.getKey().equals(commonInvoiceStatus.getFpzt())) {
                    return R.ok().put(OrderManagementConstant.MESSAGE, "赋码失败，发票未开具!");

                } else {
                    return R.error().put(OrderManagementConstant.MESSAGE, "");
                }
            }
		} catch (Exception e) {
			log.error("查询订单开具状态的接口异常,异常信息:{}",e);
			return R.error();
		}
	}
}

