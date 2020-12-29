package com.dxhy.order.consumer.modules.invoice.service.impl;

import com.dxhy.order.api.ApiInvoiceCommonService;
import com.dxhy.order.api.ApiYpWarningService;
import com.dxhy.order.api.SalerWarningService;
import com.dxhy.order.constant.Constant;
import com.dxhy.order.constant.OrderInfoEnum;
import com.dxhy.order.consumer.modules.invoice.service.InvoiceReciveService;
import com.dxhy.order.consumer.modules.itaxmsg.service.IItaxMsgService;
import com.dxhy.order.model.InvoicePush;
import com.dxhy.order.model.SalerWarning;
import com.dxhy.order.utils.JsonUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.dubbo.config.annotation.Reference;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;
import java.util.Objects;

/**
 * 纸票领用实现类
 *
 * @author Dear
 */
@Slf4j
@Service
public class InvoiceReciveServiceImpl implements InvoiceReciveService {

    private final static String LOGGER_MSG = "纸票领用Service实现类";
    
    @Reference
	private ApiYpWarningService ypWarningService;
    
    @Resource
    private IItaxMsgService IItaxMsgService;
    @Reference
    private SalerWarningService salerWarningService;
    @Reference
    private ApiInvoiceCommonService apiInvoiceCommonService;

    @Override
    public void pushExceptionMessageToItax(InvoicePush invoicePush) {
	    if(Objects.nonNull(invoicePush)){
	        log.info("异常订单信息推动大B开始...");
	        String statusCode = invoicePush.getSTATUSCODE();
	        if(StringUtils.isNotBlank(statusCode)){
                //判断状态代码（STATUSCODE）
                boolean pushStatusCode =
                        //待调税控
                        StringUtils.equals(OrderInfoEnum.PUSH_INVOICE_STATUS_1001.getKey(),statusCode)
                        //赋码失败
                        || StringUtils.equals(OrderInfoEnum.PUSH_INVOICE_STATUS_2101.getKey(),statusCode)
                        //签章失败
                        || StringUtils.equals(OrderInfoEnum.PUSH_INVOICE_STATUS_2001.getKey(),statusCode);
                if(pushStatusCode){
                    //纳税人识别号
                    String nsrsbh = invoicePush.getNSRSBH();
    
                    //根据纳税人识别号查询发票预警表，获取预警开关状态
                    List<SalerWarning> salerWarningList = salerWarningService.selectSalerWaringByNsrsbh(nsrsbh, null);
                    if (salerWarningList != null && salerWarningList.size() > 0) {
                        for (SalerWarning salerWarning : salerWarningList) {
    
                            log.info("异常订单信息推动大B-查询预警信息结果:{}", JsonUtils.getInstance().toJsonString(salerWarning));
                            if (Objects.nonNull(salerWarning) && StringUtils.equals(OrderInfoEnum.ORDER_WARNING_OPEN.getKey(),
                                    salerWarning.getWarningFlag())) {
                                String errorMsg = String.format(Constant.ERROR_MESSAGE_ORDER, nsrsbh, invoicePush.getFPQQLSH(), invoicePush.getDDH(), invoicePush.getSTATUSMSG());
    
                                log.info("异常订单信息推动大B-消息主体:{}", errorMsg);
                                IItaxMsgService.sessMessageToTax("异常订单预警", errorMsg, "2",
                                        salerWarning.getCreateId(), salerWarning.getDeptId());
                            }
                        }
                    }
                }
            }else{
                log.error("异常订单信息推动大B:状态代码为空!");
            }
            log.info("异常订单信息推动大B结束...");
        }else{
	        log.error("推送信息为空!");
        }
    }

 
}
