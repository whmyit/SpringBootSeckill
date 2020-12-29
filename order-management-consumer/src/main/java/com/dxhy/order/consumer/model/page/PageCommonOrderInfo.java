package com.dxhy.order.consumer.model.page;


import lombok.Getter;
import lombok.Setter;

import java.util.List;

/**
 *
 * @ClassName ：PageCommonOrderInfo
 * @Description ：对应页面请求的参数
 * @author ：杨士勇
 * @date ：2019年1月12日 下午4:43:01
 *
 *
 */
@Setter
@Getter
public class PageCommonOrderInfo {
    
    private PageOrderInfo orderInfo;
    
    private List<PageOrderItemInfo> orderItemInfo;
    
    
}
