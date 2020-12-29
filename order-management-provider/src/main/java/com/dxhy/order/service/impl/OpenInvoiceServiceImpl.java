package com.dxhy.order.service.impl;

import com.dxhy.order.api.OpenInvoiceService;
import com.dxhy.order.constant.NsrQueueEnum;
import com.dxhy.order.constant.OrderInfoContentEnum;
import com.dxhy.order.constant.OrderManagementConstant;
import com.dxhy.order.model.R;
import com.dxhy.order.service.IRabbitMqSendMessage;
import lombok.extern.slf4j.Slf4j;
import org.apache.dubbo.config.annotation.Service;

import javax.annotation.Resource;

/**
 *
 * @ClassName ：OpenInvoiceServiceImpl
 * @Description ：发票开具
 * @author ：杨士勇
 * @date ：2019年3月11日 下午4:16:16
 *
 *
 */
@Service
@Slf4j
public class OpenInvoiceServiceImpl implements OpenInvoiceService {
    
    @Resource
    private IRabbitMqSendMessage iRabbitMqSendMessage;
    
    @Override
    public R openAnInvoice(String content, String nsrsbh) {
        log.info("发票开具放入rabbitMq中的数据:contetn:{}", content);
        try {
            iRabbitMqSendMessage.autoSendRabbitMqMessage(nsrsbh, NsrQueueEnum.FPKJ_MESSAGE.getValue(), content);
            return R.ok().put(OrderManagementConstant.CODE, OrderInfoContentEnum.SUCCESS.getKey())
                    .put(OrderManagementConstant.MESSAGE, OrderInfoContentEnum.SUCCESS.getMessage());
        } catch (Exception e) {
            log.error("发票接收请求放入对列失败,异常信息，e:{}", e);
            return R.error().put(OrderManagementConstant.CODE, OrderInfoContentEnum.RECEIVE_FAILD.getKey())
                    .put(OrderManagementConstant.MESSAGE, OrderInfoContentEnum.RECEIVE_FAILD.getMessage());
        }
	}

}
