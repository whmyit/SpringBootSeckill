package com.dxhy.order.consumer.modules.sms;

import com.dxhy.order.consumer.annotation.SysLog;
import com.dxhy.order.model.R;
import com.dxhy.order.utils.HutoolMailSendUtil;
import com.dxhy.order.utils.MessageSenderUtil;
import com.dxhy.order.vo.HutoolSendMailVo;
import com.dxhy.order.vo.MailContentVo;
import com.dxhy.order.vo.SendMailVo;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * @author: wangyang
 * @date: 2020/5/5 14:09
 * @description
 */
@RestController
@Slf4j
@Api(value = "邮箱交付", tags = {"订单模块"})
@RequestMapping(value = "/mail")
public class MailController {
    private static final String LOGGER_MSG = "(邮件发送控制层)";
    
    private static final SimpleDateFormat SIMPLE_DATE_FORMAT = new SimpleDateFormat("yyyy年MM月dd日");
    
    @Resource
    private MessageSenderUtil messageSenderUtil;
    
    @Resource
    private HutoolMailSendUtil hutoolMailSendUtil;
    
    @ApiOperation(value = "邮件发送", notes = "邮件交付管理-邮件发送")
    @PostMapping("/sendMail")
    @SysLog(operation = "邮件发送", operationDesc = "邮件发送", key = "邮件交付管理")
    public R sendMail() {
        try {
            //发送邮件 方式一 javaMail实现
            messageSenderUtil.sendMailExecutor(getTestContentVo(), getTestConfig());
            //发送邮件 方式二 Hutool工具实现
            hutoolMailSendUtil.sendMailExecutor(getTestContentVo(), getTestHutoolConfig());
        } catch (Exception e) {
            log.error("邮件发送异常", e);
            return R.error();
        }
        return R.ok();
    }
    
    public SendMailVo getTestConfig() {
        SendMailVo sendMailVo = new SendMailVo();
        sendMailVo.setSendAddress("fangyibai@163.com");
        sendMailVo.setSmtpServer("smtp.163.com");
        sendMailVo.setAuthPassword("SJHEUAFFNDBNZLMY");
        sendMailVo.setSendName("lucy");
        return sendMailVo;
    }
    
    public HutoolSendMailVo getTestHutoolConfig() {
        HutoolSendMailVo sendMailVo = new HutoolSendMailVo();
        sendMailVo.setFrom("wangyang_727105@163.com");
        sendMailVo.setHost("smtp.163.com");
        sendMailVo.setPass("OSIVEYRACGUZZTVZ");
        return sendMailVo;
    }
    
    public Map<String, String> getTestData() {
        Map<String, String> data = new HashMap<>(5);
        data.put("ddrq", SIMPLE_DATE_FORMAT.format(new Date()));
        data.put("fpdm", "150000020026");
        data.put("fphm", "14536797");
        data.put("kprq", SIMPLE_DATE_FORMAT.format(new Date()));
        data.put("gfmc", "测试购方名称");
        data.put("kphjje", "1234.32");
        data.put("邮件标题", "14536797");
        
        return data;
    }
    
    public MailContentVo getTestContentVo() {
        MailContentVo contentVo = new MailContentVo();
        Map<String, String> data = getTestData();
        contentVo.setModel(data);
        
        String subject = "【电子发票】您收到一张新的电子发票[发票号码：<(邮件标题)>]";
        subject = messageSenderUtil.merge(subject, data);
        contentVo.setSubject(subject);
        
        String[] toMail = new String[1];
        toMail[0] = "wangyang@ele-cloud.com";
        contentVo.setTo(toMail);
        
        String[] csMail = new String[1];
        csMail[0] = "741248012@qq.com";
        contentVo.setCc(csMail);
        
        File[] files = new File[2];
        files[0] = new File("C:\\wk_doc\\5000201530-15683635.pdf");
        files[1] = new File("C:\\wk_doc\\5000201530-15683636.pdf");
        contentVo.setFiles(files);
        return contentVo;
    }
    
}
