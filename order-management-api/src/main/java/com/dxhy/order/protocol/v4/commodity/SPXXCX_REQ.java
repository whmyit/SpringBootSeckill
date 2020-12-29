package com.dxhy.order.protocol.v4.commodity;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.io.Serializable;

/**
 *  获取商品信息请求协议bean
 * @author: <a href="tivenninesongs@163.com">yaoxuguang</a>
 * @createDate: Created in 2020/4/18
 */
@Getter
@Setter
@ToString
public class SPXXCX_REQ implements Serializable {
    private static final long serialVersionUID = 1L;

    /**
     * 商品对应的ID
     */
    private String SPID;

    /**
     * 销货方纳税人识别号
     */
    private String XHFSBH;

    /**
     * 销货方纳税人名称
     */
    private String XHFMC;

    /**
     * 项目名称
     */
    private String XMMC;

    /**
     * 页数
     */
    private String YS;

    /**
     * 个数
     */
    private String GS;
}
