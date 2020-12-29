package com.dxhy.order.service.rabbitmq;

import java.nio.charset.StandardCharsets;

import org.springframework.amqp.core.Message;
import org.springframework.stereotype.Component;

import com.dxhy.order.api.ApiInsertCardService;
import com.dxhy.order.rabbitplugin.listener.BaseListener;
import com.dxhy.order.utils.JsonUtils;

import lombok.extern.slf4j.Slf4j;

import javax.annotation.Resource;

/**
 * @author ：杨士勇
 * @ClassName ：InvoiceInsertCardHander
 * @Description ：插卡队列数据处理
 * @date ：2020年4月8日 下午4:05:33
 */
@Component
@Slf4j
public class InvoiceInsertCardHander implements BaseListener {
	
    private final static String LOGGER_MSG = "(插卡队列监听)";
    
    @Resource
    ApiInsertCardService apiInsertCardService;
    
    
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
        apiInsertCardService.insertCard(message);
    
    }

}
