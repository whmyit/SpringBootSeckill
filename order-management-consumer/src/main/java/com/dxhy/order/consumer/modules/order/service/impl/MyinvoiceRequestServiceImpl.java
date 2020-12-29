package com.dxhy.order.consumer.modules.order.service.impl;

import com.dxhy.order.constant.ConfigureConstant;
import com.dxhy.order.constant.OrderInfoContentEnum;
import com.dxhy.order.constant.OrderManagementConstant;
import com.dxhy.order.consumer.config.OpenApiConfig;
import com.dxhy.order.consumer.model.myinovice.SynSellerInfoRequest;
import com.dxhy.order.consumer.modules.order.service.MyinvoiceRequestService;
import com.dxhy.order.model.R;
import com.dxhy.order.utils.HttpUtils;
import com.dxhy.order.utils.JsonUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

/**
 * 我的发票同步业务层
 *
 * @author ZSC-DXHY
 * @date 创建时间: 2020-08-14 15:21
 */
@Service
@Slf4j
public class MyinvoiceRequestServiceImpl implements MyinvoiceRequestService {
	
	private static final String LOGGER_MSG = "(我的发票公众号业务调用)";
	
	@Override
	public R synSellerInfo(SynSellerInfoRequest sellerInfo, String url) {
		
		log.info("请求请求我的发票公众号企业同步信息接口,url：{},入参:{}", url, JsonUtils.getInstance().toJsonString(sellerInfo));
		String postWithJson2 = HttpUtils.doPost(url, JsonUtils.getInstance().toJsonString(sellerInfo));
		log.info("请求请求我的发票公众号企业同步信息接口,出参:{}", postWithJson2);
		if (ConfigureConstant.STRING_1.equals(postWithJson2)) {
			return R.ok().put(OrderManagementConstant.CODE, OrderInfoContentEnum.SUCCESS.getKey())
					.put(OrderManagementConstant.MESSAGE, OrderInfoContentEnum.SUCCESS.getMessage());
		} else {
			return R.error().put(OrderManagementConstant.CODE, OrderInfoContentEnum.RECEIVE_FAILD.getKey())
					.put(OrderManagementConstant.MESSAGE, OrderInfoContentEnum.RECEIVE_FAILD.getMessage());
		}
		
	}
	
	@Override
	public Map<String, Object> getAuthUrlFromWxService(String orderNo, String money, String redirectUrl,
	                                                   String timestamp, String appid) {
		Map<String, Object> param = new HashMap<>(5);
		param.put("order_id", orderNo);
		param.put("money", Math.round(Double.parseDouble(money) * 100));
		param.put("source", "web");
		if (StringUtils.isEmpty(timestamp)) {
			param.put("timestamp", String.valueOf(System.currentTimeMillis() / 1000));
		} else {
			param.put("timestamp", timestamp);
		}
		// 暂时没跳转页
		param.put("redirect_url", StringUtils.isEmpty(redirectUrl) ? "" : redirectUrl);
		param.put("type", "0");
		param.put("gzh_appid", StringUtils.isBlank(appid) ? "" : appid);
		log.debug("调用微信服务的获取授权开票页面 参数map为：{}", JsonUtils.getInstance().toJsonString(param));
		Map<String, Object> postReturnMap = null;
		try {
			String doPost = HttpUtils.doPost(OpenApiConfig.getAuthUrl, JsonUtils.getInstance().toJsonString(param));
			log.debug("调用微信服务的获取授权开票页面 返回map为：{}", doPost);
			if (StringUtils.isNotBlank(doPost)) {
				postReturnMap = JsonUtils.getInstance().parseObject(doPost, Map.class);
				return postReturnMap;
			}
			
		} catch (Exception e) {
			log.error("{}调用微信服务的获取授权开票页面 出错，异常为：{}", LOGGER_MSG, e);
		}
		
		return postReturnMap;
	}
	
	@Override
	public Map<String, Object> getAuthStatus(String orderNo, String appid) {
		
		Map<String, Object> param = new HashMap<>(5);
		/**
		 * 用户授权id
		 */
		param.put("order_id", orderNo);
		/**
		 * 第三方企业的appid
		 */
		param.put("authAppId", appid);
		/**
		 * 我的发票公众号id,默认为空,公众号会自动补全
		 */
		param.put("s_pappid", "");
		
		Map<String, Object> postReturnMap = new HashMap<>(5);
		try {
			Map<String, String> header = new HashMap<>(5);
			header.put("Content-Type", "application/json");
			String doPost = HttpUtils.doPostWithHeader(OpenApiConfig.getAuthStatus, JsonUtils.getInstance().toJsonString(param), header);
			log.debug("调用微信服务的获取授权状态接口 返回map为：{}", doPost);
			/**
			 * 返回数据结构
			 * {
			 *   "errcode": 0,
			 *   "money": 100500,
			 *   "openid": "oN0bYv4l_hes5JehJjsg7pVs7mWI",
			 *   "auth_time": 1586776340,
			 *   "user_auth_info": {},
			 *   "errmsg": "ok",
			 *   "source": "web",
			 *   "type": 0,
			 *   "redirect_url": "",
			 *   "invoice_status": "invoice send"
			 * }
			 * 如果errcode等于0,表示接口调用成功,
			 * 如果errcode为0,会有invoice_status字段,invoice_status内容为:
			 * invoice send表示插卡成功
			 * auth success表示授权成功
			 *
			 */
			if (StringUtils.isNotBlank(doPost)) {
				postReturnMap = JsonUtils.getInstance().parseObject(doPost, Map.class);
				return postReturnMap;
			}
			
		} catch (Exception e) {
			log.error("{}调用微信服务的获取授权状态接口 出错，异常为：{}", LOGGER_MSG, e);
		}
		
		return postReturnMap;
	}
	
}
