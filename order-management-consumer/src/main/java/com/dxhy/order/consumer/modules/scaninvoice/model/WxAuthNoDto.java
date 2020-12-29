package com.dxhy.order.consumer.modules.scaninvoice.model;

import lombok.Getter;
import lombok.Setter;

/**
 * 微信鉴权
 *
 * @author ZSC-DXHY
 * @date 创建时间: 2020-08-14 15:47
 */
@Getter
@Setter
public class WxAuthNoDto {
    
    private String ToUserName;
    private String FromUserName;
    private String CreateTime;
    private String MsgType;
    private String Event;
    private String SuccOrderId;
    private String FailOrderId;
    private String AppId;
    private String Source;
}
