package com.dxhy.order.protocol.fangge;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

/**
 * @Description: 注册税盘参数实体
 * @Author:xueanna
 * @Date:2019/6/25
 */
@Setter
@Getter
public class FG_REGIS_TAXDISK_REQ implements Serializable {
    /**
     * 纳税人识别号
     */
    private String NSRSBH;
    /**
     * 机器编号
     */
    private String JQBH;
    /**
     * 纳税人名称
     */
    private String NSRMC;
    /**
     * 注册类型
     */
    private String ZCLX;
    /**
     * 客户端ID
     */
    private String CLIENTID;
    
    
}
