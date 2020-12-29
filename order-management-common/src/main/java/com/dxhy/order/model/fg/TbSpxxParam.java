package com.dxhy.order.model.fg;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.util.List;

/**
 * @ProjectName: SIMS-INVOICE-FGKP
 * @Package: com.dxhy.invoice.fgkpProtocol.sl.param
 * @ClassName: 同步税盘信息协议bean
 * @Author: liudongjie
 * @Date: 2019/7/4 13:52
 * @Version: 1.0
 * @Description:
 */
@Getter
@Setter
public class TbSpxxParam implements Serializable {
    
    /**
     * 纳税人识别号
     */
    private String NSRSBH;
    
    /**
     * 纳税人名称
     */
    private String NSRMC;
    
    /**
     * 0 非成品油 1 成品油经销 2成品油生产
     */
    private Integer CPYQYLX;
    
    /**
     * 机器编号
     */
    private String JQBH;
    
    /**
     * 发票种类代码 0:专票;2:普票;51:电子发票
     */
    private String FPZLDM;
    
    /**
     * 离线时长 72h
     */
    private String LXKPSJ;
    
    /**
     * 当前时钟
     */
    private String JSPSZ;
    
    /**
     * 金税盘类型 0航信 1百望 2方格UKey
     */
    private String JSPLX;
    
    /**
     * 税盘信息
     */
    private List<SpxxParam> JSPXX;
    
}
