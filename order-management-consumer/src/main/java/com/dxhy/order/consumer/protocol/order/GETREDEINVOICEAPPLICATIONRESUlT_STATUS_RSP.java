package com.dxhy.order.consumer.protocol.order;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

/**
 * 红字申请单下载协议
 * todo V1或者是V2版本,后续不再更新迭代.
 *
 * @author ZSC-DXHY
 * @date 创建时间: 2020/5/15 16:52
 */
@Getter
@Setter
@Deprecated
public class GETREDEINVOICEAPPLICATIONRESUlT_STATUS_RSP implements Serializable {
    
    private String SQBXZQQPCH;
    private String STATUS_CODE;
    private String STATUS_MESSAGE;
    private String SUCCESS_COUNT;
    private REDINVREQBILLXX[] REDINVREQBILLXX;
}
