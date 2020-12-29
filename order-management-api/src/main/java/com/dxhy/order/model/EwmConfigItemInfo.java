package com.dxhy.order.model;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.util.Date;
/**
 * 二维码配置明细信息
 *
 * @author ZSC-DXHY
 * @date 创建时间: 2020-08-14 14:47
 */
@Getter
@Setter
public class EwmConfigItemInfo implements Serializable {
    
    private String id;
    
    private String ewmCoinfgId;
    
    private String fpzldm;
    
    private String sld;
    
    private String sldMc;
    
    private Date createTime;
    
}
