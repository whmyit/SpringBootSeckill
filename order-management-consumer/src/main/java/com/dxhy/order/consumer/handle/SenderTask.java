package com.dxhy.order.consumer.handle;

import com.dxhy.order.api.ApiFpExpressService;
import com.dxhy.order.consumer.modules.manager.controller.FpExpressController;
import com.dxhy.order.model.dto.KdniaoQueryReq;
import com.dxhy.order.model.entity.FpExpress;
import com.xxl.job.core.biz.model.ReturnT;
import com.xxl.job.core.handler.IJobHandler;
import com.xxl.job.core.handler.annotation.JobHandler;
import lombok.extern.slf4j.Slf4j;
import org.apache.dubbo.config.annotation.Reference;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.List;

/**
 * 发票邮寄定时任务
 *
 * @author ZSC-DXHY
 * @date 创建时间: 2020-08-14 15:35
 */
@Component
@JobHandler(value = "/senderTask")
@Slf4j
public class SenderTask extends IJobHandler {

    @Reference
    private ApiFpExpressService fpExpressService;
    @Resource
    private FpExpressController fpExpressController;
    @Override
    public ReturnT<String> execute(String s) throws Exception {
        log.info("定时任务开始执行");
        this.getFpExpressList();
        return ReturnT.SUCCESS;
    }
    
    private  List<FpExpress> getFpExpressList() {
        //获取快递单号/订单号/快递公司编码列表
        log.info("查询未签收的快递单号/订单号/快递公司编码列表");
        List<FpExpress> numList = fpExpressService.queryWqs();
        int k = numList.size();
        log.info("获取的订单数量为: {} 条", k);
        //查询的时候是否需要清空数据库表
        for (int i=0;i<=numList.size();i++) {
            KdniaoQueryReq req = new KdniaoQueryReq();
            //快递公司编码
            req.setExpCode(numList.get(i).getExpressCompanyCode());
            //快递单号
            req.setExpNo(numList.get(i).getExpressNumber());
            //订单号
            req.setOrderCode(numList.get(i).getId());
            fpExpressController.query(req);
        }
        return null;
    }
}
