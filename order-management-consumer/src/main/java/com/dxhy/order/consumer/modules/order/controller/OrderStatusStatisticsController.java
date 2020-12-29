package com.dxhy.order.consumer.modules.order.controller;

import com.dxhy.order.constant.ConfigureConstant;
import com.dxhy.order.constant.OrderInfoContentEnum;
import com.dxhy.order.constant.OrderInfoEnum;
import com.dxhy.order.constant.OrderManagementConstant;
import com.dxhy.order.consumer.annotation.SysLog;
import com.dxhy.order.consumer.model.page.OrderListQuery;
import com.dxhy.order.consumer.model.page.Query;
import com.dxhy.order.consumer.modules.order.service.ExcelReadService;
import com.dxhy.order.consumer.modules.order.service.OrderStatusStatisticsService;
import com.dxhy.order.consumer.modules.user.service.UserInfoService;
import com.dxhy.order.consumer.utils.PageBeanConvertUtil;
import com.dxhy.order.model.OrderInfo;
import com.dxhy.order.model.OrderStatusStatistics;
import com.dxhy.order.model.PageUtils;
import com.dxhy.order.model.R;
import com.dxhy.order.utils.JsonUtils;
import com.dxhy.order.utils.NsrsbhUtils;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.hssf.usermodel.*;
import org.apache.poi.ss.usermodel.HorizontalAlignment;
import org.apache.poi.ss.util.WorkbookUtil;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.OutputStream;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 订单统计控制层
 *
 * @author ZSC-DXHY
 * @date 创建时间: 2020-08-14 15:26
 */
@RestController
@Api(value = "订单统计", tags = {"订单模块"})
@RequestMapping(value = "/orderStatus")
@Slf4j
public class OrderStatusStatisticsController {
    private static final String LOGGER_MSG = "(订单统计控制层)";
    SimpleDateFormat sf2 = new SimpleDateFormat("yyyy-MM-dd");
    private static final String PATTERN_JE = "^(([-1-9]\\d*)|([0]))(\\.(\\d){0,2})?$";
    
    @Resource
    private OrderStatusStatisticsService orderStatusStatisticsService;
    
    @Resource
    private UserInfoService userInfoService;
    
    @Resource
    private ExcelReadService excelReadService;
    
    @ApiOperation(value = "查询订单状态列表", notes = "订单统计-查询订单状态列表")
    @PostMapping("/queryOrderStatusList")
    @SysLog(operation = "订单列表查询rest接口", operationDesc = "查询订单状态列表", key = "订单查询")
    public R queryOrderStatusList(HttpServletRequest request,
                                  @ApiParam(name = "fplx", value = "发票类型", required = false) @RequestParam("fplx") String fplx,
                                  @ApiParam(name = "kpzt", value = "开票状态", required = false) @RequestParam("kpzt") String kpzt,
                                  @ApiParam(name = "pushStatus", value = "推送状态", required = false) @RequestParam("pushStatus") String pushStatus,
                                  @ApiParam(name = "ghfNsrsbh", value = "购货方纳税人识别号", required = false) @RequestParam("ghfNsrsbh") String ghfNsrsbh,
                                  @ApiParam(name = "xhfNsrsbh", value = "销货方纳税人识别号", required = true) @RequestParam("xhfNsrsbh") String nsrsbh,
                                  @ApiParam(name = "startTime", value = "开始时间", required = true) @RequestParam("startTime") String startTime,
                                  @ApiParam(name = "endTime", value = "结束时间", required = true) @RequestParam("endTime") String endTime,
                                  @ApiParam(name = "deptId", value = "组织机构id", required = true) @RequestParam("deptId") String deptId,
                                  @ApiParam(name = "pageSize", value = "每页显示个数", required = true) @RequestParam("pageSize") String pageSize,
                                  @ApiParam(name = "currPage", value = "当前页数", required = true) @RequestParam("currPage") String currPage) {
        try {
            log.info("{}订单列表查询,入参--fplx:{},kjzt:{},pushStatus:{},ghfNsrsbh:{},xhfNsrsbh:{}startTime:{},endTime:{},deptId:{},pageSize:{},currPage:{};",
                    LOGGER_MSG, fplx, kpzt, pushStatus, ghfNsrsbh, nsrsbh, startTime, endTime, deptId, pageSize, currPage);
            Map<String, Object> paramMap = new HashMap<>(10);
    
            if (StringUtils.isBlank(startTime) || StringUtils.isBlank(endTime)) {
        
            } else {
                Date starttime = sf2.parse(startTime);
                Date endtime = sf2.parse(endTime);
                if (starttime.after(endtime)) {
                    log.error("{}开始时间不能大于结束时间", LOGGER_MSG);
                    return R.error(OrderInfoContentEnum.ORDER_TIME_ERROR.getKey(), OrderInfoContentEnum.ORDER_TIME_ERROR.getMessage());
                }
                paramMap.put("startTime", startTime);
                paramMap.put("endTime", endTime);
            }
    
            if (StringUtils.isNotBlank(fplx)) {
                paramMap.put("fplx", fplx);
            }
            if (StringUtils.isNotBlank(kpzt)) {
                paramMap.put("kpzt", kpzt);
            }
            if (StringUtils.isNotBlank(pushStatus)) {
                paramMap.put("pushStatus", pushStatus);
            }
            List<String> shList = new ArrayList<>();
            if (StringUtils.isNotBlank(nsrsbh)) {
                String[] xfshs = JsonUtils.getInstance().fromJson(nsrsbh, String[].class);
                shList = Arrays.asList(xfshs);
            }
    
            if (StringUtils.isNotBlank(ghfNsrsbh)) {
                paramMap.put("ghfNsrsbh", ghfNsrsbh);
            }
            paramMap.put("orderStatus", OrderInfoEnum.ORDER_VALID_STATUS_0.getKey());
            if (StringUtils.isBlank(pageSize) || StringUtils.isBlank(currPage)) {
                return R.error(OrderInfoContentEnum.ORDER_PAGE_ERROR.getKey(), OrderInfoContentEnum.ORDER_PAGE_ERROR.getMessage());
            }
            paramMap.put("pageSize", Integer.parseInt(pageSize));
            paramMap.put("currPage", Integer.parseInt(currPage));
            PageUtils page = orderStatusStatisticsService.selectOrderStatusInfo(paramMap, shList);
    
            return R.ok().put(OrderManagementConstant.DATA, page);
        } catch (ParseException e) {
            log.error("{}列表查询异常:{}", LOGGER_MSG, e);
            return R.error(OrderInfoContentEnum.INTERNAL_SERVER_ERROR.getKey(), OrderInfoContentEnum.INTERNAL_SERVER_ERROR.getMessage());
        }
    }
    
    @ApiOperation(value = "导出订单统计列表", notes = "订单统计-导出订单统计列表")
    @PostMapping("/exportOrderStatusList")
    @SysLog(operation = "订单导出rest接口", operationDesc = "导出订单统计列表", key = "订单导出")
    public void exportOrderStatusList(HttpServletRequest request, HttpServletResponse response,
                                      @RequestBody OrderListQuery orderBatchQuery) throws ParseException, IOException {
        log.info("{}订单列表查询,入参:{}", LOGGER_MSG, JsonUtils.getInstance().toJsonString(orderBatchQuery));
        
        if (StringUtils.isNotBlank(orderBatchQuery.getMinKphjje())
                && StringUtils.isNotBlank(orderBatchQuery.getMaxKphjje())) {
            /**
             * 判断金额 是否正确
             */
            if (Double.parseDouble(orderBatchQuery.getMinKphjje()) > Double
                    .parseDouble(orderBatchQuery.getMaxKphjje())) {
                log.error("{}开始金额不能大于结束金额", LOGGER_MSG);
            }
        }
        
        Pattern pattern = Pattern.compile(PATTERN_JE);
        if (StringUtils.isNotBlank(orderBatchQuery.getMinKphjje())) {
            Matcher minMatch = pattern.matcher(orderBatchQuery.getMinKphjje());
            if (minMatch.matches() == false) {
                log.error("{}金额格式错误保留两位小数", LOGGER_MSG);
            }
        }
        if (StringUtils.isNotBlank(orderBatchQuery.getMaxKphjje())) {
            Matcher maxMatch = pattern.matcher(orderBatchQuery.getMaxKphjje());
            if (maxMatch.matches() == false) {
                log.error("{}金额格式错误保留两位小数", LOGGER_MSG);
            }
        }
        
        if (StringUtils.isBlank(orderBatchQuery.getStartTime()) || StringUtils.isBlank(orderBatchQuery.getEndTime())) {
    
        } else {
            Date starttime = sf2.parse(orderBatchQuery.getStartTime());
            Date endtime = sf2.parse(orderBatchQuery.getEndTime());
            if (starttime.after(endtime)) {
                log.error("{}开始时间不能大于结束时间", LOGGER_MSG);
            }
        }
        // 数据转换
        Map<String, Object> paramMap = PageBeanConvertUtil.convertToMap(orderBatchQuery);
    
        if (StringUtils.isBlank(orderBatchQuery.getXhfNsrsbh())) {
            log.error("{},请求税号为空!", LOGGER_MSG);
        }
    
        List<String> shList = NsrsbhUtils.transShListByXhfNsrsbh(orderBatchQuery.getXhfNsrsbh());
    
        response.setContentType("octets/stream");
        response.addHeader("Content-Disposition",
                "attachment;filename=" + new String("订单统计".getBytes("gb2312"), "ISO8859-1") + ".xlsx");
        OutputStream outputStream = response.getOutputStream();
        // 导出excel
        excelReadService.exportOrderInfo(paramMap, outputStream, shList);
    }
    
    
    /**
     * activex 专用
     */
    @ApiOperation(value = "导出订单统计列表-ActiveX", notes = "订单统计-导出订单统计列表")
    @PostMapping(value = "/exportOrderStatusListIE")
    @SysLog(operation = "订单导出rest接口", operationDesc = "导出订单统计列表", key = "订单导出")
    public void exportOrderStatusListIe(HttpServletRequest request, HttpServletResponse response,
                                        @RequestParam(value = "fplx") String fplx,
                                        @RequestParam(value = "kpzt") String kpzt,
                                        @RequestParam(value = "pushStatus") String pushStatus,
                                        @RequestParam(value = "startTime") String startTime,
                                        @RequestParam(value = "endTime") String endTime,
                                        @RequestParam(value = "xhfNsrsbh") String xhfNsrsbh) {
        try {
            Query query = new Query();
            query.setFplx(fplx);
            query.setKpzt(kpzt);
            query.setPushStatus(pushStatus);
            query.setStartTime(startTime);
            query.setEndTime(endTime);
            query.setXhfNsrsbh(xhfNsrsbh);
            log.info("{}订单列表查询,入参:{}",
                    LOGGER_MSG, JsonUtils.getInstance().toJsonString(query));
            Map<String, Object> paramMap = new HashMap<>(5);
            OutputStream out = null;
            response.setContentType("octets/stream");
            response.addHeader("Content-Disposition", "attachment;filename=" + new String("订单统计".getBytes("gb2312"), "ISO8859-1") + ".xlsx");
            out = response.getOutputStream();
    
            if (StringUtils.isBlank(query.getStartTime()) || StringUtils.isBlank(query.getEndTime())) {
        
            } else {
                Date starttime = sf2.parse(query.getStartTime());
                Date endtime = sf2.parse(query.getEndTime());
                if (starttime.after(endtime)) {
                    log.error("{}开始时间不能大于结束时间", LOGGER_MSG);
                }
                paramMap.put("startTime", query.getStartTime());
                paramMap.put("endTime", query.getEndTime());
            }
    
            if (StringUtils.isNotBlank(query.getFplx())) {
                paramMap.put("fplx", query.getFplx());
            }
            if (StringUtils.isNotBlank(query.getKpzt())) {
                paramMap.put("kpzt", query.getKpzt());
            }
            if (StringUtils.isNotBlank(query.getPushStatus())) {
                paramMap.put("pushStatus", query.getPushStatus());
            }
            List<String> shList = new ArrayList<>();
            if (StringUtils.isNotBlank(query.getXhfNsrsbh())) {
                String[] nsrsbhs = query.getXhfNsrsbh().split(",");
                shList = Arrays.asList(nsrsbhs);
            }
    
            paramMap.put("orderStatus", OrderInfoEnum.ORDER_VALID_STATUS_0.getKey());
            List<OrderStatusStatistics> exportList = orderStatusStatisticsService.exportOrderStatusInfo(paramMap, shList);
            log.info("查询数据结束 结果 {}", exportList);
            log.info("(导出excel)调用生成excel方法开始");
            String[] headers = {"订单号", "订单时间", "发票类型", "购方名称", "购方税号", "开票金额", "开票税额", "订单开票状态", "开票失败原因", "发票推送状态",};
            // 生成Excel数据
            HSSFWorkbook workbook = new HSSFWorkbook();
            // 生成一个表格
            HSSFSheet sheet = workbook.createSheet(WorkbookUtil.createSafeSheetName("订单统计"));
            HSSFRow row = sheet.createRow(0);
            // 生成一个样式
            HSSFCellStyle style = workbook.createCellStyle();
            style.setAlignment(HorizontalAlignment.CENTER);
            sheet.setColumnWidth(0, 256 * 35 + 184);
            sheet.setColumnWidth(1, 256 * 18 + 184);
            sheet.setColumnWidth(2, 256 * 18 + 184);
            sheet.setColumnWidth(3, 256 * 18 + 184);
            sheet.setColumnWidth(4, 256 * 35 + 184);
            sheet.setColumnWidth(5, 256 * 22 + 184);
            sheet.setColumnWidth(6, 256 * 12 + 184);
            sheet.setColumnWidth(7, 256 * 12 + 184);
            sheet.setColumnWidth(8, 256 * 18 + 184);
            sheet.setColumnWidth(9, 256 * 35 + 184);
            // 生成表头
            for (int i = 0; i < headers.length; i++) {
                HSSFCell cell = row.createCell(i);
                cell.setCellStyle(style);
                HSSFRichTextString text = new HSSFRichTextString(headers[i]);
                cell.setCellValue(text);
            }
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            int index = 1;
            for (OrderStatusStatistics order : exportList) {
                HSSFRow row2 = sheet.createRow(index++);
        
                //订单号
                row2.createCell(0).setCellValue(order.getDdh());

                //接受时间
                row2.createCell(1).setCellValue(order.getJssj() == null ? "" : sdf.format(order.getJssj()));
        
                //发票类型
                row2.createCell(2).setCellValue(formatFplx(order.getFpzldm()));
        
                //购方名称
                row2.createCell(3).setCellValue(order.getGhfmc());
        
                //购方税号
                row2.createCell(4).setCellValue(order.getGhfnsrsbh());
        
                //开票金额
                row2.createCell(5).setCellValue(order.getKphjje());
        
                //开票税额
                row2.createCell(6).setCellValue(order.getKpse());
        
                //开票状态
                row2.createCell(7).setCellValue(formatKpzt(order.getKpzt()));
        
                //开票失败原因
                row2.createCell(8).setCellValue(order.getSbyy());
        
                //发票推送状态
                row2.createCell(9).setCellValue(formatPushStatus(order.getPushstatus()));
        
            }
            workbook.write(out);
            log.info("(导出excel)调用生成excel方法结束");
            out.close();
        } catch (IOException | ParseException e) {
            e.printStackTrace();
        }
    }
    
    @ApiOperation(value = "编辑购货方信息", notes = "订单统计-编辑购货方信息")
    @PostMapping("/updateGhfInfo")
    @SysLog(operation = "更新购方信息rest接口", operationDesc = "购方数据更新接口", key = "订单更新")
    public R updateOrderInfo(HttpServletRequest request,
                             @ApiParam(name = "orderInfo", value = "订单信息", required = true) @RequestBody OrderInfo orderInfo) {
        log.info("{}订单详情编辑,订单信息为:{}", LOGGER_MSG, orderInfo);
        if (orderInfo == null) {
            return R.error(OrderInfoContentEnum.PARAM_NULL.getKey(), OrderInfoContentEnum.PARAM_NULL.getMessage());
        }
        if (StringUtils.isBlank(orderInfo.getXhfNsrsbh())) {
            return R.error(OrderInfoContentEnum.INVOICE_NSRSBH_ERROR);
        }
        Map map = orderStatusStatisticsService.updateGhfInfo(orderInfo);
        if (OrderInfoContentEnum.SUCCESS.getKey().equals(map.get(OrderManagementConstant.ERRORCODE))) {
            return R.ok();
        }
        return R.error();
    }
    
    /**
     * 格式化发票类型
     *
     * @param str 0:增值税专用发票
     *            2: 增值税普通发票
     *            51: 增值税电子普通发票
     *            41:卷票
     * @return 发票类型
     */
    private String formatFplx(String str) {
        String fplx = "";
        if (OrderInfoEnum.ORDER_INVOICE_TYPE_51.getKey().equals(str)) {
            fplx = "增值税电子普通发票";
        } else if (OrderInfoEnum.ORDER_INVOICE_TYPE_2.getKey().equals(str)) {
            fplx = "增值税普通发票";
        } else if (OrderInfoEnum.ORDER_INVOICE_TYPE_0.getKey().equals(str)) {
            fplx = "增值税专用发票";
        } else if (OrderInfoEnum.ORDER_INVOICE_TYPE_41.getKey().equals(str)) {
            fplx = "卷票";
        }
        return fplx;
    }
    
    /**
     * 格式化开票状态
     *
     * @param str 1:开票中
     *            2: 开票成功
     *            3: 开票失败
     * @return 开票状态
     */
    private String formatKpzt(String str) {
        String kpzt = "";
        if (ConfigureConstant.STRING_1.equals(str)) {
            kpzt = "开票中";
        } else if (OrderInfoEnum.INVOICE_STATUS_2.getKey().equals(str)) {
            kpzt = "开票成功";
        } else if (OrderInfoEnum.INVOICE_STATUS_3.getKey().equals(str)) {
            kpzt = "开票失败";
        }
        return kpzt;
    }
    
    /**
     * 格式化发票推送状态
     *
     * @param str 1:开票中
     *            2: 开票成功
     *            3: 开票失败
     * @return 开票状态
     */
    private String formatPushStatus(String str) {
        String pushStatus = "";
        if (OrderInfoEnum.PUSH_STATUS_0.getKey().equals(str)) {
            pushStatus = "未推送";
        } else if (OrderInfoEnum.PUSH_STATUS_1.getKey().equals(str)) {
            pushStatus = "推送成功";
        } else if (OrderInfoEnum.PUSH_STATUS_2.getKey().equals(str)) {
            pushStatus = "推送失败";
        }
        return pushStatus;
    }
}
