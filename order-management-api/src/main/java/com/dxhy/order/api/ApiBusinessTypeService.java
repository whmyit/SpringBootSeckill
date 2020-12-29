package com.dxhy.order.api;

import com.dxhy.order.model.BusinessTypeInfo;
import com.dxhy.order.model.PageUtils;

import java.util.List;
import java.util.Map;

/**
 *
 * 业务类型接口
 *
 * @author 陈玉航
 * @version 1.0 Created on 2019年6月29日 下午4:20:51
 */
public interface ApiBusinessTypeService {

	/**
	 * 根据业务类型名称税号查询业务类型信息
	 *
	 * @param ywlx   业务类型名称
	 * @param shList 销售方纳税人识别号
	 * @return BusinessTypeInfo 业务类型信息
	 * @author: 陈玉航
	 * @date: Created on 2019年6月29日 下午4:37:25
	 */
	BusinessTypeInfo queryYwlxInfoByNameAndNsrsbh(String ywlx, List<String> shList);

	/**
	 * 保存业务类型信息
	 *
	 * @param bti void
	 * @author: 陈玉航
	 * @date: Created on 2019年6月29日 下午4:53:11
	 */
	void saveBusinessTypeInfo(BusinessTypeInfo bti);

	/**
     *
     * 查询业务类型信息根据当前登陆人信息
     *
	 * @param map
	 * @return PageUtils
	 * @author: 陈玉航
	 * @date: Created on 2019年6月29日 下午7:26:02
	 */
//    PageUtils queryYwlxByUserInfo(Map<String, Object> map);

	/**
	 * 更新业务类型信息
	 *
	 * @param bti
	 * @param shList
	 * @return boolean
	 * @author: 陈玉航
	 * @date: Created on 2019年6月29日 下午7:53:39
	 */
	boolean updateYwlxInfo(BusinessTypeInfo bti, List<String> shList);
	
	/**
	 * 业务类型查询界面接口
	 * @param csmap
	 * @param xhfNsrsbh
	 * @return
	 * BusinessTypeController.java
	 * author wangruwei
	 * 2019年8月2日
	 */
    PageUtils selectYwlxByParam(Map<String, Object> csmap,List<String> xhfNsrsbh);
	
	/**
	 * 业务类型和销方名称联动。
	 * @param map
	 * @param xhfNsrsbh
	 * @return
	 * BusinessTypeController.java
	 * author wangruwei
	 * 2019年7月10日
     */
    List<Map<String, Object>> queryYwlxOrNsrsbh(Map<String, Object> map ,List<String> xhfNsrsbh);
	/**
	 * 验证，同一销货方下面的属性名称不能一样
	 * @param businessName
	 * @param xhfNsrsbh
	 * @param id
	 * @return
	 * ApiBusinessTypeService.java
	 * author wangruwei
	 * 2019年8月2日
	 */
	BusinessTypeInfo queryYwlxInfoByNameAndNsrsbhAndId(String businessName, List<String> xhfNsrsbh, String id);
	
}
