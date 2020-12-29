package com.dxhy.order.model.fg;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

/**
 * @Description:
 * @Author: chenyuzhen
 * @CreateDate: 2019/7/10 11:05
 */
@Getter
@Setter
public class SpBwXxCxDto implements Serializable {
    
    /**
     * 税盘名称
     */
    private String spmc;
    
    /**
     * 机器编号
     */
    private String jqbh;
    
    /**
     * 10,1,2,
     */
    private String fpzlDms;
    
    /**
     * 纳税人识别号
     */
    private String nsrsbh;
    
}
