package com.dxhy.order.service.manager;

import com.dxhy.order.api.ApiDictionaryService;
import com.dxhy.order.dao.DictionaryDao;
import com.dxhy.order.model.entity.DictionaryEntity;
import org.apache.dubbo.config.annotation.Service;

import javax.annotation.Resource;
import java.util.List;
/**
 * 字典表业务实现类
 *
 * @author ZSC-DXHY
 * @date 创建时间: 2020-08-14 14:42
 */
@Service
public class DictionaryServiceImpl implements ApiDictionaryService {
    
    @Resource
    private DictionaryDao dictionaryDao;
    
    @Override
	public List<DictionaryEntity> queryDictionaries(String type) {
        return dictionaryDao.selectDictionaries(type);
	}
}
