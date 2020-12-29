package com.dxhy.order.consumer.modules.fiscal.service.a9;

import com.alibaba.fastjson.JSONObject;
import com.dxhy.order.model.a9.c48ydtj.YdtjDetailParam;
import com.dxhy.order.model.a9.c48ydtj.YdtjDto;
import com.dxhy.order.model.a9.c48ydtj.YdtjParam;
import com.dxhy.order.model.a9.dy.DydListRequst;
import com.dxhy.order.model.a9.dy.DydResponse;
import com.dxhy.order.model.a9.query.YdhzxxRequest;
import com.dxhy.order.model.a9.query.YhzxxResponse;

import java.util.List;

/**
 * 受理点管理业务层
 *
 * @author ZSC-DXHY
 * @date 创建时间: 2020-08-14 13:38
 */
public interface SldManagerServiceA9 {
    
    /**
     * 查询月度汇总信息
     *
     * @param paramSkReqYhzxxcx
     * @return
     */
    YhzxxResponse queryYhzxx(YdhzxxRequest paramSkReqYhzxxcx);
    
    /**
     * 查询发票打印点列表
     *
     * @param dyRequest
     * @return
     */
    DydResponse queryDydxxcxList(DydListRequst dyRequest);
    
    
    /**
     * 百旺盘阵发票汇总查询
     *
     * @param ydtjParam
     * @return
     */
    List<YdtjDto> queryYhzxxBwpz(YdtjParam ydtjParam);
    
    /**
     * 百旺盘阵发票汇总详情查询
     *
     * @param ydtjDetailParam
     * @return
     */
    JSONObject getBbfxDetailBwPz(YdtjDetailParam ydtjDetailParam);
}
