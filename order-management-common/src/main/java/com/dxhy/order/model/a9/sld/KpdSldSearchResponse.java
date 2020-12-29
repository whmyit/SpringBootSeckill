package com.dxhy.order.model.a9.sld;

import lombok.Getter;
import lombok.Setter;
/**
 * 开票点受理点查询请求
 *
 * @author ZSC-DXHY
 * @date 创建时间: 2020-08-14 15:19
 */
@Getter
@Setter
public class KpdSldSearchResponse {
    
    private String kpdId;
    private String fjh;
    private String kpdName;
    private String kpr;
    private String kprId;
    private String cpyLx;
    private String fpzlDm;
}
