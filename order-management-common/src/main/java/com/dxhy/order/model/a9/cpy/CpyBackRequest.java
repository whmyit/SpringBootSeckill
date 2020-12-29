package com.dxhy.order.model.a9.cpy;


import com.dxhy.order.model.a9.RequestBaseBean;
import lombok.Getter;
import lombok.Setter;
/**
 * 成品油退回请求bean
 *
 * @author ZSC-DXHY
 * @date 创建时间: 2020-08-14 14:41
 */
@Setter
@Getter
public class CpyBackRequest extends RequestBaseBean{
    
    private String fjh;
    
    private String nsrsbh;
    
    private CpyMx[] mx;
}
