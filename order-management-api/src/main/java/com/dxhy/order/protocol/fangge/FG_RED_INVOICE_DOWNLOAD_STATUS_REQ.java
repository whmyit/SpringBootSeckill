package com.dxhy.order.protocol.fangge;


import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.io.Serializable;

/**
 * @Description: 方格接口  红票申请单下载修改状态参数
 * @Author:xueanna
 * @Date:2019/6/25
 */
@ToString
@Setter
@Getter
public class FG_RED_INVOICE_DOWNLOAD_STATUS_REQ implements Serializable {
    /**
     * 纳税人识别号
     */
    private String NSRSBH;
    
    /**
     * 申请表审核结果下载请求批次号
     */
    private String SQBXZQQPCH;
    /**
     * 数据状态
     */
    private String SJZT;
    
    
}
