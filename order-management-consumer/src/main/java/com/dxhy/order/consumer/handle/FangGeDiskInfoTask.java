package com.dxhy.order.consumer.handle;

import com.dxhy.order.api.ApiFangGeInterfaceService;
import com.dxhy.order.api.RedisService;
import com.dxhy.order.constant.ConfigureConstant;
import com.dxhy.order.constant.Constant;
import com.dxhy.order.constant.OrderInfoEnum;
import com.dxhy.order.consumer.config.OpenApiConfig;
import com.dxhy.order.model.RegistrationCode;
import com.dxhy.order.model.a9.ResponseBaseBean;
import com.dxhy.order.model.fg.SpxxParam;
import com.dxhy.order.model.fg.TbSpxxParam;
import com.dxhy.order.utils.HttpInvoiceRequestUtilFg;
import com.dxhy.order.utils.JsonUtils;
import com.xxl.job.core.biz.model.ReturnT;
import com.xxl.job.core.handler.IJobHandler;
import com.xxl.job.core.handler.annotation.JobHandler;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.dubbo.config.annotation.Reference;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;


/**
 * 方格税盘信息同步定时任务
 *
 * @author ZSC-DXHY
 * @date 创建时间: 2020-08-14 14:49
 */
@Slf4j
@JobHandler(value = "FangGeDiskInfoTask")
@Component
public class FangGeDiskInfoTask extends IJobHandler {

    private static final String LOGGER_MSG = "(方格发送税盘信息)";

    @Reference
    private RedisService redisService;
    @Reference
    private ApiFangGeInterfaceService apiFangGeInterfaceService;

    @Override
    public ReturnT<String> execute(String param) throws Exception {
        /**
         * 模糊查询redis里面的key，获取注册的所有税盘信息
         */
        Set<String> set = redisService.keys("*" + Constant.FG_TAX_DISK_INFO + "*");
        if(!CollectionUtils.isEmpty(set)){
            for (String key : set) {
                String s = redisService.get(key);
                RegistrationCode registrationCode = JsonUtils.getInstance().parseObject(s, RegistrationCode.class);
                //消费下一条数据
                boolean pushMqttMsg = apiFangGeInterfaceService.pushMqttMsg(registrationCode.getXhfNsrsbh(), registrationCode.getJqbh());
                if (pushMqttMsg && StringUtils.isNotBlank(registrationCode.getSpzt()) && ConfigureConstant.STRING_1.equals(registrationCode.getSpzt())) {
                    registrationCode.setSpzt(ConfigureConstant.STRING_0);
                    apiFangGeInterfaceService.saveCodeToRedis(registrationCode);
                }
    
                if (!pushMqttMsg && StringUtils.isNotBlank(registrationCode.getSpzt()) && ConfigureConstant.STRING_0.equals(registrationCode.getSpzt())) {
                    List<String> fpzls = Arrays.asList(OrderInfoEnum.ORDER_INVOICE_TYPE_0.getKey(), OrderInfoEnum.ORDER_INVOICE_TYPE_2.getKey(), OrderInfoEnum.ORDER_INVOICE_TYPE_51.getKey());
                    for (String fpzl : fpzls) {
                        /**
                         * 修改税盘信息为离线
                         */
                        TbSpxxParam tbSpxxParam = new TbSpxxParam();
                        tbSpxxParam.setNSRSBH(registrationCode.getXhfNsrsbh());
                        tbSpxxParam.setJQBH(registrationCode.getJqbh());
                        tbSpxxParam.setJSPLX(registrationCode.getSplx());
                        tbSpxxParam.setFPZLDM(fpzl);
                        List<SpxxParam> spxxParams = new ArrayList<>();
                        SpxxParam spxxParam = new SpxxParam();
                        /**
                         * 航信税盘设置航信
                         */
                        if (ConfigureConstant.STRING_0.equals(registrationCode.getSplx())) {
                            spxxParam.setHXJSPZT(ConfigureConstant.STRING_1);
                        } else {
                            spxxParam.setBWJSPZT(ConfigureConstant.INT_1);
                        }
                        spxxParams.add(spxxParam);
                        tbSpxxParam.setJSPXX(spxxParams);
                        //   调用全税dubbo接口,同步税盘信息
                        ResponseBaseBean result = HttpInvoiceRequestUtilFg.tbSpxx(OpenApiConfig.tbSpxxFg, tbSpxxParam, OrderInfoEnum.TAX_EQUIPMENT_FGHX.getKey());
                        registrationCode.setSpzt(ConfigureConstant.STRING_1);
                        apiFangGeInterfaceService.saveCodeToRedis(registrationCode);
                    }
        
        
                }
                //获取订单数据
            }
        }else {
            log.warn("{};税盘信息空",LOGGER_MSG);
            return FAIL;
        }
        return SUCCESS;
    }
}
