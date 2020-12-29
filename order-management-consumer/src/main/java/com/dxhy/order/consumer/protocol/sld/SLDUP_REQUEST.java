package com.dxhy.order.consumer.protocol.sld;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

/**
 * 受理点上票协议bean
 *
 * @author ZSC-DXHY
 * @date 创建时间: 2018/9/19 10:34
 */
@Setter
@Getter
public class SLDUP_REQUEST implements Serializable {

    private String NSRSBH;
    private String SLDID;
    private String FPZLDM;
    private String FP_DM;
    private String FPQSHM;
    private String FPZZHM;
    private String SPR;
}
