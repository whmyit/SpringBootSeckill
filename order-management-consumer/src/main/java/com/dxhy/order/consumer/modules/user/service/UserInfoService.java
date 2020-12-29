package com.dxhy.order.consumer.modules.user.service;

import com.dxhy.order.consumer.protocol.usercenter.DeptEntity;
import com.dxhy.order.consumer.protocol.usercenter.UserEntity;
import com.dxhy.order.exceptions.OrderReceiveException;
import com.dxhy.order.model.R;

import java.util.List;
import java.util.Map;

/**
 * @author ：杨士勇
 * @ClassName ：UserInfoService
 * @Description ：获取用户信息
 * @date ：2018年9月3日 上午10:58:33
 */

public interface UserInfoService {
    
    
    /**
     * 获取用户信息
     *
     * @return
     */
    UserEntity getUser();
    
    /**
     * 返前端用户信息
     *
     * @return
     */
    R getUserInfo();
    
    /**
     * 获取部门信息
     *
     * @return
     */
    DeptEntity getDepartment();
    
    /**
     * 获取税号列表
     *
     * @return
     */
    List<String> getTaxpayerCodeList();
    
    /**
     * 获取用户部门信息
     *
     * @return
     */
    Map<String, DeptEntity> getTaxpayerEntityMap();
    
    /**
     * 获取用户部门列表
     *
     * @return
     */
    List<DeptEntity> getTaxpayerEntityList();
    
    /**
     * 仅适用于辅助运营获取用户信息
     *
     * @return
     * @throws OrderReceiveException
     */
    List<DeptEntity> getFzyyTaxpayerEntityList() throws OrderReceiveException;
    
    /**
     * 根据税号获取部门信息
     *
     * @param taxpayerCode
     * @return
     */
    DeptEntity querySysDeptEntityByTaxplayercode(String taxpayerCode);
    
    /**
     * 获取企业初始化信息
     *
     * @param taxpayerCode
     * @param taxpayerName
     * @return
     */
    DeptEntity querySysDeptEntityFromUrl(String taxpayerCode, String taxpayerName);
    
}
