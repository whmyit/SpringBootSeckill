package com.dxhy.order.consumer.protocol.sld;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

/**
 * 受理点查询协议bean
 *
 * @author ZSC-DXHY
 * @date 创建时间: 2018/9/19 9:49
 */
@Setter
@Getter
public class SLD_INVOICEROLLPLO_REQUEST implements Serializable {
    private String NSRSBH;
    private String FPZLDM;
    private String KPDID;
}
