package com.dxhy.order.utils;

import com.dxhy.order.constant.ConfigureConstant;
import com.dxhy.order.constant.OrderInfoContentEnum;
import com.dxhy.order.constant.OrderManagementConstant;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Map;


/**
 * 非空长度校验工具类
 *
 * @author ZSC-DXHY
 */
@Slf4j
public class CheckParamUtil {
    
    private static final String LOGGER_MSG = "(非空长度校验)";
    
    /**
     * 校验包含税控设备code的数据
     * 根据税控设备类型判断枚举值中的长度
     *
     * @param content
     * @param param
     * @param terminalCode
     * @return
     */
    public static Map<String, String> checkParam(OrderInfoContentEnum content, String param, String terminalCode) {
        return checkParam(content, param, terminalCode, -1);
    }
    
    /**
     * 校验默认的长度
     *
     * @param content
     * @param param
     * @return
     */
    public static Map<String, String> checkParam(OrderInfoContentEnum content, String param) {
        return checkParam(content, param, -1);
    }
    
    /**
     * 校验默认的长度返回明细行书
     *
     * @param content
     * @param param
     * @param num
     * @return
     */
    public static Map<String, String> checkParam(OrderInfoContentEnum content, String param, int num) {
        
        return checkParam(content, param, "", num);
    }
    
    /**
     * 最原子方法,校验税控设备长度+返回明细
     *
     * @param content
     * @param param
     * @param terminalCode
     * @param num
     * @return
     */
    public static Map<String, String> checkParam(OrderInfoContentEnum content, String param, String terminalCode, int num) {
        
        String replyMsg = "";
        String numMsg = "";
        if (ConfigureConstant.INT_1_ != num) {
            numMsg = "第" + (num + 1) + "行,";
        }
        Map<String, String> map = new HashMap<>(5);
        map.put(OrderManagementConstant.ERRORCODE, OrderInfoContentEnum.SUCCESS.getKey());
        //校验是否为空
        if (content.getCheckNull()) {
            if (StringUtils.isBlank(param)) {
                replyMsg = numMsg + content.getMessage() + "不能为空!";
                map.put(OrderManagementConstant.ERRORCODE, content.getKey());
                map.put(OrderManagementConstant.ERRORMESSAGE, replyMsg);
            }
        }
        
        //校验长度
        int strLength = 0;
        if (StringUtils.isNotBlank(param) && content.getCheckLength()) {
            try {
                strLength = param.getBytes(ConfigureConstant.STRING_CHARSET_GBK).length;
            } catch (UnsupportedEncodingException e) {
                replyMsg = numMsg + content.getMessage() + "获取长度异常!";
                map.put(OrderManagementConstant.ERRORCODE, content.getKey());
                map.put(OrderManagementConstant.ERRORMESSAGE, replyMsg);
            }
            if (content.getMinLength() != 0) {
                if (content.getMaxLength() == content.getMinLength() && (strLength != content.getMinLength())) {
                    replyMsg = numMsg + content.getMessage() + "数据不合法,长度应等于" + content.getMinLength() + "!";
                    map.put(OrderManagementConstant.ERRORCODE, content.getKey());
                    map.put(OrderManagementConstant.ERRORMESSAGE, replyMsg);
                } else if (strLength > content.getMaxLength() || strLength < content.getMinLength()) {
                    replyMsg = numMsg + content.getMessage() + "数据不合法,长度应大于" + content.getMinLength() + "小于" + content.getMaxLength() + "!";
                    map.put(OrderManagementConstant.ERRORCODE, content.getKey());
                    map.put(OrderManagementConstant.ERRORMESSAGE, replyMsg);
                }
            } else {
                /**
                 * 根据税控设备判断字段取值
                 */
                int maxLength = content.getMaxLength();
                boolean result = StringUtils.isNotBlank(terminalCode) && (ConfigureConstant.STRING_009_.equals(terminalCode) || (ConfigureConstant.STRING_010_.equals(terminalCode)));
                if (result) {
                    maxLength = content.getMaxLengthNewTax();
                }
                if (strLength > maxLength) {
                    replyMsg = numMsg + content.getMessage() + "数据不合法,长度应小于" + maxLength + "!";
                    map.put(OrderManagementConstant.ERRORCODE, content.getKey());
                    map.put(OrderManagementConstant.ERRORMESSAGE, replyMsg);
                }
            }
            
        }
        
        //特殊字符校验
//        if (!GBKUtil.matchesX(param)) {
//            reply_msg = content.getMessage() + "数据不合法,存在特殊字符!";
//            map.put(OrderManaementConstant.ERRORCODE, content.getKey());
//            map.put(OrderManaementConstant.ERRORMESSAGE, reply_msg);
//        }
        if (!OrderInfoContentEnum.SUCCESS.getKey().equals(map.get(OrderManagementConstant.ERRORCODE))) {
        
        
            log.error("{}数据:{},失败code为:{},失败原因为:{}", LOGGER_MSG, param, map.get(OrderManagementConstant.ERRORCODE), map.get(OrderManagementConstant.ERRORMESSAGE));
        }
        return map;
    }
    
}
