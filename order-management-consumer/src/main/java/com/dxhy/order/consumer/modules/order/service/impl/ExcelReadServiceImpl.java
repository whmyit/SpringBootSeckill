package com.dxhy.order.consumer.modules.order.service.impl;

import cn.hutool.core.util.RandomUtil;
import com.dxhy.order.api.*;
import com.dxhy.order.constant.*;
import com.dxhy.order.consumer.constant.ExcelErroMessageEnum;
import com.dxhy.order.consumer.constant.NewExcelEnum;
import com.dxhy.order.consumer.model.NewOrderExcel;
import com.dxhy.order.consumer.modules.order.service.ExcelReadService;
import com.dxhy.order.consumer.openapi.service.ICommonInterfaceService;
import com.dxhy.order.consumer.utils.ExcelUtils;
import com.dxhy.order.consumer.utils.GbkUtils;
import com.dxhy.order.exceptions.OrderReceiveException;
import com.dxhy.order.file.common.ExcelReadContext;
import com.dxhy.order.file.common.OrderImportExcelEnum;
import com.dxhy.order.file.exception.ExcelReadException;
import com.dxhy.order.file.handle.ExcelReadHandle;
import com.dxhy.order.model.*;
import com.dxhy.order.model.entity.BuyerEntity;
import com.dxhy.order.model.entity.CommodityCodeEntity;
import com.dxhy.order.model.entity.OilEntity;
import com.dxhy.order.model.entity.TaxClassCodeEntity;
import com.dxhy.order.utils.JsonUtils;
import com.dxhy.order.utils.PriceTaxSeparationUtil;
import com.dxhy.order.utils.StringConvertUtils;
import com.dxhy.order.utils.StringUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections4.map.HashedMap;
import org.apache.commons.lang3.StringUtils;
import org.apache.dubbo.config.annotation.Reference;
import org.apache.poi.hssf.util.HSSFColor;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.streaming.SXSSFSheet;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;
import org.apache.poi.xssf.usermodel.XSSFRichTextString;
import org.joda.time.DateTime;
import org.joda.time.Duration;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import java.io.*;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.Map.Entry;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author ：杨士勇
 * @ClassName ：ExcelReadServiceImpl
 * @Description ：excel读取实现类
 * @date ：2018年9月11日 下午2:23:28
 */
@Slf4j
@Service
public class ExcelReadServiceImpl implements ExcelReadService {
    
    private static final String LOGGER_MSG = "excel读取工具类";
    
    private static final DecimalFormat SLDF = new DecimalFormat("#0.00#");
    
    private static final Pattern EAMILPATTERN = Pattern.compile("^[a-zA-Z0-9]+$");
    
    private static final Pattern INTEGERPATTERN = Pattern.compile("^[0-9]*$");
    
    private static final Pattern EMAILPATTERN = Pattern.compile("^[a-z0-9A-Z]+[- | a-z0-9A-Z . _]+@([a-z0-9A-Z]+(-[a-z0-9A-Z]+)?\\.)+[a-z]{2,}$");
    
    private static final Pattern DDHPATTERN = Pattern.compile("^[0-9a-zA-Z_]+$");
    
    
    private static final Pattern DIGIT_PATTERN = Pattern.compile("^-?([0-9]\\d*(\\.\\d*)?|(0\\.\\d*)?[0-9]\\d*)$");
    
    private static final Pattern PHONE_PATTERN = Pattern.compile("^(\\d*-)(\\d*)$|^(\\d{4}-)(\\d{8})|\\d*$");
    
    private static final SimpleDateFormat SIMPLE_DATE_FORMAT = new SimpleDateFormat("yyy-MM-dd");
    
    private static final String[] HEADERS = {"订单号", "发票代码", "发票号码", "购方名称", "购方税号", "银行账号", "地址电话", "购方手机号", "发票类型", "作废标志", "开票时间", "合计金额(含税)",
            "合计金额(不含税)", "合计税额", "开票人", "开票类型", "业务类型", "发票备注", "第一行商品名称", "商品名称", "规格", "单位", "数量", "单价", "金额",
            "税率", "税额", "税收分类编码", "编码表版本号"};
    
    private static final String[] ORDER_HEADERS = {"订单号", "订单生成时间", "发票类型", "购方名称", "销方名称", "开票金额", "开票税额", "订单开票状态", "开票失败原因"};
    
    @Reference
    private ApiCommodityService apiCommodityService;
    
    @Reference
    private ApiOrderInvoiceInfoService apiOrderInvoiceInfoService;
    
    @Reference
    private ApiTaxClassCodeService apiTaxClassCodeService;
    
    @Reference
    private ApiOrderProcessService apiOrderProcessService;

    @Reference
    private ApiTaxEquipmentService apiTaxEquipmentService;

    @Resource
    private ICommonInterfaceService iCommonInterfaceService;

    @Reference
    private ApiBusinessTypeService apiBusinessTypeService;

    @Reference
    private ApiInvoiceCommonService apiInvoiceCommonMapperService;

    @Reference
    private ApiBuyerService apiBuyerService;
    
    
    /**
     *
     * @param file
     * @return
     * @throws OrderReceiveException
     */
    @Override
    public List<NewOrderExcel> readOrderInfoFromExcelxls(MultipartFile file) throws OrderReceiveException, ExcelReadException, IOException {

        Map<String, String> headToProperty = new HashMap<>(10);

        for(OrderImportExcelEnum flowStatus : OrderImportExcelEnum.values()){
            headToProperty.put(flowStatus.getKey(),flowStatus.getValue());
        }
        ExcelReadContext context = new ExcelReadContext(NewOrderExcel.class,headToProperty,true);
        if(StringUtils.isBlank(file.getOriginalFilename())){
            context.setFilePrefix(".xlsx");
        }else{
            context.setFilePrefix(file.getOriginalFilename().substring(file.getOriginalFilename().lastIndexOf(".")));
        }

        ExcelReadHandle handle = new ExcelReadHandle(context);

        List<NewOrderExcel> newOrderExcels = handle.readFromExcel(file.getInputStream(), NewOrderExcel.class);

        return newOrderExcels;

    }
    
    
    private static boolean checkRowNull(Row currentRow) {
        boolean b = true;
        for (Cell c : currentRow) {
            if (CellType.BLANK != c.getCellType() || StringUtils.isNotBlank(c.getStringCellValue())) {
                return false;
            }
        }
        return b;
    }
    
    /**
     * @param lastOrderExcel
     * @Title : examinData @Description
     * ：对excel导入的订单信息进行校验 @param @param orderExcel @param @return @return
     * Map<String,Object> @exception
     */
    private Map<String, Object> examinData(NewOrderExcel orderExcel, NewOrderExcel lastOrderExcel,String xhfNsrsbh) {
        log.info("Excel导入信息：{}", JsonUtils.getInstance().toJsonString(orderExcel));
        Map<String, Object> resultMap = new HashMap<>(10);
        boolean isValid = false;
        List<String> shList = new ArrayList<>();
        shList.add(xhfNsrsbh);
        List<Map<String, Object>> resultList = new ArrayList<>();
        // 订单号 非空校验 长度20
        if (StringUtils.isBlank(orderExcel.getDdh())) {
            isValid = true;
            resultList.add(buildReturnMap(orderExcel.getRowIndex(), NewExcelEnum.ORDER_DDH.getCellName(), ExcelErroMessageEnum.ORDERINFO_NULL, true));
        } else if (GbkUtils.getGbkLength(orderExcel.getDdh()) > OrderInfoContentEnum.STRING_FPKJ_DDH.getMaxLength()) {
            // resultList.add(buildReturnMap(orderExcel.getRowIndex(),
            // ExcelErroMessageEnum.SUCCESSCODE, false));
            isValid = true;
            resultList.add(
                    buildReturnMap(orderExcel.getRowIndex(), NewExcelEnum.ORDER_DDH.getCellName(), ExcelErroMessageEnum.ORDERINFO_DDH_LENGTH_ERROR, false));
        } else if (!DDHPATTERN.matcher(orderExcel.getDdh()).matches()) {
            isValid = true;
            resultList.add(
                    buildReturnMap(orderExcel.getRowIndex(), NewExcelEnum.ORDER_DDH.getCellName(), ExcelErroMessageEnum.ORDERINFO_DDHFORMAT_ERROR, false));
        }
    
        // 开票类型(开票种类代码) 必填
        if (StringUtils.isBlank(orderExcel.getFpzlDm())) {
            isValid = true;
            resultList.add(buildReturnMap(orderExcel.getRowIndex(), NewExcelEnum.ORDER_KPLX.getCellName(), ExcelErroMessageEnum.ORDERINFO_KPLX_NULL, false));
        } else if (ConfigureConstant.STRING_SPECIAL_INVOICE.equals(orderExcel.getFpzlDm())) {
            orderExcel.setFpzlDm(OrderInfoEnum.ORDER_INVOICE_TYPE_0.getKey());
    
        } else if (ConfigureConstant.STRING_ELE_INVOICE.equals(orderExcel.getFpzlDm())) {
            orderExcel.setFpzlDm(OrderInfoEnum.ORDER_INVOICE_TYPE_51.getKey());
        } else if (ConfigureConstant.STRING_PAPER_INVOICE.equals(orderExcel.getFpzlDm())) {
            orderExcel.setFpzlDm(OrderInfoEnum.ORDER_INVOICE_TYPE_2.getKey());
        } else {
            isValid = true;
            resultList.add(buildReturnMap(orderExcel.getRowIndex(), NewExcelEnum.ORDER_KPLX.getCellName(), ExcelErroMessageEnum.ORDERINFO_UNKNOW_KPLX, true));
        }
        //购方编码校验
        if (StringUtils.isNotBlank(orderExcel.getGhf_id()) && GbkUtils.getGbkLength(orderExcel.getGhf_id()) > OrderInfoContentEnum.CHECK_ISS7PRI_107286.getMaxLength()) {
            isValid = true;
            resultList.add(buildReturnMap(orderExcel.getRowIndex(), NewExcelEnum.ORDER_GHFMC.getCellName(), ExcelErroMessageEnum.ORDERINFO_GFBM_ERROR, true));
        }
        //购方邮箱校验
        if (StringUtils.isNotBlank(orderExcel.getGhf_yx()) && GbkUtils.getGbkLength(orderExcel.getGhf_yx()) > OrderInfoContentEnum.CHECK_ISS7PRI_107042.getMaxLength()) {
            isValid = true;
            resultList.add(buildReturnMap(orderExcel.getRowIndex(), NewExcelEnum.ORDER_GHFMC.getCellName(), ExcelErroMessageEnum.ORDERINFO_GFYX_ERROR, true));
        }
    
        if ((StringUtils.isNotBlank(orderExcel.getGhf_yx()) && !EMAILPATTERN.matcher(orderExcel.getGhf_yx()).matches())) {
            isValid = true;
            resultList.add(buildReturnMap(orderExcel.getRowIndex(), NewExcelEnum.ORDER_GHFMC.getCellName(), ExcelErroMessageEnum.ORDERINFO_GFYX_FORMAT_ERROR, true));
        
        }
        // 购货方企业类型 01 企业 03 个人
        if (StringUtils.isBlank(orderExcel.getGhf_qylx())) {
            isValid = true;
            resultList.add(buildReturnMap(orderExcel.getRowIndex(), NewExcelEnum.ORDER_GHFQYLX.getCellName(), ExcelErroMessageEnum.ORDERINFO_ENTERPRISE_TYPE_NULL,
                    false));
        } else {
            if (orderExcel.getGhf_qylx().equals(ConfigureConstant.STRING_GHF_QYLX_QY)) {
                orderExcel.setGhf_qylx(OrderInfoEnum.GHF_QYLX_01.getKey());
                /*if (StringUtils.isBlank(orderExcel.getGhf_nsrsbh())) {
                    resultList.add(buildReturnMap(orderExcel.getRowIndex(), NewExcelEnum.ORDER_GHFNSRSBH.getCellName(), ExcelErroMessageEnum.TAX_ERROR,
                            false));
                }*/
            } else if (orderExcel.getGhf_qylx().equals(ConfigureConstant.STRING_GHF_QYLX_GR)) {
                orderExcel.setGhf_qylx(OrderInfoEnum.GHF_QYLX_03.getKey());
                if (OrderInfoEnum.ORDER_INVOICE_TYPE_0.getKey().equals(orderExcel.getFpzlDm())) {
                    isValid = true;
                    resultList.add(buildReturnMap(orderExcel.getRowIndex(), NewExcelEnum.ORDER_GHFQYLX.getCellName(),
                            ExcelErroMessageEnum.ORDERINFO_SPECIAL_QYLX_ERROR, false));
                }
            } else {
                isValid = true;
                resultList.add(buildReturnMap(orderExcel.getRowIndex(), NewExcelEnum.ORDER_GHFQYLX.getCellName(),
                        ExcelErroMessageEnum.ORDERINFO_ENTERPRISE_TYPE_ERROR, false));
            }
        }
    
        // 购货方名称 100个字符以内
        if (!StringUtils.isBlank(orderExcel.getGhf_mc()) && GbkUtils.getGbkLength(orderExcel.getGhf_mc()) > ConfigureConstant.INT_100) {
            isValid = true;
            resultList.add(
                    buildReturnMap(orderExcel.getRowIndex(), NewExcelEnum.ORDER_GHFMC.getCellName(), ExcelErroMessageEnum.ORDERINFO_GHFMC_OVERLENGTH, false));
        }
    
    
        // 税号校验 数字字母组合
        if (StringUtils.isNotBlank(orderExcel.getGhf_nsrsbh())) {
            Matcher matcher = EAMILPATTERN.matcher(orderExcel.getGhf_nsrsbh());
            if (!matcher.matches()) {
                isValid = true;
                resultList.add(buildReturnMap(orderExcel.getRowIndex(), NewExcelEnum.ORDER_GHFNSRSBH.getCellName(), ExcelErroMessageEnum.TAX_ERROR, true));
                // 税号长度校验
            } else if (orderExcel.getGhf_nsrsbh().length() != Integer
                    .parseInt(OrderInfoEnum.TAXPAYER_ID_LENGTH_15.getKey())
                    && orderExcel.getGhf_nsrsbh().length() != Integer
                    .parseInt(OrderInfoEnum.TAXPAYER_ID_LENGTH_17.getKey())
                    && orderExcel.getGhf_nsrsbh().length() != Integer
                    .parseInt(OrderInfoEnum.TAXPAYER_ID_LENGTH_18.getKey())
                    && orderExcel.getGhf_nsrsbh().length() != Integer
                    .parseInt(OrderInfoEnum.TAXPAYER_ID_LENGTH_20.getKey())) {
                isValid = true;
                resultList.add(buildReturnMap(orderExcel.getRowIndex(), NewExcelEnum.ORDER_GHFNSRSBH.getCellName(), ExcelErroMessageEnum.TAX_LENGTH_ERROR, false));
            }
        }

        // 购货方地址 100
        if (StringUtils.isNotBlank(orderExcel.getGhf_dz()) && (GbkUtils.getGbkLength(orderExcel.getGhf_dz())) > ConfigureConstant.INT_100) {
            isValid = true;
            resultList.add(buildReturnMap(orderExcel.getRowIndex(), NewExcelEnum.ORDER_GHFDZ.getCellName(), ExcelErroMessageEnum.ORDERINFO_GHFDZ_LENGTH_ERROR, false));
        }
        // 购货方电话 20
        if (!StringUtils.isBlank(orderExcel.getGhf_dh()) && (GbkUtils.getGbkLength(orderExcel.getGhf_dh())) > ConfigureConstant.INT_20) {
            isValid = true;
            resultList.add(
                    buildReturnMap(orderExcel.getRowIndex(), NewExcelEnum.ORDER_GHFDH.getCellName(), ExcelErroMessageEnum.ORDERINFO_GHFDH_OVERLENGTH, false));
        }
        if (!StringUtils.isBlank(orderExcel.getGhf_dh()) && !PHONE_PATTERN.matcher(orderExcel.getGhf_dh()).matches()) {
            isValid = true;
            resultList.add(
                    buildReturnMap(orderExcel.getRowIndex(), NewExcelEnum.ORDER_GHFDH.getCellName(), ExcelErroMessageEnum.ORDERINFO_GHFDH_ERROR, false));
        }

        // 购货方地址 + 购货方电话 100位
        if (!StringUtils.isBlank(orderExcel.getGhf_dz()) && !StringUtils.isBlank(orderExcel.getGhf_dh()) && (GbkUtils.getGbkLength(orderExcel.getGhf_dz()) + GbkUtils.getGbkLength(orderExcel.getGhf_dh())) > ConfigureConstant.INT_100) {
            isValid = true;
            resultList.add(
                    buildReturnMap(orderExcel.getRowIndex(), NewExcelEnum.ORDER_GHFDZ.getCellName(), ExcelErroMessageEnum.ORDERINFO_GHFDZ_OVERLENGTH, false));
        }
    
        // 购货方银行账号 30位
        if (!StringUtils.isBlank(orderExcel.getGhf_zh())
                && GbkUtils.getGbkLength(orderExcel.getGhf_zh()) > 30) {
            isValid = true;
            resultList.add(
                    buildReturnMap(orderExcel.getRowIndex(), NewExcelEnum.ORDER_GHFZH.getCellName(), ExcelErroMessageEnum.ORDERINFO_GHFZH_OVERLENGTH, false));
        
        }
        //银行 100
        if (!StringUtils.isBlank(orderExcel.getGhf_yh()) && GbkUtils.getGbkLength(orderExcel.getGhf_yh()) > ConfigureConstant.INT_100) {
        
            isValid = true;
            resultList.add(
                    buildReturnMap(orderExcel.getRowIndex(), NewExcelEnum.ORDER_GHFYH.getCellName(), ExcelErroMessageEnum.ORDERINFO_GHFYH_OVERLENGTH, false));
        }
        // 购货方银行 + 购货方帐号 100位
        if (StringUtils.isNotBlank(orderExcel.getGhf_yh()) && StringUtils.isNotBlank(orderExcel.getGhf_zh()) && (GbkUtils.getGbkLength(orderExcel.getGhf_yh()) + GbkUtils.getGbkLength(orderExcel.getGhf_zh())) > ConfigureConstant.INT_100) {
            isValid = true;
            resultList.add(buildReturnMap(orderExcel.getRowIndex(), NewExcelEnum.ORDER_GHFDZ.getCellName(), ExcelErroMessageEnum.ORDERINFO_GHFYH_GHFZH_OVERLENGTH, false));
        }

    
        // 商品名称 70位 必填   ==>改成90 （19-03-28）
        if (StringUtils.isBlank(orderExcel.getXmmc())) {
            isValid = true;
            resultList.add(buildReturnMap(orderExcel.getRowIndex(), NewExcelEnum.ORDERITEM_XMMC.getCellName(), ExcelErroMessageEnum.ORDERINFO_XMMC_NULL, true));
        }
        if (!StringUtils.isBlank(orderExcel.getXmmc()) && GbkUtils.getGbkLength(orderExcel.getXmmc()) > ConfigureConstant.INT_90) {
            isValid = true;
            resultList.add(
                    buildReturnMap(orderExcel.getRowIndex(), NewExcelEnum.ORDERITEM_XMMC.getCellName(), ExcelErroMessageEnum.ORDERINFO_SPMC_OVERLENGTH, false));
        }
    
        // 规格型号 40位
        if (!StringUtils.isBlank(orderExcel.getGgxh()) && GbkUtils.getGbkLength(orderExcel.getGgxh()) > ConfigureConstant.INT_40) {
            isValid = true;
            resultList.add(
                    buildReturnMap(orderExcel.getRowIndex(), NewExcelEnum.ORDERITEM_GGXH.getCellName(), ExcelErroMessageEnum.ORDERINFO_GGXH_OVERLENGTH, false));
        
        }
    
        // 单位 20位
        if (!StringUtils.isBlank(orderExcel.getXmdw()) && GbkUtils.getGbkLength(orderExcel.getXmdw()) > ConfigureConstant.INT_20) {
            isValid = true;
            resultList
                    .add(buildReturnMap(orderExcel.getRowIndex(), NewExcelEnum.ORDERITEM_XMDW.getCellName(), ExcelErroMessageEnum.ORDERINFO_DW_OVERLENGTH, false));
        }
        if (apiTaxClassCodeService.queryOilBySpbm(orderExcel.getSpbm()) != null) {
            orderExcel.setCpy(true);
            //成品油单位只能为升或吨
            if (Double.parseDouble(orderExcel.getXmje()) > 0 && !ConfigureConstant.STRING_SHENG.equals(orderExcel.getXmdw()) && !ConfigureConstant.STRING_DUN.equals(orderExcel.getXmdw())) {
                isValid = true;
                resultList.add(
                        buildReturnMap(orderExcel.getRowIndex(), NewExcelEnum.ORDERITEM_XMDW.getCellName(), ExcelErroMessageEnum.ORDERINFO_9714, false));
            }
            
        }else{
        	orderExcel.setCpy(false);
        }
    
        // 数量 非必填项 只能为数字
        if (!StringUtils.isBlank(orderExcel.getXmsl())) {
    
            Matcher matcher1 = DIGIT_PATTERN.matcher(orderExcel.getXmsl());
            if (!matcher1.matches()) {
                isValid = true;
                resultList.add(buildReturnMap(orderExcel.getRowIndex(), NewExcelEnum.ORDERITEM_XMSL.getCellName(), ExcelErroMessageEnum.ORDERINFO_XMSL_ERROR, true));
            }
            String xmsl = orderExcel.getXmsl();
            if (xmsl.contains(ConfigureConstant.STRING_POINT)) {
                if (xmsl.substring(xmsl.indexOf(ConfigureConstant.STRING_POINT) + 1).length() > ConfigureConstant.INT_8) {
                    isValid = true;
                    resultList.add(buildReturnMap(orderExcel.getRowIndex(), NewExcelEnum.ORDERITEM_XMSL.getCellName(),
                            ExcelErroMessageEnum.ORDERINFO_XMSL_OVERLENGTHEIGHT, true));
                } else if (!StringUtils.isBlank(orderExcel.getXmsl()) && orderExcel.getXmsl().length() > ConfigureConstant.INT_20) {
                    isValid = true;
                    resultList.add(
                            buildReturnMap(orderExcel.getRowIndex(), NewExcelEnum.ORDERITEM_XMSL.getCellName(), ExcelErroMessageEnum.ORDERINFO_XMSL_OVERLENGTH, false));
                }
    
            } else if (!StringUtils.isBlank(orderExcel.getXmsl()) && orderExcel.getXmsl().length() > ConfigureConstant.INT_20) {
                isValid = true;
                resultList.add(
                        buildReturnMap(orderExcel.getRowIndex(), NewExcelEnum.ORDERITEM_XMSL.getCellName(), ExcelErroMessageEnum.ORDERINFO_XMSL_OVERLENGTH, false));
            }
        } else if (StringUtils.isNotEmpty(orderExcel.getXmje()) && Double.parseDouble(orderExcel.getXmje()) > 0 && apiTaxClassCodeService.queryOilBySpbm(orderExcel.getSpbm()) != null) {
            isValid = true;
            resultList.add(
                    buildReturnMap(orderExcel.getRowIndex(), NewExcelEnum.ORDERITEM_XMSL.getCellName(), ExcelErroMessageEnum.ORDERINFO_9716, false));
    
        }
    
        // 单价 24位
        if (!StringUtils.isBlank(orderExcel.getXmdj()) && GbkUtils.getGbkLength(orderExcel.getXmdj()) > ConfigureConstant.INT_20) {
            isValid = true;
            resultList
                    .add(buildReturnMap(orderExcel.getRowIndex(), NewExcelEnum.ORDERITEM_XMDJ.getCellName(), ExcelErroMessageEnum.ORDERINFO_DJ_OVERLENGTH, false));
        }
        // 单价 非必填项 仅可填写数字  若【数量】*【单价】≠【金额】，校验不允许通  若金额差异＜0.01元，则通过校验
        if (!StringUtils.isBlank(orderExcel.getXmdj())) {
            Matcher matcher2 = DIGIT_PATTERN.matcher(orderExcel.getXmdj());
            if (!matcher2.matches()) {
                isValid = true;
                resultList.add(buildReturnMap(orderExcel.getRowIndex(), NewExcelEnum.ORDERITEM_XMDJ.getCellName(), ExcelErroMessageEnum.ORDERINFO_DJ_ERROR, true));
            } else if (Double.parseDouble(orderExcel.getXmdj()) == 0) {
                isValid = true;
                resultList.add(buildReturnMap(orderExcel.getRowIndex(), NewExcelEnum.ORDERITEM_XMDJ.getCellName(), ExcelErroMessageEnum.ORDERINFO_DJ_ERROR, true));
            } else if (orderExcel.getXmdj().contains(ConfigureConstant.STRING_POINT) && (orderExcel.getXmdj().substring(orderExcel.getXmdj().indexOf(ConfigureConstant.STRING_POINT) + 1).length() > ConfigureConstant.INT_8)) {
    
                isValid = true;
                resultList.add(buildReturnMap(orderExcel.getRowIndex(), NewExcelEnum.ORDERITEM_XMDJ.getCellName(),
                        ExcelErroMessageEnum.ORDERINFO_XMDJ_OVERLENGTHEIGHT, true));
    
            } else if (!StringUtils.isBlank(orderExcel.getXmsl()) && !StringUtils.isBlank(orderExcel.getXmdj())
                    && !StringUtils.isBlank(orderExcel.getXmje())) {
                // resultList.add(buildReturnMap(orderExcel.getRowIndex(),
                // ExcelErroMessageEnum.SUCCESSCODE, false));
                try {
                    double je = Double.parseDouble(orderExcel.getXmsl()) * Double.parseDouble(orderExcel.getXmdj());
                    double ce = new BigDecimal(je).subtract(new BigDecimal(orderExcel.getXmje())).setScale(2, RoundingMode.HALF_UP).doubleValue();
    
                    if (Math.abs(ce) > ConfigureConstant.DOUBLE_PENNY) {
                        isValid = true;
                        resultList.add(buildReturnMap(orderExcel.getRowIndex(), NewExcelEnum.ORDERITEM_XMDJ.getCellName(), ExcelErroMessageEnum.ORDERINFO_XMDJ_ERROR,
                                true));
                    }
                } catch (NumberFormatException e) {
                    log.error("{}数字格式转换异常", LOGGER_MSG);
                }
            }
        }
    
        // 金额 必填项 非空校验  若用户在提交订单时未填写商品名称，校验时不允许通过  若填写内容包含非数字信息，校验不允许通过
        // 允许导入订单，但包含非数字信息的订单生成为异常订单。
        // 返回错误提示：数量填写有误
        if (!StringUtils.isBlank(orderExcel.getXmje()) && orderExcel.getXmje().length() > ConfigureConstant.INT_16) {
            isValid = true;
            resultList
                    .add(buildReturnMap(orderExcel.getRowIndex(), NewExcelEnum.ORDERITEM_XMJE.getCellName(), ExcelErroMessageEnum.ORDERINFO_JE_OVERLENGTH, false));
        }
        if (StringUtils.isBlank(orderExcel.getXmje())) {
            isValid = true;
            resultList.add(buildReturnMap(orderExcel.getRowIndex(), NewExcelEnum.ORDERITEM_XMJE.getCellName(), ExcelErroMessageEnum.ORDERINFO_JE_NULL, true));
        } else {
            //如果金额过大excel会转换为科学计数(1.0E7)，需要把科学计数转换为正常数据
            Matcher matcher3 = DIGIT_PATTERN.matcher(StringConvertUtils.convertScientificNotation(orderExcel.getXmje()));
            if (!matcher3.matches()) {
                isValid = true;
                resultList.add(buildReturnMap(orderExcel.getRowIndex(), NewExcelEnum.ORDERITEM_XMJE.getCellName(), ExcelErroMessageEnum.ORDERINFO_JE_ERROR, true));
            } else if (Double.parseDouble(orderExcel.getXmje()) < 0) {
        
        
                if (lastOrderExcel == null) {
                    isValid = true;
                    resultList.add(
                            buildReturnMap(orderExcel.getRowIndex(), ConfigureConstant.STRING_EXCEL_ZKH, ExcelErroMessageEnum.ORDERINFO_ZKH_ERROR, true));
                } else if (StringUtils.isNotBlank(orderExcel.getXmmc()) && !orderExcel.getXmmc().equals(lastOrderExcel.getXmmc())) {
                    isValid = true;
                    resultList.add(
                            buildReturnMap(orderExcel.getRowIndex(), ConfigureConstant.STRING_EXCEL_ZKH, ExcelErroMessageEnum.ORDERINFO_ZKH_ERROR, true));
                } else if (lastOrderExcel.getXmje() != null && Double.parseDouble(lastOrderExcel.getXmje()) <= ConfigureConstant.DOUBLE_PENNY_ZERO) {
                    isValid = true;
                    resultList.add(
                            buildReturnMap(orderExcel.getRowIndex(), ConfigureConstant.STRING_EXCEL_ZKH, ExcelErroMessageEnum.ORDERINFO_ZKH_ERROR, true));
                } else if ((Double.parseDouble(lastOrderExcel.getXmje()) + Double.parseDouble(orderExcel.getXmje())) < 0) {
                    isValid = true;
                    resultList.add(buildReturnMap(orderExcel.getRowIndex(), NewExcelEnum.ORDERITEM_XMJE.getCellName(), ExcelErroMessageEnum.ORDERINFO_ZKH_XMJE_ERROR, true));
                }else if(!orderExcel.getSpbm().equals(lastOrderExcel.getSpbm())){
                	isValid = true;
                    resultList.add(buildReturnMap(orderExcel.getRowIndex(), NewExcelEnum.ORDERITEM_SPBM.getCellName(), ExcelErroMessageEnum.ORDERINFO_ZKH_SPBM_ERROR, true));
                }else if(StringUtils.isNotBlank(lastOrderExcel.getSl()) && !orderExcel.getSl().equals(lastOrderExcel.getSl())){
                    isValid = true;
                    resultList.add(buildReturnMap(orderExcel.getRowIndex(), ConfigureConstant.STRING_EXCEL_ZKH, ExcelErroMessageEnum.ORDERINFO_ZKH_SL_ERROR, true));
                }
    
            }
        }
    
        // 含税标志
        if (StringUtils.isBlank(orderExcel.getHsbz())) {
            isValid = true;
            resultList.add(buildReturnMap(orderExcel.getRowIndex(), NewExcelEnum.ORDERITEM_HSBZ.getCellName(), ExcelErroMessageEnum.ORDERINFO_EXCEL_HSBZ_NULL, false));
        } else if (ConfigureConstant.STRING_HSBZ_HS.equals(orderExcel.getHsbz())) {
            orderExcel.setHsbz(OrderInfoEnum.HSBZ_1.getKey());
        } else if (ConfigureConstant.STRING_HSBZ_BHS.equals(orderExcel.getHsbz())) {
            orderExcel.setHsbz(OrderInfoEnum.HSBZ_0.getKey());
        } else {
            isValid = true;
            resultList.add(buildReturnMap(orderExcel.getRowIndex(), NewExcelEnum.ORDERITEM_HSBZ.getCellName(), ExcelErroMessageEnum.ORDERINFO_EXCEL_HSBZ_UNKNOWN, false));
        }
    
        // 税率
        if (!StringUtils.isBlank(orderExcel.getSl()) && orderExcel.getSl().length() > ConfigureConstant.INT_8) {
            isValid = true;
            resultList
                    .add(buildReturnMap(orderExcel.getRowIndex(), NewExcelEnum.ORDERITEM_SL.getCellName(), ExcelErroMessageEnum.ORDERINFO_SL_OVERLENGTH, false));
        }
        // 税率校验 税率为小数或者税率为%15的形式 否则校验失败
        if (!StringUtils.isBlank(orderExcel.getSl())) {
            if (ConfigureConstant.STRING_MS.equals(orderExcel.getSl()) || ConfigureConstant.STRING_BZS.equals(orderExcel.getSl())) {
            } else {
                String substring = orderExcel.getSl();
                if (orderExcel.getSl().contains(ConfigureConstant.STRING_PERCENT)) {
                    int index = orderExcel.getSl().indexOf(ConfigureConstant.STRING_PERCENT);
                    substring = orderExcel.getSl().substring(0, index);
                    if (!DIGIT_PATTERN.matcher(substring).matches()) {
                        isValid = true;
                        resultList.add(buildReturnMap(orderExcel.getRowIndex(), NewExcelEnum.ORDERITEM_SL.getCellName(),
                                ExcelErroMessageEnum.ORDERINFO_SL_FORMAT_ERROR, false));
                    } else if (Double.parseDouble(substring) / ConfigureConstant.INT_100 > 1) {
                        isValid = true;
                        resultList.add(buildReturnMap(orderExcel.getRowIndex(), NewExcelEnum.ORDERITEM_SL.getCellName(),
                                ExcelErroMessageEnum.ORDERINFO_SL_ERROR, false));
                    } else {
                        orderExcel.setSl(SLDF.format(Double.parseDouble(substring) / ConfigureConstant.INT_100));
                    }
                } else {
                    if (!DIGIT_PATTERN.matcher(substring).matches()) {
                        isValid = true;
                        resultList.add(buildReturnMap(orderExcel.getRowIndex(), NewExcelEnum.ORDERITEM_SL.getCellName(),
                                ExcelErroMessageEnum.ORDERINFO_SL_FORMAT_ERROR, false));
                    } else if (Double.parseDouble(substring) / ConfigureConstant.INT_100 > 1) {
                        isValid = true;
                        resultList.add(buildReturnMap(orderExcel.getRowIndex(), NewExcelEnum.ORDERITEM_SL.getCellName(),
                                ExcelErroMessageEnum.ORDERINFO_SL_ERROR, false));
                    } else {
                        orderExcel.setSl(SLDF.format(Double.valueOf(substring)));
                    }
                }
            }
            if (StringUtils.isBlank(orderExcel.getSpbm())) {
                Map<String, String> map = new HashedMap<>();
                map.put(ConfigureConstant.STRING_TAXPAYER_CODE, xhfNsrsbh);
                map.put("productName", orderExcel.getXmmc());
                if (StringUtils.isNotEmpty(orderExcel.getZxbm())) {
                    map.put("zxbm", orderExcel.getZxbm());
                }

                log.info("{}根据纳税人识别号和商品名称查询商品信息的接口,入参:{}", LOGGER_MSG, JsonUtils.getInstance().toJsonString(map));
                List<CommodityCodeEntity> queryProductList = apiCommodityService.queryProductList(map, shList);
                if (queryProductList.size() != 1) {
                    log.error("根据商品名称查到多个商品!");
                    isValid = true;
                    resultList.add(buildReturnMap(orderExcel.getRowIndex(), NewExcelEnum.ORDERITEM_SPBM.getCellName(),
                            ExcelErroMessageEnum.ORDERINFO_SSFLBM_ERROR, false));
                } else {
                    orderExcel.setSpbm(queryProductList.get(0).getTaxClassCode());
                }
            }
        }else{
			if (StringUtils.isBlank(orderExcel.getSpbm())) {
                Map<String, String> map = new HashedMap<>();
                map.put(ConfigureConstant.STRING_TAXPAYER_CODE, xhfNsrsbh);
                map.put("productName", orderExcel.getXmmc());
                if (StringUtils.isNotEmpty(orderExcel.getZxbm())) {
                    map.put("zxbm", orderExcel.getZxbm());
                }
                
                log.info("{}根据纳税人识别号和商品名称查询商品信息的接口,入参:{}", LOGGER_MSG, JsonUtils.getInstance().toJsonString(map));
                List<CommodityCodeEntity> queryProductList = apiCommodityService.queryProductList(map, shList);
                if (queryProductList.size() != 1) {
                    log.error("根据商品名称查到多个商品!");
                    isValid = true;
                    resultList.add(buildReturnMap(orderExcel.getRowIndex(), NewExcelEnum.ORDERITEM_SL.getCellName(),
                            ExcelErroMessageEnum.ORDERINFO_SSFLBM_SL_NULL, false));
                    resultList.add(buildReturnMap(orderExcel.getRowIndex(), NewExcelEnum.ORDERITEM_SPBM.getCellName(),
                            ExcelErroMessageEnum.ORDERINFO_SSFLBM_ERROR, false));

                } else {
                    orderExcel.setSpbm(queryProductList.get(0).getTaxClassCode());
                    orderExcel.setSl(StringUtil.formatSl(queryProductList.get(0).getTaxRate()));
                }
            }
        }
    
        //税额 非必填 必须为数字，最多20位 0.06误差
        if (!StringUtils.isBlank(orderExcel.getSe())) {
    
            Matcher matcher3 = DIGIT_PATTERN.matcher(orderExcel.getSe());
            if (!matcher3.matches()) {
                isValid = true;
                resultList.add(buildReturnMap(orderExcel.getRowIndex(), NewExcelEnum.ORDERITEM_SE.getCellName(), ExcelErroMessageEnum.ORDERINFO_9704, false));
            }
            if (orderExcel.getSe().length() > ConfigureConstant.INT_20) {
                isValid = true;
                resultList.add(buildReturnMap(orderExcel.getRowIndex(), NewExcelEnum.ORDERITEM_SE.getCellName(), ExcelErroMessageEnum.ORDERINFO_9703, false));
            }
    
            //1.判断含税标志，上面已经判断转换过，此处判断转换过的数据
            if(StringUtils.isNotBlank(orderExcel.getSl())){
            	double bhsje;
                // 关于
                if (OrderInfoEnum.HSBZ_1.getKey().equals(orderExcel.getHsbz())) {
                    bhsje = Double.parseDouble(orderExcel.getXmje()) / (1 + Double.parseDouble(orderExcel.getSl()));
                } else {
                    bhsje = Double.parseDouble(orderExcel.getXmje());
                }
                double se = bhsje * Double.parseDouble(orderExcel.getSl());
                //2.判断计算的税额与输入的税额差值是否大于0.06
                try {
                    if (Math.abs(se - Double.parseDouble(orderExcel.getSe())) > ConfigureConstant.DOUBLE_PENNY_SIX) {
                        isValid = true;
                        resultList.add(buildReturnMap(orderExcel.getRowIndex(), NewExcelEnum.ORDERITEM_SE.getCellName(), ExcelErroMessageEnum.ORDERINFO_9704, false));
                    }
                } catch (NumberFormatException e) {
                    log.error("数据格式化double失败,异常为{}",e);
                }
            }
        }
    
        //编码版本 10位
        if (!StringUtils.isBlank(orderExcel.getBmbbbh()) && orderExcel.getBmbbbh().length() > ConfigureConstant.INT_10) {
            isValid = true;
            resultList.add(buildReturnMap(orderExcel.getRowIndex(), NewExcelEnum.ORDERITEM_BMBBBH.getCellName(), ExcelErroMessageEnum.ORDERINFO_9702, false));
        }
    
        // 税收分类编码 非必填项 仅可输入数字，导入时校验 若填写的编码非19位，校验不允许通过
        if (!StringUtils.isBlank(orderExcel.getSpbm())) {
            Matcher matcher4 = DIGIT_PATTERN.matcher(orderExcel.getSpbm());
            if (!matcher4.matches()) {
                isValid = true;
                resultList.add(
                        buildReturnMap(orderExcel.getRowIndex(), NewExcelEnum.ORDERITEM_SPBM.getCellName(), ExcelErroMessageEnum.ORDERINFO_SSFLBM_ERROR, true));
            } else if (orderExcel.getSpbm().length() > ConfigureConstant.INT_19) {
    
                isValid = true;
                resultList.add(buildReturnMap(orderExcel.getRowIndex(), NewExcelEnum.ORDERITEM_SPBM.getCellName(),
                        ExcelErroMessageEnum.ORDERINFO_SSFLBM_LENGTH_EROR, true));
            } else if (orderExcel.getSpbm().length() < ConfigureConstant.INT_19) {
    
                orderExcel.setSpbm(StringUtil.fillZero(orderExcel.getSpbm(), 19));
            }
            TaxClassCodeEntity qtc = apiTaxClassCodeService.queryTaxClassCodeEntity(orderExcel.getSpbm());
            if (qtc == null) {
                // resultList.add(buildReturnMap(orderExcel.getRowIndex(),
                // ExcelErroMessageEnum.SUCCESSCODE, false));
                isValid = true;
                resultList.add(
                        buildReturnMap(orderExcel.getRowIndex(), NewExcelEnum.ORDERITEM_SPBM.getCellName(), ExcelErroMessageEnum.ORDERINFO_SSFLBM_ERROR, true));

                if(StringUtils.isBlank(orderExcel.getSl())){

                    if (qtc != null && StringUtils.isNotBlank(qtc.getSl())) {
                        orderExcel.setSl(StringUtil.formatSl(qtc.getSl()));
                    } else {
                        isValid = true;
                        resultList.add(buildReturnMap(orderExcel.getRowIndex(), NewExcelEnum.ORDERITEM_SL.getCellName(),
                                ExcelErroMessageEnum.ORDERINFO_SSFLBM_SL_NULL, false));
                    }
                }
        
            }
        }
        
        //零税率标识转换
        if(StringUtils.isNotBlank(orderExcel.getLslbs())){
        	if(OrderInfoEnum.LSLBS_0.getValue().equals(orderExcel.getLslbs())){
        		orderExcel.setLslbs(OrderInfoEnum.LSLBS_0.getKey());
        	}else if(OrderInfoEnum.LSLBS_1.getValue().equals(orderExcel.getLslbs())){
        		orderExcel.setLslbs(OrderInfoEnum.LSLBS_1.getKey());
        	}else if(OrderInfoEnum.LSLBS_2.getValue().equals(orderExcel.getLslbs())){
        		orderExcel.setLslbs(OrderInfoEnum.LSLBS_2.getKey());
        	}else if(OrderInfoEnum.LSLBS_3.getValue().equals(orderExcel.getLslbs())){
        		orderExcel.setLslbs(OrderInfoEnum.LSLBS_3.getKey());
        	}else{
        		isValid = true;
                resultList.add(buildReturnMap(orderExcel.getRowIndex(), NewExcelEnum.ORDERITEM_LSLBS.getCellName(), ExcelErroMessageEnum.ORDERINFO_9705, false));
        	}
        }
    
        //优惠政策标识  增值税特殊管理  零税率标识  校验
        String yhzcbs = orderExcel.getYhzcbs();
        String zzstsgl = orderExcel.getZzstsgl();
        String lslbs = orderExcel.getLslbs();
        //增值税特殊管理长度不能超过50位
        if (!StringUtils.isBlank(zzstsgl) && GbkUtils.getGbkLength(zzstsgl) > ConfigureConstant.INT_50) {
            isValid = true;
            resultList.add(buildReturnMap(orderExcel.getRowIndex(), NewExcelEnum.ORDERITEM_ZZSTSGL.getCellName(), ExcelErroMessageEnum.ORDERINFO_9713, false));
        }
        //优惠政策标识只能为0或者1 必填
        if ((ConfigureConstant.STRING_YHZCBS_F.equals(yhzcbs))) {
            orderExcel.setYhzcbs(OrderInfoEnum.YHZCBS_0.getKey());
            yhzcbs = OrderInfoEnum.YHZCBS_0.getKey();
        } else if (ConfigureConstant.STRING_YHZCBS_S.equals(yhzcbs)) {
            orderExcel.setYhzcbs(OrderInfoEnum.YHZCBS_1.getKey());
            yhzcbs = OrderInfoEnum.YHZCBS_1.getKey();
        } else {
            isValid = true;
            resultList.add(buildReturnMap(orderExcel.getRowIndex(), NewExcelEnum.ORDERITEM_YHZCBS.getCellName(), ExcelErroMessageEnum.ORDERINFO_9707, false));
        }
    
        //优惠政策标识为1时;
        if (OrderInfoEnum.YHZCBS_1.getKey().equals(yhzcbs)) {
            if (StringUtils.isBlank(zzstsgl)) {
                isValid = true;
                resultList.add(buildReturnMap(orderExcel.getRowIndex(), NewExcelEnum.ORDERITEM_ZZSTSGL.getCellName(), ExcelErroMessageEnum.ORDERINFO_9708, false));
            }
            //订单明细信息中YHZCBS(优惠政策标识)为1, 且税率为0, 则LSLBS只能根据实际情况选择"0或1或2"中的一种, 不能选择3, 且ZZSTSGL内容也只能写与0/1/2对应的"出口零税/免税/不征税
            if (!StringUtils.isBlank(orderExcel.getSl()) &&
                    ConfigureConstant.STRING_000.equals(orderExcel.getSl()) &&
                    !OrderInfoEnum.LSLBS_0.getKey().equals(lslbs) &&
                    !OrderInfoEnum.LSLBS_1.getKey().equals(lslbs) &&
                    !OrderInfoEnum.LSLBS_2.getKey().equals(lslbs) &&
                    (StringUtils.isBlank(zzstsgl))) {
                isValid = true;
                resultList.add(buildReturnMap(orderExcel.getRowIndex(), NewExcelEnum.ORDERITEM_LSLBS.getCellName(), ExcelErroMessageEnum.ORDERINFO_9706, false));
            }
    
        }
        //订单明细信息中优惠政策标识为0时,增值税特殊管理须为空
        if (OrderInfoEnum.YHZCBS_0.getKey().equals(yhzcbs)) {
            if (!StringUtils.isBlank(zzstsgl)) {
                isValid = true;
                resultList.add(buildReturnMap(orderExcel.getRowIndex(), NewExcelEnum.ORDERITEM_ZZSTSGL.getCellName(), ExcelErroMessageEnum.ORDERINFO_9708, false));
            }
        }
    
        if (!StringUtils.isBlank(lslbs)) {
            //零税率标识如果非空必须为0/1/2/3
            if (!((OrderInfoEnum.LSLBS_0.getKey().equals(lslbs)) || (OrderInfoEnum.LSLBS_1.getKey().equals(lslbs))
                    || (OrderInfoEnum.LSLBS_2.getKey().equals(lslbs)) || (OrderInfoEnum.LSLBS_3.getKey().equals(lslbs)))) {
                isValid = true;
                resultList.add(buildReturnMap(orderExcel.getRowIndex(), NewExcelEnum.ORDERITEM_LSLBS.getCellName(), ExcelErroMessageEnum.ORDERINFO_9705, false));
            }
        }
        /**
         * 税率非空时,逻辑判断
         */
        if (!StringUtils.isBlank(orderExcel.getSl())) {
            /**
             * 增值税特殊管理不为空,不为不征税,不为免税,不为出口零税逻辑处理
             * 如果是按5%简易征收需要保证税率为0.05
             * 如果是按3%简易征收需要保证税率为0.03
             * 如果是简易征收需要保证税率为0.03或0.04或0.05
             * 如果是按5%简易征收减按1.5%计征需要保证税率为0.015
             */
            if ((!StringUtils.isBlank(zzstsgl)) &&
                    (!ConfigureConstant.STRING_BZS.equals(zzstsgl)) &&
                    (!ConfigureConstant.STRING_MS.equals(zzstsgl)) &&
                    (!ConfigureConstant.STRING_CKLS.equals(zzstsgl))) {
    
                if (zzstsgl.contains(ConfigureConstant.STRING_ERROR_PERCENT)) {
                    resultList.add(buildReturnMap(orderExcel.getRowIndex(), NewExcelEnum.ORDERITEM_SL.getCellName(), ExcelErroMessageEnum.ORDERINFO_9720, false));
                }
                switch (zzstsgl) {
                    case ConfigureConstant.STRING_JYZS5:
                        if (new BigDecimal(orderExcel.getSl()).doubleValue() != ConfigureConstant.DOUBLE_PENNY_5) {
                            isValid = true;
                            resultList.add(buildReturnMap(orderExcel.getRowIndex(), NewExcelEnum.ORDERITEM_SL.getCellName(), ExcelErroMessageEnum.ORDERINFO_9711, false));
                        }
                        break;
                    case ConfigureConstant.STRING_JYZS3:
                        if (new BigDecimal(orderExcel.getSl()).doubleValue() != ConfigureConstant.DOUBLE_PENNY_3) {
                            isValid = true;
                            resultList.add(buildReturnMap(orderExcel.getRowIndex(), NewExcelEnum.ORDERITEM_SL.getCellName(), ExcelErroMessageEnum.ORDERINFO_9711, false));
                        }
                        break;
                    case ConfigureConstant.STRING_JYZS:
                        if (new BigDecimal(orderExcel.getSl()).doubleValue() != ConfigureConstant.DOUBLE_PENNY_3 || new BigDecimal(orderExcel.getSl()).doubleValue() != ConfigureConstant.DOUBLE_PENNY_4 || new BigDecimal(orderExcel.getSl()).doubleValue() != ConfigureConstant.DOUBLE_PENNY_5) {
                            isValid = true;
                            resultList.add(buildReturnMap(orderExcel.getRowIndex(), NewExcelEnum.ORDERITEM_SL.getCellName(), ExcelErroMessageEnum.ORDERINFO_9711, false));
                        }
                        break;
                    case ConfigureConstant.STRING_JYZS5_1:
                        if (new BigDecimal(orderExcel.getSl()).compareTo(new BigDecimal(ConfigureConstant.STRING_0015)) != 0) {
                            isValid = true;
                            resultList.add(buildReturnMap(orderExcel.getRowIndex(), NewExcelEnum.ORDERITEM_SL.getCellName(), ExcelErroMessageEnum.ORDERINFO_9711, false));
                        }
                        break;
                    default:
                        break;
                }
            }
    
            //零税率标识不为空,税率必须为0
            if ((!StringUtils.isBlank(lslbs)) && (ConfigureConstant.DOUBLE_PENNY_ZERO != new BigDecimal(orderExcel.getSl()).doubleValue())) {
                isValid = true;
                resultList.add(buildReturnMap(orderExcel.getRowIndex(), NewExcelEnum.ORDERITEM_SL.getCellName(), ExcelErroMessageEnum.ORDERINFO_9709, false));
            }
            //零税率标识为空,税率不能为0
            if ((StringUtils.isBlank(lslbs)) && (ConfigureConstant.DOUBLE_PENNY_ZERO == new BigDecimal(orderExcel.getSl()).doubleValue())) {
                isValid = true;
                resultList.add(buildReturnMap(orderExcel.getRowIndex(), NewExcelEnum.ORDERITEM_SL.getCellName(), ExcelErroMessageEnum.ORDERINFO_9709, false));
            }
        }
    
    
        //订单明细信息中零税率标识为0/1/2, 但增值税特殊管理内容不为'出口零税/免税/不征税';
        boolean result = StringUtils.isBlank(zzstsgl) &&
                (OrderInfoEnum.LSLBS_0.getKey().equals(lslbs) ||
                        OrderInfoEnum.LSLBS_1.getKey().equals(lslbs) ||
                        OrderInfoEnum.LSLBS_2.getKey().equals(lslbs));
        if (result) {
            isValid = true;
            resultList.add(buildReturnMap(orderExcel.getRowIndex(), NewExcelEnum.ORDERITEM_ZZSTSGL.getCellName(), ExcelErroMessageEnum.ORDERINFO_9710, false));
        } else {
            if (OrderInfoEnum.LSLBS_0.getKey().equals(lslbs) && !ConfigureConstant.STRING_CKLS.equals(zzstsgl)) {
                isValid = true;
                resultList.add(buildReturnMap(orderExcel.getRowIndex(), NewExcelEnum.ORDERITEM_ZZSTSGL.getCellName(), ExcelErroMessageEnum.ORDERINFO_9710, false));
            }
            if (OrderInfoEnum.LSLBS_1.getKey().equals(lslbs) && !ConfigureConstant.STRING_MS.equals(zzstsgl)) {
                isValid = true;
                resultList.add(buildReturnMap(orderExcel.getRowIndex(), NewExcelEnum.ORDERITEM_ZZSTSGL.getCellName(), ExcelErroMessageEnum.ORDERINFO_9710, false));
            }
            if (OrderInfoEnum.LSLBS_2.getKey().equals(lslbs) && !ConfigureConstant.STRING_BZS.equals(zzstsgl)) {
                isValid = true;
                resultList.add(buildReturnMap(orderExcel.getRowIndex(), NewExcelEnum.ORDERITEM_ZZSTSGL.getCellName(), ExcelErroMessageEnum.ORDERINFO_9710, false));
            }
            if (OrderInfoEnum.LSLBS_3.getKey().equals(lslbs) && (!StringUtils.isBlank(zzstsgl))) {
                isValid = true;
                resultList.add(buildReturnMap(orderExcel.getRowIndex(), NewExcelEnum.ORDERITEM_ZZSTSGL.getCellName(), ExcelErroMessageEnum.ORDERINFO_9710, false));
            }
            if (OrderInfoEnum.LSLBS_3.getKey().equals(lslbs) && (!(OrderInfoEnum.YHZCBS_0.getKey().equals(yhzcbs)))) {
                isValid = true;
                resultList.add(buildReturnMap(orderExcel.getRowIndex(), NewExcelEnum.ORDERITEM_ZZSTSGL.getCellName(), ExcelErroMessageEnum.ORDERINFO_9712, false));
            }
        }
    
    
        // 自行编码
        if (!StringUtils.isBlank(orderExcel.getZxbm()) && GbkUtils.getGbkLength(orderExcel.getZxbm()) > ConfigureConstant.INT_16) {
            isValid = true;
            resultList
                    .add(buildReturnMap(orderExcel.getRowIndex(), NewExcelEnum.ORDERITEM_ZXBM.getCellName(), ExcelErroMessageEnum.ORDERINFO_9701, false));
        }
    
        // 备注 150
        if (!StringUtils.isBlank(orderExcel.getBz()) && GbkUtils.getGbkLength(orderExcel.getBz()) > ConfigureConstant.INT_150) {
            isValid = true;
            resultList.add(
                    buildReturnMap(orderExcel.getRowIndex(), NewExcelEnum.ORDER_BZ.getCellName(), ExcelErroMessageEnum.ORDERINFO_BZ_OVERLENGTHEIGHT, false));
        }
        
        //业务类型校验
        if (!StringUtils.isBlank(orderExcel.getYwlx())) {
            if (GbkUtils.getGbkLength(orderExcel.getYwlx()) > OrderInfoContentEnum.CHECK_ISS7PRI_107294.getMaxLength()) {
                isValid = true;
                resultList.add(
                        buildReturnMap(orderExcel.getRowIndex(), NewExcelEnum.ORDERINFO_YWLX.getCellName(), ExcelErroMessageEnum.ORDERINFO_YWLX_OVERLENGTH, false));
            }
    
        }
    
        resultMap.put("isValid", isValid);
        resultMap.put("resultList", resultList);
        resultMap.put("rowIndex", orderExcel.getRowIndex());
        return resultMap;
    }
    
    
    public static Map<String, Object> buildReturnMap(String rowIndex, String cellName, ExcelErroMessageEnum errorEnum,
                                                     boolean isShowRowNum) {
        Map<String, Object> resultMap = new HashMap<>(10);
        resultMap.put(ConfigureConstant.CELL_NAME, cellName);
        resultMap.put(OrderManagementConstant.CODE, errorEnum.getKey());
        resultMap.put(OrderManagementConstant.MESSAGE, errorEnum.getValue());
        if (isShowRowNum) {
            resultMap.put(OrderManagementConstant.MESSAGE, String.format(errorEnum.getValue(), rowIndex));
        } else {
            resultMap.put(OrderManagementConstant.MESSAGE, errorEnum.getValue());
        }
    
        return resultMap;
    }
    
    public static boolean isCellNull(Cell cell) {
        
        if (cell == null) {
            return true;
        }
        if (CellType.STRING == cell.getCellType()) {
            return cell.getStringCellValue() == null;
        } else if (CellType.BLANK == cell.getCellType()) {
            return cell.getStringCellValue() == null;
        }
        return false;
    }
    
    public static String getStringCell(Cell cell,FormulaEvaluator createFormulaEvaluator) {
        
        String temp = "";
        CellType cellTypeEnum = cell.getCellType();
        switch (cellTypeEnum) {
            case STRING:
                temp = cell.getStringCellValue().trim();
                temp = StringUtils.isEmpty(temp) ? "" : temp;
                break;
            case NUMERIC:
                temp = new DecimalFormat("#.########").format(cell.getNumericCellValue());
                break;
            case FORMULA:
                try {
                    double numericCellValue = cell.getNumericCellValue();
                    temp = new BigDecimal(numericCellValue).setScale(2, RoundingMode.HALF_UP).toString();
                } catch (IllegalStateException e) {
                    temp = String.valueOf(cell.getRichStringCellValue());
                }
                break;
            case BLANK:
                temp = "";
                break;
            case BOOLEAN:
                temp = String.valueOf(cell.getBooleanCellValue());
                break;
            case ERROR:
                temp = "";
                break;
            default:
                temp = "";
    
        }
        return temp;
    }
    
    
    /**
     * 校验订单信息
     *
     * @param readOrderInfoFromExcelxls
     * @param xhfNsrsbh
     * @return
     */
    @Override
    public Map<String, Object> examinByMap(Map<String, List<NewOrderExcel>> readOrderInfoFromExcelxls, String xhfNsrsbh) {
        Map<String, Object> resultMap = new HashMap<>(5);
        List<Map<String, Object>> errorList = new ArrayList<>();
        boolean isPass = true;
        //List<String> ddhList = new ArrayList<String>();
        
        for (Entry<String, List<NewOrderExcel>> entry : readOrderInfoFromExcelxls.entrySet()) {
            List<NewOrderExcel> value = entry.getValue();
            NewOrderExcel lastOrderExcel = null;
            boolean isValid = false;
            boolean isContainsCpySpbm = false;
            //第一条明细是否为成品油商品编码，如果是就认为是成品油订单
            if (StringUtils.isNotBlank(value.get(0).getSpbm()) && apiTaxClassCodeService.queryOilBySpbm(value.get(0).getSpbm()) != null) {
                isContainsCpySpbm = true;
            }

            int i = 0;
            BigDecimal hjje = null;
            for (NewOrderExcel newOrderExcel : value) {
                Map<String, Object> examinData = examinData(newOrderExcel, lastOrderExcel,xhfNsrsbh);
                List<Object> resultList = (List<Object>) examinData.get("resultList");
                if (i == 0 && OrderInfoEnum.ORDER_INVOICE_TYPE_51.getKey().equals(value.get(0).getFpzlDm()) && value.size() > 1000) {
                    isValid = true;
                    resultList.add(
                            buildReturnMap(newOrderExcel.getRowIndex(), "订单明细", ExcelErroMessageEnum.ORDERINFO_ELE_ITEM_OVERLIMIT, false));
                }
                if (isContainsCpySpbm && value.size() > 8 && i == value.size() - 1) {
                    isValid = true;
                    resultList.add(
                            buildReturnMap(newOrderExcel.getRowIndex(), "订单明细", ExcelErroMessageEnum.ORDERINFO_9717, false));
                }
                OilEntity queryOilBySpbm = apiTaxClassCodeService.queryOilBySpbm(newOrderExcel.getSpbm());
                boolean result = (isContainsCpySpbm && queryOilBySpbm == null) ||
                        (!isContainsCpySpbm && queryOilBySpbm != null);
                if (result) {
                    //成品油商品 不可以添加非成品油的商品编码
                    isValid = true;
                    resultList.add(
                            buildReturnMap(newOrderExcel.getRowIndex(), NewExcelEnum.ORDERITEM_SPBM.getCellName(), ExcelErroMessageEnum.ORDERINFO_9715, false));
                }
                examinData.put("resultList", resultList);
                if ((boolean) examinData.get("isValid") || isValid) {
                    isPass = false;
                    errorList.add(examinData);
                } else {
                    if (hjje == null) {
                        hjje = new BigDecimal("0.00");
                    }
                    hjje = hjje.add(new BigDecimal(Double.parseDouble(newOrderExcel.getXmje())));
                    errorList.add(examinData);
                }
                i++;
                lastOrderExcel = newOrderExcel;
            }
            if(hjje != null && hjje.doubleValue() == 0.00) {
                Map<String, Object> buildReturnMap = buildReturnMap(value.get(0).getRowIndex(), NewExcelEnum.ORDERITEM_XMJE.getCellName(), ExcelErroMessageEnum.ORDER_JE_9718, false);
                List<Object> resultList = (List<Object>) errorList.get(errorList.size() - 1).get("resultList");
                resultList.add(buildReturnMap);
                isPass = false;
            }

            //校验excel中的订单号是否重复
            /*if(!ddhList.isEmpty()){
                if(ddhList.contains(value.get(0).getDdh())){
                    Map<String, Object> buildReturnMap = buildReturnMap(value.get(0).getRowIndex(), NewExcelEnum.ORDER_DDH.getCellName(), ExcelErroMessageEnum.ORDERINFO_DDH_EXCEL_REPEAT_ERROR, false);
                    List<Object> resultList = (List<Object>) errorList.get(errorList.size() - 1).get("resultList");
                    resultList.add(buildReturnMap);
                    isPass = false;

                }
            }*/

            //查询税号下订单号是否存在
           /* List<String> shList = new ArrayList<>();
            shList.add(xhfNsrsbh);
    
            Map<String, Object> paramMap = new HashMap<>(10);
            paramMap.put("ddh",value.get(0).getDdh());
            paramMap.put("orderStatus",OrderInfoEnum.ORDER_VALID_STATUS_0.getKey());
            if(apiOrderProcessService.isExistNoAuditOrder(paramMap,shList)){
                Map<String, Object> buildReturnMap = buildReturnMap(value.get(0).getRowIndex(), NewExcelEnum.ORDER_DDH.getCellName(), ExcelErroMessageEnum.ORDERINFO_DDH_REPEAT_ERROR, false);
                List<Object> resultList = (List<Object>) errorList.get(errorList.size() - 1).get("resultList");
                resultList.add(buildReturnMap);
                isPass = false;
            }*/
            //ddhList.add(value.get(0).getDdh());
            //判断订单号是否重复
        }
        resultMap.put("isPass", isPass);
        resultMap.put(OrderManagementConstant.DATA, errorList);
        return resultMap;
    }
    
    /**
     * excel导出重写 update By ysy
     */
    @Override
    public void exportInvoiceDetailExcel(File file, OutputStream out, Map<String, Object> map, List<String> shList)
            throws FileNotFoundException {
        final DateTime startTime = DateTime.now();
        // 创建一个工作簿
        FileOutputStream inputOut = new FileOutputStream(file);
        
        final DateTime endTime = DateTime.now();
        final long time = new Duration(startTime, endTime).getMillis();
        log.debug("{},创建文件耗时:{}毫秒", LOGGER_MSG, time);
        SXSSFWorkbook sxssfWorkbook = new SXSSFWorkbook(1000);
		
		
		try {
			sxssfWorkbook.setCompressTempFiles(true);
			// 创建一个表格
			SXSSFSheet sheet = sxssfWorkbook.createSheet();
            //冻结首行
            sheet.createFreezePane(0, 1, 0, 1);
            // 创建一行
            Row row = sheet.createRow(0);
            // 生成一个样式
            CellStyle style = buildHeadStyle(sxssfWorkbook);
            // 生成字体
            
            Map<Integer, Integer> colWidthMap = new HashMap<>(5);
            for (int i = 0; i < HEADERS.length; i++) {
                Cell cell = row.createCell(i);
                cell.setCellStyle(style);
                XSSFRichTextString text = new XSSFRichTextString(HEADERS[i]);
                cell.setCellValue(text);
            }
            delColWidth(row, colWidthMap);

			
			// 根据条件批量查询
			int index = 1;
			int count = 1;
			boolean isFirst = true;
			int currrentPage = 1;
			int pageSize = 5000;
			map.put("currentPage", String.valueOf(currrentPage));
			map.put("pageSize", String.valueOf(pageSize));
			
			while (count > 0) {
                final DateTime startTime7 = DateTime.now();
                PageUtils exportAllInvoiceDetailByPage = apiOrderInvoiceInfoService.exportAllInvoiceDetailByPage(map, shList);
                
                final DateTime endTime7 = DateTime.now();
                final long time7 = new Duration(startTime7, endTime7).getMillis();
                log.debug("{},5000一批数据库查询数据获取耗时:{}毫秒", LOGGER_MSG, time7);
                if (exportAllInvoiceDetailByPage.getTotalPage() == 1) {
                    count--;
                } else {
                    if (isFirst) {
                        count = exportAllInvoiceDetailByPage.getTotalPage();
                        isFirst = false;
                    }
    
                    currrentPage = currrentPage + 1;
                    map.put("currentPage", String.valueOf(currrentPage));
                    count--;
                }
                
                @SuppressWarnings("unchecked")
                List<OrderInvoiceDetail> exportAllInfo = (List<OrderInvoiceDetail>) exportAllInvoiceDetailByPage.getList();
                for (OrderInvoiceDetail info : exportAllInfo) {
                    
                    //int k = 0;
                    //数据组装
                    if (CollectionUtils.isNotEmpty(info.getOrderItemList())) {
                        
                        for (OrderItemInfo itemInfo : info.getOrderItemList()) {
                            log.debug("处理的明细行：{}", index);
                            
                            Row tableRow = sheet.createRow(index);
                            tableRow.createCell(0).setCellValue(StringUtils.isBlank(info.getDdh()) ? "" : info.getDdh());
                            tableRow.createCell(1).setCellValue(StringUtils.isBlank(info.getFpdm()) ? "" : info.getFpdm());
                            tableRow.createCell(2).setCellValue(StringUtils.isBlank(info.getFphm()) ? "" : info.getFphm());
                            tableRow.createCell(3)
                                    .setCellValue(StringUtils.isBlank(info.getGhf_mc()) ? "" : info.getGhf_mc());
                            tableRow.createCell(4)
                                    .setCellValue(StringUtils.isBlank(info.getGhfNsrsbh()) ? "" : info.getGhfNsrsbh());
                            tableRow.createCell(5)
                                    .setCellValue((StringUtils.isBlank(info.getGhfYh()) ? "" : info.getGhfYh())
                                            + (StringUtils.isBlank(info.getGhfZh()) ? "" : info.getGhfZh()));
                            tableRow.createCell(6)
                                    .setCellValue((StringUtils.isBlank(info.getGhfDz()) ? "" : info.getGhfDz())
                                            + (StringUtils.isBlank(info.getGhfDh()) ? "" : info.getGhfDh()));
                            tableRow.createCell(7)
                                    .setCellValue(StringUtils.isBlank(info.getGhf_sj()) ? "" : info.getGhf_sj());
                            tableRow.createCell(8)
                                    .setCellValue(StringUtils.isBlank(info.getFpzlDm()) ? "" : info.getFpzlDm());
                            tableRow.createCell(9).setCellValue(StringUtils.isBlank(info.getZfbz()) ? "" : info.getZfbz());
                            tableRow.createCell(10).setCellValue(info.getKprq() == null ? "" : SIMPLE_DATE_FORMAT.format(info.getKprq()));
                            tableRow.createCell(11)
                                    .setCellValue(StringUtils.isBlank(info.getKphjje()) ? "" : info.getKphjje());
                            tableRow.createCell(12)
                                    .setCellValue(StringUtils.isBlank(info.getHjbhsje()) ? "" : info.getHjbhsje());
                            tableRow.createCell(13).setCellValue(StringUtils.isBlank(info.getKpse()) ? "" : info.getKpse());
                            tableRow.createCell(14).setCellValue(StringUtils.isBlank(info.getKpr()) ? "" : info.getKpr());
                            tableRow.createCell(15).setCellValue(StringUtils.isBlank(info.getKplx()) ? "" : info.getKplx());
                            tableRow.createCell(16).setCellValue(StringUtils.isBlank(info.getYwlx()) ? "" : info.getYwlx());
                            tableRow.createCell(17).setCellValue(StringUtils.isBlank(info.getBz()) ? "" : info.getBz());
                            tableRow.createCell(18)
                                    .setCellValue(StringUtils.isBlank(info.getOrderItemList().get(0).getXmmc()) ? ""
                                            : info.getOrderItemList().get(0).getXmmc());
                            tableRow.createCell(19)
                                    .setCellValue(StringUtils.isBlank(itemInfo.getXmmc()) ? "" : itemInfo.getXmmc());
                            tableRow.createCell(20)
                                    .setCellValue(StringUtils.isBlank(itemInfo.getGgxh()) ? "" : itemInfo.getGgxh());
                            tableRow.createCell(21)
                                    .setCellValue(StringUtils.isBlank(itemInfo.getXmdw()) ? "" : itemInfo.getXmdw());
                            tableRow.createCell(22)
                                    .setCellValue(StringUtils.isBlank(itemInfo.getXmsl()) ? "" : itemInfo.getXmsl());
                            tableRow.createCell(23)
                                    .setCellValue(StringUtils.isBlank(itemInfo.getXmdj()) ? "" : itemInfo.getXmdj());
                            tableRow.createCell(24)
                                    .setCellValue(StringUtils.isBlank(itemInfo.getXmje()) ? "" : itemInfo.getXmje());
                            tableRow.createCell(25)
                                    .setCellValue(StringUtils.isBlank(itemInfo.getSl()) ? "" : itemInfo.getSl());
                            tableRow.createCell(26)
                                    .setCellValue(StringUtils.isBlank(itemInfo.getSe()) ? "" : itemInfo.getSe());
                            tableRow.createCell(27)
                                    .setCellValue(StringUtils.isBlank(itemInfo.getSpbm()) ? "" : itemInfo.getSpbm());
                            tableRow.createCell(28)
                                    .setCellValue(StringUtils.isBlank(info.getBmbbbh()) ? "" : info.getBmbbbh());
                            
                            //处理列宽自适应高度
                            delColWidth(tableRow, colWidthMap);
                            //k++;
                            index++;
                        }
                        //单元格合并
						/*if (info.getOrderItemList().size() > 1) {
							for(int n = 0; n <= 18 ;n++){
								CellRangeAddress cra = new CellRangeAddress(index - k, index - 1, n, n); // 起始行,终止行,起始列,终止列
								sheet.addMergedRegion(cra);
							}
							
						}*/
                    }
                }
            }
			
		    for(Map.Entry<Integer,Integer> entry : colWidthMap.entrySet()){
		    	Integer key = entry.getKey();
		    	Integer value = entry.getValue();
		    	if(value < 255*256){
		            sheet.setColumnWidth(key, value);
		    	}else{
		    		sheet.setColumnWidth(key, 6000);
		    	}
		    	
		    }
			sxssfWorkbook.write(inputOut);
			final DateTime endTime6 = DateTime.now();
			final long time13 = new Duration(startTime, endTime6).getMillis();
            log.debug("{},最终耗时：{}",LOGGER_MSG,time13);
			log.info("(导出excel)调用生成excel方法结束");
		} catch (IOException e) {
			log.error("excel导出异常:{}", e);
		} finally {
			try {
				sxssfWorkbook.close();
				inputOut.close();
			} catch (IOException e) {
				log.error("输出流关闭异常:{}", e);
			}
		}
	}
 
 
	private void delColWidth(Row tableRow, Map<Integer, Integer> colWidthMap) {
		for (Cell cell : tableRow) {
			int columnIndex = cell.getColumnIndex();
			Integer maxColumIndex = colWidthMap.get(columnIndex);
			if (maxColumIndex == null) {
				String stringCellValue = cell.getStringCellValue();
				if (StringUtils.isNotBlank(stringCellValue)) {
					int length = cell.getStringCellValue().getBytes().length;
					colWidthMap.put(columnIndex, length * 256);
				} else {
					colWidthMap.put(columnIndex, 0);
				}
			} else {
				String stringCellValue = cell.getStringCellValue();
				if (StringUtils.isNotBlank(stringCellValue)) {
					int length = cell.getStringCellValue().getBytes().length;
					length = length * 256;
					if (length > maxColumIndex) {
						colWidthMap.put(columnIndex, length);
					}
				}
			}
		}

	}


	/**
     * @Title : buildHeadStyle
     * @Description ：创建excel头的格式
     * @param @return
     * @return CellStyle
     * @exception
     *
     */
    private CellStyle buildHeadStyle(SXSSFWorkbook workBook) {
		CellStyle style = workBook.createCellStyle();
		style.setFillForegroundColor(HSSFColor.HSSFColorPredefined.PALE_BLUE.getIndex());
        style.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        style.setBorderBottom(BorderStyle.THIN);
        style.setBorderLeft(BorderStyle.THIN);
        style.setBorderRight(BorderStyle.THIN);
        style.setBorderTop(BorderStyle.THIN);
        style.setAlignment(HorizontalAlignment.CENTER);
		return style;
	}


	/**
     * 导出订单数据
     */
    @Override
    @SuppressWarnings("unchecked")
    public void exportOrderInfo(Map<String, Object> paramMap, OutputStream outputStream, List<String> shList) {
        
        //查询数据库的数据
        PageUtils selectOrderInfo = apiOrderProcessService.selectOrderInfo(paramMap, shList);
        List<Map<String, Object>> orderList = (List<Map<String, Object>>) selectOrderInfo.getList();
        //创建excel工作台
        SXSSFWorkbook sxssfWorkbook = new SXSSFWorkbook(1000);
        sxssfWorkbook.setCompressTempFiles(true);
        // 创建一个表格
        Sheet sheet = sxssfWorkbook.createSheet();
        //冻结首行
        sheet.createFreezePane(0, 1, 0, 1);
        // 创建一行
        Row row = sheet.createRow(0);
        // 生成一个样式
		CellStyle style = buildHeadStyle(sxssfWorkbook);
		// 生成字体
        Font font = sxssfWorkbook.createFont();
        font.setFontHeightInPoints((short) 16);
        // 生成表头
        for (int i = 0; i < ORDER_HEADERS.length; i++) {
            Cell cell = row.createCell(i);
            cell.setCellStyle(style);
            XSSFRichTextString text = new XSSFRichTextString(ORDER_HEADERS[i]);
            cell.setCellValue(text);
        }
        //设置列宽
        Map<Integer, Integer> colWidthMap = new HashMap<>(5);
        delColWidth(row, colWidthMap);
    
        int index = 1;
    
        for (Map<String, Object> param : orderList) {
            Row tableRow = sheet.createRow(index);
            tableRow.createCell(0).setCellValue(param.get("ddh") == null ? "" : String.valueOf(param.get("ddh")));
            tableRow.createCell(1).setCellValue(cn.hutool.core.date.DateUtil.formatDateTime(cn.hutool.core.date.DateUtil.parse(param.get("createTime") == null ? "" : String.valueOf(param.get("createTime")))));
            tableRow.createCell(2).setCellValue(formatFpzldm(param.get("fpzlDm") == null ? "" : String.valueOf(param.get("fpzlDm"))));
            tableRow.createCell(3).setCellValue(param.get("ghfMc") == null ? "" : String.valueOf(param.get("ghfMc")));
            tableRow.createCell(4).setCellValue(param.get("xhfMc") == null ? "" : String.valueOf(param.get("xhfMc")));
            tableRow.createCell(5).setCellValue(param.get("kphjje") == null ? "" : String.valueOf(param.get("kphjje")));
            tableRow.createCell(6).setCellValue(param.get("kpse") == null ? "" : String.valueOf(param.get("kpse")));
            tableRow.createCell(7).setCellValue(formatDdzt(param.get("ddzt") == null ? "" : String.valueOf(param.get("ddzt"))));
            tableRow.createCell(8).setCellValue(formatSbyy(String.valueOf(param.get("ddzt")), param.get("sbyy") == null ? "" : String.valueOf(param.get("sbyy"))));
    		delColWidth(tableRow,colWidthMap);
            index++;
        }
        //设置列宽
        for(Map.Entry<Integer,Integer> entry : colWidthMap.entrySet()){
	    	Integer key = entry.getKey();
	    	Integer value = entry.getValue();
	    	if(value < 255*256){
	            sheet.setColumnWidth(key, value);
	    	}else{
	    		sheet.setColumnWidth(key, 6000);
	    	}
	    }
        try {
            sxssfWorkbook.write(outputStream);
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                outputStream.close();
                sxssfWorkbook.close();
            } catch (IOException e) {
                log.error("输出流关闭异常:{}", e);
            }
        }
    }

    /**
     * excel转换成订单数据
     * @param orderExcelMap
     * @param paramMap
     * @return
     */
    @Override
    public List<CommonOrderInfo> excelToOrderInfo(Map<String, List<NewOrderExcel>> orderExcelMap, Map<String, String> paramMap) throws UnsupportedEncodingException, OrderReceiveException, OrderSeparationException {

        String terminalCode = apiTaxEquipmentService.getTerminalCode(paramMap.get("xhfNsrsbh"));

        //补全业务类型id
        List<CommonOrderInfo> orderExcel2OrderInfo = new ArrayList<>();

        Date createTime = new Date();
        Date updateTime = createTime;
        for (Entry<String, List<NewOrderExcel>> entry : orderExcelMap.entrySet()) {
            List<NewOrderExcel> value = entry.getValue();
            CommonOrderInfo orderExcel2CommonOrderInfo = ExcelUtils.orderExcel2CommonOrderInfo(value, paramMap);
            // 补全业务类型id 如果业务类型id不存在新增
            if (StringUtils.isNotBlank(orderExcel2CommonOrderInfo.getOrderInfo().getYwlx())) {
                List<String> shList = new ArrayList<String>();
                shList.add(paramMap.get("xhfNsrsbh"));
                String yesxInfoId = yesxInfoCollect(orderExcel2CommonOrderInfo.getOrderInfo().getYwlx(), paramMap.get("xhfNsrsbh"),
                        paramMap.get("xhfMc"), shList);
                orderExcel2CommonOrderInfo.getOrderInfo().setYwlxId(yesxInfoId);
            }


            //不全购方信息
            if(checkIsBuyerEmpty(orderExcel2CommonOrderInfo)){

                if(StringUtils.isNotBlank(value.get(0).getGhf_id())){
                    BuyerEntity buyer = apiBuyerService.queryBuyerInfoByxhfNsrsbhAndBuyerCode(paramMap.get("xhfNsrsbh"), value.get(0).getGhf_id());
                    if(buyer != null){
                        orderExcel2CommonOrderInfo.getOrderInfo().setGhfMc(buyer.getPurchaseName());
                        orderExcel2CommonOrderInfo.getOrderInfo().setGhfNsrsbh(buyer.getTaxpayerCode());
                        orderExcel2CommonOrderInfo.getOrderInfo().setGhfDz(buyer.getAddress());
                        orderExcel2CommonOrderInfo.getOrderInfo().setGhfDh(buyer.getPhone());
                        orderExcel2CommonOrderInfo.getOrderInfo().setGhfYh(buyer.getBankOfDeposit());
                        orderExcel2CommonOrderInfo.getOrderInfo().setGhfZh(buyer.getBankNumber());
                    }
                }
            }

            //处理同一批次的订单创建时间一致
            orderExcel2CommonOrderInfo.getOrderInfo().setCreateTime(createTime);
            orderExcel2CommonOrderInfo.getOrderInfo().setUpdateTime(updateTime);
            orderExcel2CommonOrderInfo.getOrderInfo().setDdrq(createTime);
            for(OrderItemInfo orderItem : orderExcel2CommonOrderInfo.getOrderItemInfo()){
                orderItem.setCreateTime(createTime);
            }

            iCommonInterfaceService.dealOrderItem(orderExcel2CommonOrderInfo.getOrderItemInfo(), paramMap.get("xhfNsrsbh"), orderExcel2CommonOrderInfo.getOrderInfo().getQdBz(), terminalCode);

            //数据价税分离
            orderExcel2CommonOrderInfo = PriceTaxSeparationUtil.taxSeparationService(orderExcel2CommonOrderInfo, new TaxSeparateConfig());
            OrderProcessInfo processInfo = new OrderProcessInfo();
            processInfo.setDdlx(OrderInfoEnum.ORDER_TYPE_0.getKey());
            processInfo.setDdly(OrderInfoEnum.ORDER_SOURCE_0.getKey());
            processInfo.setDdzt(OrderInfoEnum.ORDER_STATUS_0.getKey());
            orderExcel2CommonOrderInfo.setProcessInfo(processInfo);
            orderExcel2CommonOrderInfo.getOrderInfo().setDdlx(OrderInfoEnum.ORDER_SOURCE_0.getKey());
            orderExcel2OrderInfo.add(orderExcel2CommonOrderInfo);
        }

       return orderExcel2OrderInfo;
    }


    /**
     * 业务类型收集 没有就新增
     */
    private String yesxInfoCollect(String ywlx, String nsrsbh, String xhfmc, List<String> xhfNsrsbh) {
        //查询业务类型信息
        BusinessTypeInfo bti = apiBusinessTypeService.queryYwlxInfoByNameAndNsrsbh(ywlx, xhfNsrsbh);
        if (bti == null) {
            String generateMixString = RandomUtil.randomString(20);
            log.info("{} 业务类型信息不存在，采集入库并返回业务类型Id:{}", LOGGER_MSG, generateMixString);
            bti = new BusinessTypeInfo();
            bti.setBusinessId(generateMixString);
            bti.setBusinessName(ywlx);
            bti.setDescription("");
            bti.setStatus(ConfigureConstant.STRING_0);
            bti.setId(apiInvoiceCommonMapperService.getGenerateShotKey());
            bti.setXhfNsrsbh(nsrsbh);
            bti.setXhfMc(xhfmc);
            bti.setCreateTime(new Date());
            //保存业务类型信息
            apiBusinessTypeService.saveBusinessTypeInfo(bti);
            return generateMixString;
        } else {
            log.info("{} 业务类型信息已存在，返回业务类型Id:{}", LOGGER_MSG, bti.getBusinessId());
            return bti.getBusinessId();
        }
    }

    /**
     * 判断购方信息是否为空
     * @param commonOrderInfo
     * @return
     */
    private boolean checkIsBuyerEmpty(CommonOrderInfo commonOrderInfo) {
        return StringUtils.isBlank(commonOrderInfo.getOrderInfo().getGhfMc()) && StringUtils.isBlank(commonOrderInfo.getOrderInfo().getGhfNsrsbh())
                && StringUtils.isBlank(commonOrderInfo.getOrderInfo().getGhfZh()) && StringUtils.isBlank(commonOrderInfo.getOrderInfo().getGhfYh())
                && StringUtils.isBlank(commonOrderInfo.getOrderInfo().getGhfDz()) && StringUtils.isBlank(commonOrderInfo.getOrderInfo().getGhfDh());
    }

    /**
     * 表格导出转换订单状态
     *
     * @param ddzt
     * @return
     */
    private String formatDdzt(String ddzt) {
        /**
         * 订单状态为:0,1,2,3,4,9,10为开票中
         * 订单状态为:5,7为开票成功
         * 订单状态为:6,8,11为开票失败
         */
        if (OrderInfoEnum.ORDER_STATUS_0.getKey().equals(ddzt)) {
            ddzt = "未开票";
        } else if (OrderInfoEnum.ORDER_STATUS_1.getKey().equals(ddzt)) {
            ddzt = "未开票";
        } else if (OrderInfoEnum.ORDER_STATUS_2.getKey().equals(ddzt)) {
            ddzt = "未开票";
        } else if (OrderInfoEnum.ORDER_STATUS_3.getKey().equals(ddzt)) {
            ddzt = OrderInfoEnum.INVOICE_STATUS_1.getValue();
        } else if (OrderInfoEnum.ORDER_STATUS_4.getKey().equals(ddzt)) {
            ddzt = OrderInfoEnum.INVOICE_STATUS_1.getValue();
        } else if (OrderInfoEnum.ORDER_STATUS_5.getKey().equals(ddzt)) {
            ddzt = OrderInfoEnum.INVOICE_STATUS_2.getValue();
        } else if (OrderInfoEnum.ORDER_STATUS_6.getKey().equals(ddzt)) {
            ddzt = "开票异常";
        } else if (OrderInfoEnum.ORDER_STATUS_7.getKey().equals(ddzt)) {
            ddzt = OrderInfoEnum.INVOICE_STATUS_2.getValue();
        } else if (OrderInfoEnum.ORDER_STATUS_8.getKey().equals(ddzt)) {
            ddzt = "开票异常";
        } else if (OrderInfoEnum.ORDER_STATUS_9.getKey().equals(ddzt)) {
            ddzt = OrderInfoEnum.INVOICE_STATUS_1.getValue();
        } else if (OrderInfoEnum.ORDER_STATUS_10.getKey().equals(ddzt)) {
            ddzt = OrderInfoEnum.INVOICE_STATUS_1.getValue();
        } else if (OrderInfoEnum.ORDER_STATUS_11.getKey().equals(ddzt)) {
            ddzt = "开票异常";
        }
        
        return ddzt;
    }
    
    
    /**
     * 表格导出转换发票种类代码
     *
     * @param fpzldm
     * @return
     */
    private String formatFpzldm(String fpzldm) {
        if (OrderInfoEnum.ORDER_INVOICE_TYPE_0.getKey().equals(fpzldm)) {
            fpzldm = OrderInfoEnum.ORDER_INVOICE_TYPE_0.getValue();
        } else if (OrderInfoEnum.ORDER_INVOICE_TYPE_2.getKey().equals(fpzldm)) {
            fpzldm = OrderInfoEnum.ORDER_INVOICE_TYPE_2.getValue();
        } else if (OrderInfoEnum.ORDER_INVOICE_TYPE_51.getKey().equals(fpzldm)) {
            fpzldm = OrderInfoEnum.ORDER_INVOICE_TYPE_51.getValue();
        } else if (OrderInfoEnum.ORDER_INVOICE_TYPE_41.getKey().equals(fpzldm)) {
            fpzldm = OrderInfoEnum.ORDER_INVOICE_TYPE_41.getValue();
        }
        return fpzldm;
    }
    
    /**
     * 表格导出转换失败原因
     *
     * @param sbyy
     * @return
     */
    private String formatSbyy(String ddzt, String sbyy) {
        if (StringUtils.isBlank(ddzt) || StringUtils.isBlank(sbyy)) {
            sbyy = "";
        } else if (OrderInfoEnum.ORDER_STATUS_6.getKey().equals(ddzt) || OrderInfoEnum.ORDER_STATUS_8.getKey().equals(ddzt) || OrderInfoEnum.ORDER_STATUS_11.getKey().equals(ddzt)) {
        } else {
            sbyy = "";
        }
        
        return sbyy;
    }

    
}
