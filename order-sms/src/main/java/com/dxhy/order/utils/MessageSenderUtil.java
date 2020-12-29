package com.dxhy.order.utils;

import cn.hutool.core.thread.ThreadFactoryBuilder;
import com.alibaba.fastjson.JSONObject;
import com.dxhy.order.vo.MailContentVo;
import com.dxhy.order.vo.SendMailVo;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Component;

import javax.activation.DataHandler;
import javax.activation.DataSource;
import javax.activation.FileDataSource;
import javax.annotation.Resource;
import javax.mail.*;
import javax.mail.internet.*;
import java.io.File;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author: wangyang
 * @date: 2020/4/26 11:38
 * @description
 */
@Component
@Slf4j
public class MessageSenderUtil {
    
    /**
     * 邮件模板起始拼接符
     */
    public static final String START_POS = "<(";
    /**
     * 邮件模板结束拼接符
     */
    public static final String END_POS = ")>";
    
    public static final String DEFAULT_ENCODING = "UTF-8";
    
    public static final String MAIL_TEMPLATE_NAME = "mailContent.ftl";
    
    private final ExecutorService executorService = new ThreadPoolExecutor(5, 20,
            0L, TimeUnit.MILLISECONDS,
            new LinkedBlockingQueue<Runnable>(1024), new ThreadFactoryBuilder()
            .setNamePrefix("messageSendPool-%d").build(), new ThreadPoolExecutor.AbortPolicy());
    
    private JavaMailSender mailSender;
    
    private static SendMailVo sendMailVo;
    
    private static final Pattern EMAIL_PATTERN = Pattern.compile("[;,:\"<>]");
    
    @Resource
    private FreeMarkerUtil freeMarkerUtil;
    
    //加载发件人配置信息
    static {
        Map<String, String> map = PropertiesUtil.properties2Map();
        String params = JSONObject.toJSONString(map);
        sendMailVo = JSONObject.parseObject(params, SendMailVo.class);
    }
    
    public JavaMailSender getMailSender() {
        return mailSender;
    }
    
    public void setMailSender(JavaMailSender mailSender) {
        this.mailSender = mailSender;
    }
    
    /**
     * @Description: 将模板中的变量替换成真实数据
     * @Author: wangyang
     * @Date:2020/5/5 11:38
     * @Param:
     * @return:
     */
    public String merge(String template, Map<String, String> model) {
        Set<String> keySet = model.keySet();
        for (String key : keySet) {
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
    
    private boolean createMailConfig() {
        log.info("发件人邮箱配置信息:{}", sendMailVo.toString());
    
        if (sendMailVo == null) {
            log.error("发件人邮件配置信息为空");
            return false;
        }
    
        boolean result = StringUtils.isBlank(sendMailVo.getSendAddress())
                || StringUtils.isBlank(sendMailVo.getSmtpServer())
                || ("true".equals(sendMailVo.getSmtpAuth()) && StringUtils.isBlank(sendMailVo.getAuthPassword()));
        if (result) {
            log.error("发件人邮件配置信息为空或配置有误");
            return false;
        }
    
        JavaMailSenderImpl javaMailSender = new JavaMailSenderImpl();
        javaMailSender.setHost(sendMailVo.getSmtpServer());
        javaMailSender.setUsername(sendMailVo.getSendName());
        javaMailSender.setPassword(sendMailVo.getAuthPassword());
        javaMailSender.setDefaultEncoding(DEFAULT_ENCODING);
        
        //设置Session中的信息,保证邮件认证的一致性
        Properties props = new Properties();
        props.put("mail.smtp.host", sendMailVo.getSmtpServer());
        props.put("mail.smtp.auth", sendMailVo.getSmtpAuth());
        props.put("mail.transport.protocol", "smtp");
        //如果是qq.com启用ssl
        if (sendMailVo.getSmtpServer().toLowerCase().endsWith(".qq.com")) {
            props.put("mail.smtp.starttls.enable", "true");
        }
        
        Authenticator authentic = null;
        if ("true".equals(sendMailVo.getSmtpAuth())) {
            authentic = new MyAuthenticator(sendMailVo.getSendAddress(), sendMailVo.getAuthPassword());
        }
        Session session = Session.getInstance(props, authentic);
        javaMailSender.setSession(session);
        
        this.setMailSender(javaMailSender);
        return true;
    }
    
    private static class MyAuthenticator extends Authenticator {
        String userName = null;
        String password = null;
        
        public MyAuthenticator(String username, String password) {
            this.userName = username;
            this.password = password;
        }
        
        @Override
        protected PasswordAuthentication getPasswordAuthentication() {
            return new PasswordAuthentication(userName, password);
        }
    }
    
    /**
     * @Description: 发送邮件业务放入线程池处理
     * @Author: wangyang
     * @Date:2020/5/5 11:37
     * @Param:
     * @return:
     */
    public void sendMailExecutor(MailContentVo mailContent, SendMailVo sendVo) {
        executorService.execute(new SendMailWork(mailContent, sendVo));
    }
    
    private class SendMailWork implements Runnable {
        
        private final MailContentVo mailContent;
        
        private final SendMailVo paramSendVo;
        
        public SendMailWork(MailContentVo mailContent, SendMailVo paramSendVo) {
            this.mailContent = mailContent;
            this.paramSendVo = paramSendVo;
            
            if (paramSendVo != null && StringUtils.isNotBlank(paramSendVo.getSendAddress())
                    && StringUtils.isNotBlank(paramSendVo.getAuthPassword())
                    && StringUtils.isNotBlank(paramSendVo.getSendName())
                    && StringUtils.isNotBlank(paramSendVo.getSmtpServer())) {
                sendMailVo = paramSendVo;
            }
        }
        
        @Override
        public void run() {
            log.info("线程ID-->{} start...", Thread.currentThread().getId());
            sendMailWithFile(mailContent);
            log.info("线程ID-->{} end...", Thread.currentThread().getId());
        }
    }
    
    /**
     * @Description: 发送带附件的邮件
     * @Author: wangyang
     * @Date:2020/5/5 11:27
     * @Param:
     * @return:
     */
    public void sendMailWithFile(MailContentVo mailContent) {
        //初始化邮件配置信息
        boolean configFlag = createMailConfig();
        if (!configFlag) {
            return;
        }
        //邮件内容或标题动态替换的数据map
        Map<String, String> model = mailContent.getModel();
        
        //动态替换内容模板中的值
        if (freeMarkerUtil == null) {
            freeMarkerUtil = new FreeMarkerUtil();
        }
        
        log.info("线程ID-->{} start replace mail template...", Thread.currentThread().getId());
        String content = mailContent.getContent();
        if (StringUtils.isBlank(content)) {
            String templateName = MAIL_TEMPLATE_NAME;
            if (StringUtils.isNotBlank(mailContent.getTemplateFileName())) {
                templateName = mailContent.getTemplateFileName();
            }
            content = freeMarkerUtil.processTemplate(templateName, model);
        }
        log.info("线程ID-->{} end replace mail template...", Thread.currentThread().getId());
        
        MimeMessage mimeMessage = mailSender.createMimeMessage();
        try {
            Address fromAddress = getFrom(sendMailVo.getSendName(), sendMailVo.getSendAddress());
            //发件人 支持带昵称
            mimeMessage.setFrom(fromAddress);
            MimeMessageHelper messageHelper = new MimeMessageHelper(mimeMessage);
            //收件人
            messageHelper.setTo(mailContent.getTo());
            //抄送人
            if (mailContent.getCc() != null) {
                messageHelper.setCc(mailContent.getCc());
            }
            //邮件标题
            messageHelper.setSubject(mailContent.getSubject());
    
            //内容附件对象
            Multipart multipart = new MimeMultipart();
            //文本内容
            BodyPart txt = new MimeBodyPart();
            //设置内容，格式
            txt.setContent(content, "text/html;charset=utf-8");
            //把文本内容添加到part中
            multipart.addBodyPart(txt);
    
            File[] files = mailContent.getFiles();
            if (files != null && files.length > 0) {
                for (File file : files) {
                    //附件
                    BodyPart addpendix = new MimeBodyPart();
                    //数据源
                    DataSource ds = new FileDataSource(file);
                    //添加附件
                    addpendix.setDataHandler(new DataHandler(ds));
                    //设置附件的名称
                    addpendix.setFileName(MimeUtility.encodeText(ds.getName()));
                    //解决乱码
                    //把附件添加到part中
                    multipart.addBodyPart(addpendix);
                }
            }
            mimeMessage.setContent(multipart);
    
            log.info("线程ID-->{} JavaMailSender send start.....", Thread.currentThread().getId());
            mailSender.send(mimeMessage);
            log.info("线程ID-->{} JavaMailSender send success....", Thread.currentThread().getId());
        } catch (Exception e) {
            log.error("发送邮件异常", e);
        }
    }
    
    /**
     * @Description: 获取含昵称的发件人
     * @Author: wangyang
     * @Date:2020/5/5 11:27
     * @Param:
     * @return:
     */
    private Address getFrom(String nickename, String from) {
        String nick = "";
        try {
            Matcher matcher = EMAIL_PATTERN.matcher(nickename);
            nickename = matcher.replaceAll("").trim();
            nick = javax.mail.internet.MimeUtility.encodeText(nickename, "utf-8", "B");
            return new InternetAddress(nick + " <" + from + ">");
        } catch (Exception e) {
            log.error("昵称处理异常", e);
            return null;
        }
    }
}
