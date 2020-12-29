package com.dxhy.order.model.fg;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

/**
 * 金税盘信息查询请求
 *
 * @author bianj
 * @version 1.0.0 2019-06-06
 */
@Getter
@Setter
public class FgJspxxReqEntity implements Serializable {
    
    /**
     * 纳税人识别号
     */
    private String nsrsbh;
    
    /**
     * 发票种类代码
     */
    private String fpzlDm;
    
    /**
     * 金税盘类型(0:航信,1:百望)
     */
    private String jsplx;
    
}
