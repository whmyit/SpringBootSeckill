package com.dxhy.order.consumer.modules.order.controller;

import com.dxhy.order.constant.OrderInfoContentEnum;
import com.dxhy.order.constant.OrderManagementConstant;
import com.dxhy.order.consumer.annotation.SysLog;
import com.dxhy.order.consumer.modules.order.service.IOrderInfoService;
import com.dxhy.order.model.PageUtils;
import com.dxhy.order.model.R;
import com.dxhy.order.utils.NsrsbhUtils;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 商品统计控制层
 *
 * @author WRW-DXHY
 */
@Slf4j
@RestController
@Api(value = "业务类型信息", tags = {"订单模块"})
@RequestMapping(value = "/goods")
public class GoodsCountController {
    private static final String LOGGER_MSG = "(商品统计控制层)";
    
    
    @Resource
    private IOrderInfoService orderInfoService;
    
    
    /**
     * 业务类型统计
     * 在使用
     *
     * @param request
     * @return GoodsCountController.java
     * author wangruwei
     * 2019年7月5日
     */
    @ApiOperation(value = "业务类型统计", notes = "业务类型统计-业务类型统计")
    @PostMapping("/ywlxCount")
    @SysLog(operation = "业务类型统计", operationDesc = "业务类型统计", key = "业务类型统计")
    public R ywlxCount(HttpServletRequest request,
                       @ApiParam(name = "startDate", value = "开始日期", required = true) @RequestParam(name = "startDate", required = true) String startDate,
                       @ApiParam(name = "endDate", value = "结束日期", required = true) @RequestParam(name = "endDate", required = true) String endDate,
                       @ApiParam(name = "nsrsbh", value = "纳税人识别号", required = false) @RequestParam(name = "nsrsbh", required = false) String nsrsbh,
                       @ApiParam(name = "ywlxName", value = "业务类型名称", required = false) @RequestParam(name = "ywlxName", required = false) String ywlxName,
                       @ApiParam(name = "pageSize", value = "每页显示个数", required = true) @RequestParam(name = "pageSize", required = true) String pageSize,
                       @ApiParam(name = "currPage", value = "当前页数", required = true) @RequestParam(name = "currPage", required = true) String currPage
    ) {
        Map<String, Object> paramMap = new HashMap<>(10);
    
        if (StringUtils.isBlank(nsrsbh)) {
            log.error("{},请求税号为空!", LOGGER_MSG);
            return R.error(OrderInfoContentEnum.TAXCODE_ISNULL);
        }
    
        List<String> shList = NsrsbhUtils.transShListByXhfNsrsbh(nsrsbh);
    
        SimpleDateFormat sf = new SimpleDateFormat("yyyy-MM-dd");
        try {
            if (StringUtils.isBlank(startDate) && StringUtils.isBlank(endDate)) {
                return R.error(OrderInfoContentEnum.PARAM_NULL.getKey(), OrderInfoContentEnum.PARAM_NULL.getMessage());
            } else {
                Date starttime = sf.parse(startDate);
                Date endtime = sf.parse(endDate);
                if (starttime.after(endtime)) {
                    log.error("{}开始时间不能大于结束时间", LOGGER_MSG);
                    return R.error("开始时间不能大于结束时间");
                }
                paramMap.put("startDate", starttime);
                paramMap.put("endDate", endtime);
    
            }
        } catch (ParseException e) {
            log.error("{}时间转换异常", LOGGER_MSG);
            return R.error("时间转换异常");
        }
    
        if (StringUtils.isNotBlank(ywlxName)) {
            paramMap.put("ywlxName", ywlxName);
        }
    
        if (StringUtils.isNotBlank(pageSize)) {
            paramMap.put("pageSize", Integer.parseInt(pageSize));
        }
        if (StringUtils.isNotBlank(currPage)) {
            paramMap.put("currPage", Integer.parseInt(currPage));
        }
    
        PageUtils page = orderInfoService.selectYwlxCount(paramMap, shList);
        Map<String, String> countMap = orderInfoService.selectYwlxCountTotal(paramMap, shList);
        Map<String, Object> resultMap = new HashMap<>(10);
        resultMap.put("data", page);
        resultMap.put("countMap", countMap);
        return R.ok().put(OrderManagementConstant.DATA, resultMap);
    }
    
    
}
