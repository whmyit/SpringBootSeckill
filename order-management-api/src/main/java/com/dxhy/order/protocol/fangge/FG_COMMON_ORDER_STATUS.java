package com.dxhy.order.protocol.fangge;


import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.io.Serializable;

/**
 * @Description: 开票接口 更新订单数据状态
 * @Author:xueanna
 * @Date:2019/6/25
 */
@ToString
@Setter
@Getter
public class FG_COMMON_ORDER_STATUS implements Serializable {
    /**
     * 纳税人识别号
     */
    private String NSRSBH;
    /**
     * 订单请求唯一流水号
     */
    private String DDQQLSH;
    /**
     * 数据状态  0表示方格接收数据成功    1表示接收失败
     */
    private String SJZT;
    
}
