package com.dxhy.order.api;

import com.dxhy.order.model.PageUtils;
import com.dxhy.order.model.R;
import com.dxhy.order.model.entity.BuyerEntity;

import java.util.List;
import java.util.Map;

/**
 * @author liangyuhuan
 * @date 2018/7/31
 */
public interface ApiBuyerService {
    /**
     * 查询购方信息
     *
     * @param map
     * @param xhfNsrsbh
     * @return
     */
    PageUtils queryBuyerList(Map<String, Object> map,List<String> xhfNsrsbh);
    
    /**
     * 对外提供根据购方信息名称模糊查询
     *
     * @param purchaseName
     * @param xhfNsrsbh
     * @return
     */
    List<BuyerEntity> queryBuyerByName(String purchaseName, List<String> xhfNsrsbh);
    
    /**
     * 删除
     *
     * @param ids
     * @return
     */
    R removeBuyerbyId(List<Map> ids);
    
    /**
     * 导入
     *
     * @param buyerEntityList
     * @return
     */
    R uploadGrop(List<BuyerEntity> buyerEntityList);
    
    /**
     * 购方信息根据名称查询
     *
     * @param purchaseName
     * @param xhfNsrsbh
     * @return
     */
    BuyerEntity queryBuyerByPurchaseName(String purchaseName, String xhfNsrsbh);
    
    /**
     * 根据销方纳税识别号和唯一编码查询
     *
     * @param xhfNsrsbh
     * @param buyerCode
     * @return
     */
    BuyerEntity queryBuyerInfoByxhfNsrsbhAndBuyerCode(String xhfNsrsbh, String buyerCode);
    
    /**
     * 同步购货方信息
     *
     * @param buyerEntit    购方信息实体类
     * @param operationType 操作类型（0:新增,1:更新,2:删除）
     * @return com.dxhy.order.model.R
     * @author <a href="tivenninesongs@163.com">yaoxuguang</a>
     * @date 2020/4/21
     */
    R syncBuyer(BuyerEntity buyerEntit, String operationType);
    
    /**
     * 插入或更新购方信息
     *
     * @param buyerEntity
     * @return
     */
    R saveOrUpdateBuyerInfo(BuyerEntity buyerEntity);
    
    /**
     * 批量保存购方信息
     *
     * @param buyerList
     * @param userId
     * @return
     */
    R saveBuyerInfoList(List<BuyerEntity> buyerList, String userId);
}
