package com.dxhy.order.api;

import java.util.List;
import java.util.Set;

/**
 * redis缓存服务
 *
 * @author ZSC-DXHY
 */
public interface RedisService {
    
    /**
     * 给一个key值设置过期时间
     *
     * @param key
     * @param seconds
     * @return
     */
    Boolean expire(String key, int seconds);
    
    /**
     * 删除缓存中得对象，根据key
     *
     * @param key
     * @return
     */
    boolean del(String key);
    
    /**
     * 根据key 获取内容
     *
     * @param key
     * @return
     */
    String get(String key);
    
    /**
     * 根据key 获取对象
     *
     * @param key
     * @param clazz
     * @return
     */
    <T> T get(String key, Class<T> clazz);
//
//    /**
//     * 返回 key 中字符串值的子字符
//     *
//     * @param key
//     * @param start
//     * @param end
//     * @return
//     */
//    String getRange(String key, long start, long end);
//
//    /**
//     * 将给定 key 的值设为 value ，并返回 key 的旧值(old value)。
//     *
//     * @param key
//     * @param value
//     * @return
//     */
//    String getSet(String key, String value);
//
//    /**
//     * 对 key 所储存的字符串值，获取指定偏移量上的位(bit)。
//     *
//     * @param key
//     * @param offset
//     * @return
//     */
//    Boolean getBit(String key, long offset);
//
//
//    /**
//     * 将值 value 关联到 key ，并将 key 的过期时间设为 seconds (以秒为单位)。
//     *
//     * @param key
//     * @param seconds
//     * @param value
//     * @return
//     */
//    boolean setEx(String key, int seconds, String value);
//
//    /**
//     * 用 value 参数覆写给定 key 所储存的字符串值，从偏移量 offset 开始。
//     *
//     * @param key
//     * @param offset
//     * @param value
//     * @return
//     */
//    boolean setRange(String key, long offset, String value);
//
//    /**
//     * 返回 key 所储存的字符串值的长度。
//     *
//     * @param key
//     * @return
//     */
//    long strLen(String key);
//
//
//    /**
//     * 这个命令和 SETEX 命令相似，但它以毫秒为单位设置 key 的生存时间，而不是像 SETEX 命令那样，以秒为单位。
//     *
//     * @param key
//     * @param value
//     * @param milliseconds
//     * @return
//     */
//    boolean pSetEx(String key, String value, long milliseconds);
//
//    /**
//     * 将 key 中储存的数字值增一。
//     *
//     * @param key
//     * @return
//     */
//    long iNcr(String key);
//
//    /**
//     * 将 key 所储存的值加上给定的增量值（increment） 。
//     *
//     * @param key
//     * @param increment
//     * @return
//     */
//    long incrBy(String key, long increment);
//
//    /**
//     * 将 key 所储存的值加上给定的浮点增量值（increment） 。
//     *
//     * @param key
//     * @param increment
//     * @return
//     */
//    double incrByFloat(String key, double increment);
//
//    /**
//     * 将 key 中储存的数字值减一。
//     *
//     * @param key
//     * @return
//     */
//    long decr(String key);
//
//    /**
//     * key 所储存的值减去给定的减量值（decrement） 。
//     *
//     * @param key
//     * @param increment
//     * @return
//     */
//    long decrBy(String key, long increment);
//
    
    /**
     * 如果 key 已经存在并且是一个字符串， APPEND 命令将 指定value 追加到改 key 原来的值（value）的末尾。
     *
     * @param key
     * @param value
     * @return
     */
    long append(String key, String value);
    
    /**
     * setNX
     *
     * @param key
     * @param value
     * @return
     */
    boolean setNx(String key, String value);
    
    /**
     * 向缓存中设置字符串内容
     *
     * @param key
     * @param value
     * @return
     */
    boolean set(String key, String value);

//
//    /**
//     * 对 key 所储存的字符串值，设置或清除指定偏移量上的位(bit)。
//     *
//     * @param key
//     * @param offset
//     * @param value
//     * @return
//     */
//    boolean setBit(String key, long offset, boolean value);
//
    
    /**
     * 保存缓存
     *
     * @param key
     * @param value
     * @param seconds 有效时间/秒
     * @return
     */
    boolean set(String key, String value, int seconds);
    
    /**
     * 保存缓存
     *
     * @param key
     * @param value   对象，转为json字符串后存入redis
     * @param seconds 有效时间，单位为秒
     * @return
     */
    boolean set(String key, Object value, int seconds);
//
//    /**
//     * 移出并获取列表的第一个元素， 如果列表没有元素会阻塞列表直到等待超时或发现可弹出元素为止。
//     *
//     * @param key
//     * @param timeout
//     * @return
//     */
//    String bLPop(String key, Integer timeout);
//
//    /**
//     * 移出并获取列表的最后一个元素， 如果列表没有元素会阻塞列表直到等待超时或发现可弹出元素为止。
//     *
//     * @param key
//     * @param timeout
//     * @return
//     */
//    String bRPop(String key, Integer timeout);
//
//
//    /**
//     * 通过索引获取列表中的元素
//     *
//     * @param key
//     * @param index
//     * @return
//     */
//    String lIndex(String key, Long index);
//
//    /**
//     * 在列表的元素前或者后插入元素
//     *
//     * @param key
//     * @param where BEFORE|AFTER
//     * @param pivot 在该元素前或后插入元素
//     * @param value
//     * @return
//     */
////    long lInsert(String key, RedisListCommands.Position where, String pivot, String value);
//
//    /**
//     * 获取列表长度
//     *
//     * @param key
//     * @return
//     */
//    Long lLen(String key);
//
//    /**
//     * 移出并获取列表的第一个元素
//     *
//     * @param key
//     * @return
//     */
//    String lPop(String key);
//
    
    /**
     * 将一个或多个值插入到列表头部
     *
     * @param key
     * @param value
     * @return
     */
    long lPush(String key, String... value);
//
//    /**
//     * 将一个值插入到已存在的列表头部
//     *
//     * @param key
//     * @param value
//     * @return
//     */
//    long lPushX(String key, String value);
//
//    /**
//     * 获取列表指定范围内的元素
//     *
//     * @param key
//     * @param start
//     * @param stop
//     * @return
//     */
//    List<String> lRange(String key, long start, long stop);
//
//    /**
//     * 移除列表元素
//     *
//     * @param key
//     * @param count count > 0 : 从表头开始向表尾搜索，移除与 VALUE 相等的元素，数量为 COUNT 。
//     *              count < 0 : 从表尾开始向表头搜索，移除与 VALUE 相等的元素，数量为 COUNT 的绝对值。
//     *              count = 0 : 移除表中所有与 VALUE 相等的值
//     * @param value
//     * @return 被移除元素的数量，不存在时返回0
//     */
//    long lRem(String key, long count, String value);
//
//    /**
//     * 通过索引设置列表元素的值
//     *
//     * @param key
//     * @param index
//     * @param value
//     * @return
//     */
//    boolean lSet(String key, long index, String value);
//
//    /**
//     * 对一个列表进行修剪(trim)，就是说，让列表只保留指定区间内的元素，不在指定区间之内的元素都将被删除。
//     *
//     * @param key
//     * @param start
//     * @param stop
//     * @return
//     */
//    boolean lTrim(String key, long start, long stop);
//
    
    /**
     * 移除并获取列表最后一个元素
     *
     * @param key
     * @return
     */
    String rPop(String key);
    
    /**
     * 在列表中添加一个或多个值
     *
     * @param key
     * @param value
     * @return
     */
    long rPush(String key, String... value);
//
//    /**
//     * 为已存在的列表添加值
//     *
//     * @param key
//     * @param value
//     * @return
//     */
//    long rPushX(String key, String value);
//
//    /**
//     * 删除一个或多个哈希表字段
//     *
//     * @param key
//     * @param field
//     * @return
//     */
//    long hDel(String key, String... field);
//
//    /**
//     * 查看哈希表 key 中，指定的字段是否存在。
//     *
//     * @param key
//     * @param field
//     * @return
//     */
//    boolean hExists(String key, String field);
//
//    /**
//     * 获取存储在哈希表中指定字段的值。
//     *
//     * @param key
//     * @param field
//     * @return
//     */
//    String hGet(String key, String field);
//
//    /**
//     * 获取在哈希表中指定 key 的所有字段和值
//     *
//     * @param key
//     * @return
//     */
//    Map<String, String> hGetAll(String key);
//
//    /**
//     * 为哈希表 key 中的指定字段的整数值加上增量 increment 。
//     *
//     * @param key
//     * @param field
//     * @param increment
//     * @return
//     */
//    long hIncrBy(String key, String field, long increment);
//
//    /**
//     * 为哈希表 key 中的指定字段的浮点数值加上增量 increment 。
//     *
//     * @param key
//     * @param field
//     * @param increment
//     * @return
//     */
//    double hIncrByFoloat(String key, String field, double increment);
//
//    /**
//     * 获取所有哈希表中的字段
//     *
//     * @param key
//     * @return
//     */
//    Set<String> hKeys(String key);
//
//    /**
//     * 获取哈希表中字段的数量
//     *
//     * @param key
//     * @return
//     */
//    long hLen(String key);
//
//    /**
//     * 获取所有给定字段的值
//     *
//     * @param key
//     * @param field
//     * @return
//     */
//    List<String> hMGet(String key, String... field);
//
//    /**
//     * 同时将多个 field-value (域-值)对设置到哈希表 key 中。
//     *
//     * @param key
//     * @param items
//     * @return
//     */
//    boolean hMSet(String key, Map<String, String> items);
//
//    /**
//     * 将哈希表 key 中的字段 field 的值设为 value 。
//     *
//     * @param key
//     * @param field
//     * @param value
//     * @return
//     */
//    boolean hSet(String key, String field, String value);
//
//    /**
//     * 只有在字段 field 不存在时，设置哈希表字段的值。
//     *
//     * @param key
//     * @param field
//     * @param value
//     * @return
//     */
//    boolean hSetNx(String key, String field, String value);
//
//    /**
//     * 获取哈希表中所有值
//     *
//     * @param key
//     * @return
//     */
//    List<String> hVals(String key);
//
//    /**
//     * 迭代哈希表中的键值对。
//     *
//     * @param key
//     * @param pattern
//     * @param count
//     * @return
//     */
////    Cursor<Map.Entry<byte[], byte[]>> hScan(String key, String pattern, long count);
//
//    /**
//     * 向集合添加一个或多个成员
//     *
//     * @param key
//     * @param members
//     * @return
//     */
//    long sAdd(String key, String... members);
//
//    /**
//     * 获取集合的成员数
//     *
//     * @param key
//     * @return
//     */
//    long sCard(String key);
//
//    /**
//     * @param key
//     * @param member
//     * @return
//     */
//    boolean sIsMember(String key, String member);
//
//    /**
//     * @param key
//     * @return
//     */
//    Set<String> sMembers(String key);
//
//    /**
//     * @param key
//     * @return
//     */
//    String sPop(String key);
//
//    /**
//     * @param key
//     * @param count
//     * @return
//     */
//    List<String> sRandMember(String key, Long count);
//
//    /**
//     * @param key
//     * @return
//     */
//    String sRandMember(String key);
//
//    /**
//     * @param key
//     * @param member
//     * @return
//     */
//    long sRem(String key, String... member);
//
//    /**
//     * @param key
//     * @param cursor
//     * @param pattern
//     * @param count
//     * @return
//     */
////    Cursor<byte[]> sScan(String key, String cursor, String pattern, long count);
//
//    /**
//     * 向有序集合添加多个成员，或者更新已存在成员的分数
//     *
//     * @param key
//     * @param tuples
//     * @return
//     */
////    long zAdd(String key, Set<RedisZSetCommands.Tuple> tuples);
//
//    /**
//     * 向有序集合添加一个成员，或者更新已存在成员的分数
//     *
//     * @param key
//     * @param score
//     * @param value
//     * @return
//     */
//    boolean zAdd(String key, String value, double score);
//
//    /**
//     * 获取有序集合的成员数
//     *
//     * @param key
//     * @return
//     */
//    long zCard(String key);
//
//    /**
//     * 计算在有序集合中指定区间分数的成员数
//     *
//     * @param key
//     * @param min
//     * @param max
//     * @return
//     */
//    long zCount(String key, double min, double max);
//
//    /**
//     * 有序集合中对指定成员的分数加上增量 increment
//     *
//     * @param key
//     * @param increment
//     * @param member
//     * @return
//     */
//    double zIncrBy(String key, double increment, String member);
//
//    /**
//     * 通过索引区间返回有序集合成指定区间内的成员
//     *
//     * @param key
//     * @param start
//     * @param stop
//     * @return
//     */
//    Set<String> zRange(String key, long start, long stop);
//
//    Set zRange(String key, long start, long stop, Class clazz);
//
//    Set<String> zRangeByLex(String key);
//
//    Set<String> zRangeByLex(String key, String min, String max);
//
//    /**
//     * Redis Zrangebylex 通过字典区间返回有序集合的成员。
//     *
//     * @param key
//     * @param min
//     * @param max
//     * @param count
//     * @param offset
//     * @return
//     */
//    Set<String> zRangeByLex(String key, String min, String max, int count, int offset);
//
//    /**
//     * 通过分数返回有序集合指定区间内的成员
//     *
//     * @param key
//     * @param min
//     * @param max
//     * @return
//     */
//    Set<String> zRangeByScore(String key, double min, double max);
//
//    Set<String> zRangeByScore(String key, double min, double max, long count, long offset);
//
//    /**
//     * 返回有序集合中指定成员的索引
//     *
//     * @param key
//     * @param member
//     * @return
//     */
//    long zRank(String key, String member);
//
//    /**
//     * 移除有序集合中的一个或多个成员
//     *
//     * @param key
//     * @param member
//     * @return
//     */
//    long zRem(String key, String... member);
//
//
//    /**
//     * @param key
//     * @param start
//     * @param stop
//     * @return
//     */
//    long zRemRangeByRank(String key, long start, long stop);
//
//    /**
//     * @param key
//     * @param min
//     * @param max
//     * @return
//     */
//    long zRemRangeByScore(String key, double min, double max);
//
//    Set<String> zRevRange(String key, long start, long stop);
//
//    /**
//     * @param key
//     * @param start
//     * @param stop
//     * @return
//     */
//    Set zRevRange(String key, long start, long stop, Class clazz);
//
//    /**
//     * @param key
//     * @param min
//     * @param max
//     * @return
//     */
//    Set<String> zRevRangeByScore(String key, double min, double max);
//
//    Set<String> zRevRangeByScore(String key, double min, double max, int offset, int count);
//
//    /**
//     * 返回有序集合中指定成员的排名，有序集成员按分数值递减(从大到小)排序
//     *
//     * @param key
//     * @param member
//     * @return
//     */
//    long zRevRank(String key, String member);
//
//    /**
//     * 返回有序集中，成员的分数值
//     *
//     * @param key
//     * @param member
//     * @return
//     */
//    double zScore(String key, String member);
//
//    /**
//     * 迭代有序集合中的元素（包括元素成员和元素分值）
//     *
//     * @param key
//     * @param ursor
//     * @param pattern
//     * @param count
//     * @return
//     */
////    Cursor<RedisZSetCommands.Tuple> zScan(String key, String ursor, String pattern, long count);
//
//
//    /**
//     * exists
//     *
//     * @param key
//     * @return
//     * @description 判断信息是否存在
//     */
//    boolean exists(String key);
//
//    Set zRangeByScore(String key, double min, double max, Class<?> clazz);
    
    /**
     * 模糊查询key
     *
     * @param pattern
     * @return
     */
    Set keys(String pattern);
    
    /**
     * 模糊查询key
     *
     * @param key
     * @return
     */
    List<String> lrange(String key);
}
