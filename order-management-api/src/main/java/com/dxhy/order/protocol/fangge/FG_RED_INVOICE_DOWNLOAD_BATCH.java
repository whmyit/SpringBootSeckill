package com.dxhy.order.protocol.fangge;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

/**
 * 专票冲红 红字信息表下载批次 请求协议bean
 *
 * @author ZSC-DXHY-PC
 * @date 创建时间: 2018/12/19 9:23
 */
@Setter
@Getter
public class FG_RED_INVOICE_DOWNLOAD_BATCH implements Serializable {
    
    /**
     * 申请表上传请求批次号
     */
    private String SQBSCQQPCH;
    
    /**
     * 申请方纳税人识别号
     */
    private String NSRSBH;
    /**
     * 开票机号
     */
    private String KPJH;
    /**
     * 注册码
     */
    private String ZCM;
    
    /**
     * 机器编号
     */
    private String JQBH;
    /**
     * 发票种类diam
     */
    private String FPZLDM;
    /**
     * 申请类别,0为销方申请,1为购方申请
     */
    private String SQLB;
    
    /**
     * 扩展字段
     */
    private String KZZD;
}
