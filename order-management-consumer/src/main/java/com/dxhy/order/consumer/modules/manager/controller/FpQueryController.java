package com.dxhy.order.consumer.modules.manager.controller;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.dxhy.invoice.protocol.common.PageUtils;
import com.dxhy.invoice.protocol.common.ResultZj;
import com.dxhy.invoice.protocol.sk.doto.request.SkReqYhzxxcx;
import com.dxhy.invoice.protocol.sl.sld.ZjStatementAnalysis;
import com.dxhy.invoice.protocol.sl.sld.ZjStatementData;
import com.dxhy.invoice.protocol.sl.sld.ZjYdtjDetailReq;
import com.dxhy.invoice.service.sl.FpQueryService;
import com.dxhy.order.api.ApiTaxEquipmentService;
import com.dxhy.order.constant.ConfigureConstant;
import com.dxhy.order.constant.OrderInfoContentEnum;
import com.dxhy.order.constant.OrderInfoEnum;
import com.dxhy.order.constant.OrderManagementConstant;
import com.dxhy.order.consumer.annotation.SysLog;
import com.dxhy.order.consumer.config.OpenApiConfig;
import com.dxhy.order.consumer.model.Ydxx;
import com.dxhy.order.consumer.modules.fiscal.service.UnifyService;
import com.dxhy.order.consumer.modules.fiscal.service.a9.SldManagerServiceA9;
import com.dxhy.order.consumer.modules.user.service.UserInfoService;
import com.dxhy.order.consumer.protocol.usercenter.DeptEntity;
import com.dxhy.order.model.Nsrsbh;
import com.dxhy.order.model.R;
import com.dxhy.order.model.a9.c48ydtj.*;
import com.dxhy.order.model.a9.query.FpYdtj;
import com.dxhy.order.model.a9.query.YhzxxResponse;
import com.dxhy.order.model.a9.query.YhzxxResponseExtend;
import com.dxhy.order.model.c48.sld.FpKpd;
import com.dxhy.order.model.c48.sld.KpdSpglRequest;
import com.dxhy.order.model.c48.sld.KpdSpglResponse;
import com.dxhy.order.utils.*;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.dubbo.config.annotation.Reference;
import org.apache.poi.ss.usermodel.BorderStyle;
import org.apache.poi.ss.usermodel.HorizontalAlignment;
import org.apache.poi.xssf.usermodel.*;
import org.springframework.util.ObjectUtils;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.*;

/**
 * 月度汇总控制层
 *
 * @author ZSC-DXHY
 * @date 创建时间: 2020-08-14 18:14
 */
@RestController
@RequestMapping("/fpQuery")
@Api(value = "月度汇总", tags = {"管理模块"})
@Slf4j
public class FpQueryController {
	
	@Reference
	private FpQueryService fpQueryService;
	
	@Reference
	private ApiTaxEquipmentService apiTaxEquipmentService;
	
	@Resource
	private UnifyService unifyService;
	
	@Resource
	private UserInfoService userInfoService;
	
	@Resource
	private SldManagerServiceA9 sldManagerServiceA9;
	
	
	/**
	 * 报表分析列表接口
	 *
	 * @return
	 */
	@PostMapping("/getBbfxList")
	@ApiOperation(value = "发票汇总表统计", notes = "发票汇总表统计-发票汇总表统计")
	@SysLog(operation = "发票汇总表统计rest接口", operationDesc = "发票汇总表统计", key = "发票汇总表统计")
	public R getBbfxList(
			@ApiParam(name = "nsrsbhs", value = "纳税人识别号集合", required = true) @RequestParam(value = "nsrsbhs", required = true) String nsrsbhs,
			@ApiParam(name = "kpnf", value = "开票年份", required = true) @RequestParam(value = "kpnf", required = true) String kpnf,
			@ApiParam(name = "kpyf", value = "开票月份", required = true) @RequestParam(value = "kpyf", required = true) String kpyf,
			@ApiParam(name = "fjh", value = "分机号", required = false) @RequestParam(value = "fjh", required = false) String fjh,
			@ApiParam(name = "pageSize", value = "每页显示个数", required = true) @RequestParam(name = "pageSize", required = true) Integer pageSize,
			@ApiParam(name = "currPage", value = "当前页数", required = true) @RequestParam(name = "currPage", required = true) Integer currPage) {
		// 月度统计接口 c48接口走根据多税号查询的接口 A9走单税号查询 循环查询之后组合
		List<String> parseObject = JSON.parseArray(nsrsbhs, String.class);
		if (CollectionUtils.isEmpty(parseObject)) {
			log.error("发票汇总表汇总接口，纳税人识别号参数为空");
			return R.error("纳税人识别号不能为空");
		}
		List<String> shlist = new ArrayList<>();
		parseObject.forEach(sh->{
			if (!StringUtils.isBlank(sh)) {
				shlist.add(sh);
			}
		});
		List<Map<String, String>> monthSummarInfoList = new ArrayList<>();
		List<String> c48List = new ArrayList<>();
		List<String> bwpzList = new ArrayList<>();
		List<String> ukeyList = new ArrayList<>();
		R ra = new R();
		for (String nsrsbh : shlist) {
			try {
				String terminalCode = apiTaxEquipmentService.getTerminalCode(nsrsbh);
				
				if (OrderInfoEnum.TAX_EQUIPMENT_C48.getKey().equals(terminalCode)) {
					c48List.add(nsrsbh);
				} else if (OrderInfoEnum.TAX_EQUIPMENT_BWPZ.getKey().equals(terminalCode)) {
					bwpzList.add(nsrsbh);
				} else if (OrderInfoEnum.TAX_EQUIPMENT_UKEY.getKey().equals(terminalCode)) {
					ukeyList.add(nsrsbh);
				} else if (OrderInfoEnum.TAX_EQUIPMENT_BWFWQ.getKey().equals(terminalCode)
						|| OrderInfoEnum.TAX_EQUIPMENT_BW_ACTIVEX.getKey().equals(terminalCode)
						|| OrderInfoEnum.TAX_EQUIPMENT_FGBW.getKey().equals(terminalCode)
						|| OrderInfoEnum.TAX_EQUIPMENT_FGHX.getKey().equals(terminalCode)
						|| OrderInfoEnum.TAX_EQUIPMENT_FGUKEY.getKey().equals(terminalCode)) {
					log.info("百望暂不支持发票汇总信息查询");
				} else {
					if (OrderInfoEnum.TAX_EQUIPMENT_NEWTAX.getKey().equals(terminalCode)) {
                        List<Map<String, String>> maps = testNewTax(nsrsbh, terminalCode, kpnf, kpyf, fjh);
                        if (!CollectionUtils.isEmpty(maps)){
                            for (Map<String,String> yd : maps) {
                                if(OrderInfoContentEnum.SUCCESS.getKey().equals(yd.get(ConfigureConstant.CODE))){
                                    monthSummarInfoList.add(yd);
                                }
                            }
                        }
						
					} else {
						KpdSpglRequest request = new KpdSpglRequest();
						Nsrsbh sh = new Nsrsbh();
						sh.setNsrsbh(nsrsbh);
						Nsrsbh[] shs = new Nsrsbh[1];
						shs[0] = sh;
						request.setNsrsbhs(shs);
						request.setFjh(fjh);
						KpdSpglResponse queryKpdList = HttpInvoiceRequestUtil.getFjh(OpenApiConfig.getSearchFjhFpzlDm, request,
								terminalCode);
						
						if (OrderInfoContentEnum.SUCCESS.getKey().equals(queryKpdList.getStatusCode())
								&& CollectionUtils.isNotEmpty(queryKpdList.getFpkpds())) {
							DeptEntity sysDeptEntity = userInfoService.querySysDeptEntityByTaxplayercode(nsrsbh);
							// 过滤相同的分机号
							Set<String> set = new HashSet<>();
							for (FpKpd kpd : queryKpdList.getFpkpds()) {
								set.add(kpd.getFjh());
							}
							// 根据分机号查询发票汇总数据
							for (String fjh1 : set) {
								Integer kpl = 0;
								Integer kcyl = 0;
								BigDecimal hjje = new BigDecimal("0.00");
								BigDecimal hjse = new BigDecimal("0.00");
								SkReqYhzxxcx skRequset = new SkReqYhzxxcx();
								skRequset.setFjh(fjh1);
								skRequset.setNsrsbh(nsrsbh);
								skRequset.setSsyf(kpnf + kpyf);
								YhzxxResponse queryYhzxx = unifyService.queryYhzxx(skRequset, terminalCode);
								if (queryYhzxx != null && OrderInfoContentEnum.SUCCESS.getKey().equals(queryYhzxx.getCode())) {
									YhzxxResponseExtend result = queryYhzxx.getResult();
									if (OrderInfoContentEnum.SUCCESS.getKey().equals(result.getStatusCode())) {
										
										List<FpYdtj> fpYdtj = result.getFpYdtj();
										if (!CollectionUtils.isEmpty(fpYdtj)) {
											
											for (FpYdtj ydtj : fpYdtj) {
												if (StringUtils.isNotBlank(ydtj.getSjjeTj())) {
													Map<String, String> resultMap = JsonUtils.getInstance()
															.parseObject(ydtj.getSjjeTj(), Map.class);
													if (StringUtils.isNotBlank(resultMap.get("sjjeTotal"))) {
														hjje = hjje.add(new BigDecimal(resultMap.get("sjjeTotal")));
													}
													
												}
												if (StringUtils.isNotBlank(ydtj.getSjseTj())) {
													Map<String, String> resultMap = JsonUtils.getInstance()
															.parseObject(ydtj.getSjseTj(), Map.class);
													
													if (StringUtils.isNotBlank(resultMap.get("sjseTotal"))) {
														hjse = hjse.add(new BigDecimal(resultMap.get("sjseTotal")));
													}
												}
												if (StringUtils.isNotBlank(ydtj.getQmkc())) {
													kcyl += Integer.parseInt(ydtj.getQmkc());
												}
												if (StringUtils.isNotBlank(ydtj.getZsfp())) {
													kpl += Integer.parseInt(ydtj.getZsfp());
												}
												
												if (StringUtils.isNotBlank(ydtj.getFsfp())) {
													kpl += Integer.parseInt(ydtj.getFsfp());
												}
												
											}
											
										}
										Map<String, String> map = new HashMap<>(5);
										map.put("xhfmc", sysDeptEntity.getName());
										map.put("fjh", String.valueOf(fjh1));
										map.put("hjje", hjje.toString());
										map.put("hjse", hjse.toString());
										map.put("jshj", hjje.add(hjse).toString());
										map.put("kcyl", String.valueOf(kcyl));
										map.put("kpl", String.valueOf(kpl));
										map.put("nsrsbh", nsrsbh);
										map.put("kpnf", kpnf);
										map.put("kpyf", kpyf);
										monthSummarInfoList.add(map);
									}
								} else {
									ra.put(queryYhzxx.getCode(), queryYhzxx.getMsg());
									return ra;
								}
							}
						}
					}
				}
			} catch (Exception e) {
				log.error("发票汇总表汇总汇总，查询异常 税号:{},异常信息:{}", nsrsbh, e);
			}
		}
		//百望盘阵发票统计查询
		if (CollectionUtils.isNotEmpty(bwpzList)) {
			monthSummarInfoList.addAll(getBbfxReturn(bwpzList, null, fjh, kpnf, kpyf));
		}
		if (CollectionUtils.isNotEmpty(ukeyList)) {
			monthSummarInfoList.addAll(getBbfxReturn(null, ukeyList, fjh, kpnf, kpyf));
		}
/*		if (CollectionUtils.isNotEmpty(c48List)) {
			ZjStatementAnalysis zjStatementAnalysis = new ZjStatementAnalysis();
			if (parseObject.size() == 0) {
				return R.error("纳税人识别号不能为空");
			}
			zjStatementAnalysis.setNsrsbhs(c48List);
			zjStatementAnalysis.setKpnf(kpnf);
			zjStatementAnalysis.setKpyf(kpyf);
			zjStatementAnalysis.setFjh(fjh);
			zjStatementAnalysis.setCurrPage(1);
            zjStatementAnalysis.setPageSize(10000);
            log.debug("c48 发票信息汇总的接口,入参:{}", JsonUtils.getInstance().toJsonString(zjStatementAnalysis));
            ResultZj<PageUtils> resultZj = fpQueryService.getBbfxList(zjStatementAnalysis);
            log.debug("c48 发票信息汇总的接口,出参:{}", JsonUtils.getInstance().toJsonString(resultZj));
            String code = resultZj.getCode();
            if (ConfigureConstant.STRING_0000.equals(code)) {
                PageUtils pageUtils = resultZj.getData();
                if (!org.springframework.util.StringUtils.isEmpty(pageUtils)) {
                    List<ZjStatementData> list = (List<ZjStatementData>) pageUtils.getList();
                    for (ZjStatementData zjStatementData : list) {
                        DeptEntity sysDeptEntity = userInfoService.querySysDeptEntityByTaxplayercode(zjStatementData.getNsrsbh());
                        
                        Map<String, String> resultMap = new HashMap<>(5);
                        resultMap.put("xhfmc", sysDeptEntity.getName());
                        resultMap.put("fjh", zjStatementData.getFjh());
                        resultMap.put("hjje", zjStatementData.getHjje());
						resultMap.put("hjse", zjStatementData.getHjse());
						resultMap.put("jshj", new BigDecimal(zjStatementData.getHjje())
								.add(new BigDecimal(zjStatementData.getHjse())).toString());
						resultMap.put("kcyl", zjStatementData.getKcys());
						resultMap.put("kpl", zjStatementData.getKpl());
						resultMap.put("id", zjStatementData.getId());
						resultMap.put("nsrsbh", zjStatementData.getNsrsbh());
						resultMap.put("kpnf", kpnf);
						resultMap.put("kpyf", kpyf);
						monthSummarInfoList.add(resultMap);
					}
				}
			}
		}*/
		// 处理分页
		int start = (currPage - 1) * pageSize;
		int end = (currPage - 1) * pageSize + pageSize;
		int totalPage = (int) Math.ceil(Double.parseDouble(String.valueOf(monthSummarInfoList.size() / pageSize)));
		if (end > monthSummarInfoList.size()) {
			end = monthSummarInfoList.size();
		}
		List<Map<String, String>> subList = monthSummarInfoList.subList(start, end);
		return R.ok().put(OrderManagementConstant.DATA, subList).put("currPage", currPage).put("pageSize", pageSize)
				.put("totalCount", monthSummarInfoList.size()).put("totalPage", totalPage);
	}
	
	public List<Map<String, String>> getBbfxReturn(List<String> bwpzList, List<String> ukeyList, String fjh, String kpnf, String kpyf) {
		List<Map<String, String>> monthSummarInfoList = new ArrayList<>();
		String terminal = "";
		List<String> reqList = new ArrayList<>();
		if (CollectionUtils.isNotEmpty(bwpzList)) {
			terminal = OrderInfoEnum.TAX_EQUIPMENT_BWPZ.getKey();
			reqList.addAll(bwpzList);
		} else {
			terminal = OrderInfoEnum.TAX_EQUIPMENT_UKEY.getKey();
			reqList.addAll(ukeyList);
		}
		YdtjParam ydtjParam = new YdtjParam();
		ydtjParam.setNsrsbhs(reqList);
		ydtjParam.setJqbh(fjh);
		ydtjParam.setKpnf(kpnf);
		ydtjParam.setKpyf(kpyf);
		ydtjParam.setTerminalCode(terminal);
		
		log.info("根据分机号查询发票汇总数据请求参数：{}", JSONObject.toJSONString(ydtjParam));
		List<YdtjDto> ydtjDtoList = sldManagerServiceA9.queryYhzxxBwpz(ydtjParam);
		if (ydtjDtoList != null && !ydtjDtoList.isEmpty()) {
			for (YdtjDto ydtjDto : ydtjDtoList) {
				Map<String, String> map = new HashMap<String, String>(10);
				map.put("xhfmc", ydtjDto.getNsrmc());
				map.put("fjh", ydtjDto.getFjh());
				map.put("hjje", ydtjDto.getBhsje());
				map.put("hjse", ydtjDto.getHjse());
				map.put("jshj", ydtjDto.getHjje());
				map.put("kcyl", ydtjDto.getKcys());
				map.put("kpl", ydtjDto.getKpl());
				map.put("nsrsbh", ydtjDto.getNsrsbh());
				map.put("kpnf", kpnf);
				map.put("kpyf", kpyf);
				map.put("id", ydtjDto.getId() != null ? ydtjDto.getId().toString() : "");
				map.put("terminalCode", OrderInfoEnum.TAX_EQUIPMENT_BWPZ.getKey());
				monthSummarInfoList.add(map);
			}
		}
		return monthSummarInfoList;
	}
	
	/**
	 * 报表分析详情接口
	 *
	 * @return
	 */
	@PostMapping("/getBbfxDetail")
	@ApiOperation(value = "报表分析详情", notes = "发票汇总表统计-报表分析详情")
	@SysLog(operation = "发票汇总表统计详情rest接口", operationDesc = "发票汇总表详情信息统计", key = "发票汇总表统计")
	public R getBbfxDetail(
			@ApiParam(name = "id", value = "详情id", required = true) @RequestParam(value = "id", required = false) String id,
			@ApiParam(name = "fpzlDm", value = "发票种类代码", required = true) @RequestParam(value = "fpzlDm", required = false) String fpzlDm,
			@ApiParam(name = "xhfNsrsbh", value = "纳税人识别号集合", required = true) @RequestParam(value = "xhfNsrsbh", required = false) String xhfNsrsbh,
			@ApiParam(name = "kpnf", value = "开票年份", required = true) @RequestParam(value = "kpnf", required = false) String kpnf,
			@ApiParam(name = "kpyf", value = "开票月份", required = true) @RequestParam(value = "kpyf", required = false) String kpyf,
			@ApiParam(name = "fjh", value = "分机号", required = false) @RequestParam(value = "fjh", required = false) String fjh) {
		//查询税控设备
		String terminalCode = apiTaxEquipmentService.getTerminalCode(xhfNsrsbh);
		
		/**
		 * 根据税控设备区分调用c48还是A9得接口
		 */
		if (OrderInfoEnum.TAX_EQUIPMENT_C48.getKey().equals(terminalCode)) {
			
			ZjYdtjDetailReq zjYdtjDetailReq = new ZjYdtjDetailReq();
			zjYdtjDetailReq.setId(id);
			zjYdtjDetailReq.setFpzlDm(fpzlDm);
			log.debug("查询汇总信息详细数据:{}", JsonUtils.getInstance().toJsonString(zjYdtjDetailReq));
			ResultZj resultZj = fpQueryService.getBbfxDetail(zjYdtjDetailReq);
			log.debug("查询汇总详细信息接口出参:{}", JsonUtils.getInstance().toJsonString(resultZj));
			String code = resultZj.getCode();
			if (ConfigureConstant.STRING_9999.equals(code)) {
				return R.error(resultZj.getMsg());
			} else if (ConfigureConstant.STRING_0000.equals(code)) {
				
				Ydxx parseObject = JsonUtils.getInstance().parseObject(JsonUtils.getInstance().toJsonString(resultZj.getData()), Ydxx.class);
				if (!CollectionUtils.isEmpty(parseObject.getYhzxxs())) {
					parseObject.getYhzxxs().sort((o1, o2) -> {
						
						if (StringUtils.isBlank(o1.getSl())) {
							return -1;
						}
						if (StringUtils.isBlank(o2.getSl())) {
							return 1;
						}
						
						if (Double.parseDouble(o1.getSl()) > Double.parseDouble(o2.getSl())) {
							return -1;
						} else if (Double.parseDouble(o1.getSl()) < Double.parseDouble(o2.getSl())) {
							return 1;
						}
						return 0;
					});
				}
				return R.ok().put(OrderManagementConstant.DATA, parseObject);
			} else {
				return R.error("请求出错");
			}
		} else if (OrderInfoEnum.TAX_EQUIPMENT_BWPZ.getKey().equals(terminalCode) || OrderInfoEnum.TAX_EQUIPMENT_UKEY.getKey().equals(terminalCode)) {
			YdtjDetailParam detailParam = new YdtjDetailParam();
			detailParam.setId(id);
			detailParam.setFpzlDm(fpzlDm);
			detailParam.setTerminalCode(terminalCode);
			
			JSONObject bbfxDetailBwPz = sldManagerServiceA9.getBbfxDetailBwPz(detailParam);
			if (bbfxDetailBwPz != null && OrderInfoContentEnum.SUCCESS.getKey().equals(bbfxDetailBwPz.getString("code"))) {
				//月度统计信息
				JSONObject usePo = bbfxDetailBwPz.getJSONObject("result");
				
				FpYdtj fpYdtj = convertToFpYdtj(usePo);
				fpYdtj.setFpzl(fpzlDm);
				Map<String, Object> resultMap = convertToResultMapBwpz(fpYdtj);
				resultMap.put(ConfigureConstant.STRING_TERMINAL_CODE, terminalCode);
				return R.ok().put(OrderManagementConstant.DATA, resultMap);
				
			}
			
		} else {
			SkReqYhzxxcx skRequset = new SkReqYhzxxcx();
			skRequset.setFjh(fjh);
			skRequset.setNsrsbh(xhfNsrsbh);
			skRequset.setSsyf(kpnf + kpyf);
			skRequset.setFpzlDm(fpzlDm);
			YhzxxResponse queryYhzxx = unifyService.queryYhzxx(skRequset, terminalCode);
			if (queryYhzxx != null && OrderInfoContentEnum.SUCCESS.getKey().equals(queryYhzxx.getCode())) {
				YhzxxResponseExtend result = queryYhzxx.getResult();
				if (OrderInfoContentEnum.SUCCESS.getKey().equals(result.getStatusCode())) {
					if (!CollectionUtils.isEmpty(result.getFpYdtj())) {
						if (OrderInfoEnum.TAX_EQUIPMENT_NEWTAX.getKey().equals(terminalCode)) {
							Map<String, Object> resultMap = convertToResultMapNewTax(result.getFpYdtj().get(0));
							resultMap.put(ConfigureConstant.STRING_TERMINAL_CODE, terminalCode);
							return R.ok().put(OrderManagementConstant.DATA, resultMap);
						} else {
							Map<String, Object> resultMap = convertToResultMap(result.getFpYdtj().get(0));
							resultMap.put(ConfigureConstant.STRING_TERMINAL_CODE, terminalCode);
							return R.ok().put(OrderManagementConstant.DATA, resultMap);
						}
						
					}
				}
			} else {
				return R.error(queryYhzxx.getMsg());
			}
		}
		return R.error();
	}
	
	/**
     * @param @param  fpYdtj
     * @param @return
     * @return Map<String, Object>
     * @throws
     * @Title : convertToResultMap
     * @Description ：返回参数转换
     */
	private Map<String, Object> convertToResultMap(FpYdtj fpYdtj) {
		Map<String, Object> resultMap = new HashMap<>(5);
		
		//外层数据解析 赋值 开具份数统计
		resultMap.put("nsrsbh", fpYdtj.getXsfNsrsbh());
		resultMap.put("fjh", fpYdtj.getFjh());
		resultMap.put("fpzlDm", fpYdtj.getFpzl());
		resultMap.put("qckcfs", fpYdtj.getQckc());
		resultMap.put("qmkcfs", fpYdtj.getQmkc());
		resultMap.put("gjfpfs", fpYdtj.getGjfp());
		resultMap.put("thfpfs", fpYdtj.getThfp());
		resultMap.put("shfpfs", fpYdtj.getThfp());
		resultMap.put("fpfpfs", fpYdtj.getFsfp());
		resultMap.put("zsfpkjfs", fpYdtj.getZsfp());
		resultMap.put("zsfpzffs", fpYdtj.getZszf());
		resultMap.put("fsfpkjfs", fpYdtj.getFsfp());
		resultMap.put("fsfpzffs", fpYdtj.getFszf());
		resultMap.put("zbrq", fpYdtj.getZbsj());
		resultMap.put("ssqj", fpYdtj.getTjnf() + fpYdtj.getTjyf());
		
		//存放总计金额
		Map<String, Object> jeTjMap = new HashMap<>(5);
		
		//根据税率金额统计
		List<Map<String, Object>> slList = new ArrayList<>();
		
		Map<String, Map<String, Object>> slMap = new HashMap<>(5);
		
		//销项正数金额
		if (StringUtils.isNotBlank(fpYdtj.getZsjeTj())) {
			Map<String, Object> zsjeMap = JsonUtils.getInstance().parseObject(fpYdtj.getZsjeTj(), Map.class);
			jeTjMap.put("zsje", zsjeMap.get("zsjeTotal"));
			slMap = buildMapA9(zsjeMap, "销项正数金额", slMap);
		}
		
		//销项正数税额
		if (StringUtils.isNotBlank(fpYdtj.getZsseTj())) {
			Map<String, Object> zsjeMap = JsonUtils.getInstance().parseObject(fpYdtj.getZsseTj(), Map.class);
			jeTjMap.put("zsse", zsjeMap.get("zsseTotal"));
			slMap = buildMapA9(zsjeMap, "销项正数税额", slMap);
			
		}
		
		//销项正废金额
		if (StringUtils.isNotBlank(fpYdtj.getZfjeTj())) {
			Map<String, Object> zsjeMap = JsonUtils.getInstance().parseObject(fpYdtj.getZfjeTj(), Map.class);
			jeTjMap.put("zfje", zsjeMap.get("zfjeTotal"));
			slMap = buildMapA9(zsjeMap, "销项正废金额", slMap);
			
		}
		//销项正废税额
		if (StringUtils.isNotBlank(fpYdtj.getZfseTj())) {
			Map<String, Object> zsjeMap = JsonUtils.getInstance().parseObject(fpYdtj.getZfseTj(), Map.class);
			jeTjMap.put("zfse", zsjeMap.get("zfseTotal"));
			slMap = buildMapA9(zsjeMap, "销项正废税额", slMap);
			
		}
		//销项负数金额
		if (StringUtils.isNotBlank(fpYdtj.getFsjeTj())) {
			Map<String, Object> zsjeMap = JsonUtils.getInstance().parseObject(fpYdtj.getFsjeTj(), Map.class);
			jeTjMap.put("fsje", zsjeMap.get("fsjeTotal"));
			slMap = buildMapA9(zsjeMap, "销项负数金额", slMap);
			
		}
		//销项负数税额
		if (StringUtils.isNotBlank(fpYdtj.getFsseTj())) {
			Map<String, Object> zsjeMap = JsonUtils.getInstance().parseObject(fpYdtj.getFsseTj(), Map.class);
			jeTjMap.put("fsse", zsjeMap.get("fsseTotal"));
			slMap = buildMapA9(zsjeMap, "销项负数税额", slMap);
			
		}
		//销项负废金额
		if (StringUtils.isNotBlank(fpYdtj.getFfjeTj())) {
			Map<String, Object> zsjeMap = JsonUtils.getInstance().parseObject(fpYdtj.getFfjeTj(), Map.class);
			jeTjMap.put("ffje", zsjeMap.get("ffjeTotal"));
			slMap = buildMapA9(zsjeMap, "销项负废金额", slMap);
			
		}
		//销项负废税额
		if (StringUtils.isNotBlank(fpYdtj.getFfseTj())) {
			Map<String, Object> zsjeMap = JsonUtils.getInstance().parseObject(fpYdtj.getFfseTj(), Map.class);
			jeTjMap.put("ffse", zsjeMap.get("ffseTotal"));
			slMap = buildMapA9(zsjeMap, "销项负废税额", slMap);
			
		}
		//实际销项金额
		if (StringUtils.isNotBlank(fpYdtj.getSjjeTj())) {
			Map<String, Object> zsjeMap = JsonUtils.getInstance().parseObject(fpYdtj.getSjjeTj(), Map.class);
			jeTjMap.put("sjje", zsjeMap.get("sjjeTotal"));
			slMap = buildMapA9(zsjeMap, "实际销项金额", slMap);
			
		}
		//实际销项税额
		if (StringUtils.isNotBlank(fpYdtj.getSjseTj())) {
			Map<String, Object> zsjeMap = JsonUtils.getInstance().parseObject(fpYdtj.getSjseTj(), Map.class);
			jeTjMap.put("sjse", zsjeMap.get("sjseTotal"));
			slMap = buildMapA9(zsjeMap, "实际销项税额", slMap);
		}
		resultMap.put("hjjes", jeTjMap);
		for (Map.Entry<String, Map<String, Object>> entry : slMap.entrySet()) {
			
			
			if (!entry.getKey().contains("Other")) {
				/**
				 * 过滤税率金额为零金额的数据
				 * 需要判断明细金额是否全部为0,如果为0 ,不展示
				 */
				Map<String, Object> map = entry.getValue();
				if (map.size() > 0) {
					List list = (List) map.get("mxxxs");
					int a = 0;
					for (Object o : list) {
						
						Map<String, Object> jemap = (Map<String, Object>) o;
						if (!jemap.isEmpty() && StringUtils.isNotBlank(jemap.get("hjjese").toString()) && ConfigureConstant.STRING_000.equals(DecimalCalculateUtil.decimalFormatToString(jemap.get("hjjese").toString(), ConfigureConstant.INT_2))) {
							
							a++;
						}
						
					}
					if (a == list.size()) {
					
					} else {
						slList.add(entry.getValue());
					}
				}
				
			}
		}
		
		
		//重写排序规
		slList.sort((o1, o2) -> {
			if (Double.parseDouble(String.valueOf(o1.get("sl"))) > Double.parseDouble(String.valueOf(o2.get("sl")))) {
				return -1;
			} else if (Double.parseDouble(String.valueOf(o1.get("sl"))) < Double.parseDouble(String.valueOf(o2.get("sl")))) {
				return 1;
			}
			return 0;
		});
		
		resultMap.put("yhzxxs", slList);
		return resultMap;
	}
	
	/**
	 * 发票汇总表A9组装返回前端数据
	 */
	private Map<String, Map<String, Object>> buildMapA9(Map<String, Object> zsjeMap, String mc, Map<String, Map<String, Object>> slMap) {
		for (Map.Entry<String, Object> entry : zsjeMap.entrySet()) {
			String key = entry.getKey();
			if (key.contains("Total")) {
				continue;
			}
			Map<String, Object> map = new HashMap<>(5);
			List<Map<String, Object>> list = new ArrayList<>();
			Map<String, Object> jeMap = new HashMap<>(5);
			jeMap.put("kplxmc", mc);
			jeMap.put("hjjese", entry.getValue());
			Map<String, Object> map1 = slMap.get(getSl(key.substring(4)));
			if (!ObjectUtils.isEmpty(map1)) {
				List mxxxsList = (List) map1.get("mxxxs");
				mxxxsList.add(jeMap);
				map1.put("mxxxs", mxxxsList);
				map = map1;
			} else {
				list.add(jeMap);
				map.put("mxxxs", list);
				map.put("sl", getSl(key.substring(4)));
			}
			slMap.put(getSl(key.substring(4)), map);
			/*}*/
		}
        return slMap;
    }
    
    /**
     * @param @param  zsjeMap
     * @param @param  string
     * @param @param  slMap
     * @param @return
     * @return Map<String, Map < String, Object>>
     * @throws
     * @Title : buildMap
     * @Description ：
	 */
	
	private Map<String, Map<String, Object>> buildMap(Map<String, Object> zsjeMap, String mc,
	                                                  Map<String, Map<String, Object>> slMap) {
		for (Map.Entry<String, Object> entry : zsjeMap.entrySet()) {
			String key = entry.getKey();
			if (slMap.get(key.substring(4)) != null) {
				Map<String, Object> map = slMap.get(key.substring(0, 4));
				List<Map<String, Object>> list = (List<Map<String, Object>>) map.get("mxxxs");
				Map<String, Object> jeMap = new HashMap<>(5);
				jeMap.put("kplxmc", mc);
				jeMap.put("hjjese", entry.getValue());
				list.add(jeMap);
				map.put("mxxxs", list);
				map.put("sl", getSl(key.substring(4)));
				slMap.put(key.substring(0, 4), map);
				
			} else {
				Map<String, Object> map = new HashMap<>(5);
				List<Map<String, Object>> list = new ArrayList<>();
				Map<String, Object> jeMap = new HashMap<>(5);
				jeMap.put("kplxmc", mc);
				jeMap.put("hjjese", entry.getValue());
				list.add(jeMap);
				map.put("mxxxs", list);
				map.put("sl", getSl(key.substring(4)));
				slMap.put(key.substring(0, 4), map);
			}
		}
        return slMap;
    }
    
    /**
     * @param @param  substring
     * @param @return
     * @return Object
     * @throws
     * @Title : getSl
     * @Description ：
	 */
	
	private String getSl(String substring) {
		if (substring.contains("_")) {
			substring = substring.replace("_", ".");
		}
		if (substring.contains("Other")) {
			return "0.00";
		}
		if (substring.contains("Total")) {
			return substring;
		}
		return String.valueOf(Double.parseDouble(substring) / 100);
	}
	
	
	/**
	 * 导出详情
	 *
	 * @param request
	 * @param response FpQueryController.java
	 *                 author wangruwei
	 *                 2019年7月23日
	 */
	@PostMapping(value = "/excelExportBbfxDetail")
	@ApiOperation(value = "发票汇总表导出", notes = "发票汇总表统计-发票汇总表导出")
	@SysLog(operation = "导出发票汇总表统计rest接口", operationDesc = "导出发票汇总表统计", key = "发票汇总表统计")
	public void excelExportBbfxDetail(HttpServletRequest request, HttpServletResponse response, @RequestBody Map<String, String> map) {
		OutputStream out = null;
		InputStream resource = null;
		
		String xhfNsrsbh = map.get("xhfNsrsbh");
		String fpzlDm = map.get("fpzlDm");
		try {
			Map<String, Object> resultMap = null;
			String terminalCode = apiTaxEquipmentService.getTerminalCode(xhfNsrsbh);
			if (!OrderInfoEnum.TAX_EQUIPMENT_C48.getKey().equals(terminalCode)) {
				if (OrderInfoEnum.TAX_EQUIPMENT_NEWTAX.getKey().equals(terminalCode)) {
					String kpnf = map.get("kpnf");
					String kpyf = map.get("kpyf");
					SkReqYhzxxcx skRequset = new SkReqYhzxxcx();
					skRequset.setNsrsbh(xhfNsrsbh);
					skRequset.setSsyf(kpnf + kpyf);
					skRequset.setFpzlDm(fpzlDm);
					YhzxxResponse queryYhzxx = unifyService.queryYhzxx(skRequset, terminalCode);
					if (queryYhzxx != null && OrderInfoContentEnum.SUCCESS.getKey().equals(queryYhzxx.getCode())) {
						YhzxxResponseExtend result = queryYhzxx.getResult();
						if (OrderInfoContentEnum.SUCCESS.getKey().equals(result.getStatusCode())) {
							if (!CollectionUtils.isEmpty(result.getFpYdtj())) {
								
								resultMap = convertToResultMapNewTax(result.getFpYdtj().get(0));
							}
						}
					}
				} else if (OrderInfoEnum.TAX_EQUIPMENT_BWPZ.getKey().equals(terminalCode) || OrderInfoEnum.TAX_EQUIPMENT_UKEY.getKey().equals(terminalCode)) {
					String id = map.get("id");
					YdtjDetailParam ydtjDetailParam = new YdtjDetailParam();
					ydtjDetailParam.setId(id);
					ydtjDetailParam.setFpzlDm(fpzlDm);
					ydtjDetailParam.setTerminalCode(terminalCode);
					JSONObject bbfxDetailBwPz = sldManagerServiceA9.getBbfxDetailBwPz(ydtjDetailParam);
					if (bbfxDetailBwPz != null && OrderInfoContentEnum.SUCCESS.getKey().equals(bbfxDetailBwPz.getString("code"))) {
						//月度统计信息
						JSONObject usePo = bbfxDetailBwPz.getJSONObject("result");
						
						FpYdtj fpYdtj = convertToFpYdtj(usePo);
						fpYdtj.setFpzl(fpzlDm);
						resultMap = convertToResultMapBwpz(fpYdtj);
						
					}
					
				} else {
					String fjh = map.get("fjh");
					String kpnf = map.get("kpnf");
					String kpyf = map.get("kpyf");
					SkReqYhzxxcx skRequset = new SkReqYhzxxcx();
					skRequset.setFjh(fjh);
					skRequset.setNsrsbh(xhfNsrsbh);
					skRequset.setSsyf(kpnf + kpyf);
					skRequset.setFpzlDm(fpzlDm);
					YhzxxResponse queryYhzxx = unifyService.queryYhzxx(skRequset, terminalCode);
					if (queryYhzxx != null && OrderInfoContentEnum.SUCCESS.getKey().equals(queryYhzxx.getCode())) {
						YhzxxResponseExtend result = queryYhzxx.getResult();
						if (OrderInfoContentEnum.SUCCESS.getKey().equals(result.getStatusCode())) {
							if (!CollectionUtils.isEmpty(result.getFpYdtj())) {
								
								resultMap = convertToResultMap(result.getFpYdtj().get(0));
							}
						}
					}
				}
				
			} else {
                ZjYdtjDetailReq zjYdtjDetailReq = new ZjYdtjDetailReq();
                String id = map.get("id");
                zjYdtjDetailReq.setId(id);
                zjYdtjDetailReq.setFpzlDm(fpzlDm);
                ResultZj resultZj = fpQueryService.getBbfxDetail(zjYdtjDetailReq);
                if (ConfigureConstant.STRING_0000.equals(resultZj.getCode())) {
                    
                    Ydxx parseObject = JsonUtils.getInstance().parseObject(JsonUtils.getInstance().toJsonString(resultZj.getData()), Ydxx.class);
                    if (!CollectionUtils.isEmpty(parseObject.getYhzxxs())) {
                        parseObject.getYhzxxs().sort((o1, o2) -> {
                            if (Double.parseDouble(o1.getSl()) > Double.parseDouble(o2.getSl())) {
                                return -1;
                            } else if (Double.parseDouble(o1.getSl()) < Double.parseDouble(o2.getSl())) {
                                return 1;
                            }
                            return 0;
						});
					}
					resultMap = JsonUtils.getInstance().parseObject(JsonUtils.getInstance().toJsonString(parseObject), Map.class);
				}
				
			}
			
			if (resultMap != null) {
				resultMap.put(ConfigureConstant.STRING_TERMINAL_CODE, terminalCode);
				String headTitle = "增值税发票统计报表";
				if (OrderInfoEnum.ORDER_INVOICE_TYPE_51.getKey().equals(fpzlDm)) {
					headTitle = "增值税电子普通发票统计报表";
				} else if (OrderInfoEnum.ORDER_INVOICE_TYPE_2.getKey().equals(fpzlDm)) {
					headTitle = "增值税普通发票统计报表";
				} else if (OrderInfoEnum.ORDER_INVOICE_TYPE_0.getKey().equals(fpzlDm)) {
					headTitle = "增值税专用发票统计报表";
				}
				String rootPath = this.getClass().getResource("/").getPath();
				resource = new FileInputStream(rootPath + "redinvoice/expProductExcel.xlsx");
				// 创建Excel的工作书册 Workbook,对应到一个excel文档
				XSSFWorkbook wb = new XSSFWorkbook(resource);
				XSSFSheet sheet = wb.getSheetAt(0);
				//设置表头字体
				XSSFCellStyle headBorder = wb.createCellStyle();
				//下边框
				headBorder.setBorderBottom(BorderStyle.THIN);
				//左边框
				headBorder.setBorderLeft(BorderStyle.THIN);
				//上边框
				headBorder.setBorderTop(BorderStyle.THIN);
				//右边框
				headBorder.setBorderRight(BorderStyle.THIN);
				// 居中
				headBorder.setAlignment(HorizontalAlignment.LEFT);
				XSSFFont heafont = wb.createFont();
				heafont.setFontName("宋体");
				//设置字体大小
				heafont.setFontHeightInPoints((short) 16);
				//选择需要用到的字体格式
				headBorder.setFont(heafont);
				//设置内容字体
				XSSFCellStyle textBorder = wb.createCellStyle();
				// 居中
				textBorder.setAlignment(HorizontalAlignment.CENTER);
				XSSFFont textFont = wb.createFont();
				textFont.setFontName("宋体");
				//设置字体大小
				textFont.setFontHeightInPoints((short) 10);
				//选择需要用到的字体格式
				textBorder.setFont(textFont);
				
				//设置内容字体content
				XSSFCellStyle textBorder2 = wb.createCellStyle();
				// 居中
				textBorder2.setAlignment(HorizontalAlignment.CENTER);
				XSSFFont textFont1 = wb.createFont();
				textFont1.setFontName("宋体");
				//设置字体大小
				textFont1.setFontHeightInPoints((short) 11);
				//选择需要用到的字体格式
				textBorder2.setFont(textFont1);
				
				//设置内容字体
				XSSFCellStyle textBorderProjecrt = wb.createCellStyle();
				//下边框
				textBorderProjecrt.setBorderBottom(BorderStyle.THIN);
				//左边框
				textBorderProjecrt.setBorderLeft(BorderStyle.THIN);
				//上边框
				textBorderProjecrt.setBorderTop(BorderStyle.THIN);
				//右边框
				textBorderProjecrt.setBorderRight(BorderStyle.THIN);
				textBorderProjecrt.setAlignment(HorizontalAlignment.CENTER);
				//选择需要用到的字体格式
				textBorderProjecrt.setFont(textFont);
				
				//设置内容字体
				XSSFCellStyle textBorder1 = wb.createCellStyle();
				//下边框
				textBorder1.setBorderBottom(BorderStyle.THIN);
				//左边框
				textBorder1.setBorderLeft(BorderStyle.THIN);
				//上边框
				textBorder1.setBorderTop(BorderStyle.THIN);
				//右边框
				textBorder1.setBorderRight(BorderStyle.THIN);
				textBorder1.setAlignment(HorizontalAlignment.CENTER);
				//选择需要用到的字体格式
				textBorder1.setFont(textFont);
				
				//zjDetailData
				
				String ssyf = resultMap.get("ssqj") == null ? "" : String.valueOf(resultMap.get("ssqj"));
				String xfshs = xhfNsrsbh;
				String yueAndMonth = ssyf.substring(0, 4) + "年" + ssyf.substring(4, 6) + "月";
				//制表日期：2019年03月11日
				CreatCellValue(sheet, 0, 0, headTitle, headBorder);
				//增值税专用发票汇总表  2-8
				//制表日期：2019年03月11日
				CreatCellValue(sheet, 2, 1, resultMap.get("zbrq") == null ? "" : String.valueOf(resultMap.get("zbrq")), textBorder);
				//所属期间：2019年03月~~~2019年03月
				CreatCellValue(sheet, 3, 1, yueAndMonth + "-" + yueAndMonth, textBorder);
				//纳税人登记号：150301199811285326
				CreatCellValue(sheet, 5, 1, xfshs, textBorder);
				
				
				//*********************wrw**************写完用成上面的
				//企业名称：150301199811285326
				CreatCellValue(sheet, 6, 1, userInfoService.querySysDeptEntityByTaxplayercode(xfshs).getName(), textBorder);
				
				String address = userInfoService.querySysDeptEntityByTaxplayercode(xfshs).getTaxpayerAddress();
				String phone = userInfoService.querySysDeptEntityByTaxplayercode(xfshs).getTaxpayerPhone();
				
				//地址电话：
				CreatCellValue(sheet, 7, 1, address + phone, textBorder);
				// 单元格赋值
				//★发票领用存情况★11-13
				//期初库存份数
				CreatCellValue(sheet, 9, 1, (resultMap.get("qckcfs") == null ? "" : String.valueOf(resultMap.get("qckcfs"))), textBorder);
				//正数发票份数
				CreatCellValue(sheet, 9, 4, (resultMap.get("zsfpkjfs") == null ? "" : String.valueOf(resultMap.get("zsfpkjfs"))), textBorder);
				//负数发票份数
				CreatCellValue(sheet, 9, 7, (resultMap.get("fsfpkjfs") == null ? "" : String.valueOf(resultMap.get("fsfpkjfs"))), textBorder);
				//购进发票份数
				CreatCellValue(sheet, 10, 1, (resultMap.get("gjfpfs") == null ? "" : String.valueOf(resultMap.get("gjfpfs"))), textBorder);
				//正数废票份数
				CreatCellValue(sheet, 10, 4, (resultMap.get("zsfpzffs") == null ? "" : String.valueOf(resultMap.get("zsfpzffs"))), textBorder);
				//负数废票份数
				CreatCellValue(sheet, 10, 7, (resultMap.get("fsfpzffs") == null ? "" : String.valueOf(resultMap.get("fsfpzffs"))), textBorder);
				//退回发票份数
				CreatCellValue(sheet, 11, 1, (resultMap.get("thfpfs") == null ? "" : String.valueOf(resultMap.get("thfpfs"))), textBorder);
				//期末库存份数
				CreatCellValue(sheet, 11, 4, (resultMap.get("qmkcfs") == null ? "" : String.valueOf(resultMap.get("qmkcfs"))), textBorder);
				//分配发票份数
				CreatCellValue(sheet, 11, 7, (resultMap.get("fpfpfs") == null ? "" : String.valueOf(resultMap.get("fpfpfs"))), textBorder);
				/**
				 * 针对新税控服务对文本内容进行修改
				 */
				if (OrderInfoEnum.TAX_EQUIPMENT_NEWTAX.getKey().equals(terminalCode) || OrderInfoEnum.TAX_EQUIPMENT_UKEY.getKey().equals(terminalCode)) {
					//空废发票份数
					CreatCellValue(sheet, 11, 6, "空废发票份数", textBorder2);
					
				}
				//★销售情况★ 16-25
				
				//序号
				CreatCellValue(sheet, 14, 0, "序号", textBorderProjecrt);
				for (int i = 1; i < 11; i++) {
					CreatCellValue(sheet, i + 14, 0, i, textBorderProjecrt);
				}
				//项目名称
				CreatCellValue(sheet, 14, 1, "项目名称", textBorderProjecrt);
				CreatCellValue(sheet, 15, 1, "销项正数金额", textBorderProjecrt);
				CreatCellValue(sheet, 16, 1, "销项正数税额", textBorderProjecrt);
				CreatCellValue(sheet, 17, 1, "销项正废金额", textBorderProjecrt);
				CreatCellValue(sheet, 18, 1, "销项正废税额", textBorderProjecrt);
				CreatCellValue(sheet, 19, 1, "销项负数金额", textBorderProjecrt);
				CreatCellValue(sheet, 20, 1, "销项负数税额", textBorderProjecrt);
				CreatCellValue(sheet, 21, 1, "销项负废金额", textBorderProjecrt);
				CreatCellValue(sheet, 22, 1, "销项负废税额", textBorderProjecrt);
				CreatCellValue(sheet, 23, 1, "实际销项金额", textBorderProjecrt);
				CreatCellValue(sheet, 24, 1, "实际销项税额", textBorderProjecrt);
				
				//合计
				Map<String, String> hjjes = new HashMap<>(5);
				if (!OrderInfoEnum.TAX_EQUIPMENT_C48.equals(terminalCode)) {
					hjjes = (Map<String, String>) resultMap.get("hjjes");
				} else {
					hjjes = JsonUtils.getInstance().parseObject(String.valueOf(resultMap.get("hjjes")), Map.class);
				}
				
				CreatCellValue(sheet, 14, 2, "合计", textBorderProjecrt);
				CreatCellValue(sheet, 15, 2, hjjes.get("zsje"), textBorderProjecrt);
				CreatCellValue(sheet, 16, 2, hjjes.get("zsse"), textBorderProjecrt);
				CreatCellValue(sheet, 17, 2, hjjes.get("zfje"), textBorderProjecrt);
				CreatCellValue(sheet, 18, 2, hjjes.get("zfse"), textBorderProjecrt);
				CreatCellValue(sheet, 19, 2, hjjes.get("fsje"), textBorderProjecrt);
				CreatCellValue(sheet, 20, 2, hjjes.get("fsse"), textBorderProjecrt);
				CreatCellValue(sheet, 21, 2, hjjes.get("ffje"), textBorderProjecrt);
				CreatCellValue(sheet, 22, 2, hjjes.get("ffse"), textBorderProjecrt);
				CreatCellValue(sheet, 23, 2, hjjes.get("sjje"), textBorderProjecrt);
				CreatCellValue(sheet, 24, 2, hjjes.get("sjse"), textBorderProjecrt);
				
				List<Map> yhzxxs = new ArrayList<>();
				
				if (!OrderInfoEnum.TAX_EQUIPMENT_C48.getKey().equals(terminalCode)) {
					yhzxxs = (List<Map>) resultMap.get("yhzxxs");
				} else {
					yhzxxs = JSONArray.parseArray(String.valueOf(resultMap.get("yhzxxs")), Map.class);
				}
				
				int cellNum = 3;
				for (Map<String, Object> reMap : yhzxxs) {
					int rowNum = 14;
					//税率
					CreatCellValue(sheet, rowNum, cellNum++, StringUtil.formatSl(String.valueOf(reMap.get("sl"))), textBorderProjecrt);
					
					List<Map> childList = new ArrayList<>();
					if (!OrderInfoEnum.TAX_EQUIPMENT_C48.getKey().equals(terminalCode)) {
						childList = (List<Map>) reMap.get("mxxxs");
					} else {
						childList = JSONArray.parseArray(String.valueOf(reMap.get("mxxxs")), Map.class);
					}
					
					rowNum++;
					for (Map<String, Object> item : childList) {
						//项目
						CreatCellValue(sheet, rowNum++, cellNum - 1, String.valueOf(item.get("hjjese")), textBorderProjecrt);
					}
				}
				
				response.setContentType("octets/stream");
				String xlsName = "报表分析_" + DateUtils.getYYYYMMDDHHMMSSFormatStr(new Date());
				response.addHeader("Content-Disposition",
						"attachment;filename=" + new String(xlsName.getBytes("gb2312"), "ISO8859-1") + ".xls");
				out = response.getOutputStream();
				wb.write(out);
			}
		} catch (IOException e) {
			log.error("汇总信息导出异常，异常信息为:{}", e);
		} finally {
			if (out != null) {
				try {
					out.close();
				} catch (IOException e) {
					log.error("汇总信息导出异常，异常信息为:{}", e);
				}
			}
			if (resource != null) {
				try {
					resource.close();
				} catch (IOException e) {
					log.error("汇总信息导出异常，异常信息为:{}", e);
				}
			}
		}
	}
	
	
	/**
	 * <p>
	 * 功能实现描述
	 * </p>
	 *
	 * @param sheet     这个指的是这个excel表的第几页对象
	 * @param rowIndex  表示哪一行的index ,第一行为 index : 0,.....
	 * @param cellIndex 表示这一行哪一个单元格index,第一个单元格index : 0,...
	 * @param value     void 表示给具体的单元格设置值
	 * @author: 腾金玉
	 * @date: Created on 2017年2月15日 下午3:24:56
	 */
	private static void CreatCellValue(XSSFSheet sheet, int rowIndex, int cellIndex, Object value, XSSFCellStyle cellStyle) {
		XSSFRow row = sheet.getRow(rowIndex);
		XSSFCell createCell = row.getCell(cellIndex);
		if (createCell == null) {
			createCell = row.createCell(cellIndex);
		}
		if (null == value || StringUtils.isEmpty(value.toString())) {
			createCell.setCellValue("");
		} else {
			// 单元格赋值
			createCell.setCellValue(value.toString());
		}
		createCell.setCellStyle(cellStyle);
	}
	
	private List<Map<String, String>> testNewTax(String nsrsbh, String terminalCode, String kpnf, String kpyf, String fjh) {
		List<Map<String, String>> monthSummarInfoList = new ArrayList<>();
		DeptEntity sysDeptEntity = userInfoService.querySysDeptEntityByTaxplayercode(nsrsbh);
		
		// 根据分机号查询发票汇总数据
		SkReqYhzxxcx skRequset = new SkReqYhzxxcx();
		skRequset.setNsrsbh(nsrsbh);
		skRequset.setFpzlDm("026");
		skRequset.setSsyf(kpnf + kpyf);
		skRequset.setFjh(fjh);
		YhzxxResponse queryYhzxx = unifyService.queryYhzxx(skRequset, terminalCode);
		Map<String, String> map = new HashMap<>(5);
		if (queryYhzxx != null && OrderInfoContentEnum.SUCCESS.getKey().equals(queryYhzxx.getCode())) {
			YhzxxResponseExtend result = queryYhzxx.getResult();
			if (OrderInfoContentEnum.SUCCESS.getKey().equals(result.getStatusCode())) {
				
				List<FpYdtj> fpYdtj = result.getFpYdtj();
				if (!CollectionUtils.isEmpty(fpYdtj)) {
					
					for (FpYdtj ydtj : fpYdtj) {
						Map<String, String> mapData = new HashMap<>(5);
						Integer kpl = 0;
                        Integer kcyl = 0;
                        BigDecimal hjje = new BigDecimal("0.00");
                        BigDecimal hjse = new BigDecimal("0.00");

						hjje = hjje.add(new BigDecimal(ydtj.getZsjeTj())).subtract(new BigDecimal(ydtj.getZfjeTj())).subtract(new BigDecimal(ydtj.getFsjeTj())).add(new BigDecimal(ydtj.getFfjeTj()));
						
						hjse = hjse.add(new BigDecimal(ydtj.getZsseTj())).subtract(new BigDecimal(ydtj.getZfseTj())).subtract(new BigDecimal(ydtj.getFsseTj())).add(new BigDecimal(ydtj.getFfseTj()));
						
						if (StringUtils.isNotBlank(ydtj.getQmkc())) {
							kcyl += Integer.parseInt(ydtj.getQmkc());
						}
						if (StringUtils.isNotBlank(ydtj.getZsfp())) {
							kpl += Integer.parseInt(ydtj.getZsfp());
						}
						
						if (StringUtils.isNotBlank(ydtj.getFsfp())) {
							kpl += Integer.parseInt(ydtj.getFsfp());
						}
						// 对应虚拟设备号
						mapData.put("fjh", ydtj.getFjh());
						mapData.put("xhfmc", sysDeptEntity.getName());
						mapData.put("hjje", hjje.toString());
						mapData.put("hjse", hjse.toString());
						mapData.put("jshj", hjje.add(hjse).toString());
						mapData.put("kcyl", String.valueOf(kcyl));
						mapData.put("kpl", String.valueOf(kpl));
						mapData.put("nsrsbh", nsrsbh);
						mapData.put("kpnf", kpnf);
						mapData.put("kpyf", kpyf);
						mapData.put(ConfigureConstant.CODE, OrderInfoContentEnum.SUCCESS.getKey());
                        mapData.put(ConfigureConstant.MSG, OrderInfoContentEnum.SUCCESS.getMessage());
                        monthSummarInfoList.add(mapData);
					}
					
				}else {
                    map.put(ConfigureConstant.CODE, OrderInfoContentEnum.INVOICE_AUTO_DATA_NULL.getKey());
                    map.put(ConfigureConstant.MSG, OrderInfoContentEnum.INVOICE_AUTO_DATA_NULL.getMessage());
                    monthSummarInfoList.add(map);
                }
			}else {
                map.put(ConfigureConstant.CODE, result.getStatusCode());
                map.put(ConfigureConstant.MSG, result.getStatusMessage());
                monthSummarInfoList.add(map);
            }
		} else {
			map.put(ConfigureConstant.CODE, queryYhzxx.getCode());
			map.put(ConfigureConstant.MSG, queryYhzxx.getMsg());
            monthSummarInfoList.add(map);
			return monthSummarInfoList;
		}
		return monthSummarInfoList;
	}
	
	private static FpYdtj convertToFpYdtj(JSONObject result) {
		JSONObject jsonObject = result.getJSONObject("ydtjXx");
		JSONArray poList = jsonObject.getJSONObject("xxqks").getJSONArray("xxqk");
		List<Xxqk> xxqkList = JSON.parseArray(poList.toString(), Xxqk.class);
		
		//开始处理展示数据
		//先加一个表头
		List<Xx> xxtitle = xxqkList.get(0).getXxs().get(0).getXx();
		Map<String, String> map = new HashMap<>(10);
		if (null != xxtitle && xxtitle.size() > 0) {
			
			for (Xxqk item : xxqkList) {
				
				map.put(item.getMc(), new BigDecimal(item.getHj()).setScale(ConfigureConstant.INT_2, RoundingMode.HALF_UP).toPlainString());
			}
			
		}
		JSONObject information = jsonObject.getJSONObject("information");
		
		JSONObject tjinformation = jsonObject.getJSONObject("tjinformation");
		
		String ssyf = information.getString("ssrq");
		String xfshs = information.getString("nsrsbh");
		String[] yearMonth = ssyf.split("-");
		FpYdtj fpYdtj = new FpYdtj();
		fpYdtj.setId("");
		fpYdtj.setFpzl(result.getString("fpzlDm"));
		fpYdtj.setTjnf(yearMonth[0]);
		fpYdtj.setTjyf(yearMonth[1]);
		fpYdtj.setXsfNsrsbh(xfshs);
		fpYdtj.setXsfMc(information.getString("qymc"));
		fpYdtj.setXsfDzdh("");
		fpYdtj.setXsfYhzh("");
		fpYdtj.setFjh(result.getString("fjh"));
		fpYdtj.setZbsj(result.getString("cjsj").substring(0, 10));
		fpYdtj.setQckc(tjinformation.getString("qckcfs"));
		fpYdtj.setQmkc(tjinformation.getString("qmkcfs"));
		fpYdtj.setGjfp(tjinformation.getString("gjfpfs"));
		fpYdtj.setThfp(tjinformation.getString("thfpfs"));
		fpYdtj.setZjfp(tjinformation.getString("hjfs"));
		fpYdtj.setFjth(tjinformation.getString("qckcfs"));
		fpYdtj.setZsfp(tjinformation.getString("zsfpfs"));
		fpYdtj.setZszf(tjinformation.getString("zffpfs"));
		fpYdtj.setFsfp(tjinformation.getString("fsfpfs"));
		fpYdtj.setFszf(tjinformation.getString("fffpfs"));
		fpYdtj.setZfjeTj(map.get("销项正废金额"));
		fpYdtj.setZfseTj(map.get("销项正废税额"));
		fpYdtj.setZsjeTj(map.get("销项正数金额"));
		fpYdtj.setZsseTj(map.get("销项正数税额"));
		fpYdtj.setFfjeTj(map.get("销项负废金额"));
		fpYdtj.setFfseTj(map.get("销项负废税额"));
		fpYdtj.setFsjeTj(map.get("销项负数金额"));
		fpYdtj.setFsseTj(map.get("销项负数税额"));
		fpYdtj.setSjjeTj(map.get("实际销售金额"));
		fpYdtj.setSjseTj(map.get("实际销项税额"));
		fpYdtj.setCjsj("");
		fpYdtj.setGxsj("");
		fpYdtj.setTjxxmx(poList.toString());
		fpYdtj.setKffpfs(tjinformation.getString("kffpfs"));
		
		return fpYdtj;
	}
	
	/**
	 * 新税控月度统计详情
	 *
	 * @param fpYdtj
	 * @return
	 */
	private static Map<String, Object> convertToResultMapBwpz(FpYdtj fpYdtj) {
		Map<String, Object> resultMap = new HashMap<>(5);
		BigDecimal hjje = new BigDecimal("0.00");
		BigDecimal hjse = new BigDecimal("0.00");
		
		//外层数据解析 赋值 开具份数统计
		resultMap.put("nsrsbh", fpYdtj.getXsfNsrsbh());
		resultMap.put("fjh", fpYdtj.getFjh());
		resultMap.put("fpzlDm", fpYdtj.getFpzl());
		resultMap.put("qckcfs", fpYdtj.getQckc());
		resultMap.put("qmkcfs", fpYdtj.getQmkc());
		resultMap.put("gjfpfs", fpYdtj.getGjfp());
		resultMap.put("thfpfs", fpYdtj.getThfp());
		resultMap.put("shfpfs", fpYdtj.getThfp());
		resultMap.put("fpfpfs", fpYdtj.getKffpfs());
		resultMap.put("zsfpkjfs", fpYdtj.getZsfp());
		resultMap.put("zsfpzffs", fpYdtj.getZszf());
		resultMap.put("fsfpkjfs", fpYdtj.getFsfp());
		resultMap.put("fsfpzffs", fpYdtj.getFszf());
		resultMap.put("zbrq", fpYdtj.getZbsj());
		resultMap.put("ssqj", fpYdtj.getTjnf() + fpYdtj.getTjyf());
		
		//存放总计金额
		Map<String, Object> jeTjMap = new HashMap<>(5);
		//根据税率金额统计
		List<Map<String, Object>> slList = new ArrayList<>();
		
		//销项正数金额
		if (StringUtils.isNotBlank(fpYdtj.getZsjeTj())) {
			jeTjMap.put("zsje", fpYdtj.getZsjeTj());
		}
		
		//销项正数税额
		if (StringUtils.isNotBlank(fpYdtj.getZsseTj())) {
			jeTjMap.put("zsse", fpYdtj.getZsseTj());
		}
		
		//销项正废金额
		if (StringUtils.isNotBlank(fpYdtj.getZfjeTj())) {
			jeTjMap.put("zfje", fpYdtj.getZfjeTj());
		}
		//销项正废税额
		if (StringUtils.isNotBlank(fpYdtj.getZfseTj())) {
			jeTjMap.put("zfse", fpYdtj.getZfseTj());
		}
		//销项负数金额
		if (StringUtils.isNotBlank(fpYdtj.getFsjeTj())) {
			jeTjMap.put("fsje", fpYdtj.getFsjeTj());
		}
		//销项负数税额
		if (StringUtils.isNotBlank(fpYdtj.getFsseTj())) {
			jeTjMap.put("fsse", fpYdtj.getFsseTj());
		}
		//销项负废金额
		if (StringUtils.isNotBlank(fpYdtj.getFfjeTj())) {
			jeTjMap.put("ffje", fpYdtj.getFfjeTj());
		}
		//销项负废税额
		if (StringUtils.isNotBlank(fpYdtj.getFfseTj())) {
			jeTjMap.put("ffse", fpYdtj.getFfseTj());
		}
		//实际销项金额
		if (StringUtils.isNotBlank(fpYdtj.getSjjeTj())) {
			jeTjMap.put("sjje", fpYdtj.getSjjeTj());
		} else {
			hjje = hjje.add(new BigDecimal(fpYdtj.getZsjeTj())).subtract(new BigDecimal(fpYdtj.getZfjeTj())).subtract(new BigDecimal(fpYdtj.getFsjeTj())).add(new BigDecimal(fpYdtj.getFfjeTj()));
			jeTjMap.put("sjje", hjje);
		}
		//实际销项税额
		if (StringUtils.isNotBlank(fpYdtj.getSjseTj())) {
			jeTjMap.put("sjse", fpYdtj.getSjseTj());
		} else {
			hjse = hjse.add(new BigDecimal(fpYdtj.getZsseTj())).subtract(new BigDecimal(fpYdtj.getZfseTj())).subtract(new BigDecimal(fpYdtj.getFsseTj())).add(new BigDecimal(fpYdtj.getFfseTj()));
			jeTjMap.put("sjse", hjse);
		}
		resultMap.put("hjjes", jeTjMap);
		
		String tjxxmx = fpYdtj.getTjxxmx();
		if (StringUtils.isNotBlank(tjxxmx)) {
			slList = buildMapBwpz(JSON.parseArray(tjxxmx, Map.class));
			
		}
		
		//重写排序规
		slList.sort((o1, o2) -> {
			if (Double.parseDouble(String.valueOf(o1.get("sl"))) > Double.parseDouble(String.valueOf(o2.get("sl")))) {
				return -1;
			} else if (Double.parseDouble(String.valueOf(o1.get("sl"))) < Double.parseDouble(String.valueOf(o2.get("sl")))) {
				return 1;
			}
			return 0;
		});
		
		resultMap.put("yhzxxs", slList);
		return resultMap;
	}
	
	private static List<Map<String, Object>> buildMapBwpz(List<Map> zsjeMap) {
		/**
		 * 明细数据按照税率纬度进行处理
		 * 1.遍历所有税率对应的金额数据,放在一个统一的listmap中.
		 *
		 */
		
		
		Map<String, Map<String, Object>> slMap = new HashMap<>(5);
		List<Map<String, Object>> slList = new ArrayList<>();
		List<Map<String, Object>> resourceListMap = new ArrayList<>();
		
		/**
		 * 遍历所有map,组成税率列表
		 */
		for (int i = 0; i < zsjeMap.size(); i++) {
			
			Map map2 = zsjeMap.get(i);
			
			JSONArray mx = JSON.parseArray(map2.get("xxs").toString());
			JSONArray mxs = mx.getJSONObject(0).getJSONArray("xx");
			for (int j = 0; j < mxs.size(); j++) {
				JSONObject jsonObject = mxs.getJSONObject(j);
				Map<String, Object> resourceMap = new HashMap<>(10);
				resourceMap.put("mc", map2.get("mc"));
				resourceMap.put("sl", StringUtil.formatSl(String.valueOf(jsonObject.get("sl"))));
				resourceMap.put("se", DecimalCalculateUtil.decimalFormatToString(String.valueOf(jsonObject.get("se")), ConfigureConstant.INT_2));
				resourceListMap.add(resourceMap);
			}
		}
		
		/**
		 * 处理税率列表,转换成税率分组
		 */
		
		for (Map<String, Object> objectMap : resourceListMap) {
			String mxsl = StringUtil.formatSl(String.valueOf(objectMap.get("sl")));
			String mxje = DecimalCalculateUtil.decimalFormatToString(String.valueOf(objectMap.get("se")), ConfigureConstant.INT_2);
			String mxmc = objectMap.get("mc").toString();
			if (slMap.containsKey(mxsl)) {
				Map<String, Object> stringObjectMap = slMap.get(mxsl);
				List<Map> mxxxs = (List<Map>) stringObjectMap.get("mxxxs");
				Map<String, String> mxxx = new HashMap<>(2);
				mxxx.put("kplxmc", mxmc);
				mxxx.put("hjjese", mxje);
				mxxxs.add(mxxx);
				stringObjectMap.put("mxxxs", mxxxs);
			} else {
				Map<String, Object> stringObjectMap = new HashMap<>(2);
				stringObjectMap.put("sl", mxsl);
				List<Map> mxxxs = new ArrayList<>();
				Map<String, String> mxxx = new HashMap<>(2);
				mxxx.put("kplxmc", mxmc);
				mxxx.put("hjjese", mxje);
				mxxxs.add(mxxx);
				stringObjectMap.put("mxxxs", mxxxs);
				slMap.put(mxsl, stringObjectMap);
				slList.add(slMap.get(mxsl));
			}
			
			
		}
		/**
		 * 处理排序
		 */
		for (Map<String, Object> stringObjectMap : slList) {
			List<Map> mxxxMap = (List<Map>) stringObjectMap.get("mxxxs");
			List<Map> newMxxxMap = new ArrayList<>();
			newMxxxMap.addAll(mxxxMap);
			for (Map map : mxxxMap) {
				if ("销项正数金额".equals(map.get("kplxmc"))) {
					newMxxxMap.set(0, map);
				} else if ("销项正数税额".equals(map.get("kplxmc"))) {
					newMxxxMap.set(1, map);
				} else if ("销项正废金额".equals(map.get("kplxmc"))) {
					newMxxxMap.set(2, map);
				} else if ("销项正废税额".equals(map.get("kplxmc"))) {
					newMxxxMap.set(3, map);
				} else if ("销项负数金额".equals(map.get("kplxmc"))) {
					newMxxxMap.set(4, map);
				} else if ("销项负数税额".equals(map.get("kplxmc"))) {
					newMxxxMap.set(5, map);
				} else if ("销项负废金额".equals(map.get("kplxmc"))) {
					newMxxxMap.set(6, map);
				} else if ("销项负废税额".equals(map.get("kplxmc"))) {
					newMxxxMap.set(7, map);
				} else if ("实际销售金额".equals(map.get("kplxmc"))) {
					map.put("kplxmc", "实际销项金额");
					newMxxxMap.set(8, map);
				} else if ("实际销项税额".equals(map.get("kplxmc"))) {
					newMxxxMap.set(9, map);
				}
			}
			stringObjectMap.put("mxxxs", newMxxxMap);
		}
		
		return slList;
	}
	
	/**
	 * 新税控月度统计详情
	 *
	 * @param fpYdtj
	 * @return
	 */
	private Map<String, Object> convertToResultMapNewTax(FpYdtj fpYdtj) {
		Map<String, Object> resultMap = new HashMap<>(5);
		BigDecimal hjje = new BigDecimal("0.00");
		BigDecimal hjse = new BigDecimal("0.00");
		
		//外层数据解析 赋值 开具份数统计
		resultMap.put("nsrsbh", fpYdtj.getXsfNsrsbh());
		resultMap.put("fjh", fpYdtj.getFjh());
		resultMap.put("fpzlDm", fpYdtj.getFpzl());
		resultMap.put("qckcfs", fpYdtj.getQckc());
		resultMap.put("qmkcfs", fpYdtj.getQmkc());
		resultMap.put("gjfpfs", fpYdtj.getGjfp());
        resultMap.put("thfpfs", fpYdtj.getThfp());
		resultMap.put("shfpfs", fpYdtj.getThfp());
		resultMap.put("fpfpfs", fpYdtj.getKffpfs());
		resultMap.put("zsfpkjfs", fpYdtj.getZsfp());
		resultMap.put("zsfpzffs", fpYdtj.getZszf());
		resultMap.put("fsfpkjfs", fpYdtj.getFsfp());
		resultMap.put("fsfpzffs", fpYdtj.getFszf());
		resultMap.put("zbrq", fpYdtj.getZbsj());
		resultMap.put("ssqj", fpYdtj.getTjnf() + fpYdtj.getTjyf());
		
		//存放总计金额
		Map<String, Object> jeTjMap = new HashMap<>(5);
		//根据税率金额统计
		List<Map<String, Object>> slList = new ArrayList<>();
		
		//销项正数金额
		if (StringUtils.isNotBlank(fpYdtj.getZsjeTj())) {
			jeTjMap.put("zsje", fpYdtj.getZsjeTj());
		}
		
		//销项正数税额
		if (StringUtils.isNotBlank(fpYdtj.getZsseTj())) {
			jeTjMap.put("zsse", fpYdtj.getZsseTj());
		}
		
		//销项正废金额
		if (StringUtils.isNotBlank(fpYdtj.getZfjeTj())) {
			jeTjMap.put("zfje", fpYdtj.getZfjeTj());
		}
		//销项正废税额
		if (StringUtils.isNotBlank(fpYdtj.getZfseTj())) {
			jeTjMap.put("zfse", fpYdtj.getZfseTj());
		}
		//销项负数金额
		if (StringUtils.isNotBlank(fpYdtj.getFsjeTj())) {
			jeTjMap.put("fsje", fpYdtj.getFsjeTj());
		}
		//销项负数税额
		if (StringUtils.isNotBlank(fpYdtj.getFsseTj())) {
			jeTjMap.put("fsse", fpYdtj.getFsseTj());
		}
		//销项负废金额
		if (StringUtils.isNotBlank(fpYdtj.getFfjeTj())) {
			jeTjMap.put("ffje", fpYdtj.getFfjeTj());
		}
		//销项负废税额
		if (StringUtils.isNotBlank(fpYdtj.getFfseTj())) {
			jeTjMap.put("ffse", fpYdtj.getFfseTj());
		}
		//实际销项金额
		if (StringUtils.isNotBlank(fpYdtj.getSjjeTj())) {
			jeTjMap.put("sjje", fpYdtj.getSjjeTj());
		} else {
			hjje = hjje.add(new BigDecimal(fpYdtj.getZsjeTj())).subtract(new BigDecimal(fpYdtj.getZfjeTj())).subtract(new BigDecimal(fpYdtj.getFsjeTj())).add(new BigDecimal(fpYdtj.getFfjeTj()));
			jeTjMap.put("sjje", hjje);
		}
		//实际销项税额
		if (StringUtils.isNotBlank(fpYdtj.getSjseTj())) {
			jeTjMap.put("sjse", fpYdtj.getSjseTj());
		} else {
			hjse = hjse.add(new BigDecimal(fpYdtj.getZsseTj())).subtract(new BigDecimal(fpYdtj.getZfseTj())).subtract(new BigDecimal(fpYdtj.getFsseTj())).add(new BigDecimal(fpYdtj.getFfseTj()));
			jeTjMap.put("sjse", hjse);
		}
		resultMap.put("hjjes", jeTjMap);
		
		String tjxxmx = fpYdtj.getTjxxmx();
		if (StringUtils.isNotBlank(tjxxmx)) {
			JSONArray objects = JSONObject.parseArray(tjxxmx);
			for (Object object : objects) {
				String string = object.toString();
				Map<String, Object> map = JsonUtils.getInstance().parseObject(string, Map.class);
				if (map.size() > 0) {
					List<Map> list = (List) map.get("mxxxs");
					String sl = map.get("sl").toString();
					Map<String, Map<String, Object>> stringMapMap = buildMapNewTax(list, sl);
					Map<String, Object> map1 = stringMapMap.get(sl);
					slList.add(map1);
				}
				
			}
		}
		
		//重写排序规
		slList.sort((o1, o2) -> {
			if (Double.parseDouble(String.valueOf(o1.get("sl"))) > Double.parseDouble(String.valueOf(o2.get("sl")))) {
				return -1;
			} else if (Double.parseDouble(String.valueOf(o1.get("sl"))) < Double.parseDouble(String.valueOf(o2.get("sl")))) {
				return 1;
			}
			return 0;
		});
		
		resultMap.put("yhzxxs", slList);
		return resultMap;
	}
	
	

	private Map<String, Map<String, Object>> buildMapNewTax(List<Map> zsjeMap, String sl) {
		Map<String, Map<String, Object>> slMap = new HashMap<>(5);
		BigDecimal hjje = new BigDecimal("0.00");
		BigDecimal hjse = new BigDecimal("0.00");
		
		for (Map map2 : zsjeMap) {
			Map<String, Object> map = new HashMap<>(5);
			List<Map<String, Object>> list = new ArrayList<>();
			Map<String, Object> jeMap = new HashMap<>(5);
			jeMap.put("kplxmc", map2.get("kplxmc"));
			jeMap.put("hjjese", map2.get("hjjese"));
			
			Map<String, Object> map1 = slMap.get(sl);
			if (!ObjectUtils.isEmpty(map1)) {
				List mxxxsList = (List) map1.get("mxxxs");
				mxxxsList.add(jeMap);
				map1.put("mxxxs", mxxxsList);
				map = map1;
			} else {
				list.add(jeMap);
				map.put("mxxxs", list);
				map.put("sl", sl);
			}
			slMap.put(sl, map);
		}
		Map<String, Object> map = slMap.get(sl);
		List<Map<String, Object>> mxxxs = (List<Map<String, Object>>) map.get("mxxxs");
		
		for (int u = 0; u < 2; u++) {
			if (u == 0) {
				Map<String, Object> jeMap = new HashMap<>(5);
				for (Map value : zsjeMap) {
					if ("销项正数金额".equals(value.get("kplxmc"))) {
						hjje = hjje.add(new BigDecimal(value.get("hjjese").toString()));
					}
					if ("销项正废金额".equals(value.get("kplxmc"))) {
						hjje = hjje.subtract(new BigDecimal(value.get("hjjese").toString()));
					}
					if ("销项负数金额".equals(value.get("kplxmc"))) {
						hjje = hjje.subtract(new BigDecimal(value.get("hjjese").toString()));
					}
					if ("销项负废金额".equals(value.get("kplxmc"))) {
						hjje = hjje.add(new BigDecimal(value.get("hjjese").toString()));
					}
				}
				jeMap.put("kplxmc", "实际销项金额");
				jeMap.put("hjjese", hjje.toString());
				mxxxs.add(jeMap);
				
			}
			if (u == 1) {
				Map<String, Object> jeMap = new HashMap<>(5);
				for (Map value : zsjeMap) {
					
					if ("销项正数税额".equals(value.get("kplxmc"))) {
						hjse = hjse.add(new BigDecimal(value.get("hjjese").toString()));
					}
					if ("销项正废税额".equals(value.get("kplxmc"))) {
						hjse = hjse.subtract(new BigDecimal(value.get("hjjese").toString()));
					}
					if ("销项负数税额".equals(value.get("kplxmc"))) {
						hjse = hjse.subtract(new BigDecimal(value.get("hjjese").toString()));
					}
					if ("销项负废税额".equals(value.get("kplxmc"))) {
						hjse = hjse.add(new BigDecimal(value.get("hjjese").toString()));
					}
				}
                jeMap.put("kplxmc", "实际销项税额");
                jeMap.put("hjjese", hjse.toString());
                mxxxs.add(jeMap);
            }
        }
		map.put("mxxxs", mxxxs);
		map.put("sl", sl);
		slMap.put(sl, map);
		return slMap;
    }
	
}

