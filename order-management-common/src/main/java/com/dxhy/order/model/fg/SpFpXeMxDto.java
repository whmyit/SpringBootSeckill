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
public class SpFpXeMxDto implements Serializable {
    
    /**
     * 单张开票限额
     */
    private String dzkpxe;
    
}
