package com.dxhy.order.protocol.v4.fpyl;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

/**
 * 发票余量查询接口请求
 *
 * @author ZSC-DXHY
 * @date 创建时间: 2020-08-14 14:57
 */
@Getter
@Setter
public class FPYLCX_REQ implements Serializable {
    
    /**
     * 销货方纳税人识别号
     */
    private String XHFSBH;
    
}
