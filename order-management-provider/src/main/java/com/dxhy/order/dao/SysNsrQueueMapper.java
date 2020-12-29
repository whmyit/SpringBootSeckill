package com.dxhy.order.dao;

import com.dxhy.order.model.SysNsrQueue;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 税号队列表持久层
 *
 * @author ZSC-DXHY
 */
@Mapper
public interface SysNsrQueueMapper {
    
    /**
     * 根据id删除税号队列表数据
     *
     * @param id
     * @return
     */
    int deleteNsrQueueById(String id);
    
    /**
     * 插入税号队列表数据
     *
     * @param record
     * @return
     */
    int insertNsrQueueSelective(SysNsrQueue record);
    
    
    /**
     * 根据id查询税号对列表数据
     *
     * @param id
     * @return
     */
    SysNsrQueue selectNsrQueueById(String id);
    
    
    /**
     * 根据税号查询税号对列表数据
     *
     * @param nsrsbh
     * @param queuePrefix
     * @return
     */
    SysNsrQueue selectNsrQueueListByNsrsbh(@Param("nsrsbh") String nsrsbh, @Param("queuePrefix") String queuePrefix);
    
    /**
     * 获取税号队列列表数据
     *
     * @return
     */
    List<SysNsrQueue> selectNsrQueueList();
    
    
    /**
     * 更新税号对列表数据
     *
     * @param record
     * @return
     */
    int updateNsrQueueByPrimaryKeySelective(SysNsrQueue record);
    
}
