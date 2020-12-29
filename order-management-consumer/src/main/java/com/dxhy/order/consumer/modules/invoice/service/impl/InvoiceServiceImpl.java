package com.dxhy.order.consumer.modules.invoice.service.impl;

import cn.hutool.core.util.ObjectUtil;
import com.dxhy.order.api.*;
import com.dxhy.order.constant.*;
import com.dxhy.order.consumer.config.OpenApiConfig;
import com.dxhy.order.consumer.model.page.PageSld;
import com.dxhy.order.consumer.modules.fiscal.service.UnifyService;
import com.dxhy.order.consumer.modules.invoice.service.InvoiceService;
import com.dxhy.order.consumer.modules.order.service.IGenerateReadyOpenOrderService;
import com.dxhy.order.consumer.modules.order.service.IOrderInfoService;
import com.dxhy.order.consumer.modules.order.service.MakeOutAnInvoiceService;
import com.dxhy.order.consumer.utils.BeanTransitionUtils;
import com.dxhy.order.exceptions.OrderReceiveException;
import com.dxhy.order.model.*;
import com.dxhy.order.model.a9.kp.CommonInvoiceStatus;
import com.dxhy.order.model.a9.kp.InvoiceQuery;
import com.dxhy.order.model.dto.PushPayload;
import com.dxhy.order.model.mqdata.FpkjMqData;
import com.dxhy.order.utils.HttpInvoiceRequestUtil;
import com.dxhy.order.utils.JsonUtils;
import com.dxhy.order.utils.PriceTaxSeparationUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.dubbo.config.annotation.Reference;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.*;

/**
 * @author 杨士勇
 */
@Service
@Slf4j
public class InvoiceServiceImpl implements InvoiceService {
    
    private static final String LOGGER_MSG = "(发票数据业务实现类)";
	
	@Resource
	private MakeOutAnInvoiceService makeOutAnInvoiceService;
	
	@Reference
	private ApiOrderInfoService apiOrderInfoService;
	
	@Reference
	private ApiTaxEquipmentService apiTaxEquipmentService;
	
	@Resource
	private UnifyService unifyService;
	
	@Resource
	private IGenerateReadyOpenOrderService generateReadyOpenOrderService;
	
	@Resource
	private IOrderInfoService orderInfoService;
	
	@Reference
	private RedisService redisService;
	
	@Reference
	private ApiOrderProcessService apiOrderProcessService;
	
	@Reference
	private ApiOrderInvoiceInfoService apiOrderInvoiceInfoService;
	
	@Reference
	private OpenInvoiceService openInvoiceService;
	
	@Reference
	private ApiFangGeInterfaceService apiFangGeInterfaceService;
	
	@Override
	public R batchInvoiceById(String[] paperArray, String[] specialArray, String[] eleArray, Map<String, PageSld> map, String userId, List<String> shList) {
		
		/**
		 * 数据查询分组
		 */
		List<CommonOrderInfo> commonOrderInfos = queryInvocieAndGroupBy(paperArray, specialArray, eleArray, shList);
		
		
		return batchInvoice(commonOrderInfos, map, userId, shList);
	}
	
	
	/**
     * @param @param commonList
     * @return void
     * @throws
     * @Title : R
     * @Description ：发票开具
     */
	@Override
	public R batchInvoice(List<CommonOrderInfo> commonList, Map<String, PageSld> map, String uid, List<String> shList) {
		
		List<R> resultList = new ArrayList<>();
		String uuid = UUID.randomUUID().toString();
		try {
			
			//根据发票请求流水号添加redis同步锁
			for (CommonOrderInfo comm : commonList) {
				
				if (!redisService.setNx(comm.getOrderInfo().getFpqqlsh(), uuid)) {
					return R.error().put(OrderManagementConstant.CODE, "9999")
							.put(OrderManagementConstant.MESSAGE, "流水号" + comm.getOrderInfo().getFpqqlsh() + "发票正在开具中，请勿重复开具!");
				} else {
					redisService.expire(comm.getOrderInfo().getFpqqlsh(), 300);
				}
				
			}
			
			/**
			 * 根据税号发票种类代码分组
			 */
			List<CommonOrderInfo> commonOrderInfos = new ArrayList<>();
			
			/**
			 * 数据补全 拆分 合并 开票数据校验
			 */
			try {
				commonOrderInfos = completeOrderInvoiceInfo(commonList, uid);
			} catch (OrderSplitException e) {
				return R.error().put(OrderManagementConstant.CODE, e.getCode())
						.put(OrderManagementConstant.MESSAGE, e.getMessage());
			}
			
			/**
			 *  调用发票开具接口
			 */
			
			resultList = kpInvoice(commonOrderInfos, map);
		} catch (Exception e) {
			log.error("发票开具异常，异常信息为:{}",e);
		} finally {
			//业务完成后删除redis中的key值
			for(CommonOrderInfo common : commonList){
				if(uuid.equals(redisService.get(common.getOrderInfo().getFpqqlsh()))){
					redisService.del(common.getOrderInfo().getFpqqlsh());
					
				}
			}
		}
		
        /**
         * 处理返回结果数据
         */
		
		return dealReturnResult(resultList);
		
	}
	

	private R dealReturnResult(List<R> resultList) {

		int allSuccessCount = 0;
		int allFaildCount = 0;
		int eleSuccessCount = 0;
		int eleFaildCount = 0;
		int paperSuccessCount = 0;
		int paperFaildCount = 0;
		int specialSuccessCount = 0;
		int specialFaildCount = 0;

		for (R r : resultList) {
            if (OrderInfoContentEnum.SUCCESS.getKey().equals(r.get(OrderManagementConstant.CODE))) {
                allSuccessCount += Integer.parseInt(r.get("successCount").toString());
                if (OrderInfoEnum.ORDER_INVOICE_TYPE_0.getKey().equals(String.valueOf(r.get("fpzldm")))) {
	                specialSuccessCount += Integer.parseInt(r.get("successCount").toString());
                } else if (OrderInfoEnum.ORDER_INVOICE_TYPE_2.getKey().equals(String.valueOf(r.get("fpzldm")))) {
	                paperSuccessCount += Integer.parseInt(r.get("successCount").toString());
                } else if (OrderInfoEnum.ORDER_INVOICE_TYPE_51.getKey().equals(String.valueOf(r.get("fpzldm")))) {
	                eleSuccessCount += Integer.parseInt(r.get("successCount").toString());
                }
            } else {
	            allFaildCount += Integer.parseInt(r.get("faildCount").toString());
                if (OrderInfoEnum.ORDER_INVOICE_TYPE_0.getKey().equals(String.valueOf(r.get("fpzldm")))) {
	                specialFaildCount = Integer.parseInt(r.get("faildCount").toString());
                } else if (OrderInfoEnum.ORDER_INVOICE_TYPE_2.getKey().equals(String.valueOf(r.get("fpzldm")))) {
	                paperFaildCount += Integer.parseInt(r.get("faildCount").toString());
                } else if (OrderInfoEnum.ORDER_INVOICE_TYPE_51.getKey().equals(String.valueOf(r.get("fpzldm")))) {
	                eleFaildCount += Integer.parseInt(r.get("faildCount").toString());
                }
            }
            
        }
        return R.ok().put(OrderManagementConstant.CODE, OrderInfoContentEnum.SUCCESS.getKey()).put(OrderManagementConstant.MESSAGE, OrderInfoContentEnum.SUCCESS.getMessage())
                .put("allSuccessCount", allSuccessCount).put("allFaildCount", allFaildCount).put("eleSuccessCount", eleSuccessCount)
                .put("eleFaildCount", eleFaildCount).put("paperSuccessCount", paperSuccessCount).put("paperFaildCount", paperFaildCount)
                .put("specialSuccessCount", specialSuccessCount).put("specialFaildCount", specialFaildCount).put("resultList", resultList);
    }
    
    /**
     *
     * @Title : groupByBatch
     * @Description ：999张发票一个批次
     * @param @param paramMap
     * @param @return
     * @return Map<String, List < CommonOrderInfo>>
     * @exception
	 *
	 */
	private Map<String, List<CommonOrderInfo>> groupByBatch(Map<String, List<CommonOrderInfo>> paramMap) {
		
		Map<String, List<CommonOrderInfo>> resultMap = new HashMap<>(5);
		for (Map.Entry<String, List<CommonOrderInfo>> entry : paramMap.entrySet()) {
			
			List<CommonOrderInfo> value = entry.getValue();
			if (value.size() > 999) {
				List<CommonOrderInfo> batchList = new ArrayList<>();
				int i = 1;
				int j = 0;
				for (CommonOrderInfo comm : value) {
					batchList.add(comm);
					if (i % 999 == 0) {
						j++;
						resultMap.put(entry.getKey() + j, batchList);
						batchList = new ArrayList<>();
					}
					i++;
				}
				if(batchList.size() > 0){
					j++;
					resultMap.put(entry.getKey() + j, batchList);
				}
				
			} else {
				resultMap.put(entry.getKey(), value);
			}
		}
		return resultMap;
	}
	
	
	private List<R> kpInvoice(List<CommonOrderInfo> commonOrderInfos, Map<String, PageSld> map) {
		
		List<R> resultList = new ArrayList<>();
		R makeOutAnInvoice = R.error();
		try {
			makeOutAnInvoice = makeOutAnInvoiceService.makeOutAnInovice(commonOrderInfos, map);
		} catch (Exception e) {
			log.error("{}调用开票接口异常,", LOGGER_MSG);
		}
		
		//判断发票是否接收成功
		/**
		 * 统计数组中专票数量,普票数量,电票数量
		 * todo 目前先按照请求数据类型进行统计,然后返回成功失败条数.
		 * 后续需要makeoutaninvoice接口支持批量开票,如果异常记录错误,跳过当前条,
		 */
		long dpCount = commonOrderInfos.stream().filter(commonOrderInfo -> OrderInfoEnum.ORDER_INVOICE_TYPE_51.getKey().equals(commonOrderInfo.getOrderInfo().getFpzlDm())).count();
		long ppCount = commonOrderInfos.stream().filter(commonOrderInfo -> OrderInfoEnum.ORDER_INVOICE_TYPE_2.getKey().equals(commonOrderInfo.getOrderInfo().getFpzlDm())).count();
		long zpCount = commonOrderInfos.stream().filter(commonOrderInfo -> OrderInfoEnum.ORDER_INVOICE_TYPE_0.getKey().equals(commonOrderInfo.getOrderInfo().getFpzlDm())).count();
		R fpzl51 = new R();
		R fpzl0 = new R();
		R fpzl2 = new R();
		fpzl2.put("fpzldm", OrderInfoEnum.ORDER_INVOICE_TYPE_2.getKey());
		fpzl51.put("fpzldm", OrderInfoEnum.ORDER_INVOICE_TYPE_51.getKey());
		fpzl0.put("fpzldm", OrderInfoEnum.ORDER_INVOICE_TYPE_0.getKey());
		if (OrderInfoContentEnum.SUCCESS.getKey().equals(makeOutAnInvoice.get(OrderManagementConstant.CODE))) {
			fpzl51.put("successCount", dpCount);
			fpzl51.put("faildCount", ConfigureConstant.INT_0);
			
			fpzl0.put("successCount", zpCount);
			fpzl0.put("faildCount", ConfigureConstant.INT_0);
			
			fpzl2.put("successCount", ppCount);
			fpzl2.put("faildCount", ConfigureConstant.INT_0);
		} else {
			
			fpzl51.put("faildCount", dpCount);
			fpzl51.put("successCount", ConfigureConstant.INT_0);
			fpzl51.put(OrderManagementConstant.CODE, makeOutAnInvoice.get(OrderManagementConstant.CODE));
			fpzl51.put(OrderManagementConstant.MESSAGE, makeOutAnInvoice.get(OrderManagementConstant.MESSAGE));
			
			
			fpzl0.put("faildCount", zpCount);
			fpzl0.put("successCount", ConfigureConstant.INT_0);
			fpzl0.put(OrderManagementConstant.CODE, makeOutAnInvoice.get(OrderManagementConstant.CODE));
			fpzl0.put(OrderManagementConstant.MESSAGE, makeOutAnInvoice.get(OrderManagementConstant.MESSAGE));
			
			fpzl2.put("faildCount", ppCount);
			fpzl2.put("successCount", ConfigureConstant.INT_0);
			fpzl2.put(OrderManagementConstant.CODE, makeOutAnInvoice.get(OrderManagementConstant.CODE));
			fpzl2.put(OrderManagementConstant.MESSAGE, makeOutAnInvoice.get(OrderManagementConstant.MESSAGE));
		}
		resultList.add(fpzl51);
		resultList.add(fpzl0);
		resultList.add(fpzl2);
		
		return resultList;
	}
	
	
	private List<CommonOrderInfo> completeOrderInvoiceInfo(List<CommonOrderInfo> commonOrderInfos1, String userId) throws OrderSplitException {
		
		List<CommonOrderInfo> splitCommonOrderInfoList = new ArrayList<>();
		// 生成待开的流程
		if (CollectionUtils.isNotEmpty(commonOrderInfos1)) {
			
			String fpqqlsh = "";
			try {
				// 补全订单信息
				generateReadyOpenOrderService.completeOrderInfo(commonOrderInfos1, userId);
				
				/**
				 * 循环处理单个订单数据,判断是否有超限额数据,如果有超限额数据需要进行保存
				 */
				/**
				 * 根据税号查询税控设备
				 */
				/**
				 * 特殊字符处理
				 */
				/**
				 * 超限额逻辑部分需要添加锁,控制单个数据重复超限额拆分多次.
				 */
				/**
				 * 如果超限额拆分完成后,未做超限额拆分需要走编辑流程,更新补全后数据
				 */
				/**
				 * 如果编辑成功,返回前端流水号,订单id和processId
				 */
				for (CommonOrderInfo commonOrderInfo : commonOrderInfos1) {
					/**
					 * 根据税号查询税控设备
					 */
					String terminalCode = apiTaxEquipmentService
							.getTerminalCode(commonOrderInfos1.get(0).getOrderInfo().getXhfNsrsbh());
					
					/**
					 * 特殊字符处理
					 */
					BeanTransitionUtils.replaceCharacter(commonOrderInfo);
					
					commonOrderInfo.setTerminalCode(terminalCode);
					
					// 补全订单信息
					List<CommonOrderInfo> commonOrderInfos = new ArrayList<>();
					commonOrderInfos.add(commonOrderInfo);
					
					// 订单超限额拆分
					fpqqlsh = commonOrderInfo.getOrderInfo().getFpqqlsh();
					/**
					 * 超限额逻辑部分需要添加锁,控制单个数据重复超限额拆分多次.
					 */
					List<CommonOrderInfo> orderSplit = generateReadyOpenOrderService.orderSplit(commonOrderInfos,
							commonOrderInfos.get(0).getTerminalCode(), userId);
					// 订单超限额拆分
					log.debug("数据拆分补全后的数据:{}", JsonUtils.getInstance().toJsonString(orderSplit));
					
					if (orderSplit.size() > 1) {
						// 补齐后的数据入库
						orderSplit = generateReadyOpenOrderService.saveOrderSplitInfo(orderSplit);
						splitCommonOrderInfoList.addAll(orderSplit);
						
					} else if (orderSplit.size() == 1) {
						/**
						 * 如果超限额拆分完成后,未做超限额拆分需要走编辑流程,更新补全后数据
						 */
						Map map;
						try {
							map = orderInfoService.updateOrderInfoAndOrderProcessInfo(commonOrderInfo);
						} catch (OrderReceiveException e) {
							log.error("{}订单编辑异常,异常原因为:{}", LOGGER_MSG, e);
							throw new OrderSplitException(e.getCode(), e.getMessage());
						}
						/**
						 * 如果编辑成功,返回前端流水号,订单id和processId
						 */
						if (!OrderInfoContentEnum.SUCCESS.getKey().equals(map.get(OrderManagementConstant.ERRORCODE))) {
							throw new OrderSplitException(map.get(OrderManagementConstant.ERRORCODE).toString(), map.get(OrderManagementConstant.ERRORMESSAGE).toString());
						}
						splitCommonOrderInfoList.addAll(orderSplit);
						
					}
					
				}
				
				
			} catch (OrderSplitException e) {
				log.error("拆分信息处理异常:{}", e.getMessage());
				/**
				 * 异常订单允许用户再次进行拆分操作
				 */
				throw new OrderSplitException(e.getCode(), e.getMessage());
			} catch (OrderReceiveException e) {
				log.error("补全信息处理异常:{}", e.getMessage());
				/**
				 * 异常订单允许用户再次进行拆分操作
				 */
				throw new OrderSplitException(e.getCode(), e.getMessage());
			}
			
		}
		
		return splitCommonOrderInfoList;
	}
    
    
    /**
     * @Title : queryInvocieAndGroupBy
     * @Description ：数据查询分组
     * @param @param paperArray
     * @param @param specialArray
     * @param @param eleArray
     * @param @return
     * @return Map<String, List < CommonOrderInfo>>
     * @exception
	 *
	 */
	private List<CommonOrderInfo> queryInvocieAndGroupBy(String[] paperArray, String[] specialArray,
	                                                     String[] eleArray, List<String> shList) {
		//获取所有需要开票的数据
		List<String> orderIdList = new ArrayList<>();
		
		// 查询专票并分组
		if (specialArray != null && specialArray.length > 0) {
			List<String> strings = Arrays.asList(specialArray);
			orderIdList.addAll(strings);
		}
		
		// 查询纸票并分组
		if (paperArray != null && paperArray.length > 0) {
			List<String> strings = Arrays.asList(paperArray);
			orderIdList.addAll(strings);
			
		}
		
		// 查询电票 并分组
		if (eleArray != null && eleArray.length > 0) {
			List<String> strings = Arrays.asList(eleArray);
			orderIdList.addAll(strings);
			
		}
		
		return apiOrderInfoService.batchQueryOrderInfoByOrderIds(orderIdList, shList);
    
    }
    
	/**
	 * 编辑后数据补全后开票
	 */
	@Override
	public R updateAndInvoice(List<CommonOrderInfo> paramList, Map<String, PageSld> sldMap, String uid, List<String> shList) {
		
		/**
		 * 数据查询 补全编辑后的订单数据
		 */
		queryAndComplete(paramList, shList);
		/**
		 * 编辑后的订单价税分离
		 */
		for (CommonOrderInfo commonOrder : paramList) {
			try {
				commonOrder = PriceTaxSeparationUtil.taxSeparationService(commonOrder, new TaxSeparateConfig());
			} catch (OrderSeparationException e) {
				log.error("订单价税分离异常:{}", e.getMessage());
				return R.error().put(OrderManagementConstant.CODE, e.getCode()).put(OrderManagementConstant.MESSAGE, e.getMessage());
			}
		}
		/**
		 * 数据补全 拆分 合并 开票数据校验
		 */
		return batchInvoice(paramList, sldMap, uid, shList);
    }
    
    
    /**
     * @param paramList
     * @return void
     * @throws
     * @Title : queryAndComplete
     * @Description ：编辑后的订单数据补全
	 */
	private void queryAndComplete(List<CommonOrderInfo> paramList, List<String> shList) {
		
		for (CommonOrderInfo common : paramList) {
			
			OrderInfo orderInfo = common.getOrderInfo();
			//补全编辑后的明细数据
			if (StringUtils.isNotBlank(orderInfo.getId())) {
				OrderInfo selectOrderInfoByOrderId = apiOrderInfoService.selectOrderInfoByOrderId(orderInfo.getId(), shList);
				orderInfo.setId(StringUtils.isBlank(orderInfo.getId()) ? selectOrderInfoByOrderId.getId() : orderInfo.getId());
				orderInfo.setProcessId(StringUtils.isBlank(orderInfo.getProcessId()) ? selectOrderInfoByOrderId.getProcessId() : orderInfo.getProcessId());
				orderInfo.setFpqqlsh(StringUtils.isBlank(orderInfo.getFpqqlsh()) ? selectOrderInfoByOrderId.getFpqqlsh() : orderInfo.getFpqqlsh());
				
				orderInfo.setDdh(StringUtils.isBlank(orderInfo.getDdh()) ? selectOrderInfoByOrderId.getDdh() : orderInfo.getDdh());
				orderInfo.setThdh(StringUtils.isBlank(orderInfo.getThdh()) ? selectOrderInfoByOrderId.getThdh() : orderInfo.getThdh());
				orderInfo.setDdlx(StringUtils.isBlank(orderInfo.getDdlx()) ? selectOrderInfoByOrderId.getDdlx() : orderInfo.getDdlx());
				orderInfo.setDsptbm(StringUtils.isBlank(orderInfo.getDsptbm()) ? selectOrderInfoByOrderId.getDsptbm() : orderInfo.getDsptbm());
				orderInfo.setNsrsbh(StringUtils.isBlank(orderInfo.getNsrsbh()) ? selectOrderInfoByOrderId.getNsrsbh() : orderInfo.getNsrsbh());
				orderInfo.setNsrmc(StringUtils.isBlank(orderInfo.getNsrmc()) ? selectOrderInfoByOrderId.getNsrmc() : orderInfo.getNsrmc());
				orderInfo.setNsrdzdah(StringUtils.isBlank(orderInfo.getNsrdzdah()) ? selectOrderInfoByOrderId.getNsrdzdah(): orderInfo.getNsrdzdah());
				orderInfo.setSwjgDm(StringUtils.isBlank(orderInfo.getSwjgDm()) ? selectOrderInfoByOrderId.getSwjgDm(): orderInfo.getSwjgDm());
				orderInfo.setDkbz(StringUtils.isBlank(orderInfo.getDkbz()) ? selectOrderInfoByOrderId.getDkbz(): orderInfo.getDkbz());
				orderInfo.setPydm(StringUtils.isBlank(orderInfo.getPydm()) ? selectOrderInfoByOrderId.getPydm(): orderInfo.getPydm());
				orderInfo.setKpxm(StringUtils.isBlank(orderInfo.getKpxm()) ? selectOrderInfoByOrderId.getKpxm(): orderInfo.getKpxm());
				orderInfo.setBbmBbh(StringUtils.isBlank(orderInfo.getBbmBbh()) ? selectOrderInfoByOrderId.getBbmBbh(): orderInfo.getBbmBbh());
				orderInfo.setXhfMc(StringUtils.isBlank(orderInfo.getXhfMc()) ? selectOrderInfoByOrderId.getXhfMc(): orderInfo.getXhfMc());
				orderInfo.setXhfNsrsbh(StringUtils.isBlank(orderInfo.getXhfNsrsbh()) ? selectOrderInfoByOrderId.getXhfNsrsbh(): orderInfo.getXhfNsrsbh());
				orderInfo.setXhfDz(StringUtils.isBlank(orderInfo.getXhfDz()) ? selectOrderInfoByOrderId.getXhfDz(): orderInfo.getXhfDz());
				orderInfo.setXhfDh(StringUtils.isBlank(orderInfo.getXhfDh()) ? selectOrderInfoByOrderId.getXhfDh(): orderInfo.getXhfDh());
				orderInfo.setXhfYh(StringUtils.isBlank(orderInfo.getXhfYh()) ? selectOrderInfoByOrderId.getXhfYh(): orderInfo.getXhfYh());
				orderInfo.setXhfZh(StringUtils.isBlank(orderInfo.getXhfZh()) ? selectOrderInfoByOrderId.getXhfZh(): orderInfo.getXhfZh());
				orderInfo.setGhfQylx(StringUtils.isBlank(orderInfo.getGhfQylx()) ? selectOrderInfoByOrderId.getGhfQylx(): orderInfo.getGhfQylx());
				orderInfo.setGhfSf(StringUtils.isBlank(orderInfo.getGhfSf()) ? selectOrderInfoByOrderId.getGhfSf(): orderInfo.getGhfSf());
				orderInfo.setGhfId(StringUtils.isBlank(orderInfo.getGhfId()) ? selectOrderInfoByOrderId.getGhfId(): orderInfo.getGhfId());
				
				orderInfo.setHyDm(StringUtils.isBlank(orderInfo.getHyDm()) ? selectOrderInfoByOrderId.getHyDm(): orderInfo.getHyDm());
				orderInfo.setHyMc(StringUtils.isBlank(orderInfo.getHyMc()) ? selectOrderInfoByOrderId.getHyMc(): orderInfo.getHyMc());
				
				orderInfo.setDdrq(orderInfo.getDdrq());
				orderInfo.setKplx(StringUtils.isBlank(orderInfo.getKplx()) ? selectOrderInfoByOrderId.getKplx(): orderInfo.getKplx());
				orderInfo.setYfpDm(StringUtils.isBlank(orderInfo.getYfpDm()) ? selectOrderInfoByOrderId.getYfpDm(): orderInfo.getYfpDm());
				orderInfo.setYfpHm(StringUtils.isBlank(orderInfo.getYfpHm()) ? selectOrderInfoByOrderId.getYfpHm(): orderInfo.getYfpHm());
				orderInfo.setChyy(StringUtils.isBlank(orderInfo.getChyy()) ? selectOrderInfoByOrderId.getChyy(): orderInfo.getChyy());
				orderInfo.setTschbz(StringUtils.isBlank(orderInfo.getTschbz()) ? selectOrderInfoByOrderId.getTschbz(): orderInfo.getTschbz());
				orderInfo.setCzdm(StringUtils.isBlank(orderInfo.getCzdm()) ? selectOrderInfoByOrderId.getCzdm(): orderInfo.getCzdm());
				
				orderInfo.setQdBz(StringUtils.isBlank(orderInfo.getQdBz()) ? selectOrderInfoByOrderId.getQdBz(): orderInfo.getQdBz());
				orderInfo.setQdXmmc(StringUtils.isBlank(orderInfo.getQdXmmc()) ? selectOrderInfoByOrderId.getQdXmmc(): orderInfo.getQdXmmc());
				
				orderInfo.setMdh(StringUtils.isBlank(orderInfo.getMdh()) ? selectOrderInfoByOrderId.getMdh() : orderInfo.getMdh());
				orderInfo.setTqm(StringUtils.isBlank(orderInfo.getTqm()) ? selectOrderInfoByOrderId.getTqm() : orderInfo.getTqm());
				
				orderInfo.setStatus(StringUtils.isBlank(orderInfo.getStatus()) ? selectOrderInfoByOrderId.getStatus() : orderInfo.getStatus());
				orderInfo.setByzd1(StringUtils.isBlank(orderInfo.getByzd1()) ? selectOrderInfoByOrderId.getByzd1() : orderInfo.getByzd1());
				orderInfo.setByzd2(StringUtils.isBlank(orderInfo.getByzd2()) ? selectOrderInfoByOrderId.getByzd2() : orderInfo.getByzd2());
				orderInfo.setByzd3(StringUtils.isBlank(orderInfo.getByzd3()) ? selectOrderInfoByOrderId.getByzd3() : orderInfo.getByzd3());
				orderInfo.setByzd4(StringUtils.isBlank(orderInfo.getByzd4()) ? selectOrderInfoByOrderId.getByzd4() : orderInfo.getByzd4());
				orderInfo.setByzd5(StringUtils.isBlank(orderInfo.getByzd5()) ? selectOrderInfoByOrderId.getByzd5() : orderInfo.getByzd5());
				orderInfo.setCreateTime(orderInfo.getCreateTime());
				orderInfo.setUpdateTime(new Date());
			}
			
		}
	}
	
	
	@Override
	public R dynamciInvoiceByOrderId(String id, Map<String, PageSld> sldMap, List<String> shList) {
		
		List<String> idList = new ArrayList<>();
		idList.add(id);
		// 根据id查询订单信息
		List<CommonOrderInfo> specialInvoiceList = apiOrderInfoService.batchQueryOrderInfoByOrderIds(idList, shList);
		
		R r = new R();
		String uuid = UUID.randomUUID().toString();
		try {
			
			//根据发票请求流水号添加redis同步锁
			for (CommonOrderInfo comm : specialInvoiceList) {
				
				if (!redisService.setNx(comm.getOrderInfo().getFpqqlsh(), uuid)) {
					return R.error().put(OrderManagementConstant.CODE, "9999")
							.put(OrderManagementConstant.MESSAGE, "流水号" + comm.getOrderInfo().getFpqqlsh() + "发票正在开具中，请勿重复开具!");
				} else {
					redisService.expire(comm.getOrderInfo().getFpqqlsh(), 300);
				}
            
            }
		 
			for(CommonOrderInfo comm : specialInvoiceList){
				
				try {
					comm = PriceTaxSeparationUtil.taxSeparationService(comm, new TaxSeparateConfig());
				} catch (OrderSeparationException e) {
					log.error("静态码开票数据价税分离失败,异常原因：{}",e);
				}
				
			}
			

			/**
			 * 数据补全  开票数据校验
			 */
			try {
				specialInvoiceList = completeOrderInvoiceInfo(specialInvoiceList);
			} catch (OrderReceiveException e) {
			    return R.error().put(OrderManagementConstant.CODE, e.getCode())
			            .put(OrderManagementConstant.MESSAGE, e.getMessage());
			}
			
			/**
			 *  调用发票开具接口
			 */
			
			r = singleInvoice(specialInvoiceList.get(0),sldMap);
			
		} catch (Exception e) {
			log.error("发票开具异常，异常信息为:{}",e);
			return R.error().put(OrderManagementConstant.MESSAGE,"发票开具异常");
		} finally {
			//业务完成后删除redis中的key值
			for(CommonOrderInfo common : specialInvoiceList){
				if(uuid.equals(redisService.get(common.getOrderInfo().getFpqqlsh()))){
					redisService.del(common.getOrderInfo().getFpqqlsh());
					
				}
			}
		}
        return r;
    }
	
	/**
	 * 查询发票开具状态
	 * 查询不是2101状态,请求底层进行不换流水号开票
	 *
	 * @param fpqqlsh
	 * @param xhfNsrsbh
	 * @return
	 */
	@Override
	public CommonInvoiceStatus queryInvoiceStatus(String fpqqlsh, String xhfNsrsbh) {
		
		String terminalCode = apiTaxEquipmentService.getTerminalCode(xhfNsrsbh);
		
		List<String> shList = new ArrayList<>();
		shList.add(xhfNsrsbh);
		OrderInvoiceInfo orderInvoiceInfo = apiOrderInvoiceInfoService.selectOrderInvoiceInfoByFpqqlsh(fpqqlsh, shList);
		
		if (OrderInfoEnum.TAX_EQUIPMENT_FGBW.getKey().equals(terminalCode) || OrderInfoEnum.TAX_EQUIPMENT_FGHX.getKey().equals(terminalCode)
				|| OrderInfoEnum.TAX_EQUIPMENT_FGUKEY.getKey().equals(terminalCode)) {
			
			/**
			 * 方格数据根据开票状态进行判断,
			 * 如果是未开票,没有发票数据,返回2101允许编辑,
			 * 如果是已开票,根据开票状态进行判断,如果开票失败或者开票中,调用底层接口请求开票,
			 * 如果开票成功返回不可编辑.
			 */
			CommonInvoiceStatus commonInvoicestatus = new CommonInvoiceStatus();
			commonInvoicestatus.setFpzt(OrderInfoEnum.INVOICE_QUERY_STATUS_2100.getKey());
			commonInvoicestatus.setFpqqlsh(orderInvoiceInfo.getKplsh());
			commonInvoicestatus.setFpztms(OrderInfoEnum.INVOICE_QUERY_STATUS_2100.getValue());
			
			if (ObjectUtil.isEmpty(orderInvoiceInfo)) {
				commonInvoicestatus.setFpzt(OrderInfoEnum.INVOICE_QUERY_STATUS_2101.getKey());
				commonInvoicestatus.setFpztms(OrderInfoEnum.INVOICE_QUERY_STATUS_2101.getValue());
			} else {
				if (OrderInfoEnum.INVOICE_STATUS_3.getKey().equals(orderInvoiceInfo.getKpzt()) || OrderInfoEnum.INVOICE_STATUS_1.getKey().equals(orderInvoiceInfo.getKpzt())) {
					
					String registCodeStr = apiFangGeInterfaceService.getRegistCodeByRedis(orderInvoiceInfo.getXhfNsrsbh(), orderInvoiceInfo.getSld());
					RegistrationCode registrationCode = JsonUtils.getInstance().parseObject(registCodeStr, RegistrationCode.class);
					
					/**
					 * 存放开票信息到redis队列
					 */
					PushPayload pushPayload = new PushPayload();
					//发票开具
					pushPayload.setINTERFACETYPE(ConfigureConstant.STRING_1);
					pushPayload.setNSRSBH(registrationCode.getXhfNsrsbh());
					pushPayload.setJQBH(registrationCode.getJqbh());
					pushPayload.setZCM(registrationCode.getZcm());
					pushPayload.setDDQQLSH(orderInvoiceInfo.getFpqqlsh());
					apiFangGeInterfaceService.saveMqttToRedis(pushPayload);
					
					commonInvoicestatus.setFpzt(OrderInfoEnum.INVOICE_QUERY_STATUS_2100.getKey());
					commonInvoicestatus.setFpztms(OrderInfoEnum.INVOICE_QUERY_STATUS_2100.getValue());
				} else if (OrderInfoEnum.INVOICE_STATUS_2.getKey().equals(orderInvoiceInfo.getKpzt())) {
					
					commonInvoicestatus.setFpzt(OrderInfoEnum.INVOICE_QUERY_STATUS_2100.getKey());
					commonInvoicestatus.setFpztms(OrderInfoEnum.INVOICE_QUERY_STATUS_2100.getValue());
				}
			}
			
			
			commonInvoicestatus.setStatusCode(OrderInfoContentEnum.SUCCESS.getKey());
			commonInvoicestatus.setStatusMessage(OrderInfoContentEnum.SUCCESS.getMessage());
			return commonInvoicestatus;
		}
		
		InvoiceQuery query = new InvoiceQuery();
		query.setFPQQLSH(orderInvoiceInfo.getKplsh());
		query.setNSRSBH(xhfNsrsbh);
		query.setTerminalCode(terminalCode);
		
		String url = OpenApiConfig.queryInvoiceStatus;
		
		if (OrderInfoEnum.TAX_EQUIPMENT_NEWTAX.getKey().equals(terminalCode)) {
			url = OpenApiConfig.queryInvoiceStatusNewTax;
		}
		CommonInvoiceStatus commonInvoiceStatus = HttpInvoiceRequestUtil.queryInvoiceFinalSatusFromSk(url, query);
		
		if (ObjectUtil.isNotNull(commonInvoiceStatus) && OrderInfoContentEnum.SUCCESS.getKey().equals(commonInvoiceStatus.getStatusCode())) {
			/**
			 * 开票返回状态,非失败状态
			 */
			if (!OrderInfoEnum.INVOICE_QUERY_STATUS_2101.getKey().equals(commonInvoiceStatus.getFpzt())) {
				/**
				 * 如果发票数据不为空,并且不是开票成功状态,不是初始化状态,请求底层进行不换流水号开票
				 */
				if (ObjectUtil.isNotNull(orderInvoiceInfo) && !OrderInfoEnum.INVOICE_STATUS_2.getKey().equals(orderInvoiceInfo.getKpzt()) && !OrderInfoEnum.INVOICE_STATUS_0.getKey().equals(orderInvoiceInfo.getKpzt())) {
					/**
					 * 数据放入开票队列
					 */
					String fpqqpch = orderInvoiceInfo.getKplsh().substring(0, orderInvoiceInfo.getKplsh().length() - 3);
					FpkjMqData fpkjMqData = new FpkjMqData();
					fpkjMqData.setFpqqlsh(orderInvoiceInfo.getFpqqlsh());
					fpkjMqData.setFpqqpch(fpqqpch);
					fpkjMqData.setKplsh(orderInvoiceInfo.getKplsh());
					fpkjMqData.setNsrsbh(orderInvoiceInfo.getXhfNsrsbh());
					fpkjMqData.setTerminalCode(terminalCode);
					String jsonString = JsonUtils.getInstance().toJsonString(fpkjMqData);
					log.info("{}开票查询结果请求开票数据信息{}", LOGGER_MSG, jsonString);
					openInvoiceService.openAnInvoice(jsonString, orderInvoiceInfo.getXhfNsrsbh());
				}
			} else {
				/**
				 * 同步底层数据,如果发票表有数据,并且是开票中,根据底层返回状态,更新对应状态为开票失败
				 */
				if (ObjectUtil.isNotEmpty(orderInvoiceInfo) && OrderInfoEnum.INVOICE_STATUS_1.getKey().equals(orderInvoiceInfo.getKpzt())) {
					apiOrderProcessService.updateKpzt(orderInvoiceInfo.getFpqqlsh(), OrderInfoEnum.ORDER_STATUS_6.getKey(), OrderInfoEnum.INVOICE_STATUS_3.getKey(), commonInvoiceStatus.getFpztms(), shList);
				}
			}
			
			
		}
		return commonInvoiceStatus;
	}

	/**
     * @Title : kpInvoice
     * @Description ：
     * @param @param specialInvoiceList
     * @param @param sldMap
     * @param @return
     * @return List<R>
     * @exception
	 *
	*/
	
	private R singleInvoice(CommonOrderInfo common, Map<String, PageSld> sldMap) {
		List<CommonOrderInfo> list = new ArrayList<>();
		list.add(common);
		return makeOutAnInvoiceService.makeOutAnInovice(list, sldMap);
	}
    
    
    /**
     * @Title : completeOrderInvoiceInfo
     * @param @param specialInvoiceList
     * @return void
     * @exception
	 *
	*/
	
	private List<CommonOrderInfo> completeOrderInvoiceInfo(List<CommonOrderInfo> specialInvoiceList)
			throws OrderReceiveException {

		List<CommonOrderInfo> splitCommonOrderInfoList = new ArrayList<>();

		// 生成待开的流程
		if (CollectionUtils.isNotEmpty(specialInvoiceList)) {
			// 补全订单信息
			generateReadyOpenOrderService.completeOrderInfo(specialInvoiceList);
			/**
			 * 根据税号查询税控设备
			 */
			/**
			 * 特殊字符处理
			 */
			/**
			 * 如果编辑成功,返回前端流水号,订单id和processId
			 */
			for (CommonOrderInfo commonOrderInfo : specialInvoiceList) {
				/**
				 * 根据税号查询税控设备
				 */
				String terminalCode = apiTaxEquipmentService
						.getTerminalCode(specialInvoiceList.get(0).getOrderInfo().getXhfNsrsbh());
				
				/**
				 * 特殊字符处理
				 */
				BeanTransitionUtils.replaceCharacter(commonOrderInfo);
				
				commonOrderInfo.setTerminalCode(terminalCode);

				/**
				 * 如果超限额拆分完成后,未做超限额拆分需要走编辑流程,更新补全后数据
				 */
				Map map;
				try {
					map = orderInfoService.updateOrderInfoAndOrderProcessInfo(commonOrderInfo);
				} catch (OrderReceiveException e) {
					log.error("{}订单编辑异常,异常原因为:{}", LOGGER_MSG, e);
					throw new OrderReceiveException(e.getCode(), e.getMessage());
				}
				/**
				 * 如果编辑成功,返回前端流水号,订单id和processId
				 */
				splitCommonOrderInfoList.add(commonOrderInfo);
				
			}

		}
		return splitCommonOrderInfoList;
	}

	public static void main(String[] args) {
		InvoiceQuery query = new InvoiceQuery();
		System.out.println(JsonUtils.getInstance().toJsonStringNullToEmpty(query));
	}
	
	
}
