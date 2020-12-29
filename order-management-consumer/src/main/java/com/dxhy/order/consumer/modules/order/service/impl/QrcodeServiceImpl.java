package com.dxhy.order.consumer.modules.order.service.impl;

import cn.hutool.core.date.DateUtil;
import com.dxhy.order.api.*;
import com.dxhy.order.constant.ConfigureConstant;
import com.dxhy.order.constant.OrderInfoContentEnum;
import com.dxhy.order.constant.OrderInfoEnum;
import com.dxhy.order.constant.OrderManagementConstant;
import com.dxhy.order.consumer.config.OpenApiConfig;
import com.dxhy.order.consumer.model.page.PageEwmConfigInfo;
import com.dxhy.order.consumer.model.page.PageEwmItem;
import com.dxhy.order.consumer.model.page.PageOrderItemInfo;
import com.dxhy.order.consumer.model.page.QrcodeOrderInfo;
import com.dxhy.order.consumer.modules.order.service.IGenerateReadyOpenOrderService;
import com.dxhy.order.consumer.modules.order.service.QrcodeService;
import com.dxhy.order.consumer.modules.scaninvoice.model.PageQrcodeOrderInfo;
import com.dxhy.order.consumer.openapi.service.ICommonInterfaceService;
import com.dxhy.order.consumer.utils.PageDataDealUtil;
import com.dxhy.order.consumer.utils.QrCodeUtil;
import com.dxhy.order.exceptions.OrderReceiveException;
import com.dxhy.order.model.*;
import com.dxhy.order.utils.DateUtils;
import com.dxhy.order.utils.JsonUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.dubbo.config.annotation.Reference;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;

import javax.annotation.Resource;
import java.util.*;

/**
 * 静态码业务处理
 *
 * @author ZSC-DXHY
 * @date 创建时间: 2020-08-14 20:38
 */
@Service
@Slf4j
public class QrcodeServiceImpl implements QrcodeService {
	
	@Reference
	ApiQuickCodeInfoService apiQuickCodeInfoService;
	
	@Reference
	ApiInvoiceCommonService apiInvoiceCommonService;
	
	@Reference
	ApiOrderItemInfoService apiOrderItemInfoService;
	
	@Reference
	ApiOrderQrcodeExtendService apiOrderQrcodeExtendService;

	@Resource
	ICommonInterfaceService iCommonInterfaceService;

    @Reference
	ApiVerifyOrderInfo apiVerifyOrderInfo;

    @Resource
	IGenerateReadyOpenOrderService generateReadyOpenOrderService;




	@Override
	public boolean saveQrcodeInfo(QrcodeOrderInfo qrcodeOrderInfo) {
		
		log.info("二维码信息保存接口，入参:{}", JsonUtils.getInstance().toJsonString(qrcodeOrderInfo));
		QuickResponseCodeInfo qrCodeInfo = qrcodeOrderInfoToQuickRsponseInfo(qrcodeOrderInfo);
		qrCodeInfo.setId(apiInvoiceCommonService.getGenerateShotKey());
		
		//生成提取码
		qrCodeInfo.setTqm(qrcodeOrderInfo.getTqm());
		qrCodeInfo.setQuickResponseCodeType(ConfigureConstant.STRING_0);
		List<QuickResponseCodeItemInfo> itemList = qrcodeOrderInfoToQuickRsponseItem(qrcodeOrderInfo.getOrderItemList(), qrCodeInfo.getId(), qrCodeInfo.getXhfNsrsbh());
		
		List<InvoiceTypeCodeExt> extList = buildInvoiceTypeExt(qrcodeOrderInfo, qrCodeInfo.getId(), qrCodeInfo.getXhfNsrsbh());
		
		return apiQuickCodeInfoService.saveQrcodeInfo(qrCodeInfo, itemList, extList);
	}
	
	@Override
	public PageUtils queryQrCodeList(Map map, List<String> shList) {
		PageUtils page = apiQuickCodeInfoService.queryQrCodeList(map, shList);
		return page;
	}
	
	@Override
	public Map<String, Object> queryQrCodeDetail(String qrCodeId, List<String> xhfNsrsbh) {
		log.info("二维码详情查询接口，入参", qrCodeId);
		Map<String, Object> returnMap = new HashMap<>(5);
		//查询二维码基础信息
		QuickResponseCodeInfo info = apiQuickCodeInfoService.queryQrCodeDetail(qrCodeId, xhfNsrsbh);
		//查询二维码项目明细信息
		List<QuickResponseCodeItemInfo> itemList = apiQuickCodeInfoService.queryQrCodeItemListByQrcodeId(qrCodeId, xhfNsrsbh);
		//查询二维码发票种类代码信息
		List<InvoiceTypeCodeExt> invoiceTypeList = apiQuickCodeInfoService.queryInvoiceTypeByQrcodeId(qrCodeId, xhfNsrsbh);
		
		returnMap.put("qrcodeInfo", info);
        returnMap.put("qrcodeItemList", itemList);
        returnMap.put("invoiceTypeList", invoiceTypeList);

		return returnMap;
	}
	
	@Override
	public Map<String, Object> queryQrCodeImg(String qrcodeId, String type, List<String> xhfNsrsbh, String backGround) {
		Map<String, Object> returnMap = new HashMap<>(5);
		
		if (OrderInfoEnum.QR_TYPE_0.getKey().equals(type)) {
			//动态码
			QuickResponseCodeInfo info = apiQuickCodeInfoService.queryQrCodeDetail(qrcodeId, xhfNsrsbh);
			if (ObjectUtils.isEmpty(info)) {
				return null;
			}
			// 生成qrcode的base64流
			String qrCodeString = QrCodeUtil.drawLogoQrCode(null,
					String.format(OpenApiConfig.qrCodeShortUrl, info.getTqm()), "", backGround);
			returnMap.put("ywxl", info.getYwlx());
			returnMap.put("ewm", qrCodeString);
			returnMap.put("qrcodeUrl", String.format(OpenApiConfig.qrCodeShortUrl, info.getTqm()));
			returnMap.put("qrcodeId", info.getId());
		} else {
			//静态码
			OrderQrcodeExtendInfo orderQrcodeExtendInfo = apiOrderQrcodeExtendService.queryQrcodeDetailById(qrcodeId, xhfNsrsbh);
			if (orderQrcodeExtendInfo == null) {
				log.warn("动态码不存在,id:{}", qrcodeId);
				return null;
			}
			// 生成qrcode的base64流
			String qrCodeString = QrCodeUtil.drawLogoQrCode(null,
					String.format(OpenApiConfig.qrCodeShortUrl, orderQrcodeExtendInfo.getTqm()), "", backGround);
			returnMap.put("ewm", qrCodeString);
			returnMap.put("qrcodeUrl", String.format(OpenApiConfig.qrCodeShortUrl, orderQrcodeExtendInfo.getTqm()));
			returnMap.put("qrcodeId", orderQrcodeExtendInfo.getId());
		}
		return returnMap;
	}
	
	private List<InvoiceTypeCodeExt> buildInvoiceTypeExt(QrcodeOrderInfo qrcodeOrderInfo, String invoiceTypeCodeId, String xhfNsrsbh) {
		
		List<InvoiceTypeCodeExt> extList = new ArrayList<>();
		String fpzldm = qrcodeOrderInfo.getFpzldm();
		if (StringUtils.isNotEmpty(fpzldm)) {
			/*String[] fpzldmArr = fpzldm.split("\\|");*/
			String[] fpzldmArr = JsonUtils.getInstance().fromJson(fpzldm, String[].class);
			for (String fpzl : fpzldmArr) {
				InvoiceTypeCodeExt invoiceTypeCodeExt = new InvoiceTypeCodeExt();
				invoiceTypeCodeExt.setFpzlDm(fpzl);
				invoiceTypeCodeExt.setCreateTime(new Date());
				if (OrderInfoEnum.ORDER_INVOICE_TYPE_51.getKey().equals(fpzl)) {
					invoiceTypeCodeExt.setFpzlDmMc(OrderInfoEnum.ORDER_INVOICE_TYPE_51.getValue());
				} else if (OrderInfoEnum.ORDER_INVOICE_TYPE_2.getKey().equals(fpzl)) {
					invoiceTypeCodeExt.setFpzlDmMc(OrderInfoEnum.ORDER_INVOICE_TYPE_2.getValue());
				} else if (OrderInfoEnum.ORDER_INVOICE_TYPE_0.getKey().equals(fpzl)) {
					invoiceTypeCodeExt.setFpzlDmMc(OrderInfoEnum.ORDER_INVOICE_TYPE_0.getValue());
				}
				invoiceTypeCodeExt.setId(apiInvoiceCommonService.getGenerateShotKey());
				invoiceTypeCodeExt.setInvoiceTypeCodeId(invoiceTypeCodeId);
				invoiceTypeCodeExt.setXhfNsrsbh(xhfNsrsbh);
				extList.add(invoiceTypeCodeExt);
			}
		}
	    return extList;
	}
	
	private List<QuickResponseCodeItemInfo> qrcodeOrderInfoToQuickRsponseItem(List<PageOrderItemInfo> orderItemList,
	                                                                          String quickResponseCodeInfoId, String xhfNsrsbh) {
		
		List<QuickResponseCodeItemInfo> itemList = new ArrayList<>();
		int i = 1;
		for (PageOrderItemInfo item : orderItemList) {
			QuickResponseCodeItemInfo itemInfo = new QuickResponseCodeItemInfo();
			itemInfo.setXmmc(item.getXmmc());
			itemInfo.setZzstsgl(item.getZzstsgl());
			itemInfo.setByzd1(item.getByzd1());
			itemInfo.setByzd2(item.getByzd2());
			itemInfo.setByzd3(item.getByzd3());
			itemInfo.setByzd4(item.getByzd4());
			itemInfo.setByzd5(item.getByzd5());
			itemInfo.setCreateTime(new Date());
			itemInfo.setFphxz(item.getFphxz());
			itemInfo.setGgxh(item.getGgxh());
			itemInfo.setYhzcbs(item.getYhzcbs());
			itemInfo.setHsbz(item.getHsbz());
			itemInfo.setId(apiInvoiceCommonService.getGenerateShotKey());
			itemInfo.setKce(item.getKce());
			itemInfo.setLslbs(item.getLslbs());
			itemInfo.setQuickResponseCodeInfoId(quickResponseCodeInfoId);
			itemInfo.setSe(item.getSe());
			itemInfo.setSl(item.getSl());
			itemInfo.setSpbm(item.getSpbm());
			itemInfo.setSphxh(String.valueOf(i));
			itemInfo.setWcje(item.getWcje());
			itemInfo.setXmdj(item.getXmdj());
			itemInfo.setXmdw(item.getXmdw());
			itemInfo.setXmje(item.getXmje());
			itemInfo.setXmsl(item.getXmsl());
			itemInfo.setZxbm(item.getZxbm());
			itemInfo.setXhfNsrsbh(xhfNsrsbh);
			itemList.add(itemInfo);
			i++;
		}
		return itemList;
	}

	private QuickResponseCodeInfo qrcodeOrderInfoToQuickRsponseInfo(QrcodeOrderInfo qrcodeOrderInfo) {
		QuickResponseCodeInfo quickResponseCodeInfo = new QuickResponseCodeInfo();
		quickResponseCodeInfo.setCreateTime(new Date());
		quickResponseCodeInfo.setUpdateTime(new Date());
		quickResponseCodeInfo.setYwlxId(qrcodeOrderInfo.getYwlxid());
		quickResponseCodeInfo.setYwlx(qrcodeOrderInfo.getYwlx());
		quickResponseCodeInfo.setSld(qrcodeOrderInfo.getSld());
		quickResponseCodeInfo.setSkr(qrcodeOrderInfo.getSkr());
		quickResponseCodeInfo.setFhr(qrcodeOrderInfo.getFhr());
		quickResponseCodeInfo.setKpr(qrcodeOrderInfo.getKpr());
		quickResponseCodeInfo.setXhfMc(qrcodeOrderInfo.getXhfmc());
		quickResponseCodeInfo.setXhfNsrsbh(qrcodeOrderInfo.getXhfNsrsbh());
		quickResponseCodeInfo.setXhfDz(qrcodeOrderInfo.getXhfdz());
		quickResponseCodeInfo.setXhfYh(qrcodeOrderInfo.getXhfyh());
		quickResponseCodeInfo.setXhfDh(qrcodeOrderInfo.getXhfdh());
		quickResponseCodeInfo.setXhfZh(qrcodeOrderInfo.getXhfzh());
		quickResponseCodeInfo.setQuickResponseCodeType(qrcodeOrderInfo.getQrCodeType());
		quickResponseCodeInfo.setQuickResponseCodeUrl(qrcodeOrderInfo.getQrCodeUrl());
		quickResponseCodeInfo.setEwmzt(ConfigureConstant.STRING_0);
		return quickResponseCodeInfo;
	}
	
	
	
    /**
     * 动态码列表接口
     */
    @Override
    public PageUtils queryDynamicQrcodeList(Map<String, Object> paramMap, List<String> shList) {
	
	    PageUtils page = apiOrderQrcodeExtendService.queryDynamicQrCodeList(paramMap, shList);
	
	
	    List<Map> list = (List<Map>) page.getList();
	    for (Map map : list) {
		    //开票状态处理
		    String kpzt = map.get("kpzt") == null ? "" : String.valueOf(map.get("kpzt"));
		    if (StringUtils.isBlank(kpzt)) {
			    map.put("kpzt", "0");
		    }
		    //二维码状处理
			String ewmzt = map.get("ewmzt") == null ? "" : String.valueOf(map.get("ewmzt"));
			
			if("0".equals(ewmzt)){
				String zfzt = map.get("zfzt") == null ? "" : String.valueOf(map.get("zfzt"));
				if("1".equals(zfzt)){
					map.put("ewmzt", "3");
				}else{
					String validTime = map.get("quickResponseCodeValidTime") == null ? "" : String.valueOf(map.get("quickResponseCodeValidTime"));
					if(!StringUtils.isBlank(validTime)){
						
						Date validDate = DateUtil.parse(validTime, "yyyy-MM-dd HH:mm:ss");
						if(new Date().after(validDate)){
							//二维码失效
							map.put("ewmzt", "2");
						}else{
							map.put("ewmzt", "0");
						}
					}else{
						map.put("ewmzt", "0");
					}
				}
			}else{
				String zfzt = map.get("zfzt") == null ? "" : String.valueOf(map.get("zfzt"));

				if("1".equals(zfzt)){
					map.put("ewmzt", "3");
				}else{
					map.put("ewmzt", "1");

				}
			}
			
		}
		page.setList(list);
		//列表数据处理
		
		return page;
	}
	
	
	@Override
	public Map<String,Object> queryEwmConfigInfo(Map<String,Object> paramMap) {
		
		EwmConfigInfo queryEwmConfigInfo = apiQuickCodeInfoService.queryEwmConfigInfo(paramMap);
		if (queryEwmConfigInfo == null) {
			return null;
		}
		List<EwmConfigItemInfo> ewmConfigItemList = apiQuickCodeInfoService.queryEwmConfigItemInfoById(queryEwmConfigInfo.getId());
		Map<String, Object> resultMap = new HashMap<>(5);
		resultMap.put("ewmConfigInfo", queryEwmConfigInfo);
		resultMap.put("ewmConfigItemList", ewmConfigItemList);
		return resultMap;
	}
	
	
	@Override
	public boolean addEwmConfigInfo(PageEwmConfigInfo pageEwmConfigInfo) {
		EwmConfigInfo ewmConfig = new EwmConfigInfo();
		ewmConfig.setInvalidTime(pageEwmConfigInfo.getInvalidTime());
		ewmConfig.setXhfMc(pageEwmConfigInfo.getXhfMc());
		ewmConfig.setXhfNsrsbh(pageEwmConfigInfo.getXhfNsrsbh());
		ewmConfig.setCreateTime(new Date());
		ewmConfig.setId(apiInvoiceCommonService.getGenerateShotKey());
		
		List<PageEwmItem> itemList = pageEwmConfigInfo.getItemList();
		List<EwmConfigItemInfo> ewmConfigItemList = new ArrayList<>();
		for (PageEwmItem item : itemList) {
			EwmConfigItemInfo ewmItem = new EwmConfigItemInfo();
			ewmItem.setCreateTime(new Date());
			ewmItem.setEwmCoinfgId(ewmConfig.getId());
			ewmItem.setFpzldm(item.getFpzldm());
			ewmItem.setId(apiInvoiceCommonService.getGenerateShotKey());
			ewmItem.setSld(item.getSld());
			ewmItem.setSldMc(item.getSld_mc());
			ewmConfigItemList.add(ewmItem);
			
		}
		
		return apiQuickCodeInfoService.addEwmConfigInfo(ewmConfig,ewmConfigItemList);
	}
	
	
	@Override
	public boolean updateEwmConfigInfo(PageEwmConfigInfo pageEwmConfigInfo) {
		
		EwmConfigInfo ewmConfig = new EwmConfigInfo();
		ewmConfig.setInvalidTime(pageEwmConfigInfo.getInvalidTime());
		ewmConfig.setXhfMc(pageEwmConfigInfo.getXhfMc());
		ewmConfig.setXhfNsrsbh(pageEwmConfigInfo.getXhfNsrsbh());
		ewmConfig.setId(pageEwmConfigInfo.getId());
		
		List<PageEwmItem> itemList = pageEwmConfigInfo.getItemList();
		List<EwmConfigItemInfo> ewmConfigItemList = new ArrayList<>();
		for (PageEwmItem item : itemList) {
			EwmConfigItemInfo ewmItem = new EwmConfigItemInfo();
			ewmItem.setCreateTime(new Date());
			ewmItem.setEwmCoinfgId(ewmConfig.getId());
			ewmItem.setFpzldm(item.getFpzldm());
			ewmItem.setId(apiInvoiceCommonService.getGenerateShotKey());
			ewmItem.setSld(item.getSld());
			ewmItem.setSldMc(item.getSld_mc());
			ewmConfigItemList.add(ewmItem);
			
		}
		return apiQuickCodeInfoService.updateEwmConfigInfo(ewmConfig,ewmConfigItemList);
	}
	
	
	@Override
	public Map<String, Object> queryEwmDetailByFpqqlsh(String fpqqlsh, List<String> shList) {
		
		Map<String, Object> resultMap = new HashMap<>(5);
		Map<String, Object> queryEwmDetailByFpqqlsh = apiOrderQrcodeExtendService.queryEwmDetailByFpqqlsh(fpqqlsh, shList);
		resultMap.put("qrCodeInfo", queryEwmDetailByFpqqlsh);
		
		if (queryEwmDetailByFpqqlsh != null) {
			String orderInfoId = queryEwmDetailByFpqqlsh.get("orderInfoId") == null ? "" : String.valueOf(queryEwmDetailByFpqqlsh.get("orderInfoId"));
			//todo zsc 等待处理明细表时进行操作
			List<OrderItemInfo> orderItemInfos = apiOrderItemInfoService.selectOrderItemInfoByOrderId(orderInfoId, shList);
			PageDataDealUtil.dealOrderItemInfo(orderItemInfos);
			if (CollectionUtils.isNotEmpty(orderItemInfos)) {
				resultMap.put("qrCodeItemList", orderItemInfos);
			}
		}
		return resultMap;
	}
	
	
	@Override
	public boolean updateEwmDetailInfo(List<Map> idList) {
		
		return apiOrderQrcodeExtendService.updateEwmDetailInfoByIds(idList);
	}
	
	@Override
	public R updateStaticEwmInfo(QrcodeOrderInfo qrcodeOrderInfo) {
		
		QuickResponseCodeInfo qrCodeInfo = qrcodeOrderInfoToQuickRsponseInfo(qrcodeOrderInfo);
		qrCodeInfo.setCreateTime(null);
		qrCodeInfo.setId(qrcodeOrderInfo.getId());
		
		List<QuickResponseCodeItemInfo> itemList = qrcodeOrderInfoToQuickRsponseItem(qrcodeOrderInfo.getOrderItemList(), qrCodeInfo.getId(), qrCodeInfo.getXhfNsrsbh());
		
		List<InvoiceTypeCodeExt> extList = buildInvoiceTypeExt(qrcodeOrderInfo, qrCodeInfo.getId(), qrCodeInfo.getXhfNsrsbh());
		
		return apiQuickCodeInfoService.updateStaticEwmInfo(qrCodeInfo, itemList, extList);
	}
	
	@Override
	public R deleteStaticEwmInfo(List<Map> qrcodeId) {
		
		for (Map map : qrcodeId) {
			String id = (String) map.get("id");
			String nsrsbh = (String) map.get("xhfNsrsbh");
			List<String> shList = new ArrayList<>();
			shList.add(nsrsbh);
			QuickResponseCodeInfo quickResponseCodeInfo = new QuickResponseCodeInfo();
			quickResponseCodeInfo.setId(id);
			quickResponseCodeInfo.setEwmzt("1");
			quickResponseCodeInfo.setUpdateTime(new Date());
			quickResponseCodeInfo.setXhfNsrsbh(nsrsbh);
			
			boolean b = apiQuickCodeInfoService.updateEwmDetailInfo(quickResponseCodeInfo, shList);
			if (b) {
				return R.ok();
			} else {
				return R.error();
			}
		}
		return R.ok();
		
	}

	@Override
	public R generateDynamicQrCode(PageQrcodeOrderInfo pageQrcodeOrderInfo) {

		try {
			pageQrcodeOrderInfo.setKpy("ysy");
			CommonOrderInfo pageToFpkjInfo = PageDataDealUtil.pageToFpkjInfo(pageQrcodeOrderInfo);
			List<String> shList = new ArrayList<>();
			shList.add(pageQrcodeOrderInfo.getXhfNsrsbh());
		
		
			//补全订单信息
			List<CommonOrderInfo> paramList = new ArrayList<CommonOrderInfo>();
			paramList.add(pageToFpkjInfo);
			generateReadyOpenOrderService.completeOrderInfo(paramList,pageQrcodeOrderInfo.getUid());
			if(StringUtils.isBlank(pageToFpkjInfo.getOrderInfo().getFpqqlsh())){
				pageToFpkjInfo.getOrderInfo().setFpqqlsh(apiInvoiceCommonService.getGenerateShotKey());
			}
			
			//校验二维码信息
			Map<String, String> resultMap = apiVerifyOrderInfo.verifyDynamicEwmInfo(pageToFpkjInfo);
			if (!OrderInfoContentEnum.SUCCESS.getKey()
					.equals(resultMap.get(OrderManagementConstant.ERRORCODE))) {
				log.error("动态码生成，订单信息校验失败，请求流水号:{},错误信息:{}", pageToFpkjInfo.getOrderInfo().getFpqqlsh(), resultMap.get(OrderManagementConstant.ERRORMESSAGE));
				return R.error().put(OrderManagementConstant.CODE, resultMap.get(OrderManagementConstant.ERRORCODE))
						.put(OrderManagementConstant.MESSAGE, resultMap.get(OrderManagementConstant.ERRORMESSAGE));
			}
			
			
			//查询当前税号下的配置信息
			Map<String, Object> paramMap = new HashMap<String, Object>(2);
			paramMap.put("xhfNsrsbh", pageToFpkjInfo.getOrderInfo().getXhfNsrsbh());
			EwmConfigInfo queryEwmConfigInfo = apiQuickCodeInfoService.queryEwmConfigInfo(paramMap);
			List<EwmConfigItemInfo> queryEwmConfigItemInfoById = new ArrayList<EwmConfigItemInfo>();
			if (queryEwmConfigInfo != null) {
				queryEwmConfigItemInfoById = apiQuickCodeInfoService.queryEwmConfigItemInfoById(queryEwmConfigInfo.getId());
			}
			
			
			//根据票种匹配受理点
			if (CollectionUtils.isNotEmpty(queryEwmConfigItemInfoById)) {
				for (EwmConfigItemInfo item : queryEwmConfigItemInfoById) {

					if (StringUtils.isBlank(pageToFpkjInfo.getOrderInfo().getFpzlDm())) {
						pageToFpkjInfo.getOrderInfo().setFpzlDm(OrderInfoEnum.ORDER_INVOICE_TYPE_51.getKey());
					}
					if (item.getFpzldm().equals(pageToFpkjInfo.getOrderInfo().getFpzlDm())) {
						pageToFpkjInfo.getOrderInfo().setSld(item.getSld());
						pageToFpkjInfo.getOrderInfo().setSldMc(item.getSldMc());
					}
				}
			}else{
				pageToFpkjInfo.getOrderInfo().setFpzlDm(OrderInfoEnum.ORDER_INVOICE_TYPE_51.getKey());
			}

			OrderProcessInfo processInfo = new OrderProcessInfo();
			OrderInvoiceInfo orderInvoiceInfo = new OrderInvoiceInfo();
			iCommonInterfaceService.buildInsertOrderData(pageToFpkjInfo.getOrderInfo(), pageToFpkjInfo.getOrderItemInfo(), processInfo, orderInvoiceInfo);


			//设置开票方式
			processInfo.setKpfs(OrderInfoEnum.ORDER_REQUEST_TYPE_2.getKey());
			processInfo.setDdly(OrderInfoEnum.ORDER_SOURCE_6.getKey());
			processInfo.setYwlx(pageToFpkjInfo.getOrderInfo().getYwlx());
			processInfo.setYwlxId(pageToFpkjInfo.getOrderInfo().getYwlxId());

			//创建原始订单信息
			OrderOriginExtendInfo orderOrginOrder = PageDataDealUtil.buildOriginOrder(pageToFpkjInfo);
			orderOrginOrder.setId(apiInvoiceCommonService.getGenerateShotKey());


			//保存订单二维码扩展表
			OrderQrcodeExtendInfo orderQrcodeExtendInfo = PageDataDealUtil.buildOrderQrcodeInfo(pageToFpkjInfo);

			//设置失效时间
			int invalidDays = 30;
			if (queryEwmConfigInfo != null) {
				invalidDays = StringUtils.isBlank(queryEwmConfigInfo.getInvalidTime()) ? 30
						: Integer.valueOf(queryEwmConfigInfo.getInvalidTime());
			}
			//订单没有过期时间，永不失效 数据库中过期时间设置为2099 01 01 00：00：00
			if(invalidDays == 0){
				Date validDate = DateUtil.parse("2099-01-01 00:00:00", "yyyy-MM-dd HH:mm:ss");
				orderQrcodeExtendInfo.setQuickResponseCodeValidTime(validDate);
			}else{
				Date validDate = DateUtils.addDateDays(pageToFpkjInfo.getOrderInfo().getDdrq(), invalidDays);
				orderQrcodeExtendInfo.setQuickResponseCodeValidTime(validDate);
			}
			orderQrcodeExtendInfo.setTqm(apiInvoiceCommonService.getGenerateShotKey());
			orderQrcodeExtendInfo.setId(apiInvoiceCommonService.getGenerateShotKey());
			orderQrcodeExtendInfo.setQuickResponseCodeType(OrderInfoEnum.QR_TYPE_1.getKey());
			orderQrcodeExtendInfo.setOrderInfoId(pageToFpkjInfo.getOrderInfo().getId());
			String qrcodeUrl = String.format(OpenApiConfig.qrCodeScanUrl, orderQrcodeExtendInfo.getTqm(), orderQrcodeExtendInfo.getXhfNsrsbh(), ConfigureConstant.STRING_1);
			orderQrcodeExtendInfo.setQuickResponseCodeUrl(qrcodeUrl);


            List<OrderQrcodeExtendInfo> qrcodeList = new ArrayList<>();
			List<OrderOriginExtendInfo> orderOriginList = new ArrayList<OrderOriginExtendInfo>();
			List<OrderInfo> orderInfoList = new ArrayList<OrderInfo>();
			List<OrderProcessInfo> orderProcessInfoList = new ArrayList<OrderProcessInfo>();
			List<List<OrderItemInfo>> orderItemInfoList = new ArrayList<List<OrderItemInfo>>();
			
			qrcodeList.add(orderQrcodeExtendInfo);
			orderOriginList.add(orderOrginOrder);
			orderInfoList.add(pageToFpkjInfo.getOrderInfo());
			orderItemInfoList.add(pageToFpkjInfo.getOrderItemInfo());
			orderProcessInfoList.add(processInfo);
			apiInvoiceCommonService.saveData(orderInfoList, orderItemInfoList, orderProcessInfoList, null, null, qrcodeList, orderOriginList, shList);
			
			
			String qrCodeString = QrCodeUtil.drawLogoQrCode(null,
					String.format(OpenApiConfig.qrCodeShortUrl, orderQrcodeExtendInfo.getTqm()), "", pageQrcodeOrderInfo.getBackGround());
			return R.ok().put("ewm", qrCodeString).put("qrcodeInfo", orderQrcodeExtendInfo);
		} catch (OrderReceiveException e) {
			log.error("生成动态码异常:{}",e.getMessage());
			return R.error().put(OrderManagementConstant.CODE,e.getCode()).put(OrderManagementConstant.MESSAGE,e.getMessage());
		}
	}

	@Override
	public Map<String, Object> queryQrcodeAndInvoiceDateil(String qrcodeId, String type, List<String> shList, String backGround) {
		
		Map<String, Object> resultMap = apiOrderQrcodeExtendService.queryQrcodeAndInvoiceDetail(qrcodeId, shList);
		String kpzt = resultMap.get("kpzt") == null ? "" : String.valueOf(resultMap.get("kpzt"));
		if (StringUtils.isBlank(kpzt)) {
			resultMap.put("kpzt", "0");
		}
		//二维码状处理
		String ewmzt = resultMap.get("ewmzt") == null ? "" : String.valueOf(resultMap.get("ewmzt"));
		
		if ("0".equals(ewmzt)) {
			String zfzt = resultMap.get("zfzt") == null ? "" : String.valueOf(resultMap.get("zfzt"));
			if ("1".equals(zfzt)) {
				resultMap.put("ewmzt", "3");
			}else{
				String validTime = resultMap.get("quickResponseCodeValidTime") == null ? "" : String.valueOf(resultMap.get("quickResponseCodeValidTime"));
				if(!StringUtils.isBlank(validTime)){

					Date validDate = DateUtil.parse(validTime, "yyyy-MM-dd HH:mm:ss");
					if(new Date().after(validDate)){
						//二维码失效
						resultMap.put("ewmzt", "2");
					}else{
						resultMap.put("ewmzt", "0");
					}
				} else {
					resultMap.put("ewmzt", "0");
				}
			}
		} else {
			resultMap.put("ewmzt", "1");
		}
		
		String qrCodeString = QrCodeUtil.drawLogoQrCode(null,
				String.format(OpenApiConfig.qrCodeShortUrl, resultMap.get("tqm") == null ? "" : String.valueOf(resultMap.get("tqm"))), "", backGround);
		resultMap.put("ewm", qrCodeString);
		
		return resultMap;
	}

	@Override
	public boolean isExistNoAuditOrder(Map<String,Object> paramMap,List<String> shList) {

		boolean b = apiOrderQrcodeExtendService.isExistNoAuditOrder(paramMap,shList);
		return b;
	}

}
