package com.dxhy.order.consumer.protocol.usercenter;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.util.List;

/**
 * 推送用户中心税控设备业务bean
 *
 * @author ZSC-DXHY
 * @date 创建时间: 2020/6/12 17:28
 */
@Getter
@Setter
public class PushSksb implements Serializable {
    
    
    /**
     * 纳税人识别号
     */
    private String xhfNsrsbh;
    /**
     * 纳税人名称
     */
    private String xhfMc;
    
    private List<Sksb> sksb;
    
}
