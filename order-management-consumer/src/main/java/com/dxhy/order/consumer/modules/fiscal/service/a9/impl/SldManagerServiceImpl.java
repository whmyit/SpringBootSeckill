package com.dxhy.order.consumer.modules.fiscal.service.a9.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.dxhy.order.constant.ConfigureConstant;
import com.dxhy.order.constant.OrderInfoContentEnum;
import com.dxhy.order.constant.OrderInfoEnum;
import com.dxhy.order.consumer.config.OpenApiConfig;
import com.dxhy.order.consumer.modules.fiscal.service.a9.SldManagerServiceA9;
import com.dxhy.order.model.a9.c48ydtj.YdtjDetailParam;
import com.dxhy.order.model.a9.c48ydtj.YdtjDto;
import com.dxhy.order.model.a9.c48ydtj.YdtjParam;
import com.dxhy.order.model.a9.dy.DydListRequst;
import com.dxhy.order.model.a9.dy.DydResponse;
import com.dxhy.order.model.a9.query.FpYdtj;
import com.dxhy.order.model.a9.query.YdhzxxRequest;
import com.dxhy.order.model.a9.query.YhzxxResponse;
import com.dxhy.order.model.a9.query.YhzxxResponseExtend;
import com.dxhy.order.utils.HttpInvoiceRequestUtil;
import com.dxhy.order.utils.HttpUtils;
import com.dxhy.order.utils.JsonUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
/**
 * 受理点管理实现类
 *
 * @author ZSC-DXHY
 * @date 创建时间: 2020-08-14 18:19
 */
@Slf4j
@Service
public class SldManagerServiceImpl implements SldManagerServiceA9 {
    
    
    /**
     * 查询月度汇总信息
     */
	@Override
    public YhzxxResponse queryYhzxx(YdhzxxRequest paramSkReqYhzxxcx) {
        Map<String, String> header = HttpInvoiceRequestUtil.getRequestHead(paramSkReqYhzxxcx.getTerminalCode());
        // 设置请求头参数
        log.debug("调用接口:{},月度汇总信息查询，url:{},入参:{}", header.get("X-Request-Id"), OpenApiConfig.ydtj, JsonUtils.getInstance().toJsonStringNullToEmpty(paramSkReqYhzxxcx));
        String result = HttpUtils.doPostWithHeader(OpenApiConfig.ydtj,
        		JsonUtils.getInstance().toJsonStringNullToEmpty(paramSkReqYhzxxcx), header);
        log.debug("调用接口:{},月度汇总信息查询，url:{},出参:{}", header.get("X-Request-Id"), OpenApiConfig.ydtj, result);
        // 返回参数解析成对象
        if(OrderInfoEnum.TAX_EQUIPMENT_A9.getKey().equals(paramSkReqYhzxxcx.getTerminalCode())){
            YhzxxResponse response = JsonUtils.getInstance().parseObject(result, YhzxxResponse.class);
            return response;
        }
        YhzxxResponseExtend yhzxxResponseExtend  = new YhzxxResponseExtend();
        try{
            JSONObject jsonObject = JSONObject.parseObject(result);
            String result1 = jsonObject.getString("result");
            if(StringUtils.isBlank(result1)) {
                String code = jsonObject.getString(ConfigureConstant.CODE);
                String msg = jsonObject.getString(ConfigureConstant.MSG);
                YhzxxResponse response = new YhzxxResponse();
                yhzxxResponseExtend.setStatusCode(code);
                yhzxxResponseExtend.setStatusMessage(msg);
                response.setResult(yhzxxResponseExtend);
                response.setCode(code);
                response.setMsg(msg);
                return response;
            }
            List<FpYdtj> fpYdtjList = JSON.parseArray(result1,FpYdtj.class);
    
            yhzxxResponseExtend.setFpYdtj(fpYdtjList);
            yhzxxResponseExtend.setStatusCode(OrderInfoContentEnum.SUCCESS.getKey());
            yhzxxResponseExtend.setStatusMessage(OrderInfoContentEnum.SUCCESS.getMessage());
            YhzxxResponse response = new YhzxxResponse();
            response.setResult(yhzxxResponseExtend);
            response.setCode(OrderInfoContentEnum.SUCCESS.getKey());
            response.setMsg(OrderInfoContentEnum.SUCCESS.getMessage());
            return response;
        }catch (Exception e){
            yhzxxResponseExtend.setStatusCode(OrderInfoContentEnum.INTERNAL_SERVER_ERROR.getKey());
            yhzxxResponseExtend.setStatusMessage(OrderInfoContentEnum.INTERNAL_SERVER_ERROR.getMessage());
            YhzxxResponse response = new YhzxxResponse();
            response.setResult(yhzxxResponseExtend);
            response.setCode(OrderInfoContentEnum.INTERNAL_SERVER_ERROR.getKey());
            response.setMsg(OrderInfoContentEnum.INTERNAL_SERVER_ERROR.getMessage());
            return response;
        }

	}

    /**
     * 查询打印点信息
     */
	@Override
	public DydResponse queryDydxxcxList(DydListRequst dyRequest) {
        Map<String, String> header = HttpInvoiceRequestUtil.getRequestHead(dyRequest.getTerminalCode());
        // 设置请求头参数
        log.debug("调用接口:{},查询打印点列表信息，url:{},入参:{}", header.get("X-Request-Id"), OpenApiConfig.queryDydxxcxList, JsonUtils.getInstance().toJsonStringNullToEmpty(dyRequest));
        String result = HttpUtils.doPostWithHeader(OpenApiConfig.queryDydxxcxList,
        		JsonUtils.getInstance().toJsonStringNullToEmpty(dyRequest), header);
        log.debug("调用接口:{},查询打印点列表信息，url:{},出参:{}", header.get("X-Request-Id"), OpenApiConfig.queryDydxxcxList, result);
        // 返回参数解析成对象
        
        DydResponse response = JsonUtils.getInstance().parseObject(result, DydResponse.class);
        
        if (response.getResult() != null && StringUtils.isBlank(response.getResult().getStatusCode())) {
            response.getResult().setStatusCode(response.getCode());
            response.getResult().setStatusMessage(response.getMsg());
        }
        
        return response;
    }
    
    
    @Override
    public List<YdtjDto> queryYhzxxBwpz(YdtjParam ydtjParam) {
        try {
            Map<String, String> header = HttpInvoiceRequestUtil.getRequestHead(ydtjParam.getTerminalCode());
            
            // 设置请求头参数
            log.debug("调用接口:{},百旺盘阵月度汇总信息查询，url:{},入参:{}", header.get("X-Request-Id"), OpenApiConfig.ydtj, JsonUtils.getInstance().toJsonStringNullToEmpty(ydtjParam));
            String result = HttpUtils.doPostWithHeader(OpenApiConfig.ydtj,
                    JsonUtils.getInstance().toJsonStringNullToEmpty(ydtjParam), header);
            log.debug("调用接口:{},百旺盘阵月度汇总信息查询，url:{},出参:{}", header.get("X-Request-Id"), OpenApiConfig.ydtj, result);
            
            if (StringUtils.isNotBlank(result)) {
                JSONObject resultJson = JSONObject.parseObject(result);
                if (OrderInfoContentEnum.SUCCESS.getKey().equals(resultJson.getString(ConfigureConstant.CODE))) {
                    String dataResult = resultJson.getString("result");
                    List<YdtjDto> ydtjDtoList = JSONObject.parseArray(dataResult, YdtjDto.class);
                    if (ydtjDtoList != null && !ydtjDtoList.isEmpty()) {
                        return ydtjDtoList;
                    }
                }
            }
        } catch (Exception e) {
            log.error("百旺盘阵发票汇总查询异常", e);
        }
        return null;
    }
    
    @Override
    public JSONObject getBbfxDetailBwPz(YdtjDetailParam ydtjDetailParam) {
        try {
            Map<String, String> header = HttpInvoiceRequestUtil.getRequestHead(ydtjDetailParam.getTerminalCode());
            
            // 设置请求头参数
            log.debug("调用接口:{},百旺盘阵月度汇总详情信息查询，url:{},入参:{}", header.get("X-Request-Id"), OpenApiConfig.getBbfxDetail,
                    JsonUtils.getInstance().toJsonStringNullToEmpty(ydtjDetailParam));
            String result = HttpUtils.doPostWithHeader(OpenApiConfig.getBbfxDetail,
                    JsonUtils.getInstance().toJsonStringNullToEmpty(ydtjDetailParam), header);
            
            log.debug("调用接口:{},百旺盘阵月度汇总详情信息查询，url:{},出参:{}", header.get("X-Request-Id"), OpenApiConfig.getBbfxDetail, result);
            
            if (StringUtils.isNotBlank(result)) {
                JSONObject resultJson = JSONObject.parseObject(result);
                return resultJson;
            }
        } catch (Exception e) {
            log.error("百旺盘阵发票汇总查询异常", e);
        }
        return null;
    }
    
}
