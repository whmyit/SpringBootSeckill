package com.dxhy.order.model.newtax;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.util.List;

/**
 * 新税控纳税人虚拟设备信息数据
 *
 * @author ZSC-DXHY
 * @date 创建时间: 2020-08-05 20:11
 */
@Getter
@Setter
public class NsrXnsbxx implements Serializable {
    /**
     * 虚拟设备号
     */
    private String xnsbh;
    
    /**
     * 开票终端信息列表
     */
    private List<Kpzdxx> kpzdxxs;
}
