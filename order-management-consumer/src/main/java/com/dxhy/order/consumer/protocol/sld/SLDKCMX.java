package com.dxhy.order.consumer.protocol.sld;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

/**
 * 受理点详细数据协议bean
 *
 * @author ZSC-DXHY
 * @date 创建时间: 2018/9/19 9:58
 */
@Setter
@Getter
public class SLDKCMX implements Serializable {
    private String NSRSBH;
    private String SLDID;
    private String SLDMC;
    private String FPZLDM;
    private String FP_DM;
    private String FPDQHM;
    private String FPQSHM;
    private String FPZZHM;
    private String SYFS;
    private String SYZT;
    private String SPR;
    private String XPR;
    private String SPSJ;
    private String XPSJ;
    private String FJH;
    private String JQBH;
    private String DYDMC;
    private String DDYFPHM;
}
