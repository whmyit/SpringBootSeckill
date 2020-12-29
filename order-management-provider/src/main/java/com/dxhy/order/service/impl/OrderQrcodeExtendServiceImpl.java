package com.dxhy.order.service.impl;

import cn.hutool.core.date.DateUtil;
import com.dxhy.order.api.ApiOrderQrcodeExtendService;
import com.dxhy.order.api.ApiQuickCodeInfoService;
import com.dxhy.order.api.RedisService;
import com.dxhy.order.constant.Constant;
import com.dxhy.order.dao.EwmConfigInfoMapper;
import com.dxhy.order.dao.OrderProcessInfoMapper;
import com.dxhy.order.dao.OrderQrcodeExtendInfoMapper;
import com.dxhy.order.model.CommonOrderInfo;
import com.dxhy.order.model.EwmConfigInfo;
import com.dxhy.order.model.OrderQrcodeExtendInfo;
import com.dxhy.order.model.PageUtils;
import com.dxhy.order.utils.DateUtils;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.dubbo.config.annotation.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.*;

/**
 * @author ：杨士勇
 * @ClassName ：OrderQrcodeExtendServiceImpl
 * @Description ：
 * @date ：2020年4月10日 下午1:51:05
 */

@Service
@Slf4j
public class OrderQrcodeExtendServiceImpl implements ApiOrderQrcodeExtendService {
	
	@Resource
	OrderQrcodeExtendInfoMapper orderQrcodeExtendInfoMapper;
	
	@Resource
	EwmConfigInfoMapper ewmConfigInfoMapper;

	@Resource
	OrderProcessInfoMapper orderProcessInfoMapper;
	
	@Resource
	RedisService redisService;

	@Resource
	ApiQuickCodeInfoService apiQuickCodeInfoService;

	
	@Override
	public OrderQrcodeExtendInfo queryQrCodeDetailByDdqqlshAndNsrsbh(String ddqqlsh, List<String> shList) {
		
		OrderQrcodeExtendInfo orderQrcodeExtendInfo = new OrderQrcodeExtendInfo();
		orderQrcodeExtendInfo.setFpqqlsh(ddqqlsh);
		return orderQrcodeExtendInfoMapper.selectByOrderQrcodeExtendInfo(orderQrcodeExtendInfo, shList);
	}
	
	@Override
	public boolean saveQrcodeInfo(OrderQrcodeExtendInfo commonOrderToQrCodeInfo) {
		
		int insert = orderQrcodeExtendInfoMapper.insertQrCodeInfo(commonOrderToQrCodeInfo);
		if (insert > 0) {
			/**
			 * todo 满足mycat临时使用的缓存,后期优化
			 * 目前开票接收成功后,添加发票请求流水号与销方税号对应关系的缓存
			 * 添加发票请求批次号与销方税号对应关系
			 *
			 */
			String cacheTqm = String.format(Constant.REDIS_TQM, commonOrderToQrCodeInfo.getTqm());
			if (StringUtils.isBlank(redisService.get(cacheTqm))) {
				redisService.set(cacheTqm, commonOrderToQrCodeInfo.getTqm(), Constant.REDIS_EXPIRE_TIME_DEFAULT);
			}
		}
		return insert > 0;
	}
 
	@Override
	public PageUtils queryDynamicQrCodeList(Map<String, Object> paramMap, List<String> shList) {
		int pageSize = Integer.parseInt((String) paramMap.get("pageSize"));
		int currPage = Integer.parseInt((String) paramMap.get("currentPage"));
		PageHelper.startPage(currPage, pageSize);
		
		String startTime = paramMap.get("startTime") == null ? "" : String.valueOf(paramMap.get("startTime"));
		if (StringUtils.isNotBlank(startTime)) {
			Date start = DateUtil.parse(startTime, "yyyy-MM-dd");
			paramMap.put("startTime", start);
		} else {
			paramMap.put("startTime", null);
		}

		String endTime = paramMap.get("endTime") == null ? "" : String.valueOf(paramMap.get("endTime"));

		if (StringUtils.isNotBlank(endTime)) {
			Date end = DateUtil.parse(endTime, "yyyy-MM-dd");
			end = DateUtil.endOfDay(end);
			paramMap.put("endTime", end);
		} else {
			paramMap.put("endTime", null);
		}

		// 二维码有效时间转换
		// 1.如果传了二维码有效起止时间 订单的创建时间必须小于 小于有效期截至时间 并且订单的过期时间大于起始时间
		String startValidTime = paramMap.get("startValidTime") == null ? ""
				: String.valueOf(paramMap.get("startValidTime"));
		if (StringUtils.isNotBlank(startValidTime)) {
			Date startValid = DateUtil.parse(startValidTime, "yyyy-MM-dd HH:mm:ss");
			paramMap.put("startValidTime", startValid);
		} else {
			paramMap.put("startValidTime", null);
		}

		String endValidTime = paramMap.get("endValidTime") == null ? "" : String.valueOf(paramMap.get("endValidTime"));
		if (StringUtils.isNotBlank(endValidTime)) {
			// 订单初始时间
			Date endValid = DateUtil.parse(endValidTime, "yyyy-MM-dd HH:mm:ss");
			paramMap.put("endValidTime", endValid);
			
		} else {
			paramMap.put("endValidTime", null);
		}
		
		List<Map> list = orderQrcodeExtendInfoMapper.selectDynamicQrCodeList(paramMap, shList);
		PageInfo<Map> pageInfo = new PageInfo<>(list);
		PageUtils page = new PageUtils(pageInfo.getList(), (int) pageInfo.getTotal(), pageInfo.getPageSize(),
				pageInfo.getPageNum());
		return page;
	}
	
	
	@Override
	public Map<String, Object> queryEwmDetailByFpqqlsh(String fpqqlsh, List<String> xhfNsrsbh) {
		
		return orderQrcodeExtendInfoMapper.queryEwmDetailByFpqqlsh(fpqqlsh, xhfNsrsbh);
	}

	@Override
	@Transactional(rollbackFor = Exception.class)
	public boolean updateEwmDetailInfo(OrderQrcodeExtendInfo orderQrcodeExtendInfo, List<String> shList, CommonOrderInfo pageToFpkjInfo) {
        
        int updateByPrimaryKeySelective = orderQrcodeExtendInfoMapper.updateByPrimaryKeySelective(orderQrcodeExtendInfo, shList);
		if(updateByPrimaryKeySelective <= 0){
		    return false;
		}
		if(pageToFpkjInfo != null){
			boolean b = apiQuickCodeInfoService.updateGhfInfo(pageToFpkjInfo, shList);
			return b;
		}
        return true;
    }
	
	@Transactional(rollbackFor = Exception.class)
	@Override
	public boolean updateEwmDetailInfoByIds(List<Map> idList) {
		for (Map map : idList) {
			String id = (String) map.get("id");
			String nsrsbh = (String) map.get("xhfNsrsbh");
			String ewmzt = (String) map.get("zfzt");
			List<String> shList = new ArrayList<>();
			shList.add(nsrsbh);
			if ("0".equals(ewmzt)) {
				//发票
				OrderQrcodeExtendInfo orderQrcodeExtendInfo = orderQrcodeExtendInfoMapper.selectByPrimaryKey(id, shList);
				if("1".equals(orderQrcodeExtendInfo.getEwmzt())){
					log.error("已使用的二维码禁止删除!");
					return false;
				}
				//失效直接更新数据库作废状态未 已作废
				orderQrcodeExtendInfo = new OrderQrcodeExtendInfo();
				orderQrcodeExtendInfo.setId(id);
				orderQrcodeExtendInfo.setXhfNsrsbh(nsrsbh);
				orderQrcodeExtendInfo.setZfzt("1");
				int updateByPrimaryKeySelective = orderQrcodeExtendInfoMapper.updateByPrimaryKeySelective(orderQrcodeExtendInfo, shList);
				if (updateByPrimaryKeySelective <= 0) {
					return false;
				}
			} else {
				// 激活 更新数据库作废状态为未作废 有效期顺延 用户配置的二维码有效期
				OrderQrcodeExtendInfo orderQrcodeExtendInfo = orderQrcodeExtendInfoMapper.selectByPrimaryKey(id, shList);
				
				Map<String, Object> paramMap = new HashMap<>(5);
				paramMap.put("xhfNsrsbh", orderQrcodeExtendInfo.getXhfNsrsbh());
				EwmConfigInfo queryEwmConfigInfo = ewmConfigInfoMapper.queryEwmConfigInfo(paramMap);
				
				OrderQrcodeExtendInfo updateQuickResponseCode = new OrderQrcodeExtendInfo();
				int invalidDays = 30;
				if (queryEwmConfigInfo != null) {
					invalidDays = StringUtils.isBlank(queryEwmConfigInfo.getInvalidTime()) ? 30 : Integer.parseInt(queryEwmConfigInfo.getInvalidTime());
				}
				
				//订单没有过期时间，永不失效 数据库中过期时间设置为2099 01 01 00：00：00
				if (invalidDays == 0) {
					Date validDate = DateUtil.parse("2099-01-01 00:00:00", "yyyy-MM-dd HH:mm:ss");
					updateQuickResponseCode.setQuickResponseCodeValidTime(validDate);
				} else {
					Date validDate = DateUtils.addDateDays(new Date(), invalidDays);
					updateQuickResponseCode.setQuickResponseCodeValidTime(validDate);
					
				}
				updateQuickResponseCode.setId(id);
				updateQuickResponseCode.setXhfNsrsbh(nsrsbh);
				updateQuickResponseCode.setZfzt("0");
				
				int updateByPrimaryKeySelective = orderQrcodeExtendInfoMapper.updateByPrimaryKeySelective(updateQuickResponseCode, shList);
				if (updateByPrimaryKeySelective <= 0) {
					return false;
				}
			}
		}
		return true;
	}
	
	@Override
	public OrderQrcodeExtendInfo queryQrCodeDetailByTqm(String tqm, List<String> shList, String type) {
		
		OrderQrcodeExtendInfo orderQrcodeExtendInfo = new OrderQrcodeExtendInfo();
		orderQrcodeExtendInfo.setTqm(tqm);
		orderQrcodeExtendInfo.setQuickResponseCodeType(type);
		return orderQrcodeExtendInfoMapper.selectByOrderQrcodeExtendInfo(orderQrcodeExtendInfo, shList);
	}
	
	@Override
	public OrderQrcodeExtendInfo queryQrCodeDetailByAuthOrderId(String succOrderId, List<String> shList) {
		
		OrderQrcodeExtendInfo orderQrcodeExtendInfo = new OrderQrcodeExtendInfo();
		orderQrcodeExtendInfo.setAuthOrderId(succOrderId);
		return orderQrcodeExtendInfoMapper.selectByOrderQrcodeExtendInfo(orderQrcodeExtendInfo, shList);
	}
	
	@Override
	public OrderQrcodeExtendInfo queryQrcodeDetailById(String qrcodeId, List<String> xhfNsrsbh) {
		return orderQrcodeExtendInfoMapper.selectByPrimaryKey(qrcodeId, xhfNsrsbh);
	}

	@Override
	public Map<String, Object> queryQrcodeAndInvoiceDetail(String qrcodeId, List<String> shList) {
		return orderQrcodeExtendInfoMapper.queryQrcodeAndInvoiceDetail(qrcodeId, shList);
	}
	
	/**
	 * 查询是否存在未审核订单
	 *
	 * @return
	 */
	@Override
	public boolean isExistNoAuditOrder(Map<String, Object> paramMap, List<String> shList) {
		
		String existNoAuditOrder = orderProcessInfoMapper.isExistNoAuditOrder(paramMap, shList);
		return existNoAuditOrder != null;
	}
	
	/**
	 * 查询所有开票异常的数据
	 *
	 * @param paramMap
	 * @param shList
	 * @return
	 */
	@Override
	public List<OrderQrcodeExtendInfo> selectOrderQrcodeExtendInfoForTask(Map<String, Object> paramMap, List<String> shList) {
		return orderQrcodeExtendInfoMapper.selectOrderQrcodeExtendInfoForTask(paramMap, shList);
	}
	
	
}
