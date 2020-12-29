package com.dxhy.order.model;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
/**
 * 公众号配置信息
 *
 * @author ZSC-DXHY
 * @date 创建时间: 2020-08-14 14:47
 */
@Getter
@Setter
public class EwmGzhConfig implements Serializable {
    
    private String id;
    
    private String nsrsbh;
    
    private String appid;
    
    private String appkey;
    
    private String gzhSubcribeEwm;
    
    private String homePageLogo;
    
    private String forceSubcribe;

    private String isConfig;
    
}
