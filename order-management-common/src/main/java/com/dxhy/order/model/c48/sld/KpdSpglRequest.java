package com.dxhy.order.model.c48.sld;

import com.dxhy.order.model.Nsrsbh;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

/**
 * 开票点获取C48
 *
 * @author ZSC-DXHY
 * @date 创建时间: 2020/6/5 0:59
 */
@Setter
@Getter
public class KpdSpglRequest implements Serializable {
    private Nsrsbh[] nsrsbhs;
    private String qyzt;
    private String fjh;
    private String pageNo;
    private String pageSize;
}
