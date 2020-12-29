package com.dxhy.order.consumer.modules.invoice.service.impl;

import com.dxhy.order.api.ApiHistoryDataPdfService;
import com.dxhy.order.api.ApiOrderInvoiceInfoService;
import com.dxhy.order.api.ApiTaxEquipmentService;
import com.dxhy.order.constant.ConfigureConstant;
import com.dxhy.order.constant.OrderInfoContentEnum;
import com.dxhy.order.constant.OrderInfoEnum;
import com.dxhy.order.constant.OrderManagementConstant;
import com.dxhy.order.consumer.config.OpenApiConfig;
import com.dxhy.order.consumer.generateinvoice.PDFProducer;
import com.dxhy.order.consumer.model.FileDownLoad;
import com.dxhy.order.consumer.modules.invoice.service.EInvoiceService;
import com.dxhy.order.model.CommonOrderInvoiceAndOrderMxInfo;
import com.dxhy.order.model.HistoryDataPdfEntity;
import com.dxhy.order.model.R;
import com.dxhy.order.model.a9.pdf.GetPdfRequest;
import com.dxhy.order.model.a9.pdf.GetPdfResponseExtend;
import com.dxhy.order.model.a9.pdf.InvoicePdf;
import com.dxhy.order.utils.Base64Encoding;
import com.dxhy.order.utils.HttpInvoiceRequestUtil;
import com.dxhy.order.utils.NsrsbhUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.codec.binary.Base64;
import org.apache.commons.lang3.StringUtils;
import org.apache.dubbo.config.annotation.Reference;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * 描述信息：购方信息模糊查询，商品编码查询 ,开票人  发票限额，保存商品信息 公共服务接口
 *
 * @author 谢元强
 * @date Created on 2018-08-02
 */
@Slf4j
@Service
public class EInvoiceServiceImpl implements EInvoiceService {
    private static final String LOGGER_MSG = "WebSiteService实现类";
    
    @Reference
	private ApiOrderInvoiceInfoService apiOrderInvoiceInfoService;
    
    @Reference
    private ApiTaxEquipmentService apiTaxEquipmentService;

    @Reference
    private ApiHistoryDataPdfService historyDataPdfService;
    
    /**
     * @param invoiceCode 发票代码  invocieNo 发票号码
     * @return png文件的base64编码
     * @Description 下载 根据发票信息查看发票版式文件信息 并转化为file 数组
     * @Author xieyuanqiang
     * @Date 11:02 2018-08-02
     */
    @Override
    public List<FileDownLoad> queryInvoicePdfPath(String invoiceCode, String invoiceNo, String nsrsbh) {
        CommonOrderInvoiceAndOrderMxInfo commonOrderInvoiceAndOrderMxInfo = apiOrderInvoiceInfoService.selectOrderInvoiceInfoByFpdmFphmAndNsrsbh(invoiceCode, invoiceNo, NsrsbhUtils.transShListByNsrsbh(nsrsbh));
    
        // 根据税号获取税控设备信息
        String terminalCode = apiTaxEquipmentService.getTerminalCode(commonOrderInvoiceAndOrderMxInfo.getOrderInvoiceInfo().getXhfNsrsbh());
    
        /**
         * 区分电票和纸票,如果电票调用底层进行查询,如果是纸票本地生成文件.
         */
    
        List<FileDownLoad> fileDownLoadList = new ArrayList<>();
        //判断是否为导入的历史数据,或者是方格UKey的电票数据,调用mongodb查询数据
        boolean result = StringUtils.equals(OrderInfoEnum.ORDER_TYPE_6.getKey(),
                commonOrderInvoiceAndOrderMxInfo.getOrderInfo().getDdlx()) || (OrderInfoEnum.TAX_EQUIPMENT_FGUKEY.getKey().equals(terminalCode) && OrderInfoEnum.ORDER_INVOICE_TYPE_51.getKey().equals(commonOrderInvoiceAndOrderMxInfo.getOrderInvoiceInfo().getFpzlDm()));
        if (result) {
            HistoryDataPdfEntity historyDataPdfEntity = historyDataPdfService.find(commonOrderInvoiceAndOrderMxInfo.getOrderInvoiceInfo().getFpdm(),
                    commonOrderInvoiceAndOrderMxInfo.getOrderInvoiceInfo().getFphm(), NsrsbhUtils.transShListByNsrsbh(nsrsbh));
            if (Objects.isNull(historyDataPdfEntity)) {
                generatePdfFile(commonOrderInvoiceAndOrderMxInfo, invoiceCode, invoiceNo, fileDownLoadList);
            } else {
                FileDownLoad fileDownLoad = new FileDownLoad();
                fileDownLoad.setFileContent(historyDataPdfEntity.getPdfFileData());
                fileDownLoad.setFileName(historyDataPdfEntity.getFileName());
                String[] split = historyDataPdfEntity.getFileName().split(ConfigureConstant.STRING_POINT);
                String suffix = split[split.length - 1];
                fileDownLoad.setFileSuffix(ConfigureConstant.STRING_POINT + suffix);
                fileDownLoadList.add(fileDownLoad);
            }
        }else{
            if (OrderInfoEnum.ORDER_INVOICE_TYPE_51.getKey().equals(commonOrderInvoiceAndOrderMxInfo.getOrderInvoiceInfo().getFpzlDm())) {
                GetPdfRequest pdfRequestBean = HttpInvoiceRequestUtil.getPdfRequestBean(commonOrderInvoiceAndOrderMxInfo.getOrderInvoiceInfo().getKplsh().substring(0, commonOrderInvoiceAndOrderMxInfo.getOrderInvoiceInfo().getKplsh().length() - 3), commonOrderInvoiceAndOrderMxInfo.getOrderInvoiceInfo().getXhfNsrsbh(), terminalCode, invoiceCode, invoiceNo, commonOrderInvoiceAndOrderMxInfo.getOrderInvoiceInfo().getPdfUrl());
                //调用获取pdf的接口
                GetPdfResponseExtend pdf = HttpInvoiceRequestUtil.getPdf(OpenApiConfig.getPdfFg, OpenApiConfig.getPdf, pdfRequestBean, terminalCode);
                List<InvoicePdf> invoicePdfArrayList = pdf.getResponse_EINVOICE_PDF();
                for (InvoicePdf invoicePdf : invoicePdfArrayList) {
                    String suffix = ConfigureConstant.STRING_SUFFIX_PDF;
                    if (OrderInfoEnum.TAX_EQUIPMENT_NEWTAX.getKey().equals(terminalCode) || OrderInfoEnum.TAX_EQUIPMENT_UKEY.getKey().equals(terminalCode) || OrderInfoEnum.TAX_EQUIPMENT_FGUKEY.getKey().equals(terminalCode)) {
                        suffix = ConfigureConstant.STRING_SUFFIX_OFD;
                    }
                    FileDownLoad fileDownLoad = new FileDownLoad();
                    fileDownLoad.setFileContent(invoicePdf.getPDF_FILE());
                    fileDownLoad.setFileName(invoiceCode + "-" + invoiceNo + suffix);
                    fileDownLoad.setFileSuffix(suffix);
                    fileDownLoadList.add(fileDownLoad);
                }
            } else {
                //支持纸票自动生成pdf
                generatePdfFile(commonOrderInvoiceAndOrderMxInfo, invoiceCode, invoiceNo, fileDownLoadList);
            }
        }
        
        return fileDownLoadList;
    }
    
    /**
     * @Description 打印电票pdf
     * @Author xieyuanqiang
     * @Date 16:24 2018-08-29
     */
    @Override
    public List<byte[]> printInvoice(String[] invoiceCode, String[] invoiceNo, String[] shList) {
        List<byte[]> byteList = new ArrayList<>();
        for (int i = 0; i < invoiceCode.length; i++) {
            // 先根据发票代码发票号码查询发票信息
            CommonOrderInvoiceAndOrderMxInfo commonOrderInvoiceAndOrderMxInfo = apiOrderInvoiceInfoService.selectOrderInvoiceInfoByFpdmFphmAndNsrsbh(invoiceCode[i], invoiceNo[i], NsrsbhUtils.transShListByNsrsbh(shList[i]));
            
            if (commonOrderInvoiceAndOrderMxInfo != null) {
                if (OrderInfoEnum.ORDER_INVOICE_TYPE_51.getKey().equals(commonOrderInvoiceAndOrderMxInfo.getOrderInvoiceInfo().getFpzlDm())) {
                    // 根据税号获取税控设备信息
                    String terminalCode = apiTaxEquipmentService.getTerminalCode(commonOrderInvoiceAndOrderMxInfo.getOrderInvoiceInfo().getXhfNsrsbh());
                    GetPdfRequest pdfRequestBean = HttpInvoiceRequestUtil.getPdfRequestBean(commonOrderInvoiceAndOrderMxInfo.getOrderInvoiceInfo().getKplsh().substring(0, commonOrderInvoiceAndOrderMxInfo.getOrderInvoiceInfo().getKplsh().length() - 3), commonOrderInvoiceAndOrderMxInfo.getOrderInvoiceInfo().getXhfNsrsbh(), terminalCode, invoiceCode[i], invoiceNo[i], commonOrderInvoiceAndOrderMxInfo.getOrderInvoiceInfo().getPdfUrl());
    
                    //判断是否为导入的历史数据,或者是方格UKey的电票数据,调用mongodb查询数据
                    if (StringUtils.equals(OrderInfoEnum.ORDER_TYPE_6.getKey(),
                            commonOrderInvoiceAndOrderMxInfo.getOrderInfo().getDdlx()) || OrderInfoEnum.TAX_EQUIPMENT_FGUKEY.getKey().equals(terminalCode)) {
                        HistoryDataPdfEntity historyDataPdfEntity = historyDataPdfService.find(commonOrderInvoiceAndOrderMxInfo.getOrderInvoiceInfo().getFpdm(),
                                commonOrderInvoiceAndOrderMxInfo.getOrderInvoiceInfo().getFphm(), NsrsbhUtils.transShListByNsrsbh(commonOrderInvoiceAndOrderMxInfo.getOrderInvoiceInfo().getXhfNsrsbh()));
                        if (Objects.nonNull(historyDataPdfEntity)) {
                            byteList.add(Base64Encoding.decode(historyDataPdfEntity.getPdfFileData()));
                        }
                    } else {
                        //调用获取pdf的接口
                        GetPdfResponseExtend pdf = HttpInvoiceRequestUtil.getPdf(OpenApiConfig.getPdfFg, OpenApiConfig.getPdf, pdfRequestBean, terminalCode);
                        if (pdf != null && pdf.getResponse_EINVOICE_PDF() != null && pdf.getResponse_EINVOICE_PDF().size() > 0) {
                            for (InvoicePdf reBack : pdf.getResponse_EINVOICE_PDF()) {
                                byteList.add((Base64.decodeBase64(reBack.getPDF_FILE())));
                            }
                        }
                    }
                } else {
                    //支持纸票自动生成pdf
                    R createPdf = PDFProducer.createPdf(commonOrderInvoiceAndOrderMxInfo);
                    if (OrderInfoContentEnum.SUCCESS.getKey().equals(createPdf.get(OrderManagementConstant.CODE))) {
                        byteList.add((byte[]) createPdf.get(OrderManagementConstant.DATA));
                    }
                }
            }
	
	
        }
        return byteList;
    }
    
    /**
     * 生成发票PDF文件
     *
     * @param commonOrderInvoiceAndOrderMxInfo
     * @param invoiceCode                      发票代码
     * @param invoiceNo                        发票号码
     * @param fileDownLoads
     * @author <a href="tivenninesongs@163.com">yaoxuguang</a>
     * @date 2020/5/13
     */
    private void generatePdfFile(CommonOrderInvoiceAndOrderMxInfo commonOrderInvoiceAndOrderMxInfo,
                                 String invoiceCode, String invoiceNo, List<FileDownLoad> fileDownLoads) {
        
        R createPdf = PDFProducer.createPdf(commonOrderInvoiceAndOrderMxInfo);
        if (OrderInfoContentEnum.SUCCESS.getKey().equals(createPdf.get(OrderManagementConstant.CODE))) {
            FileDownLoad fileDownLoad = new FileDownLoad();
            fileDownLoad.setFileContent(Base64.encodeBase64String((byte[]) createPdf.get(OrderManagementConstant.DATA)));
            fileDownLoad.setFileName(invoiceCode + "-" + invoiceNo + ConfigureConstant.STRING_SUFFIX_PDF);
            fileDownLoad.setFileSuffix(ConfigureConstant.STRING_SUFFIX_PDF);
            fileDownLoads.add(fileDownLoad);
        }
    }
}
