package com.dxhy.order.consumer.modules.scaninvoice.model;


import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

/**
 * @ClassName TitleInfo
 * @Author ysy
 * @Date 2020-05-05 17:19
 **/
@Getter
@Setter
public class TitleInfo implements Serializable {

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
     * 购方银行
     */
    private String gfyh;
    /**
     * 购方账号
     */
    private String gfzh;
    /**
     * 购方手机
     */
    private String gfsj;
    /**
     * 购方邮箱
     */
    private String gfyx;
    /**
     * 备注
     */
    private String bz;
    /**
     * 购货方企业类型
     */
    private String ghfqylx;

}
