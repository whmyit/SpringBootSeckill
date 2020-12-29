package com.dxhy.order.api;

import com.dxhy.order.model.PageUtils;
import com.dxhy.order.model.dto.KdniaoQueryReq;
import com.dxhy.order.model.dto.KdniaoRes;
import com.dxhy.order.model.entity.FpExpress;

import java.util.List;
import java.util.Map;

/**
 * 发票物流信息
 *
 * @author ZSC-DXHY
 * @date 创建时间: 2020-08-14 9:17
 */
public interface ApiFpExpressService {
    /**
     * 生成快递单
     *
     * @param record
     * @return
     */
    int track(FpExpress record);
    
    /**
     * 查询快递列表
     *
     * @param map
     * @return
     */
    PageUtils queryListByPage(Map map);
    
    /**
     * 快递信息跟踪
     *
     * @param req
     * @return
     */
    KdniaoRes getOrderTraces(KdniaoQueryReq req);
    
    /**
     * 查询未签收的快递单号/订单号/快递公司编码列表
     *
     * @return
     */
    List<FpExpress> queryWqs();
    
    /**
     * 快递公司列表
     *
     * @param map
     * @return
     */
    List<FpExpress> expressCompanyList(Map<String, Object> map);
    
    /**
     * 更新快递信息
     *
     * @param fpExpress
     * @return
     */
    int updateExpressInfo(FpExpress fpExpress);
}
