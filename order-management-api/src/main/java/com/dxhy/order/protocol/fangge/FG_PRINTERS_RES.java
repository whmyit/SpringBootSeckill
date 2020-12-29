package com.dxhy.order.protocol.fangge;

import com.dxhy.order.protocol.RESPONSE;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

/**
 * @ClassName FG_TAX_INFOS_RES
 * @Description
 * @Author 刘会雄
 * @Date 2020/5/5 14:01
 */
@Getter
@Setter
public class FG_PRINTERS_RES<T> extends RESPONSE implements Serializable {
    private T DYJS;
}
