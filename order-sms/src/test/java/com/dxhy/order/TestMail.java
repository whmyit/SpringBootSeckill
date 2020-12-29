package com.dxhy.order;

import com.dxhy.order.utils.MessageSenderUtil;
import com.dxhy.order.vo.MailContentVo;
import com.dxhy.order.vo.SendMailVo;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * @author: wangyang
 * @date: 2020/4/26 14:36
 * @description
 */
public class TestMail {

    private static final SimpleDateFormat sdf = new SimpleDateFormat("yyyy年MM月dd日");

    private void sendMail(Map<String,String> model,String subject){
        MessageSenderUtil messageSenderUtil = new MessageSenderUtil();
        MailContentVo contentVo = new MailContentVo();
        String[] toMail = new String[1];
        toMail[0] = "wangyang@ele-cloud.com";

        String[] csMail = new String[1];
        csMail[0] = "741248012@qq.com";

        subject = messageSenderUtil.merge(subject,model);
        contentVo.setSubject(subject);
        contentVo.setTo(toMail);
        contentVo.setCc(csMail);

        File[] files = new File[2];
        files[0] = new File("C:\\wk_doc\\5000201530-15683635.pdf");
        files[1] = new File("C:\\wk_doc\\5000201530-15683636.pdf");
        contentVo.setFiles(files);
        contentVo.setModel(model);

        SendMailVo sendMailVo = new SendMailVo();
        sendMailVo.setSendAddress("wangyang_727105@163.com");
        sendMailVo.setSmtpServer("smtp.163.com");
        sendMailVo.setAuthPassword("OSIVEYRACGUZZTVZ");
        sendMailVo.setSendName("张三");

        messageSenderUtil.sendMailExecutor(contentVo, sendMailVo);
    }

    public static void main(String[] args) {
        Map<String, String> data = new HashMap<>(5);
        data.put("ddrq",sdf.format(new Date()));
        data.put("fpdm","150000020026");
        data.put("fphm","14536797");
        data.put("kprq",sdf.format(new Date()));
        data.put("gfmc","测试购方名称");
        data.put("kphjje","1234.32");
        data.put("邮件标题","14536797");

        String subject = "【电子发票】您收到一张新的电子发票[发票号码：<(邮件标题)>]";

        TestMail testMail = new TestMail();
        testMail.sendMail(data,subject);

       /* Map<String,String> map = PropertiesUtil.properties2Map();
        String params = JSONObject.toJSONString(map);
        SendMailVo sendMailVo = JSONObject.parseObject(params,SendMailVo.class);
        System.out.println(sendMailVo.toString());*/
    }
}
