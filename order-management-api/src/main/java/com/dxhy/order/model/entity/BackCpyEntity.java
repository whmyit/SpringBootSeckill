package com.dxhy.order.model.entity;

import com.dxhy.invoice.protocol.sl.cpy.BackCpyKcMx;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.util.List;

/**
 * 成品油退回业务bean
 *
 * @author ZSC-DXHY
 * @date 创建时间: 2020-08-14 14:38
 */
@Getter
@Setter
public class BackCpyEntity implements Serializable {
    private String nsrsbh;
    private String xhfNsrsbh;
    private String fjh;
    private List<BackCpyKcMx> mx;
    
}
