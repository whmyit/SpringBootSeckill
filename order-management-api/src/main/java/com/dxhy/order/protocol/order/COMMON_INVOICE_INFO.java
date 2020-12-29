package com.dxhy.order.protocol.order;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

/**
 * 发票详情数据对外协议bean
 *
 * @author ZSC-DXHY-PC
 * @date 创建时间: 2018/12/18 19:33
 */
@Setter
@Getter
public class COMMON_INVOICE_INFO implements Serializable {
    
    /**
     * 订单请求流水号
     */
    private String DDQQLSH;
    
    /**
     * 机器编号
     */
    private String JQBH;
    
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
     * 校验码
     */
    private String JYM;
    
    /**
     * 防伪密文
     */
    private String FWM;
    
    /**
     * 状态值
     */
    private String STATUS_CODE;
    
    /**
     * 错误信息
     */
    private String STATUS_MESSAGE;
    
}
