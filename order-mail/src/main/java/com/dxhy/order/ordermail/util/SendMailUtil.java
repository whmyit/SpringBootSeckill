package com.dxhy.order.ordermail.util;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.dxhy.order.ordermail.model.Attachments;
import com.dxhy.order.ordermail.model.EmailContent;
import com.dxhy.order.ordermail.model.EmailSendRSP;
import com.sun.mail.util.MailSSLSocketFactory;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.MapUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.util.StringUtils;

import javax.activation.DataHandler;
import javax.activation.DataSource;
import javax.activation.FileDataSource;
import javax.mail.*;
import javax.mail.internet.*;
import java.io.*;
import java.util.*;

/**
 * @ClassName SendMailUtil
 * @Description TODO
 * @Author wangruwei
 * @Date 2020-08-29 19:06
 * @Version 1.0
 */
@Slf4j
public class SendMailUtil {

    private final static String messageType = "text/html;charset=UTF-8";//相应内容类型，编码类型

    private String sucCode = "0000";

    private String failCode = "9999";

    /**
     *
     * @param ifNeedAccount	是否需要传账号密码，false不传，true传
     * @param fromEmail	发送人邮箱地址，根据ifNeedAccount来决定是否可以为空
     * @param password	发送人邮箱密码址，根据ifNeedAccount来决定是否可以为空
     * @param emailTitle 邮件标题
     * @param content	邮件内容
     * @param senderName	发送人显示名称
     * @param contactEmails	收件人地址
     * @param ccEmails	抄送人地址
     * @param filesList	附件
     * @param pics
     * @return
     * TODO
     * ApiSendEmailServiceImpl.java
     * author wangruwei
     * 2019年6月28日
     * @throws Exception
     */
    private Map<String,String> sendEmail(boolean ifNeedAccount
            , String fromEmail
            , String password
            , String emailTitle
            , String content
            , String senderName
            , String[] contactEmails
            , String[] ccEmails
            , List<File> filesList
            , String[] pics) throws Exception {
        try {
            InputStream io = null;
            Properties properties = new Properties();
//			io = EmailConfigConstant.class.getResourceAsStream("/mail.properties");
            io = SendMailUtil.class.getResourceAsStream("/mail.properties");
            properties.load(io);
            //如果是true，说明要用他自己传的账号密码来发邮件
            if(!ifNeedAccount){
                String addressPass = properties.getProperty("addressPass");
                Map<String, String> randomEmail = RandomUtils.randomEmail(addressPass);
                if(MapUtils.isEmpty(randomEmail)){
                    log.info("mail系统中mail.properties没有配置发件人");
                }else{
                    fromEmail = randomEmail.get("fromEmail");
                    password = randomEmail.get("password");
                }
            }
            log.info("发送邮件：发件人地址{},发件人密码{},收件人地址{}",fromEmail,password,contactEmails);
            if(!EmailUtils.validateEmail(fromEmail)){
                return getFailRtn("发件人邮箱格式不正确");
            }
            //根据邮箱地址判断邮箱服务器是哪个
            String mailSource = fromEmail.substring(fromEmail.lastIndexOf("@") + 1);
            mailSource = mailSource.substring(0, mailSource.indexOf("."));
//            String mailSource = fromEmail.substring(fromEmail.lastIndexOf("@")+1,fromEmail.lastIndexOf("."));;

            String smtpHost = properties.getProperty(mailSource+".smtphost");
            if(null==smtpHost||"".equals(smtpHost)||"null".equals(smtpHost)){
                return getFailRtn("未设置此邮箱服务器配置");
            }
            String hostPort = properties.getProperty(mailSource+".hostport");
            //String auth = properties.getProperty(mailSource+".auth");
            String smtpName = properties.getProperty(mailSource+".smtpname");

            //第一步：配置javax.mail.Session对象
            Properties props = new Properties();   // 创建Properties 类用于记录邮箱的一些属性
            //***************************************** 使用ssl加密 *******************************/
/*            final String SSL_FACTORY = "javax.net.ssl.SSLSocketFactory";//SSL加密
            MailSSLSocketFactory sf = new MailSSLSocketFactory();
            sf.setTrustAllHosts(true);
            props.setProperty("mail.smtp.socketFactory.class", SSL_FACTORY);
            props.setProperty("mail.smtp.socketFactory.fallback", "false");
            props.setProperty("mail.smtp.socketFactory.port", "465");
            props.put("mail.smtp.ssl.socketFactory", sf);*/
            //***************************************** 使用TLS加密 *******************************/
            //props.setProperty("mail.smtp.starttls.enable", "true");//使用 STARTTLS安全连接

            //props.put("mail.smtp.ssl.enable", "true");
            props.setProperty("mail.smtp.host", smtpHost);  //此处填写SMTP服务器
            props.setProperty("mail.smtp.port",hostPort);             //ssl加密使用465，不加密默认使用25
            props.put("mail.smtp.auth", true);       // 表示SMTP发送邮件，必须进行身份验证
            props.put("mail.debug", "true");      //开启调试模式
            props.put("mail.transport.protocol",smtpName);     // 发送邮件协议名称 ssl加密使用smtps，不加密默认使用smtp

            Session mailSession = Session.getInstance(props, new MyAuthenticator(fromEmail, password));//此处填写你的账号和口令
            //第二步：编写消息
            //InternetAddress toAddress = new InternetAddress(to); // 设置收件人的邮箱
            MimeMessage message = new MimeMessage(mailSession);
            //防止成为垃圾邮件，披上outlook的马甲
            message.addHeader("X-Mailer","Microsoft Outlook Express 6.00.2900.2869");
            if(StringUtils.isEmpty(senderName)){
                senderName = fromEmail;
            }
            message.setFrom(new InternetAddress(fromEmail, senderName, "UTF-8"));
            //因为有的邮箱是把主题作为显示位的，所以主题跟标题弄成一样的。
            message.setSubject(emailTitle);
            int index = contactEmails.length;
            InternetAddress[] sendTo = new InternetAddress[index];
            for (int i = 0 ;i < index; i++) {
                if(!StringUtils.isEmpty(contactEmails[i])){
                    sendTo[i] = new InternetAddress(contactEmails[i]);
                }
            }
            //message.setRecipient(RecipientType.TO, new String[moreUsers.size()]);
            message.setRecipients(Message.RecipientType.TO, sendTo);

            //抄送人
            int ccindex = ccEmails.length;
            InternetAddress[] ccTo = new InternetAddress[ccindex];
            for (int i = 0; i < ccindex; i++) {
                if(!StringUtils.isEmpty(ccEmails[i])){
                    ccTo[i] = new InternetAddress(ccEmails[i]);
                }
            }
            if(ccTo.length>0){
                message.setRecipients(Message.RecipientType.CC, ccTo);
            }

            // 向multipart对象中添加邮件的各个部分内容，包括文本内容和附件
            Multipart multipart = new MimeMultipart();
            // 添加邮件正文
            BodyPart contentPart = new MimeBodyPart();
            contentPart.setContent(content, messageType);
            multipart.addBodyPart(contentPart);
            if (filesList != null&&filesList.size()>0) {
                for (File attachment:filesList) {
                    BodyPart attachmentBodyPart = new MimeBodyPart();
                    DataSource source = new FileDataSource(attachment);
                    attachmentBodyPart.setDataHandler(new DataHandler(source));

                    // 网上流传的解决文件名乱码的方法，其实用MimeUtility.encodeWord就可以很方便的搞定
                    // 这里很重要，通过下面的Base64编码的转换可以保证你的中文附件标题名在发送时不会变成乱码
                    //sun.misc.BASE64Encoder enc = new sun.misc.BASE64Encoder();
                    //messageBodyPart.setFileName("=?GBK?B?" + enc.encode(attachment.getName().getBytes()) + "?=");

                    //MimeUtility.encodeWord可以避免文件名乱码
                    attachmentBodyPart.setFileName(MimeUtility.encodeWord(attachment.getName()));
                    multipart.addBodyPart(attachmentBodyPart);
                }
            }
//            String logoPng = "E:\\workspaceRc\\sims-order-sunac\\order-mail\\target\\dxLogo.jpg";

            //国贸修改  不需要logo
      /*      String logoPng = properties.getProperty("mailpush.attachmentpath")+File.separator+"dxLogo.jpg";
            File logoFile = new File(logoPng);
            if(!logoFile.exists()){
                InputStream is =null;
                OutputStream os = null;
                try {
                    *//*Logo图片*//*
                    is = this.getClass().getResourceAsStream("/config/mail/dxLogo.jpg");
                    byte[] buffer = new byte[is.available()];
                    is.read(buffer);
                    os = new FileOutputStream(logoFile);
                    os.write(buffer);
                    os.flush();
                    os.close();
                } catch (Exception e) {
                    log.info("读取jar包中的图片异常：{}",e);
                } finally {
                    //释放资源
                    if (is != null) {
                        is.close();
                    }
                    if (os != null) {
                        os.close();
                    }
                }
            }

            MimeBodyPart img = new MimeBodyPart();
            DataHandler dh = new DataHandler(new FileDataSource(logoPng));//图片路径
            img.setDataHandler(dh);
            img.setContentID("pic0");
            multipart.addBodyPart(img);*/
            /*Logo图片结束*/

            /**
            if(null!=pics&&pics.length>0){
                MimeBodyPart img = new MimeBodyPart();
                for (int i = 0; i < pics.length; i++) {
                    //邮件里的图片
                    DataHandler dh = new DataHandler(new FileDataSource(path+pics[i]));//图片路径
                    img.setDataHandler(dh);
                    img.setContentID("pic"+i);
                    multipart.addBodyPart(img);
                }
            }
             */

            message.setSentDate(Calendar.getInstance().getTime());
            message.setSubject(emailTitle);   // 设置邮件标题

            // 将multipart对象放到message中
            message.setContent(multipart);

            // 第三步：发送消息
/*            Transport transport = mailSession.getTransport("smtp");
            transport.connect(smtpHost, fromEmail, password);
            transport.send(message, message.getRecipients(RecipientType.TO)); // 发送邮件啦
*/
            Transport.send(message);
            return getSussRtn(null, "发送成功");
        } catch (Exception e) {
            log.error("邮件发送错误:{}",e);
            return getFailRtn("邮件发送错误"+e.getMessage());
//            throw new Exception("邮件发送错误", e);
        }
    }

    /**
     * 获取成功的返回内容
     *
     * @param data
     * @author chenrui
     * @return
     */
    protected Map<String, String> getSussRtn(String data, String msg) {
        Map<String, String> rtn = new HashMap<String, String>();
        rtn.put("code", sucCode);
        rtn.put("msg", msg);
        rtn.put("data", data);
        return rtn;
    }

    /**
     * 获取失败的返回内容
     *
     * @param msg
     * @author chenrui
     * @return
     */
    protected Map<String, String> getFailRtn(String msg) {
        Map<String, String> rtn = new HashMap<String, String>();
        rtn.put("code", failCode);
        rtn.put("msg", msg);
        rtn.put("data", null);
        return rtn;
    }

    public Map sendMail(EmailContent content) throws Exception  {
        JSONObject.toJSONString(content);
        if(null==content.getSubjects()||"".equals(content.getSubjects())){
            return getFailRtn("邮件标题(主题)不能为空");
        }
        if(StringUtils.isEmpty(content.getTemplate_id())){
            return getFailRtn("模板id不能为空");
        }
        if(null==content.getTo()||content.getTo().length==0){
            return getFailRtn("收件人信息不能为空");
        }
        if(null==content.getCC()||content.getCC().length==0){
            content.setCC(null);
        }

        //获取内容
        String mailcontent = "";
        //邮件标题
        String Subjects = "";
        try {
            String template_id = content.getTemplate_id();
            //正常发票
            if("53".equals(template_id)){
                template_id = "zjFpDelivery.ftl";
                Subjects = "【电子发票】您收到一张新的电子发票[发票号码："+content.getSubjects()+"]";
            }
            //异常发票
            else if("55".equals(template_id)){
                template_id = "zjFpYichang.ftl";
                Subjects = "异常订单"+content.getSubjects();
            }
            //余票预警
            else if("65".equals(template_id)){
                template_id = "Invoice_Ypyj.ftl";
                Subjects = "余票预警提醒";
            }
            mailcontent = FreeMarkerUtil.generateString(
                    content.getContents(),
                    template_id);
        } catch (Exception e) {
            e.printStackTrace();
            return getFailRtn("生成模板信息出错");
        }
        InputStream io = null;
        Properties properties = new Properties();
        io = SendMailUtil.class.getResourceAsStream("/mail.properties");
        try {
            properties.load(io);
        } catch (IOException e) {
            e.printStackTrace();
        }
        String addressPass = properties.getProperty("addressPass");
        String[] split = addressPass.split("~~");
        String emailFrom =split[0];
        String emailPass =split[1];
        //处理附件
        //附件列表
        List<File> filesList = new LinkedList<File>();
        if(content.getAttachments()!=null&&content.getAttachments().length>0){
            for (Attachments item:content.getAttachments()) {
//				String fileName = item.getName()+"."+item.getType();
                /*if (Optional.ofNullable(item).map(i -> i.getContent()).isPresent()) {
                    continue;
                }*/
                if(null == item || StringUtils.isEmpty(item.getContent())) {
                    continue;
                }

                String fileName = item.getName();
                FileUtil.base64ToFile(properties.getProperty("mailpush.attachmentpath"), item.getContent(), fileName);
                String AllFilePath = properties.getProperty("mailpush.attachmentpath")+File.separator+fileName;
                filesList.add(new File(AllFilePath));
            }
        }

        Map<String, String> sendEmail = sendEmail(content.isIF_NEED_ACCOUNT(),
                emailFrom,emailPass,Subjects,
                mailcontent,content.getSenderName(),content.getTo(),
                content.getCC()==null?new String[]{}:content.getCC(),filesList,null);
        //发送完了把创建的附件删除
        for (File file:filesList) {
            file.delete();
        }
        return sendEmail;
    }

}

