package com.dxhy.order.consumer.modules.manager.controller;


import com.alibaba.fastjson.JSON;
import com.dxhy.order.api.ApiAuthenticationService;
import com.dxhy.order.api.ApiInvoiceCommonService;
import com.dxhy.order.api.ApiTaxEquipmentService;
import com.dxhy.order.constant.ConfigureConstant;
import com.dxhy.order.constant.OrderInfoEnum;
import com.dxhy.order.constant.OrderManagementConstant;
import com.dxhy.order.consumer.annotation.SysLog;
//import com.dxhy.order.consumer.modules.fzyy.service.IFzyyService;
import com.dxhy.order.consumer.modules.user.service.UserInfoService;
import com.dxhy.order.consumer.protocol.usercenter.DeptEntity;
import com.dxhy.order.model.*;
import com.dxhy.order.model.vo.CountBySldVO;
import com.dxhy.order.utils.JsonUtils;
import com.dxhy.order.utils.NsrsbhUtils;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.dubbo.config.annotation.Reference;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.*;
import java.util.stream.Collectors;

/**
 * 企业配置信息管理
 *
 * @author 杨士勇
 * @date 创建时间: 2020-08-21 8:50
 */
@RestController
@Api(value = "企业信息配置管理", tags = {"管理模块"})
@RequestMapping(value = "/enterPriseConfigManager")
@Slf4j
public class AuthInfoManagerController {
	
	private static final String LOGGER_MSG = "(企业配置信息配置管理)";

	@Reference
	private ApiAuthenticationService apiAuthenticationService;
	
	@Reference
	private ApiInvoiceCommonService apiInvoiceCommonService;

/*	@Resource
	private IFzyyService fzyyService;*/

	@Reference
	private ApiTaxEquipmentService apiTaxEquipmentService;

	@Resource
	private UserInfoService userInfoService;

	/**
	 *
	 * @param nsrsbh
	 * @return
	 */
	@ApiOperation(value = "企业信息配置列表接口", notes = "企业信息配置管理-企业信息配置列表接口")
	@PostMapping("/queryConfigInfoList")
	@SysLog(operation = "企业信息配置列表接口", operationDesc = "企业信息配置列表接口", key = "企业信息配置管理")
	public R queryConfigInfoList(
			@ApiParam(name = "nsrsbh", value = "纳税人识别号", required = false) @RequestParam(value = "nsrsbh", required = false) String nsrsbh,
			@ApiParam(name = "pageSize", value = "每页条数", required = false) @RequestParam(value = "pageSize", required = false) String pageSize,
			@ApiParam(name = "currentPage", value = "当前页", required = false) @RequestParam(value = "currentPage", required = false) String currentPage) {

		log.info("企业信息配置列表接口, 入参,nsrsbh:{},currentPage:{},pageSize:{}", nsrsbh,currentPage,pageSize);
		try {
			List<String> shList = new ArrayList<>();
			if(StringUtils.isNotBlank(nsrsbh)){
                shList.add(nsrsbh);
                AuthenticationInfo authInfo = new AuthenticationInfo();

				List<AuthenticationInfo> authenticationInfos = apiAuthenticationService.queryAuthenInfoList(authInfo, shList);

					List<DeptEntity> deptEntities = userInfoService.getTaxpayerEntityList();
					for(DeptEntity deptEntity : deptEntities){
						if(nsrsbh.equals(deptEntity.getTaxpayerCode())){

							if(CollectionUtils.isEmpty(authenticationInfos)){
								authenticationInfos  = new ArrayList<>();
								AuthenticationInfo authenticationInfo = new AuthenticationInfo();
								authenticationInfo.setNsrsbh(deptEntity.getTaxpayerCode());
								authenticationInfo.setXhfMc(deptEntity.getName());
								authenticationInfos.add(authenticationInfo);
								break;
							}else{
								for(AuthenticationInfo authenticationInfo : authenticationInfos){
									authenticationInfo.setXhfMc(deptEntity.getName());
								}
								break;
							}
						}
					}
				PageUtils page = new PageUtils(authenticationInfos, authenticationInfos.size(), 1, 1);
				return R.ok().put("data", page);
            }else {

				List<DeptEntity> deptEntities = userInfoService.getTaxpayerEntityList();
                for (DeptEntity dept : deptEntities) {
                    shList.add(dept.getTaxpayerCode());
                }
                AuthenticationInfo authInfo = new AuthenticationInfo();
                shList = shList.stream().filter(StringUtils::isNotBlank).distinct().collect(Collectors.toList());
                List<AuthenticationInfo> authenticationInfos = apiAuthenticationService.queryAuthenInfoList(authInfo, shList);

                //查询企业配置信息列表
                List<AuthenticationInfo> resultList = new ArrayList<>();
                for (DeptEntity sysDeptEntity : deptEntities) {
                    boolean isContain = false;
                    for (AuthenticationInfo auth : authenticationInfos) {
                        if (sysDeptEntity.getTaxpayerCode().equals(String.valueOf(auth.getNsrsbh()))) {
                            auth.setXhfMc(sysDeptEntity.getName());
                            isContain = true;
                            resultList.add(auth);
                        }
                    }
                    //如果是未配置
                    if (!isContain) {
                        AuthenticationInfo authenticationInfo = new AuthenticationInfo();
                        authenticationInfo.setXhfMc(sysDeptEntity.getName());
                        authenticationInfo.setNsrsbh(sysDeptEntity.getTaxpayerCode());
                        resultList.add(authenticationInfo);
                    }
                }
                List<String> xfshList = new ArrayList<>();
                for(AuthenticationInfo auth : resultList){
                	xfshList.add(auth.getNsrsbh());
				}

				int totalPage = resultList.size();
                if(StringUtils.isNotBlank(currentPage) && StringUtils.isNotBlank(pageSize) && CollectionUtils.isNotEmpty(resultList)){
					int currPage = Integer.valueOf(currentPage);
					int size = Integer.valueOf(pageSize);

					if((currPage - 1) * size >=  totalPage){
                        //如果分页参数大于当前数据 返回空
						PageUtils page = new PageUtils(null, totalPage, size, currPage);
						return R.ok().put("data", page).put("shList",xfshList);

					}else{
						List<CountBySldVO> countBySldVOList;

						if (currPage * size > totalPage) {
							resultList = resultList.subList((currPage - 1) * size, totalPage);

						} else {
							resultList = resultList.subList((currPage - 1) * size, (currPage - 1) * size + size);

						}
						PageUtils page = new PageUtils(resultList, totalPage, size, currPage);
						return R.ok().put("data", page).put("shList",xfshList);

					}

				}else{
					PageUtils page = new PageUtils(resultList, totalPage, totalPage, 1);
					return R.ok().put("data", page).put("shList",xfshList);
				}

            }
		} catch (Exception e) {
			log.error("{}查询企业配置信息列表异常:{}",LOGGER_MSG,e);
			return R.error().put(OrderManagementConstant.MESSAGE,e.getMessage());
		}

	}


	/**
	 *
	 * @param nsrsbh
	 * @param xhfMc
	 * @return
	 */
	@ApiOperation(value = "企业配置信息查询接口", notes = "企业信息配置管理-企业配置信息查询接口")
	@PostMapping("/queryEnterpriseCofnigInfo")
	@SysLog(operation = "企业配置信息查询接口", operationDesc = "企业配置信息查询接口", key = "企业信息配置管理")
	public R queryEnterpriseCofnigInfo(
			@ApiParam(name = "nsrsbh", value = "纳税人识别号", required = true) @RequestParam(value = "nsrsbh", required = true) String nsrsbh,
			@ApiParam(name = "xhfMc", value = "销方名称", required = true) @RequestParam(value = "xhfMc", required = true) String xhfMc) {
		
		log.info("{}企业配置信息查询接口, 入参,nsrsbh:{}", LOGGER_MSG, nsrsbh);
		Map<String, String> paramMap = new HashMap<String, String>(2);
		paramMap.put("nsrsbh", nsrsbh);
		paramMap.put("xhfMc", xhfMc);
		return apiAuthenticationService.queryEnterpreiseConfigInfo(paramMap);
	}


	/**
	 * 保存企业配置信息
	 * @param param
	 * @return
	 */
	@ApiOperation(value = "保存企业配置信息", notes = "企业信息配置管理-保存企业配置信息")
	@PostMapping("/saveEnterpriseConfigInfo")
	@SysLog(operation = "保存企业配置信息", operationDesc = "保存企业配置信息", key = "企业信息配置管理")
	public R saveEnterpriseCofnigInfo(@RequestBody String param) {

		try {
			log.info("{}保存企业配置信息的接口，入参:{}", LOGGER_MSG, param);
			if(StringUtils.isBlank(param)){
                log.error("保存企业配置信息，参数错误!");
                return R.error().put(OrderManagementConstant.MESSAGE,"保存企业配置信息，参数错误!");
            }

			Map map = JsonUtils.getInstance().parseObject(param, Map.class);
			AuthenticationInfo authInfo = convetToAuthenticationInfo(map);
			List<PushInfo> pushInfoList = convetToPushInfo(map);
			R r = apiAuthenticationService.saveEnterpriseCofnigInfo(authInfo,pushInfoList);
			return r;
		} catch (Exception e) {
			log.error("{}保存企业配置信息异常!,异常信息:{}",LOGGER_MSG,e);
			return R.error().put(OrderManagementConstant.MESSAGE,"企业配置信息保存失败!");
		}
	}


	/**
	 * 在使用
	 * 税控设备分组接口,提供给底层调用
	 *
	 * @param paramlist
	 * @return
	 */
	@ApiOperation(value = "分组查询税控设备信息", notes = "税控设备管理-分组查询税控设备信息")
	@PostMapping("/groupByTaxEquipment")
	@SysLog(operation = "税控设备分组rest接口", operationDesc = "税控设备信息分组接口", key = "税控分组")
	public R groupByTaxEquipment(@RequestBody String paramlist) {
		if(StringUtils.isBlank(paramlist)){
			return R.error().put(OrderManagementConstant.MESSAGE,"纳税人识别号参数不能为空!");

		}
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

		try {
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
				String currentPage = taxEquipmentInfo.getCurrentPage();
                String pageSize = taxEquipmentInfo.getPageSize();
				int totalPage = resultList.size();
				if(StringUtils.isNotBlank(currentPage) && StringUtils.isNotBlank(pageSize) && CollectionUtils.isNotEmpty(resultList)){
					int currPage = Integer.parseInt(currentPage);
					int size = Integer.parseInt(pageSize);

					if((currPage - 1) * size >=  totalPage) {
						//如果分页参数大于当前数据 返回空
						PageUtils page = new PageUtils(null, totalPage, size, currPage);
						return R.ok().put("data", page);

					}else{
						if (currPage * size > totalPage) {
							resultList = resultList.subList((currPage - 1) * size, totalPage);

						} else {
							resultList = resultList.subList((currPage - 1) * size, (currPage - 1) * size + size);

						}
						PageUtils page = new PageUtils(resultList, totalPage, size, currPage, true);
						return R.ok().put("data", page);
					}

				}else {
					PageUtils page = new PageUtils(resultList, totalPage, totalPage, 1, true);
					return R.ok().put("data", page);
				}
            }
		} catch (Exception e) {
			return R.error().put(OrderManagementConstant.MESSAGE, "处理失败!");
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
	 * @return
	 */
	@ApiOperation(value = "添加税控设备信息", notes = "税控设备管理-添加税控设备信息")
	@PostMapping("/addTaxEquipment")
	@SysLog(operation = "添加税控设备rest接口", operationDesc = "添加税控设备信息接口", key = "税控添加")
	public R addTaxEquipment(@RequestBody List<TaxEquipmentInfo> taxEquipmentInfos) {
		log.info("{}", LOGGER_MSG);
		//查询当前税号是否已经存在
		List<R> errorList = new ArrayList<>();
		for(TaxEquipmentInfo taxEquipmentInfo : taxEquipmentInfos){
			TaxEquipmentInfo queryTaxEquip = new TaxEquipmentInfo();
			queryTaxEquip.setXhfNsrsbh(taxEquipmentInfo.getXhfNsrsbh());
			List<String> shList = NsrsbhUtils.transShListByNsrsbh(taxEquipmentInfo.getXhfNsrsbh());
			List<TaxEquipmentInfo> queryTaxEquipment = apiTaxEquipmentService.queryTaxEquipmentList(queryTaxEquip, shList);
			if (!queryTaxEquipment.isEmpty()) {
				errorList.add(R.error().put(OrderManagementConstant.CODE, "9000").put("msg", "税号:" + taxEquipmentInfo.getXhfNsrsbh() + "已经配置税控设备！"));
			}
			if (StringUtils.isBlank(taxEquipmentInfo.getSksbCode())) {
				errorList.add(R.error().put(OrderManagementConstant.CODE, "9000").put("msg", "税号:" + taxEquipmentInfo.getXhfNsrsbh() + "税控类型参数不能为空！"));
			}
			//获取当前登陆账号
			taxEquipmentInfo.setCreateTime(new Date());
			taxEquipmentInfo.setUpdateTime(new Date());
			taxEquipmentInfo.setLinkTime(new Date());
			
			taxEquipmentInfo.setDeleted(OrderInfoEnum.DATE_DELETE_STATUS_0.getKey());
			taxEquipmentInfo.setId(apiInvoiceCommonService.getGenerateShotKey());
			int i = apiTaxEquipmentService.addTaxEquipment(taxEquipmentInfo);
			if (i <= 0) {
				errorList.add(R.error().put(OrderManagementConstant.CODE, "9000").put("msg", "税号:" + taxEquipmentInfo.getXhfNsrsbh() + "配置失败！"));
			}
		}

		return R.ok().put("errorList",errorList);

	}




	private List<PushInfo> convetToPushInfo(Map map) {

		List<PushInfo> pushInfoList = new ArrayList<PushInfo>();

		if(map.get("pushInfoList") != null){
			pushInfoList  = JSON.parseArray(String.valueOf(map.get("pushInfoList")), PushInfo.class);
		}
		return pushInfoList;
	}

	private AuthenticationInfo convetToAuthenticationInfo(Map map) {
		AuthenticationInfo authenticationInfo = new AuthenticationInfo();
		authenticationInfo.setSecretKey(map.get("secretKey") == null ? "" : String.valueOf(map.get("secretKey")));
		authenticationInfo.setSecretId(map.get("secretId") == null ? "" : String.valueOf(map.get("secretId")));
		authenticationInfo.setId(map.get("id") == null ? "" : String.valueOf(map.get("id")));
		authenticationInfo.setNsrsbh(map.get("nsrsbh") == null ? "" : String.valueOf(map.get("nsrsbh")));
		return authenticationInfo;
	}

	public static void main(String[] args) {
		TaxEquipmentInfo taxInfo = new TaxEquipmentInfo();
		System.out.println(JsonUtils.getInstance().toJsonStringNullToEmpty(taxInfo));

	}


}
