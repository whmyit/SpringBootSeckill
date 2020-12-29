package com.dxhy.order.consumer.modules.scaninvoice.controller;

import com.alibaba.fastjson.JSON;
import com.dxhy.order.api.IValidateInterfaceOrder;
import com.dxhy.order.constant.ConfigureConstant;
import com.dxhy.order.constant.OrderInfoContentEnum;
import com.dxhy.order.constant.OrderInfoEnum;
import com.dxhy.order.constant.OrderManagementConstant;
import com.dxhy.order.consumer.annotation.SysLog;
import com.dxhy.order.consumer.modules.scaninvoice.model.PageQrcodeOrderInfo;
import com.dxhy.order.consumer.modules.scaninvoice.model.WxAuthNoDto;
import com.dxhy.order.consumer.modules.scaninvoice.service.ScanInvoiceService;
import com.dxhy.order.model.OrderInfo;
import com.dxhy.order.model.R;
import com.dxhy.order.utils.JsonUtils;
import com.dxhy.order.utils.NsrsbhUtils;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.dubbo.config.annotation.Reference;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.Map;

/**
 * 扫码开票控制层
 *
 * @author ZSC-DXHY
 * @date 创建时间: 2020-08-14 15:34
 */
@Slf4j
@RestController
@Api(value = "扫码开票", tags = {"订单模块"})
@RequestMapping(value = "/scanInvoice")
public class ScanInvoiceController {
    
    private static final String LOGGER_MSG = "(扫码开票控制层)";
    
    @Resource
    private ScanInvoiceService scanInvoiceService;
    
    @Reference
    private IValidateInterfaceOrder validateInterfaceOrder;
    
    
    @ApiOperation(value = "二维码信息查询", notes = "扫码开票-二维码信息查询")
    @PostMapping(value = "/getOrderInfoByTqm")
    @SysLog(operation = "二维码信息查询", operationDesc = "二维码信息查询", key = "扫码开票")
    public R getOrderInfoByTqm(
            @ApiParam(name = "nsrsbh", value = "税号", required = false) @RequestParam(value = "nsrsbh", required = false) String nsrsbh,
            @ApiParam(name = "tqm", value = "二维码信息", required = false) @RequestParam(value = "tqm", required = false) String tqm,
            @ApiParam(name = "type", value = "二维码类型", required = false) @RequestParam(value = "type", required = false) String type,
            @ApiParam(name = "openId", value = "二维码类型", required = false) @RequestParam(value = "openId", required = false) String openId) throws Exception {
        log.info("{}进入校验长码controller...nsrsbh:{},type:{},tqm：{}", LOGGER_MSG, nsrsbh, type, tqm);
        //根据提取码 税号 二维码类型 获取二维码数据
        if (StringUtils.isBlank(nsrsbh)) {
            log.error("{},请求税号为空!", LOGGER_MSG);
            return R.error(OrderInfoContentEnum.TAXCODE_ISNULL);
        }
    
        List<String> shList = NsrsbhUtils.transShListByXhfNsrsbh(nsrsbh);
        R queryOrderInfoByTqmAndNsrsbh = scanInvoiceService.queryOrderInfoByTqmAndNsrsbh(tqm, shList, type, openId);
        log.info("返回信息:{}", JsonUtils.getInstance().toJsonString(queryOrderInfoByTqmAndNsrsbh));
        return queryOrderInfoByTqmAndNsrsbh;
    }
    
    
    @ApiOperation(value = "获取二维码公众号的配置", notes = "扫码开票-获取二维码公众号的配置")
    @SysLog(operation = "获取二维码公众号的配置", operationDesc = "获取二维码公众号的配置", key = "扫码开票")
    @PostMapping(value = "/getEWmGzhConfig")
    public R getEwmGzhConfig(
            @ApiParam(name = "nsrsbh", value = "税号", required = false) @RequestParam(value = "nsrsbh", required = false) String nsrsbh,
            @ApiParam(name = "tqm", value = "二维码信息", required = false) @RequestParam(value = "tqm", required = false) String tqm,
            @ApiParam(name = "type", value = "二维码类型", required = false) @RequestParam(value = "type", required = false) String type) throws Exception {
        log.info("{}进入校验长码controller...nsrsbh:{},type:{},tqm：{}", LOGGER_MSG, nsrsbh, type, tqm);
        //根据提取码 税号 二维码类型 获取二维码数据
        R queryOrderInfoByTqmAndNsrsbh = scanInvoiceService.getEwmGzhConfig(tqm, nsrsbh, type);
        log.info("返回信息:{}", JsonUtils.getInstance().toJsonString(queryOrderInfoByTqmAndNsrsbh));
        return queryOrderInfoByTqmAndNsrsbh;
    }


    /**
     * 扫码流程第一次重构
     * @return
     */
    @ApiOperation(value = "获取授权url", notes = "扫码开票-获取授权url")
    @SysLog(operation = "获取授权url", operationDesc = "获取授权url", key = "扫码开票")
    @PostMapping("/getAuthUrl")
    public R getAuthUrl(
            @ApiParam(name = "orderList", value = "orderList", required = true) @RequestBody PageQrcodeOrderInfo pageQrcodeOrderInfo) {

        return scanInvoiceService.getAuthUrlAndUpdateOrderInfo(pageQrcodeOrderInfo);
    }
    

    @RequestMapping("/receiveWechatAuthEvent")
    @ApiOperation(value = "接收微信授权事件推送", notes = "扫码开票-接收微信授权事件推")
    public R receiveWechatAuthEvent(HttpServletRequest request, HttpServletResponse response) throws IOException, IllegalAccessException, InstantiationException, InvocationTargetException {
        //用户授权完成后 调用开票接口开票
        WxAuthNoDto readRequest = readRequest(request, WxAuthNoDto.class);
        
        log.info("接受微信授权推送,入参:{}", JsonUtils.getInstance().toJsonString(readRequest));
        R r = new R();
        if (StringUtils.isNotBlank(readRequest.getSuccOrderId())) {
            //授权成功的订单去开票
            r = scanInvoiceService.authOrderInvoice(readRequest.getSuccOrderId());
        }
        if (StringUtils.isNotBlank(readRequest.getFailOrderId())) {
            //授权失败的订单更新开票状态
            r = scanInvoiceService.updateFaildOrder(readRequest.getFailOrderId());
        }
        return r;
    }


    @ApiOperation(value = "获取需要合并的二维码信息", notes = "获取需要合并的二维码信息")
    @SysLog(operation = "获取需要合并的二维码信息", operationDesc = "获取需要合并的二维码信息", key = "扫码开票")
    @PostMapping("/getMergeOrderByShortUrl")
    public R getMergeOrderByShortUrl(
            @ApiParam(name = "shortUrl", value = "二维码中的短码地址", required = false) @RequestParam(value = "shortUrl", required = true) String shortUrl,
            @ApiParam(name = "xhfNsrsbh", value = "销方税号", required = false) @RequestParam(value = "xhfNsrsbh", required = true) String xhfNsrsbh) {

        if(shortUrl.indexOf("?tqm=") < 0 || shortUrl.indexOf("&") < 0){
            log.warn("{}二维码格式不正确，xhfNsrsbh:{}",LOGGER_MSG,xhfNsrsbh);
            return R.error().put(OrderManagementConstant.MESSAGE,"二维码格式不正确");
        }

        String tqm = shortUrl.substring(shortUrl.indexOf("?tqm=") + 5 ,shortUrl.indexOf("&"));
        if(StringUtils.isBlank(tqm)){
            log.warn("{}二维码格式不正确，xhfNsrsbh:{}",LOGGER_MSG,xhfNsrsbh);
            return R.error().put(OrderManagementConstant.MESSAGE,"二维码格式不正确");
        }
        List<String> shList = new ArrayList<String>();
        shList.add(xhfNsrsbh);

        R r = scanInvoiceService.queryOrderInfoByTqm(tqm,shList);

        return r;
    }



    @ApiOperation(value = "合并订单信息并获取授权url", notes = "合并订单信息并获取授权url")
    @SysLog(operation = "合并订单信息并获取授权url", operationDesc = "合并订单信息并获取授权url", key = "扫码开票")
    @PostMapping("/getMergeOrderAuthUrl")
    public R getMergeOrderAuthUrl(
            @ApiParam(name = "fpqqlshs", value = "发票请求流水号数组", required = false) @RequestParam(value = "fpqqlshs", required = true) String fpqqlshs,
            @ApiParam(name = "xhfNsrsbh", value = "销方税号", required = false) @RequestParam(value = "xhfNsrsbh", required = true) String xhfNsrsbh){

        List<String> lshList = JSON.parseArray(fpqqlshs,String.class);

        List<String> shList = new ArrayList<>();
        shList.add(xhfNsrsbh);

        R r = scanInvoiceService.getMergeOrderAuthUrl(lshList,shList);
        return r;
    }
    
    
    public static <T> T readRequest(HttpServletRequest request, Class<T> clazz) throws IOException, IllegalAccessException, InstantiationException, InvocationTargetException {
        StringBuilder sb = new StringBuilder();
        InputStreamReader isr = new InputStreamReader(request.getInputStream());
        BufferedReader br = new BufferedReader(isr);
        String s = "";
        while ((s = br.readLine()) != null) {
            sb.append(s);
        }
        if (StringUtils.isNotEmpty(sb.toString())) {
            return JsonUtils.getInstance().fromJson(sb.toString(), clazz);
        } else {
            Enumeration e = request.getParameterNames();
            String tmp = (String) e.nextElement();
            return JsonUtils.getInstance().fromJson(tmp, clazz);
        }
    }

    /**
     * 购方信息校验
     *
     * @param pageQrcodeOrderInfo
     * @return
     */
    private R checkQrcodeGmfXx(PageQrcodeOrderInfo pageQrcodeOrderInfo) {
        //购方信息转换
        OrderInfo orderInfo = new OrderInfo();
        orderInfo.setGhfZh(pageQrcodeOrderInfo.getGhfZh());
        orderInfo.setGhfYh(pageQrcodeOrderInfo.getGhfYh());
        orderInfo.setGhfSj(pageQrcodeOrderInfo.getGhfSj());
        orderInfo.setGhfDh(pageQrcodeOrderInfo.getGhfDh());
        orderInfo.setGhfDz(pageQrcodeOrderInfo.getGhfDz());
        orderInfo.setGhfEmail(pageQrcodeOrderInfo.getGhfEmail());
        orderInfo.setGhfMc(pageQrcodeOrderInfo.getGhfMc());
        orderInfo.setGhfNsrsbh(pageQrcodeOrderInfo.getGhfNsrsbh());
        orderInfo.setGhfQylx(pageQrcodeOrderInfo.getGhfqylx());
        
        Map<String, String> resultMap = validateInterfaceOrder.checkGhfInfo(orderInfo, OrderInfoEnum.ORDER_REQUEST_TYPE_0.getKey());
        
        if (!ConfigureConstant.STRING_0000.equals(resultMap.get(OrderManagementConstant.ERRORCODE))) {
            log.error("{}购货方数据非空和长度校验未通过，未通过数据:{}", LOGGER_MSG, resultMap);
            return R.error().put(OrderManagementConstant.CODE, resultMap.get(OrderManagementConstant.ERRORCODE))
                    .put(OrderManagementConstant.MESSAGE, resultMap.get(OrderManagementConstant.ERRORMESSAGE));
        }
        return R.ok();
    }
    
}
