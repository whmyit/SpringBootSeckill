package com.dxhy.order.consumer.model.myinovice;

import lombok.Getter;
import lombok.Setter;

/**
 * 同步我的发票销货方信息协议bean
 *
 * @author ZSC-DXHY
 */
@Setter
@Getter
public class SynSellerInfoRequest {
    
    private String authorizationcode;
    private String concernStatus;
    private String dh;
    private String dsptbm;
    private String id;
    private String dz;
    private String emailCustom;
    private String enterpriseType;
    private String fpzlDm;
    private String jygz;
    private String logoBase64Str;
    private String logoPath;
    private String name;
    private String nsrmc;
    private String nsrsbh;
    private String password;
    private String qybs;
    private String requestcode;
    private String validTime;
    private String yhzh;
    private String sl_concern_status;
    private String whether_merge;
    private String money_limit;
    private String query_custom;
    private String redirect_url;
    
    private SynSellerInfoItem[] spxx;
    
}
