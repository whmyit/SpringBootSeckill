package com.dxhy.order.consumer.modules.invoice.controller;

import com.dxhy.order.api.ApiInvoiceCommonService;
import com.dxhy.order.consumer.annotation.SysLog;
import com.dxhy.order.consumer.model.page.OrderListQuery;
import com.dxhy.order.consumer.modules.order.service.ExcelReadService;
import com.dxhy.order.consumer.modules.user.service.UserInfoService;
import com.dxhy.order.consumer.utils.CommonFileUtils;
import com.dxhy.order.consumer.utils.PageBeanConvertUtil;
import com.dxhy.order.utils.NsrsbhUtils;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.dubbo.config.annotation.Reference;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.util.List;
import java.util.Map;

/**
 * @author ：杨士勇
 * @ClassName ：ExportExcelController
 * @Description ：excel导出控制层
 * @date ：2019年5月24日 下午7:34:23
 */

@RestController
@RequestMapping(value = "/export")
@Api(value = "开票结果导出", tags = {"发票模块"})
@Slf4j
public class ExportExcelController {
    
    private static final String TEMPORARY_FILE_NAME = "/temporary_file_";
    
    private static final String TEMP_INVOICE_ITEM_NAME = "ExportExcelFile";
    
    private static final String XLSX = ".xlsx";
    
    private static final String LOGGER_MSG = "excel导出控制层";
    
    @Resource
    private ExcelReadService excelReadService;
    
    @Resource
    private UserInfoService userInfoService;
    
    @Reference
    private ApiInvoiceCommonService apiInvoiceCommonService;
    
    /**
     * @Description ：excel导出
     */
    @PostMapping("/excelExport")
    @ApiOperation(value = "发票导出", notes = "发票管理-发票导出")
    @SysLog(operation = "excel明细导出rest接口", operationDesc = "excel明细导出rest接口", key = "订单查询")
    public void excelExport(HttpServletRequest request, HttpServletResponse response, @RequestBody OrderListQuery orderBatchQuery) {
        //数据转换
        Map<String, Object> newMap = PageBeanConvertUtil.convertToMap(orderBatchQuery);
    
        if (StringUtils.isBlank(orderBatchQuery.getXhfNsrsbh())) {
            log.error("{},请求税号为空!", LOGGER_MSG);
        }
    
        List<String> shList = NsrsbhUtils.transShListByXhfNsrsbh(orderBatchQuery.getXhfNsrsbh());
    
        // 创建临时文件
        OutputStream out = null;
        InputStream in = null;
        String filePrefix = apiInvoiceCommonService.getGenerateShotKey();
        File file = CommonFileUtils.creaetFile(getExportTempFilePath(TEMP_INVOICE_ITEM_NAME),
                TEMPORARY_FILE_NAME + filePrefix + XLSX);
        try {
            response.setContentType("octets/stream");
            response.addHeader("Content-Disposition",
                    "attachment;filename=" + new String("发票明细统计".getBytes("gb2312"), "ISO8859-1") + ".xlsx");
            out = response.getOutputStream();
            // 调用生成excel方法
            log.info("{}excel导出开始", LOGGER_MSG);
            // 创建excel到处临时文件
            excelReadService.exportInvoiceDetailExcel(file, out, newMap, shList);
            in = new FileInputStream(file);
            int len = 0;
            byte[] buffer = new byte[1024];
            while ((len = in.read(buffer)) > 0) {
                out.write(buffer, 0, len);
            }
        } catch (Exception e) {
            log.error("导出excel出现异常,异常信息为:{}", e);
        } finally {
            try {
                if (out != null) {
                    out.close();
                }
                if (in != null) {
                    in.close();
                }
                if (file != null && file.exists()) {
                    file.delete();
                }
            } catch (IOException e) {
                log.error("流关闭异常");
            }
            
            
        }
    }
    
    /**
     * @Title : getExportTempFilePath @Description
     * ：获取项目路径 @param @param str @param @return @return String @exception
     */
    private String getExportTempFilePath(String str) {
        return this.getClass().getClassLoader().getResource("").getPath() + str;
    }
    
}
