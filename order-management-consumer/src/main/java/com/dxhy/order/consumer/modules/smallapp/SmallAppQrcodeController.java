package com.dxhy.order.consumer.modules.smallapp;

import com.dxhy.order.constant.OrderInfoContentEnum;
import com.dxhy.order.constant.OrderInfoEnum;
import com.dxhy.order.constant.OrderManagementConstant;
import com.dxhy.order.consumer.annotation.SysLog;
import com.dxhy.order.consumer.model.OderDetailInfo;
import com.dxhy.order.consumer.model.page.*;
import com.dxhy.order.consumer.modules.invoice.controller.InvoiceController;
import com.dxhy.order.consumer.modules.invoice.controller.InvoiceDetailsController;
import com.dxhy.order.consumer.modules.invoice.controller.OrderInvoiceController;
import com.dxhy.order.consumer.modules.manager.controller.CommodityController;
import com.dxhy.order.consumer.modules.order.controller.OrderInfoController;
import com.dxhy.order.consumer.modules.order.controller.QrcodeController;
import com.dxhy.order.consumer.modules.scaninvoice.model.PageQrcodeOrderInfo;
import com.dxhy.order.consumer.modules.scaninvoice.model.PageQrcodeOrderItemInfo;
import com.dxhy.order.model.*;
import com.dxhy.order.utils.JsonUtils;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.util.*;

/**
 * @author ：杨士勇
 * @ClassName ：QrcodeController
 * @Description ：所有小程序调用销项目接口由此controller转发
 * @date ：2018年7月20日 下午2:31:24
 */
@Api(value = "小程序-二维码管理", tags = {"小程序-二维码管理模块"})
@RestController
@RequestMapping("/smallApp")
@Slf4j
public class SmallAppQrcodeController {
    
    private static final String LOGGER_MSG = "(小程序-二维码管理)";
	
	private static final DecimalFormat DECIMAL_FORMAT = new DecimalFormat("#0.00");

    @Resource
	QrcodeController qrcodeController;

    @Resource
    OrderInvoiceController orderInvoiceController;

    @Resource
    OrderInfoController orderInfoController;

    @Resource
    InvoiceController invoiceController;

    @Resource
    CommodityController commodityController;

    @Resource
    InvoiceDetailsController invoiceDetailsController;

    /**
     * 二维码列表接口
	 * 优税程序小助手
     *
     * @return
     */
    @PostMapping("/qrCode/qrCodeList")
    @ApiOperation(value = "静态二维码列表", notes = "二位码管理-二维码列表")
    @SysLog(operation = "静态二维码列表接口", operationDesc = "查询符合条件的静态二维码信息", key = "静态码管理")
    public R qrCodeList(@ApiParam(name = "ywlxId", value = "业务类型id", required = false) @RequestParam(value = "ywlxId", required = false) String ywlxId,
                        @ApiParam(name = "xmmc", value = "项目名称", required = false) @RequestParam(value = "xmmc", required = false) String xmmc,
                        @ApiParam(name = "xhfNsrsbh", value = "销方纳税人识别号", required = true) @RequestParam(value = "xhfNsrsbh", required = true) String xhfNsrsbh,
						@ApiParam(name = "sjly", value = "来源", required = false) @RequestParam(value = "sjly", required = false) String sjly,
                        @ApiParam(name = "pageSize", value = "每页条数", required = false) @RequestParam(value = "pageSize", required = false) String pageSize,
                        @ApiParam(name = "currentPage", value = "当前页", required = false) @RequestParam(value = "currentPage", required = false) String currentPage) {

        log.debug("{}静态二维码列表",LOGGER_MSG);
        R r = qrcodeController.qrCodeList(ywlxId, xmmc, xhfNsrsbh, sjly, pageSize, currentPage);
        return r;
    }


	/**
	 * 订单编辑
	 * 优税小助手调用
	 *
	 * @param data
	 * @return
	 */
	@ApiOperation(value = "编辑订单", notes = "订单信息管理-编辑订单")
	@PostMapping("/qrCode/updateOrderInfo")
	@SysLog(operation = "编辑订单rest接口", operationDesc = "订单编辑", key = "订单编辑")
	public R updateOrderInfo(@ApiParam(name = "orderInfo", value = "订单信息", required = true) @RequestBody String data) {
		log.info("{}订单详情编辑,订单信息为:{}", LOGGER_MSG, data);
		return orderInfoController.updateOrderInfo(data);
	}



	/**
     * 动态码列表接口  优税发票小助手调用
     */
    @PostMapping("/qrCode/dynamicQrCodeList")
    @ApiOperation(value = "动态码列表", notes = "动态码管理-动态码列表")
    @SysLog(operation = "动态码列表接口", operationDesc = "查询符合条件动态码码信息", key = "动态码管理")
    public R dynamicQrCodeList(@ApiParam(name = "startTime", value = "订单开始时间", required = true) @RequestParam(value = "startTime", required = true) String startTime,
                               @ApiParam(name = "endTime", value = "订单结束时间", required = true) @RequestParam(value = "endTime", required = true) String endTime,
                               @ApiParam(name = "minJe", value = "最小金额", required = true) @RequestParam(value = "minJe", required = false) String minJe,
                               @ApiParam(name = "maxJe", value = "最大金额", required = true) @RequestParam(value = "maxJe", required = false) String maxJe,
                               @ApiParam(name = "ddh", value = "订单号", required = true) @RequestParam(value = "ddh", required = false) String ddh,
                               @ApiParam(name = "currentPage", value = "当前页", required = false) @RequestParam(value = "currentPage", required = false) String currentPage,
                               @ApiParam(name = "pageSize", value = "页面条数", required = false) @RequestParam(value = "pageSize", required = false) String pageSize,
                               @ApiParam(name = "ghfmc", value = "购货方名称", required = true) @RequestParam(value = "ghfmc", required = false) String ghfmc,
                               @ApiParam(name = "fpzldm", value = "发票种类代码", required = true) @RequestParam(value = "fpzldm", required = false) String fpzldm,
                               @ApiParam(name = "kpzt", value = "开票状态", required = true) @RequestParam(value = "kpzt", required = false) String kpzt,
                               @ApiParam(name = "ewmzt", value = "二维码状态 0 未使用 1 已使用 2 已失效 3 已作废", required = true) @RequestParam(value = "ewmzt", required = true) String ewmzt,
                               @ApiParam(name = "startValidTime", value = "二维码有效截至日期起", required = false) @RequestParam(value = "startValidTime", required = false) String startValidTime,
                               @ApiParam(name = "endValidTime", value = "二维码有效截至日期止", required = false) @RequestParam(value = "endValidTime", required = false) String endValidTime,
							   @ApiParam(name = "sjly", value = "来源", required = false) @RequestParam(value = "sjly", required = false) String sjly,
                               @ApiParam(name = "xhfNsrsbh", value = "销货方纳税人识别号", required = true) @RequestParam(value = "xhfNsrsbh", required = false) String xhfNsrsbh) {
	    log.info("{}二维码列表接口", LOGGER_MSG);
        R r = qrcodeController.dynamicQrCodeList(startTime, endTime, minJe, maxJe, ddh, currentPage,pageSize,ghfmc,fpzldm,kpzt,ewmzt,startValidTime,endValidTime,sjly,xhfNsrsbh);
        return r;
	}


	/**
	 * 二维码信息展示 优税发票小助手调用
	 */
	@PostMapping("/qrCode/queryEwmDetailInfo")
	@ApiOperation(value = "动态二维码详情信息", notes = "动态码管理-动态二维码详情信息")
	@SysLog(operation = "动态二维码详情信息", operationDesc = "动态二维码详情信息", key = "二维码管理")
	public R queryEwmDetailInfo(
			@ApiParam(name = "fpqqlsh", value = "发票请求流水号", required = true) @RequestParam(value = "fpqqlsh", required = true) String fpqqlsh,
			@ApiParam(name = "xhfNsrsbh", value = "销货方纳税人识别号", required = true) @RequestParam(value = "xhfNsrsbh", required = true) String xhfNsrsbh) {

	    //小程序数据全部转换为含税展示
        R r = qrcodeController.queryEwmDetailInfo(fpqqlsh,xhfNsrsbh);
        if(OrderInfoContentEnum.SUCCESS.getKey().equals(r.get(OrderManagementConstant.CODE))){
            //把明细数据转换为含税
            if(r.get(OrderManagementConstant.DATA) != null){
                Map<String, Object> stringObjectMap = (Map<String, Object>) r.get(OrderManagementConstant.DATA);
                if(stringObjectMap.get("qrCodeItemList") != null){
                    List<OrderItemInfo> itemList = (List<OrderItemInfo>)stringObjectMap.get("qrCodeItemList");
                    if(CollectionUtils.isNotEmpty(itemList)){
                        for(OrderItemInfo orderItem : itemList){
                            if(OrderInfoEnum.HSBZ_0.getKey().equals(orderItem.getHsbz())){
                                if(StringUtils.isNotBlank(orderItem.getSe())){
                                    BigDecimal xmje = new BigDecimal(orderItem.getXmje()).add(new BigDecimal(orderItem.getSe()));
                                    if(StringUtils.isNotBlank(orderItem.getXmsl())){
                                        BigDecimal xmdj = new BigDecimal(orderItem.getXmje()).divide(new BigDecimal(orderItem.getXmsl()),8, RoundingMode.HALF_UP);
                                        orderItem.setXmdj(xmdj.toPlainString());
                                    }
                                    orderItem.setXmje(xmje.toPlainString());
                                    orderItem.setHsbz(OrderInfoEnum.HSBZ_1.getKey());
                                }

                            }
                        }
                    }
                    stringObjectMap.put("qrCodeItemList",itemList);
                }
                r.put(OrderManagementConstant.DATA,stringObjectMap);
            }

        }
        return r;
	}




	/**
	 * 优税发票小助手调用 产品无调用
	 * @param pageQrcodeOrderInfo
	 * @return
	 */
	@ApiOperation(value = "生成动态码", notes = "扫码开票-生成动态码")
	@SysLog(operation = "生成动态码", operationDesc = "生成动态码", key = "生成动态码")
	@PostMapping("/qrCode/generateDynamicQrCode")
	public R generateDynamicQrCode(@RequestBody PageQrcodeOrderInfo pageQrcodeOrderInfo) {

		log.info("{}，小程序生成动态二维码的接口,入参:{}",LOGGER_MSG,JsonUtils.getInstance().toJsonString(pageQrcodeOrderInfo));
		//对请求参数处理 处理含有优惠政策的数据
		PageQrcodeOrderItemInfo[] pageOrderItemInfo = pageQrcodeOrderInfo.getPageOrderItemInfo();
		for(PageQrcodeOrderItemInfo orderItem : pageOrderItemInfo){
			if(StringUtils.isNotBlank(orderItem.getLslbs())){
				if(OrderInfoEnum.LSLBS_0.getKey().equals(orderItem.getLslbs())){
					orderItem.setYhzcbs(OrderInfoEnum.YHZCBS_1.getKey());
					orderItem.setZzstsgl(OrderInfoEnum.LSLBS_0.getValue());

				}else if(OrderInfoEnum.LSLBS_1.getKey().equals(orderItem.getLslbs())){
					orderItem.setYhzcbs(OrderInfoEnum.YHZCBS_1.getKey());
					orderItem.setZzstsgl(OrderInfoEnum.LSLBS_1.getValue());

				}else if(OrderInfoEnum.LSLBS_2.getKey().equals(orderItem.getLslbs())){
					orderItem.setYhzcbs(OrderInfoEnum.YHZCBS_1.getKey());
					orderItem.setZzstsgl(OrderInfoEnum.LSLBS_2.getValue());

				}else if(OrderInfoEnum.LSLBS_3.getKey().equals(orderItem.getLslbs())){
					orderItem.setYhzcbs(OrderInfoEnum.YHZCBS_0.getKey());
					orderItem.setZzstsgl("");
				}

			}
		}
		R r = qrcodeController.generateDynamicQrCode(pageQrcodeOrderInfo);
		return r;

	}




	/**
	 * 优税发票小助手调用 产品无调用
	 * 查询二维码和发票信息
	 */
	@PostMapping("/qrCode/queryQrcodeAndInvoiceDateil")
	@ApiOperation(value = "二维码发票信息查询", notes = "二维码发票信息查询")
	@SysLog(operation = "二维码发票信息查询", operationDesc = "二维码发票信息查询", key = "动态码管理")
	public R queryQrcodeAndInvoiceDateil(
			@ApiParam(name = "qrcodeId", value = "二维码id", required = true) @RequestParam(value = "qrcodeId", required = true) String qrcodeId,
			@ApiParam(name = "type", value = "二维码类型", required = true) @RequestParam(value = "type", required = true) String type,
			@ApiParam(name = "xhfNsrsbh", value = "销货方纳税人识别号", required = true) @RequestParam(value = "xhfNsrsbh", required = true) String xhfNsrsbh,
			@ApiParam(name = "backGround", value = "二维码背景色", required = false) @RequestParam(value = "backGround", required = false) String backGround) {

        log.debug("查询二维码和发票信息的接口，入参,qrcodeId:{},type:{},xhfNsrsbh:{}",qrcodeId,type,xhfNsrsbh);

        R r = qrcodeController.queryQrcodeAndInvoiceDateil(qrcodeId,type,xhfNsrsbh,backGround);

        return r;
	}



	/**
	 * 更新二维码信息 二维码失效或激活  优税发票小助手调用
	 */
	@PostMapping("/qrCode/updateEwmDetailInfo")
	@ApiOperation(value = "更新二维码详情信息", notes = "动态码管理-更新二维码详情信息")
	@SysLog(operation = "更新二维码信息", operationDesc = "更新二维码详情信息", key = "动态码管理")
	public R updateEwmDetailInfo(
			@ApiParam(name = "param", value = "动态码信息表主键id和销方税号和二维码状态", required = true) @RequestBody() String param) {
		
		return qrcodeController.updateEwmDetailInfo(param);
    }

	/**
	 * 优税发票小助手调用 判断是否存在待审核的订单
	 * @return
	 */

	@PostMapping("/qrCode/isExistNoAuditOrder")
	@ApiOperation(value = "查询是否存在为审核的订单", notes = "查询是否存在为审核的订单")
	@SysLog(operation = "查询是否存在为审核的订单", operationDesc = "查询是否存在为审核的订单", key = "二维码管理")
	public R isExistNoAuditOrder(@RequestBody OrderListQuery orderBatchQuery) {

        return qrcodeController.isExistNoAuditOrder(orderBatchQuery);
    }

	/**
	 * 在使用
	 * 发票明细
	 * 优税 小助手调用
	 * @param
	 * @return
	 */
	@PostMapping("/orderInvoice/queryInvoiceDetails")
	@ApiOperation(value = "查询发票列表", notes = "订单发票管理-查询发票列表说明")
	@SysLog(operation = "发票信息查询", operationDesc = "发票信息查询", key = "订单发票管理")
	public R queryInvoiceDetails(@RequestBody OrderListQuery orderBatchQuery) {
		R r = orderInvoiceController.queryInvoiceDetails(orderBatchQuery);

	    return r;
	}


	/**
	 * 逻辑删除待开订单
	 * 优税小助手调用
	 * @param ids
	 * @return fankunfeng
	 */
	@ApiOperation(value = "逻辑删除订单", notes = "订单发票管理-逻辑删除订单")
	@PostMapping("/orderInvoice/updateOrderStatus")
	@SysLog(operation = "订单删除rest接口", operationDesc = "逻辑删除订单单据", key = "订单删除")
	public R updateOrderStatus(
			@ApiParam(name = "ids", value = "订单标志ID", required = false) @RequestBody String ids) {

		return orderInvoiceController.updateOrderStatus(ids);
	}

	/**
	 * 订单编辑后开票
	 * 优税小助手调用
	 *
	 * @return
	 */
	@ApiOperation(value = "订单编辑后开票", notes = "订单开票-订单编辑后开票")
	@PostMapping("/invoice/updateOrderAndInvoice")
	@SysLog(operation = "编辑订单rest接口", operationDesc = "订单编辑", key = "订单编辑")
	public R updateOrderAndInvoice(
			@ApiParam(name = "orderInfo", value = "订单信息", required = true) @RequestBody CommonOrderInfo commonOrder) {
	    return invoiceController.updateOrderAndInvoice(commonOrder);
	}


	/**
	 * 供优税小助手调用
	 * @Description 商品信息列表查询
	 * @Author xieyuanqiang
	 * @Date 10:13 2018-07-21
	 */
	@PostMapping("/commodity/queryCommodityInfoListByPage")
	@ApiOperation(value = "商品信息列表", notes = "商品信息管理-商品信息列表")
	@SysLog(operation = "商品信息列表查询", operationDesc = "商品信息列表查询", key = "商品信息管理")
	public R queryCommodityInfoListByPage( @ApiParam(name = "merchandiseName", value = "商品名称", required = false)@RequestParam(required = false) String merchandiseName,
										   @ApiParam(name = "encoding", value = "购方自编码", required = false)@RequestParam(required = false)  String encoding,
										   @ApiParam(name = "xhfNsrsbh", value = "销货方纳税人识别号", required = true)@RequestParam(required = true)  String xhfNsrsbh,
										   @ApiParam(name = "currPage", value = "当前页面", required = false)@RequestParam(required = false) String currentPage,
										   @ApiParam(name = "pageSize", value = "页面条数", required = false)@RequestParam(required = false) String pageSize,
										   @ApiParam(name = "orderBy", value = "排序方式 0 : 创建时间正序排，1 : 创建时间倒叙排", required = false)@RequestParam(required = false) String orderBy) {

        return commodityController.queryCommodityInfoListByPage(merchandiseName,encoding,xhfNsrsbh,currentPage,pageSize,orderBy);

	}

    @ApiOperation(value = "查询订单列表", notes = "订单信息管理-查询订单列表")
    @PostMapping("/order/queryOrderList")
    @SysLog(operation = "查询订单列表rest接口", operationDesc = "查询订单列表", key = "订单列表")
    public R queryOrderList(@RequestBody OrderListQuery orderBatchQuery) {

        log.debug("{}发票明细列表查询参数:{}", LOGGER_MSG, JsonUtils.getInstance().toJsonString(orderBatchQuery));

        return orderInfoController.queryOrderList(orderBatchQuery);
    }

    /**
     * 获取订单详情
     * 优税小程序调用
     * @param orderId
     * @param fpqqlsh
     * @return
     */
	@ApiOperation(value = "查询订单详情", notes = "订单信息管理-查询订单详情")
	@PostMapping("/order/queryOrderDetail")
	@SysLog(operation = "查询订单详情rest接口", operationDesc = "订单详情数据查询", key = "订单查询")
	public R queryOrderDetail(@ApiParam(name = "orderId", value = "处理表订单ID", required = true) @RequestParam("orderId") String orderId,
							  @ApiParam(name = "fpqqlsh", value = "发票请求流水号", required = true) @RequestParam("fpqqlsh") String fpqqlsh,
							  @ApiParam(name = "xhfNsrsbh", value = "销货方纳税人识别号", required = true) @RequestParam(value = "xhfNsrsbh", required = true) String xhfNsrsbh) {
		log.info("{}订单详情查询,订单处理表id{}", LOGGER_MSG, orderId);

		R r = orderInfoController.queryOrderDetail(orderId,fpqqlsh,xhfNsrsbh);
		if(OrderInfoContentEnum.SUCCESS.getKey().equals(r.get(OrderManagementConstant.CODE))){
			//把明细数据转换为含税
			if(r.get(OrderManagementConstant.DATA) != null){
				OderDetailInfo stringObjectMap = (OderDetailInfo) r.get(OrderManagementConstant.DATA);
				if(CollectionUtils.isNotEmpty(stringObjectMap.getOrderItemInfo())){
					List<OrderItemInfo> orderItemInfo = stringObjectMap.getOrderItemInfo();
					for(OrderItemInfo orderItem : orderItemInfo){
						if(OrderInfoEnum.HSBZ_0.getKey().equals(orderItem.getHsbz())){
							if(StringUtils.isNotBlank(orderItem.getSe())){
								BigDecimal xmje = new BigDecimal(orderItem.getXmje()).add(new BigDecimal(orderItem.getSe()));
								if(StringUtils.isNotBlank(orderItem.getXmsl())){
									BigDecimal xmdj = new BigDecimal(orderItem.getXmje()).divide(new BigDecimal(orderItem.getXmsl()),8, RoundingMode.HALF_UP);
									orderItem.setXmdj(xmdj.toPlainString());
								}
								orderItem.setXmje(xmje.toPlainString());
								orderItem.setHsbz(OrderInfoEnum.HSBZ_1.getKey());
							}

						}
					}
				}
				r.put(OrderManagementConstant.DATA,stringObjectMap);
			}

		}
		return r;
	}



    @PostMapping("/qrCode/qrCodeImg")
    @ApiOperation(value = "二维码图片接口", notes = "动态码管理-二维码图片接口")
    @SysLog(operation = "二维码图片接口", operationDesc = "二维码图片接口", key = "动态码管理")
    public R qrCodeImg(@ApiParam(name = "qrcodeId", value = "二维码id", required = true) @RequestParam(value = "qrcodeId", required = true) String qrcodeId,
                       @ApiParam(name = "type", value = "二维码类型", required = true) @RequestParam(value = "type", required = true) String type,
                       @ApiParam(name = "xhfNsrsbh", value = "销货方纳税人识别号", required = true) @RequestParam(value = "xhfNsrsbh", required = true) String xhfNsrsbh,
                       @ApiParam(name = "backGround", value = "二维码背景色", required = false) @RequestParam(value = "backGround", required = false) String backGround) {
        log.info("{}二维码图片接口", LOGGER_MSG);
        return qrcodeController.qrCodeImg(qrcodeId,type,xhfNsrsbh,backGround);
    }


    @PostMapping(value = "/invoiceDetails/previewInvoicePng")
    @ApiOperation(value = "预览发票图片", notes = "发票详情-预览发票图片")
    @SysLog(operation = "预览发票图片", operationDesc = "预览发票图片", key = "发票详情")
    public R previewInvoicePng(@ApiParam(name = "invoiceDate", value = "发票数据", required = true) @RequestBody String invoiceDate) {

        return invoiceDetailsController.previewInvoicePng(invoiceDate);
    }




}
