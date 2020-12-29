package com.dxhy.order.model.fg;

import com.dxhy.order.model.a9.ResponseBaseBean;
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
public class SpFpXeDto extends ResponseBaseBean implements Serializable {
    
    /**
     * 单张开票限额
     */
    private SpFpXeMxDto data;
    
}
