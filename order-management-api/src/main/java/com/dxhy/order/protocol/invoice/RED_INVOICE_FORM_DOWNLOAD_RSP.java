package com.dxhy.order.protocol.invoice;

import com.dxhy.order.protocol.RESPONSE;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.util.List;

/**
 * 红字信息表下载 响应协议bean
 *
 * @author ZSC-DXHY-PC
 * @date 创建时间: 2018/12/19 10:53
 */
@Setter
@Getter
public class RED_INVOICE_FORM_DOWNLOAD_RSP extends RESPONSE implements Serializable {
    
    /**
     * 申请表下载请求批次号
     */
    private String SQBXZQQPCH;
    
    /**
     * 成功获取的个数
     */
    private String SUCCESS_COUNT;
    
    /**
     * 申请信息表明细数据
     */
    private List<RED_INVOICE_FORM_DOWNLOAD> RED_INVOICE_FORM_DOWNLOADS;
}
