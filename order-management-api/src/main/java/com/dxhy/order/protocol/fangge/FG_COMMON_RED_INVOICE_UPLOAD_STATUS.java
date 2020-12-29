package com.dxhy.order.protocol.fangge;


import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.io.Serializable;
import java.util.List;

/**
 * @Description: 方格接口  红票申请单上传修改状态实体    @Author:xueanna
 * @Date:2019/6/25
 */
@ToString
@Setter
@Getter
public class FG_COMMON_RED_INVOICE_UPLOAD_STATUS implements Serializable {
    /**
     * 纳税人识别号
     */
    private String NSRSBH;
    
    private String SQBSCQQPCH;
    
    /**
     * 红票申请单数据状态
     */
    private List<FG_COMMON_RED_INVOICE_UPLOAD> SQDQQSJ;
    
}
