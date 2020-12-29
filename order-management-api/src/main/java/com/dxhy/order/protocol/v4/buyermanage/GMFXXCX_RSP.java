package com.dxhy.order.protocol.v4.buyermanage;

import com.dxhy.order.constant.ConfigureConstant;
import com.dxhy.order.constant.OrderInfoContentEnum;
import com.dxhy.order.protocol.v4.RESPONSEV4;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.util.List;
import java.util.Objects;

/**
 * 购买方信息查询接口返回协议bean
 * @author: <a href="tivenninesongs@163.com">yaoxuguang</a>
 * @createDate: Created in 2020/4/18
 */
@Getter
@Setter
public class GMFXXCX_RSP extends RESPONSEV4 implements Serializable {
    private static final long serialVersionUID = 1L;
    
    /**
     * 销售方纳税人识别号
     */
    private String XHFSBH;
    
    /**
     * "销售方纳税人名称
     */
    private String XHFMC;
    
    /**
     * 总个数
     */
    private String ZGS;
    
    
    /**
     * 购买方协议bean集合
     */
    private List<GMFXX_COMMON> GMFXX;
    
    /**
     * 静态方法的方式设置字段ZTDM和ZTXX的值
     * @author <a href="tivenninesongs@163.com">yaoxuguang</a>
     * @date 2020/4/20
     * @param orderInfoContentEnum 所有业务统一返回参数信息枚举
     * @return com.dxhy.order.protocol.v4.buyermanage.GMFXXCX_RSP
     */
    public static GMFXXCX_RSP build(OrderInfoContentEnum orderInfoContentEnum){
        GMFXXCX_RSP gmfxxcxRsp = new GMFXXCX_RSP();
        build(gmfxxcxRsp,orderInfoContentEnum);
        return gmfxxcxRsp;
    }

    /**
     * 静态方法的方式设置字段ZTDM和ZTXX的值
     * @author <a href="tivenninesongs@163.com">yaoxuguang</a>
     * @date 2020/4/20
     * @param gmfxxcxRsp 购买方信息查询接口返回协议bean
     * @param orderInfoContentEnum 所有业务统一返回参数信息枚举
     */
    public static void build(GMFXXCX_RSP gmfxxcxRsp, OrderInfoContentEnum orderInfoContentEnum){
        gmfxxcxRsp.setZTDM(orderInfoContentEnum.getKey());
        gmfxxcxRsp.setZTXX(orderInfoContentEnum.getMessage());
        gmfxxcxRsp.setZGS(ConfigureConstant.STRING_0);
    }

    /**
     * 初始化查询接口返回协议bean
     * @author <a href="tivenninesongs@163.com">yaoxuguang</a>
     * @date 2020/4/24
     * @param gmfxxcxReq 接口请求协议bean
     * @param gmfxxcxRsp 接口返回协议bean
     */
    public static void initResponse(GMFXXCX_REQ gmfxxcxReq,GMFXXCX_RSP gmfxxcxRsp){
        if(Objects.nonNull(gmfxxcxReq)){
            gmfxxcxRsp.setXHFSBH(gmfxxcxReq.getXHFSBH());
            gmfxxcxRsp.setXHFMC(gmfxxcxReq.getXHFMC());
            gmfxxcxRsp.setZGS(ConfigureConstant.STRING_0);
        }
    }
}
