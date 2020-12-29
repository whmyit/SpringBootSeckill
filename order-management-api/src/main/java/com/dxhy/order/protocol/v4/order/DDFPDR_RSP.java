package com.dxhy.order.protocol.v4.order;

import com.dxhy.order.constant.OrderInfoContentEnum;
import com.dxhy.order.protocol.v4.RESPONSEV4;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.util.Objects;

/**
 * 历史订单发票数据导入返回协议bean
 * @author: <a href="tivenninesongs@163.com">yaoxuguang</a>
 * @createDate: Created in 2020/4/23
 */
@Getter
@Setter
public class DDFPDR_RSP extends RESPONSEV4 implements Serializable {
    /**
     * 订单发票请求流水号
     */
    private String DDQQLSH;

    /**
     * 静态方法的方式设置字段ZTDM和ZTXX的值
     * @author <a href="tivenninesongs@163.com">yaoxuguang</a>
     * @date 2020/4/24
     * @param orderInfoContentEnum 所有业务统一返回参数信息枚举
     * @return com.dxhy.order.protocol.v4.order.DDFPDR_RSP
     */
    public static DDFPDR_RSP build(OrderInfoContentEnum orderInfoContentEnum){
        DDFPDR_RSP ddfpdrRsp = new DDFPDR_RSP();
        build(ddfpdrRsp, orderInfoContentEnum);
        return ddfpdrRsp;
    }

    /**
     * 静态方法的方式设置字段ZTDM和ZTXX的值
     * @author <a href="tivenninesongs@163.com">yaoxuguang</a>
     * @date 2020/4/24
     * @param ddfpdrRsp 返回协议bean
     * @param orderInfoContentEnum 所有业务统一返回参数信息枚举
     */
    public static void build(DDFPDR_RSP ddfpdrRsp,OrderInfoContentEnum orderInfoContentEnum){
        ddfpdrRsp.setZTDM(orderInfoContentEnum.getKey());
        ddfpdrRsp.setZTXX(orderInfoContentEnum.getMessage());
    }

    /**
     * 初始化接口返回对象
     * @author <a href="tivenninesongs@163.com">yaoxuguang</a>
     * @date 2020/4/24
     * @param ddfpdrRsp 返回协议bean
     * @param ddfpzxx 订单发票全数据返回协议bean
     */
    public static void initResponse(DDFPDR_RSP ddfpdrRsp, DDFPZXX ddfpzxx){
        if(Objects.isNull(ddfpzxx) || Objects.isNull(ddfpzxx.getDDFPXX())){
            ddfpdrRsp.setDDQQLSH("");
        }else{
            ddfpdrRsp.setDDQQLSH(ddfpzxx.getDDFPXX().getDDQQLSH());
        }
    }
}
