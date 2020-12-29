package com.dxhy.order.consumer.modules.order.service;

import com.dxhy.order.constant.OrderSeparationException;
import com.dxhy.order.consumer.model.NewOrderExcel;
import com.dxhy.order.exceptions.OrderReceiveException;
import com.dxhy.order.file.exception.ExcelReadException;
import com.dxhy.order.model.CommonOrderInfo;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;
import java.util.List;
import java.util.Map;

/**
 * @author ：杨士勇
 * @ClassName ：ExcelReadService
 * @Description ：excel读取service
 * @date ：2018年9月11日 下午2:22:20
 */

public interface ExcelReadService {
    
    /**
     * 读取excel中的订单信息封装到invoiceexcel中
     *
     * @param file
     * @return
     * @throws OrderReceiveException
     * @throws ExcelReadException
     * @throws IOException
     */
    List<NewOrderExcel> readOrderInfoFromExcelxls(MultipartFile file) throws OrderReceiveException, ExcelReadException, IOException;
    
    /**
     * 校验订单信息
     *
     * @param readOrderInfoFromExcelxls
     * @param xhfNsrsbh
     * @return
     */
    Map<String, Object> examinByMap(Map<String, List<NewOrderExcel>> readOrderInfoFromExcelxls, String xhfNsrsbh);
    
    /**
     * 导出excel明细
     *
     * @param file
     * @param out
     * @param map
     * @param shList
     * @throws FileNotFoundException
     */
    void exportInvoiceDetailExcel(File file, OutputStream out, Map<String, Object> map, List<String> shList) throws FileNotFoundException;
    
    /**
     * 导出订单数据
     *
     * @param paramMap
     * @param outputStream
     * @param shList
     */
    void exportOrderInfo(Map<String, Object> paramMap, OutputStream outputStream, List<String> shList);
    
    /**
     * excel数据转换
     *
     * @param newOrderExcels
     * @param paramMap
     * @return
     * @throws UnsupportedEncodingException
     * @throws OrderReceiveException
     * @throws OrderSeparationException
     */
    List<CommonOrderInfo> excelToOrderInfo(Map<String, List<NewOrderExcel>> newOrderExcels, Map<String, String> paramMap) throws UnsupportedEncodingException, OrderReceiveException, OrderSeparationException;
}
