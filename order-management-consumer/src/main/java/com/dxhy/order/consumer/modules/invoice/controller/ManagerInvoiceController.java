package com.dxhy.order.consumer.modules.invoice.controller;

import com.dxhy.order.api.ApiInvoiceService;
import com.dxhy.order.api.ApiTaxEquipmentService;
import com.dxhy.order.constant.OrderInfoContentEnum;
import com.dxhy.order.consumer.annotation.SysLog;
import com.dxhy.order.consumer.modules.fiscal.service.UnifyService;
import com.dxhy.order.consumer.modules.user.service.UserInfoService;
import com.dxhy.order.model.R;
import com.dxhy.order.model.entity.DrawerInfoEntity;
import com.dxhy.order.model.entity.InvoiceQuotaEntity;
import com.dxhy.order.utils.JsonUtils;
import com.dxhy.order.utils.NsrsbhUtils;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.dubbo.config.annotation.Reference;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.List;

/**
 * 基础信息控制层
 *
 * @author ZSC-DXHY
 * @date 创建时间: 2020-08-14 18:16
 */
@Slf4j
@RequestMapping("/invoice")
@Api(value = "基础信息", tags = {"发票模块"})
@RestController
public class ManagerInvoiceController {
    
    private static final String LOGGER_MSG = "(基础信息)";
    
    @Reference
    private ApiInvoiceService invoiceService;
    
    @Reference
    private ApiTaxEquipmentService apiTaxEquipmentService;
    
    @Resource
    private UnifyService unifyService;
    
    @Resource
    private UserInfoService userInfoService;
    
    /**
     * 在使用,基础设置-基础信息
     * 查询初始化开票人信息和发票限额
     *
     * @return
     */
    @PostMapping("/queryInvoice")
    @ApiOperation(value = "基础信息查询开票人", notes = "基础信息-基础信息查询开票人")
    @SysLog(operation = "基础信息查询开票人", operationDesc = "基础信息查询开票人", key = "基础信息")
    public R queryInvoice(String xhfNsrsbh) {
        log.info("查询初始化开票人信息和发票限额参数{} ", xhfNsrsbh);
        if (StringUtils.isBlank(xhfNsrsbh)) {
            return R.error(OrderInfoContentEnum.TAXCODE_ISNULL);
        }
        String[] xfshs = JsonUtils.getInstance().fromJson(xhfNsrsbh, String[].class);
        
        xfshs = NsrsbhUtils.getNsrsbhList(xfshs);
        //查询开票设置
        DrawerInfoEntity drawer = invoiceService.queryDrawerInfo(xfshs[0], String.valueOf(userInfoService.getUser().getUserId()));
        log.info("查询初始化开票人信息返回值 drawer:{}", JsonUtils.getInstance().toJsonString(drawer));
        return R.ok().put("drawerList", drawer);
    }
    
    /**
     * 在使用
     * 基础设置-基础信息
     * 保存开票人信息
     *
     * @param drawerInfoEntity
     * @return DrawerInfoEntity
     */
    @PostMapping("/saveDrawer")
    @ApiOperation(value = "保存开票人", notes = "基础信息-保存开票人")
    @SysLog(operation = "基础信息保存开票人", operationDesc = "基础信息保存开票人", key = "基础信息")
    public R saveDrawer(DrawerInfoEntity drawerInfoEntity) {
        log.info("保存开票人信息入参 {}", JsonUtils.getInstance().toJsonString(drawerInfoEntity));
    
        if (drawerInfoEntity == null) {
            return R.error(OrderInfoContentEnum.GENERATE_READY_ORDER_DATA_ERROR);
        }
        if (StringUtils.isBlank(drawerInfoEntity.getXhfNsrsbh())) {
            log.error("{},请求税号为空!", LOGGER_MSG);
            return R.error(OrderInfoContentEnum.TAXCODE_ISNULL);
        }
    
        List<String> shList = NsrsbhUtils.transShListByXhfNsrsbh(drawerInfoEntity.getXhfNsrsbh());
    
        String userId = userInfoService.getUser().getUserId().toString();
        drawerInfoEntity.setModifyUserId(userId);
        drawerInfoEntity.setCredateUserId(userId);
        drawerInfoEntity.setXhfNsrsbh(shList.get(0));
        log.info("参数 {} ", drawerInfoEntity);
        R r = invoiceService.saveDrawer(drawerInfoEntity, shList);
        return r;
    }
    
    /**
     * 在使用 todo 是否重复
     *
     * @Description 查询开票人维护信息
     * @Author xieyuanqiang
     * @Date 10:13 2018-07-21
     */
    @PostMapping("/queryDrawerInfo")
    @ApiOperation(value = "基础信息查询开票人", notes = "基础信息-基础信息查询开票人")
    @SysLog(operation = "基础信息查询开票人", operationDesc = "基础信息查询开票人", key = "基础信息")
    public R queryDrawerInfo(@RequestParam String xhfNsrsbh, @RequestParam String userId) {
        if (StringUtils.isBlank(xhfNsrsbh) || StringUtils.isBlank(userId)) {
            return R.error(OrderInfoContentEnum.TAXCODE_ISNULL);
        }
        String[] list = JsonUtils.getInstance().fromJson(xhfNsrsbh, String[].class);
        log.info("查询开票人维护信息 参数 纳税人识别号 {},当前登录人{}", xhfNsrsbh, userId);
        userId = userId == null ? String.valueOf(userInfoService.getUser().getUserId()) : userId;
        DrawerInfoEntity drawerInfoEntity = invoiceService.queryDrawerInfo(list[0], userId);
        return R.ok().put("drawerInfoEntity", drawerInfoEntity);
    }
    
    /**
     * 在使用
     *
     * @Description 查询发票限额
     * @Author xieyuanqiang
     * @Date 10:13 2018-07-21
     */
    @PostMapping("/queryInvoiceQuotaInfo")
    @ApiOperation(value = "查询限额", notes = "基础信息-查询限额")
    @SysLog(operation = "基础信息查询限额", operationDesc = "基础信息查询限额", key = "基础信息")
    public R queryInvoiceQuotaInfo(@RequestParam String xhfNsrsbh, @RequestParam String invoiceType) {
        log.info("查询开票人维护信息 参数 纳税人识别号 {},发票类型{}", xhfNsrsbh, invoiceType);
        
        if (StringUtils.isBlank(xhfNsrsbh)) {
            return R.error(OrderInfoContentEnum.TAXCODE_ISNULL);
        }
        String[] xfshs = JsonUtils.getInstance().fromJson(xhfNsrsbh, String[].class);
        if (xfshs.length > 1) {
            log.error("{}当前操作不支持多税号进行操作.请求参数:{}", "", JsonUtils.getInstance().toJsonString(xfshs));
            return R.error(OrderInfoContentEnum.MORE_NSRSBH_ERROR);
        }
        String nsrsbh = xfshs[0];
        
        String terminalCode = apiTaxEquipmentService.getTerminalCode(nsrsbh);
        InvoiceQuotaEntity invoiceQuotaEntity = unifyService.queryInvoiceQuotaInfoFromRedis(xfshs[0], invoiceType, terminalCode);
        
        return R.ok().put("invoiceQuota", invoiceQuotaEntity.getInvoiceAmount());
    }
    
}
