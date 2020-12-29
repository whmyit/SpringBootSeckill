package com.dxhy.order.consumer.modules.fiscal.service.acitvex.impl;

import com.alibaba.fastjson.JSONObject;
import com.dxhy.order.constant.OrderInfoContentEnum;
import com.dxhy.order.constant.OrderManagementConstant;
import com.dxhy.order.consumer.config.OpenApiConfig;
import com.dxhy.order.consumer.modules.fiscal.service.acitvex.PrintInfoService;
import com.dxhy.order.model.R;
import com.dxhy.order.utils.HttpUtils;
import com.dxhy.order.utils.JsonUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;

/**
 * @Description 查询打印机测试service
 * @Author xueanna
 * @Date 2019/8/21 13:37
 */
@Service
@Slf4j
public class PrintInfoServiceImpl implements PrintInfoService {
    /**
    * 查询打印机信息
    */
    @Override
    public R selectDyjInfo() {
        log.info("查询打印机信息");
        String post = HttpUtils.doPost(OpenApiConfig.queryDyjInfo, "");
        log.info("查询打印机信息,出参：{}",post);
        JSONObject object = JsonUtils.getInstance().parseObject(post, JSONObject.class);
        if(!ObjectUtils.isEmpty(object)){
            if (OrderInfoContentEnum.SUCCESS.getKey().equals(object.get(OrderManagementConstant.CODE))) {
                return R.ok().put("data", object.get("content"));
            } else {
                return R.error(object.getString("msg"));
            }
        }
        return R.error("调用底层返回空");
    }
}
