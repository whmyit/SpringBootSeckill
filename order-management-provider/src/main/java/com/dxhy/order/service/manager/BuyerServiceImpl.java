package com.dxhy.order.service.manager;

import com.dxhy.order.api.ApiBuyerService;
import com.dxhy.order.api.ApiInvoiceCommonService;
import com.dxhy.order.constant.ConfigureConstant;
import com.dxhy.order.constant.OrderInfoContentEnum;
import com.dxhy.order.constant.OrderInfoEnum;
import com.dxhy.order.constant.OrderManagementConstant;
import com.dxhy.order.dao.BuyerDao;
import com.dxhy.order.model.PageUtils;
import com.dxhy.order.model.R;
import com.dxhy.order.model.entity.BuyerEntity;
import com.dxhy.order.response.StatusCodes;
import com.dxhy.order.utils.GBKUtil;
import com.dxhy.order.utils.JsonUtils;
import com.dxhy.order.utils.StringUtil;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.dubbo.config.annotation.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.*;

/**
 *
 * @author liangyuhuan
 * @date 2018/7/31
 */
@Slf4j
@Service
public class BuyerServiceImpl implements ApiBuyerService{
    @Resource
    private BuyerDao buyerDao;
    
    @Resource
    private ApiInvoiceCommonService apiInvoiceCommonService;
    
    @Override
    public PageUtils queryBuyerList(Map<String, Object> map, List<String> shList) {
        int pageSize = (Integer) map.get("limit");
        int currPage = (Integer) map.get("page");
        // 这里前端从1开始需要进行-1操作
        // currPage=currPage-1;
        log.info("订单查询，当前页：{},页面条数:{}", currPage, pageSize);
        PageHelper.startPage(currPage, pageSize);
        List<BuyerEntity> list = buyerDao.selectBuyerList(map, shList);
        PageInfo<BuyerEntity> pageInfo = new PageInfo<>(list);
        PageUtils page = new PageUtils(pageInfo.getList(), (int) pageInfo.getTotal(), pageInfo.getPageSize(),
                pageInfo.getPageNum());
    
        log.info("返回值{} ", page);
        return page;
    }
    
    /**
     * 接口提供根据名称模糊查询
     *
     * @param purchaseName
     * @return
     */
    @Override
    public List<BuyerEntity> queryBuyerByName(String purchaseName, List<String> xhfNsrsbh) {
        log.info("购方信息模糊查询开始，参数为 {}", purchaseName);
        List<BuyerEntity> dataList = null;
        log.info("购方信息模糊查询调用本地数据库开始。。。");
        dataList = buyerDao.selectBuyer(purchaseName, xhfNsrsbh);
        if (dataList != null && dataList.size() > 0) {
            return dataList;
        } else {
            log.info("购方信息模糊查询调用本地数据库为空。。。");
            log.info("购方信息模糊查询调用大象服务接口。。。");
            return null;
        }
    }
    
    @Override
    public R removeBuyerbyId(List<Map> ids) {
        R r = new R();
        for (Map map : ids) {
            String id = (String) map.get("id");
            String nsrsbh = (String) map.get("xhfNsrsbh");
            List<String> shList = new ArrayList<>();
            shList.add(nsrsbh);
            int i = buyerDao.deleteBuyerById(id, shList);
            if (i > 0) {
                R.ok().put("msg", "删除成功");
            } else {
                R.ok().put("msg", "删除失败");
            }
        }
        return r;
    }
    
    @Override
    public R uploadGrop(List<BuyerEntity> buyerEntityList) {
        List<Map<String, Object>> list = new ArrayList<>();
        int k = 1;
        Map<String, Object> map = new HashMap<>(10);
        Map<String, Object> repeatMap = new HashMap<>(10);
        for (BuyerEntity buyerEntity : buyerEntityList) {
            k++;
            map.put(OrderManagementConstant.CODE, ConfigureConstant.STRING_0000);
            String key = buyerEntity.getXhfNsrsbh() + buyerEntity.getXhfMc() + buyerEntity.getTaxpayerCode() + buyerEntity.getPurchaseName();
            if (repeatMap.get(key) != null) {
                map.put(OrderManagementConstant.CODE, ConfigureConstant.STRING_9999);
                Map<String, Object> errorMap = getMap(StatusCodes.IS_REPEAT, "第" + k + "行," + buyerEntity.getTaxpayerCode() + "销方数据和购方数据重复");
                list.add(errorMap);
                continue;
            } else {
                repeatMap.put(key, buyerEntity);
            }
    
            //购方名称
            String purchaseName = buyerEntity.getPurchaseName();
            String purchase = "^[A-Za-z0-9\\u4e00-\\u9fa5]+$";
            List<String> shList = new ArrayList<>();
            shList.add(buyerEntity.getXhfNsrsbh());
            Map<String, String> param = new HashMap<>(10);
            if (StringUtils.isNotBlank(purchaseName)) {
                if (!purchaseName.matches(purchase)) {
                    map.put(OrderManagementConstant.CODE, ConfigureConstant.STRING_9999);
                    Map<String, Object> errorMap = getMap(StatusCodes.NON_CONFORMITY, "第" + k + "行," + purchaseName + "购方名称不可以存在全角字符");
                    list.add(errorMap);
                } else {
                    if (GBKUtil.getGBKLength(purchaseName) > 100) {
                        map.put(OrderManagementConstant.CODE, ConfigureConstant.STRING_9999);
                        Map<String, Object> errorMap = getMap(StatusCodes.IS_EXCESSLENGTH, "第" + k + "行,购方名称不能大于100个字节");
                        list.add(errorMap);
                    } else {
                        param.put("purchaseName", purchaseName);
                        int i = buyerDao.selectBuyerByName(param, shList);
                        if (i > 0) {
                            map.put(OrderManagementConstant.CODE, ConfigureConstant.STRING_9999);
                            Map<String, Object> errorMap = getMap(StatusCodes.IS_REPEAT, "第" + k + "行," + purchaseName + "购方名称已经存在");
                            list.add(errorMap);
                        }
                    }
                }
            } else {
                map.put(OrderManagementConstant.CODE, ConfigureConstant.STRING_9999);
                Map<String, Object> errorMap = getMap(StatusCodes.IS_NULL, "第" + k + "行,购方名称不能为空");
                list.add(errorMap);
            }
            //税号
            String taxpayerCode = buyerEntity.getTaxpayerCode();
            String taxRegular = "^[A-Z0-9]{15}$|^[A-Z0-9]{17}$|^[A-Z0-9]{18}$|^[A-Z0-9]{20}$";
            if (StringUtils.isNotBlank(taxpayerCode)) {
    
                if (!taxpayerCode.matches(taxRegular)) {
                    map.put(OrderManagementConstant.CODE, ConfigureConstant.STRING_9999);
                    Map<String, Object> errorMap = getMap(StatusCodes.NON_CONFORMITY, "第" + k + "行," + taxpayerCode + "税号不符合规范");
                    list.add(errorMap);
                } else {
                    //如果存在小写 改为大写字母
                    String tax = taxpayerCode.toUpperCase();
                    buyerEntity.setTaxpayerCode(tax);
                    param.put("taxpayerCode", buyerEntity.getTaxpayerCode());
                    int i = buyerDao.selectBuyerByName(param, shList);
                    if (i > 0) {
                        map.put(OrderManagementConstant.CODE, ConfigureConstant.STRING_9999);
                        Map<String, Object> errorMap = getMap(StatusCodes.IS_REPEAT, "第" + k + "行," + taxpayerCode + "税号已经存在");
                        list.add(errorMap);
                    }
                }
            } else {
                map.put(OrderManagementConstant.CODE, ConfigureConstant.STRING_9999);
                Map<String, Object> errorMap = getMap(StatusCodes.NON_CONFORMITY, "第" + k + "行,税号不能为空");
                list.add(errorMap);
            }
            //地址
            String address = buyerEntity.getAddress();
            if (StringUtils.isNotBlank(address)) {
                if (address.contains("|")) {
                    map.put(OrderManagementConstant.CODE, ConfigureConstant.STRING_9999);
                    Map<String, Object> errorMap = getMap(StatusCodes.NON_CONFORMITY, "第" + k + "行," + address + "地址中不能存在 '|'");
                    list.add(errorMap);
                } else if (GBKUtil.getGBKLength(address) > 100) {
                    Map<String, Object> errorMap = getMap(StatusCodes.IS_EXCESSLENGTH, "第" + k + "行,地址长度不能大于100个字节");
                    list.add(errorMap);
                }
            }
            //电话
            //String phoneNum = "^((0\\d{2,3}-\\d{7,8})|(0\\d{2,3}\\d{7,8})|(1[3456789]\\d{9}))$";
            String phone = buyerEntity.getPhone();
            if (StringUtils.isNotBlank(phone)) {
            
                if (GBKUtil.getGBKLength(phone) > 20) {
                    map.put(OrderManagementConstant.CODE, ConfigureConstant.STRING_9999);
                    Map<String, Object> errorMap = getMap(StatusCodes.IS_EXCESSLENGTH, "第" + k + "行,电话不能大于20个字节");
                    list.add(errorMap);
                }
            }
        
            //地址和电话总长度超过100位
            if (StringUtils.isNotBlank(phone) && StringUtils.isNotBlank(address) && (GBKUtil.getGBKLength(address) + GBKUtil.getGBKLength(phone)) > 100) {
                map.put(OrderManagementConstant.CODE, ConfigureConstant.STRING_9999);
                Map<String, Object> errorMap = getMap(StatusCodes.IS_EXCESSLENGTH, "第" + k + "行,地址和电话总长度超过100位");
                list.add(errorMap);
            }
        
        
            //开户银行
            String bankOfDeposit = buyerEntity.getBankOfDeposit();
            if (StringUtils.isNotBlank(bankOfDeposit)) {
                if (bankOfDeposit.contains("|")) {
                    map.put(OrderManagementConstant.CODE, ConfigureConstant.STRING_9999);
                    Map<String, Object> errorMap = getMap(StatusCodes.NON_CONFORMITY, "第" + k + "行," + bankOfDeposit + "开户银行不能存在'|'");
                    list.add(errorMap);
                } else if (GBKUtil.getGBKLength(bankOfDeposit) > 100) {
                    map.put(OrderManagementConstant.CODE, ConfigureConstant.STRING_9999);
                    Map<String, Object> errorMap = getMap(StatusCodes.IS_EXCESSLENGTH, "第" + k + "行,开户银行名称长度不能大于100个字节");
                    list.add(errorMap);
                }
            }
            //银行账号
            String bankNumber = buyerEntity.getBankNumber();
            //String bank ="^([1-9]{1})(\\d{14}|\\d{18}|\\d{19}|\\d{21})$";
            String bank = "^([1-9])([0-9]{17,22})$";
            if (StringUtils.isNotBlank(bankNumber)) {
                if (!bankNumber.matches(bank)) {
                    map.put(OrderManagementConstant.CODE, ConfigureConstant.STRING_9999);
                    Map<String, Object> errorMap = getMap(StatusCodes.NON_CONFORMITY, "第" + k + "行," + bankNumber + "银行账号不符合规范");
                    list.add(errorMap);
                } else if (GBKUtil.getGBKLength(bankNumber) > 30) {
                    map.put(OrderManagementConstant.CODE, ConfigureConstant.STRING_9999);
                    Map<String, Object> errorMap = getMap(StatusCodes.IS_EXCESSLENGTH, "第" + k + "行,银行账号长度不能大于30个字节");
                    list.add(errorMap);
                }
            }
        
            //开户银行和银行账号总长度超过100位
            if (StringUtils.isNotBlank(bankNumber) && StringUtils.isNotBlank(bankOfDeposit) && (GBKUtil.getGBKLength(bankOfDeposit) + GBKUtil.getGBKLength(bankNumber)) > 100) {
                map.put(OrderManagementConstant.CODE, ConfigureConstant.STRING_9999);
                Map<String, Object> errorMap = getMap(StatusCodes.IS_EXCESSLENGTH, "第" + k + "行,开户银行和银行账号总长度超过100位");
                list.add(errorMap);
            }
        
        
            //邮箱
            String email = buyerEntity.getEmail();
            String emailRegular = "^([a-zA-Z0-9_\\.\\-])+\\@(([a-zA-Z0-9\\-])+\\.)+([a-zA-Z0-9]{2,4})+$";
            if (StringUtils.isNotBlank(email)) {
                if (!email.matches(emailRegular)) {
                    map.put(OrderManagementConstant.CODE, ConfigureConstant.STRING_9999);
                    Map<String, Object> errorMap = getMap(StatusCodes.NON_CONFORMITY, "第" + k + "行," + email + "邮箱不符合规范");
                    list.add(errorMap);
                
                } else if (bankOfDeposit.length() > 60) {
                    map.put(OrderManagementConstant.CODE, ConfigureConstant.STRING_9999);
                    Map<String, Object> errorMap = getMap(StatusCodes.IS_EXCESSLENGTH, "第" + k + "行,邮箱长度不能大于60个字符");
                    list.add(errorMap);
                }
            }
            //备注
            String remarks = buyerEntity.getRemarks();
            if (StringUtils.isNotBlank(remarks)) {
                if (GBKUtil.getGBKLength(remarks) > 150) {
                    map.put(OrderManagementConstant.CODE, ConfigureConstant.STRING_9999);
                    Map<String, Object> errorMap = getMap(StatusCodes.IS_EXCESSLENGTH, "第" + k + "行,备注长度不能大于150个字节");
                    list.add(errorMap);
                }
            }
        
            //购方编码
            String ghfbm = buyerEntity.getBuyerCode();
            if (StringUtils.isNotBlank(ghfbm)) {
                if (GBKUtil.getGBKLength(ghfbm) > 50) {
                    map.put(OrderManagementConstant.CODE, ConfigureConstant.STRING_9999);
                    Map<String, Object> errorMap = getMap(StatusCodes.IS_EXCESSLENGTH, "第" + k + "行,购方编码长度不能大于50个字节");
                    list.add(errorMap);
                }
            }
        }
    
        Map<String, Object> resultMap = new HashMap<>(10);
        R result = R.ok();
        int importFailCount = 0;
        if(CollectionUtils.isNotEmpty(list)){
            resultMap.put("list", list);
            result.put(OrderManagementConstant.CODE, ConfigureConstant.STRING_9999)
                    .put(OrderManagementConstant.MESSAGE,"上传失败");
        }else{
            for (BuyerEntity buyerEntity : buyerEntityList) {
                //数据重复性校验
                String uuid = apiInvoiceCommonService.getGenerateShotKey();
                log.info("添加开始执行 uuid = {}", uuid);
                buyerEntity.setId(apiInvoiceCommonService.getGenerateShotKey());
                if (StringUtils.isBlank(buyerEntity.getBuyerCode())) {
                    buyerEntity.setBuyerCode(apiInvoiceCommonService.getGenerateShotKey());
                }

                int i = 0;
                try {
                    i = buyerDao.insertBuyer(buyerEntity);
                } catch (Exception e) {
                    log.error("购买方信息excel导入，保存数据库异常：" + e);
                }
                if(i == 1){
                    importFailCount++;
                }else {
                    map.put(OrderManagementConstant.CODE, ConfigureConstant.STRING_9999);
                    map.put("msg", "第" + k + "行，添加数据库失败");
                    list.add(map);
                    log.info("添加失败");
                }
                resultMap.put("list", list);
            }
        }
        return result.put("data", resultMap).put("count",buyerEntityList.size())
                .put("importResult","导入数据共"+ buyerEntityList.size() +"条,成功"+importFailCount+"条");
    }
    
    @Override
    public BuyerEntity queryBuyerByPurchaseName(String purchaseName, String xhfNsrsbh) {
        List<String> shList = new ArrayList<>();
        shList.add(xhfNsrsbh);
        BuyerEntity buyerEntity = new BuyerEntity();
        buyerEntity.setPurchaseName(purchaseName);
        List<BuyerEntity> buyerEntities = buyerDao.selectBuyerByBuyerEntity(buyerEntity, shList);
        if (buyerEntities != null && buyerEntities.size() > 0) {
            return buyerEntities.get(0);
        } else {
            return null;
        }
    }
    
    
    @Override
    public BuyerEntity queryBuyerInfoByxhfNsrsbhAndBuyerCode(String xhfNsrsbh, String buyerCode) {
        log.info("{}根据销方税号和自编ID查询购方信息{}", xhfNsrsbh, buyerCode);
        List<String> shList = new ArrayList<>();
        shList.add(xhfNsrsbh);
        BuyerEntity buyerEntity = new BuyerEntity();
        buyerEntity.setBuyerCode(buyerCode);
        List<BuyerEntity> buyerEntities = buyerDao.selectBuyerByBuyerEntity(buyerEntity, shList);
        log.info("根据销方税号和自编ID查询购方信息结果{}", JsonUtils.getInstance().toJsonString(buyerEntity));
        if (buyerEntities != null && buyerEntities.size() > 0) {
            return buyerEntities.get(0);
        } else {
            return null;
        }
    }

    @Override
    public R syncBuyer(BuyerEntity buyerEntity, String operationType) {
        OrderInfoContentEnum orderInfoContentEnum = OrderInfoContentEnum.BUYER_MESSAGE_SYNC_SUCCESS;
        try {
            //查询数据库中是否有当前请求信息
            List<String> shList = new ArrayList<>();
            shList.add(buyerEntity.getXhfNsrsbh());
            BuyerEntity buyerEntityOld = queryBuyerInfoByxhfNsrsbhAndBuyerCode(buyerEntity.getXhfNsrsbh(), buyerEntity.getBuyerCode());
            switch (operationType) {
                case "0":
                    if (Objects.isNull(buyerEntityOld)) {
                        buyerEntity.setId(apiInvoiceCommonService.getGenerateShotKey());
                        buyerDao.insertBuyer(buyerEntity);
                    } else {
                        orderInfoContentEnum = OrderInfoContentEnum.BUYER_MESSAGE_SYNC_INSERT;
                    }
                    break;
                case "1":
                    if(Objects.nonNull(buyerEntityOld)){
                        buyerEntity.setId(buyerEntityOld.getId());
                        buyerDao.updateBuyer(buyerEntity, shList);
                    }else {
                        orderInfoContentEnum = OrderInfoContentEnum.BUYER_MESSAGE_SYNC_UPDATE;
                    }
                    break;
                case "2":
                    if (Objects.nonNull(buyerEntityOld)) {
                        buyerDao.deleteBuyerById(buyerEntityOld.getId(), shList);
                    } else {
                        orderInfoContentEnum = OrderInfoContentEnum.BUYER_MESSAGE_SYNC_DELETE;
                    }
                    break;
                default:
                    log.error("不存在的类型");
                    break;
            }
        } catch (Exception e) {
            log.error("同步购买方信息异常：{}",e.getMessage());
            orderInfoContentEnum = OrderInfoContentEnum.BUYER_MESSAGE_SYNC_ERROR;
        }
        log.info("同步购买方信息：{}",orderInfoContentEnum.getMessage());
        return R.setCodeAndMsg(orderInfoContentEnum,null);
    }


    @Override
    public R saveOrUpdateBuyerInfo(BuyerEntity buyerEntity) {
    
        /**
         * 根据入参进行判断是新增还是修改
         */
        List<String> shList = new ArrayList<>();
        shList.add(buyerEntity.getXhfNsrsbh());
        R r = new R();
    
        if (StringUtils.isNotBlank(buyerEntity.getId())) {
        
            buyerEntity.setModifyTime(new Date());
            int i = buyerDao.updateBuyer(buyerEntity, shList);
        
            if (i <= 0) {
                r.put("message", "修改失败").put(OrderManagementConstant.CODE, ConfigureConstant.STRING_9999);
            }

        }else {
            BuyerEntity buyerEntity1 = new BuyerEntity();
            buyerEntity1.setBuyerCode(buyerEntity.getBuyerCode());
            buyerEntity1.setTaxpayerCode(buyerEntity.getTaxpayerCode());
            buyerEntity1.setPurchaseName(buyerEntity.getPurchaseName());
            List<BuyerEntity> queryBuyerEntity = buyerDao.selectBuyerByBuyerEntity(buyerEntity1, shList);
            if (queryBuyerEntity != null && queryBuyerEntity.size() > 0) {
            
                buyerEntity.setModifyTime(new Date());
                buyerEntity.setBuyerCode(queryBuyerEntity.get(0).getBuyerCode());
                buyerEntity.setId(queryBuyerEntity.get(0).getId());
                //更新客户信息
                int i = buyerDao.updateBuyer(buyerEntity, shList);
            
                if (i <= 0) {
                    r.put("message", "修改失败").put(OrderManagementConstant.CODE, ConfigureConstant.STRING_9999);
                }
            
            
            }else{
                //新增购方信息
                if(StringUtils.isBlank(buyerEntity.getBuyerCode())){
                    buyerEntity.setBuyerCode(apiInvoiceCommonService.getGenerateShotKey());
                }
                String uuid = apiInvoiceCommonService.getGenerateShotKey();
                log.info("添加开始执行开始执行 uuid = {}", uuid);
                buyerEntity.setId(uuid);

                int i = buyerDao.insertBuyer(buyerEntity);
                if (i <= 0) {
                    r.put("message", "添加失败").put(OrderManagementConstant.CODE, ConfigureConstant.STRING_9999);
                }
            }


        }

        return R.ok().put(OrderManagementConstant.MESSAGE,"保存成功!");

    }


    @Transactional(rollbackFor = Exception.class)
    @Override
    public R saveBuyerInfoList(List<BuyerEntity> buyerList, String userId) {
    
        //业务挪到provider
        if (CollectionUtils.isEmpty(buyerList)) {
            return R.error(OrderInfoContentEnum.GENERATE_READY_ORDER_DATA_ERROR);
        }
        List<R> errorList = new ArrayList<>();
        //数据校验
    
        for (BuyerEntity buyerEntity : buyerList) {
            boolean isEdit = ConfigureConstant.STRING_1.equals(buyerEntity.getIsEdit());
            if (!isEdit) {
            
                //税号非空校验
                if (StringUtils.isBlank(buyerEntity.getXhfNsrsbh())) {
                    return R.error(OrderInfoContentEnum.GENERATE_READY_ORDER_DATA_ERROR);
                
                }
            
                //是否重复校验
                /**
                 * 如果id不为空说明是编辑过来的数据,优先使用id进行查询,查询结果如果和入参一样,则代表更新操作,不进行拦截
                 * 如果查询结果和入参不一样,则根据入参的名称和税号进行查询.判断是否存在数据.如果存在说明重复进行拦截.
                 *
                 * 如果id为空,说明为新增,新增只需要判断名称是否重复即可
                 *
                 */
                BuyerEntity existBuyer = isExistBuyer(buyerEntity);
                if (existBuyer != null && StringUtils.isNotBlank(existBuyer.getTaxpayerCode()) && StringUtils.isNotBlank(existBuyer.getPurchaseName())) {
    
                    if (StringUtils.isNotBlank(buyerEntity.getId())) {
                        if (!existBuyer.getTaxpayerCode().equals(buyerEntity.getTaxpayerCode()) || !existBuyer.getPurchaseName().equals(buyerEntity.getPurchaseName())) {
                            String buyerId = buyerEntity.getId();
                            buyerEntity.setId("");
                            BuyerEntity existBuyer1 = isExistBuyer(buyerEntity);
                            buyerEntity.setId(buyerId);
                            if (existBuyer1 != null && StringUtils.isNotBlank(existBuyer1.getTaxpayerCode()) && StringUtils.isNotBlank(existBuyer1.getPurchaseName())) {
                                buyerEntity.setId(existBuyer.getId());
                                errorList.add(R.error().put("xhfNsrsbh", buyerEntity.getXhfNsrsbh()).put("xhfMc", buyerEntity.getXhfMc())
                                        .put("ghfMc", buyerEntity.getPurchaseName()).put("ghfNsrsbh", buyerEntity.getTaxpayerCode()));
    
                            }
    
                        }
                    } else {
                        buyerEntity.setId(existBuyer.getId());
                        errorList.add(R.error().put("xhfNsrsbh", buyerEntity.getXhfNsrsbh()).put("xhfMc", buyerEntity.getXhfMc())
                                .put("ghfMc", buyerEntity.getPurchaseName()).put("ghfNsrsbh", buyerEntity.getTaxpayerCode()));
                    }
    
                }
            
            }
        
            if (CollectionUtils.isNotEmpty(errorList)) {
                return R.error().put("errorList", errorList).put(OrderManagementConstant.CODE, "9095").put("data", buyerList);
            }
        }
    
        //数据补全入库
        for (BuyerEntity buyerEntity : buyerList) {
            buyerEntity.setGhfQylx(OrderInfoEnum.GHF_QYLX_01.getKey());
            buyerEntity.setPurchaseName(StringUtil.replaceStr(buyerEntity.getPurchaseName(), true));
            buyerEntity.setModifyUserId(userId);
            buyerEntity.setCreateUserId(userId);
            saveOrUpdateBuyerInfo(buyerEntity);
        }
        return R.ok();
    }

    /**
     * 销方下购方信息是否存在
     * @param buyerEntity
     * @return
     */
    private BuyerEntity isExistBuyer(BuyerEntity buyerEntity) {
    
        BuyerEntity queryBuyerEntity = new BuyerEntity();
        if (StringUtils.isNotBlank(buyerEntity.getId())) {
            queryBuyerEntity.setId(buyerEntity.getId());
        } else {
            queryBuyerEntity.setTaxpayerCode(buyerEntity.getTaxpayerCode());
            queryBuyerEntity.setPurchaseName(buyerEntity.getPurchaseName());
        }
    
        List<String> shList = new ArrayList<>();
        shList.add(buyerEntity.getXhfNsrsbh());
        List<BuyerEntity> result = buyerDao.selectBuyerByBuyerEntity(queryBuyerEntity, shList);
        if (result != null && result.size() > 0) {
            log.debug("购方名称和税号信息已存在,数据为:{}", JsonUtils.getInstance().toJsonString(result));
            return result.get(0);
        }
    
        return null;
    }

    /**
     * 封装错误信息
     *
     * @param code
     * @param msg
     * @return
     */
    private Map<String, Object> getMap(String code, String msg) {
        Map<String, Object> map = new HashMap<>(10);
        map.put(OrderManagementConstant.CODE, code);
        map.put("msg", msg);
        return map;
    }
    
}
