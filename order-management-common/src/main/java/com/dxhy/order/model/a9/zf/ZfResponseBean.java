package com.dxhy.order.model.a9.zf;

import com.dxhy.order.model.a9.ResponseBaseBean;
import lombok.Getter;
import lombok.Setter;

/**
 * 作废响应
 *
 * @author ZSC-DXHY
 * @date 创建时间: 2020-08-14 15:59
 */
@Getter
@Setter
public class ZfResponseBean extends ResponseBaseBean {
    
    private ZfResponseExtend result;
    
}
