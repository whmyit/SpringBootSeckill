package com.dxhy.order.api;

import com.dxhy.order.protocol.v4.buyermanage.GMFXXCX_REQ;
import com.dxhy.order.protocol.v4.buyermanage.GMFXXTB_REQ;

import java.util.Map;

/**
 * 购买方信息校验
 * @author: <a href="tivenninesongs@163.com">yaoxuguang</a>
 * @createDate: Created in 2020/4/21
 */
public interface ApiVerifyBuyerManageInfo {
    /**
     * 查询购买方信息校验
     * @author <a href="tivenninesongs@163.com">yaoxuguang</a>
     * @date 2020/4/21
     * @param gmfxxcxReq 购买方信息查询接口请求协议bean
     * @return java.util.Map
     */
    Map<String,String> checkQueryBuyerRequestParam(GMFXXCX_REQ gmfxxcxReq);

    /**
     * 同步购买方信息校验
     * @author <a href="tivenninesongs@163.com">yaoxuguang</a>
     * @date 2020/4/21
     * @param gmfxxtbReq 购买方信息请求协议bean
     * @return java.util.Map
     */
    Map<String,String> checkSyncBuyerRequestParam(GMFXXTB_REQ gmfxxtbReq);
}
