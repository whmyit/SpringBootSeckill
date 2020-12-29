package com.dxhy.order.model;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.util.Date;
/**
 * 注册码
 *
 * @author ZSC-DXHY
 * @date 创建时间: 2020-08-14 15:32
 */
@Setter
@Getter
public class RegistrationCode implements Serializable {
    /**
     * 主键id
     */
    private String id;
    
    /**
     * 纳税人识别号
     */
    private String xhfNsrsbh;
    
    /**
     * 注册码
     */
    private String zcm;
    
    /**
     * 机器编号
     */
    private String jqbh;
    
    /**
     * 税盘类型
     */
    private String splx;
    
    /**
     * 税盘状态(0:在线,1:离线)
     */
    private String spzt;
    
    /**
     * 创建时间
     */
    private Date createTime;
    
    /**
     * 更新时间
     */
    private Date updateTime;
    
}
