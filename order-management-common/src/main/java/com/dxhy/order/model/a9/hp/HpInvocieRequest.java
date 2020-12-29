package com.dxhy.order.model.a9.hp;


import com.dxhy.order.model.a9.RequestBaseBean;
import lombok.Getter;
import lombok.Setter;
/**
 * 发票请求
 *
 * @author ZSC-DXHY
 * @date 创建时间: 2020-08-14 15:04
 */
@Getter
@Setter
public class HpInvocieRequest extends RequestBaseBean{
    
    private String FPLB;
    private String FPLX;
    private String GMF_NSRSBH;
    private String KPJH;
    private String NSRSBH;
    private String pageNo;
    private String pageSize;
    private String SLDID;
    private String SQBXZQQPCH;
    private String TKRQ_Q;
    private String TKRQ_Z;
    private String XSF_NSRSBH;
    private String XXBBH;
    private String XXBFW;
}
