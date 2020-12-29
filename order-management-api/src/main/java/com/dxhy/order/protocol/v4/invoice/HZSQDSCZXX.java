package com.dxhy.order.protocol.v4.invoice;

import com.dxhy.order.protocol.v4.order.DDMXXX;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.util.List;

/**
 * 红字信息申请表上传明细协议bean
 *
 * @author ZSC-DXHY-PC
 * @date 创建时间: 2018/12/19 9:25
 */
@Setter
@Getter
public class HZSQDSCZXX implements Serializable {
    
    /**
     * 红字信息申请表头信息
     */
    private HZSQDTXX HZSQDTXX;
    
    /**
     * 明细信息
     */
    private List<DDMXXX> DDMXXX;
}
