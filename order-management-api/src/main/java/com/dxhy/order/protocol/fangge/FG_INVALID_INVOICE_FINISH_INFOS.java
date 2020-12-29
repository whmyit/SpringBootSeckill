package com.dxhy.order.protocol.fangge;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

/**
 * @Description: 发票作废完成参数实体
 * @Author:xueanna
 * @Date:2019/7/2
 */
@Setter
@Getter
public class FG_INVALID_INVOICE_FINISH_INFOS implements Serializable {
    
    private String FP_DM;
    
    private String FP_HM;
    
    private String ZFZT;
}
