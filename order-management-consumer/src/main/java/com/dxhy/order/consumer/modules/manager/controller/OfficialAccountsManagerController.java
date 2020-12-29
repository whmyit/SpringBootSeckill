package com.dxhy.order.consumer.modules.manager.controller;


import com.dxhy.order.api.ApiAuthenticationService;
import com.dxhy.order.api.ApiEwmGzhConfService;
import com.dxhy.order.api.ApiInvoiceCommonService;
import com.dxhy.order.constant.ConfigureConstant;
import com.dxhy.order.constant.OrderManagementConstant;
import com.dxhy.order.consumer.annotation.SysLog;
import com.dxhy.order.consumer.modules.user.service.UserInfoService;
import com.dxhy.order.consumer.protocol.usercenter.DeptEntity;
import com.dxhy.order.model.EwmGzhConfig;
import com.dxhy.order.model.R;
import com.dxhy.order.utils.JsonUtils;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.dubbo.config.annotation.Reference;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author ：杨士勇
 * @ClassName ：TaxEquipmentController
 * @Description ：税控设备控制层
 * @date ：2019年6月18日 下午9:22:54
 */
@RestController
@Api(value = "公众号配置管理", tags = {"管理模块"})
@RequestMapping(value = "/officialAccountManager")
@Slf4j
public class OfficialAccountsManagerController {
	
	private static final String LOGGER_MSG = "(公众号配置管理)";

	@Reference
	private ApiAuthenticationService apiAuthenticationService;
	
	@Reference
	private ApiInvoiceCommonService apiInvoiceCommonService;
	
	@Reference
	private ApiEwmGzhConfService apiEwmGzhConfService;

	@Resource
	private UserInfoService userInfoService;

	/**
	 *
	 * @return
	 */
	@ApiOperation(value = "公众号配置列表接口", notes = "公众号配置管理-公众号配置列表接口")
	@PostMapping("/queryOfficialAccountList")
	@SysLog(operation = "公众号配置列表接口", operationDesc = "公众号配置列表接口", key = "公众号配置管理")
	public R queryPushInfoList(@RequestBody EwmGzhConfig ewmGzhConfig) {


		//需要根据权限过滤信息
		log.info("公众号配置列表接口, 入参:{}", JsonUtils.getInstance().toJsonString(ewmGzhConfig));
		List<DeptEntity> deptEntities = userInfoService.getTaxpayerEntityList();

		List<String> shList = new ArrayList<>();
		if(StringUtils.isNotBlank(ewmGzhConfig.getNsrsbh())){
			shList.add(ewmGzhConfig.getNsrsbh());
		}else{
			for (DeptEntity dept : deptEntities) {
				shList.add(dept.getTaxpayerCode());
			}
			shList = shList.stream().filter(StringUtils::isNotBlank).distinct().collect(Collectors.toList());
		}
		
		// 0的话查询所有未配置的数据
		boolean isNotCofnig = false;
		if (ConfigureConstant.STRING_0.equals(ewmGzhConfig.getIsConfig())) {
			isNotCofnig = true;
		}
		List<EwmGzhConfig> gzhConfigList = apiEwmGzhConfService.queryEwmGzhConfigList(ewmGzhConfig, shList);
		List<EwmGzhConfig> resultList = new ArrayList<>();
		for (DeptEntity sysDeptEntity : deptEntities) {
			boolean isContain = false;
			for (EwmGzhConfig config : gzhConfigList) {
				if (config.getNsrsbh().equals(sysDeptEntity.getTaxpayerCode())) {
					isContain = true;
					if (!isNotCofnig) {
						resultList.add(config);
					}
				}
			}
			//如果是未配置
			if (!isContain) {
				if (StringUtils.isNotBlank(ewmGzhConfig.getNsrsbh())) {
					if (sysDeptEntity.getTaxpayerCode().equals(ewmGzhConfig.getNsrsbh())) {
						EwmGzhConfig gzhConfig = new EwmGzhConfig();
						gzhConfig.setNsrsbh(sysDeptEntity.getTaxpayerCode());
						resultList.add(gzhConfig);
					}

				}else {
					EwmGzhConfig gzhConfig = new EwmGzhConfig();
					gzhConfig.setNsrsbh(sysDeptEntity.getTaxpayerCode());
					resultList.add(gzhConfig);
				}
			}
		}
		return R.ok().put("data", resultList);


	}


	@ApiOperation(value = "公众号配置查询接口", notes = "公众号配置管理-公众号配置信息查询接口")
	@PostMapping("/queryOfficialAccount")
	@SysLog(operation = "公众号配置信息查询接口", operationDesc = "公众号配置信息查询接口", key = "公众号配置管理")
	public R queryPushInfo(@RequestBody EwmGzhConfig ewmGzhConfig) {
		
		//需要根据权限过滤信息
		log.info("公众号配置信息查询, 入参:{}", JsonUtils.getInstance().toJsonString(ewmGzhConfig));
		
		if (StringUtils.isBlank(ewmGzhConfig.getId()) && StringUtils.isBlank(ewmGzhConfig.getNsrsbh())) {
			log.error("参数错误!");
			return R.error().put(OrderManagementConstant.MESSAGE, "参数错误!");
		}
		EwmGzhConfig result = apiEwmGzhConfService.queryEwmGzhConfInfo(ewmGzhConfig);
		return R.ok().put("data", result);
	}
	
	
	/**
	 * 在使用
	 * 更新税控设备接口
	 *
	 * @param ewmGzhConfig
	 * @return
	 */
	@ApiOperation(value = "更新公众号配置信息", notes = "公众号配置管理-更新公众号配置信息")
	@PostMapping("/updateOfficialAccount")
	@SysLog(operation = "更新公众号配置信息", operationDesc = "更新公众号配置置信息", key = "公众号配置管理")
	public R updatePushInfo(@RequestBody EwmGzhConfig ewmGzhConfig) {
		
		log.info("{}更新公众号配置信息的接口，入参:{}", LOGGER_MSG, JsonUtils.getInstance().toJsonString(ewmGzhConfig));
		if (ewmGzhConfig == null || ewmGzhConfig.getId() == null || ewmGzhConfig.getNsrsbh() == null) {
			log.error("更新公众号配置信息，参数错误!");
			return R.error().put(OrderManagementConstant.MESSAGE, "更新公众号配置信息，参数错误!");
		}
		
		EwmGzhConfig query = new EwmGzhConfig();
		query.setId(ewmGzhConfig.getId());
		EwmGzhConfig result = apiEwmGzhConfService.queryEwmGzhConfInfo(query);
		if (result == null) {
			log.error("此id对应的数据不存在:{}", ewmGzhConfig.getId());
			return R.error().put(OrderManagementConstant.MESSAGE, "此id对应的数据不存在");
		}
		
		int i = apiEwmGzhConfService.updateEwmGzhConfByPrimaryKey(ewmGzhConfig);
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
	 * @param ewmGzhConfig
	 * @return
	 */
	@ApiOperation(value = "添加公众号配置信息", notes = "公众号配置管理-添加公众号配置信息")
	@PostMapping("/addOfficialAccount")
	@SysLog(operation = "添加公众号配置信息", operationDesc = "添加公众号配置信息", key = "公众号配置管理")
	public R addPushInfo(@RequestBody EwmGzhConfig ewmGzhConfig) {

		log.info("{}，添加公众号配置信息,入参:{}", LOGGER_MSG,JsonUtils.getInstance().toJsonString(ewmGzhConfig));

		if(ewmGzhConfig == null || StringUtils.isBlank(ewmGzhConfig.getNsrsbh())){
			log.error("添加公众号配置信息,nsrsbh不能为空!");
			return R.error().put(OrderManagementConstant.MESSAGE, "nsrsbh不能为空!");
		}
		if (StringUtils.isBlank(ewmGzhConfig.getAppid()) || StringUtils.isBlank(ewmGzhConfig.getAppkey())) {
			log.error("appid,appkey不能为空");
			return R.error().put(OrderManagementConstant.MESSAGE, "appid,appkey不能为空!");
		}
		//查询当前税号是否已经存在
		EwmGzhConfig query = new EwmGzhConfig();
		query.setNsrsbh(ewmGzhConfig.getNsrsbh());
		EwmGzhConfig result = apiEwmGzhConfService.queryEwmGzhConfInfo(query);
		if (result != null) {
			return R.error().put(OrderManagementConstant.MESSAGE, "当前税号已经配置过公众号信息!");
			
		}
		//获取当前登陆账号
		ewmGzhConfig.setId(apiInvoiceCommonService.getGenerateShotKey());
		if (StringUtils.isBlank(ewmGzhConfig.getForceSubcribe())) {
			ewmGzhConfig.setForceSubcribe("0");
		}
		int i = apiEwmGzhConfService.addEwmGzhConfInfo(ewmGzhConfig);
		if (i <= 0) {
			return R.error();
		} else {
			return R.ok();
		}
	}

	public static void main(String[] args) {

		EwmGzhConfig config = new EwmGzhConfig();
		System.out.println(JsonUtils.getInstance().toJsonStringNullToEmpty(config));

	}
	
}
