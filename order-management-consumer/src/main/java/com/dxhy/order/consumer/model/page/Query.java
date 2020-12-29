package com.dxhy.order.consumer.model.page;

import lombok.Getter;
import lombok.Setter;

/**
 *
 * @ClassName ：Query
 * @Description ：前端查询对象
 * @author ：杨士勇
 * @date ：2019年7月1日 下午5:06:47
 *
 *
 */
@Getter
@Setter
public class Query {
	
    private String kpzt;
    private String pushStatus;
    private String startTime;
    private String endTime;
    private String zfbz;
    private String xhfNsrsbh;
    private String ddh;
    private String fphm;
    private String fpdm;
    private String gmfmc;
    private String fplx;
    private String kplx;
    private String mdh;
    private String sld;
    private String xhfmc;
    private String minhjje;
    private String maxhjje;
    private String ddly;
    
    /**
     * 冲红标志
     */
    private String chbz;
    /**
     * 业务类型
     */
    private String ywlxId;
    
}
