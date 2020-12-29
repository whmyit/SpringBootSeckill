package com.dxhy.order.vo;

import lombok.Getter;
import lombok.Setter;

import java.io.File;
import java.util.Map;

/**
 * @author: wangyang
 * @date: 2020/5/5 10:00
 * @description
 */
@Getter
@Setter
public class MailContentVo {

    /**
     * 收件人
     * */
    private String[] to;
    /**
     * 抄送人
     * */
    private String[] cc;
    /**
     * 邮件主题
     * */
    private String subject;
    /**
     * 邮件内容
     * */
    private String content;

    /**
     * 邮件模板名称
     */
    private String templateFileName;

    /**
     * 附件
     */
    private File[] files;

    /**
     * 内容模板和标题模板中需替换的真实数据  key:模板中值 value:真实数据
     * 例如：发票号码：<(发票号码)>  key:发票号码
     */
    private Map<String,String> model;

}
