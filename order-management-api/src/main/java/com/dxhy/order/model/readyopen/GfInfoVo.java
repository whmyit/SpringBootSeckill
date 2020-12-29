package com.dxhy.order.model.readyopen;

import lombok.Getter;
import lombok.Setter;

/**
 * 购方信息业务bean
 *
 * @author 陈宇航
 */
@Setter
@Getter
public class GfInfoVo {

    /**
     * 购方类型 0个人 1单位
     */
    private String gflx;

    /**
     * 购方名称
     */
    private String gfmc;

    /**
     * 购方税号
     */
    private String gfsh;

    /**
     * 购方地址
     */
    private String gfdz;

    /**
     * 购方电话
     */
    private String gfdh;

    /**
     * 购方开户行
     */
    private String gfkhh;

    /**
     * 购方银行账号
     */
    private String gfyhzh;

    /**
     * 购方联系人
     */
    private String gflxr;

    /**
     * 购方手机
     */
    private String gfsj;

    /**
     * 购方邮箱
     */
    private String gfyx;

}
