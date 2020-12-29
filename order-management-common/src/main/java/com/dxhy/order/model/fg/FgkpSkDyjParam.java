package com.dxhy.order.model.fg;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.util.Date;

/**
 * 打印机表添加更新协议bean
 *
 * @author liudongjie
 * @version 1.0.0 2019-06-06
 */
@Getter
@Setter
public class FgkpSkDyjParam implements Serializable {
    
    /**
     * id
     */
    private Integer dyjId;
    
    /**
     * 发票种类代码
     */
    private String fpzlDm;
    
    /**
     * 纳税人识别号
     */
    private String nsrsbh;
    
    /**
     * 打印机名称
     */
    private String dyjMc;
    
    /**
     * 上边距
     */
    private Integer sbj;
    
    /**
     * 左边距
     */
    private Integer zbj;
    
    /**
     * 数据状态
     */
    private Integer sjzt;
    
    /**
     * 创建人
     */
    private String cjr;
    
    /**
     * 创建时间
     */
    private Date cjsj;
    
    /**
     * 编辑人
     */
    private String bjr;
    
    /**
     * 编辑时间
     */
    private Date bjsj;
    
    /**
     * 启用标志0已启用 1未启用
     */
    private Integer qybz;
    
    /**
     * 纳税人名称
     */
    private String nsrMc;
    
}
