//package com.dxhy.order.service.rabbitmq;
//
//import com.dxhy.order.rabbitplugin.listener.BaseListener;
//import com.dxhy.order.service.IPollinvoiceService;
//import com.dxhy.order.utils.JsonUtils;
//import lombok.extern.slf4j.Slf4j;
//import org.springframework.amqp.core.Message;
//import org.springframework.stereotype.Component;
//
//import javax.annotation.Resource;
//import java.nio.charset.StandardCharsets;
//
///**
// * 延时队列开票业务监听
// *
// * @author ZSC-DXHY
// */
//@Component
//@Slf4j
//public class InvoiceYsdlHandler implements BaseListener {
//    private final static String LOGGER_MSG = "(延时队列开票业务监听)";
//
//    @Resource
//    private IPollinvoiceService iPollinvoiceService;
//
//    /**
//     * 监听数据
//     *
//     * @param message
//     */
//    @Override
//    public void onMessage(Message message) {
//        log.debug("{}监听到数据,开始处理,处理内容为:{}", LOGGER_MSG, JsonUtils.getInstance().toJsonString(message));
//        try {
//            byte[] messageBody = message.getBody();
//            String messageData = new String(messageBody, StandardCharsets.UTF_8);
//            log.info("{}解析处理内容为:{}", LOGGER_MSG, messageData);
//            reverse(messageData);
//            log.info("{}业务处理完成", LOGGER_MSG);
//        } catch (Exception e) {
//            log.error("{}获取mq队列数据错误:{}", LOGGER_MSG, e);
//        }
//    }
//
//    /**
//     * 真正的业务处理
//     *
//     * @param message
//     */
//    public void reverse(String message) {
//
//        // TODO: 2019/9/3 暂时复用发票开具功能,后期优化该部分
//        message = JsonUtils.getInstance().parseObject(message, String.class);
//        log.info("{}发票开具数据队列消息数据:{}", LOGGER_MSG, message);
//        iPollinvoiceService.pollInvoice(message);
//
//    }
//}
