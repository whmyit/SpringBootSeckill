package com.dxhy.order.protocol.v4.order;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.io.Serializable;

/**
 * 订单发票全数据明细协议bean
 *
 * @author ZSC-DXHY
 */
@ToString
@Setter
@Getter
public class DDMXXX implements Serializable {
    
    /**
     * 项目序号
     */
    private String XH;
    
    /**
     * 发票行性质
     */
    private String FPHXZ;
    
    /**
     * 商品编码
     */
    private String SPBM;
    
    /**
     * 自行编码
     */
    private String ZXBM;
    
    /**
     * 优惠政策标识
     */
    private String YHZCBS;
    
    /**
     * 零税率标识
     * 空：非零税率，0:出口零税,1：免税，2：不征税 3:普通零税率
     * 若填写了3(普通零税率), 则:YHZCBS填0,ZZSTSGL填空
     */
    private String LSLBS;
    
    /**
     * 增值税特殊管理
     */
    private String ZZSTSGL;
    
    /**
     * 项目名称
     */
    private String XMMC;
    
    /**
     * 规格型号
     */
    private String GGXH;
    
    /**
     * 单位
     */
    private String DW;
    
    /**
     * 项目数量
     */
    private String SPSL;
    
    /**
     * 项目单价
     */
    private String DJ;
    
    /**
     * 项目金额
     */
    private String JE;
    
    /**
     * 含税标志
     */
    private String HSBZ;
    
    /**
     * 税率
     */
    private String SL;
    
    /**
     * 税额
     */
    private String SE;
    
    /**
     * 扣除额
     */
    private String KCE;
    
    /**
     * 备用字段
     */
    private String BYZD1;
    private String BYZD2;
    private String BYZD3;
    
}
