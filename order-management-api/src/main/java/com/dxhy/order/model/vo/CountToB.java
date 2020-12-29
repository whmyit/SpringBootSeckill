package com.dxhy.order.model.vo;

import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;

/**
 * @Author fankunfeng
 * @Date 2019-04-16 10:27:40
 * @Describe
 */
@Setter
@Getter
public class CountToB {
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
    private String taxpayerCode;
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
}
