package com.dxhy.order.model.a9.kp;


import com.dxhy.order.model.a9.ResponseBaseBeanExtend;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

/**
 * 调用底层查询接口,获取发票最终状态
 *
 * @author ZSC-DXHY
 * @date 创建时间: 2020-08-06 22:59
 */
@Setter
@Getter
public class CommonInvoiceStatus extends ResponseBaseBeanExtend implements Serializable {
    
    /**
     * 发票开票流水号
     */
    private String fpqqlsh;
    
    /**
     * 发票状态
     */
    private String fpzt;
    
    /**
     * 发票状态描述
     */
    private String fpztms;
}
