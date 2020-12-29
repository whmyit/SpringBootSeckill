package com.dxhy.order.consumer.openapi.service;


import com.dxhy.order.protocol.v4.buyermanage.GMFXXCX_REQ;
import com.dxhy.order.protocol.v4.buyermanage.GMFXXCX_RSP;
import com.dxhy.order.protocol.v4.buyermanage.GMFXXTB_REQ;
import com.dxhy.order.protocol.v4.buyermanage.GMFXXTB_RSP;
import com.dxhy.order.protocol.v4.commodity.SPXXCX_REQ;
import com.dxhy.order.protocol.v4.commodity.SPXXCX_RSP;
import com.dxhy.order.protocol.v4.commodity.SPXXTB_REQ;
import com.dxhy.order.protocol.v4.commodity.SPXXTB_RSP;
import com.dxhy.order.protocol.v4.fpyl.FPYLCX_REQ;
import com.dxhy.order.protocol.v4.fpyl.FPYLCX_RSP;
import com.dxhy.order.protocol.v4.invoice.HZSQDSC_REQ;
import com.dxhy.order.protocol.v4.invoice.HZSQDSC_RSP;
import com.dxhy.order.protocol.v4.invoice.HZSQDXZ_REQ;
import com.dxhy.order.protocol.v4.invoice.HZSQDXZ_RSP;
import com.dxhy.order.protocol.v4.order.*;
import com.dxhy.order.protocol.v4.taxequipment.SKSBXXTB_REQ;
import com.dxhy.order.protocol.v4.taxequipment.SKSBXXTB_RSP;

import java.util.List;

/**
 * @Description: 订单对外接口业务层接口V3
 * @author: chengyafu
 * @date: 2018年8月13日 下午4:48:28
 */
public interface IInterfaceServiceV3 {
    
    /**
     * 发票结果获取
     *
     * @param ddkjxxReq
     * @return
     */
    DDKJXX_RSP getAllocatedInvoicesV3(DDKJXX_REQ ddkjxxReq);
    
    /**
     * 根据订单号获取订单数据以及发票数据接口
     *
     * @param ddfpcxReq
     * @return
     */
    DDFPCX_RSP getOrderInfoAndInvoiceInfoV3(DDFPCX_REQ ddfpcxReq);
    
    /**
     * 专票冲红申请单
     *
     * @param hzsqdscReq
     * @param kpjh
     * @return
     */
    HZSQDSC_RSP specialInvoiceRushRedV3(HZSQDSC_REQ hzsqdscReq, String kpjh);
    
    /**
     * 红字发票申请下载V3
     *
     * @param hzsqdxzReq
     * @param sldid
     * @param kpjh
     * @return
     */
    HZSQDXZ_RSP downSpecialInvoiceV3(HZSQDXZ_REQ hzsqdxzReq, String sldid, String kpjh);
    
    /**
     * 导入已开发票的数据
     *
     * @param ddfpzxxList 订单发票协议bean集合
     * @return com.dxhy.order.protocol.v4.order.DDFPDR_RSP
     * @author <a href="tivenninesongs@163.com">yaoxuguang</a>
     * @date 2020/4/15
     */
    List<DDFPDR_RSP> importIssuedInvoice(List<DDFPZXX> ddfpzxxList);
    
    /**
     * 查询商品信息
     *
     * @param spxxcxReq 商品信息请求协议bean
     * @return com.dxhy.order.protocol.v4.order.SPXXCX_RSP
     * @author <a href="tivenninesongs@163.com">yaoxuguang</a>
     * @date 2020/4/18
     */
    SPXXCX_RSP queryCommodityMessage(SPXXCX_REQ spxxcxReq);
    
    /**
     * 同步商品信息
     *
     * @param spxxtbReqList 同步商品信息协议bean集合
     * @return com.dxhy.order.protocol.v4.commodity.SPXXTB_RSP
     * @author <a href="tivenninesongs@163.com">yaoxuguang</a>
     * @date 2020/4/20
     */
    List<SPXXTB_RSP> syncCommodityMessage(List<SPXXTB_REQ> spxxtbReqList);
    
    /**
     * 查询购买方信息
     *
     * @param gmfxxcxReq 购买方信息查询接口请求协议bean
     * @return java.util.List<com.dxhy.order.protocol.v4.buyermanage.GMFXX_COMMON>
     * @author <a href="tivenninesongs@163.com">yaoxuguang</a>
     * @date 2020/4/20
     */
    GMFXXCX_RSP queryBuyerMessage(GMFXXCX_REQ gmfxxcxReq);
    
    /**
     * 同步购买方信息
     *
     * @param gmfxxtbReqList 同步购买方信息请求协议bean集合
     * @return java.util.List<com.dxhy.order.protocol.v4.buyermanage.GMFXXTB_RSP>
     * @author <a href="tivenninesongs@163.com">yaoxuguang</a>
     * @date 2020/4/20
     */
    List<GMFXXTB_RSP> syncBuyerMessage(List<GMFXXTB_REQ> gmfxxtbReqList);
    
    /**
     * 税控设备信息同步
     *
     * @param sksbxxtbReqList
     * @return
     */
    List<SKSBXXTB_RSP> syncTaxEquipmentInfo(List<SKSBXXTB_REQ> sksbxxtbReqList);
    
    /**
     * 发票余量查询接口
     *
     * @param fpylcxReq
     * @return
     */
    List<FPYLCX_RSP> queryInvoiceStore(FPYLCX_REQ fpylcxReq);

    /**
     * 订单删除接口
     * @param ddsc_req
     * @return
     */
    DDSC_RSP orderDelete(DDSC_REQ ddsc_req);
}
