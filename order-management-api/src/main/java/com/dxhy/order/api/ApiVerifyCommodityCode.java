package com.dxhy.order.api;

import com.dxhy.order.protocol.v4.commodity.SPXXCX_REQ;
import com.dxhy.order.protocol.v4.commodity.SPXXTB_REQ;

import java.util.Map;

/**
 * 商品信息校验
 * @author: <a href="tivenninesongs@163.com">yaoxuguang</a>
 * @createDate: Created in 2020/4/21
 */
public interface ApiVerifyCommodityCode {
    /**
     * 查询商品信息校验
     * @author <a href="tivenninesongs@163.com">yaoxuguang</a>
     * @date 2020/4/21
     * @param spxxcxReq 请求协议bean
     * @return java.util.Map
     */
    Map<String,String> checkQueryCommodityRequestParam(SPXXCX_REQ spxxcxReq);

    /**
     * 同步商品信息校验
     * @author <a href="tivenninesongs@163.com">yaoxuguang</a>
     * @date 2020/4/21
     * @param spxxtbReq 同步商品信息请求协议bean
     * @return java.util.Map
     */
    Map<String, String> checkSyncCommodityRequestParam(SPXXTB_REQ spxxtbReq);
}
