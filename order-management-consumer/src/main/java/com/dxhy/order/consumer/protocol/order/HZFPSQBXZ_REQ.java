package com.dxhy.order.consumer.protocol.order;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

/**
 * 红字申请单下载请求
 * todo V1或者是V2版本,后续不再更新迭代.
 *
 * @author ZSC-DXHY
 * @date 创建时间: 2020/5/15 16:40
 */
@Getter
@Setter
@Deprecated
public class HZFPSQBXZ_REQ implements Serializable {
    
    private String SQBXZQQPCH;
    private String NSRSBH;
    private String SLDID;
    private String KPJH;
    private String FPLX;
    private String FPLB;
    private String TKRQ_Q;
    private String TKRQ_Z;
    private String GMF_NSRSBH;
    private String XSF_NSRSBH;
    private String XXBBH;
    private String XXBFW;
    private String pageNo;
    private String pageSize;
}
