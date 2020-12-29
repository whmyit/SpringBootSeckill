package com.dxhy.order.protocol.v4.invoice;

import com.dxhy.order.protocol.v4.order.DDMXXX;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.util.List;

/**
 * 专票冲红 红字信息申请表 下载明细协议bean
 *
 * @author ZSC-DXHY-PC
 * @date 创建时间: 2018/12/19 9:25
 */
@Setter
@Getter
public class HZSQDXZZXX implements Serializable {
    
    /**
     * 红字信息表下载头信息
     */
    private HZSQDXZTXX HZSQDXZTXX;
    
    /**
     * 红字信息表明细信息
     */
    private List<DDMXXX> DDMXXX;
}
