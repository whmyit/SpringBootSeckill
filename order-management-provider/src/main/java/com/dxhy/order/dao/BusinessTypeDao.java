package com.dxhy.order.dao;

import com.dxhy.order.model.BusinessTypeInfo;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

/**
 * 业务类型控制层
 *
 * @author 陈玉航
 * @version 1.0 Created on 2019年6月29日 下午4:14:03
 */
public interface BusinessTypeDao {
    
    /***
     *
     * 根据业务类型名称和税号查询业务类型信息
     *
     * @param ywlx
     * @param shList
     * @return BusinessTypeInfo
     * @author: 陈玉航
     * @date: Created on 2019年6月29日 下午4:55:51
     */
    BusinessTypeInfo queryYwlxInfoByNameAndNsrsbh(@Param("ywlx") String ywlx, @Param("shList") List<String> shList);
    
    /**
     * 保存业务类型信息
     *
     * @param bti void
     * @author: 陈玉航
     * @date: Created on 2019年6月29日 下午4:56:08
     */
    void saveBusinessTypeInfo(BusinessTypeInfo bti);
    
    /**
     * 更新业务类型信息
     *
     * @param bti
     * @param shList
     * @return int
     * @author: 陈玉航
     * @date: Created on 2019年6月29日 下午7:55:22
     */
    int updateYwlxById(@Param("ywlx") BusinessTypeInfo bti, @Param("shList") List<String> shList);
    
    /**
     * 业务类型查询界面接口
     *
     * @param map
     * @param shList
     * @return BusinessTypeController.java
     * author wangruwei
     * 2019年8月2日
     */
    List<BusinessTypeInfo> selectYwlxByParam(@Param("map") Map<String, Object> map, @Param("shList") List<String> shList);
    
    /**
     * 业务类型和销方名称联动。
     *
     * @param map
     * @param shList
     * @return BusinessTypeController.java
     * author wangruwei
     * 2019年7月10日
     */
    List<Map<String, Object>> queryYwlxOrNsrsbh(@Param("map") Map<String, Object> map, @Param("shList") List<String> shList);
    
    /**
     * 验证，同一销货方下面的属性名称不能一样
     *
     * @param ywlx
     * @param shList
     * @param id
     * @return ApiBusinessTypeServiceImpl.java
     * author wangruwei
     * 2019年8月2日
     */
    BusinessTypeInfo queryYwlxInfoByNameAndNsrsbhAndId(@Param("ywlx") String ywlx, @Param("shList") List<String> shList, @Param("id") String id);
    
}
