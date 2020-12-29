package com.dxhy.order.model.a9.dy;

import lombok.Data;

/**
 * @author ：杨士勇
 * @ClassName ：DyResponseDetail
 * @Description ：打印点详细信息
 * @date ：2019年7月22日 下午5:18:13
 */
@Data
public class DydResponseDetail {
    
    /**
     * 打印点id
     */
    private String dyjid;
    /**
     * 打印点名称
     */
    private String dyjmc;
    /**
     * 打印点名称
     */
    private String disUp;
    /**
     * 上边距
     */
    private String disRight;
    /**
     * 打印机状态
     */
    private String dyjzt;
    /**
     * 录入日期
     */
    private String lrrq;
    /**
     * 修改日期
     */
    private String xgrq;
    /**
     * 纳税人识别号
     */
    private String nsrsbh;
    /**
     * 纳税人名称
     */
    private String nsrmc;
    /**
     * 备注
     */
    private String bz;
    
    /**
     * C48部分字段支持
     */
    /**
     * 打印标识
     */
    private String spotKey;
    /**
     * 在线状态
     */
    private String zxzt;
    
    private String serverId;
    private String serverName;
}
