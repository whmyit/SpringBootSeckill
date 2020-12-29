package com.dxhy.order.consumer.modules.manager.controller;

import com.dxhy.order.api.ApiCommodityService;
import com.dxhy.order.constant.ConfigureConstant;
import com.dxhy.order.consumer.annotation.SysLog;
import com.dxhy.order.consumer.config.SystemConfig;
import com.dxhy.order.model.entity.SysDictionary;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.codec.Charsets;
import org.apache.commons.lang3.StringUtils;
import org.apache.dubbo.config.annotation.Reference;
import org.springframework.http.HttpHeaders;
import org.springframework.util.ResourceUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.FileInputStream;
import java.io.OutputStream;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * 模板下载
 *
 * @author ZSC-DXHY
 * @date 创建时间: 2020-08-14 18:13
 */
@Slf4j
@RestController
@RequestMapping("/downloadExcel")
@Api(value = "模板下载", tags = {"管理模块"})
public class DownloadExcel {
    
    @Reference
    private ApiCommodityService commodityService;
    /**
     * 购方导入模板
     */
    private final String BuyserExcel = "BuyserExcel.xlsx";
    /**
     * 商品导入模板
     */
    private final String commodityExcel = "CommodityExcel.xlsx";
    
    /**
     * 商品编码导入 智能匹配编码模板
     */
    private final String productExcel = "Product.xlsx";
    /**
     * 分组导入模板
     */
    private final String GroupExcel = "GroupExcel.xlsx";
    
    /**
     * 下载应用模板
     *
     * @return
     */
    @RequestMapping(method = RequestMethod.GET)
    @ApiOperation(value = "模板下载", notes = "模板下载管理-模板下载")
    @SysLog(operation = "模板下载", operationDesc = "模板下载", key = "模板下载")
    public void getApplicationTemplate(String excelType, HttpServletRequest req, HttpServletResponse res) {
        log.info("模板下载开始执行 参数 {}", excelType);
        try {
            //获取要下载的模板名称
            String excelName = "";
            if (StringUtils.isNotBlank(excelType)) {
                switch (excelType) {
                    case "0":
                        excelName = BuyserExcel;
                        break;
                    case "1":
                        SysDictionary sysDictionary = commodityService.querySysDictionary();
                        //遍历  调用接口  获取大数据接口  0  使用  1不使用
                        if (sysDictionary != null) {
                            String delFlag = sysDictionary.getDelFlag();
                            if (ConfigureConstant.STRING_0.equals(delFlag)) {
                                excelName = productExcel;
                            } else {
                                excelName = commodityExcel;
                            }
                        } else {
                            excelName = commodityExcel;
                        }
                        break;
                    case "2":
                        excelName = GroupExcel;
                        break;
                    default:
                        throw new IllegalStateException("Unexpected value: " + excelType);
                }
            }
            //获取文件的路径 application/vnd.ms-excel
            log.info("下载开始执行 下载文件 {}", excelName);
            String rootPath = "";
            //weblogic获取附件地址单独处理
            if (ConfigureConstant.STRING_1.equals(SystemConfig.webServerType)) {
                rootPath = SystemConfig.downloadFileUrl + excelName;
                log.info("附件地址：{}", rootPath);
            } else {
                File path = new File(ResourceUtils.getURL("classpath:").getPath());
                String absolutePath = path.getAbsolutePath();
                rootPath = absolutePath + "/download" + File.separator + excelName;
            }
            FileInputStream input = new FileInputStream(rootPath);
            //设置要下载的文件的名称
            String downLoadName = "";
            switch (excelType) {
                case "0":
                    downLoadName = "购方信息导入模板";
                    break;
                case "1":
                    downLoadName = "商品信息导入模板";
                    break;
                case "2":
                    downLoadName = "分组信息导入模板";
                    break;
                default:
                    throw new IllegalStateException("Unexpected value: " + excelType);
            }
            log.info("模板下载为 {}", downLoadName);
            //通知类型
            Date date = new Date();
            SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMddHHmmss");
            String format = formatter.format(date);
            res.setCharacterEncoding(Charsets.UTF_8.name());
            res.setContentType("application/vnd.ms-excel;charset=UTF-8");
    
            if ((req.getHeader(HttpHeaders.USER_AGENT).toUpperCase().indexOf("MSIE") > 0) || (req.getHeader(HttpHeaders.USER_AGENT).contains("Trident"))) {
                downLoadName = URLEncoder.encode(downLoadName, StandardCharsets.UTF_8.name());
            }
            res.setHeader("Content-Disposition", "attachment;filename=" + downLoadName + format + ".xlsx");
    
            OutputStream out = res.getOutputStream();
            byte[] b = new byte[2048];
            int len;
            while ((len = input.read(b)) != -1) {
                out.write(b, 0, len);
            }
            //修正 Excel在“xxx.xlsx”中发现不可读取的内容。是否恢复此工作薄的内容？如果信任此工作簿的来源，请点击"是"
            input.close();
        } catch (Exception ex) {
            log.error("getApplicationTemplate :", ex);
        }
    }
}
