package com.dxhy.order.api;

import com.dxhy.order.model.R;
import com.dxhy.order.model.entity.RuleSplitEntity;

/**
 * @author liangyuhuan
 * @date 2018/10/23
 * 拆分规则Service
 */
public interface ApiRuleSplitService {
    /**
     * 获取拆分规则
     *
     * @param taxpayerCode
     * @param userId
     * @return
     */
	RuleSplitEntity queryRuleSplit(String taxpayerCode, String userId);
    
    /**
     * 保存拆分规则
     *
     * @param ruleSplitEntity
     * @return
     */
    R saveRuleSplit(RuleSplitEntity ruleSplitEntity);
}
