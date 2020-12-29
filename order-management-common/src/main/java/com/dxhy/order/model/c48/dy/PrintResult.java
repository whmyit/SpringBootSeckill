package com.dxhy.order.model.c48.dy;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

/**
 * 打印状态C48请求数据
 *
 * @author ZSC-DXHY
 * @date 创建时间: 2020/6/4 23:16
 */
@Getter
@Setter
public class PrintResult implements Serializable {
    
    private String NSRSBH;
    
    private String FPDYPCH;
    
    private String FPQQPCH;
    
    private String FPQQLSH;
    
    private String FPZL;
    
    private String FP_DM;
    
    private String FP_HM;
    
    private String PRINT_STATUS;
    
    private String CODE;
    
    private String MSG;
}
