package com.dxhy.order.model;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.util.Date;
/**
 * 二维码配置信息
 *
 * @author ZSC-DXHY
 * @date 创建时间: 2020-08-14 14:47
 */
@Getter
@Setter
public class EwmConfigInfo implements Serializable {
    
    private String id;
    
    private String xhfMc;
    
    private String xhfNsrsbh;
    
    private String fpzldm;
    
    private String invalidTime;
    
    private Date createTime;
    
}
