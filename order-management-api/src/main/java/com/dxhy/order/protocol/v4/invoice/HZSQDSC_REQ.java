package com.dxhy.order.protocol.v4.invoice;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.util.List;

/**
 * 红字发票申请表上传 请求协议bean
 *
 * @author ZSC-DXHY-PC
 * @date 创建时间: 2018/12/19 9:20
 */
@Setter
@Getter
public class HZSQDSC_REQ implements Serializable {
    
    /**
     * 红字申请单批次对象
     */
    private HZSQDSCPC HZSQDSCPC;
    
    /**
     * 红字申请单明细对象
     */
    private List<HZSQDSCZXX> HZSQDSCZXX;
}
