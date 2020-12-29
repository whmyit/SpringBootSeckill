package com.dxhy.order.protocol.v4.taxequipment;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

/**
 * 税控设备信息同步接口请求
 *
 * @author ZSC-DXHY
 * @date 创建时间: 2020-08-14 15:36
 */
@Getter
@Setter
public class SKSBXXTB_REQ implements Serializable {
    
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
    
    /**
     * 税控设备类型
     */
    private String SKSBXH;
    
    /**
     * 操作类型
     */
    private String CZLX;
    
    /**
     * 关联时间
     */
    private String GLSJ;
    
    /**
     * 省份代码
     */
    private String SFDM;
    
    /**
     * 省份名称
     */
    private String SFMC;
    
    /**
     * 备注
     */
    private String BZ;
    
}
