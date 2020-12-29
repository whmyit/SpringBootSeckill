package com.dxhy.order.model.c48.sld;

import com.dxhy.order.model.Nsrsbh;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

/**
 * 受理点管理C48
 *
 * @author ZSC-DXHY
 * @date 创建时间: 2020/6/4 23:54
 */
@Getter
@Setter
public class SldglRequest implements Serializable {
    private Nsrsbh[] nsrsbhs;
    private String pageNo;
    private String pageSize;
}
