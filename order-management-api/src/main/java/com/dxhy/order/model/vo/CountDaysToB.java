package com.dxhy.order.model.vo;

import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;

/**
* @Description: 根据天数统计开票量  提供大B使用
* @Author:xueanna
* @Date:2019-11-27
*/
@Setter
@Getter
public class CountDaysToB {
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
     * 开始时间 yyyy-mm-dd
     */
    @NonNull
    private String startTime;
    /**
     * 开始时间 yyyy-mm-dd
     */
    @NonNull
    private String endTime;


}
