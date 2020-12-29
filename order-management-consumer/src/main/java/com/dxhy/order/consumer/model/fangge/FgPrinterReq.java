package com.dxhy.order.consumer.model.fangge;

import lombok.Getter;
import lombok.Setter;

/**
 * @Description:调用接口调用入参
 * @Author:xueanna
 * @Date:2019/7/2
 */
@Getter
@Setter
public class FgPrinterReq {
    
    /**
     * 操作标识 0、增加；1、修改；2、删除；
     */
    private String CZLX;
    
    /**
     * 纳税人识别号
     */
    private String NSRSBH;
    
    /**
     * 打印机名称
     */
    private String DYJMC;
    /**
     * 发票种类代码
     */
    private String FPZLDM;
    
    /**
     * 上边距
     */
    private String SBJ;
    
    /**
     * 左边距
     */
    private String ZBJ;
    
    /**
     * 创建人
     */
    private String CJR;
    
    /**
     * 编辑人
     */
    private String BJR;
    
    /**
     * 纳税人名称
     */
    private String NSRMC;
    
    /**
     * 创建时间
     */
    private String CJSJ;
    
    /**
     * 编辑时间
     */
    private String BJSJ;
    
}
