package com.dxhy.order.protocol.v4.buyermanage;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.io.Serializable;

/**
 * 购买方信息查询接口请求协议bean
 * @author: <a href="tivenninesongs@163.com">yaoxuguang</a>
 * @createDate: Created in 2020/4/18
 */
@Getter
@Setter
@ToString
public class GMFXXCX_REQ implements Serializable {
    private static final long serialVersionUID = 1L;

    /**
     * 购买方编码
     */
    private String GMFBM;

    /**
     * 销货方纳税人识别号
     */
    private String XHFSBH;

    /**
     * 销货方纳税人名称
     */
    private String XHFMC;

    /**
     * 购买方名称
     */
    private String GMFMC;

    /**
     * 购买方纳税人识别号
     */
    private String GMFSBH;

    /**
     * 页数
     */
    private String YS;

    /**
     * 个数
     */
    private String GS;
}
