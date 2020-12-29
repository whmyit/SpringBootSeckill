package com.dxhy.order.model.a9.hp;

import com.dxhy.order.model.a9.RequestBaseBean;
import lombok.Getter;
import lombok.Setter;

/**
 * 红票下载
 *
 * @author ZSC-DXHY
 * @date 创建时间: 2020-08-14 15:32
 */
@Getter
@Setter
public class RedInvoiceRevokeRequest extends RequestBaseBean{


    private String NSRSBH;

    private String XXBBH;


}
