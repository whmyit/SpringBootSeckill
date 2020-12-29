package com.dxhy.order.utils;

import freemarker.template.Configuration;
import freemarker.template.Template;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.ui.freemarker.FreeMarkerTemplateUtils;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import static freemarker.template.Configuration.DEFAULT_INCOMPATIBLE_IMPROVEMENTS;

/**
 * @author: wangyang
 * @date: 2020/5/5 17:40
 * @description
 */
@Component
@Slf4j
public class FreeMarkerUtil {
    
    private static final SimpleDateFormat SDF = new SimpleDateFormat("yyyy年MM月dd日");

    public Template getTemplate(String ftlName){
        //读取相应的ftl
        Configuration cfg = new Configuration(DEFAULT_INCOMPATIBLE_IMPROVEMENTS);
        //读取相应的ftl模板文件，指定模板路径
        cfg.setClassForTemplateLoading(this.getClass(), "/sms/");
        try {
            Template template =  cfg.getTemplate(ftlName);
            return template;
        } catch (IOException e) {
            log.error("读取邮件模板异常", e);
        }
        return null;
    }

    public String processTemplate(String templateName, Map<String,String> data){
        String content = "";

        try{
            content = FreeMarkerTemplateUtils.processTemplateIntoString(getTemplate(templateName),data);
        }catch (Exception e){
            log.error("替换邮件模板数据异常",e);
        }
        return content;
    }

    public static void main(String[] args) {
        String fileName = "mailContent.ftl";
    
        Map<String, String> data = new HashMap<>(5);
        data.put("ddrq", SDF.format(new Date()));
        data.put("fpdm", "150000020026");
        data.put("fphm", "14536797");
        data.put("kprq", SDF.format(new Date()));
        data.put("gfmc", "测试购方名称");
        data.put("kphjje", "1234.32");
    
        FreeMarkerUtil freeMarkerUtil = new FreeMarkerUtil();
        String content = freeMarkerUtil.processTemplate(fileName, data);
    }
}
