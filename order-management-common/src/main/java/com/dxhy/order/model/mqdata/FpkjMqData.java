package com.dxhy.order.model.mqdata;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

/**
 * 发票开具队列数据交互对象
 *
 * @author ZSC-DXHY
 * @date 创建时间: 2020-08-06 20:18
 */
@Getter
@Setter
public class FpkjMqData implements Serializable {
    
    /**
     * 税号
     */
    private String nsrsbh;
    
    /**
     * 发票请求流水号
     */
    private String fpqqlsh;
    
    /**
     * 发票请求批次号(使用该批次号用于请求底层进行开票)
     */
    private String fpqqpch;
    
    /**
     * 开票流水号(使用该流水号用于请求底层进行开票)
     */
    private String kplsh;
    
    /**
     * 税控设备类型
     */
    private String terminalCode;
}
