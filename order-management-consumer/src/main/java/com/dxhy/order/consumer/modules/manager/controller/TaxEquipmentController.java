package com.dxhy.order.consumer.modules.manager.controller;


import com.alibaba.fastjson.JSON;
import com.dxhy.order.api.ApiInvoiceCommonService;
import com.dxhy.order.api.ApiTaxEquipmentService;
import com.dxhy.order.constant.ConfigureConstant;
import com.dxhy.order.constant.OrderInfoEnum;
import com.dxhy.order.constant.OrderManagementConstant;
import com.dxhy.order.consumer.annotation.SysLog;
import com.dxhy.order.consumer.modules.user.service.UserInfoService;
import com.dxhy.order.consumer.protocol.usercenter.DeptEntity;
import com.dxhy.order.consumer.protocol.usercenter.UserEntity;
import com.dxhy.order.model.R;
import com.dxhy.order.model.TaxEquipmentInfo;
import com.dxhy.order.utils.JsonUtils;
import com.dxhy.order.utils.NsrsbhUtils;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.dubbo.config.annotation.Reference;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @author ：杨士勇
 * @ClassName ：TaxEquipmentController
 * @Description ：税控设备控制层
 * @date ：2019年6月18日 下午9:22:54
 */
@RestController
@Api(value = "税控设备", tags = {"管理模块"})
@RequestMapping(value = "/taxManager")
@Slf4j
public class TaxEquipmentController {
	
	private static final String LOGGER_MSG = "(税控设备管理)";
	
	@Reference
	private ApiTaxEquipmentService apiTaxEquipmentService;
	
	@Reference
	private ApiInvoiceCommonService apiInvoiceCommonService;
	
	@Resource
	private UserInfoService userInfoService;
	
	
	/**
	 * 在使用
	 * 税控设备列表属于运维管理界面,不能对客户进行开放
	 * 获取税控设备列表
	 *
	 * @param taxEquipmentInfo
	 * @param type
	 * @return
	 */
	@ApiOperation(value = "查询税控设备信息", notes = "税控设备管理-查询税控设备信息")
	@PostMapping("/queryTaxEquipment")
	@SysLog(operation = "税控设备查询rest接口", operationDesc = "查询税控设备信息接口", key = "税控查询")
	public R queryTaxEquipment(@RequestBody TaxEquipmentInfo taxEquipmentInfo, @RequestParam("type") String type) {
        //需要根据权限过滤信息
        log.info("税控设备查询 输入参数:{}", JsonUtils.getInstance().toJsonString(taxEquipmentInfo));
        
        if (ConfigureConstant.STRING_1.equals(type)) {
	        //单条查询
	        List<String> shList = NsrsbhUtils.transShListByNsrsbh(taxEquipmentInfo.getXhfNsrsbh());
	        List<TaxEquipmentInfo> equipmentList = apiTaxEquipmentService.queryTaxEquipmentList(taxEquipmentInfo, shList);
	        return R.ok().put("data", equipmentList.get(0));
	
        } else {
	        List<DeptEntity> deptEntities = userInfoService.getTaxpayerEntityList();
	
	        List<String> shList = new ArrayList<>();
	        for (DeptEntity dept : deptEntities) {
		        shList.add(dept.getTaxpayerCode());
	        }
	        shList = shList.stream().filter(StringUtils::isNotBlank).distinct().collect(Collectors.toList());

	        // 0 的话查询所有未关联的设备
			boolean  isNotConfig = false;
	        if (ConfigureConstant.STRING_0.equals(taxEquipmentInfo.getSksbCode())) {
		        taxEquipmentInfo.setSksbCode("");
				isNotConfig = true;
	        }
    
            List<TaxEquipmentInfo> equipmentList = apiTaxEquipmentService.queryTaxEquipmentList(taxEquipmentInfo, shList);
            List<TaxEquipmentInfo> resultList = new ArrayList<>();
	
	        for (DeptEntity sysDeptEntity : deptEntities) {
		        boolean isContain = false;
		        for (TaxEquipmentInfo equipment : equipmentList) {
			        if (equipment.getXhfNsrsbh().equals(sysDeptEntity.getTaxpayerCode())) {
				        isContain = true;
				        if (!isNotConfig) {
					        resultList.add(equipment);
				        }
			        }
		        }
		        //如果是未配置
				if (!isContain) {
					if (StringUtils.isNotBlank(taxEquipmentInfo.getXhfMc())) {
						if (sysDeptEntity.getName().equals(taxEquipmentInfo.getXhfMc())) {
							TaxEquipmentInfo equipment = new TaxEquipmentInfo();
							equipment.setXhfMc(sysDeptEntity.getName());
							equipment.setXhfNsrsbh(sysDeptEntity.getTaxpayerCode());
							resultList.add(equipment);
						}
						
					} else if (isNotConfig) {
						//如果添加了税控设备过滤条件 则未配置的不显示9
						
					} else {
						TaxEquipmentInfo equipment = new TaxEquipmentInfo();
						equipment.setXhfMc(sysDeptEntity.getName());
						equipment.setXhfNsrsbh(sysDeptEntity.getTaxpayerCode());
						resultList.add(equipment);
					}
				}
			}
			return R.ok().put("data", resultList);
			
		}
		
	}
	
	
	/**
	 * 在使用
	 * 更新税控设备接口
	 *
	 * @param taxEquipmentInfo
	 * @return
	 */
	@ApiOperation(value = "更新税控设备信息", notes = "税控设备管理-更新税控设备信息")
	@PostMapping("/updateTaxEquipment")
	@SysLog(operation = "更新税控设备rest接口", operationDesc = "更新税控设备信息接口", key = "税控设备更新")
	public R updateTaxEquipment(@RequestBody TaxEquipmentInfo taxEquipmentInfo) {
		log.info("{}", LOGGER_MSG);
		UserEntity user = userInfoService.getUser();
		
		taxEquipmentInfo.setUpdateUserId(user.getUserId() == null ? "" : String.valueOf(user.getUserId()));
		taxEquipmentInfo.setUpdateTime(new Date());
		
		int i = apiTaxEquipmentService.updateTaxEquipment(taxEquipmentInfo);
		if (i > 0) {
			return R.ok();
		} else {
			return R.error();
		}
	}
	
	
	/**
	 * 在使用
	 * 添加税控设备接口
	 *
	 * @param taxEquipmentInfo
	 * @return
	 */
	@ApiOperation(value = "添加税控设备信息", notes = "税控设备管理-添加税控设备信息")
	@PostMapping("/addTaxEquipment")
	@SysLog(operation = "添加税控设备rest接口", operationDesc = "添加税控设备信息接口", key = "税控添加")
	public R addTaxEquipment(@RequestBody TaxEquipmentInfo taxEquipmentInfo) {
		log.info("{}", LOGGER_MSG);
		//查询当前税号是否已经存在
		TaxEquipmentInfo queryTaxEquip = new TaxEquipmentInfo();
		queryTaxEquip.setXhfNsrsbh(taxEquipmentInfo.getXhfNsrsbh());
		List<String> shList = NsrsbhUtils.transShListByNsrsbh(taxEquipmentInfo.getXhfNsrsbh());
		List<TaxEquipmentInfo> queryTaxEquipment = apiTaxEquipmentService.queryTaxEquipmentList(queryTaxEquip, shList);
		if (!queryTaxEquipment.isEmpty()) {
			return R.error().put(OrderManagementConstant.CODE, "9000").put("msg", "当前税号已经配置税控设备！");
			
		}
		//获取当前登陆账号
		UserEntity user = userInfoService.getUser();
		DeptEntity department = userInfoService.getDepartment();
		taxEquipmentInfo.setCreateUserId(user.getUserId() == null ? "" : String.valueOf(user.getUserId()));
		taxEquipmentInfo.setGroupId(user.getDeptId() == null ? "" : String.valueOf(user.getDeptId()));
		taxEquipmentInfo.setGroupName(department.getName());
		taxEquipmentInfo.setCreateTime(new Date());
		taxEquipmentInfo.setUpdateTime(new Date());
		taxEquipmentInfo.setLinkTime(new Date());
		
		taxEquipmentInfo.setDeleted(OrderInfoEnum.DATE_DELETE_STATUS_0.getKey());
		taxEquipmentInfo.setId(apiInvoiceCommonService.getGenerateShotKey());
		int i = apiTaxEquipmentService.addTaxEquipment(taxEquipmentInfo);
		if (i <= 0) {
			return R.error();
		} else {
			return R.ok();
		}
	}
	
	
	/**
	 * 在使用
	 * 税控设备分组接口,按照C48,A9等
	 *
	 * @param paramlist
	 * @return
	 */
	@ApiOperation(value = "分组查询税控设备信息", notes = "税控设备管理-分组查询税控设备信息")
	@PostMapping("/groupByTaxEquipment")
	@SysLog(operation = "税控设备分组rest接口", operationDesc = "税控设备信息分组接口", key = "税控分组")
	public R groupByTaxEquipment(@RequestBody String paramlist) {
        List<String> shList = new ArrayList<>();
        List<Map> paramlist1 = JSON.parseArray(paramlist).toJavaList(Map.class);
        for (Map map : paramlist1) {
            shList.add(String.valueOf(map.get("taxpayerCode")));
        }
        //bean组装
        List<TaxEquipmentInfo> queryTaxEquipmentList = apiTaxEquipmentService.queryTaxEquipmentList(new TaxEquipmentInfo(), shList);
        
        log.debug("根据税号查询到的税控设备:{}", JsonUtils.getInstance().toJsonString(queryTaxEquipmentList));
        
        Map<String, List<Map>> resultMap = new HashMap<>(10);
        
        for (Map map : paramlist1) {
            boolean b = false;
            for (TaxEquipmentInfo equipment : queryTaxEquipmentList) {
                if (String.valueOf(map.get("taxpayerCode")).equals(equipment.getXhfNsrsbh())) {
                    if (resultMap.get(equipment.getSksbCode()) == null) {
                        List<Map> list = new ArrayList<>();
                        list.add(map);
                        resultMap.put(equipment.getSksbCode(), list);
                        
                    } else {
						List<Map> list = resultMap.get(equipment.getSksbCode());
						list.add(map);
						resultMap.put(equipment.getSksbCode(), list);
						
					}
					b = true;
					continue;
					
				}
			}
			if (!b) {
				if (resultMap.get(OrderInfoEnum.TAX_EQUIPMENT_UNKNOW.getKey()) == null) {
                    //未配置
                    List<Map> list = new ArrayList<>();
                    list.add(map);
                    resultMap.put(OrderInfoEnum.TAX_EQUIPMENT_UNKNOW.getKey(), list);
                } else {
					List<Map> list = resultMap.get(OrderInfoEnum.TAX_EQUIPMENT_UNKNOW.getKey());
					list.add(map);
					resultMap.put(OrderInfoEnum.TAX_EQUIPMENT_UNKNOW.getKey(), list);
				}
				
			}
			
		}
		return R.ok().put("data", resultMap);
	}
	
}
