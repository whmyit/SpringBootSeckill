package com.dxhy.order.model.a9.sld;

import com.dxhy.order.model.a9.RequestBaseBean;
import lombok.Getter;
import lombok.Setter;
/**
 * 金税盘查询请求
 *
 * @author ZSC-DXHY
 * @date 创建时间: 2020-08-14 18:15
 */
@Getter
@Setter
public class JspxxRequest extends RequestBaseBean{
    private String fpzldm;
    private String nsrsbh;
    private String sldid;
    private String syzt;
}
