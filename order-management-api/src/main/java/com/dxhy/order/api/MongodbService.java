package com.dxhy.order.api;

/**
 * MongoDB 服务接口
 *
 * @author: <a href="tivenninesongs@163.com">yaoxuguang</a>
 * @createDate: Created in 2020/5/11
 */
public interface MongodbService {
    /**
     * 查询数据
     *
     * @param id
     * @param clazz
     * @param collectionName
     * @param <T>
     * @return
     */
    <T> T find(Object id, Class<T> clazz, String collectionName);
    
    /**
     * 保存
     *
     * @param data           数据
     * @param collectionName 集合名称
     * @return T
     * @author <a href="tivenninesongs@163.com">yaoxuguang</a>
     * @date 2020/5/12
     */
    <T> T save(T data, String collectionName);
}
