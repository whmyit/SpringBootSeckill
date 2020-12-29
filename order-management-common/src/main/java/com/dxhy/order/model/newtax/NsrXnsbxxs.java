package com.dxhy.order.model.newtax;

import com.dxhy.order.model.a9.ResponseBaseBean;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.util.List;

/**
 * 新税控纳税人虚拟设备信息
 *
 * @author ZSC-DXHY
 * @date 创建时间: 2020-08-05 20:09
 */
@Getter
@Setter
public class NsrXnsbxxs extends ResponseBaseBean implements Serializable {
    /**
     * 虚拟设备数组
     */
    private List<NsrXnsbxx> content;
}
