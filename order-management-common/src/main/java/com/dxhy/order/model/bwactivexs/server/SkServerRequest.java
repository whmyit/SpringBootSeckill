package com.dxhy.order.model.bwactivexs.server;

import com.dxhy.order.model.a9.RequestBaseBean;
import lombok.Getter;
import lombok.Setter;

/**
 * 开票点查询
 *
 * @author ZSC-DXHY
 * @date 创建时间: 2020-08-14 15:36
 */
@Getter
@Setter
public class SkServerRequest extends RequestBaseBean {
    
    private String kpdId;
    
    private String nsrsbh;
    
}
