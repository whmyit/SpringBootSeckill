package com.dxhy.order.service.impl;


/**
 * 鉴权业务实现类
 *
 * @author ZSC-DXHY
 * @date 创建时间: 2020-08-14 14:38
 */
import cn.hutool.core.util.RandomUtil;
import com.dxhy.order.api.ApiAuthenticationService;
import com.dxhy.order.api.ApiInvoiceCommonService;
import com.dxhy.order.constant.ConfigureConstant;
import com.dxhy.order.constant.ConfigurerInfo;
import com.dxhy.order.constant.OrderInfoEnum;
import com.dxhy.order.constant.OrderManagementConstant;
import com.dxhy.order.dao.AuthenticationInfoMapper;
import com.dxhy.order.dao.PushInfoMapper;
import com.dxhy.order.model.AuthenticationInfo;
import com.dxhy.order.model.PushInfo;
import com.dxhy.order.model.R;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.dubbo.config.annotation.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.*;


@Service
public class AuthenticationServiceImpl implements ApiAuthenticationService{


    private final String regex = "^([hH][tT]{2}[pP]://|[hH][tT]{2}[pP][sS]://)(([A-Za-z0-9-~]+).)+([A-Za-z0-9-~\\\\\\\\/])+$";


    @Resource
    AuthenticationInfoMapper authenticationInfoMapper;

    @Resource
    PushInfoMapper pushInfoMapper;

    @Resource
    ApiInvoiceCommonService apiInvoiceCommonService;


    @Override
    public List<AuthenticationInfo> queryAuthenInfoList(AuthenticationInfo authenticationInfo, List<String> shList) {
        return authenticationInfoMapper.queryAuthenInfoList(authenticationInfo,shList);
    }

    @Override
    public int updateAuthenInfoByPrimaryKey(AuthenticationInfo authenticationInfo) {
        return authenticationInfoMapper.updateByPrimaryKeySelective(authenticationInfo);
    }

    @Override
    public int addAuthenInfo(AuthenticationInfo authenticationInfo) {
        return authenticationInfoMapper.insert(authenticationInfo);
    }

    @Override
    public AuthenticationInfo queryAuthenInfo(AuthenticationInfo authenticationInfo) {
        return authenticationInfoMapper.queryAuthenInfo(authenticationInfo);
    }

    @Override
    public R queryEnterpreiseConfigInfo(Map<String, String> paramMap) {

        AuthenticationInfo queryAuthenInfo = new AuthenticationInfo();
        queryAuthenInfo.setNsrsbh(paramMap.get("nsrsbh"));
        AuthenticationInfo authenticationInfo = authenticationInfoMapper.queryAuthenInfo(queryAuthenInfo);
        //如果secretId 和 secretkey为空 自动生成secretId 和 secretkey

        if(authenticationInfo == null){
            authenticationInfo = new AuthenticationInfo();
            authenticationInfo.setSecretId(RandomUtil.randomString(32));
            authenticationInfo.setSecretKey(RandomUtil.randomString(36));
        }
        List<String> shList = new ArrayList<String>();
        shList.add(paramMap.get("nsrsbh"));
        Map queryMap = new HashMap<>(2);
        List<String> interfaceType = new ArrayList<>();
        interfaceType.add("1");
        interfaceType.add("2");
        queryMap.put("interfaceType",interfaceType);
        queryMap.put("status","0");
        List<PushInfo> pushInfos = pushInfoMapper.queryPushInfoListByMap(queryMap, shList);


        Map<String,Object> resultMap = convertToMap(authenticationInfo,pushInfos);
        resultMap.put("nsrsbh",paramMap.get("nsrsbh"));
        resultMap.put("xhfMc",paramMap.get("xhfMc"));
        return R.ok().put(OrderManagementConstant.DATA,resultMap);
    }

    private Map<String,Object> convertToMap(AuthenticationInfo authenticationInfo, List<PushInfo> pushInfos) {
        Map<String, Object> resultMap = new HashMap<>(2);
        if(authenticationInfo != null){
            resultMap.put("secretId",authenticationInfo.getSecretId() == null ? "" : authenticationInfo.getSecretId());
            resultMap.put("secretKey",authenticationInfo.getSecretKey() == null ? "" : authenticationInfo.getSecretKey());
        }

        if(CollectionUtils.isNotEmpty(pushInfos)){
            resultMap.put("versionIdent",pushInfos.get(0).getVersionIdent());
        }
        resultMap.put("pushInfoList",pushInfos);
        return resultMap;

    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public R saveEnterpriseCofnigInfo(AuthenticationInfo authInfo, List<PushInfo> pushInfoList) {

        //数据校验
        if(StringUtils.isBlank(authInfo.getNsrsbh())){
            return R.error().put(OrderManagementConstant.MESSAGE,"税号不能为空!");
        }else if(StringUtils.isBlank(authInfo.getSecretId())){
            return R.error().put(OrderManagementConstant.MESSAGE,"SecretId不能为空!");
        }else if(StringUtils.isBlank(authInfo.getSecretKey())){
            return R.error().put(OrderManagementConstant.MESSAGE,"secretKey不能为空!");
        }

        int pushUrlCount = 0;
        int validUrlCount = 0;
        List<PushInfo> validPushList = new ArrayList<>();
        List<PushInfo> invoicePushList = new ArrayList<>();

        //过滤重复的推送地址
        List<String> pushUrlList = new ArrayList<>();
        for(PushInfo push : pushInfoList){
            if(StringUtils.isBlank(push.getNsrsbh())){
                return R.error().put(OrderManagementConstant.MESSAGE,"税号不能为空!");
            }else if(StringUtils.isBlank(push.getInterfaceType())){
                return R.error().put(OrderManagementConstant.MESSAGE,"接口类型不能为空!");
            }else if(StringUtils.isBlank(push.getVersionIdent())){
                return R.error().put(OrderManagementConstant.MESSAGE,"接口版本号不能为空!");
            }else if(StringUtils.isBlank(push.getPushUrl())){
                return R.error().put(OrderManagementConstant.MESSAGE,"推送地址不能为空!");
            }

            if(!push.getPushUrl().matches(regex)){
                return R.error().put(OrderManagementConstant.MESSAGE,"推送地址格式错误!");
            }
            if(!ConfigurerInfo.INTERFACE_VERSION_V4.equals(push.getVersionIdent()) && !ConfigurerInfo.INTERFACE_VERSION_V3.equals(push.getVersionIdent())
                    && !ConfigurerInfo.INTERFACE_VERSION_V2.equals(push.getVersionIdent()) && !ConfigurerInfo.INTERFACE_VERSION_V1.equals(push.getVersionIdent())){
                return R.error().put(OrderManagementConstant.MESSAGE,"推送接口版本号错误!");
            }
            if(!OrderInfoEnum.INTERFACE_TYPE_INVOICE_PUSH_STATUS_2.getKey().equals(push.getInterfaceType())
                    && !OrderInfoEnum.INTERFACE_TYPE_INVOICE_PUSH_STATUS_1.getKey().equals(push.getInterfaceType())){
                return R.error().put(OrderManagementConstant.MESSAGE,"接口类型参数错误!");
            }

            if(pushUrlList.contains(push.getPushUrl())){
                return R.error().put(OrderManagementConstant.MESSAGE,"推送接口地址重复!");
            }else{
                pushUrlList.add(push.getPushUrl());
            }

            if(OrderInfoEnum.INTERFACE_TYPE_INVOICE_PUSH_STATUS_1.getKey().equals(push.getInterfaceType())){
                pushUrlCount ++;
                invoicePushList.add(push);
            }

            if(OrderInfoEnum.INTERFACE_TYPE_INVOICE_PUSH_STATUS_2.getKey().equals(push.getInterfaceType())){
                validUrlCount ++;
                validPushList.add(push);
            }
        }
    
        if (validUrlCount > ConfigureConstant.INT_5) {
            return R.error().put(OrderManagementConstant.MESSAGE, "作废推送地址最大支持配置5条!");
        }
    
        if (pushUrlCount > ConfigureConstant.INT_5) {
            return R.error().put(OrderManagementConstant.MESSAGE, "发票推送地址最大支持配置5条!");
        }


        //更新保存数据
        AuthenticationInfo query = new AuthenticationInfo();
        query.setNsrsbh(authInfo.getNsrsbh());
        AuthenticationInfo authenticationInfo = authenticationInfoMapper.queryAuthenInfo(query);
        if(authenticationInfo == null){
            //数据库不存在相关数据则新增
            authInfo.setId(apiInvoiceCommonService.getGenerateShotKey());
            authInfo.setAuthStatus("0");
            authInfo.setCreateTime(new Date());
            authInfo.setUpdateTime(new Date());
            authenticationInfoMapper.insert(authInfo);

        }else{
            //数据库已存在相关数据更新
            authenticationInfo.setUpdateTime(new Date());
            authenticationInfo.setAuthStatus("0");
            authenticationInfo.setSecretId(authInfo.getSecretId());
            authenticationInfo.setSecretKey(authInfo.getSecretKey());
            authenticationInfoMapper.updateByPrimaryKeySelective(authenticationInfo);
        }

        if(CollectionUtils.isNotEmpty(validPushList)){

            PushInfo updatePushInfo = new PushInfo();
            updatePushInfo.setNsrsbh(validPushList.get(0).getNsrsbh());
            updatePushInfo.setInterfaceType(validPushList.get(0).getInterfaceType());
            updatePushInfo.setStatus("1");
            pushInfoMapper.updateAuthStatusByShAndInterfaceType(updatePushInfo);
            //更新以原来的推送配置为无效状态
            for(PushInfo push : validPushList){
                push.setId(apiInvoiceCommonService.getGenerateShotKey());
                push.setModifyTime(new Date());
                push.setCreateTime(new Date());
                push.setEncryptCode(StringUtils.isBlank(push.getEncryptCode()) ? "" : "0");
                push.setStatus("0");
                push.setZipCode(StringUtils.isBlank(push.getZipCode()) ? "" : "0");
                pushInfoMapper.insertSelective(push);
            }
        }


        if(CollectionUtils.isNotEmpty(invoicePushList)){

            PushInfo updatePushInfo = new PushInfo();
            updatePushInfo.setNsrsbh(invoicePushList.get(0).getNsrsbh());
            updatePushInfo.setInterfaceType(invoicePushList.get(0).getInterfaceType());
            updatePushInfo.setStatus("1");
            pushInfoMapper.updateAuthStatusByShAndInterfaceType(updatePushInfo);
            //更新以原来的推送配置为无效状态
            for(PushInfo push : invoicePushList){
                push.setId(apiInvoiceCommonService.getGenerateShotKey());
                push.setModifyTime(new Date());
                push.setCreateTime(new Date());
                push.setEncryptCode(StringUtils.isBlank(push.getEncryptCode()) ? "" : "0");
                push.setStatus("0");
                push.setZipCode(StringUtils.isBlank(push.getZipCode()) ? "" : "0");
                pushInfoMapper.insertSelective(push);
            }
        }

        return R.ok();
    }
}
