package com.dxhy.order.protocol.v4.buyermanage;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

/**
 * 购买方信息协议bean
 *
 * @author: <a href="tivenninesongs@163.com">yaoxuguang</a>
 * @createDate: Created in 2020/4/18 20:18
 */
@Getter
@Setter
public class GMFXX extends GMFXX_COMMON implements Serializable {
    private static final long serialVersionUID = 1L;

    /**
     * 销货方纳税人识别号
     */
    private String XHFSBH;

    /**
     * 销货方纳税人名称
     */
    private String XHFMC;
}