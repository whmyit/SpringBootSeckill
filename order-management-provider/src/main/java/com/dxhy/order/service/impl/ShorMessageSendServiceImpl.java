package com.dxhy.order.service.impl;

import cn.hutool.core.util.RandomUtil;
import com.dxhy.order.api.ApiInvoiceCommonService;
import com.dxhy.order.api.ApiShorMessageSend;
import com.dxhy.order.api.RedisService;
import com.dxhy.order.config.OpenApiConfig;
import com.dxhy.order.constant.ConfigureConstant;
import com.dxhy.order.constant.Constant;
import com.dxhy.order.constant.OrderInfoContentEnum;
import com.dxhy.order.dao.OrderInvoiceInfoMapper;
import com.dxhy.order.model.OrderInvoiceInfo;
import com.dxhy.order.model.R;
import com.dxhy.order.model.message.GlobalInfo;
import com.dxhy.order.model.message.OpenApiResponse;
import com.dxhy.order.model.message.ShortMessageRequest;
import com.dxhy.order.service.OpenApiService;
import com.dxhy.order.utils.Base64Encoding;
import com.dxhy.order.utils.JsonUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.dubbo.config.annotation.Service;

import javax.annotation.Resource;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Map;

/**
 * @author ：杨士勇
 * @ClassName ：ShorMessageSendServiceImpl
 * @Description ：短信发送
 * @date ：2020年4月15日 下午5:48:03
 */
@Service
@Slf4j
public class ShorMessageSendServiceImpl implements ApiShorMessageSend{
	
	@Resource
	RedisService redisService;
	
	@Resource
	OpenApiService openApiService;
	
	@Resource
	OrderInvoiceInfoMapper orderInvoiceInfoMapper;
	
	@Resource
    ApiInvoiceCommonService apiInvoiceCommonService;
	
	private static final String SHORT_MESSAE_TQM_PREFIX = "sims_notes_tqm_";
	
	@Override
	public R sendShortMessage(List<Map> invoiceIdArray, String phone) {
        
        List<String> errorMessageList = new ArrayList<>();
        int successCount = 0;
        for (Map map : invoiceIdArray) {
	
	        OrderInvoiceInfo orderInvoiceInfo = new OrderInvoiceInfo();
	        orderInvoiceInfo.setId((String) map.get("id"));
	        List<String> shList = new ArrayList<>();
	        shList.add((String) map.get("xhfNsrsbh"));
	        OrderInvoiceInfo selectByPrimaryKey = orderInvoiceInfoMapper.selectOrderInvoiceInfo(orderInvoiceInfo, shList);
	        String tqm = apiInvoiceCommonService.getGenerateShotKey();
	
	        Calendar cal = Calendar.getInstance();
	        cal.setTime(selectByPrimaryKey.getKprq());
	        int month = cal.get(Calendar.MONTH) + 1;
	        int day = cal.get(Calendar.DAY_OF_MONTH);
	
	        //根据提取码存储 发票信息 有效期暂定一周
	        redisService.set(SHORT_MESSAE_TQM_PREFIX + tqm, JsonUtils.getInstance().toJsonString(selectByPrimaryKey), Constant.REDIS_EXPIRE_TIME_DEFAULT);
	
	        //外层报文
	        GlobalInfo globalInfo = new GlobalInfo();
	        globalInfo.setEncryptCode("0");
	        globalInfo.setZipCode("0");
	        globalInfo.setDataExchangeId(RandomUtil.randomNumbers(ConfigureConstant.INT_25));
	
	        //内层报文
	        ShortMessageRequest request = new ShortMessageRequest();
	
	        //短信模板参数
	        String url = String.format(OpenApiConfig.messageShortUrl, tqm);
	        String[] params = new String[3];
	        params[0] = String.valueOf(month);
	        params[1] = String.valueOf(day);
	        params[2] = url;
			
			//需要发送的手机号
	        String[] phones = new String[1];
	        phones[0] = phone;
	
	        request.setParams(params);
	        request.setPhones(phones);
	        request.setSerialNum(apiInvoiceCommonService.getGenerateShotKey());
	        request.setTemplateCode(OpenApiConfig.notesTemplateId);
	        log.info("短信发送内层报文:{}", JsonUtils.getInstance().toJsonString(request));
	        globalInfo.setContent(Base64Encoding.encodeToString(JsonUtils.getInstance().toJsonString(request).getBytes(StandardCharsets.UTF_8)));
	        log.info("短信发送接口入参：{}", JsonUtils.getInstance().toJsonString(globalInfo));
	
	        OpenApiResponse response = openApiService.sendRequest(globalInfo, OpenApiConfig.sendMessageUrl);
	
	        log.info("短信发送接口出参：{}", JsonUtils.getInstance().toJsonString(response));
	        if (!OrderInfoContentEnum.SUCCESS.getKey().equals(response.getReturnStateInfo().getReturnCode())) {
		        String msg = Base64Encoding.decodeToString(String.valueOf(response.getReturnStateInfo().getReturnMessage()));
		        log.error("短信交付失败,发票代码:{},发票号码:{},错误信息：{}", selectByPrimaryKey.getFpdm(), selectByPrimaryKey.getFphm(), msg);
		        String errorMsg = "短信交付异常,发票代码:" + selectByPrimaryKey.getFpdm() + "发票号码:" + selectByPrimaryKey.getFphm();
		        errorMessageList.add(errorMsg);
	        } else {
		        String msg = Base64Encoding.decodeToString(String.valueOf(response.getReturnStateInfo().getReturnMessage()));
		        log.info(msg);
		        successCount++;
				
			}
		}
		
	    if(successCount == 0){
	    	return R.error().put("message","短信发送失败");
	    }else if(successCount == invoiceIdArray.size()){
	    	return R.ok().put("message", "短信发送成功 +" + successCount + "条");
	    }else{
		    return R.ok().put("message", "短信发送成功 +" + successCount + "条").put("errorList", errorMessageList);
	    }
	}

}
