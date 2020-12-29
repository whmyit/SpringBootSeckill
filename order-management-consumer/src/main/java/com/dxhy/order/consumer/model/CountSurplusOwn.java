package com.dxhy.order.consumer.model;

import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;

/**
 * @Author fankunfeng
 * @Date 2019-06-18 17:51:39
 * @Describe
 */
@Setter
@Getter
public class CountSurplusOwn {
    /**
     * 选项卡标识码
     */
    private String tabCode;
    /**
     * 用户id
     */
    private String userId;
    /**
     * 组织机构id
     */
    private String deptId;
    /**
     * 纳税人识别号
     */
    @NonNull
    private String xhfNsrsbh;
    /**
     * 数据类型
     */
    private String dataType;
    /**
     * 分机号
     */
    private String fjh;
    /**
     * 纳税人名称
     */
    private String nsrmc;
    
    /**
     * 分页页数
     */
    int pageSize;
    
    /**
     * 分页当前页
     */
    int currPage;
}
