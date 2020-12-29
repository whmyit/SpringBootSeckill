package com.dxhy.order.model.fg;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.util.List;

/**
 * 打印机查询协议bean
 *
 * @author liudongjie
 * @version 1.0.0 2019-06-06
 */
@Getter
@Setter
public class FgkpSkDyjmcCxParam implements Serializable {
    
    /**
     * 纳税人识别号支持多个查询
     */
    private List<String> xhfNsrsbh;
    
    /**
     * 发票种类代码
     */
    private String fpzldm;
    
    
}
