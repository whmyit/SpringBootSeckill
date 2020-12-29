package com.dxhy.order.protocol.fangge;


import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.io.Serializable;

/**
 * @Description: 方格接口  红票申请单下载返回实体
 * @Author:xueanna
 * @Date:2019/6/25
 */
@ToString
@Setter
@Getter
public class FG_COMMON_RED_INVOICE_DOWNLOAD implements Serializable {
    /**
     * 纳税人识别号
     */
    private String NSRSBH;
    
    /**
     * 申请表审核结果下载请求批次号
     */
    private String SQBXZQQPCH;
    
    /**
     * 注册码
     */
    private String ZCM;
    /**
     * 开票机号
     */
    private String KPJH;
    /**
     * 发票种类代码
     */
    private String FPZLDM;
    /**
     * 填开日期起
     */
    private String TKRQ_Q;
    /**
     * 填开日期止
     */
    private String TKRQ_Z;
    /**
     * 购买方税号
     */
    private String GMF_NSRSBH;
    /**
     * 销售方税号
     */
    private String XSF_NSRSBH;
    /**
     * 信息表编号
     */
    private String XXBBH;
    /**
     * 信息表下载范围
     */
    private String XXBFW;
    /**
     * 页号
     */
    private String pageNo;
    /**
     * 个数
     */
    private String pageSize;
    /**
     * 机器编号
     */
    private String JQBH;
    
    /**
     * 税局IP,只有方格UKey需要
     */
    private String JDIP;
    
    /**
     * 税局端口,只有方格UKey需要
     */
    private String JDDK;
    
}
