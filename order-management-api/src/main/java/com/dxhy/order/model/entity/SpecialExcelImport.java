package com.dxhy.order.model.entity;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

/**
 * 专票订单导入Excel协议bean
 *
 * @author ZSC-DXHY
 */
@Getter
@Setter
public class SpecialExcelImport implements Serializable {
    
    private static final long serialVersionUID = 1L;
    
    /**
     * 申请单唯一编号
     */
    private String sqdwybh;
    
    /**
     * 申请原因(申请原因: 1100000000:购方申请 已抵扣;1010000000:购方申请  未抵扣;0000000100 因发票有误购买方拒收的因开票有误等原因尚未交付的)
     */
    private String sqyy;
    
    /**
     * 成品油专用发票类型(申请单类型0:正常;1:成品油-销售数量变更2:成品油-销售金额变更;3成品油-其他)
     */
    private String cypzyfplx;
    
    /**
     * 原发票代码
     */
    private String yfpDm;
    
    /**
     * 原发票号码
     */
    private String yfpHm;
    
    /**
     * 销货方名称
     */
    private String xhfMc;
    
    /**
     * 销货方税号
     */
    private String xhfSh;
    
    /**
     * 购货方名称
     */
    private String ghfMc;
    
    /**
     * 购货方税号
     */
    private String ghfSh;
    
    /**
     * 商品名称
     */
    private String spMc;
    
    /**
     * 税收分类编码
     */
    private String spBm;
    
    /**
     * 税率
     */
    private String sLv;
    
    /**
     * 规格型号
     */
    private String ggXh;
    
    /**
     * 计量单位
     */
    private String xmDw;
    
    /**
     * 项目单价
     */
    private String xmDj;
    
    /**
     * 项目数量
     */
    private String xmSl;
    
    /**
     * 项目金额
     */
    private String xmje;
    
    /**
     * 项目税额
     */
    private String xmSe;
    
    /**
     * 享受优惠政策
     */
    
    private String yhzcbs;
    
    /**
     * 优惠政策类型
     */
    private String zzstsgl;
    
    /**
     * 含税标志
     */
    private String hsbz;

    /**
     * 原蓝票日期
     */
    private String ylprq;


}
