package com.dxhy.order.protocol.supplychain;

import lombok.Getter;
import lombok.Setter;

/**
 * @ClassName SupplyChainPushResponse
 * @Author ysy
 * @Date 2020-06-29 14:31
 **/

@Getter
@Setter
public class SupplyChainPushResponse extends SupplyChainBaseResponse{

    private String DDQQLSH;

    private String DDH;

    private String ZTDM;

    private String ZTXX;

}
