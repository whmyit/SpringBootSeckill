package com.dxhy.order.model.c48.sld;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.util.Date;

/**
 * 发票退回底层bean
 *
 * @author ZSC-DXHY
 * @date 创建时间: 2020/6/4 22:26
 */
@Getter
@Setter
public class FpThly implements Serializable {
    
    private int id;
    private String nsrsbh;
    private String fjh;
    private String jqbh;
    private String fpzlDm;
    private String fpdm;
    private String fpqshm;
    private String fphm;
    private String fpzzhm;
    private String fpsl;
    private String syfs;
    private String thbj;
    private String sxpbj;
    private Date lysj;
    private Date gxsj;
}
