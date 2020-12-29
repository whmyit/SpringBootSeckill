package com.dxhy.order.consumer.modules.invoice.service.impl;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.RandomUtil;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.dxhy.order.api.*;
import com.dxhy.order.constant.*;
import com.dxhy.order.consumer.config.OpenApiConfig;
import com.dxhy.order.consumer.config.SystemConfig;
import com.dxhy.order.consumer.model.NewOrderExcel;
import com.dxhy.order.consumer.model.page.PageSld;
import com.dxhy.order.consumer.modules.fiscal.service.UnifyService;
import com.dxhy.order.consumer.modules.invoice.constant.SpecialInvoiceImportExcelEnum;
import com.dxhy.order.consumer.modules.invoice.service.SpecialInvoiceService;
import com.dxhy.order.consumer.modules.order.service.IGenerateReadyOpenOrderService;
import com.dxhy.order.consumer.modules.order.service.MakeOutAnInvoiceService;
import com.dxhy.order.consumer.modules.user.service.UserInfoService;
import com.dxhy.order.consumer.protocol.usercenter.DeptEntity;
import com.dxhy.order.exceptions.OrderReceiveException;
import com.dxhy.order.file.common.ExcelReadContext;
import com.dxhy.order.file.handle.ExcelReadHandle;
import com.dxhy.order.model.PageUtils;
import com.dxhy.order.model.*;
import com.dxhy.order.model.a9.ResponseBaseBean;
import com.dxhy.order.model.a9.hp.*;
import com.dxhy.order.model.a9.sld.QueryNextInvoiceRequest;
import com.dxhy.order.model.a9.sld.QueryNextInvoiceResponseExtend;
import com.dxhy.order.model.dto.PushPayload;
import com.dxhy.order.model.entity.*;
import com.dxhy.order.protocol.v4.invoice.*;
import com.dxhy.order.protocol.v4.order.DDMXXX;
import com.dxhy.order.utils.*;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.dubbo.config.annotation.Reference;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.Row;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.NumberFormat;
import java.util.*;

import static com.dxhy.order.constant.OrderInfoEnum.*;

/**
 * @Author fankunfeng
 * @Date 2019-06-15 11:05:31
 * @Describe
 */
@Service
@Slf4j
public class SpecialInvoiceServiceImpl implements SpecialInvoiceService {
	
	private static final String LOGGER_MSG = "（红字发票业务类）";
	private static final NumberFormat NF = NumberFormat.getPercentInstance();
	
	@Reference
	private ApiSpecialInvoiceReversalService reversalService;
	
	@Reference
	private ApiBuyerService buyerService;
	
	@Reference
	private ApiInvoiceService invoiceService;
	
	@Reference
	private ApiTaxClassCodeService apiTaxClassCodeService;
	
	@Reference
	private ApiSpecialInvoiceReversalService apiSpecialInvoiceReversalService;
	
	@Resource
	private IGenerateReadyOpenOrderService generateReadyOpenOrderService;
	
	@Resource
	private MakeOutAnInvoiceService makeOutAnInvoiceService;
	
	@Resource
	private UnifyService unifyService;
	
	@Reference
	private ApiTaxEquipmentService apiTaxEquipmentService;
	
	@Reference
	private ApiInvoiceCommonService apiInvoiceCommonService;
	
	@Reference
	private ValidateOrderInfo validateOrderInfo;
	
	@Reference
	private ApiOrderInvoiceInfoService apiOrderInvoiceInfoService;
	
	@Reference
	private ApiOrderInfoService apiOrderInfoService;
	
	@Resource
	private UserInfoService userInfoService;
	
	@Reference
	private ApiFangGeInterfaceService apiFangGeInterfaceService;

	@Reference
	private ApiPushService pushService;
	
	/**
	 * update by ysy 红字发票开具流程优化 20190829 update by ysy 红字申请单流程重写 2020-01-03
	 *
	 * @throws OrderSeparationException
	 */
	
	@Override
	public Map<String, Object> addInvoice(String[] ids, String accessPointId, String accessPointName,
	                                      String extensionNum, String operatorId, String operatorName, String departmentId, String taxpayerCode)
			throws OrderSeparationException {
		
		Map<String, Object> returnMap = new HashMap<>(5);
		
		// 存放校验的错误信息返回前端
		List<Map<String, String>> resultList = new ArrayList<>();
		
		int successCount = 0;
		for (String id : ids) {
			// 根据红字申请单id查询红字申请单信息
			SpecialInvoiceReversalEntity querySpecialInvoiceReversal = apiSpecialInvoiceReversalService
					.querySpecialInvoiceReversal(id);
			// 数据校验 开票人信息补全
			Map<String, String> exminMap = examinAndComplete(querySpecialInvoiceReversal, taxpayerCode, operatorId);
			if (!"0000".equals(exminMap.get(OrderManagementConstant.CODE))) {
				resultList.add(exminMap);
				continue;
			}
			
			//补全购方 销方信息
			querySpecialInvoiceReversal = completeSellerAndBuyerInfo(querySpecialInvoiceReversal,resultList);
			if(querySpecialInvoiceReversal == null){
				continue;
			}
			
			
			OrderInfo orderInfo = buildOrderInfo(querySpecialInvoiceReversal);
			// 开票机号
			orderInfo.setKpjh(extensionNum);
			// 开票点ID
			orderInfo.setSld(accessPointId);
			// 开票点名称
			orderInfo.setSldMc(accessPointName);
			// 根据原发票代码发票号码 判断特殊冲红标志 默认非特殊冲红
			orderInfo.setTschbz(OrderInfoEnum.TSCHBZ_0.getKey());
			
			// 特殊冲红标志产品不使用
			/*
			 * if
			 * (StringUtils.isNotBlank(specialInvoiceReversalsAndItemsMap.get(
			 * "sirinvoicecode")) &&
			 * StringUtils.isNotBlank(specialInvoiceReversalsAndItemsMap.get(
			 * "sirinvoiceno"))) { JSONObject invoiceData =
			 * 3.getPreInvoiceReversal(specialInvoiceReversalsAndItemsMap.get(
			 * "sirinvoicecode"),
			 * specialInvoiceReversalsAndItemsMap.get("sirinvoiceno")); if (null
			 * == invoiceData) { orderInfo.setTschbz("1"); } }
			 */
			// 根据id查询红字申请单明细信息
			//List<SpecialInvoiceReversalItemEntity> querySpecialInvoiceReversalItems =
			List<SpecialInvoiceReversalItem> specialInvoiceReversalItems = apiSpecialInvoiceReversalService.querySpecialInvoiceReversalItems(id);
			List<OrderItemInfo> orderItemInfoList = buildOrderItemInfo(specialInvoiceReversalItems, orderInfo.getXhfNsrsbh());
			CommonOrderInfo commonOrderInfo = new CommonOrderInfo();
			if (OrderInfoEnum.SPECIAL_INVOICE_REASON_1100000000.getKey().equals(querySpecialInvoiceReversal.getSqsm())) {
				// 购方已抵扣此字段不能为空
				commonOrderInfo.setFlagbs(ConfigureConstant.STRING_0);
			}
			
			commonOrderInfo.setOrderInfo(orderInfo);
			commonOrderInfo.setOrderItemInfo(orderItemInfoList);
			
			
			commonOrderInfo.setHzfpxxbbh(querySpecialInvoiceReversal.getXxbbh());
			
			R r = addInvoice(commonOrderInfo, operatorId, departmentId);
			
			if (OrderInfoContentEnum.SUCCESS.getKey().equals(r.get("code"))) {
				querySpecialInvoiceReversal.setEditorId(operatorId);
				querySpecialInvoiceReversal.setEditorName(operatorName);
				querySpecialInvoiceReversal.setKpzt("1");
				querySpecialInvoiceReversal.setUpdateTime(DateUtils.getDate(new Date(), DateUtils.DATE_TIME_PATTERN));
				Integer isEditSuccess = reversalService.updateSpecialInvoiceReversal(querySpecialInvoiceReversal);
				if (isEditSuccess <= 0) {
					log.error("开票成功后更新红字申请单");
				}
				successCount++;
			} else {
				Map<String, String> resultMap = new HashMap<>(5);
				resultMap.put(OrderManagementConstant.CODE, String.valueOf(r.get(OrderManagementConstant.CODE)));
				resultMap.put(OrderManagementConstant.MESSAGE, String.valueOf(r.get(OrderManagementConstant.MESSAGE)));
				resultList.add(resultMap);
				returnMap.put(OrderManagementConstant.CODE, "9999");
				returnMap.put(OrderManagementConstant.ALL_MESSAGE, "failure");
				
			}
		}
		returnMap.put("successCount", successCount);
		returnMap.put("data", resultList);
		return returnMap;
	}


    /**
     * 补全销方购方名称
     * @param querySpecialInvoiceReversal
     * @return
     */
    private SpecialInvoiceReversalEntity completeSellerAndBuyerInfo(SpecialInvoiceReversalEntity querySpecialInvoiceReversal,List<Map<String, String>> resultList) {


        //补全 销方信息
        if(StringUtils.isBlank(querySpecialInvoiceReversal.getXhfDz()) || StringUtils.isBlank(querySpecialInvoiceReversal.getXhfDh())
                || StringUtils.isBlank(querySpecialInvoiceReversal.getXhfYh()) || StringUtils.isBlank(querySpecialInvoiceReversal.getXhfZh())){
	
	        Map<String, DeptEntity> taxplayerEntityList = userInfoService.getTaxpayerEntityMap();
	        DeptEntity sysDeptEntity = taxplayerEntityList.get(querySpecialInvoiceReversal.getXhfNsrsbh());
	        if (sysDeptEntity != null) {
		        querySpecialInvoiceReversal.setXhfDz(StringUtils.isBlank(querySpecialInvoiceReversal.getXhfDz()) ? sysDeptEntity.getTaxpayerAddress() : querySpecialInvoiceReversal.getXhfDz());
		        querySpecialInvoiceReversal.setXhfDh(StringUtils.isBlank(querySpecialInvoiceReversal.getXhfDh()) ? sysDeptEntity.getTaxpayerPhone() : querySpecialInvoiceReversal.getXhfDh());
		        querySpecialInvoiceReversal.setXhfYh(StringUtils.isBlank(querySpecialInvoiceReversal.getXhfYh()) ? sysDeptEntity.getTaxpayerBank() : querySpecialInvoiceReversal.getXhfYh());
		        querySpecialInvoiceReversal.setXhfZh(StringUtils.isBlank(querySpecialInvoiceReversal.getXhfZh()) ? sysDeptEntity.getTaxpayerAccount() : querySpecialInvoiceReversal.getXhfZh());
		
		
	        } else {
		        BuyerEntity queryBuyerByPurchaseName = buyerService.queryBuyerByPurchaseName(querySpecialInvoiceReversal.getXhfMc(), querySpecialInvoiceReversal.getGhfNsrsbh());
		        if (queryBuyerByPurchaseName == null) {
			
			        Map<String, String> resultMap = new HashMap<>(5);
                    resultMap.put(OrderManagementConstant.CODE, "9999");
                    resultMap.put(OrderManagementConstant.MESSAGE, "信息表编号："+ querySpecialInvoiceReversal.getXxbbh() + " 的申请单购方信息不全，请去客户信息中维护 "+ querySpecialInvoiceReversal.getXhfMc() + "的详细信息" );
                    resultList.add(resultMap);
                    return null;

                } else {
			        querySpecialInvoiceReversal.setXhfDz(StringUtils.isBlank(querySpecialInvoiceReversal.getXhfDz()) ? queryBuyerByPurchaseName.getAddress() : querySpecialInvoiceReversal.getXhfDz());
			        querySpecialInvoiceReversal.setXhfDh(StringUtils.isBlank(querySpecialInvoiceReversal.getXhfDh()) ? queryBuyerByPurchaseName.getPhone() : querySpecialInvoiceReversal.getXhfDh());
			        querySpecialInvoiceReversal.setXhfYh(StringUtils.isBlank(querySpecialInvoiceReversal.getXhfYh()) ? queryBuyerByPurchaseName.getBankOfDeposit() : querySpecialInvoiceReversal.getXhfYh());
			        querySpecialInvoiceReversal.setXhfZh(StringUtils.isBlank(querySpecialInvoiceReversal.getXhfZh()) ? queryBuyerByPurchaseName.getBankNumber() : querySpecialInvoiceReversal.getXhfZh());
			
		        }

            }

        }

        //购方信息补全
        if(StringUtils.isBlank(querySpecialInvoiceReversal.getGhfDz()) || StringUtils.isBlank(querySpecialInvoiceReversal.getGhfDh())
                || StringUtils.isBlank(querySpecialInvoiceReversal.getGhfYh()) || StringUtils.isBlank(querySpecialInvoiceReversal.getGhfZh())) {
	        Map<String, DeptEntity> taxplayerEntityList = userInfoService.getTaxpayerEntityMap();
	        DeptEntity sysDeptEntity = taxplayerEntityList.get(querySpecialInvoiceReversal.getGhfNsrsbh());
	        if (sysDeptEntity != null) {
		        querySpecialInvoiceReversal.setGhfDz(StringUtils.isBlank(querySpecialInvoiceReversal.getGhfDz()) ? sysDeptEntity.getTaxpayerAddress() : querySpecialInvoiceReversal.getGhfDz());
		        querySpecialInvoiceReversal.setGhfDh(StringUtils.isBlank(querySpecialInvoiceReversal.getGhfDh()) ? sysDeptEntity.getTaxpayerPhone() : querySpecialInvoiceReversal.getGhfDh());
		        querySpecialInvoiceReversal.setGhfYh(StringUtils.isBlank(querySpecialInvoiceReversal.getGhfYh()) ? sysDeptEntity.getTaxpayerBank() : querySpecialInvoiceReversal.getGhfYh());
		        querySpecialInvoiceReversal.setGhfZh(StringUtils.isBlank(querySpecialInvoiceReversal.getGhfZh()) ? sysDeptEntity.getTaxpayerAccount() : querySpecialInvoiceReversal.getGhfZh());
		
	        } else {
		        BuyerEntity queryBuyerByPurchaseName = buyerService.queryBuyerByPurchaseName(querySpecialInvoiceReversal.getGhfMc(), querySpecialInvoiceReversal.getXhfNsrsbh());
		        if (queryBuyerByPurchaseName == null) {
			
			        Map<String, String> resultMap = new HashMap<>(5);
                    resultMap.put(OrderManagementConstant.CODE, "9999");
                    resultMap.put(OrderManagementConstant.MESSAGE, "信息表编号："+ querySpecialInvoiceReversal.getXxbbh() + "的申请单购方信息不全，请去客户信息中维护 "+ querySpecialInvoiceReversal.getGhfMc() + " 的详细信息" );
                    resultList.add(resultMap);
                    return null;
                }else{

                    querySpecialInvoiceReversal.setGhfDz(StringUtils.isBlank(querySpecialInvoiceReversal.getGhfDz()) ? queryBuyerByPurchaseName.getAddress() : querySpecialInvoiceReversal.getGhfDz());
                    querySpecialInvoiceReversal.setGhfDh(StringUtils.isBlank(querySpecialInvoiceReversal.getGhfDh()) ? queryBuyerByPurchaseName.getPhone() : querySpecialInvoiceReversal.getGhfDh());
                    querySpecialInvoiceReversal.setGhfYh(StringUtils.isBlank(querySpecialInvoiceReversal.getGhfYh()) ? queryBuyerByPurchaseName.getBankOfDeposit() : querySpecialInvoiceReversal.getGhfYh());
                    querySpecialInvoiceReversal.setGhfZh(StringUtils.isBlank(querySpecialInvoiceReversal.getGhfZh()) ? queryBuyerByPurchaseName.getBankNumber() : querySpecialInvoiceReversal.getGhfZh());

                }

            }

        }

        return querySpecialInvoiceReversal;

    }

    private Map<String, String> examinAndComplete(SpecialInvoiceReversalEntity querySpecialInvoiceReversal,
			String taxpayerCode, String operatorId) {
	
	    Map<String, String> resultMap = new HashMap<>(5);
	    resultMap.put(OrderManagementConstant.CODE, "0000");
	    resultMap.put(OrderManagementConstant.MESSAGE, "处理成功");
	
	    // 申请单审核状态校验
	    if (!OrderInfoEnum.SPECIAL_INVOICE_STATUS_TZD0000.getKey()
			    .equals(querySpecialInvoiceReversal.getStatusCode())) {
		    log.error("开具红字发票，红字申请单未审核通过，申请单号：{}", querySpecialInvoiceReversal.getSqdh());
		    resultMap.put(OrderManagementConstant.CODE, "9999");
		    resultMap.put(OrderManagementConstant.MESSAGE,
				    "红字申请单信息表编号为" + querySpecialInvoiceReversal.getSqdh() + "处于开票中状态");
			return resultMap;
		}

		// 开具状态校验
		if (OrderInfoEnum.SPECIAL_INVOICE_STATUS_1.getKey().equals(querySpecialInvoiceReversal.getKpzt())) {
			// 开票中
			log.error("开具中的发票，不允许重复提交开票申请,申请单号:{}", querySpecialInvoiceReversal.getSqdh());
			resultMap.put(OrderManagementConstant.CODE, "9999");
			resultMap.put(OrderManagementConstant.MESSAGE,
					"红字申请单信息表编号为" + querySpecialInvoiceReversal.getSqdh() + "处于开票中状态");
			return resultMap;
		} else if (OrderInfoEnum.SPECIAL_INVOICE_STATUS_2.getKey().equals(querySpecialInvoiceReversal.getKpzt())) {
			// 开具成功
			log.error("开具成功的发票，不允许重复提交开票申请,申请单号:{}", querySpecialInvoiceReversal.getSqdh());
			resultMap.put(OrderManagementConstant.CODE, "9999");
			resultMap.put(OrderManagementConstant.MESSAGE,
					"红字申请单信息表编号为" + querySpecialInvoiceReversal.getSqdh() + "已经开票成功，请不要重复提交");
			return resultMap;
		}

		/**
		 * 开票人信息校验
		 */
		if (StringUtils.isBlank(querySpecialInvoiceReversal.getKpr())) {
			DrawerInfoEntity drawerInfoEntity = invoiceService.queryDrawerInfo(taxpayerCode, operatorId);
			if (drawerInfoEntity != null && StringUtils.isNotBlank(drawerInfoEntity.getDrawerName())) {
				querySpecialInvoiceReversal.setKpr(drawerInfoEntity.getDrawerName());
				querySpecialInvoiceReversal.setFhr(StringUtils.isBlank(querySpecialInvoiceReversal.getFhr())
								? drawerInfoEntity.getReCheckName() : querySpecialInvoiceReversal.getFhr());
				querySpecialInvoiceReversal.setSkr(StringUtils.isBlank(querySpecialInvoiceReversal.getSkr())
						? drawerInfoEntity.getNameOfPayee() : querySpecialInvoiceReversal.getSkr());
			} else {
				resultMap.put(OrderManagementConstant.CODE, "9999");
				resultMap.put(OrderManagementConstant.MESSAGE,
						"红字申请单信息表编号为" + querySpecialInvoiceReversal.getXxbbh() + "的红字申请单开票人不能为空");
				return resultMap;
			}

		}
		return resultMap;
	}
	
	
	private OrderInfo buildOrderInfo(SpecialInvoiceReversalEntity querySpecialInvoiceReversal) {
		String qdBz = OrderInfoEnum.QDBZ_CODE_0.getKey();
		if (StringUtils.isNotBlank(querySpecialInvoiceReversal.getType())
				&& !ConfigureConstant.STRING_0.equals(querySpecialInvoiceReversal.getType())) {
			qdBz = OrderInfoEnum.QDBZ_CODE_4.getKey();
		}
		String ddh = RandomUtil.randomNumbers(12);
		OrderInfo orderInfo = new OrderInfo();
		orderInfo.setId("");
		orderInfo.setProcessId("");
		orderInfo.setFpqqlsh("");
		orderInfo.setDdh(ddh);
		orderInfo.setThdh(ddh);
		orderInfo.setDdlx(OrderInfoEnum.ORDER_TYPE_3.getKey());
		orderInfo.setDsptbm("");
		orderInfo.setNsrsbh(querySpecialInvoiceReversal.getXhfNsrsbh());
		orderInfo.setNsrmc(querySpecialInvoiceReversal.getXhfMc());
		orderInfo.setNsrdzdah("");
		orderInfo.setSwjgDm("");
		orderInfo.setDkbz(OrderInfoEnum.DKBZ_0.getKey());
		orderInfo.setPydm("");
		orderInfo.setKpxm("");
		orderInfo.setBbmBbh(SystemConfig.bmbbbh);
		orderInfo.setXhfMc(querySpecialInvoiceReversal.getXhfMc());
		orderInfo.setXhfNsrsbh(querySpecialInvoiceReversal.getXhfNsrsbh());
		orderInfo.setXhfDz(querySpecialInvoiceReversal.getXhfDz());
		orderInfo.setXhfDh(querySpecialInvoiceReversal.getXhfDh());
		orderInfo.setXhfYh(querySpecialInvoiceReversal.getXhfYh());
		orderInfo.setXhfZh(querySpecialInvoiceReversal.getXhfZh());
		orderInfo.setGhfQylx(querySpecialInvoiceReversal.getGhfqylx());
		orderInfo.setGhfSf("");
		orderInfo.setGhfId("");
		orderInfo.setGhfMc(querySpecialInvoiceReversal.getGhfMc());
		orderInfo.setGhfNsrsbh(querySpecialInvoiceReversal.getGhfNsrsbh());
		orderInfo.setGhfDz(querySpecialInvoiceReversal.getGhfDz());
		orderInfo.setGhfDh(querySpecialInvoiceReversal.getGhfDh());
		orderInfo.setGhfYh(querySpecialInvoiceReversal.getGhfYh());
		orderInfo.setGhfZh(querySpecialInvoiceReversal.getGhfZh());
		orderInfo.setGhfSj("");
		orderInfo.setGhfEmail("");
		orderInfo.setHyDm("");
		orderInfo.setHyMc("");
		orderInfo.setKpr(querySpecialInvoiceReversal.getKpr());
		orderInfo.setSkr(querySpecialInvoiceReversal.getSkr());
		orderInfo.setFhr(querySpecialInvoiceReversal.getFhr());
		orderInfo.setDdrq(new Date());
		orderInfo.setKplx(OrderInfoEnum.INVOICE_BILLING_TYPE_1.getKey());
		orderInfo.setFpzlDm(querySpecialInvoiceReversal.getFpzlDm());
		orderInfo.setYfpDm(querySpecialInvoiceReversal.getYfpDm());
		orderInfo.setYfpHm(querySpecialInvoiceReversal.getYfpHm());
		orderInfo.setChyy(getReason(querySpecialInvoiceReversal.getSqsm()));
		orderInfo.setTschbz(OrderInfoEnum.TSCHBZ_0.getKey());
		/**
		 * 操作代码10正票正常开具 11正票错票重开 20 退货折让红票、21 错票重开红票、22换票冲红
		 */
		orderInfo.setCzdm(ConfigureConstant.STRING_10);
		orderInfo.setQdBz(qdBz);
		orderInfo.setQdXmmc("");
		orderInfo.setKphjje(querySpecialInvoiceReversal.getKphjje());
		orderInfo.setHjbhsje(querySpecialInvoiceReversal.getHjbhsje());
		orderInfo.setHjse(querySpecialInvoiceReversal.getHjse());
		orderInfo.setMdh("");
		orderInfo.setYwlx("");
		orderInfo.setYwlxId("");
		orderInfo.setBz(ConfigureConstant.STRING_HZBZ + querySpecialInvoiceReversal.getXxbbh());
		orderInfo.setKpjh("");
		orderInfo.setSld("");
		orderInfo.setSldMc("");
		orderInfo.setTqm("");
		orderInfo.setStatus(OrderInfoEnum.ORDER_STATUS_0.getKey());
		orderInfo.setByzd1("");
		orderInfo.setByzd2("");
		orderInfo.setByzd3("");
		orderInfo.setByzd4("");
		orderInfo.setByzd5("");
		orderInfo.setCreateTime(new Date());
		orderInfo.setUpdateTime(new Date());
		
		return orderInfo;
	}
	
	private List<OrderItemInfo> buildOrderItemInfo(
			List<SpecialInvoiceReversalItem> querySpecialInvoiceReversalItems, String xhfNsrsbh) {
		List<OrderItemInfo> orderItemList = new ArrayList<>();
		
		for (SpecialInvoiceReversalItem item : querySpecialInvoiceReversalItems) {
			OrderItemInfo orderItemInfo = new OrderItemInfo();
			String itemName = item.getXmmc();
			String fphxz = "0";
			if (itemName.equals(ConfigureConstant.XJZSXHQD)) {
				fphxz = "6";
			}
			// 发票行性质 0正常商品行 1折扣行 2被折扣行 6 清单红字发票
			orderItemInfo.setFphxz(fphxz);
			
			// 规格型号
			orderItemInfo.setGgxh(item.getGgxh());
			orderItemInfo.setHsbz(item.getHsbz());
			// 含税标志
			orderItemInfo.setSe(item.getSe());
			orderItemInfo.setSl(item.getSl());
			orderItemInfo.setSpbm(item.getSpbm());
			orderItemInfo.setSphxh(item.getSphxh());
			orderItemInfo.setXmdj(item.getXmdj());
			orderItemInfo.setXmdw(item.getXmdw());
			orderItemInfo.setXmje(item.getXmje());
			orderItemInfo.setXmmc(itemName);
			orderItemInfo.setXmsl(item.getXmsl());
			orderItemInfo.setYhzcbs(item.getYhzcbs());
			orderItemInfo.setKce(item.getKce());
			orderItemInfo.setZzstsgl(item.getZzstsgl());
			orderItemInfo.setXhfNsrsbh(xhfNsrsbh);
			orderItemList.add(orderItemInfo);
		}
		return orderItemList;
	}


	private void pushRespData(JSONArray dataArray, String statusCode, String errorMsg, Map<String, String> extParams) {
		JSONObject respData = new JSONObject();
		respData.put("errorCode", statusCode);
		respData.put("errorMsg", errorMsg);
		if (MapUtils.isNotEmpty(extParams)) {
			respData.putAll(extParams);
		}
		dataArray.add(respData);
	}

	private String getReason(String reasonCode) {
		String result = "";

		switch (reasonCode) {
		case "1100000000":
			result = "已抵扣";
			break;
		case "1010000000":
			result = "未抵扣";
			break;
		case "0000000100":
			result = "销售方申请";
			break;
		default:
			throw new IllegalStateException("Unexpected value: " + reasonCode);
		}
		return result;
	}
	
	/**
	 * 红字发票开具
	 *
	 * @throws OrderSeparationException
	 */
	public R addInvoice(CommonOrderInfo orderInfo, String uid, String deptId) throws OrderSeparationException {
		R vo = new R();
		String sld = orderInfo.getOrderInfo().getSld();
		String sldMc = orderInfo.getOrderInfo().getSldMc();
		// 冲红生成待开单据
		R excuSingle = generateReadyOpenOrderService.reshRed(orderInfo, uid, deptId);
		if (!excuSingle.get(OrderManagementConstant.CODE).equals(OrderInfoContentEnum.SUCCESS.getKey())) {
			return R.error().put(OrderManagementConstant.CODE, excuSingle.get(OrderManagementConstant.CODE))
					.put(OrderManagementConstant.MESSAGE, excuSingle.get(OrderManagementConstant.MESSAGE));
		}
		Map<String, PageSld> sldMap = new HashMap<>(2);
		PageSld pageSld = new PageSld();
		pageSld.setSldid(sld);
		pageSld.setSldmc(sldMc);
		sldMap.put("_" + orderInfo.getOrderInfo().getFpzlDm(), pageSld);
		// 调用开票接口
		R r = makeOutAnInvoiceService
				.makeOutAnInovice((List<CommonOrderInfo>) excuSingle.get(OrderManagementConstant.DATA), sldMap);
		if (OrderInfoContentEnum.SUCCESS.getKey().equals(r.get(OrderManagementConstant.CODE))) {
			return vo.put(OrderManagementConstant.CODE, OrderInfoContentEnum.SUCCESS.getKey()).put("msg",
					OrderInfoContentEnum.SUCCESS.getMessage());
		} else {
			return vo.put(OrderManagementConstant.CODE, OrderInfoContentEnum.RECEIVE_FAILD.getKey()).put("msg",
					r.get("msg"));
		}
		
	}

	@Override
	public Map<String,String> submitSpecialInvoiceReversal(SpecialInvoiceReversalEntity specialInvoiceReversal,
			List<SpecialInvoiceReversalItem> specialInvoiceReversalItems) {
		
		log.info("红字申请单上传:specialInvoice:{},itemInfo:{}", JsonUtils.getInstance().toJsonString(specialInvoiceReversal),
				JsonUtils.getInstance().toJsonString(specialInvoiceReversalItems));
		
		Map<String, String> errorMsg = new HashMap<>(2);
		errorMsg.put(OrderManagementConstant.CODE, OrderInfoContentEnum.SUCCESS.getKey());
		errorMsg.put(OrderManagementConstant.MESSAGE, OrderInfoContentEnum.SUCCESS.getMessage());
		
		/**
		 * 根据税号查询税控设备
		 */
		
		// 如果是购方申请以购方税号作为查询条件
		String nsrsbh = "";
		if (OrderInfoEnum.SPECIAL_INVOICE_REASON_1100000000.getKey().equals(specialInvoiceReversal.getSqsm())
				|| OrderInfoEnum.SPECIAL_INVOICE_REASON_1010000000.getKey().equals(specialInvoiceReversal.getSqsm())) {
			nsrsbh = specialInvoiceReversal.getGhfNsrsbh();
		} else {
			nsrsbh = specialInvoiceReversal.getNsrsbh();
		}
		String terminalCode = apiTaxEquipmentService.getTerminalCode(nsrsbh);
		
		
		HzfpsqbsReq hzfpsqbsReq = new HzfpsqbsReq();
		try {
			HzfpsqbscBatch hzfpsqbscBatch = new HzfpsqbscBatch();
			hzfpsqbscBatch.setSQBSCQQPCH(specialInvoiceReversal.getSqdscqqpch() == null ? specialInvoiceReversal.getSqdscqqlsh() : specialInvoiceReversal.getSqdscqqpch());
			hzfpsqbscBatch.setSLDID(StringUtils.isNotBlank(specialInvoiceReversal.getSld())
					? specialInvoiceReversal.getSld() : "");
			hzfpsqbscBatch.setKPJH(StringUtils.isNotBlank(specialInvoiceReversal.getFjh())
					? specialInvoiceReversal.getFjh() : "");
			hzfpsqbscBatch.setFPLX(ConfigureConstant.STRING_1);
			hzfpsqbscBatch.setFPLB(OrderInfoEnum.ORDER_INVOICE_TYPE_0.getKey());
			
			// 截取红字申请单的第一位作为申请单的类别 1：购方申请 0 销方申请
			String callType = specialInvoiceReversal.getSqsm().substring(0, 1);
			hzfpsqbscBatch.setSQLB(callType);
			
			hzfpsqbscBatch.setNSRSBH(ConfigureConstant.STRING_1.equals(callType)
					? specialInvoiceReversal.getGhfNsrsbh() : specialInvoiceReversal.getXhfNsrsbh());
			
			hzfpsqbsReq.setHZFPSQBSCSBATCH(hzfpsqbscBatch);
			List<String> shList = new ArrayList<>();
			shList.add(hzfpsqbscBatch.getNSRSBH());
			Hzfpsqbsc hzfpsqbsc = new Hzfpsqbsc();
			
			HzfpsqbsHead hzfpsqbsHead = new HzfpsqbsHead();
			hzfpsqbsHead.setSQBSCQQLSH(specialInvoiceReversal.getSqdscqqlsh());
			hzfpsqbsHead.setYFP_DM(specialInvoiceReversal.getYfpDm());
			hzfpsqbsHead.setYFP_HM(specialInvoiceReversal.getYfpHm());
			
			// 根据申请单类型 判断是否需要传原发票开票日期 购方已抵扣的发票不需要传原发票开票日期
			hzfpsqbsHead.setYFP_KPRQ(specialInvoiceReversal.getYfpKprq() == null ? ""
					: DateUtil.format(specialInvoiceReversal.getYfpKprq(),"yyyy-MM-dd HH:mm:ss"));

			hzfpsqbsHead.setXSF_NSRSBH(specialInvoiceReversal.getXhfNsrsbh());
			hzfpsqbsHead.setXSF_MC(specialInvoiceReversal.getXhfMc());
			hzfpsqbsHead.setGMF_NSRSBH(specialInvoiceReversal.getGhfNsrsbh());
			hzfpsqbsHead.setGMF_MC(specialInvoiceReversal.getGhfMc());
			hzfpsqbsHead.setHJJE(specialInvoiceReversal.getHjbhsje());
			hzfpsqbsHead.setHJSE(specialInvoiceReversal.getHjse());
			hzfpsqbsHead.setSQSM(specialInvoiceReversal.getSqsm());
			hzfpsqbsHead.setBMB_BBH("1.0");
			hzfpsqbsHead.setXXBLX(ConfigureConstant.STRING_0);
			hzfpsqbsHead.setTKSJ(DateUtils.getYYYYMMDDHHMMSSFormatStr(new Date()));

			//营业税标志 TODO 红字申请单生成的时候判断
			//hzfpsqbsHead.setYYSBZ(specialInvoiceReversal.getYysbz());
            //hzfpsqbsHead.setYYSBZ("0000000000"specialInvoiceReversal);
			hzfpsqbsHead.setYYSBZ(StringUtils.isBlank(specialInvoiceReversal.getYysbz()) ? OrderInfoEnum.SPECIAL_YYSBZ_0000000000.getKey() : specialInvoiceReversal.getYysbz());

			hzfpsqbsc.setHZFPSQBSCHEAD(hzfpsqbsHead);
            HzfpsqbsDetail[] hzfpsqbsDetails = new HzfpsqbsDetail[specialInvoiceReversalItems.size()];
			for (int i = 0; i < specialInvoiceReversalItems.size(); i++) {
				SpecialInvoiceReversalItem item = specialInvoiceReversalItems.get(i);
				
				HzfpsqbsDetail hzfpsqbsDetail = new HzfpsqbsDetail();
				hzfpsqbsDetail.setXMXH(item.getSphxh());
				String fphxz = "0";
				if (ConfigureConstant.XJZSXHQD.equals(item.getXmmc())
						&& specialInvoiceReversalItems.size() == 1) {
					fphxz = "6";
				}
				hzfpsqbsDetail.setFPHXZ(fphxz);
				hzfpsqbsDetail.setSPBM(StringUtils.isNotBlank(item.getSpbm()) ? item.getSpbm() : "");
				hzfpsqbsDetail.setZXBM("");
				hzfpsqbsDetail.setYHZCBS(item.getYhzcbs());
				hzfpsqbsDetail.setLSLBS(item.getLslbs());
				hzfpsqbsDetail.setZZSTSGL(item.getZzstsgl());
				hzfpsqbsDetail.setXMMC(item.getXmmc());
				hzfpsqbsDetail.setGGXH(StringUtils.isNotBlank(item.getGgxh()) ? item.getGgxh() : "");
				hzfpsqbsDetail.setDW(StringUtils.isNotBlank(item.getXmdw()) ? item.getXmdw() : "");
				hzfpsqbsDetail.setXMSL(StringUtils.isNotBlank(item.getXmsl()) ? item.getXmsl() : "");
				hzfpsqbsDetail.setXMDJ(StringUtils.isNotBlank(item.getXmdj()) ? item.getXmdj() : "");
				hzfpsqbsDetail.setXMJE(item.getXmje());
				hzfpsqbsDetail.setHSBZ(item.getHsbz());
				hzfpsqbsDetail.setSL(item.getSl());
				hzfpsqbsDetail.setSE(StringUtils.isNotBlank(item.getSe()) ? item.getSe() : "0.00");
				hzfpsqbsDetails[i] = hzfpsqbsDetail;
			}
			hzfpsqbsc.setHZFPSQBSCDETAILIST(hzfpsqbsDetails);
			
			Hzfpsqbsc[] reqForms = {hzfpsqbsc};
			
			hzfpsqbsReq.setHZFPSQBSCLIST(reqForms);
			
			
			/**
			 * 方格红字申请单逻辑添加 update By ysy 2020-07-13
			 */
			if (OrderInfoEnum.TAX_EQUIPMENT_FGBW.getKey().equals(terminalCode) || OrderInfoEnum.TAX_EQUIPMENT_FGHX.getKey().equals(terminalCode)
					|| OrderInfoEnum.TAX_EQUIPMENT_FGUKEY.getKey().equals(terminalCode)) {
				//方格接口设置状态为待上传
				specialInvoiceReversal.setScfgStatus(ConfigureConstant.STRING_2);
				//更新数据为待上传
				specialInvoiceReversal.setStatusCode(OrderInfoContentEnum.CHECK_ISS7PRI_TZD0500.getKey());
				specialInvoiceReversal.setStatusMessage(OrderInfoContentEnum.CHECK_ISS7PRI_TZD0500.getMessage());
				//redis获取里面获取注册的税盘信息
				String registCodeStr = apiFangGeInterfaceService.getRegistCodeByRedis(specialInvoiceReversal.getXhfNsrsbh(), specialInvoiceReversal.getSld());
				if (StringUtils.isNotEmpty(registCodeStr)) {
					RegistrationCode registCode = JsonUtils.getInstance().parseObject(registCodeStr, RegistrationCode.class);
					/**
					 * 存放上传信息到redis队列
					 */
					PushPayload pushPayload = new PushPayload();
					//发票上传税局
					pushPayload.setINTERFACETYPE(ConfigureConstant.STRING_2);
					pushPayload.setNSRSBH(registCode.getXhfNsrsbh());
					pushPayload.setJQBH(registCode.getJqbh());
					pushPayload.setZCM(registCode.getZcm());
					pushPayload.setSQBSCQQPCH(specialInvoiceReversal.getSqdscqqpch());
					apiFangGeInterfaceService.saveMqttToRedis(pushPayload);
				}

				boolean isEditSuccess = editSpecialInvoiceReversal(specialInvoiceReversal.getId(), "", specialInvoiceReversal.getStatusCode(),
						specialInvoiceReversal.getStatusMessage(), specialInvoiceReversal.getEditorId(), specialInvoiceReversal.getEditorName(),specialInvoiceReversal.getScfgStatus());
				if(!isEditSuccess){
					log.error("红字生清单上传更新数据库状态失败,批次号:{}",specialInvoiceReversal.getSqdscqqpch());
				}

			}else {
				
				
				HpUploadResponse hpUploadResponse = HttpInvoiceRequestUtil.redInvoiceUpload(OpenApiConfig.redInvoiceUpload, hzfpsqbsReq, terminalCode);
				String statusCode = hpUploadResponse.getResult().getStatusCode();
				String statusMsg = hpUploadResponse.getResult().getStatusMessage();
				
				if (ConfigureConstant.STRING_0000.equals(statusCode)) {
					
					String xxbbh = "";
					String sqdh = hzfpsqbsHead.getSQBSCQQLSH();
					List<HpUploadResponseHzfpsqbsc> responseHzfpsqbsc = hpUploadResponse.getResult().getResponse_HZFPSQBSC();
					if (!"TZD0000".equals(responseHzfpsqbsc.get(0).getSTATUS_CODE())) {
						log.error("红字申请单上报税局，返回未审核通过状态！申请单编号：{}", specialInvoiceReversal.getId());
					} else {
						xxbbh = responseHzfpsqbsc.get(0).getXXBBH();
						sqdh = responseHzfpsqbsc.get(0).getSQDH();
					}
					
					
					SpecialInvoiceReversalEntity udpateSpecialInvoice = new SpecialInvoiceReversalEntity();
					udpateSpecialInvoice.setId(specialInvoiceReversal.getId());
					udpateSpecialInvoice.setXxbbh(xxbbh);
					udpateSpecialInvoice.setSqdh(sqdh);
					udpateSpecialInvoice.setStatusCode(responseHzfpsqbsc.get(0).getSTATUS_CODE());
					udpateSpecialInvoice.setStatusMessage(responseHzfpsqbsc.get(0).getSTATUS_MESSAGE());
					udpateSpecialInvoice.setEditorId(specialInvoiceReversal.getEditorId());
					udpateSpecialInvoice.setEditorName(specialInvoiceReversal.getEditorName());
					udpateSpecialInvoice.setUpdateTime(new Date());
					
					int editResult = apiSpecialInvoiceReversalService.updateSpecialInvoiceReversal(udpateSpecialInvoice);
					
					if (editResult <= 0) {
						log.error("红字申请单上报税局，更新申请单信息失败！申请单编号：{}", specialInvoiceReversal.getId());
						Map<String, String> extParams = new HashMap<>(5);
						extParams.put("id", specialInvoiceReversal.getId());
						errorMsg = pushRespData(ResponseStatusCodes.FAIL, "更新申请单信息失败！", extParams);
					}
				} else {
					log.error("红字申请单上报税局，更新申请单信息失败！申请单编号：{}",specialInvoiceReversal.getId());
					Map<String, String> extParams = new HashMap<>(5);
					extParams.put("id", specialInvoiceReversal.getId());
					if (StringUtils.isBlank(statusCode)) {
						statusCode = ResponseStatusCodes.RESPONSE_IS_NULL;
						statusMsg = "上传失败";
					}
					errorMsg = pushRespData(statusCode, statusMsg, extParams);
				}
			}
		} catch (Exception e) {
			log.error("红字申请单上报税局异常", e);
			Map<String, String> extParams = new HashMap<>(5);
			extParams.put("id", specialInvoiceReversal.getId());
			errorMsg = pushRespData(ResponseStatusCodes.ERROR, "红字申请单上报税局，处理异常", extParams);
		}
		return errorMsg;
	}

	private Boolean editSpecialInvoiceReversal(String id, String submitCode, String submitStatus,
			String submitStatusDesc, String editorId, String editorName,String scfgStatus) {
		SpecialInvoiceReversalEntity specialInvoiceReversal = new SpecialInvoiceReversalEntity();
		specialInvoiceReversal.setId(id);
		specialInvoiceReversal.setXxbbh(submitCode);
		specialInvoiceReversal.setStatusCode(submitStatus);
		specialInvoiceReversal.setStatusMessage(submitStatusDesc);
		specialInvoiceReversal.setEditorId(editorId);
		specialInvoiceReversal.setEditorName(editorName);
		if(!StringUtils.isBlank(scfgStatus)){
			specialInvoiceReversal.setScfgStatus(scfgStatus);
		}
		return editSpecialInvoiceReversal(specialInvoiceReversal);
	}

	public Boolean editSpecialInvoiceReversal(SpecialInvoiceReversalEntity specialInvoiceReversal) {
		Boolean isSuccess = false;

		specialInvoiceReversal.setUpdateTime(DateUtils.getDate(new Date(), DateUtils.DATE_TIME_PATTERN));
		int editResult = apiSpecialInvoiceReversalService.updateSpecialInvoiceReversal(specialInvoiceReversal);
		if (editResult > 0) {
			isSuccess = true;
		}
		return isSuccess;
	}

	private Map<String,String> pushRespData(String statusCode, String errorMsg, Map<String, String> extParams) {
		
		Map<String, String> result = new HashMap<>(5);
		result.put(OrderManagementConstant.CODE, statusCode);
		result.put(OrderManagementConstant.MESSAGE, errorMsg);
		result.putAll(extParams);
		return result;
	}

	@Override
	public JSONArray syncSpecialInvoiceReversal(String code, String taxpayerCode, String invoiceType,
			String invoiceCategory, String operatorId, String operatorName) {
		JSONArray errorMsgArray = new JSONArray();

		/**
		 * 查询税控设备
		 */
		String terminalCode = apiTaxEquipmentService.getTerminalCode(taxpayerCode);

		for (int i = 1; i < 1000; i++) {
			HpInvocieRequest reqData = new HpInvocieRequest();
			reqData.setSQBXZQQPCH(code);
			reqData.setNSRSBH(taxpayerCode);
			reqData.setFPLX(invoiceType);
			reqData.setFPLB(invoiceCategory);
			reqData.setXXBFW(ConfigureConstant.STRING_0);
			Calendar currentCalendar = Calendar.getInstance();
			currentCalendar.add(Calendar.DATE, -5);
			reqData.setTKRQ_Q(DateUtils.getYYYYMMDDFormatStr(currentCalendar.getTime()));
			reqData.setTKRQ_Z(DateUtils.getYYYYMMDDFormatStr(new Date()));
			reqData.setPageNo(String.valueOf(i));
			reqData.setPageSize("10");
			if (!OrderInfoEnum.TAX_EQUIPMENT_C48.getKey().equals(terminalCode)) {
				reqData.setXSF_NSRSBH(taxpayerCode);
			}
			
			/**
			 * 税控盘托管和百望服务器不进行下载操作
			 */
			if (OrderInfoEnum.TAX_EQUIPMENT_BW_ACTIVEX.getKey().equals(terminalCode)) {
				continue;
			}
			HpResponseBean respData = null;
			try {
				respData = HttpInvoiceRequestUtil.redInvoiceDown(OpenApiConfig.redInvoiceDown, reqData, terminalCode);
				
				if (null != respData) {
					String statusCode = respData.getResult().getStatusCode();
					if (ConfigureConstant.STRING_0000.equals(statusCode)) {
						if (ObjectUtil.isNull(respData.getResult().getRedinvreqbillxx()) || respData.getResult().getRedinvreqbillxx().size() <= 0) {
							break;
						}
						for (int j = 0; j < respData.getResult().getRedinvreqbillxx().size(); j++) {
							
							ResponseHzfpsqbsc responseHzfpsqbsc = respData.getResult().getRedinvreqbillxx().get(j);
							/**
							 * 同步申请单号相当于申请单请求流水号
							 */
							String sqdh = responseHzfpsqbsc.getSqdh();
							if (StringUtils.isBlank(sqdh)) {
								continue;
							}
							SpecialInvoiceReversalEntity specialInvoiceReversal = apiSpecialInvoiceReversalService
									.selectSpecialInvoiceReversalBySqdqqlsh(sqdh);
							if (null != specialInvoiceReversal) {
								if (!"TZD0000".equals(specialInvoiceReversal.getStatusCode())) {
									String submitCode = "";
									String submitStatus = responseHzfpsqbsc.getStatus_CODE();
									if (!"TZD0061".equals(submitStatus)) {
										if (!"TZD0500".equals(submitStatus)) {
											submitCode = responseHzfpsqbsc.getXxbbh();
											boolean isSuccess = editSpecialInvoiceReversal(
													specialInvoiceReversal.getId(), submitCode, submitStatus,
													responseHzfpsqbsc.getStatus_MESSAGE(), operatorId, operatorName,null);
											if (!isSuccess) {
												log.error("红字申请单同步，编辑失败！申请单编号：" + code + "，请求报文："
														+ JsonUtils.getInstance().toJsonString(reqData) + "，响应报文："
														+ JsonUtils.getInstance().toJsonString(respData));
												
												pushRespData(errorMsgArray, ResponseStatusCodes.FAIL,
														"红字申请单同步，编辑失败", null);
												continue;
											}
										} else {
											log.error("红字申请单同步，返回未上传状态！申请单编号：" + code + "，请求报文："
													+ JsonUtils.getInstance().toJsonString(reqData) + "，响应报文："
													+ JsonUtils.getInstance().toJsonString(respData));
											
											pushRespData(errorMsgArray, ResponseStatusCodes.FAIL, "红字申请单同步，返回未上传状态",
													null);
											continue;
										}
									} else {
										// 重复上传，继续同步
										continue;
									}
								}
							} else { // 此接口只能是纸票情况才会有
								specialInvoiceReversal = new SpecialInvoiceReversalEntity();
								String type = "0";
								if ("0000000090".equals(responseHzfpsqbsc.getYysbz())) {
									type = "3";
								}
								specialInvoiceReversal.setType(type);
								specialInvoiceReversal.setSqdh(sqdh);
								specialInvoiceReversal.setSqsm(convertReason(responseHzfpsqbsc.getSqsm()));
								specialInvoiceReversal.setYfpDm(("0000000000".equals(responseHzfpsqbsc.getYfp_DM()) ? ""
												: responseHzfpsqbsc.getYfp_DM()));
								specialInvoiceReversal.setYfpHm(("00000000".equals(responseHzfpsqbsc.getYfp_HM())
										? "" : responseHzfpsqbsc.getYfp_HM()));
								specialInvoiceReversal.setInvoiceType(responseHzfpsqbsc.getFplx());
								specialInvoiceReversal.setFpzlDm(responseHzfpsqbsc.getFplb());
								specialInvoiceReversal.setTksj(DateUtils.stringToDate(responseHzfpsqbsc.getTksj(),
																DateUtils.DATE_TIME_PATTERN));
								specialInvoiceReversal.setXhfMc(responseHzfpsqbsc.getXsf_MC());
								specialInvoiceReversal.setXhfNsrsbh(responseHzfpsqbsc.getXsf_NSRSBH());
								specialInvoiceReversal.setGhfMc(responseHzfpsqbsc.getGmf_MC());
								specialInvoiceReversal.setGhfNsrsbh(responseHzfpsqbsc.getGmf_NSRSBH());
								specialInvoiceReversal.setGhfqylx("01");
								specialInvoiceReversal.setHjbhsje(responseHzfpsqbsc.getHjje());
								specialInvoiceReversal.setHjse(responseHzfpsqbsc.getHjse());
								specialInvoiceReversal.setKphjje(new BigDecimal(responseHzfpsqbsc.getHjje())
										.add(new BigDecimal(responseHzfpsqbsc.getHjse())).toString());
								specialInvoiceReversal.setXxbbh(responseHzfpsqbsc.getXxbbh());
								specialInvoiceReversal.setStatusCode(responseHzfpsqbsc.getStatus_CODE());
								specialInvoiceReversal.setStatusMessage(responseHzfpsqbsc.getStatus_MESSAGE());
								specialInvoiceReversal.setNsrsbh(taxpayerCode);
								specialInvoiceReversal.setCreatorId(operatorId);
								specialInvoiceReversal.setCreatorName(operatorName);
								specialInvoiceReversal.setEditorId(operatorId);
								specialInvoiceReversal.setEditorName(operatorName);
								boolean isSuccess = addSpecialInvoiceReversal(specialInvoiceReversal);
								if (isSuccess) {
									String specialInvoiceReversalId = specialInvoiceReversal.getId();
									String taxRate = "";
									List<Commoninvdetail> invoiceDetails = responseHzfpsqbsc.getCommoninvdetails();
									for (int k = 0; k < invoiceDetails.size(); k++) {
										Commoninvdetail invoiceDetail = invoiceDetails.get(k);

										SpecialInvoiceReversalItem specialInvoiceReversalItem = new SpecialInvoiceReversalItem();
										specialInvoiceReversalItem
												.setSpecialInvoiceReversalId(specialInvoiceReversalId);
										specialInvoiceReversalItem.setSpbm(invoiceDetail.getSpbm());
										specialInvoiceReversalItem.setXmmc(invoiceDetail.getXmmc());
										specialInvoiceReversalItem.setGgxh(invoiceDetail.getGgxh());
										specialInvoiceReversalItem.setXmdw(invoiceDetail.getDw());
										/**
										 * 申请单下载判断单价数量和税率
										 */
										if (StringUtils.isNotBlank(invoiceDetail.getSl()) && "0.000000".equals(invoiceDetail.getSl())) {
											specialInvoiceReversalItem.setXmsl("");
											specialInvoiceReversalItem.setXmdj("");
											specialInvoiceReversalItem.setSl("");
										} else {
											specialInvoiceReversalItem.setXmdj(String.valueOf(invoiceDetail.getXmdj()));
											specialInvoiceReversalItem.setXmje(String.valueOf(invoiceDetail.getXmje()));
											specialInvoiceReversalItem.setSl(String.valueOf(invoiceDetail.getSl()));
										}
										specialInvoiceReversalItem.setXmsl(String.valueOf(invoiceDetail.getXmsl()));
										specialInvoiceReversalItem.setSe(String.valueOf(invoiceDetail.getSe()));
										specialInvoiceReversalItem.setHsbz(invoiceDetail.getHsbz());
										specialInvoiceReversalItem.setYhzcbs(StringUtils.isNotBlank(invoiceDetail.getYhzcbs())
														? invoiceDetail.getYhzcbs() : OrderInfoEnum.YHZCBS_0.getKey());
										specialInvoiceReversalItem.setLslbs(StringUtils.isNotBlank(invoiceDetail.getLslbs())
														? invoiceDetail.getLslbs() : "");
										specialInvoiceReversalItem.setSphxh(String.valueOf(k + 1));

										isSuccess = isSuccess & addSpecialInvoiceReversalItem(specialInvoiceReversalItem);
										if (!isSuccess) {
											log.error("红字申请单同步，保存红字申请单明细信息失败！申请单编号：" + sqdh + "，请求报文："
													+ JsonUtils.getInstance().toJsonString(reqData) + "，响应报文："
													+ JsonUtils.getInstance().toJsonString(respData));
											
											pushRespData(errorMsgArray, ResponseStatusCodes.FAIL,
													"红字申请单同步，保存红字申请单明细信息失败", null);
											break;
										}
										if (k == 0) {
											taxRate = String.valueOf(invoiceDetail.getSl());
										} else if (!taxRate.equals(invoiceDetail.getSl())) {
											taxRate = "多税率";
										}
									}
									if (isSuccess) {
										specialInvoiceReversal = new SpecialInvoiceReversalEntity();
										specialInvoiceReversal.setId(specialInvoiceReversalId);
										specialInvoiceReversal.setDslbz(taxRate);
										isSuccess = editSpecialInvoiceReversal(specialInvoiceReversal);
										if (!isSuccess) {
											log.error("红字申请单同步，编辑红字申请单信息失败！申请单编号：" + sqdh + "，请求报文："
													+ JsonUtils.getInstance().toJsonString(reqData) + "，响应报文："
													+ JsonUtils.getInstance().toJsonString(respData));
											
											pushRespData(errorMsgArray, ResponseStatusCodes.FAIL,
													"红字申请单同步，编辑红字申请单信息失败", null);
											continue;
										}
									} else {
										log.error("红字申请单同步，保存红字申请单明细信息失败！申请单编号：" + sqdh + "，请求报文："
												+ JsonUtils.getInstance().toJsonString(reqData) + "，响应报文："
												+ JsonUtils.getInstance().toJsonString(respData));
										
										pushRespData(errorMsgArray, ResponseStatusCodes.FAIL,
												"红字申请单同步，保存红字申请单明细信息失败", null);
										
										boolean isDeleteSuccess = removeSpecialInvoiceReversal(
												specialInvoiceReversalId);
										if (isDeleteSuccess) {
											log.error("红字申请单同步，级联删除红字申请单失败！申请单编号：" + sqdh + "，请求报文："
													+ JsonUtils.getInstance().toJsonString(reqData) + "，响应报文："
													+ JsonUtils.getInstance().toJsonString(respData));
											
											pushRespData(errorMsgArray, ResponseStatusCodes.FAIL,
													"红字申请单同步，级联删除红字申请单失败", null);
										}
										continue;
									}
								} else {
									log.error("红字申请单同步，保存红字申请单失败！申请单编号：" + sqdh + "，请求报文："
											+ JsonUtils.getInstance().toJsonString(reqData) + "，响应报文：" + JsonUtils.getInstance().toJsonString(respData));
									
									pushRespData(errorMsgArray, ResponseStatusCodes.FAIL, "红字申请单同步，保存红字申请单失败",
											null);
									continue;
								}
							}
						}
						
					} else {
						log.error("红字申请单同步，同步失败！申请单编号：" + code + "，请求报文：" + JsonUtils.getInstance().toJsonString(reqData) + "，响应报文："
								+ JsonUtils.getInstance().toJsonString(respData));
						
						pushRespData(errorMsgArray, statusCode, respData.getResult().getStatusCode(), null);
						break;
					}
				} else {
					log.error("红字申请单同步，底层响应内容为空！申请单编号：" + code + "，请求报文：" + JsonUtils.getInstance().toJsonString(reqData));
					
					pushRespData(errorMsgArray, ResponseStatusCodes.RESPONSE_IS_NULL, "红字申请单同步，底层响应内容为空", null);
					break;
				}
			} catch (Exception e) {
				log.error("红字申请单同步异常，请求报文：" + JsonUtils.getInstance().toJsonString(reqData) + "，响应报文：" + JsonUtils.getInstance().toJsonString(respData));
				
				pushRespData(errorMsgArray, ResponseStatusCodes.ERROR, "红字申请单同步异常", null);
			}
		}

		return errorMsgArray;
	}

	private String convertReason(String reason) {
		String result = reason;
		switch (reason) {
			case "Y":
				result = OrderInfoEnum.SPECIAL_INVOICE_REASON_1100000000.getKey();
				break;
			case "N1":
				result = OrderInfoEnum.SPECIAL_INVOICE_REASON_1010000000.getKey();
				break;
			case "N2":
				result = OrderInfoEnum.SPECIAL_INVOICE_REASON_1010000000.getKey();
				break;
			case "N3":
				result = OrderInfoEnum.SPECIAL_INVOICE_REASON_1010000000.getKey();
				break;
			case "N4":
				result = OrderInfoEnum.SPECIAL_INVOICE_REASON_1010000000.getKey();
				break;
			case "N5":
				result = OrderInfoEnum.SPECIAL_INVOICE_REASON_0000000100.getKey();
				break;
			default:
				throw new IllegalStateException("Unexpected value: " + reason);
		}

		return result;
	}

	public Boolean addSpecialInvoiceReversal(SpecialInvoiceReversalEntity specialInvoiceReversal) {
		Boolean isSuccess = false;
		
		if (StringUtils.isBlank(specialInvoiceReversal.getId())) {
			specialInvoiceReversal.setId(apiInvoiceCommonService.getGenerateShotKey());
		}
		if (StringUtils.isBlank(specialInvoiceReversal.getStatusCode())) {
			specialInvoiceReversal.setStatusCode("TZD0500");
		}
		specialInvoiceReversal.setKpzt(ConfigureConstant.STRING_0);
		specialInvoiceReversal.setCreateTime(specialInvoiceReversal.getCreateTime() != null ? specialInvoiceReversal.getCreateTime() : DateUtils.getDate(new Date(), DateUtils.DATE_TIME_PATTERN));
		specialInvoiceReversal.setUpdateTime(DateUtils.getDate(new Date(), DateUtils.DATE_TIME_PATTERN));
		
		int addResult = apiSpecialInvoiceReversalService.insertSpecialInvoiceReversal(specialInvoiceReversal);
		if (addResult > 0) {
			isSuccess = true;
		}
		return isSuccess;
	}

	public Boolean addSpecialInvoiceReversalItem(SpecialInvoiceReversalItem specialInvoiceReversalItem) {
		Boolean isSuccess = false;

		if (StringUtils.isBlank(specialInvoiceReversalItem.getYhzcbs())) {
			specialInvoiceReversalItem.setYhzcbs(OrderInfoEnum.YHZCBS_0.getKey());
		}
		specialInvoiceReversalItem.setId(apiInvoiceCommonService.getGenerateShotKey());
		specialInvoiceReversalItem.setCreateTime(DateUtils.getDate(new Date(), DateUtils.DATE_TIME_PATTERN));

		int addResult = apiSpecialInvoiceReversalService.insertSpecialInvoiceReversalItem(specialInvoiceReversalItem);
		if (addResult > 0) {
			isSuccess = true;
		}
		return isSuccess;
	}

	public Boolean removeSpecialInvoiceReversal(String id) {
		return apiSpecialInvoiceReversalService.deleteSpecialInvoice(id);
	}
	
	/**
	 * 生成红字申请单编号
	 * @throws OrderReceiveException
	 */
	@Override
	public Map<String, String> querySpecialInvoiceReversalCode(String accessPointId, String mechainCode,
			String invoiceCategory, String taxpayerCode, String terminalCode) throws OrderReceiveException {
		Map<String, String> resultMap = new HashMap<>(5);
		String specialInvoiceReversalCode = null;
		String fjh = null;
		if (StringUtils.isBlank(mechainCode)) {
			QueryNextInvoiceRequest queryNextInvoiceRequest = new QueryNextInvoiceRequest();
			queryNextInvoiceRequest.setFpzlDm(invoiceCategory);
			queryNextInvoiceRequest.setNsrsbh(taxpayerCode);
			queryNextInvoiceRequest.setSldId(accessPointId);
			QueryNextInvoiceResponseExtend queryNextInvoiceResponseExtend = HttpInvoiceRequestUtil.queryNextInvoice(OpenApiConfig.queryNextInvoice, queryNextInvoiceRequest, terminalCode);
			
			
			if (!OrderInfoContentEnum.SUCCESS.getKey().equals(queryNextInvoiceResponseExtend.getStatusCode())) {
				throw new OrderReceiveException("9999", "获取机器编码异常!");
			}
			mechainCode = queryNextInvoiceResponseExtend.getJqbh();
			fjh = queryNextInvoiceResponseExtend.getFjh();
			specialInvoiceReversalCode = mechainCode
					+ DateUtils.getYYYYMMDDHHMMSSFormatStr(new Date()).substring(2);
		} else {
			specialInvoiceReversalCode = mechainCode + DateUtils.getYYYYMMDDHHMMSSFormatStr(new Date()).substring(2);

		}

		resultMap.put(OrderManagementConstant.CODE, specialInvoiceReversalCode);
		resultMap.put("fjh", fjh);
		resultMap.put("mechainCode", mechainCode);
		return resultMap;
	}
	
	/**
	 * @param yfpDm
	 * @param yfpHm
	 * @return
	 * @descrption 代码重写 by ysy
	 */
	@Override
	public R mergeSpecialInvoice(String yfpDm, String yfpHm) {

		R result = new R();
		String statusCode = ResponseStatusCodes.SUCCESS;
		String errorMsg = "";

		CommonSpecialInvoice commonSpecialRsult = new CommonSpecialInvoice();
		try {

			// 明细行数据分组
			CommonSpecialInvoice commonSpecialInvoice = apiSpecialInvoiceReversalService.selectSpecialInvoiceReversalsAndItems(yfpDm, yfpHm);
			List<SpecialInvoiceReversalItem> specialInvoiceReversalItems = commonSpecialInvoice.getSpecialInvoiceReversalItemEntities();

			/**
			 * 同类明细分组
			 */
			Map<String, Map<String, BigDecimal>> itemMap = new HashMap<>(10);
			//总合计不含税金额（审核通过的红字申请单明细金额)
			BigDecimal zhjbhsje = BigDecimal.ZERO;
			//总价税合计金额（审核通过的红字申请单明细价税合计金额）
			BigDecimal zhjse = BigDecimal.ZERO;
			for (SpecialInvoiceReversalItem item : specialInvoiceReversalItems) {

				String xmmc = item.getXmmc() == null ? "" : item.getXmmc();
				String ggxh = item.getGgxh() == null ? "" : item.getGgxh();
				String xmdw = item.getXmdw() == null ? "" : item.getXmdw();
				
				BigDecimal xmsl = BigDecimal.ZERO;
				BigDecimal xmje = BigDecimal.ZERO;
				BigDecimal se = BigDecimal.ZERO;
				
				// 项目名称,项目规格型号,项目单价进行归类
				String itemKey = xmmc + ggxh + xmdw;
				
				
				// 判断明细行数组中数据是否已经存在,如果存在,说明有同类明细项,累加同类明细项,如果没有直接计算当前数据的金额等
				if (MapUtils.isNotEmpty(itemMap.get(itemKey))) {
					
					xmsl = (StringUtils.isNotBlank(item.getXmsl()) ? new BigDecimal(item.getXmsl()) : BigDecimal.ZERO)
							.abs().add(itemMap.get(itemKey).get("xmsl"));
					xmje = new BigDecimal(item.getXmje()).abs().add(itemMap.get(itemKey).get("xmje"));
					se = (StringUtils.isNotBlank(item.getSe()) ? new BigDecimal(item.getSe())
							: BigDecimal.ZERO).abs().add(itemMap.get(itemKey).get("se"));
				} else {
					xmsl = (StringUtils.isNotBlank(item.getXmsl()) ? new BigDecimal(item.getXmsl()) : BigDecimal.ZERO).abs();
					xmje = new BigDecimal(item.getXmje()).abs();
					se = (StringUtils.isNotBlank(item.getSe()) ? new BigDecimal(item.getSe()) : BigDecimal.ZERO).abs();
				}

				// 计算总金额和总税额
				zhjbhsje = zhjbhsje.add(xmje);
				zhjse = zhjse.add(se);
				
				Map<String, BigDecimal> totalItemMap = new HashMap<>(3);
				totalItemMap.put("xmsl", xmsl);
				totalItemMap.put("xmje", xmje);
				totalItemMap.put("se", se);
				itemMap.put(itemKey, totalItemMap);
			}
			
			
			//查询蓝字发票信息 转化蓝字发票信息 合并折扣行
			
			List<String> taxplayerCodeList = userInfoService.getTaxpayerCodeList();
			CommonOrderInvoiceAndOrderMxInfo commonOrderInvoiceAndOrderMxInfo = apiSpecialInvoiceReversalService.mergeBuleInvoiceInfo(yfpDm, yfpHm, taxplayerCodeList);
			if (commonOrderInvoiceAndOrderMxInfo == null) {
				result.put(OrderManagementConstant.CODE, ResponseStatusCodes.INVOICE_NOT_FOUND);
				result.put(OrderManagementConstant.ALL_MESSAGE, "原发票信息未找到");
				return result;
			}
			
			OrderInfo orderInfo = commonOrderInvoiceAndOrderMxInfo.getOrderInfo();
			OrderInvoiceInfo orderInvoiceInfo = commonOrderInvoiceAndOrderMxInfo.getOrderInvoiceInfo();


			//计算剩余可冲红金额
			BigDecimal totalTaxAmount = new BigDecimal(orderInvoiceInfo.getKpse()).negate();
			BigDecimal totalAmount = new BigDecimal(orderInvoiceInfo.getHjbhsje()).negate();

			//剩余可冲红税额
			BigDecimal sykchse = new BigDecimal(orderInvoiceInfo.getKpse()).subtract(zhjse);
			//剩余可冲红金额
			BigDecimal sykchje= new BigDecimal(orderInvoiceInfo.getHjbhsje()).subtract(zhjbhsje);

			if (sykchje.compareTo(BigDecimal.ZERO) < 0 || sykchse.compareTo(BigDecimal.ZERO) < 0) {
				//剩余可冲红金额不足
				result.put(OrderManagementConstant.CODE, ResponseStatusCodes.SPECIAL_INVOICE_REVERSAL_INSUFFICIENT_FUNDS);
				result.put(OrderManagementConstant.ALL_MESSAGE, "原发票可冲红余额不足");
				return result;
			}

			SpecialInvoiceReversalEntity specialInvoiceReversal = new SpecialInvoiceReversalEntity();

			String accessPointId = orderInvoiceInfo.getSld();
			String mechainCode = orderInvoiceInfo.getJqbh();
			String invoiceCategory = orderInvoiceInfo.getFpzlDm();
			String taxpayerCode = orderInvoiceInfo.getXhfNsrsbh();

			/**
			 * 获取红字申请单上传请求流水号
			 */
			String terminalCode = apiTaxEquipmentService.getTerminalCode(taxpayerCode);
			Map<String, String> resultMap = querySpecialInvoiceReversalCode(accessPointId, mechainCode,
					invoiceCategory, taxpayerCode, terminalCode);

			if(StringUtils.isBlank(resultMap.get(OrderManagementConstant.CODE))){
                log.error("机器编号获取失败!,yfpDm:{},yfpHm:{}",yfpDm,yfpHm);
				result.put(OrderManagementConstant.CODE,ResponseStatusCodes.SPECIAL_INVOICE_REVERSAL_CODE_IS_BLANK);
				result.put(OrderManagementConstant.ALL_MESSAGE, "");
				return result;
			}

			/**
			 * 可冲红数据组装
			 */
			specialInvoiceReversal.setSqdscqqlsh(resultMap.get(OrderManagementConstant.CODE));
			specialInvoiceReversal.setYfpDm(yfpDm);
			specialInvoiceReversal.setYfpHm(yfpHm);
			String invoiceType = "";
			if (OrderInfoEnum.ORDER_INVOICE_TYPE_0.getKey().equals(orderInvoiceInfo.getFpzlDm())
					|| OrderInfoEnum.ORDER_INVOICE_TYPE_2.getKey().equals(orderInvoiceInfo.getFpzlDm())) {
				invoiceType = "1";
			} else if (OrderInfoEnum.ORDER_INVOICE_TYPE_51.getKey().equals(orderInvoiceInfo.getFpzlDm())) {
				invoiceType = "2";
			}
			specialInvoiceReversal.setInvoiceType(invoiceType);
			specialInvoiceReversal.setFpzlDm(invoiceCategory);
			specialInvoiceReversal.setXhfMc(orderInfo.getXhfMc());
			specialInvoiceReversal.setXhfNsrsbh(orderInfo.getXhfNsrsbh());
			specialInvoiceReversal.setGhfqylx(orderInfo.getGhfQylx());
			specialInvoiceReversal.setGhfMc(orderInfo.getGhfMc());
			specialInvoiceReversal.setGhfNsrsbh(orderInfo.getGhfNsrsbh());
			specialInvoiceReversal.setHjbhsje(DigitUtils.formatDoublePrecision(totalAmount).toPlainString());
			specialInvoiceReversal.setHjse(DigitUtils.formatDoublePrecision(sykchse).toPlainString());
			specialInvoiceReversal.setKphjje(DigitUtils.formatDoublePrecision(totalAmount.add(sykchse)).toPlainString());
			specialInvoiceReversal.setSld(accessPointId);
			specialInvoiceReversal.setFjh(mechainCode);
			specialInvoiceReversal.setGhfDz(orderInfo.getGhfDz());
			specialInvoiceReversal.setGhfDh(orderInfo.getGhfDh());
			specialInvoiceReversal.setGhfYh(orderInfo.getGhfYh());
			specialInvoiceReversal.setGhfZh(orderInfo.getGhfZh());
			specialInvoiceReversal.setXhfDz(orderInfo.getXhfDz());
			specialInvoiceReversal.setXhfDh(orderInfo.getXhfDh());
			specialInvoiceReversal.setXhfYh(orderInfo.getXhfYh());
			specialInvoiceReversal.setXhfZh(orderInfo.getXhfZh());
			specialInvoiceReversal.setFhr(orderInfo.getFhr());
			specialInvoiceReversal.setSkr(orderInfo.getSkr());
			specialInvoiceReversal.setKpr(orderInvoiceInfo.getKpr());

			//原蓝字发票信息
			specialInvoiceReversal.setZfbz(orderInvoiceInfo.getZfBz());
			specialInvoiceReversal.setJqbh(orderInvoiceInfo.getJqbh());
			specialInvoiceReversal.setYfphjbhsje(orderInvoiceInfo.getHjbhsje());
			specialInvoiceReversal.setYfphjse(orderInvoiceInfo.getKpse());
			specialInvoiceReversal.setType(orderInvoiceInfo.getQdbz());
			specialInvoiceReversal.setSldMc(orderInvoiceInfo.getSldMc());
			specialInvoiceReversal.setYfpKprq(orderInvoiceInfo.getKprq());


			List<SpecialInvoiceReversalItem> specialInvoiceReversalItemEntities = new ArrayList<>();
			BigDecimal mxhjbhsje = BigDecimal.ZERO;
			BigDecimal mxhjse = BigDecimal.ZERO;


			//明细行大于8行的时候需要合并明细行
			List<OrderItemInfo> orderItemList = commonOrderInvoiceAndOrderMxInfo.getOrderItemList();
			boolean isMergeItem = orderItemList.size() > 8;
			if (QDBZ_CODE_1.getKey().equals(orderInvoiceInfo.getQdbz()) || QDBZ_CODE_3.getKey().equals(orderInvoiceInfo.getQdbz())) {
				isMergeItem = true;
			}
			String taxRate = null;
			if(isMergeItem){
				//合并明细行
				String spbm = orderItemList.get(0).getSpbm();
				for (int i = 0; i < orderItemList.size(); i++) {

					OrderItemInfo orderItemInfo = orderItemList.get(i);
					if (!spbm.equals(orderItemInfo.getSpbm())) {
						spbm = "";
					}

					//多税率标志
					if (i == 0) {
						taxRate = orderItemInfo.getSl();
					} else if (!orderItemInfo.getSl().equals(taxRate)) {
						taxRate = "";
					}
					mxhjbhsje = mxhjbhsje.add(new BigDecimal(orderItemInfo.getXmje()));
					mxhjse = mxhjse.add(new BigDecimal(orderItemInfo.getSe()));

				}
                //剩余明细可冲红金额
				BigDecimal symxkchje = mxhjbhsje.subtract(zhjbhsje);
				//剩余明细可冲红税额
				BigDecimal symxkchse = mxhjse.subtract(zhjse);

				SpecialInvoiceReversalItem specialInvoiceReversalItemEntity = new SpecialInvoiceReversalItem();
				specialInvoiceReversalItemEntity.setSpbm(spbm);
				specialInvoiceReversalItemEntity.setXmmc(ConfigureConstant.XJZSXHQD);
				specialInvoiceReversalItemEntity.setXmje(DigitUtils.formatDoublePrecision(symxkchje).toPlainString());
				specialInvoiceReversalItemEntity.setSe(DigitUtils.formatDoublePrecision(symxkchse).toPlainString());
				specialInvoiceReversalItemEntity.setHsbz(orderItemList.get(0).getHsbz());
				specialInvoiceReversalItemEntity.setSl(taxRate);
				//默认不使用优惠政策
				specialInvoiceReversalItemEntity.setYhzcbs(OrderInfoEnum.YHZCBS_0.getKey());
				//默认非零税率
				specialInvoiceReversalItemEntity.setSphxh(ConfigureConstant.STRING_0);
				specialInvoiceReversalItemEntities.add(specialInvoiceReversalItemEntity);
				specialInvoiceReversal.setDslbz(taxRate);
				specialInvoiceReversal.setHjbhsje(DigitUtils.formatDoublePrecision(symxkchje).toPlainString());
				specialInvoiceReversal.setHjse(DigitUtils.formatDoublePrecision(symxkchse).toPlainString());
				specialInvoiceReversal.setKphjje(DigitUtils.formatDoublePrecision(symxkchje.add(symxkchse)).toPlainString());
			}else{
				//不合并明细行
				for (SpecialInvoiceReversalItem item : specialInvoiceReversalItems) {
					//剔除已经审核通过的明细数据
					removeAlreadyRedItem(orderItemList, item);
				}
				specialInvoiceReversalItemEntities = convertToSpecialItemInfos(orderItemList);
				BigDecimal symxkchje = BigDecimal.ZERO;
				BigDecimal symxkchse = BigDecimal.ZERO;
				int i = 0;
				for(SpecialInvoiceReversalItem item : specialInvoiceReversalItemEntities){
					if (i == 0) {
						taxRate = item.getSl();
					} else if (!item.getSl().equals(taxRate)) {
						taxRate = "";
					}
					symxkchje = symxkchje.add(new BigDecimal(item.getXmje()));
					symxkchse = symxkchse.add(new BigDecimal(item.getSe()));
					i++;
				}
				specialInvoiceReversal.setHjbhsje(DigitUtils.formatDoublePrecision(symxkchje).toPlainString());
				specialInvoiceReversal.setHjse(DigitUtils.formatDoublePrecision(symxkchse).toPlainString());
				specialInvoiceReversal.setKphjje(DigitUtils.formatDoublePrecision(symxkchje.add(symxkchse)).toPlainString());
				specialInvoiceReversal.setDslbz(taxRate);
			}



			
			specialInvoiceReversalItemEntities.forEach(specialInvoiceReversalItem -> {
				
				/**
				 * 处理折扣行单价和数量,不显示
				 */
				if (StringUtils.isNotBlank(specialInvoiceReversalItem.getHsbz()) && OrderInfoEnum.FPHXZ_CODE_1.getKey().equals(specialInvoiceReversalItem.getHsbz())) {
					specialInvoiceReversalItem.setXmdj("");
					specialInvoiceReversalItem.setXmsl("");
				}
				
				/**
				 * 处理金额,格式化金额
				 */
				if (StringUtils.isNotBlank(specialInvoiceReversalItem.getXmje())) {
					specialInvoiceReversalItem.setXmje(DecimalCalculateUtil.decimalFormatToString(specialInvoiceReversalItem.getXmje(), ConfigureConstant.INT_2));
				}
				
				/**
				 * 处理税额,格式化税额
				 */
				if (StringUtils.isNotBlank(specialInvoiceReversalItem.getSe())) {
					specialInvoiceReversalItem.setSe(DecimalCalculateUtil.decimalFormatToString(specialInvoiceReversalItem.getSe(), ConfigureConstant.INT_2));
				}
				
				/**
				 * 处理单价,保留非零位
				 */
				if (StringUtils.isNotBlank(specialInvoiceReversalItem.getXmdj())) {
					specialInvoiceReversalItem.setXmdj(StringUtil.slFormat(DecimalCalculateUtil.decimalFormatToString(specialInvoiceReversalItem.getXmdj(), ConfigureConstant.INT_8)));
				}
				
				/**
				 * 处理数量,保留非零位
				 */
				if (StringUtils.isNotBlank(specialInvoiceReversalItem.getXmsl())) {
					specialInvoiceReversalItem.setXmsl(StringUtil.slFormat(DecimalCalculateUtil.decimalFormatToString(specialInvoiceReversalItem.getXmsl(), ConfigureConstant.INT_8)));
				}
				
				/**
				 * 处理税率,按照百分比显示
				 */
				if (StringUtils.isNotBlank(specialInvoiceReversalItem.getSl())) {
					NF.setMaximumFractionDigits(3);
					specialInvoiceReversalItem.setSl(specialInvoiceReversalItem.getSl().contains("%") ? specialInvoiceReversalItem.getSl() : NF.format(Double.valueOf(specialInvoiceReversalItem.getSl())));
				}
				
				if (specialInvoiceReversalItem.getLslbs() == null) {
					specialInvoiceReversalItem.setLslbs("");
				}
			});
			commonSpecialRsult.setSpecialInvoiceReversalEntity(specialInvoiceReversal);
			commonSpecialRsult.setSpecialInvoiceReversalItemEntities(specialInvoiceReversalItemEntities);
			result.put(OrderManagementConstant.CODE, statusCode);
			result.put(OrderManagementConstant.DATA, commonSpecialRsult);
			result.put(OrderManagementConstant.ALL_MESSAGE, errorMsg);
			return result;
		} catch (Exception e) {
			log.error("处理原蓝字发票信息一场:{}",e);
			log.error("原蓝票与红字申请单合并异常，发票代码：{},发票号码：{}",yfpDm,yfpHm);
			result.put(OrderManagementConstant.CODE,ResponseStatusCodes.ERROR);
			result.put(OrderManagementConstant.ALL_MESSAGE, "原发票信息获取异常");
			return result;
		}

	}

	/**
	 * bean转换
	 * @param orderItemList
	 * @return
	 */
	private List<SpecialInvoiceReversalItem> convertToSpecialItemInfos(List<OrderItemInfo> orderItemList) {

		List<SpecialInvoiceReversalItem> resultList = new ArrayList<>();
		for(OrderItemInfo orderItem : orderItemList){
			SpecialInvoiceReversalItem specialItem = new SpecialInvoiceReversalItem();
			BeanUtils.copyProperties(orderItem,specialItem);
			resultList.add(specialItem);
		}
		return resultList;
	}


	private List<OrderItemInfo> removeAlreadyRedItem(List<OrderItemInfo> orderItems, SpecialInvoiceReversalItem orderItem) {
		int j = 0;
		for (int i = 0; i < orderItems.size(); i++) {
			OrderItemInfo redOrder = orderItems.get(i);
			if (isEquality(redOrder, orderItem)) {
				BigDecimal xmsl = new BigDecimal(0);
				if (!StringUtils.isBlank(redOrder.getXmsl())) {
					xmsl = new BigDecimal(redOrder.getXmsl());
				}
				BigDecimal xmje = new BigDecimal(redOrder.getXmje());
				BigDecimal se = new BigDecimal(redOrder.getSe());

				BigDecimal rexmje = new BigDecimal(orderItem.getXmje());
				BigDecimal rese = new BigDecimal(orderItem.getSe());
				if ((xmje.abs().add(se.abs())).compareTo(rexmje.abs().add(rese.abs())) < 0) {
					continue;
				}

				if (!StringUtils.isBlank(redOrder.getXmsl())) {
					BigDecimal rexmsl = new BigDecimal(orderItem.getXmsl());
					xmsl = xmsl.abs().subtract(rexmsl.abs());
				}
				xmje = xmje.abs().subtract(rexmje.abs());
				se = se.abs().subtract(rese.abs());

				if (!StringUtils.isBlank(redOrder.getXmsl())) {
					redOrder.setXmsl(StringConvertUtils.removeLastZero("-" + DecimalCalculateUtil.decimalFormatToString(xmsl.abs().toString(), ConfigureConstant.INT_8)));
				}

				redOrder.setXmje("-" + DecimalCalculateUtil.decimalFormatToString(xmje.abs().toString(), ConfigureConstant.INT_2));
				redOrder.setSe("-" + DecimalCalculateUtil.decimalFormatToString(se.abs().toString(), ConfigureConstant.INT_2));
				orderItems.remove(i);
				if (BigDecimal.ZERO.doubleValue() != xmje.doubleValue()) {
					orderItems.add(i, redOrder);
				}
				break;
			}

		}

		return orderItems;
	}


	/**
	 * @Description 判断明细行是否为同一行
	 * @Author xieyuanqiang
	 * @Date 15:42 2018-11-07
	 */
	private boolean isEquality(OrderItemInfo orderItemInfo, SpecialInvoiceReversalItem redOrder) {
		StringBuilder key = new StringBuilder(com.github.pagehelper.util.StringUtil.isEmpty(orderItemInfo.getSpbm()) ? "" : orderItemInfo.getSpbm())
				.append(com.github.pagehelper.util.StringUtil.isEmpty(orderItemInfo.getXmmc()) ? "" : orderItemInfo.getXmmc())
				.append(com.github.pagehelper.util.StringUtil.isEmpty(orderItemInfo.getGgxh()) ? "" : orderItemInfo.getGgxh())
				.append(com.github.pagehelper.util.StringUtil.isEmpty(orderItemInfo.getXmdw()) ? "" : orderItemInfo.getXmdw());
		StringBuilder redkey = new StringBuilder(com.github.pagehelper.util.StringUtil.isEmpty(redOrder.getSpbm()) ? "" : redOrder.getSpbm())
				.append(com.github.pagehelper.util.StringUtil.isEmpty(redOrder.getXmmc()) ? "" : redOrder.getXmmc())
				.append(com.github.pagehelper.util.StringUtil.isEmpty(redOrder.getGgxh()) ? "" : redOrder.getGgxh())
				.append(com.github.pagehelper.util.StringUtil.isEmpty(redOrder.getXmdw()) ? "" : redOrder.getXmdw());
		boolean reslut = key.toString().equals(redkey.toString());
		return reslut;
	}




	@Override
	public Map<String, String> syncSpecialInvoiceReversal(String code, String taxpayerCode, String invoiceType,
			String invoiceCategory, String operatorId, String operatorName, String startTime, String endTime) {
		
		Map<String, String> resultMap = new HashMap<>(5);
		resultMap.put(OrderManagementConstant.CODE, "0000");
		resultMap.put(OrderManagementConstant.MESSAGE, "申请单同步成功");
		// 查询税控设备
		String terminalCode = apiTaxEquipmentService.getTerminalCode(taxpayerCode);
		
		if (OrderInfoEnum.TAX_EQUIPMENT_FGBW.getKey().equals(terminalCode) || OrderInfoEnum.TAX_EQUIPMENT_FGHX.getKey().equals(terminalCode) || OrderInfoEnum.TAX_EQUIPMENT_FGUKEY.getKey().equals(terminalCode)) {
			/**
			 * todo 方格默认使用1000张,后期如果超过1000张再考虑其他方案
			 */
			HZSQDXZ_REQ hzsqdxzReq = new HZSQDXZ_REQ();
			hzsqdxzReq.setSQBXZQQPCH(apiInvoiceCommonService.getGenerateShotKey());
			hzsqdxzReq.setNSRSBH(taxpayerCode);
			hzsqdxzReq.setFPLXDM(invoiceType);
			hzsqdxzReq.setTKRQQ(startTime);
			hzsqdxzReq.setTKRQZ(endTime);
			hzsqdxzReq.setGMFSBH("");
			hzsqdxzReq.setXHFSBH(taxpayerCode);
			hzsqdxzReq.setXXBBH("");
			hzsqdxzReq.setXXBFW("");
			hzsqdxzReq.setYS("1");
			hzsqdxzReq.setGS("1000");
			
			
			downloadSpecialInvoiceReversalFg(hzsqdxzReq);
			resultMap.put(OrderManagementConstant.MESSAGE, "申请单同步请求成功");
			
		} else {
			boolean stop = false;
			for (int i = 1; i < 1000; i++) {
				
				if (stop) {
					break;
				}
				HpInvocieRequest reqData = new HpInvocieRequest();
				reqData.setSQBXZQQPCH(code);
				reqData.setNSRSBH(taxpayerCode);
				reqData.setFPLX(invoiceType);
				reqData.setFPLB(invoiceCategory);
				reqData.setXXBFW(ConfigureConstant.STRING_0);
				reqData.setTKRQ_Q(startTime);
				reqData.setTKRQ_Z(endTime);
				reqData.setPageNo(String.valueOf(i));
				reqData.setPageSize("10");
				
				/**
				 * 税控盘托管和百望服务器不进行下载操作
				 */
				if (OrderInfoEnum.TAX_EQUIPMENT_BW_ACTIVEX.getKey().equals(terminalCode)) {
					log.error("百望active_x不支持后端红字申请单同步");
					resultMap.put(OrderManagementConstant.CODE, "9999");
					resultMap.put(OrderManagementConstant.MESSAGE, "百望active_x不支持后端红字申请单同步");
					return resultMap;
				}
				
				HpResponseBean respData = HttpInvoiceRequestUtil.redInvoiceDown(OpenApiConfig.redInvoiceDown, reqData, terminalCode);
				
				if (respData == null) {
					log.error("红字申请单同步，底层响应内容为空！申请单编号:{},请求报文：{}", code, JsonUtils.getInstance().toJsonString(reqData));
					resultMap.put(OrderManagementConstant.CODE, "0000");
					resultMap.put(OrderManagementConstant.MESSAGE, "申请单同步成功");
					return resultMap;
				}
				
				if (!ConfigureConstant.STRING_0000.equals(respData.getResult().getStatusCode())) {
					log.error("红字申请单同步，底层返回错误！申请单编号:{},请求报文：{},响应报文:{}", code,
							JsonUtils.getInstance().toJsonString(reqData),
							JsonUtils.getInstance().toJsonString(respData));
					resultMap.put(OrderManagementConstant.CODE, respData.getResult().getStatusCode());
					resultMap.put(OrderManagementConstant.MESSAGE, respData.getResult().getStatusMessage());
					return resultMap;
				}
				
				int successCount = Integer
						.parseInt(StringUtils.isBlank(respData.getResult().getSuccess_COUNT()) ? "0" : respData.getResult().getSuccess_COUNT());
				
				if (i == 1 && successCount <= 0) {
					log.info("没有需要同步的申请单数据!");
					resultMap.put(OrderManagementConstant.CODE, "9999");
					resultMap.put(OrderManagementConstant.MESSAGE, "没有需要同步的申请单数据");
					return resultMap;
				} else if (successCount <= 0) {
					resultMap.put(OrderManagementConstant.CODE, "0000");
					resultMap.put(OrderManagementConstant.MESSAGE, "申请单数据同步完成");
					return resultMap;
				} else if (respData.getResult().getRedinvreqbillxx() == null) {
					log.info("没有需要同步的申请单数据!");
					resultMap.put(OrderManagementConstant.CODE, "0000");
					resultMap.put(OrderManagementConstant.MESSAGE, "申请单数据同步完成");
					return resultMap;
				}
				
				if (CollectionUtils.isEmpty(respData.getResult().getRedinvreqbillxx())) {
					break;
				}
				
				for (int j = 0; j < respData.getResult().getRedinvreqbillxx().size(); j++) {
					
					ResponseHzfpsqbsc responseHzfpsqbsc = respData.getResult().getRedinvreqbillxx().get(j);
					if (ObjectUtil.isNotEmpty(responseHzfpsqbsc) && StringUtils.isNotBlank(responseHzfpsqbsc.getStatus_CODE()) && SPECIAL_INVOICE_STATUS_B900076.getKey().equals(responseHzfpsqbsc.getStatus_CODE())) {
						stop = true;
						break;
					}
					
					//申请说明
					String sqsm = responseHzfpsqbsc.getSqsm();
					//申请单号
					String sqdh = responseHzfpsqbsc.getSqdh();
					
					if (StringUtils.isBlank(sqdh)) {
						log.error("下载红字申请单，返回红字申请单编号为空!");
						continue;
					}
					/**
					 * 同步申请单号相当于申请单请求流水号
					 */
					SpecialInvoiceReversalEntity specialInvoiceReversal = apiSpecialInvoiceReversalService
							.selectSpecialInvoiceReversalBySqdqqlsh(sqdh);
					//删除状态
					boolean isDelete = false;
					if (null != specialInvoiceReversal) {
						SpecialInvoiceReversalEntity updateSpecialEntity = new SpecialInvoiceReversalEntity();
						updateSpecialEntity.setSqdh(sqdh);
						//判断申请单是否被删除
						if ("1".equals(specialInvoiceReversal.getDataStatus())) {

							//如果同步下来已经删除的申请单 将申请单恢复
							isDelete = true;
						}
						
						//如果是审核通过 已审核通过并且删除的订单恢复为未删除状态
						if (OrderInfoEnum.SPECIAL_INVOICE_STATUS_TZD0000.getKey().equals(responseHzfpsqbsc.getStatus_CODE())) {
							updateSpecialEntity.setDataStatus("0");
						}
						if (OrderInfoEnum.SPECIAL_INVOICE_STATUS_TZD0061.getKey().equals(responseHzfpsqbsc.getStatus_CODE())) {
							// 重复上传 处理吓一条数据
							log.info("重复同步审核结果，申请单编号:{}", sqdh);
							continue;
						}
						if (OrderInfoEnum.SPECIAL_INVOICE_STATUS_TZD0500.getKey().equals(responseHzfpsqbsc.getStatus_CODE())) {
							// 未上传状态 同步审核结果错误 继续处理下一条
							log.error("同步审核结果错误， 同步的申请单位未上传状态,申请单编号:{}", sqdh);
							continue;
						}

						//如果同步下来的结果与已有的结果不一致的话 更新最新的审核结果
						if(!responseHzfpsqbsc.getStatus_CODE().equals(specialInvoiceReversal.getStatusCode())){
							updateSpecialEntity.setXxbbh(responseHzfpsqbsc.getXxbbh());
							updateSpecialEntity.setStatusCode(responseHzfpsqbsc.getStatus_CODE());
							updateSpecialEntity.setStatusMessage(responseHzfpsqbsc.getStatus_MESSAGE());
							updateSpecialEntity.setEditorId(operatorId);
							updateSpecialEntity.setEditorName(operatorName);
							updateSpecialEntity.setId(specialInvoiceReversal.getId());
							updateSpecialEntity.setUpdateTime(new Date());
							int update = apiSpecialInvoiceReversalService.updateSpecialInvoiceReversal(updateSpecialEntity);
							if (update <= 0) {
								log.error("红字申请单同步，更新红字申请单状态失败,申请单编号:{}", sqdh);
								continue;
							}
						}
						
					} else {
						
						Map<String, DeptEntity> taxplayerEntityList = userInfoService.getTaxpayerEntityMap();
						if (!OrderInfoEnum.SPECIAL_INVOICE_STATUS_TZD0000.getKey().equals(responseHzfpsqbsc.getStatus_CODE())) {
							String submitStatus = responseHzfpsqbsc.getStatus_CODE();
							
							if (OrderInfoEnum.SPECIAL_INVOICE_STATUS_TZD0061.getKey().equals(submitStatus)) {
								// 重复上传 处理吓一条数据
								log.info("重复同步审核结果，申请单编号:{}", sqdh);
								continue;
							}
							
							if (OrderInfoEnum.SPECIAL_INVOICE_STATUS_TZD0500.getKey().equals(submitStatus)) {
								// 未上传状态 同步审核结果错误 继续处理下一条
								log.error("同步审核结果错误， 同步的申请单位未上传状态,申请单编号:{}", sqdh);
								continue;
							}
							
							if (OrderInfoEnum.SPECIAL_INVOICE_STATUS_B900076.getKey().equals(submitStatus)) {
								//未同步到数据
								log.error("同步审核结果错误， 同步结果为空,申请单编号:{}", sqdh);
								stop = true;
								break;
								
							}
						}
						
						// 如果根据申请单号去数据库里没有查到数据 则根据同步的数据重新创建红字申请单信息
						specialInvoiceReversal = new SpecialInvoiceReversalEntity();
						String type = "0";
						if ("0000000090".equals(responseHzfpsqbsc.getYysbz())) {
							type = "3";
						}
						specialInvoiceReversal.setType(type);
						specialInvoiceReversal.setSqdh(sqdh);
						specialInvoiceReversal.setSqdscqqlsh(sqdh);
						specialInvoiceReversal.setSqsm(convertReason(responseHzfpsqbsc.getSqsm()));
						specialInvoiceReversal.setYfpDm(
								("0000000000".equals(responseHzfpsqbsc.getYfp_DM()) ? "" : responseHzfpsqbsc.getYfp_DM()));
						specialInvoiceReversal.setYfpHm(
								("00000000".equals(responseHzfpsqbsc.getYfp_HM()) ? "" : responseHzfpsqbsc.getYfp_HM()));
						specialInvoiceReversal.setInvoiceType(responseHzfpsqbsc.getFplx());
						specialInvoiceReversal.setFpzlDm(responseHzfpsqbsc.getFplb());
						/*specialInvoiceReversal.setInvoiceDate(DateUtils.format(
								DateUtils.stringToDate(responseHzfpsqbsc.getTKSJ(), DateUtils.DATE_TIME_PATTERN),
								DateUtils.DATE_TIME_PATTERN));*/
						specialInvoiceReversal.setXhfMc(responseHzfpsqbsc.getXsf_MC());
						specialInvoiceReversal.setXhfNsrsbh(responseHzfpsqbsc.getXsf_NSRSBH());
						//获取当前登陆的用户信息
						if (StringUtils.isNotBlank(responseHzfpsqbsc.getXsf_NSRSBH())) {
							//如果是销方申请
							DeptEntity sysDeptEntity = taxplayerEntityList.get(responseHzfpsqbsc.getXsf_NSRSBH());
							if (sysDeptEntity != null) {
								specialInvoiceReversal.setXhfDh(sysDeptEntity.getTaxpayerPhone());
								specialInvoiceReversal.setXhfDz(sysDeptEntity.getTaxpayerAddress());
								specialInvoiceReversal.setXhfYh(sysDeptEntity.getTaxpayerBank());
								specialInvoiceReversal.setXhfZh(sysDeptEntity.getTaxpayerAccount());
							} else {
								
								BuyerEntity queryBuyerByPurchaseName = buyerService.queryBuyerByPurchaseName(responseHzfpsqbsc.getXsf_MC(), responseHzfpsqbsc.getGmf_NSRSBH());
								if (queryBuyerByPurchaseName != null) {
									specialInvoiceReversal.setXhfDh(queryBuyerByPurchaseName.getPhone());
									specialInvoiceReversal.setXhfDz(queryBuyerByPurchaseName.getAddress());
									specialInvoiceReversal.setXhfYh(queryBuyerByPurchaseName.getBankOfDeposit());
									specialInvoiceReversal.setXhfZh(queryBuyerByPurchaseName.getBankNumber());
								}
							}
							
						}
						
						if (StringUtils.isNotBlank(responseHzfpsqbsc.getGmf_NSRSBH())) {
							DeptEntity sysDeptEntity = taxplayerEntityList.get(responseHzfpsqbsc.getGmf_NSRSBH());
							if (sysDeptEntity != null) {
								specialInvoiceReversal.setGhfDh(sysDeptEntity.getTaxpayerPhone());
								specialInvoiceReversal.setGhfDz(sysDeptEntity.getTaxpayerAddress());
								specialInvoiceReversal.setGhfYh(sysDeptEntity.getTaxpayerBank());
								specialInvoiceReversal.setGhfZh(sysDeptEntity.getTaxpayerAccount());
							} else {
								
								BuyerEntity queryBuyerByPurchaseName = buyerService.queryBuyerByPurchaseName(responseHzfpsqbsc.getGmf_MC(), responseHzfpsqbsc.getXsf_NSRSBH());
								if (queryBuyerByPurchaseName != null) {
									specialInvoiceReversal.setGhfDh(queryBuyerByPurchaseName.getPhone());
									specialInvoiceReversal.setGhfDz(queryBuyerByPurchaseName.getAddress());
									specialInvoiceReversal.setGhfYh(queryBuyerByPurchaseName.getBankOfDeposit());
									specialInvoiceReversal.setGhfZh(queryBuyerByPurchaseName.getBankNumber());
								}
							}
							
						}
						specialInvoiceReversal.setGhfMc(responseHzfpsqbsc.getGmf_MC());
						specialInvoiceReversal.setGhfNsrsbh(responseHzfpsqbsc.getGmf_NSRSBH());
						specialInvoiceReversal.setGhfqylx("01");
						specialInvoiceReversal.setHjbhsje(responseHzfpsqbsc.getHjje());
						specialInvoiceReversal.setHjse(responseHzfpsqbsc.getHjse());
						specialInvoiceReversal.setKphjje(new BigDecimal(responseHzfpsqbsc.getHjje())
								.add(new BigDecimal(responseHzfpsqbsc.getHjse())).toString());
						specialInvoiceReversal.setXxbbh(responseHzfpsqbsc.getXxbbh());
						specialInvoiceReversal.setStatusCode(responseHzfpsqbsc.getStatus_CODE());
						specialInvoiceReversal.setStatusMessage(responseHzfpsqbsc.getStatus_MESSAGE());
						specialInvoiceReversal.setNsrsbh(taxpayerCode);
						specialInvoiceReversal.setCreatorId(operatorId);
						specialInvoiceReversal.setCreatorName(operatorName);
						specialInvoiceReversal.setEditorId(operatorId);
						specialInvoiceReversal.setEditorName(operatorName);
						specialInvoiceReversal.setKpr(operatorName);
						specialInvoiceReversal.setCreateTime(DateUtils.stringToDate(responseHzfpsqbsc.getTksj(), DateUtils.DATE_TIME_PATTERN));
						boolean isSuccess = addSpecialInvoiceReversal(specialInvoiceReversal);
						if (isSuccess) {
							String specialInvoiceReversalId = specialInvoiceReversal.getId();
							String taxRate = "";
							List<Commoninvdetail> invoiceDetails = responseHzfpsqbsc.getCommoninvdetails();
							for (int k = 0; k < invoiceDetails.size(); k++) {
								Commoninvdetail invoiceDetail = invoiceDetails.get(k);
								
								SpecialInvoiceReversalItem specialInvoiceReversalItem = new SpecialInvoiceReversalItem();
								specialInvoiceReversalItem.setSpecialInvoiceReversalId(specialInvoiceReversalId);
								specialInvoiceReversalItem.setSpbm(invoiceDetail.getSpbm());
								specialInvoiceReversalItem.setXmmc(invoiceDetail.getXmmc());
								specialInvoiceReversalItem.setGgxh(invoiceDetail.getGgxh());
								specialInvoiceReversalItem.setXmdw(invoiceDetail.getDw());
								/**
								 * 申请单下载判断单价数量和税率
								 */
								if (StringUtils.isNotBlank(invoiceDetail.getSl()) && "0.000000".equals(invoiceDetail.getSl())) {
									specialInvoiceReversalItem.setXmsl("");
									specialInvoiceReversalItem.setXmdj("");
									specialInvoiceReversalItem.setSl("");
								} else {
									specialInvoiceReversalItem.setXmdj(String.valueOf(invoiceDetail.getXmdj()));
									specialInvoiceReversalItem.setXmje(String.valueOf(invoiceDetail.getXmje()));
									specialInvoiceReversalItem.setSl(String.valueOf(invoiceDetail.getSl()));
								}
								specialInvoiceReversalItem.setXmje(String.valueOf(invoiceDetail.getXmje()));
								specialInvoiceReversalItem.setSe(String.valueOf(invoiceDetail.getSe()));
								specialInvoiceReversalItem.setHsbz(invoiceDetail.getHsbz());
								specialInvoiceReversalItem.setYhzcbs(StringUtils.isNotBlank(invoiceDetail.getYhzcbs())
										? invoiceDetail.getYhzcbs() : OrderInfoEnum.YHZCBS_0.getKey());
								specialInvoiceReversalItem.setYhzcbs(StringUtils.isNotBlank(invoiceDetail.getLslbs())
										? invoiceDetail.getLslbs() : "");
								specialInvoiceReversalItem.setSphxh(String.valueOf(k + 1));
								
								isSuccess = isSuccess & addSpecialInvoiceReversalItem(specialInvoiceReversalItem);
								if (!isSuccess) {
									log.error("红字申请单同步，保存申请单明细异常,申请单编号:{}", sqdh);
									continue;
								}
								
								if (k == 0) {
									taxRate = String.valueOf(invoiceDetail.getSl());
								} else if (!taxRate.equals(invoiceDetail.getSl())) {
									taxRate = "多税率";
								}
							}
							
							// 明细是否全部保存成功
							if (isSuccess) {
								specialInvoiceReversal = new SpecialInvoiceReversalEntity();
								specialInvoiceReversal.setId(specialInvoiceReversalId);
								specialInvoiceReversal.setDslbz(taxRate);
								isSuccess = editSpecialInvoiceReversal(specialInvoiceReversal);
								if (!isSuccess) {
									log.error("红字申请单信息保存异常,申请单号:{}", sqdh);
									continue;
								}
							} else {
								log.error("红字申请单明细信息保存异常,申请单号:{}", sqdh);
								boolean isDeleteSuccess = removeSpecialInvoiceReversal(specialInvoiceReversalId);
								if (isDeleteSuccess) {
									log.error("红字申请单级联删除失败,申请单号:{},红字申请单id:{}", sqdh, specialInvoiceReversalId);
								}
								continue;
							}
						} else {
							log.error("红字申请单同步，保存红字申请单失败！申请单编号：{}", sqdh);
							continue;
						}
					}
				}
			}
		}
		
		return resultMap;
	}
	
	
	@Override
	public List<SpecialExcelImport> readSpecialInvoiceFromExcel(MultipartFile file) throws OrderReceiveException {
		
		
		List<SpecialExcelImport> specialExcelImportList = null;
		try {
			specialExcelImportList = new ArrayList<>();
			boolean isEmpty = true;
			for (int i = 0; i < 3; i++) {
				
				Map<String, String> headToProperty = new HashMap<>(10);
				
				
				for (SpecialInvoiceImportExcelEnum flowStatus : SpecialInvoiceImportExcelEnum.values()) {
					headToProperty.put(flowStatus.getKey(), flowStatus.getValue());
				}
				
				ExcelReadContext context = new ExcelReadContext(NewOrderExcel.class, headToProperty, true);
				
				if (StringUtils.isBlank(file.getOriginalFilename())) {
					context.setFilePrefix(".xlsx");
				}else{
					context.setFilePrefix(file.getOriginalFilename().substring(file.getOriginalFilename().lastIndexOf(".")));
				}
				context.setHeadRow(2);
				context.setNeedRowIndex(false);
				context.setSheetIndex(i);
				ExcelReadHandle handle = new ExcelReadHandle(context);
				
				List<SpecialExcelImport> specialExcelList = handle.readFromExcel(file.getInputStream(), SpecialExcelImport.class);
				
				if(CollectionUtils.isNotEmpty(specialExcelList)){
					
					for(SpecialExcelImport specialExcelImport : specialExcelList){
						
						specialExcelImport.setCypzyfplx(getType(specialExcelImport.getCypzyfplx()));
						specialExcelImport.setHsbz(OrderInfoEnum.HSBZ_0.getKey());
						
						if(i == 0){
							specialExcelImport.setSqyy(SPECIAL_INVOICE_REASON_1100000000.getKey());
						}else if(i ==1){
							specialExcelImport.setSqyy(SPECIAL_INVOICE_REASON_1010000000.getKey());
							
						}else if(i == 2){
							specialExcelImport.setSqyy(SPECIAL_INVOICE_REASON_0000000100.getKey());
							
						}
					}
				}
				specialExcelImportList.addAll(specialExcelList);
				
			}
			
			if (CollectionUtils.isEmpty(specialExcelImportList)) {
				throw new OrderReceiveException(OrderInfoContentEnum.SPECIAL_INVOICE_IMPORT_VALIDATE_ERROR4);
			}
		} catch (Exception e) {
			log.error("excel读取异常，异常原因:{}", e);
			throw new OrderReceiveException("9999", "excel读取异常");
		}
		return specialExcelImportList;
	}
	
	
	/**
	 * 判断明细行是否为空
	 *
	 * @param row
	 * @return
	 */
	private boolean rowIsEmpty(Row row) {
		if (null != row) {
			for (int i = 0; i < row.getLastCellNum(); i++) {
				Cell cell = row.getCell(i);
				if (null != cell && cell.getCellType() != CellType.BLANK) {
					return false;
				}
			}
		}
		return true;
	}

	private String getType(String type) {
		String result = "0";
		switch (type) {
		case "":
			result = SPECIAL_INVOICE_TYPE_0.getKey();
			break;
		case "仅涉及销售数量变更（销货退回、开票有误等原因）":
			result = SPECIAL_INVOICE_TYPE_1.getKey();
			break;
			case "仅涉及销售金额变更（销售折让）":
				result = SPECIAL_INVOICE_TYPE_2.getKey();
				break;
			default:
				result = SPECIAL_INVOICE_TYPE_3.getKey();
				break;
		}
		return result;
	}
	
	/**
	 * 补全发票数据
	 *
	 * @param commonSpecialInvoiceList
	 * @param accessPointId
	 * @param accessPointName
	 * @param machineCode
	 * @param terminalCode
	 * @param drawerInfoEntity
	 * @param userId
	 * @param userName
	 * @param xhfMc
	 * @param xhfNsrsbh
	 * @param xhfDz
	 * @param xhfDh
	 * @param xhfYh
	 * @param xhfZh
	 * @return
	 * @throws OrderReceiveException
	 * @throws InterruptedException
	 */
	@Override
	public List<CommonSpecialInvoice> completeOrderInvoiceInfo(List<CommonSpecialInvoice> commonSpecialInvoiceList,
	                                                           String accessPointId, String accessPointName, String machineCode, String terminalCode,
	                                                           DrawerInfoEntity drawerInfoEntity, String userId, String userName, String xhfMc, String xhfNsrsbh, String xhfDz, String xhfDh, String xhfYh, String xhfZh) throws OrderReceiveException, InterruptedException {
		String prvCode = "";
		
		/**
		 * 如果为销方申请,需要把页面传递销方数据存放在销方中,购方数据使用原蓝票数据进行填充,
		 * 反之,页面传递的销方数据需要村让在购方信息中,销方数据从原蓝票获取.
		 */
		/**
		 * 如果销方申请,并且表格中数据,购方名称不为空,需要判断购房数据和原蓝票数据是否一致
		 */
		/**
		 * 如果销方申请,并且表格中数据,购方税号不为空,需要判断购房数据和原蓝票数据是否一致
		 */
		/**
		 * 如果购方申请,并且表格中数据,销方名称不为空,需要判断销方数据和原蓝票数据是否一致
		 */
		/**
		 * 如果购方申请,并且表格中数据,销方税号不为空,需要判断销方数据和原蓝票数据是否一致
		 */
		/**
		 * 补全明细信息
		 */
		for (CommonSpecialInvoice specialInvoice : commonSpecialInvoiceList) {
			
			if (StringUtils.isNotBlank(specialInvoice.getSpecialInvoiceReversalEntity().getYfpDm()) &&
					StringUtils.isNotBlank(specialInvoice.getSpecialInvoiceReversalEntity().getYfpHm())) {

				// 获取发票数据
				R respData = mergeSpecialInvoice(specialInvoice.getSpecialInvoiceReversalEntity().getYfpDm(), specialInvoice.getSpecialInvoiceReversalEntity().getYfpHm());
				log.debug("{}红票查询到的蓝票数据为:{}", LOGGER_MSG, JsonUtils.getInstance().toJsonString(respData));

				String statusCode = (String) respData.get(OrderManagementConstant.CODE);
				String statusMsg = (String) respData.get(OrderManagementConstant.ALL_MESSAGE);
				if (statusCode.equals(ResponseStatusCodes.SUCCESS)) {

					CommonSpecialInvoice commonSpecialInvoice = JsonUtils.getInstance().parseObject(JsonUtils.getInstance().toJsonString(respData.get(OrderManagementConstant.DATA)), CommonSpecialInvoice.class);

					/**
					 * 如果为销方申请,需要把页面传递销方数据存放在销方中,购方数据使用原蓝票数据进行填充,
					 * 反之,页面传递的销方数据需要村让在购方信息中,销方数据从原蓝票获取.
					 */
					if (SPECIAL_INVOICE_REASON_0000000100.getKey().equals(specialInvoice.getSpecialInvoiceReversalEntity().getSqsm())) {

						/**
						 * 如果销方申请,并且表格中数据,购方名称不为空,需要判断购房数据和原蓝票数据是否一致
						 */
						if (StringUtils.isNotBlank(specialInvoice.getSpecialInvoiceReversalEntity().getGhfMc())
								&& !specialInvoice.getSpecialInvoiceReversalEntity().getGhfMc().equals(commonSpecialInvoice.getSpecialInvoiceReversalEntity().getGhfMc())) {
							log.error("{}专票表格导入转换异常:{}", LOGGER_MSG, OrderInfoContentEnum.SPECIAL_INVOICE_IMPORT_VALIDATE_ERROR6.getMessage());
							throw new OrderReceiveException(OrderInfoContentEnum.SPECIAL_INVOICE_IMPORT_VALIDATE_ERROR6);
						}
						/**
						 * 如果销方申请,并且表格中数据,购方税号不为空,需要判断购房数据和原蓝票数据是否一致
						 */
						if (StringUtils.isNotBlank(specialInvoice.getSpecialInvoiceReversalEntity().getGhfNsrsbh())
								&& !specialInvoice.getSpecialInvoiceReversalEntity().getGhfNsrsbh().equals(commonSpecialInvoice.getSpecialInvoiceReversalEntity().getGhfNsrsbh())) {
							log.error("{}专票表格导入转换异常:{}", LOGGER_MSG, OrderInfoContentEnum.SPECIAL_INVOICE_IMPORT_VALIDATE_ERROR7.getMessage());
							throw new OrderReceiveException(OrderInfoContentEnum.SPECIAL_INVOICE_IMPORT_VALIDATE_ERROR7);
						}

						if(!xhfNsrsbh.equals(commonSpecialInvoice.getSpecialInvoiceReversalEntity().getXhfNsrsbh())){
							throw new OrderReceiveException(OrderInfoContentEnum.SPECIAL_INVOICE_IMPORT_VALIDATE_ERROR7);

						}
						if(!xhfMc.equals(commonSpecialInvoice.getSpecialInvoiceReversalEntity().getXhfMc())){
							throw new OrderReceiveException(OrderInfoContentEnum.SPECIAL_INVOICE_IMPORT_VALIDATE_ERROR7);

						}
					} else {

						/**
						 * 如果购方申请,并且表格中数据,销方名称不为空,需要判断销方数据和原蓝票数据是否一致
						 */
						if (StringUtils.isNotBlank(specialInvoice.getSpecialInvoiceReversalEntity().getXhfMc()) &&
								!specialInvoice.getSpecialInvoiceReversalEntity().getXhfMc().equals(commonSpecialInvoice.getSpecialInvoiceReversalEntity().getXhfMc())) {
							log.error("{}专票表格导入转换异常:{}", LOGGER_MSG, OrderInfoContentEnum.SPECIAL_INVOICE_IMPORT_VALIDATE_ERROR7.getMessage());
							throw new OrderReceiveException(OrderInfoContentEnum.SPECIAL_INVOICE_IMPORT_VALIDATE_ERROR7);
						}
						/**
						 * 如果购方申请,并且表格中数据,销方税号不为空,需要判断销方数据和原蓝票数据是否一致
						 */
						if (StringUtils.isNotBlank(specialInvoice.getSpecialInvoiceReversalEntity().getXhfNsrsbh())
								&& !specialInvoice.getSpecialInvoiceReversalEntity().getXhfNsrsbh().equals(commonSpecialInvoice.getSpecialInvoiceReversalEntity().getXhfNsrsbh())) {
							log.error("{}专票表格导入转换异常:{}", LOGGER_MSG, OrderInfoContentEnum.SPECIAL_INVOICE_IMPORT_VALIDATE_ERROR7.getMessage());
							throw new OrderReceiveException(OrderInfoContentEnum.SPECIAL_INVOICE_IMPORT_VALIDATE_ERROR7);
						}

						//判断当前销方是否为此发票的购方
						if(!xhfNsrsbh.equals(commonSpecialInvoice.getSpecialInvoiceReversalEntity().getGhfNsrsbh())){
							throw new OrderReceiveException(OrderInfoContentEnum.SPECIAL_INVOICE_IMPORT_VALIDATE_ERROR7);

						}
						if(!xhfMc.equals(commonSpecialInvoice.getSpecialInvoiceReversalEntity().getGhfMc())){
							throw new OrderReceiveException(OrderInfoContentEnum.SPECIAL_INVOICE_IMPORT_VALIDATE_ERROR7);

						}

					}
					specialInvoice.getSpecialInvoiceReversalEntity().setGhfMc(commonSpecialInvoice.getSpecialInvoiceReversalEntity().getGhfMc());
					specialInvoice.getSpecialInvoiceReversalEntity().setGhfNsrsbh(commonSpecialInvoice.getSpecialInvoiceReversalEntity().getGhfNsrsbh());
					specialInvoice.getSpecialInvoiceReversalEntity().setGhfDz(StringUtils.isBlank(specialInvoice.getSpecialInvoiceReversalEntity().getGhfDz()) ?
							commonSpecialInvoice.getSpecialInvoiceReversalEntity().getGhfDz() : specialInvoice.getSpecialInvoiceReversalEntity().getGhfDz());
					specialInvoice.getSpecialInvoiceReversalEntity().setGhfDh(StringUtils.isBlank(specialInvoice.getSpecialInvoiceReversalEntity().getGhfDh()) ?
							commonSpecialInvoice.getSpecialInvoiceReversalEntity().getGhfDh() : specialInvoice.getSpecialInvoiceReversalEntity().getGhfDh());
					specialInvoice.getSpecialInvoiceReversalEntity().setGhfYh(StringUtils.isBlank(specialInvoice.getSpecialInvoiceReversalEntity().getGhfYh()) ?
							commonSpecialInvoice.getSpecialInvoiceReversalEntity().getGhfYh() : specialInvoice.getSpecialInvoiceReversalEntity().getGhfYh());
					specialInvoice.getSpecialInvoiceReversalEntity().setGhfZh(StringUtils.isBlank(specialInvoice.getSpecialInvoiceReversalEntity().getGhfZh()) ?
							commonSpecialInvoice.getSpecialInvoiceReversalEntity().getGhfZh() : specialInvoice.getSpecialInvoiceReversalEntity().getGhfZh());
					specialInvoice.getSpecialInvoiceReversalEntity().setDslbz(commonSpecialInvoice.getSpecialInvoiceReversalEntity().getDslbz());
					specialInvoice.getSpecialInvoiceReversalEntity().setGhfqylx(commonSpecialInvoice.getSpecialInvoiceReversalEntity().getGhfqylx());

					specialInvoice.getSpecialInvoiceReversalEntity().setXhfMc(commonSpecialInvoice.getSpecialInvoiceReversalEntity().getXhfMc());
					specialInvoice.getSpecialInvoiceReversalEntity().setXhfNsrsbh(commonSpecialInvoice.getSpecialInvoiceReversalEntity().getXhfNsrsbh());
					specialInvoice.getSpecialInvoiceReversalEntity().setXhfDz(StringUtils.isBlank(specialInvoice.getSpecialInvoiceReversalEntity().getXhfDz()) ?
							commonSpecialInvoice.getSpecialInvoiceReversalEntity().getXhfDz() : specialInvoice.getSpecialInvoiceReversalEntity().getXhfDz());
					specialInvoice.getSpecialInvoiceReversalEntity().setXhfDh(StringUtils.isBlank(specialInvoice.getSpecialInvoiceReversalEntity().getXhfDh()) ?
							commonSpecialInvoice.getSpecialInvoiceReversalEntity().getXhfDh() : specialInvoice.getSpecialInvoiceReversalEntity().getXhfDh());
					specialInvoice.getSpecialInvoiceReversalEntity().setXhfYh(StringUtils.isBlank(specialInvoice.getSpecialInvoiceReversalEntity().getXhfYh()) ?
							commonSpecialInvoice.getSpecialInvoiceReversalEntity().getXhfYh() : specialInvoice.getSpecialInvoiceReversalEntity().getXhfYh());
					specialInvoice.getSpecialInvoiceReversalEntity().setXhfZh(StringUtils.isBlank(specialInvoice.getSpecialInvoiceReversalEntity().getXhfZh()) ?
							commonSpecialInvoice.getSpecialInvoiceReversalEntity().getXhfZh() : specialInvoice.getSpecialInvoiceReversalEntity().getXhfZh());

					/**
					 * 补全明细信息
					 */
					if (ConfigureConstant.STRING_Y.equals(specialInvoice.getSpecialInvoiceReversalEntity().getUseOldInvoiceData())) {

						BigDecimal hjbhsje = BigDecimal.ZERO;
						BigDecimal hjse = BigDecimal.ZERO;
						List<SpecialInvoiceReversalItem> specialInvoiceReversalItemEntityList = new ArrayList<>();
						for (int k = 0; k < commonSpecialInvoice.getSpecialInvoiceReversalItemEntities().size(); k++) {
							SpecialInvoiceReversalItem specialInvoiceReversalItemEntity = new SpecialInvoiceReversalItem();
							BeanUtils.copyProperties(commonSpecialInvoice.getSpecialInvoiceReversalItemEntities().get(k), specialInvoiceReversalItemEntity);
							specialInvoiceReversalItemEntity.setId(apiInvoiceCommonService.getGenerateShotKey());
							specialInvoiceReversalItemEntity.setSpecialInvoiceReversalId(specialInvoice.getSpecialInvoiceReversalEntity().getId());
							specialInvoiceReversalItemEntity.setSphxh(String.valueOf(k + 1));
							specialInvoiceReversalItemEntity.setCreateTime(DateUtil.parseDateTime(DateUtil.formatDateTime(new Date())));
							specialInvoiceReversalItemEntityList.add(specialInvoiceReversalItemEntity);
							hjbhsje = new BigDecimal(specialInvoiceReversalItemEntity.getXmje()).add(hjbhsje);
							hjse = new BigDecimal(specialInvoiceReversalItemEntity.getSe()).add(hjse);
						}

						specialInvoice.getSpecialInvoiceReversalEntity()
								.setHjbhsje(hjbhsje.setScale(ConfigureConstant.INT_2, RoundingMode.HALF_UP).toPlainString());
						specialInvoice.getSpecialInvoiceReversalEntity()
								.setHjse(hjse.setScale(ConfigureConstant.INT_2, RoundingMode.HALF_UP).toPlainString());
						specialInvoice.getSpecialInvoiceReversalEntity()
								.setKphjje(hjbhsje.add(hjse).setScale(ConfigureConstant.INT_2, RoundingMode.HALF_UP).toPlainString());
						specialInvoice.setSpecialInvoiceReversalItemEntities(specialInvoiceReversalItemEntityList);
					}

					specialInvoice.getSpecialInvoiceReversalEntity().setTksj(commonSpecialInvoice.getSpecialInvoiceReversalEntity().getTksj());

				} else if (statusCode.equals(ResponseStatusCodes.INVOICE_NOT_FOUND)) {
					if (ConfigureConstant.STRING_Y.equals(specialInvoice.getSpecialInvoiceReversalEntity().getUseOldInvoiceData())) {
						throw new OrderReceiveException(OrderInfoContentEnum.SPECIAL_INVOICE_IMPORT_VALIDATE_ERROR2);
					}

				}else{
					throw new OrderReceiveException(statusCode,statusMsg);
				}

			}
			//补充销方 购方信息
			if (SPECIAL_INVOICE_REASON_1100000000.getKey().equals(specialInvoice.getSpecialInvoiceReversalEntity().getSqsm())
					|| SPECIAL_INVOICE_REASON_1010000000.getKey().equals(specialInvoice.getSpecialInvoiceReversalEntity().getSqsm())) {
				specialInvoice.getSpecialInvoiceReversalEntity().setGhfMc(xhfMc);
				specialInvoice.getSpecialInvoiceReversalEntity().setGhfNsrsbh(xhfNsrsbh);
				specialInvoice.getSpecialInvoiceReversalEntity().setTksj(new Date());
				specialInvoice.getSpecialInvoiceReversalEntity().setGhfDz(xhfDz);
				specialInvoice.getSpecialInvoiceReversalEntity().setGhfDh(xhfDh);
				specialInvoice.getSpecialInvoiceReversalEntity().setGhfYh(xhfYh);
				specialInvoice.getSpecialInvoiceReversalEntity().setGhfZh(xhfZh);

			} else {
				specialInvoice.getSpecialInvoiceReversalEntity().setXhfMc(xhfMc);
				specialInvoice.getSpecialInvoiceReversalEntity().setXhfNsrsbh(xhfNsrsbh);
				specialInvoice.getSpecialInvoiceReversalEntity().setXhfDz(xhfDz);
				specialInvoice.getSpecialInvoiceReversalEntity().setXhfDh(xhfDh);
				specialInvoice.getSpecialInvoiceReversalEntity().setXhfYh(xhfYh);
				specialInvoice.getSpecialInvoiceReversalEntity().setXhfZh(xhfZh);
			}

			// 调用获取下一张发票的接口获取分机号和机器编码
			Map<String, String> map = getUninCode(prvCode, accessPointId, machineCode,
					specialInvoice.getSpecialInvoiceReversalEntity().getXhfNsrsbh(),
					terminalCode);
			prvCode = map.get(OrderManagementConstant.CODE);
			
			log.info("获取到的code:{}", prvCode);
			specialInvoice.getSpecialInvoiceReversalEntity().setKpr(drawerInfoEntity.getDrawerName());
			specialInvoice.getSpecialInvoiceReversalEntity().setFhr(drawerInfoEntity.getReCheckName());
			specialInvoice.getSpecialInvoiceReversalEntity().setSkr(drawerInfoEntity.getNameOfPayee());
			specialInvoice.getSpecialInvoiceReversalEntity().setNsrsbh(specialInvoice.getSpecialInvoiceReversalEntity().getXhfNsrsbh());
			specialInvoice.getSpecialInvoiceReversalEntity().setSqdscqqlsh(prvCode);
			specialInvoice.getSpecialInvoiceReversalEntity().setFjh(map.get("fjh"));
			specialInvoice.getSpecialInvoiceReversalEntity().setKpzt(OrderInfoEnum.SPECIAL_INVOICE_STATUS_0.getKey());
			specialInvoice.getSpecialInvoiceReversalEntity().setSld(accessPointId);
			specialInvoice.getSpecialInvoiceReversalEntity().setSldMc(accessPointName);
			specialInvoice.getSpecialInvoiceReversalEntity().setAgentName(userName);
		}
		return commonSpecialInvoiceList;
		
	}
	
	/**
	 * 获取 机器编号code 值
	 *
	 * @param prevCode
	 * @param accessPointId
	 * @param mechainCode
	 * @param nsrsbh
	 * @param terminalCode
	 * @return
	 * @throws InterruptedException
	 * @throws OrderReceiveException
	 */
	private Map<String, String> getUninCode(String prevCode, String accessPointId, String mechainCode, String nsrsbh,
	                                        String terminalCode) throws InterruptedException, OrderReceiveException {
		Map<String, String> resultMap = querySpecialInvoiceReversalCode(accessPointId, mechainCode,
				ORDER_INVOICE_TYPE_0.getKey(), nsrsbh, terminalCode);
		String code = resultMap.get(OrderManagementConstant.CODE);
		log.info("getUninCode获取到的code:{}", code);
		while (code.equals(prevCode)) {
			log.info("code重复重新获取code");
			Thread.sleep(1000);
			code = resultMap.get("mechainCode") + DateUtils.getYYYYMMDDHHMMSSFormatStr(new Date()).substring(2);
			log.info("重新获取到的code:{}",code);
		}
		resultMap.put(OrderManagementConstant.CODE, code);
		return resultMap;
	}

	@Override
	public boolean saveSpecialInvoiceInfo(List<CommonSpecialInvoice> commonSpecialInvoiceList) {
		boolean isSaveSuccess = true;
		for (CommonSpecialInvoice commonInvoice : commonSpecialInvoiceList) {
			Boolean processCommonSpecialInvoice = apiSpecialInvoiceReversalService.processCommonSpecialInvoice(false, commonInvoice);
			if (!processCommonSpecialInvoice) {
				return false;
			}
		}
		return isSaveSuccess;
	}
	
	@Override
	public void saveSpecialInvoiceRequest(HZSQDSC_RSP hzsqdscRsp, HZSQDSC_REQ hzsqdscReq, String sldid, String kpjh, String fplx, String fplb) {
		/**
		 *  根据申请流水号判断数据库中是否存在,如果存在就跳过操作,如果不存在就插入.
		 *
		 */
		log.debug("{}红字信息表上传保存数据请求数据为:{},返回数据为:{}", LOGGER_MSG, JsonUtils.getInstance().toJsonString(hzsqdscReq), JsonUtils.getInstance().toJsonString(hzsqdscRsp));
		Map<String, Object> responseMap = new HashMap<>(10);
		if (hzsqdscRsp != null && hzsqdscRsp.getHZSQDSCJG() != null && hzsqdscRsp.getHZSQDSCJG().size() > 0) {
			
			for (HZSQDSCJG hzsqdscjg : hzsqdscRsp.getHZSQDSCJG()) {
				if (hzsqdscjg != null) {
					
					responseMap.put(hzsqdscjg.getSQBSCQQLSH(), hzsqdscjg);
				}
			}
		}
		if (hzsqdscReq != null && hzsqdscReq.getHZSQDSCZXX().size() > 0) {
			
			for (int i = 0; i < hzsqdscReq.getHZSQDSCZXX().size(); i++) {
				HZSQDSCZXX hzsqdsczxx = hzsqdscReq.getHZSQDSCZXX().get(i);
				HZSQDSCJG hzsqdscjg = (HZSQDSCJG) responseMap.get(hzsqdsczxx.getHZSQDTXX().getSQBSCQQLSH());
				if (hzsqdscjg == null || StringUtils.isBlank(hzsqdscjg.getSQDH())) {
					log.warn("{}红字信息表上传保存数据失败,数据信息不全", LOGGER_MSG);
					return;
				}
				if (!SPECIAL_INVOICE_STATUS_TZD0000.getKey().equals(hzsqdscjg.getZTDM()) && !SPECIAL_INVOICE_STATUS_TZD1000.getKey().equals(hzsqdscjg.getZTDM()) && !SPECIAL_INVOICE_STATUS_TZD0500.getKey().equals(hzsqdscjg.getZTDM())) {
					log.warn("{}红字信息表上传保存数据失败,返回code不正确", LOGGER_MSG);
					return;
				}
				if (hzsqdsczxx.getHZSQDTXX() != null && hzsqdsczxx.getDDMXXX().size() > 0) {
					
					CommonSpecialInvoice commonSpecialInvoice = new CommonSpecialInvoice();
					SpecialInvoiceReversalEntity specialInvoiceReversalEntity = new SpecialInvoiceReversalEntity();
					HZSQDTXX hzsqdtxx = hzsqdsczxx.getHZSQDTXX();
					List<DDMXXX> ddmxxx = hzsqdsczxx.getDDMXXX();
					
					/**
					 * 查询数据库中是否存在数据
					 */
					SpecialInvoiceReversalEntity specialInvoiceReversalEntity1 = apiSpecialInvoiceReversalService.selectSpecialInvoiceReversalBySqdqqlsh(hzsqdtxx.getSQBSCQQLSH());
					
					if (specialInvoiceReversalEntity1 != null && StringUtils.isNotBlank(specialInvoiceReversalEntity1.getId())) {
						log.warn("{}红字信息表上传保存数据失败,数据已存在不再进行插入", LOGGER_MSG);
						return;
					}
					
					/**
					 * 入库数据转换
					 */
					String type = "0";
					if ("0000000090".equals(hzsqdtxx.getYYSBZ())) {
						type = "3";
					}
					specialInvoiceReversalEntity.setType(type);
					
					specialInvoiceReversalEntity.setSqdh(hzsqdscjg.getSQDH());
					specialInvoiceReversalEntity.setInvoiceType(fplx);
					specialInvoiceReversalEntity.setFpzlDm(fplb);
					specialInvoiceReversalEntity.setSqsm(hzsqdtxx.getSQSM());
					specialInvoiceReversalEntity.setYfpDm(("0000000000".equals(hzsqdtxx.getYFPDM()) ? "" : hzsqdtxx.getYFPDM()));
					specialInvoiceReversalEntity.setYfpHm(("00000000".equals(hzsqdtxx.getYFPHM()) ? "" : hzsqdtxx.getYFPHM()));
					specialInvoiceReversalEntity.setTksj(DateUtils.stringToDate(hzsqdtxx.getYFPKPRQ(), DateUtils.DATE_TIME_PATTERN));
					specialInvoiceReversalEntity.setXhfMc(hzsqdtxx.getXHFMC());
					String nsrsbh = hzsqdtxx.getXHFSBH();
					if (StringUtils.isBlank(nsrsbh)) {
						nsrsbh = hzsqdscReq.getHZSQDSCPC().getNSRSBH();
					}
					specialInvoiceReversalEntity.setXhfNsrsbh(nsrsbh);
					specialInvoiceReversalEntity.setGhfMc(hzsqdtxx.getGMFMC());
					specialInvoiceReversalEntity.setGhfNsrsbh(hzsqdtxx.getGMFSBH());
					specialInvoiceReversalEntity.setGhfqylx("01");
					specialInvoiceReversalEntity.setHjbhsje(hzsqdtxx.getHJJE());
					specialInvoiceReversalEntity.setHjse(hzsqdtxx.getHJSE());
					specialInvoiceReversalEntity.setKphjje(new BigDecimal(hzsqdtxx.getHJJE()).add(new BigDecimal(hzsqdtxx.getHJSE())).toString());
					specialInvoiceReversalEntity.setXxbbh(hzsqdscjg.getXXBBH());
					specialInvoiceReversalEntity.setStatusCode(hzsqdscjg.getZTDM());
					specialInvoiceReversalEntity.setStatusMessage(hzsqdscjg.getZTXX());
					specialInvoiceReversalEntity.setNsrsbh(hzsqdscReq.getHZSQDSCPC().getNSRSBH());
					specialInvoiceReversalEntity.setCreatorId(ConfigureConstant.STRING_0);
					specialInvoiceReversalEntity.setCreatorName(ConfigureConstant.STRING_SYSTEM);
					specialInvoiceReversalEntity.setEditorId(ConfigureConstant.STRING_0);
					specialInvoiceReversalEntity.setEditorName(ConfigureConstant.STRING_SYSTEM);
					specialInvoiceReversalEntity.setKpr("");
					specialInvoiceReversalEntity.setSld(sldid);
					specialInvoiceReversalEntity.setFjh(kpjh);
					specialInvoiceReversalEntity.setId(hzsqdtxx.getSQBSCQQLSH());
					if (StringUtils.isBlank(specialInvoiceReversalEntity.getStatusCode())) {
						specialInvoiceReversalEntity.setStatusCode("TZD0500");
					}
					specialInvoiceReversalEntity.setKpzt(ConfigureConstant.STRING_0);
					specialInvoiceReversalEntity.setCreateTime(DateUtils.stringToDate(hzsqdtxx.getTKSJ(), DateUtils.DATE_TIME_PATTERN_NOSPLIT));
					specialInvoiceReversalEntity.setUpdateTime(DateUtils.getDate(new Date(), DateUtils.DATE_TIME_PATTERN));
					
					List<SpecialInvoiceReversalItem> specialInvoiceReversalItemEntityList = new ArrayList<>();
					String taxRate = "";
					for (int k = 0; k < ddmxxx.size(); k++) {
						DDMXXX ddmxxx1 = ddmxxx.get(k);

						SpecialInvoiceReversalItem specialInvoiceReversalItem = new SpecialInvoiceReversalItem();
						specialInvoiceReversalItem.setSpecialInvoiceReversalId(specialInvoiceReversalEntity.getId());
						specialInvoiceReversalItem.setSpbm(ddmxxx1.getSPBM());
						specialInvoiceReversalItem.setXmmc(ddmxxx1.getXMMC());
						specialInvoiceReversalItem.setGgxh(ddmxxx1.getGGXH());
						specialInvoiceReversalItem.setXmdw(ddmxxx1.getDW());
						specialInvoiceReversalItem.setXmsl(String.valueOf(ddmxxx1.getSPSL()));
						specialInvoiceReversalItem.setXmdj(String.valueOf(ddmxxx1.getDJ()));
						specialInvoiceReversalItem.setXmje(String.valueOf(ddmxxx1.getJE()));
						specialInvoiceReversalItem.setSl(String.valueOf(ddmxxx1.getSL()));
						specialInvoiceReversalItem.setSe(String.valueOf(ddmxxx1.getSE()));
						specialInvoiceReversalItem.setHsbz(ddmxxx1.getHSBZ());
						specialInvoiceReversalItem.setYhzcbs(StringUtils.isNotBlank(ddmxxx1.getYHZCBS())
										? ddmxxx1.getYHZCBS() : OrderInfoEnum.YHZCBS_0.getKey());
						specialInvoiceReversalItem.setLslbs(StringUtils.isNotBlank(ddmxxx1.getLSLBS())
										? ddmxxx1.getLSLBS() : "");
						specialInvoiceReversalItem.setSphxh(String.valueOf(k + 1));
						if (StringUtils.isBlank(specialInvoiceReversalItem.getYhzcbs())) {
							specialInvoiceReversalItem.setYhzcbs(OrderInfoEnum.YHZCBS_0.getKey());
						}
						specialInvoiceReversalItem.setId(apiInvoiceCommonService.getGenerateShotKey());
						specialInvoiceReversalItem.setCreateTime(DateUtils.getDate(new Date(), DateUtils.DATE_TIME_PATTERN));
						if (i == 0) {
							taxRate = specialInvoiceReversalItem.getSl();
						} else if (!taxRate.equals(specialInvoiceReversalItem.getSl()) && !"多税率".equals(taxRate)) {
							taxRate = "多税率";
						}
						specialInvoiceReversalItemEntityList.add(specialInvoiceReversalItem);
					}
					specialInvoiceReversalEntity.setDslbz(taxRate);
					specialInvoiceReversalEntity.setScfgStatus(ConfigureConstant.STRING_2);
					commonSpecialInvoice.setSpecialInvoiceReversalEntity(specialInvoiceReversalEntity);
					commonSpecialInvoice.setSpecialInvoiceReversalItemEntities(specialInvoiceReversalItemEntityList);
					
					Boolean processCommonSpecialInvoice = apiSpecialInvoiceReversalService.processCommonSpecialInvoice(false, commonSpecialInvoice);
					if (!processCommonSpecialInvoice) {
						log.warn("{}红字信息表上传保存数据失败,插入数据库失败", LOGGER_MSG);
						return;
					}
					
				}
				
				
			}
			
			
		}
	}
	
	@Override
	public HpResponseBean downloadSpecialInvoiceReversalFg(HZSQDXZ_REQ hzsqdxzReq) {
		HpResponseBean hpResponseBean = new HpResponseBean();
		HpResponseExtend hpResponseExtend = new HpResponseExtend();
		hpResponseExtend.setSqbscqqpch(hzsqdxzReq.getSQBXZQQPCH());
		hpResponseExtend.setSuccess_COUNT(ConfigureConstant.STRING_0);
		List<ResponseHzfpsqbsc> responseHzfpsqbscList = new ArrayList<>();
		int count = ConfigureConstant.INT_0;
		/**
		 * 1.根据请求参数查询红字信息表下载表
		 * 2.如果未查询到数据,保存信息表请求数据,返回信息表下载中
		 * 3.如果查询到数据,并且数据表记录已经更新为全部获取成功
		 * 4.根据请求参数查询信息表数据,并返回结果
		 */
		
		/**
		 * 根据销方税号获取可用的设备信息
		 */
		//redis获取里面获取注册的税盘信息
		String registCodeStr = apiFangGeInterfaceService.getRegistCodeByRedis(hzsqdxzReq.getNSRSBH(), null);
		RegistrationCode registrationCode = JsonUtils.getInstance().parseObject(registCodeStr, RegistrationCode.class);
		
		SpecialInvoiceReversalDownloadEntity specialInvoiceReversalDownload = apiSpecialInvoiceReversalService.getSpecialInvoiceReversalDownload(hzsqdxzReq.getSQBXZQQPCH());
		if (ObjectUtil.isNull(specialInvoiceReversalDownload)) {
			//保存查询条件到数据库
			SpecialInvoiceReversalDownloadEntity downloadEntity = transSpecialInvoiceReversalDownload(hzsqdxzReq, registrationCode.getJqbh());
			boolean isSuccess = apiSpecialInvoiceReversalService.saveSpecialInvoiceReversalDownload(downloadEntity);
			hpResponseExtend.setStatusCode(OrderInfoContentEnum.CHECK_ISS7PRI_060112.getKey());
			hpResponseExtend.setStatusMessage(OrderInfoContentEnum.CHECK_ISS7PRI_060112.getMessage());
			
		} else {
			if (SPECIAL_INVOICE_DOWNLOAD_TYPE_1.getKey().equals(specialInvoiceReversalDownload.getDownStatus())) {
				
				hpResponseExtend.setStatusCode(OrderInfoContentEnum.CHECK_ISS7PRI_060112.getKey());
				hpResponseExtend.setStatusMessage(OrderInfoContentEnum.CHECK_ISS7PRI_060112.getMessage());
			} else if (SPECIAL_INVOICE_DOWNLOAD_TYPE_2.getKey().equals(specialInvoiceReversalDownload.getDownStatus())) {
				//根据参数查询下载成功的数据
				Map<String, Object> params = new HashMap<>(10);
				
				params.put("sqbscqqpch", hzsqdxzReq.getSQBXZQQPCH());
				
				params.put("limit", hzsqdxzReq.getGS());
				params.put("page", hzsqdxzReq.getYS());
				
				PageUtils pageUtils = apiSpecialInvoiceReversalService.querySpecialInvoiceReversals(params);
				List<SpecialInvoiceReversalEntity> list = (List<SpecialInvoiceReversalEntity>) pageUtils.getList();
				
				for (SpecialInvoiceReversalEntity specialInvoiceReversal : list) {
					//处理下载成功返回的数据
					//查询红字明细信息
					//List<SpecialInvoiceReversalItemEntity> specialInvoiceReversalItemEntitys =
					List<SpecialInvoiceReversalItem> specialInvoiceReversalItemEntitys = apiSpecialInvoiceReversalService.querySpecialInvoiceReversalItems(specialInvoiceReversal.getId());
					ResponseHzfpsqbsc responseHzfpsqbsc = transitionDownSpecialInvoiceRspFangGeV3(specialInvoiceReversal, specialInvoiceReversalItemEntitys);
					responseHzfpsqbscList.add(responseHzfpsqbsc);
					count++;
				}
				hpResponseExtend.setStatusCode(OrderInfoContentEnum.CHECK_ISS7PRI_060000.getKey());
				hpResponseExtend.setSuccess_COUNT(String.valueOf(count));
				hpResponseExtend.setStatusMessage(OrderInfoContentEnum.CHECK_ISS7PRI_060000.getMessage());
			}
		}
		
		
		if (StringUtils.isNotEmpty(registCodeStr)) {
			/**
			 * 存放下载信息到redis队列
			 */
			PushPayload pushPayload = new PushPayload();
			//发票下载税局
			pushPayload.setINTERFACETYPE(ConfigureConstant.STRING_3);
			pushPayload.setNSRSBH(registrationCode.getXhfNsrsbh());
			pushPayload.setJQBH(registrationCode.getJqbh());
			pushPayload.setZCM(registrationCode.getZcm());
			pushPayload.setSQBXZQQPCH(hzsqdxzReq.getSQBXZQQPCH());
			apiFangGeInterfaceService.saveMqttToRedis(pushPayload);
		}
		hpResponseBean.setCode(hpResponseExtend.getStatusCode());
		hpResponseBean.setMsg(hpResponseExtend.getStatusMessage());
		hpResponseBean.setResult(hpResponseExtend);
		
		return hpResponseBean;
	}

	@Override
	public R submitSpecialInvoiceReversal(String[] ids) {

		List<Map<String,String>> errorMsgArray = new ArrayList<>();

		//数据查询
		List<SpecialInvoiceReversalEntity> specialInvoiceReversals = apiSpecialInvoiceReversalService.querySpecialInvoiceReversalsByIds(ids, null);
		if(CollectionUtils.isEmpty(specialInvoiceReversals)){
			return R.error(ResponseStatusCodes.SPECIAL_INVOICE_REVERSAL_NOT_FOUND, "红字申请单信息未找到");
		}


		//数据校验
		List<SpecialInvoiceReversalEntity> specialResultList = new ArrayList<>();

		List<String> idList = new ArrayList<>();
		for (int i = 0; i < specialInvoiceReversals.size(); i++) {
			SpecialInvoiceReversalEntity specialInvoiceReversal = specialInvoiceReversals.get(i);
			
			if (!OrderInfoEnum.SPECIAL_INVOICE_STATUS_TZD0500.getKey().equals(specialInvoiceReversal.getStatusCode()) && !OrderInfoEnum.SPECIAL_INVOICE_STATUS_TZD9998.getKey().equals(specialInvoiceReversal.getStatusCode())) {
				Map<String, String> errorMsg = new HashMap<>(3);
				
				//TODO 此处修改提示信息根据上报编号
				errorMsg.put("submitode", specialInvoiceReversal.getStatusCode());
				errorMsg.put(OrderManagementConstant.CODE, ResponseStatusCodes.SPECIAL_INVOICE_REVERSAL_ALREADY_AUDITED_PRASSED);
				errorMsg.put(OrderManagementConstant.MESSAGE, "请选择未上传的申请单数据");
				errorMsgArray.add(errorMsg);
				continue;
			} else {
				specialResultList.add(specialInvoiceReversal);
				idList.add(specialInvoiceReversal.getId());
			}
		}


		if (specialResultList.size() > 0) {

			//查询所有的明细信息
			List<SpecialInvoiceReversalItem> items = apiSpecialInvoiceReversalService.querySpecialInvoiceReversalItemsBySirIds(ArrayUtils.toStringArray(idList.toArray()));
			Map<String, List<SpecialInvoiceReversalItem>> itemsMap = new HashMap<>(5);
            //根据id对明细信息分组
			for (SpecialInvoiceReversalItem item : items) {
				List<SpecialInvoiceReversalItem> sirItems = null;

				if (CollectionUtils.isEmpty(itemsMap.get(item.getSpecialInvoiceReversalId()))) {
					sirItems = new ArrayList<>();
				} else {
					sirItems = itemsMap.get(item.getSpecialInvoiceReversalId());
				}
				sirItems.add(item);
				itemsMap.put(item.getSpecialInvoiceReversalId(), sirItems);
			}

			//循环调用红字申请单接口
			for (SpecialInvoiceReversalEntity specialInvoiceReversal : specialInvoiceReversals) {

				specialInvoiceReversal.setEditorId(userInfoService.getUser().getUserId().toString());
				Map<String,String> respResult = submitSpecialInvoiceReversal(specialInvoiceReversal, itemsMap.get(specialInvoiceReversal.getId()));
				log.info("红字申请单上传结果{}", JsonUtils.getInstance().toJsonString(respResult));

				if (null != respResult && !OrderInfoContentEnum.SUCCESS.getKey().equals(respResult.get(OrderManagementConstant.CODE))) {
					errorMsgArray.add(respResult);
				}
			}
		}
		return R.ok().put(OrderManagementConstant.DATA,errorMsgArray);

	}


	/**
	 * 红字申请单撤销
	 * @param id
	 * @param xhfNsrsbh
	 * @return
	 */
	@Override
	public R revoke(String id, String xhfNsrsbh) {

		SpecialInvoiceReversalEntity specialInvoiceReversalEntity = apiSpecialInvoiceReversalService.querySpecialInvoiceReversal(id);
		if(specialInvoiceReversalEntity == null){
			return R.error().put(OrderManagementConstant.MESSAGE,"没有查询到数据");
		}

		if(!OrderInfoEnum.SPECIAL_INVOICE_STATUS_0.getKey().equals(specialInvoiceReversalEntity.getKpzt())){
			return R.error().put(OrderManagementConstant.MESSAGE,"信息表编号:" + specialInvoiceReversalEntity.getXxbbh() + "已开具或开具中，不支持撤销!");

		}

		String nsrsbh = "";

		//购方申请传购方税号 销方申请传销方税号
		if(OrderInfoEnum.SPECIAL_INVOICE_REASON_1100000000.getKey().equals(specialInvoiceReversalEntity.getSqsm()) ||
				OrderInfoEnum.SPECIAL_INVOICE_REASON_1010000000.getKey().equals(specialInvoiceReversalEntity.getSqsm())){
			nsrsbh = specialInvoiceReversalEntity.getGhfNsrsbh();
		}else{
			nsrsbh = xhfNsrsbh;
		}

		String termianlCode = apiTaxEquipmentService.getTerminalCode(nsrsbh);

		if(!OrderInfoEnum.TAX_EQUIPMENT_A9.getKey().equals(termianlCode) && !OrderInfoEnum.TAX_EQUIPMENT_C48.getKey().equals(termianlCode)){
			return R.error().put(OrderManagementConstant.MESSAGE, "税控盘及税控ukey暂不能撤销红字信息表");

		}

		if(!OrderInfoEnum.SPECIAL_INVOICE_STATUS_TZD0000.getKey().equals(specialInvoiceReversalEntity.getStatusCode()) && OrderInfoEnum.SPECIAL_INVOICE_STATUS_TZD1000.getKey().equals(specialInvoiceReversalEntity.getStatusCode())
				&& OrderInfoEnum.SPECIAL_INVOICE_STATUS_TZD0500.getKey().equals(specialInvoiceReversalEntity.getStatusCode())){
			return R.error().put(OrderManagementConstant.MESSAGE,"审核未通过，不能撤销!");

		}


		RedInvoiceRevokeRequest request = new RedInvoiceRevokeRequest();
		request.setXXBBH(specialInvoiceReversalEntity.getXxbbh());
		request.setNSRSBH(nsrsbh);

		request.setTerminalCode(termianlCode);
		ResponseBaseBean responseBaseBean = HttpInvoiceRequestUtil.redInvoiceRevoke(OpenApiConfig.redInvoiceRevoke,request);

		if(OrderInfoContentEnum.SUCCESS.getKey().equals(responseBaseBean.getCode())){
            //更新数据库状态已撤销
			SpecialInvoiceReversalEntity specialEntity = new SpecialInvoiceReversalEntity();
			specialEntity.setId(specialInvoiceReversalEntity.getId());
			specialEntity.setStatusCode(OrderInfoEnum.SPECIAL_INVOICE_STATUS_TZD0082.getKey());
			specialEntity.setStatusMessage(OrderInfoEnum.SPECIAL_INVOICE_STATUS_TZD0082.getValue());
			apiSpecialInvoiceReversalService.updateSpecialInvoiceReversal(specialEntity);
			//撤销成功  推送信息表状态 0：已撤销
			pushService.pushHZXXBtatus(specialInvoiceReversalEntity.getXxbbh(),specialInvoiceReversalEntity.getXhfNsrsbh(),specialInvoiceReversalEntity.getGhfNsrsbh(),"0");
			return R.ok().put(OrderManagementConstant.MESSAGE,responseBaseBean.getMsg());
		}else{
			return R.error().put(OrderManagementConstant.MESSAGE, "信息表编号:" + specialInvoiceReversalEntity.getXxbbh() + "," + "撤销失败!");
		}
	}


	private SpecialInvoiceReversalDownloadEntity transSpecialInvoiceReversalDownload(HZSQDXZ_REQ hzsqdxzReq, String jqbh) {
		SpecialInvoiceReversalDownloadEntity entity = new SpecialInvoiceReversalDownloadEntity();
		entity.setId(apiInvoiceCommonService.getGenerateShotKey());
		entity.setSqbxzqqpch(hzsqdxzReq.getSQBXZQQPCH());
		entity.setNsrsbh(hzsqdxzReq.getNSRSBH());
		entity.setKpjh(jqbh);
		entity.setFpzldm(ORDER_INVOICE_TYPE_0.getKey());
		entity.setSldid(jqbh);
		entity.setTkrqQ(hzsqdxzReq.getTKRQQ());
		entity.setTkrqZ(hzsqdxzReq.getTKRQZ());
		entity.setGmfNsrsbh(hzsqdxzReq.getGMFSBH());
		entity.setXsfNsrsbh(hzsqdxzReq.getXHFSBH());
		entity.setXxbbh(hzsqdxzReq.getXXBBH());
		entity.setXxbfw(hzsqdxzReq.getXXBFW());
		entity.setPageno(hzsqdxzReq.getGS());
		entity.setPagesize(hzsqdxzReq.getYS());
		entity.setDownStatus(OrderInfoEnum.SPECIAL_INVOICE_DOWNLOAD_TYPE_0.getKey());
		entity.setCreateTime(new Date());
		return entity;
	}
	
	
	public static ResponseHzfpsqbsc transitionDownSpecialInvoiceRspFangGeV3(SpecialInvoiceReversalEntity specialInvoiceReversal, List<SpecialInvoiceReversalItem> itemList) {
		
		ResponseHzfpsqbsc responseHzfpsqbsc = new ResponseHzfpsqbsc();
		List<Commoninvdetail> commoninvdetailArrayList = new ArrayList<>();
		
		responseHzfpsqbsc.setSqdh(specialInvoiceReversal.getSqdh());
		responseHzfpsqbsc.setXxbbh(specialInvoiceReversal.getXxbbh());
		responseHzfpsqbsc.setStatus_CODE(specialInvoiceReversal.getStatusCode());
		responseHzfpsqbsc.setStatus_MESSAGE(specialInvoiceReversal.getStatusMessage());
		responseHzfpsqbsc.setYfp_DM(specialInvoiceReversal.getYfpDm());
		responseHzfpsqbsc.setYfp_HM(specialInvoiceReversal.getYfpHm());
		responseHzfpsqbsc.setFplx(specialInvoiceReversal.getInvoiceType());
		responseHzfpsqbsc.setFplb(specialInvoiceReversal.getFpzlDm());
		responseHzfpsqbsc.setTksj(DateUtils.format(specialInvoiceReversal.getCreateTime(), DateUtils.DATE_TIME_PATTERN_NOSPLIT));
		responseHzfpsqbsc.setXsf_NSRSBH(specialInvoiceReversal.getXhfNsrsbh());
		responseHzfpsqbsc.setXsf_MC(specialInvoiceReversal.getXhfMc());
		responseHzfpsqbsc.setGmf_NSRSBH(specialInvoiceReversal.getGhfNsrsbh());
		responseHzfpsqbsc.setGmf_MC(specialInvoiceReversal.getGhfMc());
		responseHzfpsqbsc.setHjje(specialInvoiceReversal.getHjbhsje());
		responseHzfpsqbsc.setHjse(specialInvoiceReversal.getHjse());
		responseHzfpsqbsc.setSqsm(specialInvoiceReversal.getSqsm());
		
		String sl = itemList.get(0).getSl();
		String dslbz = "";
		if (StringUtils.isEmpty(sl)) {
			dslbz = "1";
		}
		String fphxz = "0";
		if (itemList.size() == 1 && itemList.get(0).getXmmc().equals(ConfigureConstant.XJZSXHQD)) {
			fphxz = "6";
		}
		for (SpecialInvoiceReversalItem itemEntity : itemList) {
			Commoninvdetail commoninvdetail = new Commoninvdetail();
			commoninvdetail.setXmxh(itemEntity.getSphxh());
			commoninvdetail.setSpbm(itemEntity.getSpbm());
			commoninvdetail.setFphxz(fphxz);
			commoninvdetail.setYhzcbs(itemEntity.getYhzcbs());
			commoninvdetail.setLslbs(itemEntity.getLslbs());
			commoninvdetail.setZzstsgl(itemEntity.getZzstsgl());
			commoninvdetail.setXmmc(itemEntity.getXmmc());
			commoninvdetail.setGgxh(itemEntity.getGgxh());
			commoninvdetail.setDw(itemEntity.getXmdw());
			commoninvdetail.setXmsl(itemEntity.getXmsl());
			commoninvdetail.setXmdj(itemEntity.getXmdj());
			commoninvdetail.setXmje(itemEntity.getXmje());
			commoninvdetail.setHsbz(itemEntity.getHsbz());
			if (StringUtils.isEmpty(dslbz)) {
				if (!sl.equals(itemEntity.getSl())) {
					dslbz = "1";
				}
			}
			commoninvdetail.setSl(itemEntity.getSl());
			commoninvdetail.setSe(itemEntity.getSe());
		}
		responseHzfpsqbsc.setCommoninvdetails(commoninvdetailArrayList);
		
		return responseHzfpsqbsc;
	}
}