package com.dxhy.order.service.impl;

import com.dxhy.invoice.protocol.sl.sld.SldJspxxRequest;
import com.dxhy.invoice.protocol.sl.sld.SldJspxxResponse;
import com.dxhy.invoice.service.sl.SldManagerService;
import com.dxhy.order.constant.OrderInfoEnum;
import com.dxhy.order.service.UnifyService;
import lombok.extern.slf4j.Slf4j;
import org.apache.dubbo.config.annotation.Reference;
import org.springframework.stereotype.Service;

/**
 *
 *
 * @ClassName ：UnifyServiceImpl
 * @Description ：与底层交互接口统一调用 输入输出参数的适配在此servcie层实现
 * @author ：杨士勇
 * @date ：2019年6月1日 下午2:35:38
 *
 *
 */

@Service
@Slf4j
public class UnifyServiceImpl implements UnifyService {
	
	@Reference(retries = 0)
	private SldManagerService sldManagerService;

	/**
	 * 获取金税盘信息的接口
	 */
	@Override
	public SldJspxxResponse selectSldJspxx(SldJspxxRequest sldJspxxRequest, String terminalCode) {
		SldJspxxResponse reponse = new SldJspxxResponse();

		if (!OrderInfoEnum.TAX_EQUIPMENT_C48.getKey().equals(terminalCode)) {
		
		} else {
			// c48
			reponse = sldManagerService.selectSldJspxx(sldJspxxRequest);

		}
		return reponse;
	}

}
