package com.dxhy.order.protocol.v4.commodity;

import com.dxhy.order.constant.ConfigureConstant;
import com.dxhy.order.constant.OrderInfoContentEnum;
import com.dxhy.order.protocol.v4.RESPONSEV4;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.util.List;
import java.util.Objects;

/**
 * 商品信息查询对外接口返回协议bean
 * @author: <a href="tivenninesongs@163.com">yaoxuguang</a>
 * @createDate: Created in 2020/4/18
 */
@Getter
@Setter
public class SPXXCX_RSP extends RESPONSEV4 implements Serializable {
    private static final long serialVersionUID = 1L;
    
    /**
     * 销货方纳税人识别号
     */
    private String XHFSBH;
    
    /**
     * 销货方纳税人名称
     */
    private String XHFMC;
    
    /**
     * 总个数
     */
    private String ZGS;
    
    /**
     * 商品信息协议bea集合
     */
    private List<SPXX> SPXX;
    
    /**
     * 静态方法的方式设置字段ZTDM和ZTXX的值
     * @author <a href="tivenninesongs@163.com">yaoxuguang</a>
     * @date 2020/4/24
     * @param orderInfoContentEnum 所有业务统一返回参数信息枚举
     * @return com.dxhy.order.protocol.v4.commodity.SPXXCX_RSP
     */
    public static SPXXCX_RSP build(OrderInfoContentEnum orderInfoContentEnum){
        SPXXCX_RSP spxxcxRsp = new SPXXCX_RSP();
        build(spxxcxRsp,orderInfoContentEnum);
        return spxxcxRsp;
    }

    /**
     * 静态方法的方式设置字段ZTDM和ZTXX的值
     * @param spxxcxRsp SPXXCX_RSP实例
     * @param orderInfoContentEnum 所有业务统一返回参数信息枚举
     */
    public static void build(SPXXCX_RSP spxxcxRsp,OrderInfoContentEnum orderInfoContentEnum){
        spxxcxRsp.setZTDM(orderInfoContentEnum.getKey());
        spxxcxRsp.setZTXX(orderInfoContentEnum.getMessage());
        spxxcxRsp.setZGS(ConfigureConstant.STRING_0);
    }

    /**
     * 初始化返回协议bean
     * @author <a href="tivenninesongs@163.com">yaoxuguang</a>
     * @date 2020/4/24
     * @param spxxcxReq 请求协议bean
     * @param spxxcxRsp 返回协议bean
     */
    public static void initResponse(SPXXCX_REQ spxxcxReq,SPXXCX_RSP spxxcxRsp){
        if(Objects.isNull(spxxcxReq)){
            spxxcxRsp.setXHFSBH("");
            spxxcxRsp.setXHFMC("");
        }else {
            spxxcxRsp.setXHFSBH(Objects.isNull(spxxcxReq.getXHFSBH())?"":spxxcxReq.getXHFSBH());
            spxxcxRsp.setXHFMC(Objects.isNull(spxxcxReq.getXHFMC()) ? "" : spxxcxReq.getXHFMC());
            spxxcxRsp.setZGS(ConfigureConstant.STRING_0);
        }
    }
}
