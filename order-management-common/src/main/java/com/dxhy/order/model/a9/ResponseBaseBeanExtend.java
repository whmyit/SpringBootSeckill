package com.dxhy.order.model.a9;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
/**
 * 底层响应通用
 *
 * @author ZSC-DXHY
 * @date 创建时间: 2020-08-14 15:32
 */
@Getter
@Setter
@EqualsAndHashCode
public class ResponseBaseBeanExtend implements Serializable {
    
    
    private String statusCode;
    
    private String statusMessage;
    
}
