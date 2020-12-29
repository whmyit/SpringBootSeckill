package com.dxhy.order.model.bwactivexs.kpd;

import com.dxhy.order.model.a9.RequestBaseBean;
import lombok.Getter;
import lombok.Setter;

import java.util.List;
/**
 * 开票点请求
 *
 * @author ZSC-DXHY
 * @date 创建时间: 2020-08-14 15:19
 */
@Getter
@Setter
public class KpdRequset extends RequestBaseBean{
    
    private List<String> nsrsbhs;
    
    private String skpId;

}
