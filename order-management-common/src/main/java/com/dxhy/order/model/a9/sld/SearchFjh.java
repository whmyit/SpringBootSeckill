package com.dxhy.order.model.a9.sld;

import lombok.Data;

import java.io.Serializable;

/**
 * @Description:
 * @Author: chenyuzhen
 * @CreateDate: 2019/7/10 11:05
 */
@Data
public class SearchFjh implements Serializable {
    private String kpdId;
    private String kpdMc;
    private String fjh;
    /**
     * 10,1,2,
     */
    private String fpzlDms;
    private String nsrsbh;
    /**
     * 税控设备标识
     */
    private String terminalCode;
}
