package com.dxhy.order.service.impl;

import cn.hutool.core.util.ObjectUtil;
import com.dxhy.order.api.ApiCommodityService;
import com.dxhy.order.api.ApiInvoiceCommonService;
import com.dxhy.order.api.ApiTaxClassCodeService;
import com.dxhy.order.constant.*;
import com.dxhy.order.dao.*;
import com.dxhy.order.model.CommodityCodeInfo;
import com.dxhy.order.model.CommodityTaxClassCodeParam;
import com.dxhy.order.model.PageUtils;
import com.dxhy.order.model.R;
import com.dxhy.order.model.entity.CommodityCodeEntity;
import com.dxhy.order.model.entity.DictionaryEntity;
import com.dxhy.order.model.entity.SysDictionary;
import com.dxhy.order.model.entity.TaxClassCodeEntity;
import com.dxhy.order.utils.*;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.dubbo.config.annotation.Service;
import org.springframework.util.ObjectUtils;

import javax.annotation.Resource;
import java.util.*;

/**
 *
 * @author liangyuhuan
 * @date 2018/7/23
 */
@Slf4j
@Service
public class CommodityServiceImpl implements ApiCommodityService {

    @Resource
    private CommodityDao commodityDao;
	@Resource
    private GroupCommodityDao goupCommodityDao;
    @Resource
    private ApiInvoiceCommonService apiInvoiceCommonService;
	@Resource
    private TaxClassCodeDao taxClassCodeDao;
	@Resource
    private DictionaryDao baseMapper;
	@Resource
    private GroupTaxClassCodeMapper groupTaxClassCodeMapper;
    @Resource
    private ApiTaxClassCodeService taxClassCodeService;
    /**
     * 查询
     * @param map
     * @return
     */
    @Override
    public PageUtils queryCommodity(Map<String, Object> map,List<String> xhfNsrsbh) {
	    int pageSize = Integer.parseInt((String) map.get("limit"));
	    int currPage = Integer.parseInt((String) map.get("page"));
	    PageHelper.startPage(currPage, pageSize);
	    List<CommodityCodeEntity> selectCommodity = commodityDao.selectCommodity(map, xhfNsrsbh);
	    selectCommodity.forEach(commodityCodeEntity -> {
		    if (StringUtils.isNotBlank(commodityCodeEntity.getTaxRate())) {
			    commodityCodeEntity.setTaxRate(StringUtil.formatSl(commodityCodeEntity.getTaxRate()));
		    }
	    });
	    PageInfo<CommodityCodeEntity> pageInfo = new PageInfo<>(selectCommodity);
	    PageUtils page = new PageUtils(pageInfo.getList(), (int) pageInfo.getTotal(), pageInfo.getPageSize(), pageInfo.getPageNum());
	    return page;
    }
    /**
     * 保存
     * @param codeEntity
     * @return
     */
    @Override
    public boolean addOrEditCommodity(CommodityCodeEntity codeEntity) {
	    List<String> shList = NsrsbhUtils.transShListByNsrsbh(codeEntity.getXhfNsrsbh());
	
	    boolean flag;
	    //判断保存或者修改操作
	    String id = codeEntity.getId();
	    if (StringUtils.isNotBlank(id)) {
		    log.info("修改操作 id = {}", id);
		    if (StringUtils.isNotEmpty(codeEntity.getTaxClassCode())) {
			    //已匹配
			    codeEntity.setMatchingState(TaxClassCodeEnum.MATCHING_STATE_0.getKey());
			    //启用
			    codeEntity.setDataState(TaxClassCodeEnum.DATA_STATE_0.getKey());
		    }
		    int i = commodityDao.updateCommodity(codeEntity, shList);
            flag = i > 0;
        }else{
            String uuid = apiInvoiceCommonService.getGenerateShotKey();
            codeEntity.setId(uuid);
            codeEntity.setDataSource(TaxClassCodeEnum.DATA_SOURCE_1.getKey());
            if(StringUtils.isNotEmpty(codeEntity.getTaxClassificationName())&&StringUtils.isNotEmpty(codeEntity.getTaxClassCode())) {
	            //税收分类名称和税收分类编码都存在
	            codeEntity.setMatchingState(TaxClassCodeEnum.MATCHING_STATE_0.getKey());
	            //已匹配默认为启用
	            codeEntity.setDataState(TaxClassCodeEnum.DATA_STATE_0.getKey());
            }else {
	            codeEntity.setMatchingState(TaxClassCodeEnum.MATCHING_STATE_1.getKey());
	            //未匹配默认为停用
	            codeEntity.setDataState(TaxClassCodeEnum.DATA_STATE_1.getKey());
            }
            log.info("新增操作 id = {}",uuid);
            String merchandiseName = codeEntity.getMerchandiseName();
		    String encoding = codeEntity.getEncoding();
		    Map<String, String> map1 = new HashMap<>(10);
		    map1.put("productName", merchandiseName);
		    map1.put("zxbm", encoding);
		
		    //检验名称是否存在
		    log.info("校验名称是否存在 参数,", map1.toString());
		    List<CommodityCodeEntity> commodityCodeEntityList = commodityDao.queryProductList(map1, shList);
		    int i;
		    if (commodityCodeEntityList.size() > 0) {
			    codeEntity.setId(commodityCodeEntityList.get(0).getId());
			    i = commodityDao.updateCommodity(codeEntity, shList);
			    log.info("修改成功");
		    } else {
			    i = commodityDao.insertCommodity(codeEntity);
			    log.info("添加成功");
		    }
		    flag = i > 0;
	    }
        return flag;
    }

    /**
     * 删除
     *
     * @param ids
     * @return
     */
    @Override
    public R deleteCommodity(List<Map> ids) {
	    R r = new R();
	    log.info("参数 id = {}", ids);
	    for (Map map : ids) {
		    String id = (String) map.get("id");
		    String nsrsbh = (String) map.get("xhfNsrsbh");
		    List<String> shList = new ArrayList<>();
		    shList.add(nsrsbh);
		    int i = commodityDao.deleteCommodity(id, shList);
		    if (i > 0) {
			    R.ok().put("msg", "删除成功");
		    } else {
			    R.ok().put("msg", "删除失败");
		    }
	    }
	    return r;
    }

    /**
     * 根据id查询
     * @param id
     * @return
     */
    @Override
    public R queryCommodityById(String id,List<String> xhfNsrsbh) {
	    Map<String, Object> commodity = commodityDao.queryCommodityById(id, xhfNsrsbh);
	    if (ObjectUtils.isEmpty(commodity)) {
		    return R.error("商品信息不存在");
	    }
	    if (ObjectUtil.isNotNull(commodity.get("taxRate")) && String.valueOf(commodity.get("taxRate")).contains(ConfigureConstant.STRING_PERCENT)) {
		    commodity.put("taxRate", StringUtil.formatSl(String.valueOf(commodity.get("taxRate"))));
	    }
	    return R.ok().put("data", commodity);
    }
	
	/**
	 * 校验
	 *
	 * @param map
	 * @return
	 */
	@Override
	public R checkRepeat(Map<String, String> map, List<String> shList) {
		log.info("参数 ：{}", map);
		R r = new R();
		String deptId = map.get("deptId");
		//商品名称校验
		String merchandiseName = map.get("merchandiseName");
		String encoding = map.get("encoding");
		if (StringUtils.isNotBlank(merchandiseName)) {
			int i = 1;
			if (StringUtils.isNotEmpty(deptId)) {
				//集团商品名称校验
				i = groupTaxClassCodeMapper.selectByName(map);
				log.info("商品名称数量：{}", i);
				if (i == 0) {
					R.ok().put("msg", "检验通过");
				} else {
					r.put(OrderManagementConstant.CODE, ConfigureConstant.STRING_9999).put("msg", "商品名称：" + map.get("merchandiseName") + "已经存在");
				}
			} else {
				
				i = commodityDao.selectByNameAndCode(map, shList);
				if (i == 0) {
					R.ok().put("msg", "检验通过");
				} else {
					if (StringUtils.isNotBlank(encoding)) {
						r.put(OrderManagementConstant.CODE, ConfigureConstant.STRING_9999).put("msg", "商品名称：" + merchandiseName + "和商品编码：" + encoding + "已经存在");
					} else {
						r.put(OrderManagementConstant.CODE, ConfigureConstant.STRING_9999).put("msg", "商品名称：" + merchandiseName + " 已经存在");
					}
				}
			}
		}
		if (StringUtils.isNotEmpty(deptId)) {
			//集团商品编码校验
			if (StringUtils.isNotBlank(encoding)) {
				int k = 1;
				k = groupTaxClassCodeMapper.selectByCode(map);
				log.info("商品编码数量：{}", k);
				if (k == 0) {
					R.ok().put("msg", "检验通过");
				} else {
					r.put(OrderManagementConstant.CODE, ConfigureConstant.STRING_9999).put("msg", "商品编码：" + map.get("encoding") + "已经存在");
				}
			}
        }
        return r;
    }

    @Override
    public R uploadCommodityCode(List<CommodityCodeEntity> commodityCodeEntityList) {
        R r = checkParams(commodityCodeEntityList);
        return r;
    }
    
    
	@Override
	public R checkParams(List<CommodityCodeEntity> commodityCodeList) {
		
		List<Map<String, Object>> list = new ArrayList<>();
		int k = 2;
		int num = 0;
		Map<String, Object> map = new HashMap<>(10);
		Map<String, Object> errorMap = new HashMap<>(10);
		List<String> errorMsg = new ArrayList<>();
		
		if (CollectionUtils.isEmpty(commodityCodeList)) {
			map.put(OrderManagementConstant.CODE, ConfigureConstant.STRING_9999);
			List<String> resultList = new ArrayList<>();
			resultList.add("请输入需要导入的商品编码");
			map.put("msg", resultList);
			list.add(map);
			return R.error().put(OrderManagementConstant.CODE, ResponseStatusCodes.PRODUCT_PRODUCT_NAME)
					.put("list", list).put("count", "0").put("fail", list.size());
		}
		
		for (CommodityCodeEntity commodityCodeEntity : commodityCodeList) {
            
            errorMap = new HashMap<>(10);
            errorMsg = new ArrayList<>();
            map.put(OrderManagementConstant.CODE, ConfigureConstant.STRING_0000);
            k++;
            // 企业名称
            String enterpriseName = commodityCodeEntity.getEnterpriseName();
            if (StringUtils.isNotBlank(enterpriseName)) {
                if (GBKUtil.getGBKLength(enterpriseName) > 100) {
                    map.put(OrderManagementConstant.CODE, ConfigureConstant.STRING_9999);
                    errorMsg.add("企业名称不能大于100个字节");
                }
            } else {
                map.put(OrderManagementConstant.CODE, ConfigureConstant.STRING_9999);
				errorMsg.add("企业名称不能为空");
			}
			// 企业税号
			String taxpayerCode = commodityCodeEntity.getXhfNsrsbh();
			if (StringUtils.isNotBlank(taxpayerCode)) {
				// 是否包含空格
				if (taxpayerCode.contains(" ")) {
					map.put(OrderManagementConstant.CODE, ConfigureConstant.STRING_9999);
					errorMsg.add("企业税号不能包含空格");
				}
				// 判断税号长度合法性问题,长度必须15,17,18,20位
				if (ConfigureConstant.INT_15 != ValidateUtil.getStrBytesLength(taxpayerCode)
						&& ConfigureConstant.INT_17 != ValidateUtil.getStrBytesLength(taxpayerCode)
						&& ConfigureConstant.INT_18 != ValidateUtil.getStrBytesLength(taxpayerCode)
						&& ConfigureConstant.INT_20 != ValidateUtil.getStrBytesLength(taxpayerCode)) {
					map.put(OrderManagementConstant.CODE, ConfigureConstant.STRING_9999);
					errorMsg.add("企业税号长度不合法，必须为15,17,18,20位");
				}
				// 纳税人识别号需要全部大写
				if (!ValidateUtil.isAcronym(taxpayerCode)) {
					map.put(OrderManagementConstant.CODE, ConfigureConstant.STRING_9999);
					errorMsg.add("企业税号需要全部大写");
				}
			} else {
				map.put(OrderManagementConstant.CODE, ConfigureConstant.STRING_9999);
				errorMsg.add("企业税号不能为空");
			}
			// 编码
			String encoding = commodityCodeEntity.getEncoding();
			String codeVerify = "^.{2,20}$";
			if (StringUtils.isNotBlank(encoding)) {
				if (!encoding.matches(codeVerify)) {
					map.put(OrderManagementConstant.CODE, ConfigureConstant.STRING_9999);
					errorMsg.add("编码 " + encoding + " 不符合规范，长度为2-20位");
				}
			}
			// 商品名称校验
			String merchandiseName = commodityCodeEntity.getMerchandiseName();
			if (StringUtils.isNotBlank(merchandiseName)) {
				if (GBKUtil.getGBKLength(merchandiseName) > 90) {
					map.put(OrderManagementConstant.CODE, ConfigureConstant.STRING_9999);
					errorMsg.add("商品名称不能大于90个字节");
				} else {
					Map<String, String> map1 = new HashMap<>(10);
					map1.put("encoding", encoding);
					map1.put("merchandiseName", merchandiseName);
					List<String> shList = new ArrayList<>();
					shList.add(commodityCodeEntity.getXhfNsrsbh());
					int i = commodityDao.selectByNameAndCode(map1, shList);
					if (i > 0) {
						if (StringUtils.isNotEmpty(encoding)) {
							map.put(OrderManagementConstant.CODE, ConfigureConstant.STRING_9999);
							errorMsg.add("商品名称" + merchandiseName + " 和商品编码 " + encoding + " 已经存在");
						} else {
							map.put(OrderManagementConstant.CODE, ConfigureConstant.STRING_9999);
							errorMsg.add("商品名称" + merchandiseName + " 已经存在");
						}
						
					}
				}
			} else {
				map.put(OrderManagementConstant.CODE, ConfigureConstant.STRING_9999);
				errorMsg.add("未填写商品名称");
			}
			
			// 简码
			String briefCode = commodityCodeEntity.getBriefCode();
			String briefCodeVerify = "^[A-Z][A-Za-z0-9]{0,5}$";
			if (StringUtils.isNotBlank(briefCode)) {
				if (!briefCode.matches(briefCodeVerify)) {
					map.put(OrderManagementConstant.CODE, ConfigureConstant.STRING_9999);
					errorMsg.add("简码 " + briefCode + " 不符合规则，只能为数字和字母");
				}
			}
			// 商品税目
			String taxItems = commodityCodeEntity.getTaxItems();
			if (StringUtils.isNotBlank(taxItems)) {
				int length = taxItems.length();
				if (length > 4) {
					map.put(OrderManagementConstant.CODE, ConfigureConstant.STRING_9999);
					errorMsg.add("商品税目 " + taxItems + " 长度不能大于4个字符");
				}
			}
			// 税率 17% 16% 11% 10% 6% 5% 3% 0% 减按1.5%计算 中外合作油气田
			String taxRate = commodityCodeEntity.getTaxRate();
			if (StringUtils.isNotBlank(taxRate)) {
				List<DictionaryEntity> data = baseMapper.selectDictionaries("taxRate");
				boolean reslut = false;
				for (DictionaryEntity dict : data) {
					if (taxRate.equals(dict.getValue())) {
						reslut = true;
					}
				}
				if (!reslut) {
					map.put(OrderManagementConstant.CODE, ConfigureConstant.STRING_9999);
					errorMsg.add("税率 " + taxRate + " 不符合规范");
				}
			}
			// 规格型号
			String specificationModel = commodityCodeEntity.getSpecificationModel();
			if (StringUtils.isNotBlank(specificationModel)) {
				if (GBKUtil.getGBKLength(specificationModel) > 40) {
					map.put(OrderManagementConstant.CODE, ConfigureConstant.STRING_9999);
					errorMsg.add("规格型号长度不能大于40字节");
				}
			}
			// 计量单位
			String meteringUnit = commodityCodeEntity.getMeteringUnit();
			if (StringUtils.isNotBlank(meteringUnit)) {
				if (GBKUtil.getGBKLength(meteringUnit) > 20) {
					map.put(OrderManagementConstant.CODE, ConfigureConstant.STRING_9999);
					errorMsg.add("计量单位长度不能大于20字节");
				}
			}
			// 单价
			String unitPrice = commodityCodeEntity.getUnitPrice();
			String priceVerify = "^-?([0-9]\\d*(\\.\\d*)?|(0\\.\\d*)?[0-9]\\d*)$";
			if (StringUtils.isNotBlank(unitPrice)) {
				if (!unitPrice.matches(priceVerify)) {
					map.put(OrderManagementConstant.CODE, ConfigureConstant.STRING_9999);
					errorMsg.add("单价必须是数字");
				}
				
				if (unitPrice.contains(ConfigureConstant.STRING_POINT)) {
					if (unitPrice.substring(unitPrice.indexOf(ConfigureConstant.STRING_POINT) + 1)
							.length() > ConfigureConstant.INT_8) {
						map.put(OrderManagementConstant.CODE, ConfigureConstant.STRING_9999);
						errorMsg.add("单价小数点后不能大于8位");
					}
				}
			}
			// 含税价标志（0：否，1：是）',
			String taxLogo = commodityCodeEntity.getTaxLogo();
			if (StringUtils.isNotBlank(taxLogo)) {
				if ("是".equals(taxLogo)) {
					// 默认含税 必须含税
					commodityCodeEntity.setTaxLogo(OrderInfoEnum.HSBZ_1.getKey());
				} else if ("否".equals(taxLogo)) {
					commodityCodeEntity.setTaxLogo(OrderInfoEnum.HSBZ_0.getKey());
				} else {
					map.put(OrderManagementConstant.CODE, ConfigureConstant.STRING_9999);
					errorMsg.add("请输入正确的含税价标志(必须为 是/否)");
				}
			}
			// 税收分类编码
			String taxClassCode = commodityCodeEntity.getTaxClassCode();
			// 补齐税收分类编码 不足19位 自动不补齐19位
			taxClassCode = polishingTaxCode(taxClassCode);
			commodityCodeEntity.setTaxClassCode(taxClassCode);
			String tax = "^[0-9]{0,19}$";
			if (StringUtils.isNotBlank(taxClassCode)) {
				if (!taxClassCode.matches(tax)) {
					map.put(OrderManagementConstant.CODE, ConfigureConstant.STRING_9999);
					errorMsg.add("税收分类编码 " + taxClassCode + " 不符合规范，只能为数字");
				} else {
					TaxClassCodeEntity taxClassCodeEntity = taxClassCodeService.queryTaxClassCodeEntity(taxClassCode);
					if (taxClassCodeEntity != null) {
						commodityCodeEntity.setTaxClassAbbreviation(taxClassCodeEntity.getSpjc());
						commodityCodeEntity.setTaxClassificationName(taxClassCodeEntity.getSpmc());
					}
				}
			}
			// 商品信息校验
			if (StringUtils.isNotBlank(taxClassCode)) {
				// 有税编信息
				Map<String, Object> paramMap = new HashMap<>(5);
				paramMap.put("spbm", encoding);
				paramMap.put("spmc", merchandiseName);
				paramMap.put("nsrsbh", commodityCodeEntity.getXhfNsrsbh());
				paramMap.put("ssbm", taxClassCode);
				List<String> shList = new ArrayList<>();
				shList.add(commodityCodeEntity.getXhfNsrsbh());
				int count = commodityDao.queryCommodityByMap(paramMap, shList);
				if (count > 0) {
					// 商品信息和税编信息存在
					map.put(OrderManagementConstant.CODE, ConfigureConstant.STRING_9999);
					errorMsg.add("商品信息和税编信息已经存在,商品编码：" + encoding + ",商品名称：" + merchandiseName + ",纳税人识别号："
							+ commodityCodeEntity.getXhfNsrsbh() + ";税收分类编码 " + taxClassCode + " 不符合规范");
				}
			}

			// 隐藏标志（0：否，1：是）',
			String hideTheLogo = commodityCodeEntity.getHideTheLogo();
			hideTheLogo = StringUtils.isEmpty(hideTheLogo) ? "否" : hideTheLogo;
			if (StringUtils.isNotBlank(hideTheLogo)) {
				if ("是".equals(hideTheLogo)) {
					commodityCodeEntity.setHideTheLogo(ConfigureConstant.STRING_1);
				} else if ("否".equals(hideTheLogo)) {
					commodityCodeEntity.setHideTheLogo(ConfigureConstant.STRING_0);
				} else {
					map.put(OrderManagementConstant.CODE, ConfigureConstant.STRING_9999);
					errorMsg.add("隐藏标志 " + hideTheLogo + " 不符合规范 (是/否)");
				}
			} else {
				// 不填 默认为否
				commodityCodeEntity.setHideTheLogo("0");
			}
			// 是否享受优惠政策
			String enjoyPreferentialPolicies = commodityCodeEntity.getEnjoyPreferentialPolicies();
			enjoyPreferentialPolicies = StringUtils.isEmpty(enjoyPreferentialPolicies) ? "否"
					: enjoyPreferentialPolicies;
			if (StringUtils.isNotBlank(enjoyPreferentialPolicies)) {
				if ("是".equals(enjoyPreferentialPolicies) || "否".equals(enjoyPreferentialPolicies)) {
					if ("是".equals(enjoyPreferentialPolicies)) {
						// '享受优惠政策（1：是，0：否）',
						commodityCodeEntity.setEnjoyPreferentialPolicies(OrderInfoEnum.YHZCBS_1.getKey());
					} else if ("否".equals(enjoyPreferentialPolicies)) {
						commodityCodeEntity.setEnjoyPreferentialPolicies(OrderInfoEnum.YHZCBS_0.getKey());
					}
				} else {
					map.put(OrderManagementConstant.CODE, ConfigureConstant.STRING_9999);
					errorMsg.add("是否享受优惠政策 " + enjoyPreferentialPolicies + "不符合规范(是/否)");
				}
			} else {
				commodityCodeEntity.setEnjoyPreferentialPolicies("0");
			}
			// 优惠政策类型 先检验是否享受优惠政策
			// 如果填写了 是否享受优惠政策 并且为是 则校验优惠政策类型
			String preferentialPoliciesType = commodityCodeEntity.getPreferentialPoliciesType();
			if (StringUtils.isNotBlank(enjoyPreferentialPolicies) && "是".equals(enjoyPreferentialPolicies)) {
				if (StringUtils.isNotBlank(preferentialPoliciesType)) {
					if ("不征税".equals(preferentialPoliciesType)) {
						commodityCodeEntity.setPreferentialPoliciesType(OrderInfoEnum.LSLBS_2.getKey());

					} else if ("免税".equals(preferentialPoliciesType)) {
						commodityCodeEntity.setPreferentialPoliciesType(OrderInfoEnum.LSLBS_1.getKey());

					} else if ("非零税率".equals(preferentialPoliciesType)) {
						commodityCodeEntity.setPreferentialPoliciesType("");

					} else if ("出口零税".equals(preferentialPoliciesType)) {
						commodityCodeEntity.setPreferentialPoliciesType(OrderInfoEnum.LSLBS_0.getKey());

					} else if ("普通零税率".equals(preferentialPoliciesType)) {
						commodityCodeEntity.setPreferentialPoliciesType(OrderInfoEnum.LSLBS_3.getKey());
					} else {
						map.put(OrderManagementConstant.CODE, ConfigureConstant.STRING_9999);
						errorMsg.add("优惠政策 " + preferentialPoliciesType + "不符合规范");
					}
				} else {
					map.put(OrderManagementConstant.CODE, ConfigureConstant.STRING_9999);
					errorMsg.add("未填写优惠政策类型");
				}
			}
			// 分组编码
			String groupName = commodityCodeEntity.getGroupName();

			if (StringUtils.isNotBlank(groupName)) {
				int i = goupCommodityDao.selectGroupByNameAndUserId(groupName, commodityCodeEntity.getUserId());
				if (i != 1) {
					map.put(OrderManagementConstant.CODE, ConfigureConstant.STRING_9999);
					errorMsg.add("分组名称不存在!");
				}
			}

			// 描述
			String description = commodityCodeEntity.getDescription();
			if (StringUtils.isNotBlank(description)) {
				if (GBKUtil.getGBKLength(description) > 400) {
					map.put(OrderManagementConstant.CODE, ConfigureConstant.STRING_9999);
                    errorMsg.add("描述长度不能大于400个字节");
                }
            }
            if (CollectionUtils.isNotEmpty(errorMsg)) {
                errorMap.put("count", "第" + k + "行");
                errorMap.put("msg", errorMsg);
                list.add(errorMap);
            }
        }
        
        if (ConfigureConstant.STRING_0000.equals(map.get(OrderManagementConstant.CODE))) {
            for (CommodityCodeEntity commodityCodeEntity : commodityCodeList) {
                commodityCodeEntity.setId(apiInvoiceCommonService.getGenerateShotKey());
                log.info("添加接口开始执行");
                String name = commodityCodeEntity.getGroupName();
                // 获取分组id
                if (StringUtils.isNotBlank(commodityCodeEntity.getGroupName())) {
                    String groupId = goupCommodityDao.selectGroupIdByNameAndUserId(name, commodityCodeEntity.getId());
                    commodityCodeEntity.setGroupId(groupId);
                }
                commodityCodeEntity.setCreateTime(new Date());
				int i = 0;
				// 中英文括号替换
				String tempMerchandiseName = StringUtil.replaceStr(commodityCodeEntity.getMerchandiseName());
				commodityCodeEntity.setMerchandiseName(tempMerchandiseName);
				
				commodityCodeEntity.setDataSource(TaxClassCodeEnum.DATA_SOURCE_2.getKey());
				if (StringUtils.isNotEmpty(commodityCodeEntity.getTaxClassificationName())
						&& StringUtils.isNotEmpty(commodityCodeEntity.getTaxClassCode())) {
					// 税收分类名称和税收分类编码都存在
					commodityCodeEntity.setMatchingState(TaxClassCodeEnum.MATCHING_STATE_0.getKey());
					commodityCodeEntity.setDataState(TaxClassCodeEnum.DATA_STATE_0.getKey());
					// 已匹配默认为启用
				} else {
					commodityCodeEntity.setMatchingState(TaxClassCodeEnum.MATCHING_STATE_1.getKey());
					commodityCodeEntity.setDataState(TaxClassCodeEnum.DATA_STATE_1.getKey());
					// 未匹配默认为停用
				}
				if (StringUtils.isNotBlank(commodityCodeEntity.getTaxRate()) && commodityCodeEntity.getTaxRate().contains(ConfigureConstant.STRING_PERCENT)) {
					commodityCodeEntity.setTaxRate(StringUtil.formatSl(commodityCodeEntity.getTaxRate()));
				}
				i = commodityDao.insertCommodity(commodityCodeEntity);
				log.info("添加成功");
				
				if (i > 0) {
					num++;
				} else {
					map.put(OrderManagementConstant.CODE, ConfigureConstant.STRING_9999);
					map.put("msg", "第" + k + "行,添加失败");
				}
			}
		} else {
			return R.ok().put(OrderManagementConstant.CODE, ResponseStatusCodes.PRODUCT_PRODUCT_NAME).put("list", list)
					.put("count", commodityCodeList.size()).put("fail", list.size());

		}
		return R.ok().put("list", list).put("msg", "导入成功" + num + "条").put("count", commodityCodeList.size())
				.put("fail", list.size());
	}

    /**
    * 同步上级税编信息
    */
    @Override
    public R syncGroupTaxClassCode(List<CommodityTaxClassCodeParam> taxClassCodeList,String userId,String nsrsbh,String name) {
	    int successCount = 1;
	    StringBuilder msg = new StringBuilder();
        for (CommodityTaxClassCodeParam taxClassCodeParam : taxClassCodeList) {
	        Map<String, String> map = new HashMap<>(5);
	        map.put("encoding", taxClassCodeParam.getSpbm());
	        map.put("merchandiseName", taxClassCodeParam.getSpmc());
	        List<String> shList = new ArrayList<>();
	        shList.add(nsrsbh);
	        int i = commodityDao.selectByNameAndCode(map, shList);
	        if (i > 0) {
		        //存在
		        if (StringUtils.isNotEmpty(taxClassCodeParam.getSpbm())) {
			        msg.append("第" + successCount + "条数据商品名称已经存在；");
		        } else {
			        msg.append("第" + successCount + "条数据商品名称和商品编码已经存在；");
		        }
		        continue;
	        } else {//不存在新增
		        successCount++;
		        CommodityCodeEntity entity = new CommodityCodeEntity();
		        entity.setId(apiInvoiceCommonService.getGenerateShotKey());
                entity.setGroupId(taxClassCodeParam.getGroupId());
                entity.setMerchandiseName(taxClassCodeParam.getSpmc());
                entity.setEncoding(taxClassCodeParam.getSpbm());
                entity.setTaxClassificationName(taxClassCodeParam.getSsmc());
                entity.setTaxClassCode(taxClassCodeParam.getSsbm());
                entity.setXhfNsrsbh(nsrsbh);
                entity.setTaxClassAbbreviation(taxClassCodeParam.getSsjc());
                entity.setDataSource(TaxClassCodeEnum.DATA_SOURCE_0.getKey());
                entity.setMatchingState(TaxClassCodeEnum.MATCHING_STATE_0.getKey());
                entity.setDataState(TaxClassCodeEnum.DATA_STATE_0.getKey());
                entity.setUserId(userId);
                entity.setCreateTime(new Date());
                entity.setEnterpriseName(name);
                commodityDao.insertCommodity(entity);
            }
        }
        if(successCount>1){
            msg.append("同步成功"+successCount+"条信息");
        }
        return R.ok(msg.toString());
    }

    /**
    * 税收信息初始化
    */
    @Override
    public R initCommodityTaxClassCode(List<CommodityCodeInfo> info) {
        int successCount = 0;
        for (CommodityCodeInfo codeInfo : info) {
	        List<String> shList = new ArrayList<>();
	        shList.add(codeInfo.getXhfNsrsbh());
	        TaxClassCodeEntity taxClassCodeEntity = taxClassCodeDao.queryTaxClassCodeEntityBySpbm(codeInfo.getSsbm());
	        if (!ObjectUtils.isEmpty(taxClassCodeEntity)) {
		        CommodityCodeEntity commodityCodeEntity = new CommodityCodeEntity();
		        commodityCodeEntity.setId(codeInfo.getId());
		        if (StringUtils.isNotEmpty(taxClassCodeEntity.getZzssl())) {
			        String[] split = taxClassCodeEntity.getZzssl().split(",");
			        commodityCodeEntity.setTaxRate(split[0]);
		        } else {
			        commodityCodeEntity.setTaxRate("");
		        }
		        if (StringUtils.isNotBlank(commodityCodeEntity.getTaxRate()) && commodityCodeEntity.getTaxRate().contains(ConfigureConstant.STRING_PERCENT)) {
			        commodityCodeEntity.setTaxRate(StringUtil.formatSl(commodityCodeEntity.getTaxRate()));
		        }
		        if (StringUtils.isNotEmpty(taxClassCodeEntity.getYhzcmc())) {
			        commodityCodeEntity.setEnjoyPreferentialPolicies(ConfigureConstant.STRING_1);
		        } else {
			        commodityCodeEntity.setEnjoyPreferentialPolicies(ConfigureConstant.STRING_0);
		        }
		        commodityCodeEntity.setTaxExemptionType(taxClassCodeEntity.getMslx());
		        commodityDao.updateCommodity(commodityCodeEntity, shList);
		        successCount++;
	        }
        }
        return R.ok("初始化成功"+successCount+"条");
    }
	
	@Override
	public R commodityHandleDataStatus(List<Map> taxClassCodeIdArray, String dataStatus) {
		for (Map map : taxClassCodeIdArray) {
			String id = (String) map.get("id");
			String nsrsbh = (String) map.get("xhfNsrsbh");
			List<String> shList = new ArrayList<>();
			shList.add(nsrsbh);
			CommodityCodeEntity codeEntity = new CommodityCodeEntity();
			codeEntity.setDataState(dataStatus);
			codeEntity.setId(id);
			commodityDao.updateCommodity(codeEntity, shList);
		}
		
		return R.ok();
	}

    /**
     * 税收分类编码补齐19位
     * @param taxCode
     * @param taxCode
     * @return
     */
    private  String polishingTaxCode(String taxCode){
        if (StringUtils.isEmpty(taxCode)) {
            return "";
        }
        return StringUtils.rightPad(taxCode, 19, "0");
    }

    /**
     * @param merchandiseName 商品编码 encoding 商品编码 成品油类型 1 成品油 0 非成品油
     * @Description 成品油商品信息查询
     * @Author xieyuanqiang
     * @Date 11:02 2018-08-02
     */
    @Override
    public List<CommodityCodeEntity> queryCommodityInfoList(String merchandiseName, String encoding,List<String> xhfNsrsbh,String cpylx ) {
	    log.info("商品信息查询开始，参数为商品名称：{} 商品编码：{}", merchandiseName, encoding);
	    List<CommodityCodeEntity> dataList = commodityDao.queryCommodityInfoList(merchandiseName, encoding, xhfNsrsbh, cpylx);
	    if (dataList != null && dataList.size() > 0) {
		    dataList.forEach(commodityCodeEntity -> {
			    if (StringUtils.isNotBlank(commodityCodeEntity.getTaxRate()) && commodityCodeEntity.getTaxRate().contains(ConfigureConstant.STRING_PERCENT)) {
				    commodityCodeEntity.setTaxRate(StringUtil.formatSl(commodityCodeEntity.getTaxRate()));
			    }
		    });
	    }
	    log.info("商品信息查询结束 结果为{}", dataList);
	    return dataList;
    }

    @Override
    public R querypoLicyTypeList() {
        log.info("获取优惠政策类型");
        List<Map<String ,String>> list = commodityDao.selectLicyTypeList();
        return R.ok().put("list",list);
    }

    @Override
    public SysDictionary querySysDictionary() {
        SysDictionary map = commodityDao.querySysDictionary();
        return map;
    }

	@Override
	public R syncCommodity(CommodityCodeEntity codeEntity,String operationType) {
		OrderInfoContentEnum orderInfoContentEnum = OrderInfoContentEnum.COMMODITY_MESSAGE_SYNC_SUCCESS;
		try {
			//查询数据库中是否有当前请求信息
			List<String> shList = new ArrayList<>();
			shList.add(codeEntity.getXhfNsrsbh());
			CommodityCodeEntity commodityCodeEntity = commodityDao.selectCommodityById(codeEntity.getId(), shList);
			
			
			/**
			 * 根据商品编码补全商品信息.
			 */
			TaxClassCodeEntity taxClassCodeEntity = taxClassCodeService.queryTaxClassCodeEntity(codeEntity.getTaxClassCode());
			if (taxClassCodeEntity != null) {
				codeEntity.setTaxClassAbbreviation(taxClassCodeEntity.getSpjc());
				codeEntity.setTaxClassificationName(taxClassCodeEntity.getSpmc());
			}
			
			switch (operationType) {
				case "0":
					if (Objects.isNull(commodityCodeEntity)) {
						codeEntity.setMatchingState(TaxClassCodeEnum.MATCHING_STATE_0.getKey());
						codeEntity.setDataState(TaxClassCodeEnum.DATA_STATE_0.getKey());
						commodityDao.insertCommodity(codeEntity);
					} else {
						orderInfoContentEnum = OrderInfoContentEnum.COMMODITY_MESSAGE_SYNC_INSERT;
					}
					break;
				case "1":
					if (Objects.nonNull(commodityCodeEntity)) {
						commodityDao.updateCommodity(codeEntity, shList);
					}else{
						orderInfoContentEnum = OrderInfoContentEnum.COMMODITY_MESSAGE_SYNC_UPDATE;
					}
					break;
				case "2":
					if(Objects.nonNull(commodityCodeEntity)) {
						commodityDao.deleteCommodity(codeEntity.getId(), shList);
					}else{
						orderInfoContentEnum = OrderInfoContentEnum.COMMODITY_MESSAGE_SYNC_DELETE;
					}
					break;
				default:
					log.error("不存在的类型");
					break;
			}
		} catch (Exception e) {
			log.error("同步商品信息异常：{}", orderInfoContentEnum.getMessage());
			orderInfoContentEnum = OrderInfoContentEnum.COMMODITY_MESSAGE_SYNC_ERROR;
		}
		log.info("同步商品信息：{}", orderInfoContentEnum.getMessage());
		return R.setCodeAndMsg(orderInfoContentEnum, null);
	}
	
	/**
	 * 根据商品名称 或 税收分类编码查询
	 *
	 * @param map
	 * @return
	 */
	@Override
	public List<CommodityCodeEntity> queryProductList(Map<String, String> map, List<String> shList) {
		log.info("参数 {}", map.toString());
		List<CommodityCodeEntity> commodityCodeEntity = commodityDao.queryProductList(map, shList);
		log.info("返回参数 {}", JsonUtils.getInstance().toJsonString(commodityCodeEntity));
		if (commodityCodeEntity != null) {
			log.info("返回值：{}", commodityCodeEntity.toString());
		}
		return commodityCodeEntity;
	}

	/**
	 * 商品信息列表
	 *
	 * @param paramMap
	 * @return
	 */
	@Override
	public PageUtils queryCommodityInfoListByMap(Map<String, String> paramMap,List<String> shList) {

		int pageSize = Integer.valueOf(paramMap.get("pageSize"));
		int currPage = Integer.valueOf(paramMap.get("currPage"));
		log.info("订单查询，当前页：{},页面条数:{}", currPage, pageSize);
		PageHelper.startPage(currPage, pageSize);
		List<CommodityCodeEntity> list = commodityDao.queryCommodityInfoListByMap(paramMap, shList);
		PageInfo<CommodityCodeEntity> pageInfo = new PageInfo<>(list);
		PageUtils page = new PageUtils(pageInfo.getList(), (int) pageInfo.getTotal(), pageInfo.getPageSize(),
				pageInfo.getPageNum());
		return page;
	}

}
