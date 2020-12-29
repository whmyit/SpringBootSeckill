package com.dxhy.order.service.rabbitmq;

import com.dxhy.order.api.ApiEmailService;
import com.dxhy.order.api.ApiPushService;
import com.dxhy.order.rabbitplugin.listener.BaseListener;
import com.dxhy.order.utils.JsonUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.Message;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.nio.charset.StandardCharsets;

/**
 * 作废推送业务监听
 *
 * @author ZSC-DXHY
 */
@Component
@Slf4j
public class InvoiceZftsHandler implements BaseListener {
    private final static String LOGGER_MSG = "(作废推送业务监听)";
    
/*    @Resource
    private ApiEmailService apiEmailService;*/

    @Resource
    private ApiPushService apiPushService;
    /**
     * 监听数据
     *
     * @param message
     */
    @Override
    public void onMessage(Message message) {
        log.debug("{}监听到数据,开始处理,处理内容为:{}", LOGGER_MSG, JsonUtils.getInstance().toJsonString(message));
        try {
            byte[] messageBody = message.getBody();
            String messageData = new String(messageBody, StandardCharsets.UTF_8);
            log.info("{}解析处理内容为:{}", LOGGER_MSG, messageData);
            reverse(messageData);
            log.info("{}业务处理完成", LOGGER_MSG);
        } catch (Exception e) {
            log.error("{}获取mq队列数据错误:{}", LOGGER_MSG, e);
        }
    }
    
    /**
     * 真正的业务处理
     *
     * @param message
     */
    public void reverse(String message) {
        message = JsonUtils.getInstance().parseObject(message, String.class);
        log.info("{}发票数据推送队列消息数据:{}", LOGGER_MSG, message);
        apiPushService.pushInvoiceInvalidRouting(message);
    }
}
