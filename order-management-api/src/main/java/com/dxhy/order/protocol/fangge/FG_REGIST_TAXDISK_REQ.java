package com.dxhy.order.protocol.fangge;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

/**
 * @Description: 税盘注册
 * @Author:xueanna
 * @Date:2019/7/2
 */
@Setter
@Getter
public class FG_REGIST_TAXDISK_REQ implements Serializable {
    /**
     * 纳税人识别号
     */
    private String NSRSBH;
    /**
     * 纳税人名称
     */
    private String NSRMC;
    /**
     * 注册类型
     */
    private String ZCLX;
    /**
     * 机器编号
     */
    private String JQBH;
    
}
