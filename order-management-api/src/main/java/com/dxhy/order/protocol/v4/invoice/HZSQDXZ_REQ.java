package com.dxhy.order.protocol.v4.invoice;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

/**
 * 专票冲红 红字信息表 下载请求协议bean
 *
 * @author ZSC-DXHY-PC
 * @date 创建时间: 2018/12/19 10:53
 */
@Setter
@Getter
public class HZSQDXZ_REQ implements Serializable {
    
    /**
     * 申请表审核结果下载请求批次号
     */
    private String SQBXZQQPCH;
    
    /**
     * 申请方纳税人识别号
     */
    private String NSRSBH;
    
    /**
     * 发票类型
     */
    private String FPLXDM;
    
    /**
     * 填开日期起，可空
     */
    private String TKRQQ;
    
    /**
     * 填开日期止，可空
     */
    private String TKRQZ;
    
    /**
     * 购买方税号，可空
     */
    private String GMFSBH;
    
    /**
     * 销售方税号，可空
     */
    private String XHFSBH;
    
    /**
     * 信息表编号，可空
     */
    private String XXBBH;
    
    /**
     * 信息表下载范围：0全部；1本企业申请；2其它企业申请
     */
    private String XXBFW;
    
    /**
     * 页数
     */
    private String YS;
    
    /**
     * 个数
     */
    private String GS;
}
