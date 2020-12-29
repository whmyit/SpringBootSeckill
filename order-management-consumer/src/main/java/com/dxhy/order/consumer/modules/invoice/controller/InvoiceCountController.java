package com.dxhy.order.consumer.modules.invoice.controller;

import cn.hutool.core.util.ObjectUtil;
import com.dxhy.order.api.ApiFangGeInterfaceService;
import com.dxhy.order.api.ApiTaxEquipmentService;
import com.dxhy.order.constant.ConfigureConstant;
import com.dxhy.order.constant.OrderInfoContentEnum;
import com.dxhy.order.constant.OrderInfoEnum;
import com.dxhy.order.constant.OrderManagementConstant;
import com.dxhy.order.consumer.annotation.SysLog;
import com.dxhy.order.consumer.model.CountSurplusOwn;
import com.dxhy.order.consumer.modules.fiscal.service.UnifyService;
import com.dxhy.order.consumer.modules.invoice.service.InvoiceCountService;
import com.dxhy.order.model.PageUtils;
import com.dxhy.order.model.R;
import com.dxhy.order.model.TaxEquipmentInfo;
import com.dxhy.order.model.a9.sld.SearchFjh;
import com.dxhy.order.model.vo.*;
import com.dxhy.order.utils.DateUtilsLocal;
import com.dxhy.order.utils.JsonUtils;
import com.dxhy.order.utils.NsrsbhUtils;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.dubbo.config.annotation.Reference;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.text.ParseException;
import java.util.*;

/**
 * @Author fankunfeng
 * @Date 2019-04-12 09:59:04
 * @Describe
 */
@Api(value = "发票统计", tags = {"发票模块"})
@RestController
@RequestMapping("/invoiceCount")
@Slf4j
public class InvoiceCountController {
    
    private final static String LOGGER_MSG = "(发票统计)";
    
    @Resource
    private InvoiceCountService invoiceCountService;
    
    @Reference
    ApiTaxEquipmentService apiTaxEquipmentService;
    
    @Reference
    private ApiFangGeInterfaceService apiFangGeInterfaceService;
    
    @Resource
    private UnifyService unifyService;
    
    /**
     * 大B 使用
     *
     * @param countToBVO
     * @return
     */
    @ApiOperation(value = "统计合计税额", notes = "发票统计-统计合计税额")
    @PostMapping(value = "/countHjse")
    public R toItaxHjseCount(@RequestBody CountToB countToBVO) {
        log.debug("统计开票税额入参：{}", JsonUtils.getInstance().toJsonString(countToBVO));
        R volid = volid(countToBVO);
        if (volid != null) {
            return volid;
        }
        List<String> list = new ArrayList<>();
        list.add(countToBVO.getTaxpayerCode());
        List<CountHjseVO> sixMonthOfInvoiceHjse = invoiceCountService.getSixMonthOfInvoiceHjse(list);
        if (sixMonthOfInvoiceHjse == null || sixMonthOfInvoiceHjse.size() == 0) {
            log.error("未查到合计金额相关数据！");
            R r = new R(false);
            r.put(OrderManagementConstant.ALL_MESSAGE, OrderInfoContentEnum.INVOICE_COUNT_NULL.getMessage());
            return r;
        }
        String count = ConfigureConstant.STRING_0;
        for (CountHjseVO countHjseVO : sixMonthOfInvoiceHjse) {
            //补全
            if (StringUtils.isBlank(countHjseVO.getHjse())) {
                countHjseVO.setHjse(ConfigureConstant.STRING_0);
            }
            if (StringUtils.isBlank(countHjseVO.getHzse())) {
                countHjseVO.setHzse(ConfigureConstant.STRING_0);
            }
            if (StringUtils.isBlank(countHjseVO.getLzse())) {
                countHjseVO.setLzse(ConfigureConstant.STRING_0);
            }
            if (DateUtilsLocal.getZMonthAgeMore(0).equals(countHjseVO.getTime())) {
                count = countHjseVO.getHjse();
            }
        }
        R r = new R(true);
        r.put(ConfigureConstant.TAB_CODE, "");
        //统计最近一个月的开票量
        r.put(OrderManagementConstant.DATA, count);
        r.put(ConfigureConstant.STRING_CONTENT, sixMonthOfInvoiceHjse);
        return r;
    }
    
    /**
     * 大B 使用
     *
     * @param countToBVO
     * @return
     */
    @ApiOperation(value = "统计合计金额", notes = "发票统计-统计合计金额")
    @PostMapping(value = "/countHjje")
    public R toItaxHjjeCount(@RequestBody CountToB countToBVO) {
        log.debug("统计开票金额入参：{}", JsonUtils.getInstance().toJsonString(countToBVO));
        //校验
        R volid = volid(countToBVO);
        if (volid != null) {
            return volid;
        }
        List<String> list = new ArrayList<>();
        list.add(countToBVO.getTaxpayerCode());
        List<CountHjjeVO> sixMonthOfInvoiceHjje = invoiceCountService.getSixMonthOfInvoiceHjje(list);
        if (sixMonthOfInvoiceHjje == null || sixMonthOfInvoiceHjje.size() == 0) {
            log.error("未查到合计税额相关数据！");
            R r = new R(false);
            r.put(OrderManagementConstant.ALL_MESSAGE, OrderInfoContentEnum.INVOICE_COUNT_NULL.getMessage());
            return r;
        }
        String count = ConfigureConstant.STRING_0;
        for (CountHjjeVO countHjjeVO : sixMonthOfInvoiceHjje) {
            if (StringUtils.isBlank(countHjjeVO.getHjje())) {
                countHjjeVO.setHjje(ConfigureConstant.STRING_0);
            }
            if (StringUtils.isBlank(countHjjeVO.getHzje())) {
                countHjjeVO.setHzje(ConfigureConstant.STRING_0);
            }
            if (StringUtils.isBlank(countHjjeVO.getLzje())) {
                countHjjeVO.setLzje(ConfigureConstant.STRING_0);
            }
            if (DateUtilsLocal.getZMonthAgeMore(0).equals(countHjjeVO.getTime())) {
                count = countHjjeVO.getHjje();
            }
        }
        R r = new R(true);
        r.put(ConfigureConstant.TAB_CODE, "");
        //统计最近一个月的开票量
        r.put(OrderManagementConstant.DATA, count);
        r.put(ConfigureConstant.STRING_CONTENT, sixMonthOfInvoiceHjje);
        log.info("返回信息：{}", JsonUtils.getInstance().toJsonString(r));
        return r;
    }
    
    private R volid(CountToB countToB) {
        if (countToB == null) {
            log.error("输入参数有错！");
            return new R(false).put(OrderManagementConstant.ALL_MESSAGE, OrderInfoContentEnum.INVOICE_NSRMC_NULL.getMessage());
        }
        if (StringUtils.isBlank(countToB.getTaxpayerCode())) {
            log.error("输入纳税人识别号有错！");
            return new R(false).put(OrderManagementConstant.ALL_MESSAGE, OrderInfoContentEnum.INVOICE_NSRMC_NULL.getMessage());
        }
        return null;
    }
    
    private R volidDays(CountDaysToB countDaysToB) {
        if (countDaysToB == null) {
            log.error("输入参数有错！");
            return new R(false).put(OrderManagementConstant.ALL_MESSAGE, OrderInfoContentEnum.INVOICE_NSRMC_NULL.getMessage());
        }
        if (StringUtils.isBlank(countDaysToB.getTaxpayerCode())) {
            log.error("输入纳税人识别号有错！");
            return new R(false).put(OrderManagementConstant.ALL_MESSAGE, OrderInfoContentEnum.INVOICE_NSRMC_NULL.getMessage());
        }
        if (StringUtils.isBlank(countDaysToB.getStartTime())) {
            log.error("开始时间不能为空!");
            return new R(false).put(OrderManagementConstant.ALL_MESSAGE, OrderInfoContentEnum.INVOICE_PARAM_ERROR.getMessage());
        }
        if (StringUtils.isBlank(countDaysToB.getEndTime())) {
            log.error("结束时间不能为空!");
            return new R(false).put(OrderManagementConstant.ALL_MESSAGE, OrderInfoContentEnum.INVOICE_PARAM_ERROR.getMessage());
        }
        return null;
    }
    
    /**
     * 大B 使用
     *
     * @param countToBVO
     * @return
     */
    @ApiOperation(value = "统计开票总金额", notes = "发票统计-统计开票总金额")
    @PostMapping(value = "/countAll")
    public R toItaxCount(@RequestBody CountToB countToBVO) {
        log.debug("入参：{}", JsonUtils.getInstance().toJsonString(countToBVO));
        R volid = volid(countToBVO);
        if (volid != null) {
            return volid;
        }
        List<String> list = new ArrayList<>();
        list.add(countToBVO.getTaxpayerCode());
        List<InvoiceCountByTimeVO> sixMonthOfInvoice = invoiceCountService.getSixMonthOfInvoice(list);
        if (sixMonthOfInvoice == null || sixMonthOfInvoice.size() == 0) {
            log.error("未查到统计相关数据！");
            R r = new R(false);
            r.put(OrderManagementConstant.ALL_MESSAGE, OrderInfoContentEnum.INVOICE_COUNT_NULL.getMessage());
            return r;
        }
        log.info("统计结果：{}", JsonUtils.getInstance().toJsonString(sixMonthOfInvoice));
        String count = ConfigureConstant.STRING_0;
        for (InvoiceCountByTimeVO invoiceCountByTimeVO : sixMonthOfInvoice) {
            if (DateUtilsLocal.getZMonthAgeMore(0).equals(invoiceCountByTimeVO.getTime())) {
                count = invoiceCountByTimeVO.getCount();
            }
        }
        
        R r = new R(true);
        r.put(ConfigureConstant.TAB_CODE, "");
        //统计最近一个月的开票量
        r.put(OrderManagementConstant.DATA, count);
        r.put(ConfigureConstant.STRING_CONTENT, sixMonthOfInvoice);
        log.info("返回信息：{}", JsonUtils.getInstance().toJsonString(r));
        return r;
    }
    
    /**
     * 产品 使用
     *
     * @param countSurplusOwn 前端协议参数
     * @return
     */
    @ApiOperation(value = "统计开票余量-产品", notes = "发票统计-统计开票余量")
    @PostMapping(value = "/invoiceSurplus")
    @SysLog(operation = "开票余量统计rest接口", operationDesc = "提供产品使用,统计企业开票余量", key = "票量统计")
    public R countInvoiceOwn(@RequestBody CountSurplusOwn countSurplusOwn) {
        @NonNull String xhfNsrsbh = countSurplusOwn.getXhfNsrsbh();
        String[] xfshs = new String[0];
        try {
            xfshs = JsonUtils.getInstance().fromJson(xhfNsrsbh, String[].class);
        } catch (Exception e) {
            xfshs = new String[]{xhfNsrsbh};
        }
        xfshs = NsrsbhUtils.getNsrsbhList(xfshs);
        R r = new R(true);
        r.put(ConfigureConstant.TAB_CODE, "");
        if (ObjectUtil.isNotNull(xfshs) && xfshs.length == 1) {
            String terminalCode = apiTaxEquipmentService.getTerminalCode(xfshs[0]);
            r.put(ConfigureConstant.STRING_TERMINAL_CODE, terminalCode);
        }
    
        List<CountSurplusVO> surplusVOS = new ArrayList<>();
        for (String xfsh : xfshs) {
            try {
                CountToB countToB = new CountToB();
                countToB.setDataType(countSurplusOwn.getDataType());
                countToB.setUserId(countSurplusOwn.getUserId());
                countToB.setTaxpayerCode(xfsh);
                countToB.setTabCode(countSurplusOwn.getTabCode());
                countToB.setFjh(countSurplusOwn.getFjh());
                countToB.setDeptId(countSurplusOwn.getDeptId());
                countToB.setNsrmc(countSurplusOwn.getNsrmc());
                R r1 = countInvoiceSurplus(countToB);
                if (r1 != null && r1.get(ConfigureConstant.STRING_CONTENT) != null) {
                    Collection<CountSurplusVO> countSurplusVOS = (Collection<CountSurplusVO>) r1.get(ConfigureConstant.STRING_CONTENT);
                    surplusVOS.addAll(countSurplusVOS);
                }
            } catch (Exception e) {
                log.error("发票余量查询异常,异常信息是：{}", e);
            }
        }
        //当月剩余可开票量 p +d+z
        int totalPage = surplusVOS.size();
        int pageSize = countSurplusOwn.getPageSize();
        int currPage = countSurplusOwn.getCurrPage();
    
        PageUtils page = new PageUtils(surplusVOS, totalPage, pageSize, currPage, true);
        r.put(OrderManagementConstant.DATA, page);
        return r;
    }
    
    
    /**
     * 大B 使用
     *
     * @param countToB
     * @return
     */
    @ApiOperation(value = "统计开票余量-大B", notes = "发票统计-统计开票余量")
    @PostMapping(value = "/surplus")
    public R countInvoiceSurplus(@RequestBody CountToB countToB) {
        log.debug("入参：{}", JsonUtils.getInstance().toJsonString(countToB));
        R volid = volid(countToB);
        if (volid != null) {
            return volid;
        }
        /**
         * 查询剩余票量
         */
        TaxEquipmentInfo taxEquipmentInfo = new TaxEquipmentInfo();
        taxEquipmentInfo.setXhfNsrsbh(countToB.getTaxpayerCode());
        List<String> shList = NsrsbhUtils.transShListByNsrsbh(countToB.getTaxpayerCode());
        List<TaxEquipmentInfo> queryTaxEquipment = apiTaxEquipmentService.queryTaxEquipmentList(taxEquipmentInfo, shList);
        String terminalCode = "";
        String nsrmc = "";
        if (CollectionUtils.isEmpty(queryTaxEquipment) || StringUtils.isBlank(queryTaxEquipment.get(0).getSksbCode())) {
        
            return R.error().put(OrderManagementConstant.CODE, ConfigureConstant.STRING_9999).put(OrderManagementConstant.MESSAGE, "税控设备未配置");
        } else {
    
            terminalCode = queryTaxEquipment.get(0).getSksbCode();
            nsrmc = queryTaxEquipment.get(0).getXhfMc();
            if (OrderInfoEnum.TAX_EQUIPMENT_FGHX.getKey().equals(terminalCode) || OrderInfoEnum.TAX_EQUIPMENT_FGBW.getKey().equals(terminalCode) || OrderInfoEnum.TAX_EQUIPMENT_FGUKEY.getKey().equals(terminalCode)) {
                log.warn("方格相关不支持统计");
                return null;
            }
        }
    
        R r = invoiceCountService.getFpyl(countToB, terminalCode, nsrmc);
        
        log.info("返回信息：{}", JsonUtils.getInstance().toJsonString(r));
        return r;
    }
    
    @ApiOperation(value = "按照开票点统计", notes = "发票统计-按照开理点统计开票")
    @PostMapping("/bySld")
    @SysLog(operation = "开票点统计发票rest接口", operationDesc = "提供产品使用,按照开票点统计发票", key = "票量统计")
    public R countBySld(
            @ApiParam(name = "startTime", value = "开始时间", required = true) @RequestParam(value = "startTime", required = true) String startTime,
            @ApiParam(name = "endTime", value = "结束时间", required = true) @RequestParam(value = "endTime", required = true) String endTime,
            @ApiParam(name = "xhfNsrsbh", value = "销方税号", required = true) @RequestParam(value = "xhfNsrsbh", required = true) String xhfNsrsbh,
            @ApiParam(name = "pageSize", value = "分页页数", required = true) @RequestParam(value = "pageSize", required = true) int pageSize,
            @ApiParam(name = "currPage", value = "分页当前页", required = true) @RequestParam(value = "currPage", required = true) int currPage,
            @ApiParam(name = "sld", value = "开票点", required = false) @RequestParam(value = "sld", required = false) String sld) {
        log.info("接口入参：startTime:{},endTime:{},nsrsbh:{}", startTime, endTime, xhfNsrsbh);
        
        
        /**
         * timeFlag = 0 == > 时间格式 yyyy-MM
         * timeFlag = 1 == > 时间格式 yyyy-MM-dd
         */
        Date start;
        Date end;
        try {
            start = DateUtilsLocal.parseYYYYMMDD(startTime);
            end = DateUtilsLocal.parseYYYYMMDD(endTime);
        } catch (ParseException e) {
            log.error("时间输入格式有误！");
            return new R(false).put(OrderManagementConstant.ALL_MESSAGE, OrderInfoContentEnum.INVOICE_TIME_PARSEEXCEPTION.getMessage());
        }
        if (StringUtils.isBlank(xhfNsrsbh)) {
            log.error("{},请求税号为空!", LOGGER_MSG);
            return R.error(OrderInfoContentEnum.TAXCODE_ISNULL);
        }
    
        List<String> shList = NsrsbhUtils.transShListByXhfNsrsbh(xhfNsrsbh);
    
        Set<SearchFjh> resultList = unifyService.getFjh(shList, "");
    
        if (resultList.size() <= 0) {
            log.error("未查询到开票点！");
            return new R(false).put(OrderManagementConstant.ALL_MESSAGE, OrderInfoContentEnum.INVOICE_SLD_NULL.getMessage());
        }
    
        log.info("开票点查询结果：{}", JsonUtils.getInstance().toJsonString(resultList));
        /**
         * 把Sld 和 Kpdmc 放入Map
         */
        Map<String, SearchFjh> map = new HashMap<>(5);
        for (SearchFjh searchSld : resultList) {
            map.put(searchSld.getNsrsbh() + searchSld.getFjh(), searchSld);
        }
    
    
        List<CountBySldVO> moreMonthOfInvoice = invoiceCountService.getCountOfInvoiceBySld(start, end, shList, sld);
        if (moreMonthOfInvoice == null || moreMonthOfInvoice.size() == 0) {
            return new R(false).put(OrderManagementConstant.ALL_MESSAGE, OrderInfoContentEnum.INVOICE_COUNT_NULL.getMessage());
        }
        log.info("根据受理点统计结果：{}", JsonUtils.getInstance().toJsonString(moreMonthOfInvoice));
        //补全开票点名称
        for (CountBySldVO vo : moreMonthOfInvoice) {
            if (map.get(vo.getNsrsbh() + vo.getFjh()) != null && StringUtils.isNotBlank(map.get(vo.getNsrsbh() + vo.getFjh()).getKpdMc())) {
                vo.setSpmc(map.get(vo.getNsrsbh() + vo.getFjh()).getKpdMc());
            } else {
                log.error("未找到sld:{}对应的kpdmc", map.get(vo.getSld()));
            }
            if (StringUtils.isNotBlank(vo.getSldMc())) {
                vo.setSld(vo.getSldMc());
            } else {
                if ("-1".equals(vo.getSld())) {
                    vo.setSld("自动匹配");
                } else {
                    log.error("未找到统计的开票点对应税盘名称！sld:{}", vo.getSld());
                    vo.setSld("此开票点已删除");
                }
            }
    
    
        }
        
        int totalPage = moreMonthOfInvoice.size();
    
        PageUtils page = new PageUtils(moreMonthOfInvoice, totalPage, pageSize, currPage, true);
        
        return R.ok().put(OrderManagementConstant.DATA, page);
    }
    
    /**
     * 根据天数统计发票数据   提供产品和大B使用
     */
    @ApiOperation(value = "按照时间统计开票", notes = "发票统计-按照时间统计开票")
    @PostMapping("/byTime")
    @SysLog(operation = "开票点统计发票rest接口", operationDesc = "提供产品使用,按照开票点统计发票", key = "票量统计")
    public R countByTime(
            @ApiParam(name = "startTime", value = "开始时间", required = true) @RequestParam(value = "startTime", required = true) String startTime,
            @ApiParam(name = "endTime", value = "结束时间", required = true) @RequestParam(value = "endTime", required = true) String endTime,
            @ApiParam(name = "xhfNsrsbh", value = "纳税人识别号", required = true) @RequestParam(value = "xhfNsrsbh", required = true) String xhfNsrsbh,
            @ApiParam(name = "timeFlag", value = "时间标志", required = true) @RequestParam(value = "timeFlag", required = true) String timeFlag,
            @ApiParam(name = "pageSize", value = "页面条数", required = true) @RequestParam(value = "pageSize", required = true) String pageSize,
            @ApiParam(name = "currPage", value = "当前页", required = true) @RequestParam(value = "currPage", required = true) String currPage) {
        
        if (!(ConfigureConstant.STRING_0.equals(timeFlag) || ConfigureConstant.STRING_1.equals(timeFlag))) {
            return new R(false).put(OrderManagementConstant.ALL_MESSAGE, OrderInfoContentEnum.INVOICE_TIMEFLAG_ERROR.getMessage());
        }
        /**
         * timeFlag = 0 == > 时间格式 yyyy-MM
         * timeFlag = 1 == > 时间格式 yyyy-MM-dd
         */
        Date start = new Date();
        Date end = new Date();
        try {
            if (ConfigureConstant.STRING_0.equals(timeFlag)) {
                start = DateUtilsLocal.parseYYYYMM(startTime);
                end = DateUtilsLocal.parseYYYYMM(endTime);
    
            } else if (ConfigureConstant.STRING_1.equals(timeFlag)) {
                start = DateUtilsLocal.parseYYYYMMDD(startTime);
                end = DateUtilsLocal.parseYYYYMMDD(endTime);
                //获取当天的最大时间
                end = DateUtilsLocal.getEndOfDay(end);
            }
        } catch (ParseException e) {
            log.error("时间输入格式有误！");
            return new R(false).put(OrderManagementConstant.ALL_MESSAGE, OrderInfoContentEnum.INVOICE_TIME_PARSEEXCEPTION.getMessage());
        }
        List<String> list = new ArrayList<>();
        if (StringUtils.isNotBlank(xhfNsrsbh)) {
            list = JsonUtils.getInstance().parseObject(xhfNsrsbh, List.class);
        }
        PageUtils countByTime = invoiceCountService.getCountByTime(start, end, list, timeFlag, pageSize, currPage);
        return R.ok().put(ConfigureConstant.STRING_CONTENT, countByTime);
    }
    
    
}
