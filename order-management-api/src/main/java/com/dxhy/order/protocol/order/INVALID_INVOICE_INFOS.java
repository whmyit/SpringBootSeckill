package com.dxhy.order.protocol.order;

import com.dxhy.order.protocol.RESPONSE;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

/**
 * @Description 推送发票作废数据状态实体
 * @Author xueanna
 * @Date 2019/9/2 18:27
 */
@Getter
@Setter
public class INVALID_INVOICE_INFOS extends RESPONSE implements Serializable {
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
    */
    private String ZFLX;

    /**
    * 作废原因
    */
    private String ZFYY;


}
