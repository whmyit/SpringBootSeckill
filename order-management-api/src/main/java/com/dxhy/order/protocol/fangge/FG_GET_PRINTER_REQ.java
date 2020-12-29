package com.dxhy.order.protocol.fangge;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.io.Serializable;

/**
 * @ClassName FG_GET_PRINTER_REQ
 * @Description 获取打印机参数
 * @Author 刘会雄
 * @Date 2020/5/27 16:51
 */
@ToString
@Setter
@Getter
public class FG_GET_PRINTER_REQ implements Serializable {
    /**
     * 纳税人识别号
     */
    private String NSRSBH;
    
    private String FPZLDM;
}
