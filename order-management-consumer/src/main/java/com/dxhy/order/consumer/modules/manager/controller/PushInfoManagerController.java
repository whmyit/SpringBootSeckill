package com.dxhy.order.consumer.modules.manager.controller;


import com.dxhy.order.api.ApiInvoiceCommonService;
import com.dxhy.order.api.ApiPushService;
import com.dxhy.order.constant.ConfigureConstant;
import com.dxhy.order.constant.OrderManagementConstant;
import com.dxhy.order.consumer.annotation.SysLog;
import com.dxhy.order.consumer.modules.user.service.UserInfoService;
import com.dxhy.order.consumer.protocol.usercenter.DeptEntity;
import com.dxhy.order.consumer.protocol.usercenter.UserEntity;
import com.dxhy.order.model.PushInfo;
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
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author ：杨士勇
 * @ClassName ：TaxEquipmentController
 * @Description ：企业推送地址管理控制层
 * @date ：2019年6月18日 下午9:22:54
 */
@RestController
@Api(value = "企业推送地址管理", tags = {"管理模块"})
@RequestMapping(value = "/pushInfoManager")
@Slf4j
public class PushInfoManagerController {
	
	private static final String LOGGER_MSG = "(企业推送地址管理)";

	private static final int PUSH_INFO_AMOUNT_LIMIT = 5;

	@Reference
	private ApiPushService apiPushService;
	
	@Reference
	private ApiInvoiceCommonService apiInvoiceCommonService;
	
	@Resource
	private UserInfoService userInfoService;

	/**
	 *
	 * @param pushInfo
	 * @return
	 */
	@ApiOperation(value = "企业推送地址配置信息列表接口", notes = "推送地址管理-企业推送地址配置信息列表接口")
	@PostMapping("/queryPushInfoList")
	@SysLog(operation = "企业推送地址配置信息列表接口", operationDesc = "企业推送地址配置信息列表接口", key = "推送地址管理")
	public R queryPushInfoList(@RequestBody PushInfo pushInfo) {
		//需要根据权限过滤信息
		log.info("推送地址配置信息列表接口 输入参数:{}", JsonUtils.getInstance().toJsonString(pushInfo));


		List<DeptEntity> deptEntities = userInfoService.getTaxpayerEntityList();

		List<String> shList = new ArrayList<>();
		if(StringUtils.isNotBlank(pushInfo.getNsrsbh())){
			shList.add(pushInfo.getNsrsbh());
		}else{
			for (DeptEntity dept : deptEntities) {
				shList.add(dept.getTaxpayerCode());
			}
			shList = shList.stream().filter(StringUtils::isNotBlank).distinct().collect(Collectors.toList());
		}

		// 0的话查询所有未配置的数据
		boolean isNotCofnig = false;
		if (ConfigureConstant.STRING_0.equals(pushInfo.getInterfaceType())) {
			pushInfo.setInterfaceType("");
			isNotCofnig = true;
		}

		List<PushInfo> pushInfoList = apiPushService.queryPushInfoList(pushInfo, shList);
		List<PushInfo> resultList = new ArrayList<>();
		for (DeptEntity sysDeptEntity : deptEntities) {
			boolean isContain = false;
			for (PushInfo push : pushInfoList) {
				if (push.getNsrsbh().equals(sysDeptEntity.getTaxpayerCode())) {
					isContain = true;
					if (!isNotCofnig) {
						resultList.add(push);
					}
				}
			}
			//如果是未配置
			if (!isContain) {
				if (StringUtils.isNotBlank(pushInfo.getNsrsbh())) {
					if (sysDeptEntity.getTaxpayerCode().equals(pushInfo.getNsrsbh())) {
						PushInfo push = new PushInfo();
						push.setXhfMc(sysDeptEntity.getName());
						push.setNsrsbh(sysDeptEntity.getTaxpayerCode());
						resultList.add(push);
					}

				}else{
					PushInfo push = new PushInfo();
					push.setXhfMc(sysDeptEntity.getName());
					push.setNsrsbh(sysDeptEntity.getTaxpayerCode());
					resultList.add(push);
				}
			}
		}
		return R.ok().put("data", resultList);
	}


	@ApiOperation(value = "单条推送配置查询接口", notes = "推送地址管理-单条推送配置查询接口")
	@PostMapping("/queryPushInfo")
	@SysLog(operation = "单条推送配置查询接口", operationDesc = "单条推送配置查询接口", key = "推送地址管理")
	public R queryPushInfo(@RequestBody PushInfo pushInfo) {
		//需要根据权限过滤信息
		log.info("推送地址配置详细信息查询 输入参数:{}", JsonUtils.getInstance().toJsonString(pushInfo));
		PushInfo push = apiPushService.queryPushInfo(pushInfo);
		return R.ok().put("data",push);
	}
	
	
	/**
	 * 在使用
	 * 更新税控设备接口
	 *
	 * @param pushInfo
	 * @return
	 */
	@ApiOperation(value = "更新推送地址配置信息", notes = "推送地址管理-更新推送地址配置信息")
	@PostMapping("/updatePushInfo")
	@SysLog(operation = "更新推送地址配置信息", operationDesc = "更新推送地址配置信息", key = "推送地址管理")
	public R updatePushInfo(@RequestBody PushInfo pushInfo) {;

		log.info("{}更新推送地址配置信息的接口，入参:{}", LOGGER_MSG,JsonUtils.getInstance().toJsonString(pushInfo));
		UserEntity user = userInfoService.getUser();
		pushInfo.setModifyTime(new Date());
		if(StringUtils.isBlank(pushInfo.getId()) || StringUtils.isBlank(pushInfo.getNsrsbh())){

			log.error("请求参数nsrsbh，id不能为空!");
			return R.error().put(OrderManagementConstant.MESSAGE,"请求参数nsrsbh，id不能为空!");
		}

		PushInfo queryPush = new PushInfo();
		queryPush.setId(pushInfo.getId());
		PushInfo result = apiPushService.queryPushInfo(queryPush);
		if(result == null){
			log.error("更新的数据不存在!");
			return R.error().put(OrderManagementConstant.MESSAGE,"更新的数据不存在!");
		}

		int i = apiPushService.updatePushInfo(pushInfo);
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
	 * @param pushInfo
	 * @return
	 */
	@ApiOperation(value = "添加推送地址配置信息", notes = "推送地址管理-添加推送地址配置信息")
	@PostMapping("/addPushInfo")
	@SysLog(operation = "添加推送地址配置信息", operationDesc = "添加推送地址配置信息", key = "推送地址管理")
	public R addPushInfo(@RequestBody PushInfo pushInfo) {
		
		
		log.info("{}，添加推送地址配置信息,入参:{}", LOGGER_MSG, JsonUtils.getInstance().toJsonString(pushInfo));
		//数据校验
		if (StringUtils.isBlank(pushInfo.getNsrsbh())) {
			log.error("推送地址新增配置，税号不能为空!");
			return R.error().put(OrderManagementConstant.MESSAGE, "税号不能为空");
			
		}
		boolean result = StringUtils.isBlank(pushInfo.getInterfaceType()) || (!"1".equals(pushInfo.getInterfaceType())
				&& !"2".equals(pushInfo.getInterfaceType()) && !"3".equals(pushInfo.getInterfaceType())
				&& !"4".equals(pushInfo.getInterfaceType()) && !"5".equals(pushInfo.getInterfaceType()));
		if (result) {
			log.error("推送地址新增配置，接口类型参数错误!");
			return R.error().put(OrderManagementConstant.MESSAGE, "接口类型参数错误!");
			
		}
		boolean result1 = StringUtils.isBlank(pushInfo.getVersionIdent()) || (!"v1".equals(pushInfo.getVersionIdent())
				&& !"v2".equals(pushInfo.getVersionIdent()) && !"v3".equals(pushInfo.getVersionIdent()))
				&& !"v4".equals(pushInfo.getVersionIdent());
		if (result1) {
			log.error("推送地址新增配置，接口版本号参数错误!");
			return R.error().put(OrderManagementConstant.MESSAGE, "接口版本号参数错误!");
			
		}
		
		//查询当前税号是否已经存在
		PushInfo queryPushInfo = new PushInfo();
		queryPushInfo.setVersionIdent(pushInfo.getVersionIdent());
		queryPushInfo.setInterfaceType(pushInfo.getInterfaceType());
		List<String> shList = new ArrayList<String>();
		shList.add(pushInfo.getNsrsbh());
		List<PushInfo> pushInfos = apiPushService.queryPushInfoList(queryPushInfo, shList);
		if (pushInfos.size() >= PUSH_INFO_AMOUNT_LIMIT) {
			return R.error().put("msg", "当前税号可配置的推送地址已达上限！");
		}

		//获取当前登陆账号
		pushInfo.setCreateTime(new Date());
		pushInfo.setModifyTime(new Date());
		pushInfo.setId(apiInvoiceCommonService.getGenerateShotKey());
		int i = apiPushService.addPushInfo(pushInfo);
		if (i <= 0) {
			return R.error();
		} else {
			return R.ok();
		}
	}

	public static void main(String[] args) {
		PushInfo pushInfo = new PushInfo();
		System.out.println(JsonUtils.getInstance().toJsonStringNullToEmpty(pushInfo));
	}
	
}
