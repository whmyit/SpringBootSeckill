package com.dxhy.order.model.a9.zf;

import com.dxhy.order.model.a9.RequestBaseBean;
import lombok.Getter;
import lombok.Setter;

/**
 * 空白作废请求底层协议bean
 *
 * @author ZSC-DXHY
 */
@Getter
@Setter
public class KbZfRequest extends RequestBaseBean{
    
    private String FP_DM;
    private String FP_HM;
    private String FPLB;
    private String KPJH;
    private String NSRSBH;
    private String SLDID;
    private String ZFLX;
    private String ZFYY;
    private String ZFR;
    
}
