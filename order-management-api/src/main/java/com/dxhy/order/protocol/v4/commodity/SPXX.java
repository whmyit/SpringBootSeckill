package com.dxhy.order.protocol.v4.commodity;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

/**
 * 商品信息协议bean
 * @author: <a href="tivenninesongs@163.com">yaoxuguang</a>
 * @createDate: Created in 2020/4/18
 */
@Getter
@Setter
public class SPXX extends SPXX_COMMON implements Serializable {
    private static final long serialVersionUID = 1L;

    /**
     * 商品简称
     */
    private String SPJC;
}
