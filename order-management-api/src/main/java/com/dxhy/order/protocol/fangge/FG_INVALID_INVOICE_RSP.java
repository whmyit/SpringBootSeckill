package com.dxhy.order.protocol.fangge;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.util.List;

/**
 * @Description: 发票作废返回实体
 * @Author:xueanna
 * @Date:2019/7/2
 */
@Setter
@Getter
public class FG_INVALID_INVOICE_RSP implements Serializable {
    
    /**
     * 作废批次号
     */
    private String ZFPCH;
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
    
    private List<FG_INVALID_INVOICE_INFOS> INVALID_INVOICE_INFOS;
    
    
}
