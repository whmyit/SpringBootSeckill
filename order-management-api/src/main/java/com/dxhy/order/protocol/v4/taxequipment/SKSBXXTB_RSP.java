package com.dxhy.order.protocol.v4.taxequipment;

import com.dxhy.order.constant.OrderInfoContentEnum;
import com.dxhy.order.protocol.v4.RESPONSEV4;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

/**
 * 税控设备信息同步接口返回
 *
 * @author ZSC-DXHY
 * @date 创建时间: 2020-08-14 15:36
 */
@Getter
@Setter
public class SKSBXXTB_RSP extends RESPONSEV4 implements Serializable {
    
    /**
     * 销货方纳税人识别号
     */
    private String XHFSBH;
    
    /**
     * 销货方名称
     */
    private String XHFMC;
    
    /**
     * 税控设备代码
     */
    private String SKSBDM;
    
    
    public static SKSBXXTB_RSP build(OrderInfoContentEnum orderInfoContentEnum) {
        SKSBXXTB_RSP gmfxxtbRsp = new SKSBXXTB_RSP();
        gmfxxtbRsp.setZTDM(orderInfoContentEnum.getKey());
        gmfxxtbRsp.setZTXX(orderInfoContentEnum.getMessage());
        return gmfxxtbRsp;
    }
    
}
