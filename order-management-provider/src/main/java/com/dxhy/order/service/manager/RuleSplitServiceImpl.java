package com.dxhy.order.service.manager;

import com.dxhy.order.api.ApiInvoiceCommonService;
import com.dxhy.order.api.ApiRuleSplitService;
import com.dxhy.order.constant.ConfigureConstant;
import com.dxhy.order.constant.OrderManagementConstant;
import com.dxhy.order.dao.RuleSplitDao;
import com.dxhy.order.model.R;
import com.dxhy.order.model.entity.RuleSplitEntity;
import lombok.extern.slf4j.Slf4j;
import org.apache.dubbo.config.annotation.Service;

import javax.annotation.Resource;

/**
 *
 * @author liangyuhuan
 * @date 2018/10/23
 * 拆分规则接口实现类
 */
@Slf4j
@Service
public class RuleSplitServiceImpl implements ApiRuleSplitService {
    @Resource
    private ApiInvoiceCommonService apiInvoiceCommonService;

    @Resource
    private RuleSplitDao ruleSplitMapper;
    
    @Override
    public RuleSplitEntity queryRuleSplit(String taxpayerCode, String userId) {
        return ruleSplitMapper.selectRuleSplit(taxpayerCode, userId);
    }

    @Override
    public R saveRuleSplit(RuleSplitEntity ruleSplitEntity) {
        R r = new R();
        log.info("参数{}",ruleSplitEntity.toString());
        RuleSplitEntity ruleSplit = ruleSplitMapper.selectRuleSplit(ruleSplitEntity.getTaxpayerCode(), ruleSplitEntity.getUserId());
        //有值 走修改  无值  走保存
        if (ruleSplit == null){
            log.info("没有值  新增");
            String uuid = apiInvoiceCommonService.getGenerateShotKey();
            ruleSplitEntity.setId(uuid);
            int i =  ruleSplitMapper.insert(ruleSplitEntity);
            if(i > 0 ){
                R.ok().put("msg", "保存成功");
            }else{
                r.put(OrderManagementConstant.CODE, ConfigureConstant.STRING_9999).put("msg", "保存失败");
            }
        }else{
            ruleSplit.setRuleSplitType(ruleSplitEntity.getRuleSplitType());
            int i = ruleSplitMapper.update(ruleSplit);
            if(i > 0 ){
                R.ok().put("msg", "保存成功");
            }else{
                r.put(OrderManagementConstant.CODE, ConfigureConstant.STRING_9999).put("msg", "保存失败");
            }
        }
        return r;
    }
}
