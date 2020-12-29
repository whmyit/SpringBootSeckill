package com.dxhy.order.protocol.fangge;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

/**
 * @Description: 发票打印返回实体
 * @Author:xueanna
 * @Date:2019/7/2
 */
@Setter
@Getter
public class FG_INVOICE_PRINT_RSP implements Serializable {
    
    /**
     * 打印批次号
     */
    private String DYPCH;
    /**
     * 纳税人识别号
     */
    private String NSRSBH;
    /**
     * 注册码
     */
    private String ZCM;
    /**
     * 机器编号
     */
    private String JQBH;
    /**
     * 发票代码
     */
    private String FP_DM;
    
    /**
     * 发票起号
     */
    private String FPQH;
    /**
     * 发票止号
     */
    private String FPZH;
    /**
     * 发票种类代码
     */
    private String FPZLDM;
    /**
     * fp:发票，qd:清单
     * 打印类型
     */
    private String DYLX;
    /**
     * 打印点标识
     */
    private String DYDBS;
    /**
     * 左偏移
     */
    private String ZPY;
    /**
     * 上偏移
     */
    private String SPY;
    /**
     * 打印机名称
     */
    private String DYJMC;
}
