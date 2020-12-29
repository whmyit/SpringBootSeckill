package com.dxhy.order.consumer.protocol.usercenter;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

/**
 * 税控设备数据bean
 *
 * @author ZSC-DXHY
 * @date 创建时间: 2020/6/12 17:40
 */
@Getter
@Setter
public class Sksb implements Serializable {
    
    /**
     * 税控设备编码
     */
    private String sksbbm;
    
    /**
     * 税控设备名称
     */
    private String sksbmc;
    
}
