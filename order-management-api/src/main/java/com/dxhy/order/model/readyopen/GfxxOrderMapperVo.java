package com.dxhy.order.model.readyopen;

import com.dxhy.order.model.OrderInfo;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

/**
 * 购方信息归类
 *
 * @author 陈玉航
 * @version 1.0 Created on 2018年7月31日 下午9:29:01
 */
@Setter
@Getter
public class GfxxOrderMapperVo {

    /**
     * 订单集合
     */
    private List<OrderInfo> orderInfo;

    /**
     * 购方信息
     */
    private GfInfoVo gfInfo;

    /**
     * 购方信息
     */
    private String uId;

    /**
     * 组织唯一标识Id
     */
    private String deptId;
}
