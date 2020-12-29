package com.dxhy.order.model.a9.pdf;


import com.dxhy.order.model.a9.ResponseBaseBean;
import lombok.Getter;
import lombok.Setter;
/**
 * 调用底层获取pdf响应bean
 *
 * @author ZSC-DXHY
 * @date 创建时间: 2020-08-14 14:57
 */
@Getter
@Setter
public class GetPdfResponse extends ResponseBaseBean {
    
    private GetPdfResponseExtend result;
    
}
