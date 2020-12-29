//package com.dxhy.order.consumer.modules.fzyy.service.impl;
//
//import cn.hutool.core.util.ObjectUtil;
//import com.alibaba.fastjson.JSON;
//import com.dxhy.order.constant.ConfigureConstant;
//import com.dxhy.order.constant.OrderManagementConstant;
//import com.dxhy.order.consumer.config.OpenApiConfig;
//import com.dxhy.order.consumer.modules.fzyy.service.IFzyyService;
//import com.dxhy.order.consumer.protocol.usercenter.DeptEntity;
//import com.dxhy.order.exceptions.OrderReceiveException;
//import com.dxhy.order.model.R;
//import com.dxhy.order.utils.JsonUtils;
//import com.google.common.collect.Lists;
//import lombok.extern.slf4j.Slf4j;
//import org.apache.commons.lang3.StringUtils;
//import org.springframework.http.HttpEntity;
//import org.springframework.http.HttpHeaders;
//import org.springframework.http.HttpMethod;
//import org.springframework.http.ResponseEntity;
//import org.springframework.stereotype.Service;
//import org.springframework.web.client.RestTemplate;
//
//import javax.annotation.Resource;
//import java.util.ArrayList;
//import java.util.List;
//import java.util.Map;
//
///**
// * 辅助运营用户已开通销项信息获取
// *
// * @author ZSC-DXHY
// * @date 创建时间: 2020-08-17 16:36
// */
//@Service
//@Slf4j
//public class FzyyServiceImpl implements IFzyyService {
//
//    private final static String LOGGER_MSG = "(辅助运营用户已开通销项信息获取)";
//
//    @Resource
//    private RestTemplate eurekaRestTemplate;
//
//    /**
//     * 仅适用于辅助运营获取用户信息
//     *
//     * @return
//     * @throws OrderReceiveException
//     */
//    @Override
//    public List<DeptEntity> getFzyyTaxpayerEntityListByEureka(String currentPage,String pageSize,String xhfMc) throws OrderReceiveException {
//        List<DeptEntity> texCodeList = new ArrayList<>();
//
//        int curr = StringUtils.isBlank(currentPage) ? 1 : Integer.valueOf(currentPage);
//        int size = StringUtils.isBlank(pageSize) ? 100 : Integer.valueOf(pageSize);
//
//        List<DeptEntity> taxplayercodeDeptList = getTotalTaxListByEureka(eurekaRestTemplate, Lists.newArrayList(), size, curr, true, null);
//
//        for (DeptEntity sysDeptEntity : taxplayercodeDeptList) {
//            if (StringUtils.isNotBlank(sysDeptEntity.getTaxpayerCode())) {
//                texCodeList.add(sysDeptEntity);
//            }
//        }
//        return texCodeList;
//    }
//
//
//    public static List<DeptEntity> getTotalTaxListByEureka(RestTemplate eurekaRestTemplate, List<DeptEntity> deptEntityList, int pageSize, int currPage, boolean getAll, String token) throws OrderReceiveException {
//        if (ObjectUtil.isNull(deptEntityList)) {
//            deptEntityList = Lists.newArrayList();
//        }
//        HttpHeaders headers = new HttpHeaders();
//        headers.add("distributorId", "0");
//        headers.add("Accept", "application/json");
//
//        long start = System.currentTimeMillis();
//        String url = String.format(OpenApiConfig.queryFzyyTaxpayerListByEureka, OpenApiConfig.systemProductId, currPage, pageSize);
//        log.debug("{}调用辅助运营用户信息接口，开始时间:{},URL为:{},入参:{}", LOGGER_MSG, start, OpenApiConfig.queryFzyyTaxpayerListByEureka, JsonUtils.getInstance().toJsonString(headers));
//        ResponseEntity<String> resEntity = eurekaRestTemplate.exchange(url, HttpMethod.GET, new HttpEntity<>(headers), String.class);
//        long end = System.currentTimeMillis();
//        String result = resEntity.getBody();
//        log.debug("{}调用辅助运营用户信息接口为,结束时间:{},耗时(毫秒）:{},出参:{}", LOGGER_MSG, end, end - start, resEntity);
//        if (StringUtils.isNotBlank(result)) {
//            R r = JsonUtils.getInstance().parseObject(result, R.class);
//
//            if (ConfigureConstant.STRING_0000.equals(r.get(OrderManagementConstant.CODE))) {
//                R data = JsonUtils.getInstance().parseObject(r.get(OrderManagementConstant.DATA).toString(), R.class);
//                int count = Integer.parseInt(data.get("pages").toString());
//                if (data.get("records") != null) {
//                    List<Map> records = JSON.parseArray(data.get("records").toString(), Map.class);
//                    if (records != null && records.size() > 0) {
//                        for (Map record : records) {
//                            DeptEntity deptEntity = new DeptEntity();
//                            deptEntity.setTaxpayerCode(ObjectUtil.isNull(record.get("taxNo")) ? "" : record.get("taxNo").toString());
//                            deptEntity.setName(ObjectUtil.isNull(record.get("companyName")) ? "" : record.get("companyName").toString());
//                            if (StringUtils.isBlank(deptEntity.getTaxpayerCode())) {
//                                continue;
//                            }
//                            deptEntityList.add(deptEntity);
//                        }
//
//                    }
//                }
//                if (getAll && currPage <= count) {
//
//
//                    getTotalTaxListByEureka(eurekaRestTemplate, deptEntityList, pageSize, currPage + 1, true, token);
//                }
//
//            } else {
//                throw new OrderReceiveException(String.valueOf(r.get(OrderManagementConstant.CODE)), String.valueOf(r.get(OrderManagementConstant.ALL_MESSAGE)));
//            }
//        }
//        return deptEntityList;
//    }
//}
