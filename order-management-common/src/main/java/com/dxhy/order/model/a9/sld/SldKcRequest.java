package com.dxhy.order.model.a9.sld;

import com.dxhy.order.model.a9.RequestBaseBean;
import lombok.Getter;
import lombok.Setter;

/**
 * 受理点库存请求
 *
 * @author ZSC-DXHY
 * @date 创建时间: 2020-08-14 15:39
 */
@Getter
@Setter
public class SldKcRequest extends RequestBaseBean{
    
    private String sldid;
    private String fpzldm;
    private String nsrsbh;
    private String fjh;
    private String jqbh;
    
}
