package com.dxhy.order.consumer.modules.order.controller;

import com.dxhy.order.constant.ConfigureConstant;
import com.dxhy.order.consumer.annotation.SysLog;
import com.dxhy.order.consumer.config.SystemConfig;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FileUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;

/**
 * 模板下载
 *
 * @author 陈玉航
 * @version 1.0 Created on 2018年7月25日 下午3:30:16
 */
@Api(value = "订单模板下载", tags = {"订单模块"})
@RestController
@RequestMapping("/download")
@Slf4j
public class OrderTemplateDownloadController {
    
    private static final String LOGGER_MSG = "模板下载接口";
    
    /**
     * 订单模板下载接口
     *
     * @date: Created on 2018年7月25日 下午3:30:28
     */
    @ApiOperation(value = "订单模板下载接口", notes = "订单模板下载-下载订单模板文件")
    @RequestMapping(value = "/order", method = RequestMethod.GET)
    @SysLog(operation = "订单模板下载rest接口", operationDesc = "订单模板下载", key = "订单模板")
    public void orderTemplateDownload(HttpServletRequest request, HttpServletResponse response) {
        log.info("{} 订单模板下载开始", LOGGER_MSG);
        try {
            String resource = "";
            //weblogic获取附件地址单独处理
            if (ConfigureConstant.STRING_1.equals(SystemConfig.webServerType)) {
                resource = SystemConfig.downloadFileUrl + "CS_new.xlsx";
                log.info("文件路径：{}", resource);
            } else {
                resource = Thread.currentThread().getContextClassLoader().getResource("download/CS_new.xlsx").getPath();
            }
            response.reset();
            response.setContentType("application/x-msdownload");
            response.addHeader("Content-Disposition",
                    "attachment;filename=" + new String("订单导入excel模板".getBytes("gb2312"), "ISO8859-1") + ".xlsx");
            response.getOutputStream().write(FileUtils.readFileToByteArray(new File(resource)));
            log.info("{} 订单模板下载完毕", LOGGER_MSG);
        } catch (Exception e) {
            log.error("{}订单模板下载异常 e:{}", LOGGER_MSG, e);
            throw new RuntimeException("订单模板下载异常");
        }
    }
    
}
