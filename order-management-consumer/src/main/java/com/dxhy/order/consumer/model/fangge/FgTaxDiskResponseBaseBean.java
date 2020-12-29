package com.dxhy.order.consumer.model.fangge;

import lombok.Getter;
import lombok.Setter;

import java.util.Map;

/**
 * @Description:调用接口返回数据
 * @Author:xueanna
 * @Date:2019/7/2
 */
@Getter
@Setter
public class FgTaxDiskResponseBaseBean extends FgResponseBaseBean {
    
    private Map data;
    
}
