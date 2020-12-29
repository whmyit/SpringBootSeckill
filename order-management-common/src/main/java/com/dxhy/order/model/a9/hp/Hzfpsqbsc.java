package com.dxhy.order.model.a9.hp;

import lombok.Getter;
import lombok.Setter;

/**
 * @Description: 红字发票申请单申请
 * @Author: csf
 * @CreateDate: 2018-07-23 14:48
 * @UpdateUser: csf
 * @UpdateDate: 2018-07-23 14:48
 * @UpdateRemark:
 * @Version: 1.0
 */
@Getter
@Setter
public class Hzfpsqbsc {
    
    private static final long serialVersionUID = -740937381726082832L;
    private HzfpsqbsHead HZFPSQBSCHEAD;
    private HzfpsqbsDetail[] HZFPSQBSCDETAILIST;
    
}
