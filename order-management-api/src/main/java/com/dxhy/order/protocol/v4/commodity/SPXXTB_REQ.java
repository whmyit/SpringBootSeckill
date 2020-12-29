package com.dxhy.order.protocol.v4.commodity;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.io.Serializable;

/**
 * 同步商品信息请求协议bean
 * @author: <a href="tivenninesongs@163.com">yaoxuguang</a>
 * @createDate: Created in 2020/4/20
 */
@Getter
@Setter
@ToString
public class SPXXTB_REQ extends SPXX_COMMON implements Serializable {
    private static final long serialVersionUID = 6665605206220977078L;

    /**
     * 操作类型
     */
    private String CZLX;
}
