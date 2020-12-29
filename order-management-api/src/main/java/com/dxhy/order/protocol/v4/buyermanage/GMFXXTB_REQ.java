package com.dxhy.order.protocol.v4.buyermanage;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.io.Serializable;

/**
 * 同步购买方信息请求协议bean
 * @author: <a href="tivenninesongs@163.com">yaoxuguang</a>
 * @createDate: Created in 2020/4/20
 */
@Getter
@Setter
@ToString
public class GMFXXTB_REQ extends GMFXX implements Serializable {
    private static final long serialVersionUID = 1L;

    /**
     * 操作类型
     */
    private String CZLX;
}
