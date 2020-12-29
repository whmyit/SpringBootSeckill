package com.dxhy.order.model.dto;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.util.Date;

/**
 * @Description TODO
 * @Author xueanna
 * @Date 2019/8/13 15:10
 */
@Setter
@Getter
public class FgFpsldmx implements Serializable {
    private static final long serialVersionUID = 7601422679388855331L;
    private String sldid;
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
