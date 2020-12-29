package com.dxhy.order.api;

import com.dxhy.order.model.PageUtils;
import com.dxhy.order.model.R;
import com.dxhy.order.model.entity.GroupTaxClassCodeEntity;

import java.util.List;
import java.util.Map;

/**
 * @Description: 集团税编库
 * @Author:xueanna
 * @Date:2019/9/17
 */
public interface ApiGroupTaxClassCodeService {
    /**
     * 查询税收商品分类编码
     *
     * @param map
     * @return
     */
    PageUtils queryGroupTaxClassCode(Map<String, Object> map);
    
    /**
     * 处理采集下级数据
     *
     * @param taxpayerCodeList
     * @param deptid
     * @return
     */
    R collectTaxClassCode(List<String> taxpayerCodeList, String deptid);
    
    /**
     * 集团税编处理共享数据状态
     *
     * @param taxClassCodeIdArray
     * @param shareStatus
     * @return
     */
    R taxClassCodeHandleShareStatus(String[] taxClassCodeIdArray, String shareStatus);
    
    /**
     * 集团税编处理启用数据状态
     *
     * @param taxClassCodeIdArray
     * @param dataStatus
     * @return
     */
    R taxClassCodeHandleDataStatus(String[] taxClassCodeIdArray, String dataStatus);
    
    /**
     * 保存集团税编信息
     *
     * @param groupTaxClassCodeEntity
     * @param deptId
     * @return
     */
    R saveGroupTaxClassCode(GroupTaxClassCodeEntity groupTaxClassCodeEntity, String deptId);
    
    /**
     * 集团税编详情
     *
     * @param groupTaxClassCodeId
     * @return
     */
    R queryGroupTaxClassCodeDetail(String groupTaxClassCodeId);
    
    /**
     * 逻辑删除集团税编
     *
     * @param groupTaxClassCodeId
     * @param deptId
     * @return
     */
    R delGroupTaxClassCode(String groupTaxClassCodeId, String deptId);
    
    /**
     * 更新分组
     *
     * @param groupTaxClassCodeEntity
     * @return
     */
    R updateGroupTaxClassCode(GroupTaxClassCodeEntity groupTaxClassCodeEntity);
    
    /**
     * 模板导入更新数据
     *
     * @param commodityCodeEntityList
     * @param paraMap
     * @return
     */
    R uploadCommodityCode(List<GroupTaxClassCodeEntity> commodityCodeEntityList, Map<String, String> paraMap);
}
