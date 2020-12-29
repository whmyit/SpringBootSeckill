package com.dxhy.order.protocol.v4.invoice;

import com.dxhy.order.protocol.v4.RESPONSEV4;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

/**
 * 红字信息申请表上传结果协议bean
 *
 * @author ZSC-DXHY-PC
 * @date 创建时间: 2018/12/19 9:25
 */
@Setter
@Getter
public class HZSQDSCJG extends RESPONSEV4 implements Serializable {
    
    /**
     * 申请表上传请求流水号
     */
    private String SQBSCQQLSH;
    
    /**
     * 申请单号
     */
    private String SQDH;
    
    /**
     * 信息表编号
     */
    private String XXBBH;
}
