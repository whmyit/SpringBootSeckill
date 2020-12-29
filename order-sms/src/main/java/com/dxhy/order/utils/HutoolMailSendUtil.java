package com.dxhy.order.utils;

import cn.hutool.core.thread.ThreadFactoryBuilder;
import cn.hutool.extra.mail.MailAccount;
import cn.hutool.extra.mail.MailUtil;
import com.dxhy.order.vo.HutoolSendMailVo;
import com.dxhy.order.vo.MailContentVo;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.io.File;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * @author: wangyang
 * @date: 2020/5/7 9:30
 * @description
 */
@Component
@Slf4j
public class HutoolMailSendUtil {
    
    public static SimpleDateFormat sdf = new SimpleDateFormat("yyyy年MM月dd日");
    
    public static final String START_PREFIX = "<";
    
    public static final String END_PREFIX = "@";
    
    /**
     * 邮件模板起始拼接符
     */
    public static final String START_POS = "<(";
    /**
     * 邮件模板结束拼接符
     */
    public static final String END_POS = ")>";
    
    public static final String MAIL_TEMPLATE_NAME = "mailContent.ftl";
    
    private final ExecutorService executorService = new ThreadPoolExecutor(5, 20,
            0L, TimeUnit.MILLISECONDS,
            new LinkedBlockingQueue<Runnable>(1024), new ThreadFactoryBuilder()
            .setNamePrefix("messageSendPool-%d").build(), new ThreadPoolExecutor.AbortPolicy());
    
    @Resource
    private FreeMarkerUtil freeMarkerUtil;
    
    public List<String> array2List(String[] objArray) {
        if (objArray == null) {
            return null;
        }
        return Arrays.asList(objArray);
    }

    public static String merge(String template, Map<String, String> model){
        Set<String> keySet = model.keySet();
        for(String key : keySet){
            if (template.contains(START_POS) && template.contains(END_POS)) {
                String templateKey = START_POS + key + END_POS;
                if (template.contains(templateKey)) {
                    String value = "";
                    try {
                        if (model.containsKey(key)) {
                            value = model.get(key);
                        }
                        template = template.replace(templateKey, value);
                
                    } catch (Exception e) {
                        log.error("模板替换数据异常", e);
                    }
                }
            }
        }
        return template;
    }

    /**
    * @Description: 线程池处理邮件发送服务
    * @Author: wangyang
    * @Date:2020/5/8 11:09
    * @Param:
    * @return:
    */
    public void sendMailExecutor(MailContentVo mailContent, HutoolSendMailVo hutoolSendMailVo){
        executorService.execute(new SendMailWork(mailContent, hutoolSendMailVo));
    }

    private class SendMailWork implements Runnable{
    
        private final MailContentVo mailContent;
    
        private final HutoolSendMailVo hutoolSendMailVo;

        public SendMailWork(MailContentVo mailContent, HutoolSendMailVo hutoolSendMailVo) {
            this.mailContent = mailContent;
            this.hutoolSendMailVo = hutoolSendMailVo;
        }

        @Override
        public void run() {
            try{
                log.info("线程ID-->{} start replace mail template...", Thread.currentThread().getId());
                String content = mailContent.getContent();
                if(StringUtils.isBlank(content)){
                    String templateName = MAIL_TEMPLATE_NAME;
                    if(StringUtils.isNotBlank(mailContent.getTemplateFileName())){
                        templateName = mailContent.getTemplateFileName();
                    }
                    content = freeMarkerUtil.processTemplate(templateName,mailContent.getModel());
                }
                log.info("线程ID-->{} end replace mail template...", Thread.currentThread().getId());

                Long startTime = System.currentTimeMillis();
                log.info("线程ID-->{} mail send start",Thread.currentThread().getId());
                if(hutoolSendMailVo != null){
                    MailUtil.send(getMailAccount(hutoolSendMailVo),array2List(mailContent.getTo()) ,
                            array2List(mailContent.getCc()),null,
                            mailContent.getSubject(),content,true,mailContent.getFiles());
                }else{
                    MailUtil.send(array2List(mailContent.getTo()) ,
                            array2List(mailContent.getCc()),null,
                            mailContent.getSubject(),content,true,mailContent.getFiles());
                }
                Long endTime = System.currentTimeMillis();
                log.info("线程ID-->{} mail send end,耗时：{}秒",Thread.currentThread().getId(),(endTime - startTime)/1000);
            }catch (Exception e){
                log.error("发送邮件异常", e);
            }finally {
                //邮件发送成功后，删除临时文件
                File[] fileList = mailContent.getFiles();
                if(fileList != null && fileList.length > 0){
                    for(File tempFile : fileList){
                        if(tempFile.exists()){
                            tempFile.delete();
                        }
                    }
                }
            }
        }
    }

    /**
    * @Description: 处理自定义邮件配置信息
    * @Author: wangyang
    * @Date:2020/5/8 11:08
    * @Param:
    * @return:
    */
    public MailAccount getMailAccount(HutoolSendMailVo sendMailVo){
        MailAccount mailAccount = new MailAccount();

        String user = "";
        String from = sendMailVo.getFrom();
        int startIndex = from.indexOf(START_PREFIX);
        int endIndex = from.lastIndexOf(END_PREFIX);
        if(startIndex > 0){
            user = from.substring(startIndex + 1, endIndex);
        }else{
            user = from.substring(0,endIndex);
        }
        sendMailVo.setUser(user);

        BeanUtils.copyProperties(sendMailVo,mailAccount);
        return mailAccount;
    }

    public static HutoolSendMailVo getTestSendConfig(){
        HutoolSendMailVo sendMailVo = new HutoolSendMailVo();
        sendMailVo.setFrom("wangyang_727105@163.com");
        sendMailVo.setHost("smtp.163.com");
        sendMailVo.setPass("OSIVEYRACGUZZTVZ");
        return sendMailVo;
    }

    public static Map<String,String> getTestData(){
        Map<String, String> data = new HashMap<>(5);
        data.put("ddrq",sdf.format(new Date()));
        data.put("fpdm","150000020026");
        data.put("fphm","14536797");
        data.put("kprq",sdf.format(new Date()));
        data.put("gfmc","测试购方名称");
        data.put("kphjje","1234.32");
        data.put("邮件标题","14536797");

        return data;
    }

    public static MailContentVo getTestContentVo(){
        MailContentVo contentVo = new MailContentVo();
        Map<String,String> data = getTestData();
        contentVo.setModel(data);

        String subject = "【电子发票】您收到一张新的电子发票[发票号码：<(邮件标题)>]";
        subject = merge(subject,data);
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

    public static void main(String[] args) {
        HutoolMailSendUtil hutoolMailSendUtil = new HutoolMailSendUtil();
        hutoolMailSendUtil.sendMailExecutor(getTestContentVo(),getTestSendConfig());
    }
}
