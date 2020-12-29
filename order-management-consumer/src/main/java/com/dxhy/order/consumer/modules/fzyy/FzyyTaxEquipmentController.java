//package com.dxhy.order.consumer.modules.fzyy;
//
//
//import cn.hutool.core.util.ObjectUtil;
//import com.alibaba.fastjson.JSON;
//import com.dxhy.order.api.ApiInvoiceCommonService;
//import com.dxhy.order.api.ApiTaxEquipmentService;
//import com.dxhy.order.constant.ConfigureConstant;
//import com.dxhy.order.constant.OrderInfoEnum;
//import com.dxhy.order.constant.OrderManagementConstant;
//import com.dxhy.order.consumer.config.OpenApiConfig;
//import com.dxhy.order.consumer.modules.user.service.UserInfoService;
//import com.dxhy.order.consumer.protocol.usercenter.DeptEntity;
//import com.dxhy.order.consumer.protocol.usercenter.PushSksb;
//import com.dxhy.order.consumer.protocol.usercenter.Sksb;
//import com.dxhy.order.exceptions.OrderReceiveException;
//import com.dxhy.order.model.R;
//import com.dxhy.order.model.TaxEquipmentInfo;
//import com.dxhy.order.utils.HttpUtils;
//import com.dxhy.order.utils.JsonUtils;
//import com.dxhy.order.utils.NsrsbhUtils;
//import com.google.common.collect.Lists;
//import io.swagger.annotations.Api;
//import io.swagger.annotations.ApiOperation;
//import lombok.extern.slf4j.Slf4j;
//import org.apache.commons.lang3.StringUtils;
//import org.apache.dubbo.config.annotation.Reference;
//import org.apache.http.entity.ContentType;
//import org.springframework.web.bind.annotation.*;
//
//import javax.annotation.Resource;
//import java.util.*;
//import java.util.stream.Collectors;
//
///**
// * todo
// *
// * @author ：杨士勇
// * @ClassName ：TaxEquipmentController
// * @Description ：辅助运营使用税控设备控制层
// * @date ：2019年6月18日 下午9:22:54
// */
//@SuppressWarnings("AlibabaCommentsMustBeJavadocFormat")
//@RestController
//@Api(value = "税控设备", tags = {"管理模块"})
//@RequestMapping(value = "/fzyyTaxManager")
//@Slf4j
//public class FzyyTaxEquipmentController {
//
//	private static final String LOGGER_MSG = "(辅助运营税控设备管理)";
//
//	@Reference
//	private ApiTaxEquipmentService apiTaxEquipmentService;
//
//	@Reference
//	private ApiInvoiceCommonService apiInvoiceCommonService;
//
//	@Resource
//	private UserInfoService userInfoService;
//
//	/**
//	 * 在使用
//	 * 辅助运营查询税号列表
//	 *
//	 * @return
//	 */
//	@ApiOperation(value = "辅助运营查询税号列表", notes = "税控设备管理-辅助运营查询税号列表")
//	@PostMapping("/queryTaxpayerListByToken")
//	public R queryTaxpayerListByToken() {
//
//		/**
//		 * 通过token调用辅助运营接口获取用户所有数据
//		 */
//		List<DeptEntity> totalTaxList = null;
//		try {
//			totalTaxList = userInfoService.getFzyyTaxpayerEntityList();
//		} catch (OrderReceiveException e) {
//			return R.error(e.getCode(), e.getMessage());
//		}
//
//		return R.ok().put(OrderManagementConstant.DATA, totalTaxList);
//	}
//
//	/**
//	 * 在使用
//	 * 税控设备列表属于运维管理界面,不能对客户进行开放
//	 * 获取税控设备列表
//	 *
//	 * @param taxEquipmentInfo
//	 * @param type
//	 * @return
//	 */
//	@ApiOperation(value = "查询税控设备信息", notes = "税控设备管理-查询税控设备信息")
//	@PostMapping("/queryTaxEquipment")
//	public R queryTaxEquipment(@RequestBody TaxEquipmentInfo taxEquipmentInfo, @RequestParam("type") String type) {
//		//需要根据权限过滤信息
//		log.info("税控设备查询 输入参数:{}", JsonUtils.getInstance().toJsonString(taxEquipmentInfo));
//
//
//		List<DeptEntity> deptEntities = null;
//		try {
//			deptEntities = userInfoService.getFzyyTaxpayerEntityList();
//		} catch (OrderReceiveException e) {
//			return R.error(e.getCode(), e.getMessage());
//		}
//
//		List<String> shList = new ArrayList<>();
//		if (StringUtils.isNotBlank(taxEquipmentInfo.getXhfNsrsbh())) {
//			shList = NsrsbhUtils.transShListByNsrsbh(taxEquipmentInfo.getXhfNsrsbh());
//		} else {
//			for (DeptEntity dept : deptEntities) {
//				shList.add(dept.getTaxpayerCode());
//			}
//		}
//
//		shList = shList.stream().filter(StringUtils::isNotBlank).distinct().collect(Collectors.toList());
//		// 0 的话查询所有未关联的设备
//		if (ConfigureConstant.STRING_0.equals(taxEquipmentInfo.getSksbCode())) {
//			taxEquipmentInfo.setSksbCode("");
//		}
//
//		List<TaxEquipmentInfo> equipmentList = apiTaxEquipmentService.queryTaxEquipmentList(taxEquipmentInfo, shList);
//		List<TaxEquipmentInfo> resultList = new ArrayList<>();
//
//		for (DeptEntity sysDeptEntity : deptEntities) {
//			boolean isContain = false;
//			for (TaxEquipmentInfo equipment : equipmentList) {
//				if (equipment.getXhfNsrsbh().equals(sysDeptEntity.getTaxpayerCode())) {
//					isContain = true;
//					if (!ConfigureConstant.STRING_0.equals(taxEquipmentInfo.getSksbCode())) {
//						resultList.add(equipment);
//					}
//				}
//			}
//			//如果是未配置
//			if (!isContain) {
//				/**
//				 * 如果税号不为空,说明筛选条件带销方税号
//				 */
//				if (StringUtils.isNotBlank(taxEquipmentInfo.getXhfNsrsbh())) {
//					if (sysDeptEntity.getTaxpayerCode().equals(taxEquipmentInfo.getXhfNsrsbh())) {
//						TaxEquipmentInfo equipment = new TaxEquipmentInfo();
//						equipment.setXhfMc(sysDeptEntity.getName());
//						equipment.setXhfNsrsbh(sysDeptEntity.getTaxpayerCode());
//						resultList.add(equipment);
//					}
//
//				} else if (StringUtils.isNotBlank(taxEquipmentInfo.getSksbCode())) {
//					//如果添加了税控设备过滤条件 则未配置的不显示9
//
//				} else {
//					TaxEquipmentInfo equipment = new TaxEquipmentInfo();
//					equipment.setXhfMc(sysDeptEntity.getName());
//					equipment.setXhfNsrsbh(sysDeptEntity.getTaxpayerCode());
//					resultList.add(equipment);
//				}
//			}
//		}
//		return R.ok().put("data", resultList);
//
//	}
//
//
//	/**
//	 * 在使用
//	 * 更新税控设备接口
//	 *
//	 * @param taxEquipmentInfo
//	 * @return
//	 */
//	@ApiOperation(value = "更新税控设备信息", notes = "税控设备管理-更新税控设备信息")
//	@PostMapping("/updateTaxEquipment")
//	public R updateTaxEquipment(@RequestBody TaxEquipmentInfo taxEquipmentInfo) {
//		log.info("{}", LOGGER_MSG);
//
//		taxEquipmentInfo.setUpdateUserId("");
//		taxEquipmentInfo.setUpdateTime(new Date());
//
//		//查询当前税号是否已经存在
//		TaxEquipmentInfo queryTaxEquip = new TaxEquipmentInfo();
//		queryTaxEquip.setXhfNsrsbh(taxEquipmentInfo.getXhfNsrsbh());
//		List<String> shList = NsrsbhUtils.transShListByNsrsbh(taxEquipmentInfo.getXhfNsrsbh());
//		List<TaxEquipmentInfo> queryTaxEquipment = apiTaxEquipmentService.queryTaxEquipmentList(queryTaxEquip, shList);
//		if (ObjectUtil.isEmpty(queryTaxEquipment)) {
//			return addTaxEquipment(taxEquipmentInfo);
//		} else {
//			int i = apiTaxEquipmentService.updateTaxEquipment(taxEquipmentInfo);
//			if (i > 0) {
//				/**
//				 * 更新成功后,调用用户中心接口,同步税控设备信息
//				 */
//				pushTaxEquipment(taxEquipmentInfo);
//
//				return R.ok();
//			} else {
//				return R.error();
//			}
//		}
//	}
//
//
//	/**
//	 * 在使用
//	 * 添加税控设备接口
//	 *
//	 * @param taxEquipmentInfo
//	 * @return
//	 */
//	@ApiOperation(value = "添加税控设备信息", notes = "税控设备管理-添加税控设备信息")
//	@PostMapping("/addTaxEquipment")
//	public R addTaxEquipment(@RequestBody TaxEquipmentInfo taxEquipmentInfo) {
//		log.info("{}", LOGGER_MSG);
//		//查询当前税号是否已经存在
//		TaxEquipmentInfo queryTaxEquip = new TaxEquipmentInfo();
//		queryTaxEquip.setXhfNsrsbh(taxEquipmentInfo.getXhfNsrsbh());
//		List<String> shList = NsrsbhUtils.transShListByNsrsbh(taxEquipmentInfo.getXhfNsrsbh());
//		List<TaxEquipmentInfo> queryTaxEquipment = apiTaxEquipmentService.queryTaxEquipmentList(queryTaxEquip, shList);
//		if (!queryTaxEquipment.isEmpty()) {
//			return R.error().put(OrderManagementConstant.CODE, "9000").put("msg", "当前税号已经配置税控设备！");
//
//		}
//		//获取当前登陆账号
//		taxEquipmentInfo.setCreateUserId("");
//		taxEquipmentInfo.setGroupId("");
//		taxEquipmentInfo.setGroupName("");
//		taxEquipmentInfo.setCreateTime(new Date());
//		taxEquipmentInfo.setUpdateTime(new Date());
//		taxEquipmentInfo.setLinkTime(new Date());
//		taxEquipmentInfo.setDeleted(OrderInfoEnum.DATE_DELETE_STATUS_0.getKey());
//		taxEquipmentInfo.setId(apiInvoiceCommonService.getGenerateShotKey());
//		int i = apiTaxEquipmentService.addTaxEquipment(taxEquipmentInfo);
//		if (i <= 0) {
//			return R.error();
//		} else {
//			pushTaxEquipment(taxEquipmentInfo);
//			return R.ok();
//		}
//	}
//
//
//	/**
//	 * 在使用
//	 * 税控设备分组接口,按照C48,A9等
//	 *
//	 * @param paramlist
//	 * @return
//	 */
//	@ApiOperation(value = "分组查询税控设备信息", notes = "税控设备管理-分组查询税控设备信息")
//	@PostMapping("/groupByTaxEquipment")
//	public R groupByTaxEquipment(@RequestBody String paramlist) {
//		List<String> shList = new ArrayList<>();
//		List<Map> paramlist1 = JSON.parseArray(paramlist).toJavaList(Map.class);
//		for (Map map : paramlist1) {
//			shList.add(String.valueOf(map.get("taxpayerCode")));
//		}
//		//bean组装
//		List<TaxEquipmentInfo> queryTaxEquipmentList = apiTaxEquipmentService.queryTaxEquipmentList(new TaxEquipmentInfo(), shList);
//
//		log.debug("根据税号查询到的税控设备:{}", JsonUtils.getInstance().toJsonString(queryTaxEquipmentList));
//
//		Map<String, List<Map>> resultMap = new HashMap<>(10);
//
//		for (Map map : paramlist1) {
//			boolean b = false;
//			for (TaxEquipmentInfo equipment : queryTaxEquipmentList) {
//				if (String.valueOf(map.get("taxpayerCode")).equals(equipment.getXhfNsrsbh())) {
//					if (resultMap.get(equipment.getSksbCode()) == null) {
//						List<Map> list = new ArrayList<>();
//						list.add(map);
//						resultMap.put(equipment.getSksbCode(), list);
//
//					} else {
//						List<Map> list = resultMap.get(equipment.getSksbCode());
//						list.add(map);
//						resultMap.put(equipment.getSksbCode(), list);
//
//					}
//					b = true;
//					continue;
//
//				}
//			}
//			if (!b) {
//				if (resultMap.get(OrderInfoEnum.TAX_EQUIPMENT_UNKNOW.getKey()) == null) {
//					//未配置
//					List<Map> list = new ArrayList<>();
//					list.add(map);
//					resultMap.put(OrderInfoEnum.TAX_EQUIPMENT_UNKNOW.getKey(), list);
//				} else {
//					List<Map> list = resultMap.get(OrderInfoEnum.TAX_EQUIPMENT_UNKNOW.getKey());
//					list.add(map);
//					resultMap.put(OrderInfoEnum.TAX_EQUIPMENT_UNKNOW.getKey(), list);
//				}
//
//			}
//
//		}
//		return R.ok().put("data", resultMap);
//	}
//
//	public static void pushTaxEquipment(TaxEquipmentInfo taxEquipmentInfo) {
//		try {
//			/**
//			 * 更新成功后,调用用户中心接口,同步税控设备信息
//			 */
//			PushSksb pushSksb = new PushSksb();
//			Sksb sksb = new Sksb();
//			sksb.setSksbbm(taxEquipmentInfo.getSksbCode());
//			sksb.setSksbmc(taxEquipmentInfo.getSksbName());
//			List<Sksb> sksbs = Lists.newArrayList();
//			sksbs.add(sksb);
//			pushSksb.setXhfNsrsbh(taxEquipmentInfo.getXhfNsrsbh());
//			pushSksb.setXhfMc(taxEquipmentInfo.getXhfMc());
//			pushSksb.setSksb(sksbs);
//			Map<String, String> headMap = new HashMap<>(2);
//			headMap.put("Content-Type", ContentType.APPLICATION_JSON.toString());
//			log.debug("{}推送用户中心税控设备请求信息为:{}", LOGGER_MSG, JsonUtils.getInstance().toJsonString(pushSksb));
//			String result = HttpUtils.doPostWithHeader(OpenApiConfig.pushTaxEquipment, JsonUtils.getInstance().toJsonString(pushSksb), headMap);
//			log.debug("{}推送用户中心税控设备返回信息为:{}", LOGGER_MSG, result);
//		} catch (Exception e) {
//			log.error("{}推送用户中心税控设备异常:{}", LOGGER_MSG, e);
//		}
//
//
//	}
////
////	public static void main(String[] args) {
////		TaxEquipmentInfo taxEquipmentInfo = new TaxEquipmentInfo();
////		taxEquipmentInfo.setXhfNsrsbh("15000120561127953X");
////		taxEquipmentInfo.setXhfMc("测试税盘公司");
////		taxEquipmentInfo.setSksbCode("001");
////		taxEquipmentInfo.setSksbName("金税盘托管");
////		pushTaxEquipment(taxEquipmentInfo);
////	}
//}
