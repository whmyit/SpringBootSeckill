package com.dxhy.order.protocol.v4.fpyl;

import com.dxhy.order.constant.OrderInfoContentEnum;
import com.dxhy.order.protocol.v4.RESPONSEV4;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

/**
 * 税控设备信息同步接口返回
 *
 * @author ZSC-DXHY
 * @date 创建时间: 2020-08-14 14:57
 */
@Getter
@Setter
public class FPYLCX_RSP extends RESPONSEV4 implements Serializable {
    
    /**
     * 销货方纳税人识别号
     */
    private String XHFSBH;
    
    /**
     * 销货方名称
     */
    private String XHFMC;
    
    /**
     * 分机号
     */
    private String FJH;
    
    /**
     * 普票余量
     */
    private String PPYL;
    
    /**
     * 电票余量
     */
    private String DPYL;
    
    /**
     * 专票余量
     */
    private String ZPYL;
    
    public static FPYLCX_RSP build(OrderInfoContentEnum orderInfoContentEnum) {
        FPYLCX_RSP fpylcxRsp = new FPYLCX_RSP();
        fpylcxRsp.setZTDM(orderInfoContentEnum.getKey());
        fpylcxRsp.setZTXX(orderInfoContentEnum.getMessage());
        return fpylcxRsp;
    }
    
}
