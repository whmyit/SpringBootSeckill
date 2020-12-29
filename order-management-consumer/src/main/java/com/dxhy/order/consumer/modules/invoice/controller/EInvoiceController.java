package com.dxhy.order.consumer.modules.invoice.controller;


import com.alibaba.fastjson.JSONObject;
import com.dxhy.order.api.ApiOrderInvoiceInfoService;
import com.dxhy.order.constant.ConfigureConstant;
import com.dxhy.order.consumer.annotation.SysLog;
import com.dxhy.order.consumer.model.FileDownLoad;
import com.dxhy.order.consumer.modules.invoice.service.EInvoiceService;
import com.dxhy.order.consumer.utils.Base64Encoding;
import com.dxhy.order.consumer.utils.PdfUtil;
import com.dxhy.order.utils.DateUtilsLocal;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.dubbo.config.annotation.Reference;
import org.springframework.http.HttpHeaders;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

/**
 * 描述信息：电子发票开票控制层
 *
 * @author 谢元强
 * @date Created on 2018-07-21
 */
@Slf4j
@RestController
@RequestMapping("/eInvoice")
@Api(value = "电票开具", tags = {"发票模块"})
public class EInvoiceController {
    
    private static final String LOGGER_MSG = "电子发票控制层";
    
    @Resource
    private EInvoiceService eInvoiceService;
    
    @Reference
    private ApiOrderInvoiceInfoService apiOrderInvoiceInfoService;
    
    /**
     * 在使用
     *
     * @param invoiceCodes 发票代码
     * @param invoiceNos   发票号码
     * @Description 打印电子发票
     * @Author xieyuanqiang
     * @Date 10:13 2018-07-21
     */
    @RequestMapping(value = "/printInvoice", method = RequestMethod.GET)
    @ApiOperation(value = "发票打印", notes = "发票打印管理-发票电票打印")
    @SysLog(operation = "发票打印", operationDesc = "发票打印", key = "发票打印")
    public void printInvoice(String[] invoiceCodes, String[] invoiceNos, @ApiParam(name = "xhfNsrsbh", value = "销货方纳税人识别号", required = true) @RequestParam(value = "xhfNsrsbh", required = true) String[] xhfNsrsbh, HttpServletRequest request, HttpServletResponse response) {
        log.info("{}打印电子发票 参数 发票代码{} 发票号码 {},销方税号:{}", LOGGER_MSG, invoiceCodes, invoiceNos, xhfNsrsbh);
        if (invoiceCodes == null || invoiceNos == null || xhfNsrsbh == null || invoiceCodes.length != invoiceNos.length || invoiceCodes.length != xhfNsrsbh.length) {
            log.info("{}参数错误", LOGGER_MSG);
        }
        response.setHeader("Content-disposition", "inline;filename="
                + "printInvoice.pdf");
        response.setContentType(ConfigureConstant.STRING_APPLICATION_PDF);
        OutputStream outputStream = null;
        try {
            List<byte[]> files = eInvoiceService.printInvoice(invoiceCodes, invoiceNos, xhfNsrsbh);
            response.getOutputStream().write(PdfUtil.mergePdfFiles(files));
        
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    /**
     * 在使用
     *
     * @param invoiceCodes 发票代码
     * @param invoiceNos   发票号码
     * @Param zipName 压缩文件名称
     * @Description 下载电子发票
     * @Author xieyuanqiang
     * @Date 10:13 2018-07-21
     */
    @RequestMapping(value = "/downInvoice", method = RequestMethod.GET)
    @ApiOperation(value = "发票下载", notes = "发票打印管理-发票下载")
    @SysLog(operation = "发票下载", operationDesc = "发票下载", key = "发票下载")
    public void downInvoice(@RequestParam String[] invoiceCodes, @RequestParam String[] invoiceNos, @RequestParam String zipName, @ApiParam(name = "xhfNsrsbh", value = "销货方纳税人识别号", required = true) @RequestParam(value = "xhfNsrsbh", required = true) String[] xhfNsrsbh, HttpServletRequest request, HttpServletResponse response) {
        log.info("{}打印电子发票 参数invoiceCodes {} invoiceNos{}", LOGGER_MSG, JSONObject.toJSONString(invoiceCodes), JSONObject.toJSONString(invoiceNos));
        //压缩文件初始设置
        // PDF本地临时文件
        File tepmpdffile = null;
        List<FileDownLoad> dataList = new ArrayList<>();
    
        if (invoiceCodes != null && invoiceNos != null && xhfNsrsbh != null && invoiceCodes.length == invoiceNos.length && invoiceCodes.length == xhfNsrsbh.length) {
            for (int n = 0; n < invoiceCodes.length; n++) {
                List<FileDownLoad> fileDownLoadList = eInvoiceService.queryInvoicePdfPath(invoiceCodes[n], invoiceNos[n], xhfNsrsbh[n]);
                dataList.addAll(fileDownLoadList);
            }
        }
        zipName = StringUtils.isEmpty(zipName) ? DateUtilsLocal.getDefaultFormatToString(new Date()) : zipName;
        // 创建临时压缩文件
        try {
            tepmpdffile = File.createTempFile(zipName, ".zip",
                    new File(System.getProperty("java.io.tmpdir")));
            BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(tepmpdffile));
            ZipOutputStream zos = new ZipOutputStream(bos);
            ZipEntry ze = null;
            //将所有需要下载的pdf文件都写入临时zip文件
            for (FileDownLoad file : dataList) {
                BufferedInputStream bis = new BufferedInputStream(new ByteArrayInputStream(Base64Encoding.decode(file.getFileContent())));
                ze = new ZipEntry(file.getFileName());
                zos.putNextEntry(ze);
                int s = -1;
                while ((s = bis.read()) != -1) {
                    zos.write(s);
                }
                bis.close();
            }
            zos.flush();
            zos.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        //以上，临时压缩文件创建完成
    
        //进行浏览器下载
        //获得浏览器代理信息
        final String userAgent = request.getHeader(HttpHeaders.USER_AGENT);
        //判断浏览器代理并分别设置响应给浏览器的编码格式
        String finalFileName = null;
        DataInputStream in = null;
        ServletOutputStream servletOutputStream = null;
        try {
            //IE浏览器
            if (StringUtils.contains(userAgent, "MSIE") || StringUtils.contains(userAgent, "Trident")) {
                finalFileName = URLEncoder.encode(zipName, "UTF8");
                System.out.println("IE浏览器");
        
            } else if (StringUtils.contains(userAgent, "Mozilla")) {
                //google,火狐浏览器
                finalFileName = new String(zipName.getBytes(StandardCharsets.UTF_8), "ISO8859-1");
            } else {
                //其他浏览器
                finalFileName = URLEncoder.encode(zipName, "UTF8");
            }
            response.setContentType("multipart/form-data");
            response.setHeader("Content-Disposition", "attachment;filename=\"" + finalFileName + "\".zip");
            //下载文件的名称
            FileInputStream input = new FileInputStream(tepmpdffile);
    
            OutputStream out = response.getOutputStream();
            byte[] b = new byte[2048];
            int len;
            while ((len = input.read(b)) != -1) {
                out.write(b, 0, len);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (tepmpdffile != null) {
                    if (!tepmpdffile.delete()) {
                        throw new Exception("删除文件失败");
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        
        
        }
    
    }
    
}


