package com.dxhy.order.protocol.fangge;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

/**
 * @Description: 开票   接收开票完成数据实体bean
 * @Author:xueanna
 * @Date:2019/6/25
 */
@Setter
@Getter
public class FG_COMMON_INVOICE_INFO implements Serializable {
    /**
     * 纳税人识别号
     */
    private String NSRSBH;
    /**
     * 订单请求流水号
     */
    private String DDQQLSH;
    
    /**
     * 机器编号
     */
    private String JQBH;
    
    /**
     * 订单号
     */
    private String DDH;
    /**
     * 校验码
     */
    private String JYM;
    
    /**
     * 防伪密文
     */
    private String FWM;
    /**
     * 二维码
     */
    private String EWM;
    
    /**
     * 发票代码
     */
    private String FP_DM;
    
    /**
     * 发票号码
     */
    private String FP_HM;
    
    /**
     * 开票日期
     */
    private String KPRQ;
    
    /**
     * 发票种类代码
     */
    private String FPZLDM;
    
    /**
     * 合计金额不含税
     */
    private String HJBHSJE;
    
    /**
     * 合计税额
     */
    private String KPHJSE;
    
    /**
     * ofd文件流,只有方格UKey的电票时时返回数据
     */
    private String OFDWJL;
    
    /**
     * 状态代码
     */
    private String STATUSCODE;
    /**
     * 状态描述
     */
    private String STATUSMSG;
    
    
}
