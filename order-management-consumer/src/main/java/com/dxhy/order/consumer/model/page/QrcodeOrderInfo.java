package com.dxhy.order.consumer.model.page;


import lombok.Getter;
import lombok.Setter;

import java.util.List;

/**
 * 生成二维码
 *
 * @author ZSC-DXHY
 */
@Getter
@Setter
public class QrcodeOrderInfo {

    private String id;
    
    private String fpzldm;
    
    private String ywlxid;
    
    private String ywlx;
    
    private String sld;
    
    private String skr;
    
    private String fhr;
    
    private String kpr;
    
    private String xhfmc;
    
    private String xhfNsrsbh;
    
    private String xhfdz;
    
    private String xhfdh;
    
    private String xhfyh;
    
    private String xhfzh;
    
    private String tqm;
    
    private String qrCodeUrl;
    
    private String sjly;
    
    /**
     * 扫码的类型 0 静态码 1 动态码
     */
    private String qrCodeType;
    
    private String backGround;
    
    List<PageOrderItemInfo> orderItemList;
    
}
