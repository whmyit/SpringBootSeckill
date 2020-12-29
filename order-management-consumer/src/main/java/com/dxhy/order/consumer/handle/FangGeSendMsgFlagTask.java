package com.dxhy.order.consumer.handle;

import cn.hutool.core.date.DateUtil;
import com.dxhy.order.api.ApiFangGeInterfaceService;
import com.xxl.job.core.biz.model.ReturnT;
import com.xxl.job.core.handler.IJobHandler;
import com.xxl.job.core.handler.annotation.JobHandler;
import lombok.extern.slf4j.Slf4j;
import org.apache.dubbo.config.annotation.Reference;
import org.springframework.stereotype.Component;

import java.util.Calendar;
import java.util.Date;

/**
 * 方格开票中的数据重新发送消息
 *
 * @author ZSC-DXHY
 * @date 创建时间: 2020-08-14 14:50
 */
@Slf4j
@JobHandler(value = "fangGeSendMsgFlagTask")
@Component
public class FangGeSendMsgFlagTask extends IJobHandler {
    
    private static final String LOGGER_MSG = "(方格发送消息状态任务)";
    @Reference
    private ApiFangGeInterfaceService apiFangGeInterfaceService;
    
    @Override
    public ReturnT<String> execute(String param) {
        try {
            log.info("========>{}定时任务开始！！！！", LOGGER_MSG);
            apiFangGeInterfaceService.handlerIsSendFlag();
        } catch (Exception e) {
            log.error("{}抛出异常：{}", LOGGER_MSG, e);
        }
        return SUCCESS;
    }
    
    public static void main(String[] args) {
        Calendar beforeTime = Calendar.getInstance();
        int minute = Integer.parseInt("5");
        // minute分钟之前的时间
        beforeTime.add(Calendar.MINUTE, -minute);
        Date date = beforeTime.getTime();
        String dateStr = DateUtil.format(date, "yyyy-MM-dd HH:mm:ss");
        System.out.println(dateStr);
    }
    
    
}
