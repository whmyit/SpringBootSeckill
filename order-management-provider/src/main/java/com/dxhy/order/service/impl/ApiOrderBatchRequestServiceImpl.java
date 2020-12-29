package com.dxhy.order.service.impl;

import com.dxhy.order.api.ApiOrderBatchRequestService;
import com.dxhy.order.dao.OrderBatchRequestMapper;
import org.apache.dubbo.config.annotation.Service;

import javax.annotation.Resource;
import java.util.List;

/**
 * 订单发票批次业务层
 *
 * @author ZSC-DXHY
 * @date 创建时间: 2020-08-14 14:36
 */
@Service
public class ApiOrderBatchRequestServiceImpl implements ApiOrderBatchRequestService {
	
	@Resource
	private OrderBatchRequestMapper orderBatchRequestMapper;
	
	@Override
	public int selectOrderBatchRequestByDdqqpch(String fpqqpch, List<String> shList) {
		return orderBatchRequestMapper.selectOrderBatchRequestByDdqqpch(fpqqpch, shList);
	}
	
}
