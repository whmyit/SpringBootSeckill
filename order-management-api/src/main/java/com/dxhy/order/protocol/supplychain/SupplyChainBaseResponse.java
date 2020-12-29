package com.dxhy.order.protocol.supplychain;

import lombok.Data;

import java.util.List;

/**
 * @ClassName SupplyChainBaseResponse
 * @Author ysy
 * @Date 2020-06-29 14:32
 **/

@Data
public class SupplyChainBaseResponse {

    private String ZTDM;
    private String ZTXX;
    private List<SupplyChainPushResponse> DDFPTS;

}
