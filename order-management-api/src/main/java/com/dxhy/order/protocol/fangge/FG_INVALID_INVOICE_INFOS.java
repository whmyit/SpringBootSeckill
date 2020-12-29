package com.dxhy.order.protocol.fangge;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

/**
 * @Description: 发票作废返回实体
 * @Author:xueanna
 * @Date:2019/7/2
 */
@Setter
@Getter
public class FG_INVALID_INVOICE_INFOS implements Serializable {
    
    /**
     * 发票代码
     */
    private String FP_DM;
    /**
     * 发票号码
     */
    private String FP_HM;
    
    /**
     * 作废类型
     * 0：空白发票作废
     * 1：已开发票作废
     */
    private String ZFLX;
    /**
     * 作废人
     */
    private String ZFR;
    /**
     * 发票种类代码
     */
    private String FPZLDM;
    /**
     * 签名参数
     */
    private String QMCS;
    /**
     * 合计金额
     */
    private String HJJE;
}
