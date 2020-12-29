package com.dxhy.order.model.a9.kp;

import com.dxhy.order.model.a9.RequestBaseBean;
import lombok.Getter;
import lombok.Setter;

/**
 * 发票查询
 *
 * @author ZSC-DXHY
 * @date 创建时间: 2020-08-14 15:10
 */
@Getter
@Setter
public class InvoiceQuery  extends RequestBaseBean{


    private String NSRSBH;

    private String FPQQLSH;

}
