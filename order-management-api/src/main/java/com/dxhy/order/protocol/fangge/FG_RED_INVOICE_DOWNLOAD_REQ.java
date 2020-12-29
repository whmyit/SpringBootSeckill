package com.dxhy.order.protocol.fangge;


import com.dxhy.order.protocol.RESPONSE;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.io.Serializable;
import java.util.List;

/**
 * @Description:红票申请单下载 参数
 * @Author:xueanna
 * @Date:2019/7/3
 */
@ToString
@Setter
@Getter
public class FG_RED_INVOICE_DOWNLOAD_REQ extends RESPONSE implements Serializable {
    /**
     * 申请表审核结果下载请求批次号
     */
    private String SQBXZQQPCH;
    
    /**
     * 成功获取的个数
     */
    private String SUCCESS_COUNT;
    
    /**
     * 申请信息表明细数据
     */
    private List<FG_RED_INVOICE_FORM_DOWNLOAD> RED_INVOICE_FORM_DOWNLOADS;
}
