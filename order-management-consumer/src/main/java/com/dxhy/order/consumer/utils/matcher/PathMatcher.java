package com.dxhy.order.consumer.utils.matcher;

import java.util.Comparator;
import java.util.Map;

/**
 * 工具类：路径正则匹配器接口
 *
 * @author: <a href="tivenninesongs@163.com">yaoxuguang</a>
 * @createDate: Created in 2020/4/9 10:10
 */
public interface PathMatcher {
    /**
     * 是否匹配
     *
     * @param path
     * @return
     */
    boolean isPattern(String path);
    
    /**
     * 匹配规则
     *
     * @param pattern
     * @param path
     * @return
     */
    boolean match(String pattern, String path);
    
    /**
     * 匹配
     *
     * @param pattern
     * @param path
     * @return
     */
    boolean matchStart(String pattern, String path);
    
    /**
     * 匹配
     *
     * @param pattern
     * @param path
     * @return
     */
    String extractPathWithinPattern(String pattern, String path);
    
    /**
     * 匹配
     *
     * @param pattern
     * @param path
     * @return
     */
    Map<String, String> extractUriTemplateVariables(String pattern, String path);
    
    /**
     * 匹配
     *
     * @param path
     * @return
     */
    Comparator<String> getPatternComparator(String path);
    
    /**
     * 匹配
     *
     * @param pattern1
     * @param pattern2
     * @return
     */
    String combine(String pattern1, String pattern2);
}
