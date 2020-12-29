package com.dxhy.order.protocol.fangge;


import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.io.Serializable;

/**
 * @Description: 方格接口  红票申请单上传修改状态实体    @Author:xueanna
 * @Date:2019/6/25
 */
@ToString
@Setter
@Getter
public class FG_COMMON_RED_INVOICE_UPLOAD implements Serializable {
    /**
     * 申请表上传请求流水号
     */
    private String SQBSCQQLSH;
    
    /**
     * 红票申请单数据状态
     */
    private String SJZT;
    
}
