package com.dxhy.order.model.a9.zf;

import com.dxhy.order.model.a9.ResponseBaseBeanExtend;
import lombok.Getter;
import lombok.Setter;
/**
 * 作废响应扩展
 *
 * @author ZSC-DXHY
 * @date 创建时间: 2020-08-14 16:00
 */
@Getter
@Setter
public class ZfResponseExtend extends ResponseBaseBeanExtend {
    
    
    /**
     * 作废失败的发票
     */
    private String deprecate_failed_invoice;
    
    
    /**
     * 作废批次号
     */
    private String zfpch;
    
    
}
