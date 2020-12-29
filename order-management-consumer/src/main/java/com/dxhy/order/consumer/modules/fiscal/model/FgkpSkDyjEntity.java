package com.dxhy.order.consumer.modules.fiscal.model;

import lombok.Getter;
import lombok.Setter;

import java.util.Date;
import java.util.List;

/**
 * 方格开票打印机
 *
 * @author ZSC-DXHY
 * @date 创建时间: 2020-08-14 14:50
 */
@Getter
@Setter
public class FgkpSkDyjEntity {

    private Integer id;
    private String fpzlDm;
    private String nsrsbh;
    private String mc;
    private Integer sbj;
    private Integer zbj;
    private Integer sjzt;
    private String cjr;
    private Date cjsj;
    private String bjr;
    private Date bjsj;
    private Integer qybz;
    private String nsrmc;
    private List<String> nsrsbhs;
}
