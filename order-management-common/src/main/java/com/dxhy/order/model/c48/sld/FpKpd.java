package com.dxhy.order.model.c48.sld;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.util.Date;

/**
 * 开票点C48
 *
 * @author ZSC-DXHY
 * @date 创建时间: 2020/6/5 1:19
 */
@Getter
@Setter
public class FpKpd implements Serializable {
    private Integer kpdid;
    private String nsrsbh;
    private String fjh;
    private String jqbh;
    private String kpdmc;
    private String cjr;
    private String bz;
    private String qyzt;
    private Date cjsj;
    private Date gxsj;
    private String cpyzt;
}
