package com.dxhy.order.model.a9.zf;

import com.dxhy.order.model.a9.ResponseBaseBeanExtend;
import lombok.Getter;
import lombok.Setter;

/**
 * 空白发票响应扩展
 *
 * @author ZSC-DXHY
 * @date 创建时间: 2020-08-14 15:18
 */
@Setter
@Getter
public class KbZfResponseExtend extends ResponseBaseBeanExtend{
	
	 /**
     * 作废失败的发票
     */
    private String deprecate_failed_balan_invoice;
    
    
    private String deprecate_failed_invoice;
    
    /**
     * 作废批次号
     */
    private String zfpch;

}
