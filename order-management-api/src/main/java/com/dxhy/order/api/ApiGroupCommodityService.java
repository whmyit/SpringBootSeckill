package com.dxhy.order.api;

import com.dxhy.order.model.R;
import com.dxhy.order.model.entity.GroupCommodity;

import java.util.List;
import java.util.Map;

/**
 * @author liangyuhuan
 * @date 2018/7/30
 */
public interface ApiGroupCommodityService {
    /**
     * 查询分组
     *
     * @param userId
     * @return
     */
    List<GroupCommodity> queryGroupList(String userId);
    
    /**
     * 保存
     *
     * @param croupCommodity
     * @param shList
     * @return
     */
    R saveGroup(GroupCommodity croupCommodity, List<String> shList);
    
    /**
     * 校验
     *
     * @param map
     * @return
     */
    R checkGroup(Map<String, String> map);
    
    /**
     * 分组删除
     *
     * @param id
     * @param shList
     * @return
     */
    R removeGroup(String id, List<String> shList);
    
    /**
     * 上传分组
     *
     * @param groupCommodity
     * @return
     */
    R uploadGrop(List<GroupCommodity> groupCommodity);
}
