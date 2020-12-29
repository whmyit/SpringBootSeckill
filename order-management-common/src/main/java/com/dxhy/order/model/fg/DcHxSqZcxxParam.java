package com.dxhy.order.model.fg;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

/**
 * 调用第三方申请注册码协议bean
 *
 * @author liudongjie
 * @version 1.0.0 2019-07-03
 */
@Getter
@Setter
public class DcHxSqZcxxParam implements Serializable {
    
    /**
     * 秘钥
     */
    private String key;
    
    /**
     * 纳税人名称 必填
     */
    private String nsrmc;
    
    /**
     * 纳税人识别号 必填
     */
    private String nsrsbh;
    /**
     * 机器编号
     */
    private String jqbh;
    /**
     * 0为测试; 1为正式  必填
     */
    private String zclx;
    /**
     * 发票类型 不传默认为全票种
     */
    private String fplx;
    /**
     * 备注
     */
    private String bz;
    /**
     * 更新标志
     */
    private String gxbz;
    
    
}
