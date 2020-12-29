package com.dxhy.order.dao;

import com.dxhy.order.model.entity.BuyerEntity;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

/**
 * @author liangyuhuan
 * @date 2018/7/31
 */
public interface BuyerDao {

    
    /**
     * 查询购方信息
     *
     * @param map
     * @param shList
     * @return
     */
    List<BuyerEntity> selectBuyerList(@Param("map") Map<String, Object> map, @Param("shList") List<String> shList);
    
    /**
     * 修改
     *
     * @param buyerEntity
     * @param shList
     * @return
     */
    int updateBuyer(@Param("buyer") BuyerEntity buyerEntity, @Param("shList") List<String> shList);
    
    /**
     * 添加
     *
     * @param buyerEntity
     * @return
     */
    int insertBuyer(BuyerEntity buyerEntity);
    
    /**
     * 根据购方名称查询
     *
     * @param map
     * @param shList
     * @return
     */
    int selectBuyerByName(@Param("map") Map<String, String> map, @Param("shList") List<String> shList);
    
    /**
     * 根据销方税号,购方名称和购方税号获取购方信息
     *
     * @param buyerEntity
     * @param shList
     * @return
     */
    List<BuyerEntity> selectBuyerByBuyerEntity(@Param("buyer") BuyerEntity buyerEntity, @Param("shList") List<String> shList);
    
    /**
     * 对外提供根据名称模糊查询
     *
     * @param purchaseName
     * @param shList
     * @return
     */
    List<BuyerEntity> selectBuyer(@Param("purchaseName") String purchaseName, @Param("shList") List<String> shList);
    
    /**
     * 删除
     *
     * @param id
     * @param shList
     * @return
     */
    int deleteBuyerById(@Param("id") String id, @Param("shList") List<String> shList);

}
