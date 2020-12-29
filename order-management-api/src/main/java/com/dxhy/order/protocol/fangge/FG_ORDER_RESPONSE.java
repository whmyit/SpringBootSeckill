package com.dxhy.order.protocol.fangge;

import com.dxhy.order.protocol.RESPONSE;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

/**
 * @Description: 业务层返回数据实体及状态
 * @Author:xueanna
 * @Date:2019/6/25
 */
@Setter
@Getter
public class FG_ORDER_RESPONSE<T> extends RESPONSE implements Serializable {
    
    private T data;
    
}
