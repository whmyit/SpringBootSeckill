package com.dxhy.order.consumer.protocol.fiscal;

import lombok.Getter;
import lombok.Setter;

/**
 * @Author fankunfeng
 * @Date 2019-06-19 10:00:35
 * @Describe
 */
@Setter
@Getter
public class KPD_ADD_REQ {
    private String kpdId;
    private String xhfNsrsbh;
    private String fjh;
    private String jqbh;
    private String kpdMc;
    private String cjr;
    private String bz;
    /**
     * 是否成品油税盘（0 非成品油 1 成品油经销 2成品油生产）
     */
    private String cpyzt;
}
