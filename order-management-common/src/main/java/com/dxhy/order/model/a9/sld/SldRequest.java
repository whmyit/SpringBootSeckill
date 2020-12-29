package com.dxhy.order.model.a9.sld;

import com.dxhy.order.model.a9.RequestBaseBean;
import lombok.Getter;
import lombok.Setter;

/**
 * 受理点请求参数
 *
 * @author ZSC-DXHY
 */
@Setter
@Getter
public class SldRequest extends RequestBaseBean{
    
    /**
     * 成品油标识,0 成品油 1 非成品油
     */
    private String cpybs;
    
    /**
     * 发票种类代码
     */
    private String fpzldm;
    
    /**
     * 新税控发票种类代码
     */
    private String fplxDm;
    
    /**
     * 启用状态
     */
    private String qyzt;
    
    /**
     * 开票点id
     */
    private String id;
    
    /**
     * 纳税人识别号
     */
    private String nsrsbh;
    
    private String fjh;
    
}
