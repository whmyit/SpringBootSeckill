package com.dxhy.order.protocol.fangge;

import com.dxhy.order.protocol.RESPONSE;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.util.List;

/**
 * @Description: 发票作废完成参数实体
 * @Author:xueanna
 * @Date:2019/7/2
 */
@Setter
@Getter
public class FG_INVALID_INVOICE_FINISH_REQ extends RESPONSE implements Serializable {
    
    /**
     * 作废批次号
     */
    private String ZFPCH;
    /**
     * 纳税人识别号
     */
    private String NSRSBH;
    /**
     * 发票作废返回结果实体
     */
    private List<FG_INVALID_INVOICE_FINISH_INFOS> INVALID_INVOICE_INFOS;
    
}
