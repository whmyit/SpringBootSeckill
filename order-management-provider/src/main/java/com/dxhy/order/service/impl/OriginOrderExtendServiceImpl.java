package com.dxhy.order.service.impl;

import com.dxhy.order.api.ApiOriginOrderExtendService;
import com.dxhy.order.dao.OrderOriginExtendInfoMapper;
import com.dxhy.order.model.OrderOriginExtendInfo;
import com.dxhy.order.model.PageUtils;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.apache.dubbo.config.annotation.Service;

import javax.annotation.Resource;
import java.util.List;
import java.util.Map;

/**
 * 原始订单扩展业务层
 *
 * @author ZSC-DXHY
 * @date 创建时间: 2020-08-14 15:26
 */
@Service
public class OriginOrderExtendServiceImpl implements ApiOriginOrderExtendService{
	
	@Resource
    private OrderOriginExtendInfoMapper orderOriginExtendInfoMapper;
    
	@Override
	public PageUtils queryOriginList(Map<String, Object> paramMap, List<String> shList) {
		int pageSize = (Integer) paramMap.get("pageSize");
		int currPage = (Integer) paramMap.get("currPage");
		PageHelper.startPage(currPage, pageSize);
		List<Map<String, Object>> queryOriginList = orderOriginExtendInfoMapper.queryOriginList(paramMap, shList);
		PageInfo<Map<String, Object>> pageInfo = new PageInfo<>(queryOriginList);
		PageUtils page = new PageUtils(pageInfo.getList(), (int) pageInfo.getTotal(), pageInfo.getPageSize(),
				pageInfo.getPageNum());
		return page;
	}
	
	@Override
	public PageUtils queryOriginOrderCompare(Map<String, Object> paramMap, List<String> shList) {
		
		int pageSize = (Integer) paramMap.get("pageSize");
		int currPage = (Integer) paramMap.get("currPage");
		PageHelper.startPage(currPage, pageSize);
		List<Map<String, Object>> orderOriginList = orderOriginExtendInfoMapper.queryOriginOrderCompare(paramMap, shList);
		PageInfo<Map<String, Object>> pageInfo = new PageInfo<>(orderOriginList);
		PageUtils page = new PageUtils(pageInfo.getList(), (int) pageInfo.getTotal(), pageInfo.getPageSize(),
				pageInfo.getPageNum());
		return page;
	}
	
	
	@Override
	public List<Map<String, Object>> queryOriginOrderAndInvoiceInfo(Map<String, Object> paramMap, List<String> shList) {
		
		return orderOriginExtendInfoMapper.queryOriginOrderAndInvoiceInfo(paramMap, shList);
	}
	
	@Override
	public Map<String, Object> queryCompareOriginOrderAndInvoiceCounter(Map<String, Object> paramMap, List<String> shList) {
		
		return orderOriginExtendInfoMapper.queryCompareOriginOrderAndInvoiceCounter(paramMap, shList);
	}
	
	@Override
	public List<OrderOriginExtendInfo> queryOriginOrderByOrder(OrderOriginExtendInfo orderOriginExtendInfo, List<String> shList) {
		
		return orderOriginExtendInfoMapper.queryOriginOrderByOrder(orderOriginExtendInfo, shList);
	}
	
}
