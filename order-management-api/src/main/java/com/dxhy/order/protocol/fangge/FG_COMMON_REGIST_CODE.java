package com.dxhy.order.protocol.fangge;


import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.io.Serializable;

/**
 * @Description:
 * @Author:liuhx
 */
@ToString
@Setter
@Getter
public class FG_COMMON_REGIST_CODE implements Serializable {
    /**
     * 纳税人识别号
     */
    private String NSRSBH;
    
}
