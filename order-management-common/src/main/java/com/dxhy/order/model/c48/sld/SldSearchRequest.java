package com.dxhy.order.model.c48.sld;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

/**
 * 受理点请求C48
 *
 * @author ZSC-DXHY
 * @date 创建时间: 2020/6/4 23:48
 */
@Getter
@Setter
public class SldSearchRequest implements Serializable {
    private String fpzlDm;
    private String nsrsbh;
    private String cpyzt;
}
