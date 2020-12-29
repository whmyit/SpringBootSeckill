package com.dxhy.order.ordermail.util;

import com.sun.mail.util.MailSSLSocketFactory;

import javax.mail.*;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;
import java.util.Calendar;
import java.util.Map;
import java.util.Properties;

public class TestMail2 {
	
	
	public static Map<String,Object> sendEmail(
			String fromEmail
			,String password
			,String emailTitle
			, String content
			,String senderName
			,String receiveEmail) {
    	try {
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
            props.setProperty("mail.smtp.host", "mail.itg.com.cn");  //此处填写SMTP服务器
            props.setProperty("mail.smtp.port", "25");             //ssl加密使用465，不加密默认使用25
            props.put("mail.smtp.auth", true);       // 表示SMTP发送邮件，必须进行身份验证
            props.put("mail.debug", "true");      //开启调试模式
            props.put("mail.transport.protocol", "smtp");     // 发送邮件协议名称 ssl加密使用smtps，不加密默认使用smtp
            //props.put("mail.smtp.auth.mechanisms", "NTLM");

            Session mailSession = Session.getInstance(props, new MyAuthenticator(fromEmail, password));//此处填写你的账号和口令(16位口令)
            //Session mailSession = Session.getInstance(props, new MyAuthenticator(fromEmail, password));//此处填写你的账号和口令(16位口令)
/*            Session mailSession = Session.getInstance(props);
            Transport transport = mailSession.getTransport();
            transport.connect(fromEmail,password);*/
            //第二步：编写消息
            MimeMessage message = new MimeMessage(mailSession);
            //防止成为垃圾邮件，披上outlook的马甲
            message.addHeader("X-Mailer","Microsoft Outlook Express 6.00.2900.2869");
            	senderName = fromEmail;
            message.setFrom(new InternetAddress (fromEmail, senderName, "UTF-8"));
            message.setRecipients(Message.RecipientType.TO, receiveEmail);
            
            //因为有的邮箱是把主题作为显示位的，所以主题跟标题弄成一样的。
            message.setSubject(emailTitle);
            
         // 向multipart对象中添加邮件的各个部分内容，包括文本内容和附件
            Multipart multipart = new MimeMultipart();
            // 添加邮件正文
            BodyPart contentPart = new MimeBodyPart();
            contentPart.setContent(content, "text/html;charset=UTF-8");
            multipart.addBodyPart(contentPart);

            message.setSentDate(Calendar.getInstance().getTime());
            message.setSubject(emailTitle);   // 设置邮件标题
         // 将multipart对象放到message中
            message.setContent(multipart);
            Transport.send(message);
            //transport.sendMessage(message,message.getAllRecipients());
            return null;
        } catch (MessagingException e) {
            e.printStackTrace();
//            logger.error("邮件发送错误" + e.getMessage());
            return null;
        } catch (Exception e) {
//            logger.error("邮件发送错误" + e.getMessage());
        	return null;
        }
    
	}
	
	
	public static void main(String[] args) throws InterruptedException {
		
		for (int i = 0; i < 1; i++) {
			sendEmail("itax@itg.com.cn", "Rmnfa86mnfa8kr", "第"+String.valueOf(i)+"封加密测试", "测试加密邮件", "c","957086562@qq.com");
			Thread.sleep(500);
		}

        //System.out.println("wl-invoice@sunac.com.cn".substring(0, "wl-invoice@sunac.com.cn".lastIndexOf("@")));
    }
}
