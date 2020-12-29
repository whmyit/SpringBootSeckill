package com.dxhy.order.dao;

import com.dxhy.order.model.entity.CommodityCodeEntity;
import com.dxhy.order.model.entity.SysDictionary;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

/**
 * 商品编码dao
 * todo 需要优化
 *
 * @author liangyuhuan
 * @date 2018/7/24
 */
public interface CommodityDao {
    /**
     * 查询商品编码信息
     *
     * @param map
     * @param shList
     * @return
     */
    List<CommodityCodeEntity> selectCommodity(@Param("map") Map<String, Object> map, @Param("shList") List<String> shList);
    
    /**
     * 查询商品税编信息
     *
     * @param map
     * @param shList
     * @return
     */
    List<CommodityCodeEntity> queryProductList(@Param("map") Map<String, String> map, @Param("shList") List<String> shList);
    
    /**
     * 修改商品信息
     *
     * @param codeEntity
     * @param shList
     * @return
     */
    int updateCommodity(@Param("commodityCode") CommodityCodeEntity codeEntity, @Param("shList") List<String> shList);
    
    /**
     * 根据id查询商品信息
     *
     * @param id
     * @param shList
     * @return
     */
    CommodityCodeEntity selectCommodityById(@Param(value = "id") String id, @Param("shList") List<String> shList);
    
    /**
     * 新增商品信息
     *
     * @param codeEntity
     * @return
     */
    int insertCommodity(CommodityCodeEntity codeEntity);
    
    /**
     * 删除商品信息
     *
     * @param id
     * @param shList
     * @return
     */
    int deleteCommodity(@Param("id") String id, @Param("shList") List<String> shList);
    
    /**
     * 根据分组id修改
     *
     * @param id
     * @param shList
     * @return
     */
    int updateByGropId(@Param("id") String id, @Param("shList") List<String> shList);
    
    /**
     * 商品信息查询
     *
     * @param merchandiseName
     * @param encoding
     * @param shList
     * @param cpylx
     * @return
     */
    List<CommodityCodeEntity> queryCommodityInfoList(@Param("merchandiseName") String merchandiseName, @Param("encoding") String encoding,
                                                     @Param("shList") List<String> shList, @Param("cpylx") String cpylx);
    
    /**
     * 获取优惠政策类型字典表
     *
     * @return
     */
    List<Map<String, String>> selectLicyTypeList();
    
    /**
     * 查询字典表   判断是否开通外网
     *
     * @return
     */
    SysDictionary querySysDictionary();
    
    /**
     * 查询商品信息列表
     *
     * @param shList
     * @return
     */
    List<CommodityCodeEntity> getCommodityCode(@Param("shList") List<String> shList);
    
    /**
     * 根据id查询商品信息
     *
     * @param id
     * @param shList
     * @return
     */
    Map<String, Object> queryCommodityById(@Param("id") String id, @Param("shList") List<String> shList);
    
    /**
     * 查询商品是否存在
     *
     * @param paramMap
     * @param shList
     * @return
     */
    int queryCommodityByMap(@Param("map") Map<String, Object> paramMap, @Param("shList") List<String> shList);
    
    /**
     * 查询商品信息是否存在
     *
     * @param map
     * @param shList
     * @return
     */
    int selectByNameAndCode(@Param("map") Map<String, String> map, @Param("shList") List<String> shList);
    
    /**
     * 新增查询 通用条件查询
     *
     * @param paramMap
     * @param shList
     * @return
     */
    List<CommodityCodeEntity> queryCommodityInfoListByMap(@Param("map") Map<String, String> paramMap, @Param("shList") List<String> shList);
}
