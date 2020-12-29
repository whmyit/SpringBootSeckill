package com.dxhy.order.ordermail.util;

import freemarker.cache.ClassTemplateLoader;
import freemarker.cache.StringTemplateLoader;
import freemarker.core.ParseException;
import freemarker.template.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.servlet.view.freemarker.FreeMarkerConfigurer;

import java.io.File;
import java.io.IOException;
import java.io.StringWriter;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

/**
 * FreeMarker工具，用于生成邮件内容
 * 
 * @author Haijian
 * @date 2015-9-11
 */
public class FreeMarkerUtil {

	private static final Logger LOGGER = LoggerFactory
			.getLogger(FreeMarkerUtil.class);
	private static Configuration conf = new Configuration();
	private static Map<String, Template> templateCache = new HashMap<String, Template>();

	static {
		try {
			//war包启动可以使用如下配置
			/*URL url = FreeMarkerUtil.class.getResource("/config/mail");
			File dir = new File(url.getPath());
			conf.setClassicCompatible(true);
			conf.setDefaultEncoding("UTF-8");
			conf.setDirectoryForTemplateLoading(dir);*/

			//jar包启动使用如下配置
			conf.setClassicCompatible(true);
			conf.setDefaultEncoding("UTF-8");
			conf.setClassForTemplateLoading(FreeMarkerUtil.class,"/config/mail");
			conf.setTemplateLoader(new ClassTemplateLoader(FreeMarkerUtil.class,"/config/mail"));


		} catch (Exception e) {
		LOGGER.error("FreeMarkerUtil配置出错......", e);
	}
	}

	/**
	 * 根据模板和传入值生成String
	 * 
	 * @param dataModel
	 *            传入值
	 * @param templateName
	 *            模板文件名
	 * @return String
	 */
	public static String generateString(Map<String, Object> dataModel,
			String templateName) {
		StringWriter sw = new StringWriter();
		try {
			Template template = null;
			if (templateCache.containsKey(templateName)) {
				LOGGER.debug("从缓存中获取模板信息");
				template = templateCache.get(templateName);
			} else {
				template = conf.getTemplate(templateName);
				templateCache.put(templateName, template);
			}
			template.process(dataModel, sw);
		} catch (Exception e) {
			LOGGER.error("根据模板和传入值生成String失败", e);
			return null;
		}
		LOGGER.debug(sw.toString());
		return sw.toString();
	}
	
	//从数据库中获取模板数据含${}这类的。然后赋值
	public static String getS() throws TemplateNotFoundException, MalformedTemplateNameException, ParseException, IOException, TemplateException{
		StringTemplateLoader stringLoader = new StringTemplateLoader();
		//这个名字随意
		String firstTemplate = "firstTemplate";
		stringLoader.putTemplate(firstTemplate, "你好，我是${namevalue}");
		// It's possible to add more than one template (they might include each other)
		// String secondTemplate = "<#include \"greetTemplate\"><@greet/> World!";
		// stringLoader.putTemplate("greetTemplate", secondTemplate);
		conf.setTemplateLoader(stringLoader);
		Template template = conf.getTemplate(firstTemplate);
		Map<String,Object> map = new HashMap<String,Object>();
		map.put("namevalue", "哈");
		StringWriter sw = new StringWriter();
		template.process(map, sw);
		System.out.println(sw.toString());
		return null;
	}
	/**
	 * 传的是数组，里面要用一个特定的参数来获取
	 * @param contents
	 * @param templateName
	 * @return
	 * @throws IOException 
	 * @throws ParseException 
	 * @throws MalformedTemplateNameException 
	 * @throws TemplateNotFoundException 
	 * @throws TemplateException 
	 */
	public static String generateString(String[] contents, String templateName) throws TemplateNotFoundException, MalformedTemplateNameException, ParseException, IOException, TemplateException {
		StringWriter sw = new StringWriter();
//		try {
			Template template = null;
			if (templateCache.containsKey(templateName)) {
				LOGGER.debug("从缓存中获取模板信息");
				template = templateCache.get(templateName);
			} else {
				template = conf.getTemplate(templateName);
				templateCache.put(templateName, template);
			}
			Map<String,Object> dataModel = new HashMap<String,Object>();
			dataModel.put("temData", contents);
			template.process(dataModel, sw);
//		} catch (Exception e) {
//			LOGGER.error("根据模板和传入值生成String失败", e);
//			return null;
//		}
//		LOGGER.debug(sw.toString());
		return sw.toString();
	}
}
