package com.dxhy.order.consumer.modules.order.controller;

import cn.hutool.core.date.DateUtil;
import com.alibaba.fastjson.JSON;
import com.dxhy.order.api.ApiInvoiceCommonService;
import com.dxhy.order.constant.ConfigureConstant;
import com.dxhy.order.constant.OrderInfoContentEnum;
import com.dxhy.order.constant.OrderManagementConstant;
import com.dxhy.order.consumer.annotation.SysLog;
import com.dxhy.order.consumer.config.OpenApiConfig;
import com.dxhy.order.consumer.model.page.OrderListQuery;
import com.dxhy.order.consumer.model.page.PageEwmConfigInfo;
import com.dxhy.order.consumer.model.page.PageOrderItemInfo;
import com.dxhy.order.consumer.model.page.QrcodeOrderInfo;
import com.dxhy.order.consumer.modules.order.service.MyinvoiceRequestService;
import com.dxhy.order.consumer.modules.order.service.QrcodeService;
import com.dxhy.order.consumer.modules.scaninvoice.model.PageQrcodeOrderInfo;
import com.dxhy.order.consumer.utils.PageBeanConvertUtil;
import com.dxhy.order.consumer.utils.QrCodeUtil;
import com.dxhy.order.model.PageUtils;
import com.dxhy.order.model.R;
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
import java.text.DecimalFormat;
import java.util.*;

/**
 * @author ：杨士勇
 * @ClassName ：QrcodeController
 * @Description ：订单接收controller
 * @date ：2018年7月20日 下午2:31:24
 */
@Api(value = "动态码管理", tags = {"订单模块-二维码管理模块"})
@RestController
@RequestMapping("/qrCode")
@Slf4j
public class QrcodeController {

    private static final String LOGGER_MSG = "(二维码扫开)";

	private static final DecimalFormat DECIMAL_FORMAT = new DecimalFormat("#0.00");

	@Resource
	QrcodeService qrcodeService;

	@Resource
    MyinvoiceRequestService myinvoiceRequestService;

    @Reference
    ApiInvoiceCommonService apiInvoiceCommonService;

    /**
	 *
     * 生成二维码的接口
     *
     * @param qrcodeOrderInfo
     * @return
     */
    @PostMapping("/generateQrCode")
    @ApiOperation(value = "生成静态二维码", notes = "动态码管理-生成静态二维码")
    @SysLog(operation = "生成静态二维码rest接口", operationDesc = "根据前端传入的数据生成静态二维码", key = "二位码管理")
    public R generateQrcode(
            @ApiParam(name = "orderList", value = "orderList", required = true) @RequestBody QrcodeOrderInfo qrcodeOrderInfo) {

        log.info("{}生成二维码的接口:{}", LOGGER_MSG, JsonUtils.getInstance().toJsonString(qrcodeOrderInfo));

        //校验项目名称是否重复
        boolean isFlag = checkOrderItemName(qrcodeOrderInfo);
        if (!isFlag) {
            return R.error().put(OrderManagementConstant.CODE, ConfigureConstant.STRING_9999).put(OrderManagementConstant.MESSAGE, "项目名称重复!");
        }

	    qrcodeOrderInfo.setTqm(apiInvoiceCommonService.getGenerateShotKey());
        String url = String.format(OpenApiConfig.qrCodeScanUrl, qrcodeOrderInfo.getTqm(), qrcodeOrderInfo.getXhfNsrsbh(), qrcodeOrderInfo.getQrCodeType());
        qrcodeOrderInfo.setQrCodeUrl(url);

        boolean saveQrcodeInfo = qrcodeService.saveQrcodeInfo(qrcodeOrderInfo);
        if (!saveQrcodeInfo) {
            return R.error().put(OrderManagementConstant.CODE, ConfigureConstant.STRING_9999).put(OrderManagementConstant.MESSAGE, "数据库保存失败!");
        }

        String imgUrl = String.format(OpenApiConfig.qrCodeShortUrl, qrcodeOrderInfo.getTqm());
        //生成qrcode的base64流
        String qrCodeString = QrCodeUtil.drawLogoQrCode(null, imgUrl, "", qrcodeOrderInfo.getBackGround());

        Map<String, Object> returnMap = new HashMap<>(5);
        returnMap.put("ewm", qrCodeString);
        returnMap.put("qrcodeUrl", imgUrl);

        return R.ok().put(OrderManagementConstant.CODE, OrderInfoContentEnum.SUCCESS.getKey()).put(OrderManagementConstant.MESSAGE, OrderInfoContentEnum.SUCCESS.getMessage())
                .put(OrderManagementConstant.DATA, returnMap);

    }

    /**
     * 二维码列表接口
	 * 优税程序小助手
     *
     * @return
     */
    @PostMapping("/qrCodeList")
    @ApiOperation(value = "静态二维码列表", notes = "二位码管理-二维码列表")
    @SysLog(operation = "静态二维码列表接口", operationDesc = "查询符合条件的静态二维码信息", key = "静态码管理")
    public R qrCodeList(@ApiParam(name = "ywlxId", value = "业务类型id", required = false) @RequestParam(value = "ywlxId", required = false) String ywlxId,
                        @ApiParam(name = "xmmc", value = "项目名称", required = false) @RequestParam(value = "xmmc", required = false) String xmmc,
                        @ApiParam(name = "xhfNsrsbh", value = "销方纳税人识别号", required = true) @RequestParam(value = "xhfNsrsbh", required = true) String xhfNsrsbh,
						@ApiParam(name = "sjly", value = "来源", required = false) @RequestParam(value = "sjly", required = false) String sjly,
                        @ApiParam(name = "pageSize", value = "每页条数", required = false) @RequestParam(value = "pageSize", required = false) String pageSize,
                        @ApiParam(name = "currentPage", value = "当前页", required = false) @RequestParam(value = "currentPage", required = false) String currentPage) {
	    log.info("{}静态二维码列表接口", LOGGER_MSG);

	    if (StringUtils.isBlank(xhfNsrsbh)) {
		    log.error("{},请求税号为空!", LOGGER_MSG);
		    return R.error(OrderInfoContentEnum.TAXCODE_ISNULL);
	    }

	    List<String> shList = NsrsbhUtils.transShListByXhfNsrsbh(xhfNsrsbh);
	    Map<String, Object> paramMap = new HashMap<>(5);
	    paramMap.put("ywlxId", ywlxId);
	    paramMap.put("xmmc", xmmc);
	    paramMap.put("pageSize", pageSize);
	    paramMap.put("currentPage", currentPage);
	    //查询列表数据
	    PageUtils pageUtil = qrcodeService.queryQrCodeList(paramMap, shList);
	    return R.ok().put("page", pageUtil);
    }

	/**
	 * 二维码详情接口
	 *
	 * @return
	 */
	@PostMapping("/qrCodeDetail")
	@ApiOperation(value = "静态二维码详情", notes = "二维码管理-静态二维码详情")
	@SysLog(operation = "静态二维码详情", operationDesc = "静态二维码详情", key = "二维码管理")
	public R qrCodeDetail(@ApiParam(name = "qrcodeId", value = "二维码id", required = true) @RequestParam(value = "qrcodeId", required = true) String qrcodeId,
	                      @ApiParam(name = "xhfNsrsbh", value = "销货方纳税人识别号", required = true) @RequestParam(value = "xhfNsrsbh", required = true) String xhfNsrsbh) {
		log.info("{}静态二维码详情接口", LOGGER_MSG);
		if (StringUtils.isBlank(qrcodeId)) {
			return R.error(OrderInfoContentEnum.INVOICE_PARAM_ERROR);
		}
		if (StringUtils.isBlank(xhfNsrsbh)) {
			log.error("{},请求税号为空!", LOGGER_MSG);
			return R.error(OrderInfoContentEnum.TAXCODE_ISNULL);
		}

		List<String> shList = NsrsbhUtils.transShListByXhfNsrsbh(xhfNsrsbh);
		Map map = qrcodeService.queryQrCodeDetail(qrcodeId, shList);
		return R.ok().put("data", map);
	}




	/**
	 * 二维码图片接口
	 *
	 * @return
	 */
	@PostMapping("/qrCodeImg")
	@ApiOperation(value = "二维码图片接口", notes = "动态码管理-二维码图片接口")
	@SysLog(operation = "二维码图片接口", operationDesc = "二维码图片接口", key = "动态码管理")
	public R qrCodeImg(@ApiParam(name = "qrcodeId", value = "二维码id", required = true) @RequestParam(value = "qrcodeId", required = true) String qrcodeId,
					   @ApiParam(name = "type", value = "二维码类型", required = true) @RequestParam(value = "type", required = true) String type,
					   @ApiParam(name = "xhfNsrsbh", value = "销货方纳税人识别号", required = true) @RequestParam(value = "xhfNsrsbh", required = true) String xhfNsrsbh,
					   @ApiParam(name = "backGround", value = "二维码背景色", required = false) @RequestParam(value = "backGround", required = false) String backGround) {
		log.info("{}二维码图片接口", LOGGER_MSG);
		if (StringUtils.isBlank(qrcodeId)) {
			return R.error(OrderInfoContentEnum.INVOICE_PARAM_ERROR);
		}
		if (StringUtils.isBlank(xhfNsrsbh)) {
			log.error("{},请求税号为空!", LOGGER_MSG);
			return R.error(OrderInfoContentEnum.TAXCODE_ISNULL);
		}

		List<String> shList = NsrsbhUtils.transShListByXhfNsrsbh(xhfNsrsbh);
		Map map = qrcodeService.queryQrCodeImg(qrcodeId, type, shList,backGround);
		return R.ok().put("data", map);
	}


	/**
	 * 编辑静态码
	 * @return
	 */
	@PostMapping("/updateStaticEwmInfo")
	@ApiOperation(value = "编辑静态码", notes = "动态码管理-编辑静态码")
	@SysLog(operation = "编辑静态码", operationDesc = "编辑静态码", key = "动态码管理")
	public R updatEwmInfo(
			@ApiParam(name = "orderList", value = "orderList", required = true) @RequestBody QrcodeOrderInfo qrcodeOrderInfo) {
		log.info("{},编辑静态码的接口，入参:{}", LOGGER_MSG,JsonUtils.getInstance().toJsonString(qrcodeOrderInfo));
		if (qrcodeOrderInfo == null && StringUtils.isBlank(qrcodeOrderInfo.getId())) {
			return R.error(OrderInfoContentEnum.INVOICE_PARAM_ERROR);
		}

		return qrcodeService.updateStaticEwmInfo(qrcodeOrderInfo);
	}


	/**
	 * 删除功能
	 *
	 * @param qrcodeId
	 * @return
	 */
	@PostMapping("/deleteStaticEwmInfo")
	@SysLog(operation = "删除静态码", operationDesc = "删除静态码", key = "动态码管理")
	@ApiOperation(value = "删除静态码", notes = "动态码管理-删除静态码")
	public R deleteStaticEwmInfo(@ApiParam(name = "qrcodeId", value = "二维码id和销货方税号", required = true) @RequestBody String qrcodeId) {
		log.info("{}二维码详情接口", LOGGER_MSG);
		if (StringUtils.isBlank(qrcodeId)) {
			return R.error(OrderInfoContentEnum.INVOICE_PARAM_ERROR);
		}

		List<Map> idList = JSON.parseArray(qrcodeId, Map.class);

		return qrcodeService.deleteStaticEwmInfo(idList);
	}


    /**
     * 生成二维码是校验校验项目名称是否重复
     */
    private boolean checkOrderItemName(QrcodeOrderInfo qrcodeOrderInfo) {
        if (CollectionUtils.isNotEmpty(qrcodeOrderInfo.getOrderItemList())) {
	        List<PageOrderItemInfo> list = qrcodeOrderInfo.getOrderItemList();
	        List<String> xmmcList = new ArrayList<>();
            for (PageOrderItemInfo info : list) {
                xmmcList.add(info.getXmmc());
            }
	        HashSet set = new HashSet<>(xmmcList);
	        Boolean result = set.size() == list.size();
            return result;
        }
        return true;
    }

	/**
	 * 静态码待审核已审核列表
	 */


	/**
     * 动态码列表接口  优税发票小助手调用
     */
    @PostMapping("/dynamicQrCodeList")
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
	    if (StringUtils.isBlank(xhfNsrsbh)) {
		    log.error("{},请求税号为空!", LOGGER_MSG);
		    return R.error(OrderInfoContentEnum.TAXCODE_ISNULL);
	    }

	    List<String> shList = NsrsbhUtils.transShListByXhfNsrsbh(xhfNsrsbh);
	    Map<String, Object> paramMap = new HashMap<>(5);
	    paramMap.put("startTime", startTime);
	    paramMap.put("endTime", endTime);
	    paramMap.put("ddh", ddh);
	    if (StringUtils.isNotBlank(minJe)) {
		    Double dl = Double.parseDouble(minJe);
		    paramMap.put("minJe", DECIMAL_FORMAT.format(dl));
	    }
	    if (StringUtils.isNotBlank(maxJe)) {
            Double d2 = Double.parseDouble(maxJe);
		    paramMap.put("maxJe", DECIMAL_FORMAT.format(d2));

        }
        paramMap.put("currentPage", currentPage);
		paramMap.put("pageSize", pageSize);
		paramMap.put("ghfmc", ghfmc);
		paramMap.put("fpzldm", fpzldm);
		paramMap.put("kpzt", kpzt);
		paramMap.put("ddly", sjly);
		//转换作废时间
		Date startVlidDate = null;
		if (StringUtils.isNotBlank(startValidTime)) {
			startVlidDate = DateUtil.parse(startValidTime, "yyyy-MM-dd");
			startVlidDate = DateUtil.beginOfDay(startVlidDate);
			startValidTime = DateUtil.format(startVlidDate, "yyyy-MM-dd HH:mm:ss");

		}
		Date endVlidDate = null;
		if (StringUtils.isNotBlank(endValidTime)) {
			endVlidDate = DateUtil.parse(endValidTime, "yyyy-MM-dd");
			endVlidDate = DateUtil.endOfDay(endVlidDate);
			endValidTime = DateUtil.format(endVlidDate, "yyyy-MM-dd HH:mm:ss");

		}
		paramMap.put("startValidTime", startValidTime);
		paramMap.put("endValidTime", endValidTime);


        //查询条件 二维码状态 转换 0 未使用 1 已使用 2 已失效 3 已作废
        if ("0".equals(ewmzt)) {
            // 四个条件互斥

            paramMap.put("ewmzt", ewmzt);
            // 为作废
            paramMap.put("zfzt", "0");
            // 未过期
            Date now = new Date();
            if (StringUtils.isNotBlank(startValidTime)) {
				if (startVlidDate.after(now)) {
                    paramMap.put("startValidTime", startValidTime);
                } else {
					startVlidDate = DateUtil.beginOfDay(now);
					startValidTime = DateUtil.format(startVlidDate, "yyyy-MM-dd HH:mm:ss");
                    paramMap.put("startValidTime",startValidTime);
                }

            } else {
                paramMap.put("startValidTime", DateUtil.format(now, "yyyy-MM-dd HH:mm:ss"));
            }

        } else if ("1".equals(ewmzt)) {
            paramMap.put("ewmzt", ewmzt);
        } else if ("2".equals(ewmzt)) {
            //已过期的需要根据失效时间查询 已失效并且为作废的数据

            Date now = new Date();
            if (StringUtils.isNotBlank(endValidTime)) {
                if (endVlidDate.before(now)) {
                    paramMap.put("endValidTime", endValidTime);
                } else {
                    paramMap.put("endValidTime", DateUtil.format(now, "yyyy-MM-dd HH:mm:ss"));
                }

            } else {
                paramMap.put("endValidTime", DateUtil.format(now, "yyyy-MM-dd HH:mm:ss"));
            }
            paramMap.put("zfzt", "0");
            paramMap.put("ewmzt", "0");
        } else if ("3".equals(ewmzt)) {
            //已作废的根据作废状态查询
            paramMap.put("zfzt", "1");

        }
        //查询列表数据
        log.info("查询接口参数:{}", JsonUtils.getInstance().toJsonString(paramMap));
	    PageUtils pageUtil = qrcodeService.queryDynamicQrcodeList(paramMap, shList);
		return R.ok().put("page", pageUtil);
	}




	/**
	 * 二维码信息展示 优税发票小助手调用
	 */
	@PostMapping("/queryEwmDetailInfo")
	@ApiOperation(value = "动态二维码详情信息", notes = "动态码管理-动态二维码详情信息")
	@SysLog(operation = "动态二维码详情信息", operationDesc = "动态二维码详情信息", key = "二维码管理")
	public R queryEwmDetailInfo(
			@ApiParam(name = "fpqqlsh", value = "发票请求流水号", required = true) @RequestParam(value = "fpqqlsh", required = true) String fpqqlsh,
			@ApiParam(name = "xhfNsrsbh", value = "销货方纳税人识别号", required = true) @RequestParam(value = "xhfNsrsbh", required = true) String xhfNsrsbh) {
		if (StringUtils.isBlank(fpqqlsh)) {
			return R.error().put("9999", "请求参数不能为空");
		}
		if (StringUtils.isBlank(xhfNsrsbh)) {
			log.error("{},请求税号为空!", LOGGER_MSG);
			return R.error(OrderInfoContentEnum.TAXCODE_ISNULL);
		}

		List<String> shList = NsrsbhUtils.transShListByXhfNsrsbh(xhfNsrsbh);
		Map<String, Object> queryEwmConfigInfo = qrcodeService.queryEwmDetailByFpqqlsh(fpqqlsh, shList);
		return R.ok().put("data", queryEwmConfigInfo);
	}




	/**
	 * 优税发票小助手调用 产品无调用
	 * @param pageQrcodeOrderInfo
	 * @return
	 */
	@ApiOperation(value = "生成动态码", notes = "扫码开票-生成动态码")
	@SysLog(operation = "生成动态码", operationDesc = "生成动态码", key = "生成动态码")
	@PostMapping("/generateDynamicQrCode")
	public R generateDynamicQrCode(@RequestBody PageQrcodeOrderInfo pageQrcodeOrderInfo) {
		R r = qrcodeService.generateDynamicQrCode(pageQrcodeOrderInfo);
		return r;

	}




	/**
	 *  优税发票小助手调用 产品无调用
	 * 查询二维码和发票信息
	 */
	@PostMapping("/queryQrcodeAndInvoiceDateil")
	@ApiOperation(value = "二维码发票信息查询", notes = "二维码发票信息查询")
	@SysLog(operation = "二维码发票信息查询", operationDesc = "二维码发票信息查询", key = "动态码管理")
	public R queryQrcodeAndInvoiceDateil(
			@ApiParam(name = "qrcodeId", value = "二维码id", required = true) @RequestParam(value = "qrcodeId", required = true) String qrcodeId,
			@ApiParam(name = "type", value = "二维码类型", required = true) @RequestParam(value = "type", required = true) String type,
			@ApiParam(name = "xhfNsrsbh", value = "销货方纳税人识别号", required = true) @RequestParam(value = "xhfNsrsbh", required = true) String xhfNsrsbh,
			@ApiParam(name = "backGround", value = "二维码背景色", required = false) @RequestParam(value = "backGround", required = false) String backGround) {

        log.debug("查询二维码和发票信息的接口，入参,qrcodeId:{},type:{},xhfNsrsbh:{}",qrcodeId,type,xhfNsrsbh);
		List<String> shList = NsrsbhUtils.transShListByXhfNsrsbh(xhfNsrsbh);
		Map<String, Object> queryEwmConfigInfo = qrcodeService.queryQrcodeAndInvoiceDateil(qrcodeId,type,shList,backGround);
		return R.ok().put("data", queryEwmConfigInfo);
	}



	/**
	 * 更新二维码信息 二维码失效或激活  优税发票小助手调用
	 */
	@PostMapping("/updateEwmDetailInfo")
	@ApiOperation(value = "更新二维码详情信息", notes = "动态码管理-更新二维码详情信息")
	@SysLog(operation = "更新二维码信息", operationDesc = "更新二维码详情信息", key = "动态码管理")
	public R updateEwmDetailInfo(
			@ApiParam(name = "param", value = "动态码信息表主键id和销方税号和二维码状态", required = true) @RequestBody() String param) {

		/**
		 * 修改id为数组格式,传递多个[{"id":"xxxx","xhfNsrsbh":"dddddd"}]
		 * 二维码状态  0 失效 1 激活
		 */
		log.debug("更新二维码状态的接口，参数，id:{}", param);
		if (StringUtils.isBlank(param)) {
			return R.error().put("9999", "请求参数不能为空");
		}
		List<Map> idList = JSON.parseArray(param, Map.class);

		boolean isUpdate = qrcodeService.updateEwmDetailInfo(idList);
		if (isUpdate) {
			return R.ok();
		} else {
			return R.error().put(OrderManagementConstant.MESSAGE, "二维码状态更新失败");

		}

	}


	/**
	 * 二维码配置信息获取
	 */
	@PostMapping("/queryEwmConfigInfo")
	@ApiOperation(value = "动态二维码配置信息", notes = "二维码管理-动态二维码配置信息")
	@SysLog(operation = "动态二维码配置信息", operationDesc = "动态二维码配置信息", key = "动态码管理")
	public R queryEwmConfigInfo(
			@ApiParam(name = "xhfNsrsbh", value = "销货方纳税人识别号", required = true) @RequestParam(value = "xhfNsrsbh", required = true) String xhfNsrsbh) {
		if (StringUtils.isBlank(xhfNsrsbh)) {
			return R.error().put("9999", "销货方纳税人识别号不能为空!");
		}
		Map<String, Object> paramMap = new HashMap<>(5);
		paramMap.put("xhfNsrsbh", xhfNsrsbh);

		Map<String, Object> queryEwmConfigInfo = qrcodeService.queryEwmConfigInfo(paramMap);
		return R.ok().put("data", queryEwmConfigInfo);
	}



	/**
	 * 二维码配置信息更新或新增
	 */
	@PostMapping("/updateEwmConfigInfo")
	@ApiOperation(value = "更新动态二维码配置信息", notes = "二维码管理-更新动态二维码配置信息")
	@SysLog(operation = "更新动态二维码配置信息", operationDesc = "更新动态二维码配置信息", key = "二维码管理")
	public R updateEwmConfigInfo(
			@RequestBody PageEwmConfigInfo ewmConfig) {
		if (ewmConfig == null || StringUtils.isBlank(ewmConfig.getXhfNsrsbh())) {
			return R.error().put("9999", "销货方纳税人识别号不能为空!");
		}
		Map<String, Object> paramMap = new HashMap<>(5);
		paramMap.put("xhfNsrsbh", ewmConfig.getXhfNsrsbh());

		Map<String, Object> queryEwmConfigInfo = qrcodeService.queryEwmConfigInfo(paramMap);
		if (queryEwmConfigInfo == null) {
			//新增二维码配置信息
			boolean b = qrcodeService.addEwmConfigInfo(ewmConfig);
			if (!b) {
				log.error("添加二维码配置信息失败，税号:{}", ewmConfig.getXhfNsrsbh());
				return R.error().put("9999", "二维码配置信息设置失败!");
			}
			return R.ok().put("0000", "二维码配置信息设置成功!");


		}else{
			//更新二维码配置信息
			boolean b = qrcodeService.updateEwmConfigInfo(ewmConfig);
			if(!b){
				log.error("添加二维码配置信息失败，税号:{}",ewmConfig.getXhfNsrsbh());
				return R.error().put("9999","二维码配置信息设置失败!" );
			}
			return R.ok().put("0000", "二维码配置信息设置成功!");

		}

	}


	/**
	 * 优税发票小助手调用 判断是否存在待审核的订单
	 * @return
	 */

	@PostMapping("/isExistNoAuditOrder")
	@ApiOperation(value = "查询是否存在为审核的订单", notes = "查询是否存在为审核的订单")
	@SysLog(operation = "查询是否存在为审核的订单", operationDesc = "查询是否存在为审核的订单", key = "二维码管理")
	public R isExistNoAuditOrder(@RequestBody OrderListQuery orderBatchQuery) {

		Map<String, Object> paramMap = PageBeanConvertUtil.convertToMap(orderBatchQuery);

		List<String> shList = NsrsbhUtils.transShListByXhfNsrsbh(orderBatchQuery.getXhfNsrsbh());

		boolean b = qrcodeService.isExistNoAuditOrder(paramMap, shList);

		Map<String, String> resultMap = new HashMap<>(2);
		if (b) {
			resultMap.put("isExist", "1");
		} else {
			resultMap.put("isExist", "0");
		}
		return R.ok().put(OrderManagementConstant.CODE, OrderInfoContentEnum.SUCCESS.getKey()).put("data", resultMap);
	}


}
