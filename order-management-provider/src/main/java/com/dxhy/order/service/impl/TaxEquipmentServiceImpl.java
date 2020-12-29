package com.dxhy.order.service.impl;

import com.dxhy.order.api.ApiTaxEquipmentService;
import com.dxhy.order.constant.OrderInfoEnum;
import com.dxhy.order.dao.TaxEquipmentInfoMapper;
import com.dxhy.order.model.TaxEquipmentInfo;
import org.apache.dubbo.config.annotation.Service;

import javax.annotation.Resource;
import java.util.List;

/**
 * 税控设备类型业务层
 *
 * @author ZSC-DXHY
 * @date 创建时间: 2020-08-14 15:44
 */
@Service
public class TaxEquipmentServiceImpl implements ApiTaxEquipmentService {
	
	@Resource
	private TaxEquipmentInfoMapper taxEquipmentInfoMapper;
	
	@Override
	public int updateTaxEquipment(TaxEquipmentInfo taxEquipmentInfo) {
		return taxEquipmentInfoMapper.updateTaxEquipment(taxEquipmentInfo);
	}

	@Override
	public int addTaxEquipment(TaxEquipmentInfo taxEquipmentInfo) {
		return taxEquipmentInfoMapper.insertTaxEquipment(taxEquipmentInfo);
	}

	@Override
	public List<TaxEquipmentInfo> queryTaxEquipmentList(TaxEquipmentInfo taxEquipmentInfo, List<String> shList) {
		return taxEquipmentInfoMapper.queryTaxEquipmentList(taxEquipmentInfo,shList);
	}
    
    @Override
    public String getTerminalCode(String nsrsbh) {
        String terminalCode = OrderInfoEnum.TAX_EQUIPMENT_C48.getKey();
        TaxEquipmentInfo taxEquipmentInfo = taxEquipmentInfoMapper.selectByNsrsbh(nsrsbh);
        if (taxEquipmentInfo != null) {
            terminalCode = taxEquipmentInfo.getSksbCode();
        }
        return terminalCode;
    }
	

}
