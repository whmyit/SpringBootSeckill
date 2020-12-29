package com.dxhy.order.protocol.fangge;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;


/**
 * @Description:方格接口 获取红票申请单下载数据请求参数
 * @Author:xueanna
 * @Date:2019/6/25
 */
@Setter
@Getter
public class FG_GET_INVOICE_DOWNLOAD_REQ implements Serializable {
    
    /**
     * 纳税人识别号
     */
    private String NSRSBH;
    
    /**
     * 申请表审核结果下载请求批次号
     */
    private String SQBXZQQPCH;
    
}
