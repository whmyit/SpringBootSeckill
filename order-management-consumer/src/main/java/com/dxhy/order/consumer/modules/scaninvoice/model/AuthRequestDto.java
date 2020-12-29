package com.dxhy.order.consumer.modules.scaninvoice.model;

import lombok.Getter;
import lombok.Setter;

/**
 * @ClassName AuthRequestDto
 * @Author ysy
 * @Date 2020-04-27 16:15
 **/

@Getter
@Setter
public class AuthRequestDto {

    private String orderId;
    private String tqm;
    private String kphjje;
    private String type;
    private String nsrsbh;
    private String openId;
    private String appid;
    private String unionId;

}
