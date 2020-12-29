package com.dxhy.order.consumer.modules.user.service.impl;

import cn.hutool.core.util.ObjectUtil;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.dxhy.order.constant.ConfigureConstant;
import com.dxhy.order.constant.Constant;
import com.dxhy.order.constant.OrderManagementConstant;
import com.dxhy.order.consumer.config.OpenApiConfig;
import com.dxhy.order.consumer.config.UserCenterConfig;
import com.dxhy.order.consumer.modules.user.service.UserInfoService;
import com.dxhy.order.consumer.protocol.oldusercenter.SsoUser;
import com.dxhy.order.consumer.protocol.oldusercenter.SysDeptEntity;
import com.dxhy.order.consumer.protocol.oldusercenter.SysMenuEntity;
import com.dxhy.order.consumer.protocol.usercenter.DeptEntity;
import com.dxhy.order.consumer.protocol.usercenter.MenuEntity;
import com.dxhy.order.consumer.protocol.usercenter.TaxPlayerCodeDept;
import com.dxhy.order.consumer.protocol.usercenter.UserEntity;
import com.dxhy.order.exceptions.OrderReceiveException;
import com.dxhy.order.model.R;
import com.dxhy.order.utils.HttpUtils;
import com.dxhy.order.utils.JsonUtils;
import com.google.common.collect.Lists;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.HttpStatus;
import org.apache.http.entity.ContentType;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import java.util.*;
import java.util.stream.Collectors;

/**
 * 用户信息获取业务层
 *
 * @author ：杨士勇
 * @ClassName ：UserInfoServiceImpl
 * @date ：2018年9月3日 上午10:59:37
 */
@Slf4j
@Service
public class UserInfoServiceImpl implements UserInfoService {
    private final static String LOGGER_MSG = "(用户信息获取业务层)";
    
    
    @Override
    public UserEntity getUser() {
    
    
        try {
            R result = getUserInfo();
            if (!result.isEmpty() && ConfigureConstant.STRING_0000.equals(result.get(OrderManagementConstant.CODE))) {
                return (UserEntity) result.get(OrderManagementConstant.DATA);
            }
        } catch (Exception e) {
            log.error("{}调用用户信息获取服务异常:{}", LOGGER_MSG, e);
        }
    
        return null;
    }
    
    @Override
    public R getUserInfo() {
    
        /**
         * todo 兼容之前大B用户信息获取接口,
         * 如果接口header中传递cookieId,则说明使用大B用户中心,
         * 如果没有查询到,使用新用户中心数据
         */
    
        HttpServletRequest req = ((ServletRequestAttributes) Objects.requireNonNull(RequestContextHolder.getRequestAttributes())).getRequest();
        String token = req.getHeader(ConfigureConstant.TOKEN);
        String dxhySsoSessionId = req.getHeader(ConfigureConstant.DXHY_SSO_SESSION_ID);
        String fzyyToken = req.getHeader(ConfigureConstant.FZYYTOKEN);
        log.debug("{}调用用户信息接口请求地址为:{}", LOGGER_MSG, JsonUtils.getInstance().toJsonString(req.getRequestURI()));
    
        if (StringUtils.isNotBlank(fzyyToken) && req.getRequestURI().contains("fzyyTaxManager")) {
            return R.ok().put(OrderManagementConstant.CODE, ConfigureConstant.STRING_0000);
        }
        Map<String, String> headMap = new HashMap<>(2);
        headMap.put("Content-Type", ContentType.APPLICATION_JSON.toString());
        /**
         * 处理辅助运营token,新用户中心token,大B的token共存情况
         * 1.如果都不为空,
         */
    
        if (StringUtils.isNotBlank(dxhySsoSessionId) && StringUtils.isBlank(token)) {
            headMap.put("Cookie", "dxhy_sso_sessionid=" + dxhySsoSessionId);
            long start = System.currentTimeMillis();
            log.debug("{}调用用户信息接口，开始时间:{},URL:{},入参:{}", LOGGER_MSG, start, OpenApiConfig.queryDBUserInfo, JsonUtils.getInstance().toJsonString(headMap));
            String result = HttpUtils.doGetWithHeader(OpenApiConfig.queryDBUserInfo, null, headMap);
            long end = System.currentTimeMillis();
            /**
             * 暂时注释日志输出.
             */
//            log.debug("{}调用用户信息获取信息为,结束时间:{},耗时(毫秒）:{},出参:{}", LOGGER_MSG, end, end - start, result);
            if (StringUtils.isNotBlank(result)) {
                R r = JsonUtils.getInstance().parseObject(result, R.class);
                if (r.get(OrderManagementConstant.CODE) != null && HttpStatus.SC_OK == Integer.parseInt(r.get(OrderManagementConstant.CODE).toString())) {
                    SsoUser ssoUser = JsonUtils.getInstance().parseObject(r.get(OrderManagementConstant.DATA).toString(), SsoUser.class);
                    if (ObjectUtil.isNull(ssoUser)) {
                        return R.error();
                    } else {
                        UserEntity userEntity = ssoUserTransUserEntity(ssoUser);
                        if (userEntity.getTaxplayercodeDeptList() == null) {
                            r.put(OrderManagementConstant.CODE, "9999");
                            r.put(OrderManagementConstant.ALL_MESSAGE, "用户未设置企业信息");
                            return r;
                        }
                        if (userEntity.getTaxplayercodeDeptList().size() < 1) {
                            r.put(OrderManagementConstant.CODE, "9999");
                            r.put(OrderManagementConstant.ALL_MESSAGE, "用户未设置企业信息");
                            return r;
                        } else {
                            List<TaxPlayerCodeDept> taxPlayerCodeDeptList = userEntity.getTaxplayercodeDeptList().stream().filter(taxPlayerCodeDept -> StringUtils.isNotBlank(taxPlayerCodeDept.getTaxpayerCode())).distinct().collect(Collectors.toList());
                            if (taxPlayerCodeDeptList.size() < 1) {
                                r.put(OrderManagementConstant.CODE, "9999");
                                r.put(OrderManagementConstant.ALL_MESSAGE, "用户未完善企业信息");
                                return r;
                            }
                        }
                        r.put(OrderManagementConstant.DATA, userEntity);
                    }
    
                    r.put(OrderManagementConstant.CODE, ConfigureConstant.STRING_0000);
                } else {
                    if (ObjectUtil.isNull(r.get(OrderManagementConstant.ALL_MESSAGE)) && ObjectUtil.isNotNull(r.get(OrderManagementConstant.MESSAGE))) {
                        r.put(OrderManagementConstant.ALL_MESSAGE, r.get(OrderManagementConstant.MESSAGE));
                    }
                }
                return r;
            } else {
            
                return R.error().put(OrderManagementConstant.CODE, HttpStatus.SC_UNAUTHORIZED).put("redirectUrl", UserCenterConfig.redirectUrl);
            }
        
        }
    
    
        headMap.put(ConfigureConstant.AUTHORIZATION, ConfigureConstant.BEARER + "" + token);
    
        try {
            long start = System.currentTimeMillis();
            log.debug("{}调用用户信息接口，开始时间:{},URL:{},入参:{}", LOGGER_MSG, start, OpenApiConfig.queryUserInfo, JsonUtils.getInstance().toJsonString(headMap));
            String result = HttpUtils.doGetWithHeader(OpenApiConfig.queryUserInfo, null, headMap);
            long end = System.currentTimeMillis();
//            log.debug("{}调用用户信息获取信息为,结束时间:{},耗时(毫秒）:{},出参:{}", LOGGER_MSG, end, end - start, result);
            if (StringUtils.isNotBlank(result)) {
                R r = JsonUtils.getInstance().parseObject(result, R.class);
                if (String.valueOf(HttpStatus.SC_UNAUTHORIZED).equals(r.get(ConfigureConstant.CODE))) {
                    r.put("redirectUrl", UserCenterConfig.redirectUrl);
                }
                if (ConfigureConstant.STRING_0000.equals(r.get(OrderManagementConstant.CODE))) {
                    UserEntity userEntity = JsonUtils.getInstance().parseObject(r.get(OrderManagementConstant.DATA).toString(), UserEntity.class);
                    if (userEntity != null) {
                        if (userEntity.getTaxplayercodeDeptList() == null) {
                            r.put(OrderManagementConstant.CODE, "9999");
                            r.put(OrderManagementConstant.ALL_MESSAGE, "用户未设置企业信息");
                            return r;
                        }
                        if (userEntity.getTaxplayercodeDeptList().size() < 1) {
                            r.put(OrderManagementConstant.CODE, "9999");
                            r.put(OrderManagementConstant.ALL_MESSAGE, "用户未设置企业信息");
                            return r;
                        } else {
                            List<TaxPlayerCodeDept> taxPlayerCodeDeptList = userEntity.getTaxplayercodeDeptList().stream().filter(taxPlayerCodeDept -> StringUtils.isNotBlank(taxPlayerCodeDept.getTaxpayerCode())).distinct().collect(Collectors.toList());
                            if (taxPlayerCodeDeptList.size() < 1) {
                                r.put(OrderManagementConstant.CODE, "9999");
                                r.put(OrderManagementConstant.ALL_MESSAGE, "用户未完善企业信息");
                                return r;
                            }
                        }
                        r.put(OrderManagementConstant.DATA, userEntity);
                    }
                } else {
                    if (ObjectUtil.isNull(r.get(OrderManagementConstant.ALL_MESSAGE)) && ObjectUtil.isNotNull(r.get(OrderManagementConstant.MESSAGE))) {
                        r.put(OrderManagementConstant.ALL_MESSAGE, r.get(OrderManagementConstant.MESSAGE));
                    }
                }
                return r;
    
    
            }
        } catch (Exception e) {
            log.error("{}调用用户信息获取服务异常:{}", LOGGER_MSG, e);
        }
        return R.error();
    }
    
    @Override
    public DeptEntity getDepartment() {
        UserEntity user = getUser();
        if (user != null) {
            return user.getDept();
        }
        return null;
    }
    
    
    /**
     * 获取当前登陆人下的所有不为空的税号
     */
    @Override
    public List<String> getTaxpayerCodeList() {
        Set<String> texCodeList = new HashSet<>();
        UserEntity user = getUser();
        List<TaxPlayerCodeDept> taxplayercodeDeptList = user.getTaxplayercodeDeptList();
        for (TaxPlayerCodeDept sysDeptEntity : taxplayercodeDeptList) {
            texCodeList.add(sysDeptEntity.getTaxpayerCode());
        }
        return new ArrayList<>(texCodeList);
    }
    
    /**
     *
     */
    @Override
    public Map<String, DeptEntity> getTaxpayerEntityMap() {
        Map<String, DeptEntity> resultMap = new HashMap<>(10);
        UserEntity user = getUser();
        List<TaxPlayerCodeDept> taxplayercodeDeptList = user.getTaxplayercodeDeptList();
        for (TaxPlayerCodeDept sysDeptEntity : taxplayercodeDeptList) {
            if (StringUtils.isNotBlank(sysDeptEntity.getTaxpayerCode())) {
                resultMap.put(sysDeptEntity.getTaxpayerCode(), sysDeptEntity);
            }
        }
        return resultMap;
    }
    
    @Override
    public List<DeptEntity> getTaxpayerEntityList() {
        List<DeptEntity> texCodeList = new ArrayList<>();
        UserEntity user = getUser();
        List<TaxPlayerCodeDept> taxplayercodeDeptList = user.getTaxplayercodeDeptList();
        for (TaxPlayerCodeDept sysDeptEntity : taxplayercodeDeptList) {
            if (StringUtils.isNotBlank(sysDeptEntity.getTaxpayerCode())) {
                texCodeList.add(sysDeptEntity);
            }
        }
        return texCodeList;
    }
    
    /**
     * 仅适用于辅助运营用户信息获取
     *
     * @return
     */
    @Override
    public List<DeptEntity> getFzyyTaxpayerEntityList() throws OrderReceiveException {
        List<DeptEntity> texCodeList = new ArrayList<>();
        List<DeptEntity> taxplayercodeDeptList = getTotalTaxList(Lists.newArrayList(), 100, 1, true, null);
        for (DeptEntity sysDeptEntity : taxplayercodeDeptList) {
            if (StringUtils.isNotBlank(sysDeptEntity.getTaxpayerCode())) {
                texCodeList.add(sysDeptEntity);
            }
        }
        return texCodeList;
    }
    
    
    @Override
    public DeptEntity querySysDeptEntityByTaxplayercode(String taxpayerCode) {
        UserEntity user = getUser();
        List<TaxPlayerCodeDept> taxplayercodeDeptList = user.getTaxplayercodeDeptList();
        for (TaxPlayerCodeDept sysDeptEntity : taxplayercodeDeptList) {
            if (StringUtils.isNotBlank(sysDeptEntity.getTaxpayerCode()) && sysDeptEntity.getTaxpayerCode().equals(taxpayerCode)) {
                return sysDeptEntity;
            }
        }
        return null;
    }
    
    @Override
    public DeptEntity querySysDeptEntityFromUrl(String taxpayerCode, String taxpayerName) {
        Map<String, String> paraMap = new HashMap<>(2);
        paraMap.put(Constant.TAXPAYERCODE, taxpayerCode);
        paraMap.put(Constant.TAXPAYERNAME, taxpayerName);
        Map<String, String> headMap = new HashMap<>(2);
        headMap.put("Content-Type", ContentType.APPLICATION_JSON.toString());
        try {
            /**
             * todo 根据ssourl判断调用获取企业信息接口
             */
            String ssoUrl = OpenApiConfig.queryDBUserInfo;
            String queryTaxInfo = OpenApiConfig.queryOrgInfoByCode;
            if (StringUtils.isNotBlank(ssoUrl) && ssoUrl.contains(ConfigureConstant.USER_CENTER_URL)) {
                queryTaxInfo = OpenApiConfig.queryOrgInfoByCode;
            } else {
                queryTaxInfo = OpenApiConfig.queryDBOrgInfoByCode;
            }

            log.info("{},获取企业信息的接口,入参:{}",LOGGER_MSG,JsonUtils.getInstance().toJsonString(queryTaxInfo));
            String result = HttpUtils.doPostWithHeader(queryTaxInfo, JsonUtils.getInstance().toJsonString(paraMap), headMap);
            log.info("{}调用用户信息获取信息为:{}", LOGGER_MSG, result);
            if (StringUtils.isNotBlank(result)) {
                JSONObject jsonObject = JSON.parseObject(result);
                if (!jsonObject.isEmpty() && ConfigureConstant.STRING_0000.equals(jsonObject.get(OrderManagementConstant.CODE))) {
                    return JsonUtils.getInstance().parseObject(jsonObject.getString(OrderManagementConstant.DATA), DeptEntity.class);
                }
        
        
            }
        } catch (Exception e) {
            log.error("{}调用用户信息获取服务异常:{}", LOGGER_MSG, e);
            return null;
        }
        return null;
    }
    
    
    public static UserEntity ssoUserTransUserEntity(SsoUser ssoUser) {
        UserEntity userEntity = new UserEntity();
        userEntity.setUserId(ssoUser.getUserId());
        userEntity.setUsername(ssoUser.getUsername());
        userEntity.setPassword(ssoUser.getPassword());
        userEntity.setSalt(ssoUser.getSalt());
        userEntity.setEmail(ssoUser.getEmail());
        userEntity.setPhone(ssoUser.getMobile());
        userEntity.setStatus(ssoUser.getStatus());
        userEntity.setDeptId(String.valueOf(ssoUser.getCurrentDeptId()));
        userEntity.setDeptName("");
        userEntity.setCreateTime(ssoUser.getCreateTime());
        userEntity.setName(ssoUser.getName());
        userEntity.setLastLoginTime(ssoUser.getLastLoginTime());
        userEntity.setCreateBy(ssoUser.getUserId());
        userEntity.setAvatar("");
        userEntity.setDelFlag(0);
        userEntity.setUserType(0);
        userEntity.setUserSource(String.valueOf(ssoUser.getUserSource()));
        userEntity.setUpdateTime(null);
        userEntity.setUpdateBy(0L);
        
        DeptEntity deptEntity = new DeptEntity();
        deptEntity.setDeptId(String.valueOf(ssoUser.getDept().getDeptId()));
        deptEntity.setParentId(String.valueOf(ssoUser.getDept().getParentId()));
        deptEntity.setName(ssoUser.getDept().getName());
        deptEntity.setDeptSname(ssoUser.getDept().getDeptSname());
        deptEntity.setCode(ssoUser.getDept().getCode());
        deptEntity.setLevel(ssoUser.getDept().getLevel());
        deptEntity.setTaxpayerCode(ssoUser.getDept().getTaxpayerCode());
        deptEntity.setTaxpayerProvince("");
        deptEntity.setTaxpayerCity("");
        deptEntity.setTaxpayerCounty("");
        deptEntity.setTaxpayerAddress(ssoUser.getDept().getTaxpayerAddress());
        deptEntity.setTaxpayerPhone(ssoUser.getDept().getTaxpayerPhone());
        deptEntity.setTaxpayerBank(ssoUser.getDept().getTaxpayerBank());
        deptEntity.setTaxpayerAccount(ssoUser.getDept().getTaxpayerAccount());
        deptEntity.setTaxpayerType(0);
        deptEntity.setTaxpayerIndustry("");
        deptEntity.setAccountingPrinciple("");
        deptEntity.setCreateTime(ssoUser.getDept().getCreateTime());
        deptEntity.setDeptType(ssoUser.getDept().getDeptType());
        deptEntity.setCreateUser(ssoUser.getDept().getCreateUser());
        deptEntity.setUpdateUser(0L);
        deptEntity.setContactName(ssoUser.getDept().getContactName());
        deptEntity.setContactPhone(ssoUser.getDept().getContactPhone());
        deptEntity.setContactEmail(ssoUser.getDept().getContactEmail());
        deptEntity.setEnterpriseNumbers(ssoUser.getDept().getEnterpriseNumbers());
        deptEntity.setAuthorizationCode("");
        deptEntity.setSourceId("");
        deptEntity.setOrderNum(ssoUser.getDept().getLevel());
        deptEntity.setUpdateTime(new Date());
        deptEntity.setDelFlag("");
        deptEntity.setDataSource("");
        deptEntity.setMenuId(0L);
        deptEntity.setSetMealId(0L);
        deptEntity.setEinNumber(0);
        userEntity.setDept(deptEntity);
        
        
        List<TaxPlayerCodeDept> taxPlayerCodeDeptList = Lists.newArrayList();
        for (SysDeptEntity sysDeptEntity : ssoUser.getTaxplayercodeDeptList()) {
            TaxPlayerCodeDept taxPlayerCodeDept = new TaxPlayerCodeDept();
            BeanUtils.copyProperties(sysDeptEntity, taxPlayerCodeDept);
            taxPlayerCodeDeptList.add(taxPlayerCodeDept);
        }
        userEntity.setTaxplayercodeDeptList(taxPlayerCodeDeptList);
        
        List<MenuEntity> menuEntityList = Lists.newArrayList();
        for (SysMenuEntity menu : ssoUser.getMenus()) {
            MenuEntity menuEntity = new MenuEntity();
            BeanUtils.copyProperties(menu, menuEntity);
            menuEntityList.add(menuEntity);
        }
        userEntity.setMenus(menuEntityList);
        return userEntity;
    }
    
    public static List<DeptEntity> getTotalTaxList(List<DeptEntity> deptEntityList, int pageSize, int currPage, boolean getAll, String token) throws OrderReceiveException {
        if (StringUtils.isBlank(token)) {
            HttpServletRequest req = ((ServletRequestAttributes) Objects.requireNonNull(RequestContextHolder.getRequestAttributes())).getRequest();
            token = req.getHeader(ConfigureConstant.FZYYTOKEN);
        }
        if (StringUtils.isBlank(token)) {
            return deptEntityList;
        }
        if (ObjectUtil.isNull(deptEntityList)) {
            deptEntityList = Lists.newArrayList();
        }
        Map<String, String> headMap = new HashMap<>(2);
        headMap.put("Content-Type", ContentType.APPLICATION_JSON.toString());
        headMap.put(ConfigureConstant.AUTHORIZATION, ConfigureConstant.BEARER + " " + token);
        long start = System.currentTimeMillis();
        log.debug("{}调用辅助运营用户信息接口，开始时间:{},URL为:{},入参:{}", LOGGER_MSG, start, OpenApiConfig.queryfzyyTaxpayerList, JsonUtils.getInstance().toJsonString(headMap));
        String result = HttpUtils.doGetWithHeader(String.format(OpenApiConfig.queryfzyyTaxpayerList, OpenApiConfig.systemProductId, currPage, pageSize), null, headMap);
        long end = System.currentTimeMillis();
        log.debug("{}调用辅助运营用户信息接口为,结束时间:{},耗时(毫秒）:{},出参:{}", LOGGER_MSG, end, end - start, result);
        if (StringUtils.isNotBlank(result)) {
            R r = JsonUtils.getInstance().parseObject(result, R.class);
    
            if (ConfigureConstant.STRING_0000.equals(r.get(OrderManagementConstant.CODE))) {
                R data = JsonUtils.getInstance().parseObject(r.get(OrderManagementConstant.DATA).toString(), R.class);
                int count = Integer.parseInt(data.get("pages").toString());
                if (data.get("records") != null) {
                    List<Map> records = JSON.parseArray(data.get("records").toString(), Map.class);
                    if (records != null && records.size() > 0) {
                        for (Map record : records) {
                            DeptEntity deptEntity = new DeptEntity();
                            deptEntity.setTaxpayerCode(ObjectUtil.isNull(record.get("taxNo")) ? "" : record.get("taxNo").toString());
                            deptEntity.setName(ObjectUtil.isNull(record.get("companyName")) ? "" : record.get("companyName").toString());
                            if (StringUtils.isBlank(deptEntity.getTaxpayerCode())) {
                                continue;
                            }
                            deptEntityList.add(deptEntity);
                        }
                
                    }
                }
                if (getAll && currPage <= count) {
            
            
                    getTotalTaxList(deptEntityList, pageSize, currPage + 1, true, token);
                }
        
            } else {
                throw new OrderReceiveException(String.valueOf(r.get(OrderManagementConstant.CODE)), String.valueOf(r.get(OrderManagementConstant.ALL_MESSAGE)));
            }
        }
        return deptEntityList;
    }
    
}
