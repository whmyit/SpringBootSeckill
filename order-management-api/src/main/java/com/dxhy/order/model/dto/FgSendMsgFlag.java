package com.dxhy.order.model.dto;

import lombok.Getter;
import lombok.Setter;

/**
 * @Description:方格redis 方格发送状态实体
 * @Author:xueanna
 * @Date:2019/7/2
 */
@Getter
@Setter
public class FgSendMsgFlag {
    
    private String isSendFlag;
    
    private String lastUpdateTime;
    
}
