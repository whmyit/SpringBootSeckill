package com.dxhy.order.consumer.modules.fiscal.service;

import com.dxhy.invoice.protocol.sk.doto.request.SkReqYhzxxcx;
import com.dxhy.invoice.protocol.sl.sld.DydxxcxRequest;
import com.dxhy.invoice.protocol.sl.sld.SldJspxxRequest;
import com.dxhy.invoice.protocol.sl.sld.SldJspxxResponse;
import com.dxhy.order.model.a9.dy.DydResponseExtend;
import com.dxhy.order.model.a9.query.YhzxxResponse;
import com.dxhy.order.model.a9.sld.SearchFjh;
import com.dxhy.order.model.bwactivexs.server.SkServerRequest;
import com.dxhy.order.model.bwactivexs.server.SkServerResponse;
import com.dxhy.order.model.entity.InvoiceQuotaEntity;

import java.util.List;
import java.util.Set;

/**
 * @author ：杨士勇
 * @ClassName ：UnifyService
 * @Description 统一与底层交互的接口
 * @date ：2019年6月1日 下午5:26:29
 */

public interface UnifyService {
    
    /**
     * 查询受理点发票份数
     *
     * @param sldJspxxRequest
     * @param terminalCode
     * @return
     */
    SldJspxxResponse querSldFpfs(SldJspxxRequest sldJspxxRequest, String terminalCode);
    
    /**
     * 月度汇总信息查询
     *
     * @param paramSkReqYhzxxcx
     * @param terminalCode
     * @return
     */
    YhzxxResponse queryYhzxx(SkReqYhzxxcx paramSkReqYhzxxcx, String terminalCode);
    
    /**
     * 查询打印点列表
     *
     * @param dydxxcxRequest
     * @param terminalCode
     * @return
     */
    DydResponseExtend queryDydxxcxList(DydxxcxRequest dydxxcxRequest, String terminalCode);
    
    /**
     * 获取开票点信息
     *
     * @param request
     * @param terminalCode
     * @return
     */
    SkServerResponse queryServerInfo(SkServerRequest request, String terminalCode);
    
    /**
     * 获取开票限额
     *
     * @param nsrsbh
     * @param fpzlDm
     * @param terminalCode
     * @return
     */
    InvoiceQuotaEntity queryInvoiceQuotaInfoFromRedis(String nsrsbh, String fpzlDm, String terminalCode);
    
    /**
     * 获取分机号
     *
     * @param shList
     * @param fjh
     * @return
     */
    Set<SearchFjh> getFjh(List<String> shList, String fjh);
    
}
