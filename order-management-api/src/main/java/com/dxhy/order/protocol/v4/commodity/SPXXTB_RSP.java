package com.dxhy.order.protocol.v4.commodity;

import com.dxhy.order.constant.OrderInfoContentEnum;
import com.dxhy.order.protocol.v4.RESPONSEV4;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.util.Objects;

/**
 * 商品信息同步接口返回协议bean
 * @author: <a href="tivenninesongs@163.com">yaoxuguang</a>
 * @createDate: Created in 2020/4/18
 */
@Getter
@Setter
public class SPXXTB_RSP extends RESPONSEV4 implements Serializable {
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
     * 商品编码
     */
    private String SPBM;

    /**
     * 项目名称
     */
    private String XMMC;

    /**
     * 静态方法的方式设置字段ZTDM和ZTXX的值
     * @author <a href="tivenninesongs@163.com">yaoxuguang</a>
     * @date 2020/4/20
     * @param orderInfoContentEnum 所有业务统一返回参数信息枚举
     * @return  com.dxhy.order.protocol.v4.commodity.SPXXTB_RSP
     */
    public static SPXXTB_RSP build(OrderInfoContentEnum orderInfoContentEnum){
        SPXXTB_RSP spxxtbRsp = new SPXXTB_RSP();
        build(spxxtbRsp,orderInfoContentEnum);
        return spxxtbRsp;
    }

    /**
     * 静态方法的方式设置字段ZTDM和ZTXX的值
     * @author <a href="tivenninesongs@163.com">yaoxuguang</a>
     * @date 2020/4/20
     * @param spxxtbRsp SPXXTB_RSP实例
     * @param orderInfoContentEnum 所有业务统一返回参数信息枚举
     */
    public static void build (SPXXTB_RSP spxxtbRsp, OrderInfoContentEnum orderInfoContentEnum){
        spxxtbRsp.setZTDM(orderInfoContentEnum.getKey());
        spxxtbRsp.setZTXX(orderInfoContentEnum.getMessage());
    }

    /**
     * 初始化接口返回协议bean
     * @author <a href="tivenninesongs@163.com">yaoxuguang</a>
     * @date 2020/4/24
     * @param spxxtbReq 同步商品信息请求协议bean
     * @param spxxtbRsp 接口返回协议bean
     */
    public static void initResponse(SPXXTB_REQ spxxtbReq, SPXXTB_RSP spxxtbRsp) {
        if (Objects.nonNull(spxxtbReq)) {
            spxxtbRsp.setSPID(spxxtbReq.getSPID());
            spxxtbRsp.setXHFSBH(spxxtbReq.getXHFSBH());
            spxxtbRsp.setXHFMC(spxxtbReq.getXHFMC());
            spxxtbRsp.setSPBM(spxxtbReq.getSPBM());
            spxxtbRsp.setXMMC(spxxtbReq.getXMMC());
        }
    }
}
