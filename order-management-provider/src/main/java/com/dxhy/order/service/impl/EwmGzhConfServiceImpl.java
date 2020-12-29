package com.dxhy.order.service.impl;

import com.dxhy.order.api.ApiEwmGzhConfService;
import com.dxhy.order.dao.EwmGzhConfigMapper;
import com.dxhy.order.model.EwmGzhConfig;
import org.apache.dubbo.config.annotation.Service;

import javax.annotation.Resource;
import java.util.List;

/**
 * 公众号配置业务实现类
 *
 * @author ZSC-DXHY
 * @date 创建时间: 2020-08-14 14:47
 */
@Service
public class EwmGzhConfServiceImpl implements ApiEwmGzhConfService {
    
    @Resource
    EwmGzhConfigMapper ewmGzhConfigMapper;
    
    
    @Override
    public List<EwmGzhConfig> queryEwmGzhConfigList(EwmGzhConfig ewmGzhConfig, List<String> shList) {
        return ewmGzhConfigMapper.selectListByEwmGzhConfig(ewmGzhConfig, shList);
    }

    @Override
    public EwmGzhConfig queryEwmGzhConfInfo(EwmGzhConfig ewmGzhConfig) {
        return ewmGzhConfigMapper.selectByEwmGzhConfig(ewmGzhConfig);
    }

    @Override
    public int updateEwmGzhConfByPrimaryKey(EwmGzhConfig ewmGzhConfig) {
        return ewmGzhConfigMapper.updateByPrimaryKeySelective(ewmGzhConfig);
    }

    @Override
    public int addEwmGzhConfInfo(EwmGzhConfig ewmGzhConfig) {
        return ewmGzhConfigMapper.insert(ewmGzhConfig);
    }
}
