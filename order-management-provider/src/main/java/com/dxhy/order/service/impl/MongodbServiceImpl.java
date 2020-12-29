package com.dxhy.order.service.impl;

import com.dxhy.order.api.MongodbService;
import org.apache.dubbo.config.annotation.Service;
import org.springframework.data.mongodb.core.MongoTemplate;

import javax.annotation.Resource;

/**
 * MongoDB 服务接口实现类
 *
 * @author: <a href="tivenninesongs@163.com">yaoxuguang</a>
 * @createDate: Created in 2020/5/11
 */
@Service
public class MongodbServiceImpl implements MongodbService {
    
    @Resource
    private MongoTemplate mongoTemplate;


    @Override
    public <T> T find(Object id,Class<T>clazz,String collectionName) {
        return mongoTemplate.findById(id,clazz,collectionName);
    }

    @Override
    public <T> T save(T data, String collectionName) {
        mongoTemplate.insert(data,collectionName);
        return data;
    }
}
