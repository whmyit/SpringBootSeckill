package com.dxhy.order.consumer.utils;


import com.dxhy.order.constant.OrderInfoContentEnum;
import com.dxhy.order.model.R;
import com.dxhy.order.utils.JsonUtils;
import lombok.extern.slf4j.Slf4j;

/**
 * @author ：杨士勇
 * @ClassName ：InterfaceResponseUtils
 * @Description ：接口返回工具类
 * @date ：2018年7月21日 下午4:55:00
 */
@Slf4j
public class InterfaceResponseUtils {
    
    private static final String LOGGER_MSG = "(组建返回信息工具类)";
    
    
    /**
     * 数据返回
     *
     * @param responseEnum
     * @param data
     * @return
     */
    public static R buildReturnInfo(OrderInfoContentEnum responseEnum, Object data) {
        log.info("{}接口返回信息：code:{},message:{},data:{}", LOGGER_MSG, responseEnum.getKey(), responseEnum.getMessage(),
                JsonUtils.getInstance().toJsonString(data));
        return R.setCodeAndMsg(responseEnum, data);
    }
    
    /**
     * 返回错误信息
     *
     * @param responseEnum
     * @return
     */
    public static R buildErrorInfo(OrderInfoContentEnum responseEnum) {
        log.error("{}接口返回信息：code:{},message:{}", LOGGER_MSG, responseEnum.getKey(), responseEnum.getMessage());
        return R.error(responseEnum);
    }


}
