package com.dxhy.order.ordermail.util;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.dxhy.order.ordermail.model.AccessTokenBean;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;

import java.util.*;

/**
 *
 * 描述信息：大数据请求是数据处理Bean
 *
 * @author 李志永
 * @version 1.0 Created on 2017年2月8日 下午5:05:27
 */
@Slf4j
public class DataHandleUtil {

	/**
	 * 日志输出信息
	 */
	private static final String LOGGER_MSG = "(调用token接口)";

	/**
     *
	 * 字典排序请求数据字典
     *
	 * @param map
	 * @return String
	 * @author: 李志永
	 * @date: Created on 2017年2月9日 下午2:58:10
	 */
	public static String sortParameter(Map<String, Object> map) {

		Collection<String> keyset = map.keySet();
		StringBuffer mosaicString = new StringBuffer();
		List<String> list = new ArrayList<String>(keyset);
		Collections.sort(list);
		// 这种打印出的字符串顺序和微信官网提供的字典序顺序是一致的
		for (int i = 0; i < list.size(); i++) {
			if (0 == i) {
				mosaicString.append(list.get(i)).append("=").append(map.get(list.get(i)));
			} else {
				mosaicString.append("&").append(list.get(i)).append("=").append(map.get(list.get(i)));
			}
		}

		return mosaicString.toString();

	}

	/**
     *
	 * <p>
	 * 处理返回数据
	 * </p>
     *
	 * @param reqeustGet
	 * @return Map<String,Object>
	 * @author: tengjy
	 * @date: Created on 2017年3月31日 上午11:27:05
	 */
	public static Map<String, Object> executeReturnData(String reqeustGet) {
        
        Map<String, Object> returnMap = new HashMap<>(10);
		try {
			ObjectMapper jsonMapper = new ObjectMapper();

			AccessTokenBean responseReturnBean = jsonMapper.readValue(reqeustGet, AccessTokenBean.class);

			if (responseReturnBean != null) {
				returnMap.put("responseReturnBean", responseReturnBean);
			}

		} catch (Exception e) {
			log.error(LOGGER_MSG + "JSON数据转化，异常信息为：{}", e.getMessage());
		}
		return returnMap;
	}

	/**
     *
	 * <p>
	 * 处理邮件返回数据
	 * </p>
     *
	 * @param reqeustGet
	 * @return Map<String,Object>
	 * @author: tengjy
	 * @date: Created on 2017年3月31日 上午11:27:05
	 */
	public static Map<String, Object> executeReturnDataOfEmail(String reqeustGet) {
        Map<String, Object> returnMap = new HashMap<>(10);
		try {
			JSONObject jsonObject = JSON.parseObject(reqeustGet);
			if (jsonObject != null) {
				Map<String, Object> responseMap = (Map<String, Object>) jsonObject.get("returnStateInfo");
				if(responseMap!=null) {
                    returnMap.put("code", responseMap.get("returnCode"));
                    returnMap.put("massage", responseMap.get("returnMessage"));
                    
                }

			}
		} catch (Exception e) {
			log.error(LOGGER_MSG + "JSON数据转化，异常信息为：{}", e.getMessage());
		}
		return returnMap;
	}

	/**
     *
	 * <p>
	 * json数据转换
	 * </p>
     *
	 * @param content
	 * @return
	 * @throws JsonProcessingException
	 *             String
	 * @author: tengjy
	 * @date: Created on 2017年6月16日 下午2:33:15
	 */
	public static String json(Object content) throws JsonProcessingException {
		ObjectMapper mapper = new ObjectMapper();
		return mapper.writeValueAsString(content);
	}
}
