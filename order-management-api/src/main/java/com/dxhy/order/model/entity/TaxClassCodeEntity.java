package com.dxhy.order.model.entity;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.util.Date;

/**
 * 税收分类编码实体类
 *
 * @author ZSC-DXHY
 * @date 创建时间: 2020-08-14 15:43
 */
@Getter
@Setter
public class TaxClassCodeEntity implements Serializable {
    /**
     * (主键)
     */
    private String  id;
    /**
     * 商品编码
     */
    private String  spbm;
    /**
     * 商品名称
     */
    private String spmc;
    /**
     * 说明
     */
    private String sm;
    /**
     * 增值税税率
     */
    private String zzssl;
    /**
     * 关键字
     */
    private String  gjz;
    /**
     * 汇总项 Y 是 N 不是
     */
    private String  hzx;
    /**
     * 可用状态 Y 可用 N 不可用
     */
    private String  kyzt;
    /**
     * 增值税特殊管理
     */
    private String  zzstsgl;
    /**
     * 增值税政策依据
     */
    private String  zzszcyj;
    /**
     * 增值税特殊内容代码
     */
    private String zzstsnrdm;
    /**
     * 消费税管理
     */
    private String  xfsgl;
    /**
     * 消费税政策依据
     */
    private String  xfszcyj;
    /**
     * 消费税特殊内容代码
     */
    private String  xfstsnrdm;
    /**
     * 统计局编码
     */
    private String  tjjbm;
    /**
     * 海关进出口商品名称
     */
    private String  hgjcksppm;
    /**
     * pid
     */
    private String   pid;
    /**
     * 优惠政策名称
     */
    private String   yhzcmc;
    /**
     * 税率
     */
    private String  sl;
    /**
     * 创建时间
     */
    private Date createTime;
    /**
     * 商品简称
     */
    private String spjc;

    /**
    * 启用时间
    */
    private Date enablingTime;
    /**
    * 更新时间
    */
    private Date updateTime;
    /**
    * 版本号
    */
    private String bbh;
    /**
    * 是否成品油(Y:成品油;N:非成品油)
    */
    private String cpy;
    /**
    * 免税类型
    */
    private String mslx;

}




