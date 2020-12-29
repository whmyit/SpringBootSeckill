package com.dxhy.order.protocol.v4.invalid;

import com.dxhy.order.protocol.v4.RESPONSEV4;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

/**
 * 发票作废结构数据协议bean
 *
 * @author ZSC-DXHY-PC
 * @date 创建时间: 2018/12/18 20:21
 */
@Setter
@Getter
public class ZFFPXX extends RESPONSEV4 implements Serializable {
    
    /**
     * 发票代码
     */
    private String FPDM;
    
    /**
     * 发票号码
     */
    private String FPHM;
    
    /**
     * 作废类型
     */
    private String ZFLX;
    
    /**
     * 作废原因
     */
    private String ZFYY;
    
}
