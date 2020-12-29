package com.dxhy.order.protocol.order;

import com.dxhy.order.protocol.RESPONSE;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

/**
 * 发票推送协议bean
 *
 * @author ZSC-DXHY-PC
 * @date 创建时间: 2018/12/18 18:10
 */
@Setter
@Getter
public class PUSH_RSP extends RESPONSE {
    
    /**
     * 推送的参数
     */
    
    private List<COMMON_ORDER_INVOCIE> COMMON_ORDER_INVOCIE;
}
