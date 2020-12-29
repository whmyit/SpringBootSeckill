package com.dxhy.order.model.page;

import com.dxhy.order.xss.SqlFilter;
import org.apache.commons.lang3.StringUtils;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * 查询参数
 *
 * @author Mark sunlightcs@gmail.com
 * @since 2.0.0 2017-03-14
 */
public class QueryPage<T> extends LinkedHashMap<String, Object> {
    private static final long serialVersionUID = 1L;

    /**
     * 当前页码
     */
    private int currPage = 1;
    /**
     * 每页条数
     */
    private int limit = 10;
    
    public QueryPage(Map<String, Object> params) {
        this.putAll(params);
        
        //分页参数
        if (params.get("page") != null) {
            currPage = Integer.parseInt((String) params.get("page"));
        }
        if (params.get("limit") != null) {
            limit = Integer.parseInt((String) params.get("limit"));
        }
    
        this.put("page", currPage);
        this.put("limit", limit);
    
        //防止SQL注入（因为sidx、order是通过拼接SQL实现排序的，会有SQL注入风险）
        String sidx = SqlFilter.sqlInject((String) params.get("sidx"));
        String order = SqlFilter.sqlInject((String) params.get("order"));
        this.put("sidx", sidx);
        this.put("order", order);
    
    
        //排序
        if (StringUtils.isNotBlank(sidx) && StringUtils.isNotBlank(order)) {
//            this.page.setOrderByField(sidx);
//            this.page.setAsc("ASC".equalsIgnoreCase(order));
        }
        
    }

//    public Page<T> getPage() {
//        return page;
//    }
    
    public int getCurrPage() {
        return currPage;
    }
    
    public int getLimit() {
        return limit;
    }
}
