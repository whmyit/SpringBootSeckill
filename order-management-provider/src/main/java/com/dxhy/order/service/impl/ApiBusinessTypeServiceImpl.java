package com.dxhy.order.service.impl;

import com.dxhy.order.api.ApiBusinessTypeService;
import com.dxhy.order.dao.BusinessTypeDao;
import com.dxhy.order.model.BusinessTypeInfo;
import com.dxhy.order.model.PageUtils;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import lombok.extern.slf4j.Slf4j;
import org.apache.dubbo.config.annotation.Service;

import javax.annotation.Resource;
import java.util.Date;
import java.util.List;
import java.util.Map;

/***
 *
 * 业务类型接口实现
 *
 * @author 陈玉航
 * @version 1.0 Created on 2019年6月29日 下午4:23:13
 */
@Service
@Slf4j
public class ApiBusinessTypeServiceImpl implements ApiBusinessTypeService {
	
	@Resource
	private BusinessTypeDao businessTypeDao;
	
	/**
	 * 根据业务类型名称和税号查询业务类型信息
	 *
	 * @author: 陈玉航
	 * @date: Created on 2019年6月29日 下午4:54:07
	 */
	@Override
	public BusinessTypeInfo queryYwlxInfoByNameAndNsrsbh(String ywlx, List<String> shList) {
        return businessTypeDao.queryYwlxInfoByNameAndNsrsbh(ywlx, shList);
    }

	/**
	 * 保存业务类型信息
	 *
	 * @author: 陈玉航
	 * @date: Created on 2019年6月29日 下午4:54:44
	 */
	@Override
	public void saveBusinessTypeInfo(BusinessTypeInfo bti) {
		businessTypeDao.saveBusinessTypeInfo(bti);
		
	}

	/**
	 * 更新业务类型信息
	 *
	 * @author: 陈玉航
	 * @date: Created on 2019年6月29日 下午7:54:12
	 */
	@Override
	public boolean updateYwlxInfo(BusinessTypeInfo bti, List<String> shList) {
        bti.setUpdateTime(new Date());
        int i = businessTypeDao.updateYwlxById(bti, shList);
        return i > 0;
    }

	/**
     *
     * 查询业务类型信息 页面模糊查询
     *
     * @see com.dxhy.order.api.ApiBusinessTypeService#queryYwlxByUserInfoMerge(java.util.Map)
	 * @author: 陈玉航
	 * @date: Created on 2019年7月3日 下午7:45:53
	 */
	/*@Override
    public List<String> queryYwlxByUserInfoMerge(Map<String, Object> map) {
        List<String> list = businessTypeDao.queryYwlxByUserInfoMerge(map);
		return list;
	}*/
	/**
     * 业务类型查询界面接口
     * @param map
	 * @return
	 * BusinessTypeController.java
	 * author wangruwei
	 * 2019年8月2日
	 */
	@Override
    public PageUtils selectYwlxByParam(Map<String, Object> map,List<String> xhfNsrsbh) {
		int pageSize = (Integer) map.get("pageSize");
		int currPage = (Integer) map.get("currPage");
		// 这里前端从1开始需要进行-1操作
		// currPage=currPage-1;
		log.info("订单查询，当前页：{},页面条数:{}", currPage, pageSize);
		PageHelper.startPage(currPage, pageSize);
		List<BusinessTypeInfo> list = businessTypeDao.selectYwlxByParam(map, xhfNsrsbh);
		PageInfo<BusinessTypeInfo> pageInfo = new PageInfo<>(list);
		PageUtils page = new PageUtils(pageInfo.getList(), (int) pageInfo.getTotal(), pageInfo.getPageSize(),
				pageInfo.getPageNum());
		return page;
	}
	/**
     * 业务类型和销方名称联动。
     *
     * @param map
     * @return BusinessTypeController.java
     * author wangruwei
     * 2019年7月10日
     */
	@Override
    public List<Map<String, Object>> queryYwlxOrNsrsbh(Map<String, Object> map, List<String> xhfNsrsbh) {
        return businessTypeDao.queryYwlxOrNsrsbh(map,xhfNsrsbh);
	}
	/**
	 * 验证，同一销货方下面的属性名称不能一样
	 * ame
	 * @param xhfNsrsbh
	 * @param id
	 * @return
	 * ApiBusinessTypeServiceImpl.java
	 * author wangruwei
	 * 2019年8月2日
	 */
	@Override
    public BusinessTypeInfo queryYwlxInfoByNameAndNsrsbhAndId(String businessName, List<String> xhfNsrsbh, String id) {
        return businessTypeDao.queryYwlxInfoByNameAndNsrsbhAndId(businessName, xhfNsrsbh, id);
	}
	
}
