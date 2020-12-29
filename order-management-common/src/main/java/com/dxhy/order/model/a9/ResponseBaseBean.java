package com.dxhy.order.model.a9;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

/**
 * @author ：杨士勇
 * @ClassName ：ResponseBaseBean
 * @Description ：A9接口基础bean
 * @date ：2019年5月28日 下午5:39:28
 */
@Getter
@Setter
public class ResponseBaseBean implements Serializable {
    
    private String msg;
    
    private String code;
    
}
