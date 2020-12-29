package com.dxhy.order.model.a9.zf;

import com.dxhy.order.model.a9.RequestBaseBean;
import lombok.Getter;
import lombok.Setter;
/**
 * 作废请求
 *
 * @author ZSC-DXHY
 * @date 创建时间: 2020-08-14 15:58
 */
@Setter
@Getter
public class ZfRequest extends RequestBaseBean{
    
    private String FP_DM;
    private String FP_QH;
    private String FP_ZH;
    private String FPZLDM;
    private String ISHISTORY;
    private String NSRSBH;
    private String SLDID;
    private String ZFLX;
    private String ZFPCH;
    private String ZFYY;
    private String ZFR;
}
