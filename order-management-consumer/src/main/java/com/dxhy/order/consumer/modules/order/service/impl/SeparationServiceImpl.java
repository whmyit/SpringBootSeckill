package com.dxhy.order.consumer.modules.order.service.impl;

import com.dxhy.order.constant.OrderSeparationException;
import com.dxhy.order.constant.TaxSeparateConfig;
import com.dxhy.order.consumer.modules.order.service.ISeparationService;
import com.dxhy.order.model.CommonOrderInfo;
import com.dxhy.order.utils.PriceTaxSeparationUtil;
import org.springframework.stereotype.Component;
/**
 * 价税分离业务层
 *
 * @author ZSC-DXHY
 * @date 创建时间: 2020-08-14 15:35
 */
@Component
public class SeparationServiceImpl implements ISeparationService {
    

    @Override
    public CommonOrderInfo taxSeparationService(CommonOrderInfo commonOrderInfo) throws OrderSeparationException {
        return PriceTaxSeparationUtil.taxSeparationService(commonOrderInfo,new TaxSeparateConfig());
    }


}
