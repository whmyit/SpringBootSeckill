package com.dxhy.order.ordermail.controller;

import com.dxhy.order.ordermail.constant.OrderInfoContentEnum;
import com.dxhy.order.ordermail.model.*;
import com.dxhy.order.ordermail.util.*;
import freemarker.template.TemplateException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@Controller
@Slf4j
public class MailController {

    private String sucCode = "0000";

    private String failCode = "9999";

    @Value("${mail.senderName}")
    private String senderName;

    @RequestMapping("/email/send")
    @ResponseBody
    public Map<String,Object> invoiceSave(HttpServletRequest request,String sendContent)  {

        EmailBody emailBody = JsonUtils.getInstance().parseObject(sendContent, EmailBody.class);
        String requestResult = Base64Encoding.decodeToString(emailBody.getContent());
        log.info("发送邮件数据体:{}", requestResult);
        EmailContent emailContent1 = JsonUtils.getInstance().parseObject(requestResult, EmailContent.class);
        emailContent1.setSenderName(senderName);
        SendMailUtil sendMailUtil = new SendMailUtil();
        try {
            Map map = sendMailUtil.sendMail(emailContent1);
            if(map.get("code").equals("9999")){
                return getFailRtn("邮件发送失败:"+map.get("msg"));
            }
            return getSussRtn("", "发送邮件请求成功");
        } catch (Exception e) {
            e.printStackTrace();
            return getFailRtn("邮件发送失败:"+e.getMessage());
        }
    }

    @RequestMapping("/emailSend")
    @ResponseBody
    public EmailSendResponse emailSend(@RequestBody String sendContent)  {
        EmailBody emailBody = JsonUtils.getInstance().parseObject(sendContent, EmailBody.class);
        String requestResult = Base64Encoding.decodeToString(emailBody.getContent());
        log.info("发送邮件数据体:{}", requestResult);
        EmailContent emailContent1 = JsonUtils.getInstance().parseObject(requestResult, EmailContent.class);
        emailContent1.setSenderName(senderName);
        SendMailUtil sendMailUtil = new SendMailUtil();
        try {
            Map<String,String> map = sendMailUtil.sendMail(emailContent1);
            log.info("发送邮件over，结果:{}", JsonUtils.getInstance().toJsonString(map));
            return getReturn(map.get("code"),map.get("msg"));
        } catch (Exception e) {
            log.info("发送邮件异常:{}", e);
            return getReturn("9999","邮件发送异常");
        }
    }

    private EmailSendResponse getReturn(String code, String msg) {
        EmailSendResponse esr = new EmailSendResponse();
        ReturnStateInfo rsi = new ReturnStateInfo();
        rsi.setReturnCode(code);
        rsi.setReturnMessage(msg);
        esr.setReturnStateInfo(rsi);
        return esr;
    }

    protected Map<String, Object> getFailRtn(String msg) {
        Map<String, Object> rtn = new HashMap<String, Object>();
        rtn.put("code", failCode);
        rtn.put("msg", msg);
        rtn.put("data", null);
        return rtn;
    }

    /**
     * 获取成功的返回内容
     *
     * @param data
     * @author chenrui
     * @return
     */
    protected Map<String, Object> getSussRtn(Object data, String msg) {
        Map<String, Object> rtn = new HashMap<String, Object>();
        rtn.put("code", sucCode);
        rtn.put("msg", msg);
        rtn.put("data", data);
        return rtn;
    }


    /**
     * 设置邮件体
     *
     * @param content
     * @return
     * @throws Exception
     */
    private static String setEmailBody(EmailContent content) throws Exception {
        //设置邮件体参数
        String serialNumber = "000000000011" + String.valueOf(System.currentTimeMillis());
//        content.setSerialNum(serialNumber);
        EmailBody globalInfo = new EmailBody();
        globalInfo.setZipCode("0");
        globalInfo.setEncryptCode("0");
        globalInfo.setDataExchangeId(serialNumber);
        globalInfo.setContent(Base64Encoding.encode(DataHandleUtil.json(content)));
        String json = DataHandleUtil.json(globalInfo);
        log.info("组件的数据体json:{}", json);
        return json;
    }

}
