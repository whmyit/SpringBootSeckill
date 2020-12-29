package com.dxhy.order.protocol.v4.buyermanage;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

/**
 * 购买方公共协议bean
 * @author: <a href="tivenninesongs@163.com">yaoxuguang</a>
 * @createDate: Created in 2020/4/18
 */
@Getter
@Setter
public class GMFXX_COMMON implements Serializable {
    private static final long serialVersionUID = 1L;

    /**
     * 购买方编码
     */
    private String GMFBM;

    /**
     * 购买方类型
     */
    private String GMFLX;

    /**
     * 购买方纳税人识别号
     */
    private String GMFSBH;

    /**
     * 购买方名称
     */
    private String GMFMC;

    /**
     * 购买方地址
     */
    private String GMFDZ;

    /**
     * 购买方电话
     */
    private String GMFDH;

    /**
     * 购买方银行
     */
    private String GMFYH;

    /**
     * 购买方帐号
     */
    private String GMFZH;

    /**
     * 备注
     */
    private String BZ;
}
