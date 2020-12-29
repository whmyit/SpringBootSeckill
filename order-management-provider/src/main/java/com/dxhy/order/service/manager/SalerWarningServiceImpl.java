package com.dxhy.order.service.manager;

import com.dxhy.order.api.ApiInvoiceCommonService;
import com.dxhy.order.api.SalerWarningService;
import com.dxhy.order.dao.SalerWarningMapper;
import com.dxhy.order.model.SalerWarning;
import lombok.extern.slf4j.Slf4j;
import org.apache.dubbo.config.annotation.Service;

import javax.annotation.Resource;
import java.util.Date;
import java.util.List;

/**
 * 销方预警
 *
 * @author lizy
 */
@Slf4j
@Service
public class SalerWarningServiceImpl implements SalerWarningService {
	@Resource
	private SalerWarningMapper salerWarningMapper;
	
	@Resource
	private ApiInvoiceCommonService apiInvoiceCommonService;
	
	@Override
	public void addSalerWarning(SalerWarning record) {
		//查询对应数据是否存在
		List<SalerWarning> salerWarningList = selectSalerWaringByNsrsbh(record.getXhfNsrsbh(), record.getCreateId());
		if (salerWarningList != null && salerWarningList.size() > 0) {
			
			update(record);
		} else {
			Date date = new Date();
			record.setCreateTime(date);
			record.setUpdateTime(date);
			record.setId(apiInvoiceCommonService.getGenerateShotKey());
			salerWarningMapper.insertSelective(record);
		}
		
		
	}

	@Override
	public void update(SalerWarning record) {
		Date date=new Date();
		record.setUpdateTime(date);
		salerWarningMapper.updateByTaxCode(record);

	}
	
	@Override
	public List<SalerWarning> selectSalerWaringByNsrsbh(String nsrsbh, String createId) {
		return salerWarningMapper.selectSalerWaringByNsrsbh(nsrsbh, createId);
	}

}
