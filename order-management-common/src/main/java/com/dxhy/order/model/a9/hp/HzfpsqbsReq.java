package com.dxhy.order.model.a9.hp;

import com.dxhy.order.model.a9.RequestBaseBean;
import lombok.Getter;
import lombok.Setter;

/**
 * @Description: 红字发票申请单申请请求
 * @Author: csf
 * @CreateDate: 2018-07-23 14:48
 * @UpdateUser: csf
 * @UpdateDate: 2018-07-23 14:48
 * @UpdateRemark:
 * @Version: 1.0
 */
@Getter
@Setter
public class HzfpsqbsReq extends RequestBaseBean{
    
    private HzfpsqbscBatch HZFPSQBSCSBATCH;
    private Hzfpsqbsc[] HZFPSQBSCLIST;
}
