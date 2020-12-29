package com.dxhy.order.service.manager;

import com.dxhy.order.api.ApiGroupTaxClassCodeService;
import com.dxhy.order.api.ApiInvoiceCommonService;
import com.dxhy.order.api.ApiTaxClassCodeService;
import com.dxhy.order.constant.ConfigureConstant;
import com.dxhy.order.constant.OrderManagementConstant;
import com.dxhy.order.constant.ResponseStatusCodes;
import com.dxhy.order.constant.TaxClassCodeEnum;
import com.dxhy.order.dao.CommodityDao;
import com.dxhy.order.dao.GroupCommodityDao;
import com.dxhy.order.dao.GroupTaxClassCodeMapper;
import com.dxhy.order.dao.TaxClassCodeDao;
import com.dxhy.order.model.PageUtils;
import com.dxhy.order.model.R;
import com.dxhy.order.model.entity.CommodityCodeEntity;
import com.dxhy.order.model.entity.GroupTaxClassCodeEntity;
import com.dxhy.order.model.entity.TaxClassCodeEntity;
import com.dxhy.order.utils.GBKUtil;
import com.dxhy.order.utils.StringUtil;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.dubbo.config.annotation.Service;
import org.springframework.util.ObjectUtils;

import javax.annotation.Resource;
import java.util.*;

/**
* @Description: 税收商品分类编码
* @Author:xueanna
* @Date:2019/9/17
*/
@Slf4j
@Service
public class GroupTaxClassCodeServiceImpl implements ApiGroupTaxClassCodeService {
    
    private final static String LOGGER_MSG = "(税收商品分类编码)";
    @Resource
    private GroupTaxClassCodeMapper groupTaxClassCodeMapper;
    @Resource
    private CommodityDao commodityDao;
    @Resource
    private TaxClassCodeDao taxClassCodeDao;
    @Resource
    private ApiInvoiceCommonService apiInvoiceCommonService;
    @Resource
    private ApiTaxClassCodeService taxClassCodeService;
    @Resource
    private GroupCommodityDao goupCommodityDao;

    @Override
    public PageUtils queryGroupTaxClassCode(Map<String ,Object> map) {
        int pageSize = Integer.parseInt(String.valueOf(map.get("limit")));
        int currPage = Integer.parseInt(String.valueOf(map.get("page")));
        PageHelper.startPage(currPage, pageSize);
        List<Map<String, Object>> selectCommodity = groupTaxClassCodeMapper.queryGroupTaxClassCode(map);
        PageInfo<Map<String, Object>> pageInfo = new PageInfo<>(selectCommodity);
        PageUtils page = new PageUtils(pageInfo.getList(), (int) pageInfo.getTotal(), pageInfo.getPageSize(), pageInfo.getPageNum());
        return page;
    }


    /**
     * 采集税编数据功能
     *
     * @param shList 集团下的税号
     * @param deptid 集团id 保存集团信息的时候需要
     */
    @Override
    public R collectTaxClassCode(List<String> shList, String deptid) {
        //获取下级未采集的商品数据
        List<CommodityCodeEntity> commodity = commodityDao.getCommodityCode(shList);
    
        List<GroupTaxClassCodeEntity> updateTaxClassCodeEntityList = new ArrayList<>();
        int count = 0;
        if (CollectionUtils.isNotEmpty(commodity)) {
            for (CommodityCodeEntity entity : commodity) {
                Map<String, String> paramMap = new HashMap<>(5);
                GroupTaxClassCodeEntity groupTaxClassCodeEntity = new GroupTaxClassCodeEntity();
                groupTaxClassCodeEntity = transtionGroupTaxClassCode(entity, deptid);
                paramMap.put("deptId",deptid);
                paramMap.put("spbm",entity.getEncoding());
                paramMap.put("spmc",entity.getMerchandiseName());
                //判断商品信息数据库是否存在
                List<GroupTaxClassCodeEntity> codeEntities = groupTaxClassCodeMapper.queryTaxClassCodeBySpbmAndSpmc(paramMap);
                if(CollectionUtils.isNotEmpty(codeEntities)){
                    boolean flag = false;
                    for (GroupTaxClassCodeEntity codeEntity : codeEntities) {
                        if(TaxClassCodeEnum.SHARE_STATE_0.getKey().equals(codeEntity.getShareState())){
                            flag = true;
                        }
                    }
                    if (flag) {
                        log.info("商品名称：[{}]已经存在，并且数据为已共享", entity.getMerchandiseName());
                        continue;
                    }
                    paramMap.put("ssmc", entity.getTaxClassificationName());
                    paramMap.put("ssbm", entity.getTaxClassCode());
                    List<GroupTaxClassCodeEntity> codeList = groupTaxClassCodeMapper.queryTaxClassCodeBySpbmAndSpmc(paramMap);
                    if (CollectionUtils.isNotEmpty(codeList)) {
                        //其他信息也相同，不做处理
                        log.warn("{}商品名称和税收信息已经存在，商品名称为:[{}],税收名称为:[{}],税收编码为:[{}]", LOGGER_MSG, entity.getMerchandiseName(), entity.getTaxClassificationName(), entity.getTaxClassCode());
                        continue;
                    } else {
                        //税收信息不相同，差异数据
                        groupTaxClassCodeEntity.setDifferenceFlag(TaxClassCodeEnum.DIFFERENCE_FLAG_0.getKey());
                        if (1 == codeEntities.size()) {
                            updateTaxClassCodeEntityList.add(codeEntities.get(0));
                        }
                    }
                } else {
                    //不存在
                    if (!StringUtils.isEmpty(entity.getEncoding())) {
                        //商编不为空，判断商品名称或者商品编码是否重复
                        List<GroupTaxClassCodeEntity> codeList = groupTaxClassCodeMapper.queryTaxClassCodeBySpbmOrSpmc(paramMap);
                        if (CollectionUtils.isNotEmpty(codeList)) {
                            groupTaxClassCodeEntity.setDifferenceFlag(TaxClassCodeEnum.DIFFERENCE_FLAG_0.getKey());
                            if (1 == codeList.size()) {
                                updateTaxClassCodeEntityList.add(codeList.get(0));
                            }
                        }
                    }
                }
                count++;
                groupTaxClassCodeMapper.insert(groupTaxClassCodeEntity);
                /**
                 * 修改商品数据为已采集
                 */
                CommodityCodeEntity codeEntity = new CommodityCodeEntity();
                codeEntity.setId(entity.getId());
                codeEntity.setCollectIdent("0");
                commodityDao.updateCommodity(codeEntity, shList);
            }
            if(CollectionUtils.isNotEmpty(updateTaxClassCodeEntityList)){
                for (GroupTaxClassCodeEntity codeEntity : updateTaxClassCodeEntityList) {
                    GroupTaxClassCodeEntity updateEntity = new GroupTaxClassCodeEntity();
                    updateEntity.setId(codeEntity.getId());
                    updateEntity.setDifferenceFlag(TaxClassCodeEnum.DIFFERENCE_FLAG_1.getKey());
                    groupTaxClassCodeMapper.updateByPrimaryKeySelective(updateEntity);
                }
            }
            return R.ok("成功采集"+count+"条下级数据");
        }else{
            return R.ok("没有采集数据");
        }
    }

    /**
     * 集团处理共享数据状态
     */
    @Override
    public R taxClassCodeHandleShareStatus(String[] taxClassCodeIdArray, String shareStatus) {
        groupTaxClassCodeMapper.updateTaxClassCodeShareStatus(taxClassCodeIdArray,shareStatus);
        return R.ok();
    }
    /**
     * 集团处理启用数据状态
     */
    @Override
    public R taxClassCodeHandleDataStatus(String[] taxClassCodeIdArray,String dataStatus) {
        groupTaxClassCodeMapper.updateTaxClassCodeDataStatus(taxClassCodeIdArray,dataStatus);
        return R.ok();
    }

    /**
    * 保存集团税编信息
    */
    @Override
    public R saveGroupTaxClassCode(GroupTaxClassCodeEntity groupTaxClassCodeEntity,String deptId) {
        Map<String, String> paramMap = new HashMap<>(5);
        groupTaxClassCodeEntity.setId(apiInvoiceCommonService.getGenerateShotKey());
        groupTaxClassCodeEntity.setDeptId(deptId);
        groupTaxClassCodeEntity.setCreateTime(new Date());
        if (StringUtils.isEmpty(groupTaxClassCodeEntity.getEncoding())) {
            //无商品编码
            paramMap.put("deptId", deptId);
            paramMap.put("spmc", groupTaxClassCodeEntity.getMerchandiseName());
            //判断商品名称数据库是否存在
            List<GroupTaxClassCodeEntity> codeEntities = groupTaxClassCodeMapper.queryTaxClassCodeBySpbmAndSpmc(paramMap);
            if (CollectionUtils.isNotEmpty(codeEntities)) {
                return R.error("商品名称已经存在");
            }
        } else {
            //有商品编码
            paramMap.put("spmc", groupTaxClassCodeEntity.getMerchandiseName());
            paramMap.put("spbm", groupTaxClassCodeEntity.getEncoding());
            paramMap.put("deptId", deptId);
            //判断商品信息是否存在
            List<GroupTaxClassCodeEntity> codeEntities = groupTaxClassCodeMapper.queryTaxClassCodeBySpbmAndSpmc(paramMap);
            if (CollectionUtils.isNotEmpty(codeEntities)) {
                //存在
                return R.error("商品名称或者商品编码已经存在");
            }
        }
        //数据来源为手工创建
        groupTaxClassCodeEntity.setDataSource(TaxClassCodeEnum.DATA_SOURCE_1.getKey());
        //共享状态为待核实
        groupTaxClassCodeEntity.setShareState(TaxClassCodeEnum.SHARE_STATE_1.getKey());
        //数据状态为停用
        groupTaxClassCodeEntity.setDataState(TaxClassCodeEnum.DATA_STATE_1.getKey());
        if (StringUtils.isEmpty(groupTaxClassCodeEntity.getTaxClassificationName()) &&
                StringUtils.isEmpty(groupTaxClassCodeEntity.getTaxClassCode()) &&
                StringUtils.isEmpty(groupTaxClassCodeEntity.getTaxClassAbbreviation())) {
            //匹配状态为未匹配
            groupTaxClassCodeEntity.setMatchingState(TaxClassCodeEnum.MATCHING_STATE_1.getKey());
        } else {
            //有税收信息
            //匹配状态为已匹配
            groupTaxClassCodeEntity.setMatchingState(TaxClassCodeEnum.MATCHING_STATE_0.getKey());
        }
        groupTaxClassCodeMapper.insert(groupTaxClassCodeEntity);
        return R.ok();
    }

    /**
    * 集团税编详情
    */
    @Override
    public R queryGroupTaxClassCodeDetail(String groupTaxClassCodeId) {
        Map<String,String> entity = groupTaxClassCodeMapper.selectGroupTaxClassCodeById(groupTaxClassCodeId);
        if(ObjectUtils.isEmpty(entity)){
            return R.error("税编信息不存在");
        }
        return R.ok().put("data",entity);
    }


    /**
    * 逻辑删除集团税编
    */
    @Override
    public R delGroupTaxClassCode(String groupTaxClassCodeId,String deptId) {
        String[] str = {groupTaxClassCodeId};
        //差异数据为最后一条时修改该条数据的差异数据状态
        for (String id : str) {
            GroupTaxClassCodeEntity entity = groupTaxClassCodeMapper.selectGroupTaxClassCode(id);
            if(!ObjectUtils.isEmpty(entity)){
                if (StringUtils.isEmpty(entity.getDifferenceFlag())) {
                    continue;
                } else {
                    //差异数据，判断是否为最后一条
                    Map paramMap = new HashMap(5);
                    paramMap.put("spmc", entity.getMerchandiseName());
                    paramMap.put("spbm", entity.getEncoding());
                    paramMap.put("deptId", deptId);
                    List<GroupTaxClassCodeEntity> codeEntities = groupTaxClassCodeMapper.queryTaxClassCodeBySpbmOrSpmc(paramMap);
                    if (CollectionUtils.isNotEmpty(codeEntities)) {
                        //设置除本身的另一条数据差异标志为空
                        for (GroupTaxClassCodeEntity codeEntity : codeEntities) {
                            if (codeEntity.getDifferenceFlag().equals(TaxClassCodeEnum.DIFFERENCE_FLAG_1.getKey())) {
                                paramMap.put("spmc", codeEntity.getMerchandiseName());
                                paramMap.put("spbm", codeEntity.getEncoding());
                                paramMap.put("deptId", deptId);
                                List<GroupTaxClassCodeEntity> codeEntitie = groupTaxClassCodeMapper.queryTaxClassCodeBySpbmOrSpmc(paramMap);
                                if (CollectionUtils.isNotEmpty(codeEntitie) && codeEntitie.size() == 2) {
                                    groupTaxClassCodeMapper.updateTaxClassCodeDifferenceFlag(codeEntity.getId());
                                }
                            }
                        }
                    }
                }
            }
        }
        groupTaxClassCodeMapper.updateTaxClassCodeDataStatus(str,TaxClassCodeEnum.DATA_STATE_2.getKey());
        return R.ok();
    }

    /**
    * 修改集团税编信息
    */
    @Override
    public R updateGroupTaxClassCode(GroupTaxClassCodeEntity groupTaxClassCodeEntity) {
        Map<String, String> paramMap = new HashMap<>(5);
        GroupTaxClassCodeEntity codeEntity = groupTaxClassCodeMapper.selectGroupTaxClassCode(groupTaxClassCodeEntity.getId());
        if (StringUtils.isEmpty(groupTaxClassCodeEntity.getEncoding())) {
            //无商品编码
            if (!codeEntity.getMerchandiseName().equals(groupTaxClassCodeEntity.getMerchandiseName())) {
                //商品名称有修改，校验
                paramMap.put("deptId", groupTaxClassCodeEntity.getDeptId());
                paramMap.put("spmc", groupTaxClassCodeEntity.getMerchandiseName());
                //判断商品名称数据库是否存在
                List<GroupTaxClassCodeEntity> codeEntities = groupTaxClassCodeMapper.queryTaxClassCodeBySpbmAndSpmc(paramMap);
                if (CollectionUtils.isNotEmpty(codeEntities)) {
                    return R.error("修改失败，商品名称已经存在");
                }
            }
        }else{//有商编
            if(!codeEntity.getMerchandiseName().equals(groupTaxClassCodeEntity.getMerchandiseName())||!groupTaxClassCodeEntity.getEncoding().equals(codeEntity.getEncoding())) {
                paramMap.put("spmc", groupTaxClassCodeEntity.getMerchandiseName());
                paramMap.put("spbm", groupTaxClassCodeEntity.getEncoding());
                paramMap.put("deptId", codeEntity.getDeptId());
                //判断商品信息是否存在
                List<GroupTaxClassCodeEntity> codeEntities = groupTaxClassCodeMapper.queryTaxClassCodeBySpbmAndSpmc(paramMap);
                if (CollectionUtils.isNotEmpty(codeEntities)) {
                    //存在
                    return R.error("修改失败，商品信息已经存在");
                }
            }
        }
        //税收信息是否不存在
        if(StringUtils.isEmpty(groupTaxClassCodeEntity.getTaxClassificationName())&&
                StringUtils.isEmpty(groupTaxClassCodeEntity.getTaxClassCode())&&
                StringUtils.isEmpty(groupTaxClassCodeEntity.getTaxClassAbbreviation())) {
            //匹配状态为已匹配
            groupTaxClassCodeEntity.setMatchingState(TaxClassCodeEnum.MATCHING_STATE_1.getKey());
        }else {
            //匹配状态为已匹配
            groupTaxClassCodeEntity.setMatchingState(TaxClassCodeEnum.MATCHING_STATE_0.getKey());
        }
        int i = groupTaxClassCodeMapper.updateByPrimaryKeySelective(groupTaxClassCodeEntity);
        if(i>0){
            log.info("税编信息修改成功");
        }
        return R.ok();
    }

    /**
    * 集团模板导入更新数据
    */
    @Override
    public R uploadCommodityCode(List<GroupTaxClassCodeEntity> commodityCodeEntityList, Map<String, String> paraMap) {
        R r = checkParams(commodityCodeEntityList,paraMap);
        return r;
    }
    public  R checkParams(List<GroupTaxClassCodeEntity> commodityCodeList, Map<String, String> paraMap) {
        String deptId = paraMap.get("deptId");
        String userId = paraMap.get("userId");
        List<Map<String, Object>> list = new ArrayList<>();
        List<GroupTaxClassCodeEntity> updateList = new ArrayList<>();
        int k = 2;
        int num = 0;
        Map<String, Object> map = new HashMap<>(10);
        Map<String, Object> errorMap =null;
        List errorMsg =null;
        if (commodityCodeList != null) {
            for (GroupTaxClassCodeEntity groupTaxClassCodeEntity : commodityCodeList) {
                errorMap = new HashMap<>(10);
                errorMsg = new ArrayList();
                map.put(OrderManagementConstant.CODE, ConfigureConstant.STRING_0000);
                k++;
                //编码
                String encoding = groupTaxClassCodeEntity.getEncoding();
                String codeVerify = "^.{2,20}$";
                if (StringUtils.isNotBlank(encoding)) {
                    //有商编
                    if (!encoding.matches(codeVerify)) {
                        map.put(OrderManagementConstant.CODE, ConfigureConstant.STRING_9999);
                        errorMsg.add("编码 " + encoding + " 不符合规范，长度为2-20位");
                    } else {
                        Map<String, String> paramMap = new HashMap<>(5);
                        paramMap.put("spmc", groupTaxClassCodeEntity.getMerchandiseName());
                        paramMap.put("spbm", encoding);
                        paramMap.put("deptId", deptId);
                        //判断商品信息是否存在
                        List<GroupTaxClassCodeEntity> codeEntities = groupTaxClassCodeMapper.queryTaxClassCodeBySpbmAndSpmc(paramMap);
                        if (CollectionUtils.isNotEmpty(codeEntities)) {
                            //存在，查询税收信息
                            paramMap.put("ssmc", groupTaxClassCodeEntity.getTaxClassificationName());
                            paramMap.put("ssbm", groupTaxClassCodeEntity.getTaxClassCode());
                            List<GroupTaxClassCodeEntity> codeList = groupTaxClassCodeMapper.queryTaxClassCodeBySpbmAndSpmc(paramMap);
                            if (CollectionUtils.isNotEmpty(codeEntities)) {
                                //税收信息也相同，不做处理
                                map.put(OrderManagementConstant.CODE, ConfigureConstant.STRING_9999);
                                errorMsg.add("第" + k + "行数据已经存在");
                            } else {
                                groupTaxClassCodeEntity.setDifferenceFlag(TaxClassCodeEnum.DIFFERENCE_FLAG_0.getKey());
                                if ("1".equals(codeEntities.size())) {
                                    //数据库里面有一条相同的，需要打出差异标识
                                    updateList.add(codeEntities.get(0));
                                }
                            }
                        } else {
                            //不存在
                            List<GroupTaxClassCodeEntity> codeList = groupTaxClassCodeMapper.queryTaxClassCodeBySpbmOrSpmc(paramMap);
                            if (CollectionUtils.isNotEmpty(codeList)) {
                                groupTaxClassCodeEntity.setDifferenceFlag(TaxClassCodeEnum.DIFFERENCE_FLAG_0.getKey());
                                if ("1".equals(codeEntities.size())) {
                                    //数据库里面有一条相同的，需要打出差异标识
                                    updateList.add(codeEntities.get(0));
                                }
                            }
                        }
                    }
                } else {
                    //无商品编码
                    //商品名称校验
                    String merchandiseName = groupTaxClassCodeEntity.getMerchandiseName();
                    if (StringUtils.isNotBlank(merchandiseName)) {
                        Map<String, String> paramMap = new HashMap<>(5);
                        paramMap.put("spmc", merchandiseName);
                        paramMap.put("deptId", deptId);
                        //判断商品名称数据库是否存在
                        List<GroupTaxClassCodeEntity> codeEntities = groupTaxClassCodeMapper.queryTaxClassCodeBySpbmAndSpmc(paramMap);
                        if (CollectionUtils.isNotEmpty(codeEntities)) {
                            paramMap.put("ssmc", groupTaxClassCodeEntity.getTaxClassificationName());
                            paramMap.put("ssbm", groupTaxClassCodeEntity.getTaxClassCode());
                            List<GroupTaxClassCodeEntity> codeList = groupTaxClassCodeMapper.queryTaxClassCodeBySpbmAndSpmc(paramMap);
                            if (CollectionUtils.isNotEmpty(codeList)) {
                                //其他信息也相同，不做处理
                                map.put(OrderManagementConstant.CODE, ConfigureConstant.STRING_9999);
                                errorMsg.add("第" + k + "行数据已经存在");
                            } else {
                                groupTaxClassCodeEntity.setDifferenceFlag(TaxClassCodeEnum.DIFFERENCE_FLAG_0.getKey());
                                if ("1".equals(codeEntities.size())) {
                                    //数据库里面有一条相同的，需要打出差异标识
                                    updateList.add(codeEntities.get(0));
                                }
                            }
                        }
                    } else {
                        map.put(OrderManagementConstant.CODE, ConfigureConstant.STRING_9999);
                        errorMsg.add("未填写商品名称");
                    }
                }
                //商品名称校验
                String merchandiseName = groupTaxClassCodeEntity.getMerchandiseName();
                if (StringUtils.isNotBlank(merchandiseName)) {
                    if (GBKUtil.getGBKLength(merchandiseName) > 90) {
                        map.put(OrderManagementConstant.CODE, ConfigureConstant.STRING_9999);
                        errorMsg.add("商品名称不能大于90个字节");
                    }
                } else {
                    map.put(OrderManagementConstant.CODE, ConfigureConstant.STRING_9999);
                    errorMsg.add("未填写商品名称");
                }

                //规格型号
                String specificationModel = groupTaxClassCodeEntity.getSpecificationModel();
                if (StringUtils.isNotBlank(specificationModel)) {
                    if (GBKUtil.getGBKLength(specificationModel) > 40) {
                        map.put(OrderManagementConstant.CODE, ConfigureConstant.STRING_9999);
                        errorMsg.add("规格型号长度不能大于40字节");
                    }
                }
                //计量单位
                String meteringUnit = groupTaxClassCodeEntity.getMeteringUnit();
                if (StringUtils.isNotBlank(meteringUnit)) {
                    if (GBKUtil.getGBKLength(meteringUnit) > 20) {
                        map.put(OrderManagementConstant.CODE, ConfigureConstant.STRING_9999);
                        errorMsg.add("计量单位长度不能大于20字节");
                    }
                }
                //单价
                String unitPrice = groupTaxClassCodeEntity.getUnitPrice();
                String priceVerify = "^[0-9]{1,8}.[0-9]{1,11}$";
                if (StringUtils.isNotBlank(unitPrice)) {
                    if (!unitPrice.matches(priceVerify)) {
                        map.put(OrderManagementConstant.CODE, ConfigureConstant.STRING_9999);
                        errorMsg.add("单价 " + unitPrice + " 不符合规范,为2-16位小数");
                    }
                }
                //描述
                String description = groupTaxClassCodeEntity.getDescription();
                if (StringUtils.isNotBlank(description)) {
                    if (GBKUtil.getGBKLength(description) > 400) {
                        map.put(OrderManagementConstant.CODE, ConfigureConstant.STRING_9999);
                        errorMsg.add("描述长度不能大于400字节");
                    }
                }
                //税收分类编码
                String taxClassCode = groupTaxClassCodeEntity.getTaxClassCode();
                //补齐税收分类编码 不足19位 自动不补齐19位
                taxClassCode =polishingTaxCode(taxClassCode);
                String tax = "^[0-9]{0,19}$";
                if (StringUtils.isNotBlank(taxClassCode)) {
                    if (!taxClassCode.matches(tax)) {
                        map.put(OrderManagementConstant.CODE, ConfigureConstant.STRING_9999);
                        errorMsg.add("税收分类编码 " + taxClassCode + " 不符合规范");
                    } else {
                        TaxClassCodeEntity taxClassCodeEntity = taxClassCodeService.queryTaxClassCodeEntity(taxClassCode);
                        if (taxClassCodeEntity == null) {
                            map.put(OrderManagementConstant.CODE, ConfigureConstant.STRING_9999);
                            errorMsg.add("税收分类编码 " + taxClassCode + " 不存在");
                        } else {
                            groupTaxClassCodeEntity.setTaxClassAbbreviation(taxClassCodeEntity.getSpjc());
                            groupTaxClassCodeEntity.setTaxClassificationName(taxClassCodeEntity.getSpmc());
                        }
                    }
                }

                //分组编码
                String groupName = groupTaxClassCodeEntity.getGroupName();
    
                if (StringUtils.isNotBlank(groupName)) {
                    int i = goupCommodityDao.selectGroupByNameAndUserId(groupName, userId);
                    if (i != 1) {
                        map.put(OrderManagementConstant.CODE, ConfigureConstant.STRING_9999);
                        errorMsg.add("分组名称不存在!");
                    }
                }
                if (org.apache.commons.collections.CollectionUtils.isNotEmpty(errorMsg)) {
                    errorMap.put("count", "第" + k + "行");
                    errorMap.put("msg", errorMsg);
                    list.add(errorMap);
                }
            }
    
            if (ConfigureConstant.STRING_0000.equals(map.get(OrderManagementConstant.CODE))) {
                for (GroupTaxClassCodeEntity groupTaxClassCodeEntity : commodityCodeList) {
                    groupTaxClassCodeEntity.setId(apiInvoiceCommonService.getGenerateShotKey());
                    log.info("添加接口开始执行");
                    String name = groupTaxClassCodeEntity.getGroupName();
                    //获取分组id
                    String groupId = goupCommodityDao.selectGroupIdByNameAndUserId(name, userId);
                    groupTaxClassCodeEntity.setGroupId(groupId);
                    groupTaxClassCodeEntity.setCreateTime(new Date());
                    int i = 0;
                    //中英文括号替换
                    String tempMerchandiseName = StringUtil.replaceStr(groupTaxClassCodeEntity.getMerchandiseName());
                    groupTaxClassCodeEntity.setMerchandiseName(tempMerchandiseName);
                    groupTaxClassCodeEntity.setShareState(TaxClassCodeEnum.SHARE_STATE_1.getKey());
                    groupTaxClassCodeEntity.setDataSource(TaxClassCodeEnum.DATA_SOURCE_2.getKey());
                    //默认停用
                    groupTaxClassCodeEntity.setDataState(TaxClassCodeEnum.DATA_STATE_1.getKey());
                    if (StringUtils.isNotEmpty(groupTaxClassCodeEntity.getTaxClassificationName()) && StringUtils.isNotEmpty(groupTaxClassCodeEntity.getTaxClassCode())) {
                        //税收分类名称和税收分类编码都存在
                        groupTaxClassCodeEntity.setMatchingState(TaxClassCodeEnum.MATCHING_STATE_0.getKey());
                    } else {
                        groupTaxClassCodeEntity.setMatchingState(TaxClassCodeEnum.MATCHING_STATE_1.getKey());
                    }
                    i = groupTaxClassCodeMapper.insert(groupTaxClassCodeEntity);
                    log.info("添加成功");
    
                    if (i > 0) {
                        num++;
                    } else {
                        map.put(OrderManagementConstant.CODE, ConfigureConstant.STRING_9999);
                        map.put("msg", "第" + k + "行,添加失败");
                    }
                }
                //需要修改原库里的数据
                log.info("修改原库原库里的数据差异标识");
                for (GroupTaxClassCodeEntity codeEntity : updateList) {
                    GroupTaxClassCodeEntity updateCodeEntity = new GroupTaxClassCodeEntity();
                    updateCodeEntity.setId(codeEntity.getId());
                    updateCodeEntity.setDifferenceFlag(TaxClassCodeEnum.DIFFERENCE_FLAG_1.getKey());
                    groupTaxClassCodeMapper.updateByPrimaryKey(updateCodeEntity);
                }
            }else{
                return R.ok().put(OrderManagementConstant.CODE, ResponseStatusCodes.PRODUCT_PRODUCT_NAME).put("list", list).put("count", commodityCodeList.size()).put("fail", list.size());
            }
            return R.ok().put("list",list).put("msg","导入成功"+num+"条").put("count",commodityCodeList.size()).put("fail",list.size());
        }else{
            map.put(OrderManagementConstant.CODE, ConfigureConstant.STRING_9999);
            map.put("msg", "请输入需要导入的商品编码");
            list.add(map);
            return R.ok().put(OrderManagementConstant.CODE, ResponseStatusCodes.PRODUCT_PRODUCT_NAME).put("list", list).put("count", "0").put("fail", list.size());
        }
    }

    public GroupTaxClassCodeEntity transtionGroupTaxClassCode(CommodityCodeEntity commodityCodeEntity,String deptid) {
        GroupTaxClassCodeEntity entity = new GroupTaxClassCodeEntity();
        entity.setId(apiInvoiceCommonService.getGenerateShotKey());
        entity.setCreateTime(new Date());
        //商品名称
        entity.setMerchandiseName(commodityCodeEntity.getMerchandiseName());
        //商品编码
        entity.setEncoding(commodityCodeEntity.getEncoding());
        //分组id
        entity.setGroupId(commodityCodeEntity.getGroupId());
        //税收编码
        entity.setTaxClassCode(commodityCodeEntity.getTaxClassCode());
        //税收名称
        entity.setTaxClassificationName(commodityCodeEntity.getTaxClassificationName());
        entity.setTaxClassAbbreviation(commodityCodeEntity.getTaxClassAbbreviation());
        entity.setDescription(commodityCodeEntity.getDescription());
        //规格型号
        entity.setSpecificationModel(commodityCodeEntity.getSpecificationModel());
        //计量单位
        entity.setMeteringUnit(commodityCodeEntity.getMeteringUnit());
        //单价
        entity.setUnitPrice(commodityCodeEntity.getUnitPrice());
        //数据来源
        entity.setDataSource(TaxClassCodeEnum.DATA_SOURCE_3.getKey());
        //匹配状态
        entity.setMatchingState(TaxClassCodeEnum.MATCHING_STATE_0.getKey());
        //数据状态
        entity.setDataState(TaxClassCodeEnum.DATA_STATE_1.getKey());
        //共享状态
        entity.setShareState(TaxClassCodeEnum.SHARE_STATE_1.getKey());
        entity.setDeptId(deptid);
        return entity;
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
}
