package com.dxhy.order.model.c48.sld;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.util.Date;

/**
 * 受理点C48明细信息
 *
 * @author ZSC-DXHY
 * @date 创建时间: 2020/6/4 22:31
 */
@Getter
@Setter
public class Fpsldmx implements Serializable {
    private Integer sldid;
    private String nsrsbh;
    private Integer kpdid;
    private String kpdmc;
    private String sldmc;
    private String sldzt;
    private String bz;
    private String cjr;
    private Date cjsj;
    private Date gxsj;
    private Integer serverId;
    private String dydzt;
    private String zxzt;
}
