package com.dxhy.order.consumer.model.page;


import lombok.Getter;
import lombok.Setter;

/**
 * @author ：杨士勇
 * @ClassName ：PageOrderInfo
 * @Description ：对应页面开票接口参数
 * @date ：2018年7月23日 下午3:18:33
 */
@Setter
@Getter
public class PageOrderInfo {
  
    /**
     *  订单处理表id 
     */
    private String processId;
    /**
     * 订单表id
     */
    private String id;
    /**
     * 开票合计金额
     */
    private String kphjje;
    /**
     * 订单状态
     */
    private String status;
    
    /**
     * 订单号
     */
    private String ddh;
    
    /**
     * 销方税号
     */
    private String xhfNsrsbh;


}
