package com.dxhy.order.protocol.fangge;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

/**
 * 方格UKey税局地址获取,中转业务使用
 *
 * @author ZSC-DXHY
 * @date 创建时间: 2020-08-27 10:32
 */
@Getter
@Setter
public class FgUkeySf implements Serializable {
    
    /**
     * 税局地址
     */
    private String sjdz;
    
    /**
     * 税局端口
     */
    private String sjdk;
}
