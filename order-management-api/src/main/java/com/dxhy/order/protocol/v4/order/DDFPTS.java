package com.dxhy.order.protocol.v4.order;

import com.dxhy.order.protocol.v4.RESPONSEV4;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

/**
 * 历史订单发票数据导入返回协议bean
 *
 * @author: <a href="tivenninesongs@163.com">yaoxuguang</a>
 * @createDate: Created in 2020/4/23
 */
@Getter
@Setter
public class DDFPTS extends RESPONSEV4 implements Serializable {
    /**
     * 订单发票请求流水号
     */
    private String DDQQLSH;
    
    /**
     * 订单号
     */
    private String DDH;
    
}
