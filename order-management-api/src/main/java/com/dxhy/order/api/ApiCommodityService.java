package com.dxhy.order.api;

import com.dxhy.order.model.CommodityCodeInfo;
import com.dxhy.order.model.CommodityTaxClassCodeParam;
import com.dxhy.order.model.PageUtils;
import com.dxhy.order.model.R;
import com.dxhy.order.model.entity.CommodityCodeEntity;
import com.dxhy.order.model.entity.SysDictionary;

import java.util.List;
import java.util.Map;

/**
 *
 * @author liangyuhuan
 * @date 2018/7/23
 */
public interface ApiCommodityService {
    /**
     * 查询商品信息
     *
     * @param map
     * @param xhfNsrsbh
     * @return
     */
    PageUtils queryCommodity(Map<String, Object> map,List<String> xhfNsrsbh);
    
    /**
     * 保存
     *
     * @param codeEntity
     * @return
     */
    boolean addOrEditCommodity(CommodityCodeEntity codeEntity);
    
    /**
     * 删除
     *
     * @param ids
     * @return
     */
    R deleteCommodity(List<Map> ids);
    
    /**
     * 根据id查询商品信息
     * @param id
     * @param xhfNsrsbh
     * @return
     */
    R queryCommodityById(String id,List<String> xhfNsrsbh);
    
    
    /**
     * 校验重复商品信息
     * @param map
     * @param shList
     * @return
     */
    R checkRepeat(Map<String, String> map, List<String> shList);
    
    /**
     * 导入商品编码
     *
     * @param commodityCodeEntity
     * @return
     */
    R uploadCommodityCode(List<CommodityCodeEntity> commodityCodeEntity);
    
    /**
     * 商品编码 encoding 商品编码 cpylx 成品油类型 1 成品油 0 非成品油
     *
     * @param merchandiseName
     * @param encoding
     * @param xhfNsrsbh
     * @param cpylx
     * @return
     */
    List<CommodityCodeEntity> queryCommodityInfoList(String merchandiseName, String encoding, List<String> xhfNsrsbh, String cpylx);
    
    /**
     * 获取优惠政策类型
     *
     * @return
     */
    R querypoLicyTypeList();
    
    /**
     * 获取大数据接口
     *
     * @return
     */
    SysDictionary querySysDictionary();
    
    /**
     * 校验购方信息
     *
     * @param commodityCodeList
     * @return
     */
    R checkParams(List<CommodityCodeEntity> commodityCodeList);

    /**
     * 同步集团税编信息
     * @param taxClassCodeIdArray
     * @param userId
     * @param nsrsbh
     * @param name
     * @return
     */
    R syncGroupTaxClassCode(List<CommodityTaxClassCodeParam> taxClassCodeIdArray, String userId, String nsrsbh, String name);
    
    /**
     * 商品税收信息初始化
     * @param info
     * @return
     */
    R initCommodityTaxClassCode(List<CommodityCodeInfo> info);
    
    /**
     * 商品信息停用启用
     *
     * @param taxClassCodeIdArray
     * @param dataStatus
     * @return
     */
    R commodityHandleDataStatus(List<Map> taxClassCodeIdArray, String dataStatus);
    
    /**
     * 同步商品信息
     *
     * @param codeEntity    商品编码实体类
     * @param operationType 操作类型（0:新增,1:更新,2:删除）
     * @return com.dxhy.order.model.R
     */
    R syncCommodity(CommodityCodeEntity codeEntity, String operationType);
    
    /**
     * 查询税收分类编码 或 商品名称查询
     *
     * @param map
     * @param shList
     * @return
     */
    List<CommodityCodeEntity> queryProductList(Map<String, String> map, List<String> shList);
    
    /**
     * 商品信息列表
     * @param paramMap
     * @param shList
     * @return
     */

    PageUtils queryCommodityInfoListByMap(Map<String,String> paramMap,List<String> shList);
}
