package com.dxhy.order.protocol.v4.buyermanage;

import com.dxhy.order.constant.OrderInfoContentEnum;
import com.dxhy.order.protocol.v4.RESPONSEV4;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.util.Objects;

/**
 * 购买方信息同步接口返回协议bean
 *
 * @author: <a href="tivenninesongs@163.com">yaoxuguang</a>
 * @createDate: Created in 2020/4/18
 */
@Getter
@Setter
public class GMFXXTB_RSP extends RESPONSEV4 implements Serializable {
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
     * 购买方纳税人识别号
     */
    private String GMFSBH;

    /**
     * 购买方名称
     */
    private String GMFMC;

    /**
     * 静态方法的方式设置字段ZTDM和ZTXX的值
     *
     * @param orderInfoContentEnum 所有业务统一返回参数信息枚举
     * @return com.dxhy.order.protocol.v4.buyermanage.GMFXXTB_RSP
     * @author <a href="tivenninesongs@163.com">yaoxuguang</a>
     * @date 2020/4/21
     */
    public static GMFXXTB_RSP build(OrderInfoContentEnum orderInfoContentEnum) {
        GMFXXTB_RSP gmfxxtbRsp = new GMFXXTB_RSP();
        build(gmfxxtbRsp, orderInfoContentEnum);
        return gmfxxtbRsp;
    }

    /**
     * 静态方法的方式设置字段ZTDM和ZTXX的值
     *
     * @param gmfxxtbRsp           接口返回协议bean
     * @param orderInfoContentEnum 所有业务统一返回参数信息枚举
     * @author <a href="tivenninesongs@163.com">yaoxuguang</a>
     * @date 2020/4/21
     */
    public static void build(GMFXXTB_RSP gmfxxtbRsp, OrderInfoContentEnum orderInfoContentEnum) {
        gmfxxtbRsp.setZTDM(orderInfoContentEnum.getKey());
        gmfxxtbRsp.setZTXX(orderInfoContentEnum.getMessage());
    }

    /**
     * 初始化同步接口返回协议bean
     * @author <a href="tivenninesongs@163.com">yaoxuguang</a>
     * @date 2020/4/24
     * @param gmfxxtbReq 接口返回协议bean
     * @param gmfxxtbRsp 请求协议bean
     */
    public static void initResponse(GMFXXTB_REQ gmfxxtbReq, GMFXXTB_RSP gmfxxtbRsp) {
        if (Objects.nonNull(gmfxxtbReq)) {
            gmfxxtbRsp.setGMFBM(gmfxxtbReq.getGMFBM());
            gmfxxtbRsp.setXHFSBH(gmfxxtbReq.getXHFSBH());
            gmfxxtbRsp.setXHFMC(gmfxxtbReq.getXHFMC());
            gmfxxtbRsp.setGMFSBH(gmfxxtbReq.getGMFSBH());
            gmfxxtbRsp.setGMFMC(gmfxxtbReq.getGMFMC());
        }
    }
}
