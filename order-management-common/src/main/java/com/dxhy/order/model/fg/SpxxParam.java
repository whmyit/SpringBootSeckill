package com.dxhy.order.model.fg;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.util.Date;

/**
 * @ProjectName: SIMS-INVOICE-FGKP
 * @Package: com.dxhy.invoice.fgkpProtocol.sl.param
 * @ClassName: 税盘信息
 * @Author: liudongjie
 * @Date: 2019/7/4 13:52
 * @Version: 1.0
 * @Description:
 */
@Getter
@Setter
public class SpxxParam implements Serializable {
    
    /**
     * 区域代码
     */
    private String QYDM;
    
    /**
     * 分机号
     */
    private String FJH;
    
    /**
     * 航信金税盘状态0:在线；1:离线
     */
    private String HXJSPZT;
    
    /**
     * 是否简易征收0:是；1:否；
     */
    private String SFJYZS;
    
    /**
     * 上次报税日期2018-08-30
     */
    private String SCBSRQ;
    
    /**
     * 抄税起始日期2019-09-12
     */
    private String CSQSRQ;
    
    /**
     * 是否到抄税期0:是，已到抄税期；1:否;
     */
    private String SFDCSQ;
    
    /**
     * 是否到锁死期0:是，已到锁死期；1:否
     */
    private String SFDSSQ;
    
    /**
     * 开票限额
     */
    private String KPXE;
    
    /**
     * 离线开票限额999999.99
     */
    private String LXKPXE;
    
    /**
     * 启用时间
     */
    private Date QYSJ;
    
    /**
     * 开票截止时间
     */
    private Date KPJZSJ;
    
    /**
     * 百望金税盘状态0:正常；1:锁死；
     */
    private Integer BWJSPZT;
    
    /**
     * 数据报送起始日期
     */
    private String SJBSQSRQ;
    
    /**
     * 数据报送终止日期
     */
    private String SJBSZZRQ;
    
    /**
     * 单张开票限额
     */
    private String DZKPXE;
    
    /**
     * 正数累计限额
     */
    private String ZSLJXE;
    
    /**
     * 负数累计限额
     */
    private String FSLJXE;
    
    /**
     * 负数发票天数
     */
    private String FSFPTS;
    
    /**
     * 最新报税日期
     */
    private String ZXBSRQ;
    
    /**
     * 剩余容量
     */
    private String SYRL;
    
    /**
     * 上传截止日期
     */
    private String SCJZRQ;
    
    /**
     * 离线开票张数
     */
    private String LXKPZS;
    
    /**
     * 离线正数累计金额
     */
    private String LXZSLJJE;
    
    /**
     * 离线负数累计金额
     */
    private String LXFSLJJE;
}
