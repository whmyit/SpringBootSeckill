package com.dxhy.order.protocol.v4.order;

import com.dxhy.order.protocol.v4.RESPONSEV4;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.util.List;

/**
 * 获取订单接口返回协议bean
 *
 * @author ZSC-DXHY
 * @date 创建时间: 2018/9/19 15:05
 */
@Setter
@Getter
public class DDFPCX_RSP extends RESPONSEV4 implements Serializable {
    
    /**
     * 订单发票信息
     */
    private List<DDFPZXX> DDFPZXX;
    
}
