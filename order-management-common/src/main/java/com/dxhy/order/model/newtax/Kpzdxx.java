package com.dxhy.order.model.newtax;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

/**
 * 新税控开票终端信息
 *
 * @author ZSC-DXHY
 * @date 创建时间: 2020-08-19 20:09
 */
@Getter
@Setter
public class Kpzdxx implements Serializable {
    
    /**
     * 开票点id
     */
    private String kpdid;
    
    /**
     * 开票终端代码
     */
    private String kpzddm;
    
    /**
     * 开票终端名称
     */
    private String kpzdmc;
    
    /**
     * 发票类型代码
     */
    private String fplxdm;
    
    /**
     * 启用标志
     */
    private String qybz;
    
}
