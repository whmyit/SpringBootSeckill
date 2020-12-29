package com.dxhy.order.service.impl;

import com.dxhy.order.api.ApiQuickCodeInfoService;
import com.dxhy.order.api.RedisService;
import com.dxhy.order.constant.Constant;
import com.dxhy.order.dao.*;
import com.dxhy.order.model.*;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.dubbo.config.annotation.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * 静态码业务层
 *
 * @author ZSC-DXHY
 * @date 创建时间: 2020-08-14 15:30
 */
@Service
@Slf4j
public class QuickCodeInfoServiceImpl implements ApiQuickCodeInfoService {
	
	@Resource
	InvoiceTypeCodeExtMapper invoiceTypeCodeExtMapper;
	
	@Resource
	QuickResponseCodeInfoMapper quickResponseCodeInfoMapper;
	
	@Resource
	QuickResponseCodeItemInfoMapper quickResponseCodeItemInfoMapper;
	
	@Resource
	EwmConfigInfoMapper ewmConfigInfoMapper;
	
	@Resource
	EwmConfigItemInfoMapper ewmConfigItemInfoMapper;
	
	@Resource
	private OrderInfoMapper orderInfoMapper;
	
	@Resource
	private OrderProcessInfoMapper orderProcessInfoMapper;
	
	@Resource
	private EwmGzhConfigMapper ewmGzhConfigMapper;
	
	@Resource
	private RedisService redisService;
	
	@Override
	@Transactional(rollbackFor = Exception.class)
	public boolean saveQrcodeInfo(QuickResponseCodeInfo qrcodeInfo, List<QuickResponseCodeItemInfo> itemList,
	                              List<InvoiceTypeCodeExt> extList) {
		
		if (qrcodeInfo != null) {
			int insertSelective = quickResponseCodeInfoMapper.insertSelective(qrcodeInfo);
			if (insertSelective <= 0) {
				return false;
			}
			/**
			 * todo 满足mycat临时使用的缓存,后期优化
			 * 目前开票接收成功后,添加发票请求流水号与销方税号对应关系的缓存
			 * 添加发票请求批次号与销方税号对应关系
			 *
			 */
			String cacheTqm = String.format(Constant.REDIS_TQM, qrcodeInfo.getTqm());
			if (StringUtils.isBlank(redisService.get(cacheTqm))) {
				redisService.set(cacheTqm, qrcodeInfo.getTqm(), Constant.REDIS_EXPIRE_TIME_DEFAULT);
			}
			
		}
		
		if (CollectionUtils.isNotEmpty(itemList)) {
			for (QuickResponseCodeItemInfo item : itemList) {
				int insertSelective = quickResponseCodeItemInfoMapper.insertSelective(item);
				if (insertSelective <= 0) {
					return false;
				}
			
			}
		}
		
		if(CollectionUtils.isNotEmpty(extList)){
			for(InvoiceTypeCodeExt ext : extList){
				int insertSelective = invoiceTypeCodeExtMapper.insertInvoiceTypeCodeExt(ext);
				if (insertSelective <= 0) {
					return false;
				}
			}
		}
		
		return true;
	}


	/*@Override
	public Map<String, Object> queryQrcodeInfoByTqm(String tqm, String nsrsbh, String type) {
		Map<String, Object> resultMap = new HashMap<>(5);
		
		QuickResponseCodeInfo qrCodeInfo = new QuickResponseCodeInfo();
		qrCodeInfo.setTqm(tqm);
		qrCodeInfo.setXhfNsrsbh(nsrsbh);
		qrCodeInfo.setQuickResponseCodeType(type);
		qrCodeInfo = quickResponseCodeInfoMapper.selectBySelective(qrCodeInfo);
		if (qrCodeInfo == null) {
			return null;
		}
		
		*//**
		 * 添加静态码开票逻辑,返回企业流水号.
		 *//*
		*//*if (StringUtils.isBlank(qrCodeInfo.getFpqqlsh())) {
			qrCodeInfo.setFpqqlsh(apiInvoiceCommonService.getGenerateShotKey());
		}*//*
		
		
		QuickResponseCodeItemInfo queryItem = new QuickResponseCodeItemInfo();
		queryItem.setQuickResponseCodeInfoId(qrCodeInfo.getId());
		List<QuickResponseCodeItemInfo> itemList = quickResponseCodeItemInfoMapper.selectBySelective(queryItem);
		
		InvoiceTypeCodeExt ext = new InvoiceTypeCodeExt();
		ext.setInvoiceTypeCodeId(qrCodeInfo.getId());
		List<InvoiceTypeCodeExt> extList = invoiceTypeCodeExtMapper.selectBySelective(ext);
		String status = OrderInfoEnum.QUICK_RESPONSE_CODE_STATUS_0.getKey();
		*//**
		 * 添加二维码状态信息
		 * 0:二维码状态正常;1:二维码已作废;2:二维码已过期;3:二维码已开票;4:二维码开票异常;5:二维码开票中;
		 *
		 * 如果数据库二维码状态为0,表示正常状态,还未使用,可以进行操作,需要判断二维码是否过期或者是作废
		 * 如果数据库二维码状态为1,标识已使用,可能存在
		 *//*
		if (StringUtils.isBlank(qrCodeInfo.getEwmzt()) || ConfigureConstant.STRING_0.equals(qrCodeInfo.getEwmzt())) {
			
			//判断过期时间是否过期
			//判断是否作废,如果作废直接返回二维码已作废,如果是未作废,判断是否过期,已过期返回已过期
			// TODO: 2020/2/28 需要补全逻辑
			*//*if (ConfigureConstant.STRING_1.equals(qrCodeInfo.getZfzt())) {
				status = OrderInfoEnum.QUICK_RESPONSE_CODE_STATUS_1.getKey();
			} else {
				if (qrCodeInfo.getQuickResponseCodeValidTime() != null && DateTime.now().compareTo(DateTime.of(qrCodeInfo.getQuickResponseCodeValidTime())) > 0) {
					status = OrderInfoEnum.QUICK_RESPONSE_CODE_STATUS_2.getKey();
				}
			}*//*
		} else {
			//根据流水号查询发票表数据,
			OrderInvoiceInfo orderInvoiceInfo = apiOrderInvoiceInfoService.selectOrderInvoiceInfoByFpqqlsh(qrCodeInfo.getFpqqlsh());
			if (orderInvoiceInfo != null) {
				if (OrderInfoEnum.INVOICE_STATUS_2.getKey().equals(orderInvoiceInfo.getKpzt())) {
					status = OrderInfoEnum.QUICK_RESPONSE_CODE_STATUS_3.getKey();
				} else if (OrderInfoEnum.INVOICE_STATUS_1.getKey().equals(orderInvoiceInfo.getKpzt())) {
					status = OrderInfoEnum.QUICK_RESPONSE_CODE_STATUS_5.getKey();
				} else if (OrderInfoEnum.INVOICE_STATUS_3.getKey().equals(orderInvoiceInfo.getKpzt())) {
					status = OrderInfoEnum.QUICK_RESPONSE_CODE_STATUS_4.getKey();
				} else if (OrderInfoEnum.INVOICE_STATUS_4.getKey().equals(orderInvoiceInfo.getKpzt())) {
					status = OrderInfoEnum.QUICK_RESPONSE_CODE_STATUS_4.getKey();
				}
			}
		}
		
		resultMap.put("qrCodeInfo", qrCodeInfo);
		resultMap.put("itemList", itemList);
		resultMap.put("extList", extList);
		resultMap.put("qrStatus", status);
		return resultMap;
	}*/

	/**
	 * 查询二维码列表
	 */
	@Override
	public PageUtils queryQrCodeList(Map map, List<String> shList) {
		int pageSize = Integer.parseInt((String) map.get("pageSize"));
		int currPage = Integer.parseInt((String) map.get("currentPage"));
		PageHelper.startPage(currPage, pageSize);
		List<Map> list = quickResponseCodeInfoMapper.selectQrCodeList(map, shList);
		PageInfo<Map> pageInfo = new PageInfo<>(list);
		PageUtils page = new PageUtils(pageInfo.getList(), (int) pageInfo.getTotal(), pageInfo.getPageSize(), pageInfo.getPageNum());
		return page;
	}
	
	/**
	 * 查询二维码详情
	 */
	@Override
	public QuickResponseCodeInfo queryQrCodeDetail(String qrcodeId, List<String> xhfNsrsbh) {
		return quickResponseCodeInfoMapper.selectQuickResponseCodeById(qrcodeId, xhfNsrsbh);
	}
	
	@Override
	public List<QuickResponseCodeItemInfo> queryQrCodeItemListByQrcodeId(String qrcodeId, List<String> xhfNsrsbh) {
		return quickResponseCodeItemInfoMapper.selectByQrcodeId(qrcodeId, xhfNsrsbh);
	}
	
	@Override
	public List<InvoiceTypeCodeExt> queryInvoiceTypeByQrcodeId(String qrcodeId, List<String> xhfNsrsbh) {
		return invoiceTypeCodeExtMapper.selectByQrcodeId(qrcodeId, xhfNsrsbh);
	}
	
	@Override
	public QuickResponseCodeInfo queryQrCodeDetailByTqm(String tqm, List<String> shList, String qrCodeType) {
		return quickResponseCodeInfoMapper.queryQrCodeDetailByTqm(tqm, shList, qrCodeType);
	}

	@Override
	public EwmConfigInfo queryEwmConfigInfo(Map<String, Object> paramMap) {
		return ewmConfigInfoMapper.queryEwmConfigInfo(paramMap);
	}
	
	
	@Transactional(rollbackFor = Exception.class)
	@Override
	public boolean addEwmConfigInfo(EwmConfigInfo ewmConfig,List<EwmConfigItemInfo> ewmConfigItemList) {
		int insert = ewmConfigInfoMapper.insertSelective(ewmConfig);
		if(insert <= 0){
			return false;
		}
		
		for(EwmConfigItemInfo item : ewmConfigItemList){
			int insertSelective = ewmConfigItemInfoMapper.insertEwmConfigItem(item);
			if (insertSelective <= 0) {
				return false;
			}
		}
		return true;
	}
	
	@Transactional(rollbackFor = Exception.class)
	@Override
	public boolean updateEwmConfigInfo(EwmConfigInfo ewmConfig, List<EwmConfigItemInfo> ewmConfigItemList) {
		int updateByXhfNsrsbh = ewmConfigInfoMapper.updateByPrimaryKeySelective(ewmConfig);
		if (updateByXhfNsrsbh <= 0) {
			return false;
		}
		
		int i = ewmConfigItemInfoMapper.deleteByEwmConfigId(ewmConfig.getId());
		for (EwmConfigItemInfo item : ewmConfigItemList) {
			int insertSelective = ewmConfigItemInfoMapper.insertEwmConfigItem(item);
			if (insertSelective <= 0) {
				return false;
			}
		}
		
		return true;
	}
	
	@Override
	public List<EwmConfigItemInfo> queryEwmConfigItemInfoById(String id) {
		return ewmConfigItemInfoMapper.queryEwmItemInfoByEwmConfigId(id);
	}
	
	/*@Override
	public Map<String, Object> queryEwmDetailByFpqqlsh(String fpqqlsh) {
		
		return quickResponseCodeInfoMapper.queryEwmDetailByFpqqlsh(fpqqlsh);
	}*/
	
	//@Override
	/*public QuickResponseCodeInfo queryQrCodeDetailByDdqqlshAndNsrsbh(String fpqqlsh, String nsrsbh) {
		
		return quickResponseCodeInfoMapper.queryQrCodeDetailByDdqqlshAndNsrsbh(fpqqlsh, nsrsbh);
	}*/
	
	/*@Override
	public List<QuickResponseCodeInfo> selectQuickResponseCodeListByFpqqlshDdhNsrsbh(String xsfNsrsbh, String ddh, String tqm, String fpqqlsh) {
		return quickResponseCodeInfoMapper.selectQuickResponseCodeListByFpqqlshDdhNsrsbh(xsfNsrsbh, ddh, tqm, fpqqlsh);
	}*/
	
	
	@Override
	public boolean updateEwmDetailInfo(QuickResponseCodeInfo quickInfo, List<String> shList) {
		int updateByPrimaryKeySelective = quickResponseCodeInfoMapper.updateQrCodeInfo(quickInfo, shList);
		return updateByPrimaryKeySelective > 0;
	}
	
	/**
	 * 更新购方信息
	 */
	@Transactional(rollbackFor = Exception.class)
	@Override
	public boolean updateGhfInfo(CommonOrderInfo pageToFpkjInfo, List<String> shList) {
		
		OrderInfo selectOrderInfoByOrderId = orderInfoMapper
				.selectOrderInfoByOrderId(pageToFpkjInfo.getOrderInfo().getId(), shList);
		if (selectOrderInfoByOrderId == null) {
			return false;
		}
		
		OrderInfo updateOrderInfo = new OrderInfo();
		updateOrderInfo.setGhfDh(pageToFpkjInfo.getOrderInfo().getGhfDh());
		updateOrderInfo.setGhfDz(pageToFpkjInfo.getOrderInfo().getGhfDz());
		updateOrderInfo.setGhfEmail(pageToFpkjInfo.getOrderInfo().getGhfEmail());
		updateOrderInfo.setGhfYh(pageToFpkjInfo.getOrderInfo().getGhfYh());
		updateOrderInfo.setGhfZh(pageToFpkjInfo.getOrderInfo().getGhfZh());
		updateOrderInfo.setGhfId(pageToFpkjInfo.getOrderInfo().getGhfId());
		updateOrderInfo.setGhfMc(pageToFpkjInfo.getOrderInfo().getGhfMc());
		updateOrderInfo.setGhfNsrsbh(pageToFpkjInfo.getOrderInfo().getGhfNsrsbh());
		updateOrderInfo.setGhfQylx(pageToFpkjInfo.getOrderInfo().getGhfQylx());
		updateOrderInfo.setId(selectOrderInfoByOrderId.getId());
		updateOrderInfo.setFpzlDm(pageToFpkjInfo.getOrderInfo().getFpzlDm());
		updateOrderInfo.setYwlx(pageToFpkjInfo.getOrderInfo().getYwlx());
		updateOrderInfo.setBz(pageToFpkjInfo.getOrderInfo().getBz());
		updateOrderInfo.setGhfSj(pageToFpkjInfo.getOrderInfo().getGhfSj());
		updateOrderInfo.setUpdateTime(new Date());
		int updateOrderInfoByOrderId = orderInfoMapper.updateOrderInfoByOrderId(updateOrderInfo, shList);
		if (updateOrderInfoByOrderId <= 0) {
			return false;
		}
		
		// 更新订单扩展表
		
		OrderProcessInfo updateOrderProcessInfo = new OrderProcessInfo();
		updateOrderProcessInfo.setGhfMc(pageToFpkjInfo.getOrderInfo().getGhfMc());
		updateOrderProcessInfo.setGhfNsrsbh(pageToFpkjInfo.getOrderInfo().getGhfNsrsbh());
		updateOrderProcessInfo.setId(selectOrderInfoByOrderId.getProcessId());
		updateOrderProcessInfo.setFpzlDm(pageToFpkjInfo.getOrderInfo().getFpzlDm());
		updateOrderProcessInfo.setYwlx(pageToFpkjInfo.getOrderInfo().getYwlx());
		updateOrderProcessInfo.setYwlxId(pageToFpkjInfo.getOrderInfo().getYwlxId());
		updateOrderProcessInfo.setUpdateTime(new Date());
		int updateOrderProcessInfoByProcessId = orderProcessInfoMapper.updateOrderProcessInfoByProcessId(updateOrderProcessInfo, shList);
		return updateOrderProcessInfoByProcessId > 0;
	}

	@Override
	public EwmGzhConfig queryGzhEwmConfig(String xhfNsrsbh) {
		EwmGzhConfig config = new EwmGzhConfig();
		config.setNsrsbh(xhfNsrsbh);
		return ewmGzhConfigMapper.selectByEwmGzhConfig(config);
	}


	@Transactional(rollbackFor = Exception.class)
	@Override
	public R updateStaticEwmInfo(QuickResponseCodeInfo qrCodeInfo, List<QuickResponseCodeItemInfo> itemList, List<InvoiceTypeCodeExt> extList) {
		
		List<String> shList = new ArrayList<>();
		shList.add(qrCodeInfo.getXhfNsrsbh());
		int i = quickResponseCodeInfoMapper.updateQrCodeInfo(qrCodeInfo, shList);
		if (i <= 0) {
			log.error("更新静态码信息失败,id:{}", qrCodeInfo.getId());
			return R.error();
		}
		
		//删除老的明细
		quickResponseCodeItemInfoMapper.deleteByQrId(qrCodeInfo.getId(), shList);
		//删除老的发票种类
		invoiceTypeCodeExtMapper.deleteByQrId(qrCodeInfo.getId(), shList);
		
		if (CollectionUtils.isNotEmpty(itemList)) {
			for (QuickResponseCodeItemInfo item : itemList) {
				int insertSelective = quickResponseCodeItemInfoMapper.insertSelective(item);
				if (insertSelective <= 0) {
					return R.error();
				}
				
			}
		}

		if(CollectionUtils.isNotEmpty(extList)){
			for(InvoiceTypeCodeExt ext : extList) {
				int insertSelective = invoiceTypeCodeExtMapper.insertInvoiceTypeCodeExt(ext);
				if (insertSelective <= 0) {
					return R.error();
				}
			}
		}


		return R.ok();
	}


}
