package com.dxhy.order.service.impl;/**
 * Created by thinkpad on 2020-05-25.
 */

import com.dxhy.order.api.ApiInvoiceCommonService;
import com.dxhy.order.api.ApiUserGuiderService;
import com.dxhy.order.constant.ConfigureConstant;
import com.dxhy.order.constant.UserGuiderEnum;
import com.dxhy.order.dao.UserGuiderInfoMapper;
import com.dxhy.order.model.UserGuiderInfo;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.dubbo.config.annotation.Service;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * @ClassName UserGuiderServiceIpl
 * @Author ysy
 * @Date 2020-05-25 10:44
 **/
@Service
public class UserGuiderServiceImpl implements ApiUserGuiderService {
    
    
    @Resource
    private UserGuiderInfoMapper userGuiderInfoMapper;
    
    @Resource
    private ApiInvoiceCommonService apiInvoiceCommonService;
    
    /**
     * @param userGuiderInfo
     * @return
     */
    @Override
    public List<UserGuiderInfo> queryUserGuiderList(UserGuiderInfo userGuiderInfo) {

        List<UserGuiderInfo> resultList = new ArrayList<>();
        //查询用户下所有用户引导操作
        UserGuiderInfo queryUserGuiderParam = new UserGuiderInfo();
        queryUserGuiderParam.setXhfNsrsbh(userGuiderInfo.getXhfNsrsbh());
        queryUserGuiderParam.setOperateGroup(userGuiderInfo.getOperateGroup());
        List<UserGuiderInfo> userGuiderInfoList = userGuiderInfoMapper.queryUserGuiderList(queryUserGuiderParam);


        if (CollectionUtils.isNotEmpty(userGuiderInfoList)) {

            for(UserGuiderInfo guiderInfo : userGuiderInfoList) {

                if (UserGuiderEnum.SET_UP_INVOICE_PARAM.getKey().equals(guiderInfo.getOperate())) {
                    if (userGuiderInfo.getUid().equals(guiderInfo.getUid())) {
                        resultList.add(guiderInfo);
                    }
                } else {
                    resultList.add(guiderInfo);
                }

            }
        }

        return resultList;
    }

    /**
     * @param userGuiderInfo
     * @return
     */
    @Override
    public boolean updateUserGuider(UserGuiderInfo userGuiderInfo) {


        UserGuiderInfo queryUserGuider = new UserGuiderInfo();
        queryUserGuider.setOperateGroup(userGuiderInfo.getOperateGroup());
        queryUserGuider.setOperate(userGuiderInfo.getOperate());
        queryUserGuider.setXhfNsrsbh(userGuiderInfo.getXhfNsrsbh());
        if (UserGuiderEnum.SET_UP_INVOICE_PARAM.getKey().equals(userGuiderInfo.getOperate())) {
            queryUserGuider.setUid(userGuiderInfo.getUid());
        }
        List<UserGuiderInfo> userGuiderInfos = userGuiderInfoMapper.queryUserGuiderList(queryUserGuider);
        //没有的话新增 已有的话更新
        if (CollectionUtils.isNotEmpty(userGuiderInfos)) {

            UserGuiderInfo oldUserGuiderInfo = userGuiderInfos.get(0);
            if (!ConfigureConstant.STRING_1.equals(oldUserGuiderInfo.getStatus())) {
                UserGuiderInfo updateUserGuider = new UserGuiderInfo();
                updateUserGuider.setId(oldUserGuiderInfo.getId());
                updateUserGuider.setStatus("1");
                int i = userGuiderInfoMapper.updateByPrimaryKeySelective(updateUserGuider);
                return i > 0;
            }

        } else {
            Date date = new Date();
            userGuiderInfo.setCreateTime(date);
            userGuiderInfo.setUpdateTime(date);
            UserGuiderEnum codeValue = UserGuiderEnum.getCodeValue(userGuiderInfo.getOperate());
            userGuiderInfo.setOperateDescription(codeValue.getValue());
            //是否完成引导 0 未完成 1 已完成
            userGuiderInfo.setStatus("1");
            userGuiderInfo.setId(apiInvoiceCommonService.getGenerateShotKey());
            userGuiderInfo.setStep(codeValue.getStep());
            int i = userGuiderInfoMapper.insertSelective(userGuiderInfo);
            return i > 0;

        }

        return true;
    }
}
