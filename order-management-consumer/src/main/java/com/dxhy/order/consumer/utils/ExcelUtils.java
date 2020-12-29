package com.dxhy.order.consumer.utils;

import cn.hutool.core.util.RandomUtil;
import com.dxhy.order.constant.ConfigureConstant;
import com.dxhy.order.constant.OrderInfoEnum;
import com.dxhy.order.consumer.config.SystemConfig;
import com.dxhy.order.consumer.model.NewOrderExcel;
import com.dxhy.order.exceptions.OrderReceiveException;
import com.dxhy.order.model.CommonOrderInfo;
import com.dxhy.order.model.OrderInfo;
import com.dxhy.order.model.OrderItemInfo;
import com.dxhy.order.model.entity.BuyerEntity;
import com.dxhy.order.model.entity.CommodityCodeEntity;
import com.dxhy.order.model.entity.GroupCommodity;
import com.dxhy.order.model.entity.GroupTaxClassCodeEntity;
import com.dxhy.order.utils.DecimalCalculateUtil;
import com.dxhy.order.utils.StringUtil;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.util.ResourceUtils;

import java.io.*;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.*;

/**
 * @author ：杨士勇
 * @ClassName ：PriceAndTaxTest
 * @Description ：价税分离
 * @date ：2018年7月18日 上午10:09:52
 */
public class ExcelUtils {


    private final static String EXCEL_NAME = System.currentTimeMillis() + ".xlsx";


    public static CommonOrderInfo orderExcel2CommonOrderInfo(List<NewOrderExcel> value, Map<String, String> paramMap)
            throws OrderReceiveException, UnsupportedEncodingException {

        CommonOrderInfo orderInfoAndItemInfo = new CommonOrderInfo();
        List<OrderItemInfo> orderItemList = new ArrayList<>();
        OrderInfo orderInfo = new OrderInfo();

        int i = 0;
        double jshj = 0.00;
        StringBuilder sb = new StringBuilder();
        String bz = "";
        boolean isBzOverLimit = false;
        Map<String, String> resultMap1 = new HashMap<>(5);

        Date creatTime = new Date();
        Date updateTime = creatTime;
        for (NewOrderExcel orderExcel : value) {
            // 对明细进行处理
            if (resultMap1.get(orderExcel.getBz()) == null) {
                resultMap1.put(orderExcel.getBz(), "");
                if (!isBzOverLimit) {
                    if (StringUtils.isNotBlank(orderExcel.getBz())) {
                        if (sb.toString().getBytes("gbk").length > 150) {
                            isBzOverLimit = true;
                            bz = StringUtil.substringByte(sb.toString(), 0, 150);
                        } else if (sb.toString().getBytes("gbk").length == 150) {
                            isBzOverLimit = true;
                            bz = sb.toString();
                            if (bz.endsWith(";")) {
                                bz = bz.substring(0, bz.length() - 1);
                            }
                        } else {
                            sb.append(orderExcel.getBz()).append(";");
                        }

                    }
                }
            }

            if (i == 0) {
                orderInfo = orderExcel2OrderInfo(orderExcel, paramMap);
                OrderItemInfo orderExcel2OrderItemInfo = orderExcel2OrderItemInfo(orderExcel, orderInfo.getXhfNsrsbh());
                if (OrderInfoEnum.ORDER_LINE_TYPE_1.getKey().equals(orderExcel2OrderItemInfo.getFphxz())) {
                    orderItemList.get(i - 1).setFphxz(OrderInfoEnum.ORDER_LINE_TYPE_2.getKey());
                }
                orderExcel2OrderItemInfo.setSphxh(String.valueOf(i + 1));
                orderItemList.add(orderExcel2OrderItemInfo);
            } else {
                OrderItemInfo orderItemInfo = orderExcel2OrderItemInfo(orderExcel, orderInfo.getXhfNsrsbh());
                orderItemInfo.setSphxh(String.valueOf(i + 1));
                if (OrderInfoEnum.ORDER_LINE_TYPE_1.getKey().equals(orderItemInfo.getFphxz())) {
                    orderItemList.get(i - 1).setFphxz(OrderInfoEnum.ORDER_LINE_TYPE_2.getKey());
                    orderItemInfo.setXmdw(StringUtils.isBlank(orderItemInfo.getXmdw()) ? "" : orderItemList.get(i - 1).getXmdw());
                    orderItemInfo.setGgxh(StringUtils.isBlank(orderItemInfo.getGgxh()) ? "" : orderItemList.get(i - 1).getGgxh());
                }
                orderItemList.add(orderItemInfo);
            }
            // 此前已经校验过，只会有0和1，这里之判断这两种情况
            if (OrderInfoEnum.HSBZ_0.getKey().equals(orderExcel.getHsbz())) {
                // 如果导入数据不含税，重新计算价税合计,合计金额*（1 + 税率）
                BigDecimal se = new BigDecimal(orderExcel.getXmje()).multiply(new BigDecimal(orderExcel.getSl())).setScale(2, RoundingMode.HALF_UP);
                BigDecimal add = new BigDecimal(orderExcel.getXmje()).add(se).setScale(2, RoundingMode.HALF_UP);
                jshj = add.add(new BigDecimal(jshj)).setScale(2, RoundingMode.HALF_UP).doubleValue();

            } else if (OrderInfoEnum.HSBZ_1.getKey().equals(orderExcel.getHsbz())) {
                // 如果含税标志为是,直接计算价税合计
                jshj += Double.parseDouble(orderExcel.getXmje());
            }
            i++;
        }
        if (StringUtils.isBlank(bz)) {
            bz = sb.toString();
            if (bz.endsWith(";")) {
                bz = bz.substring(0, bz.length() - 1);
            }
        }
        orderInfo.setBz(bz);
        orderInfo.setKphjje(DecimalCalculateUtil.decimalFormat(jshj, ConfigureConstant.INT_2));
        if (jshj < 0) {
            orderInfo.setKplx(OrderInfoEnum.ORDER_BILLING_INVOICE_TYPE_1.getKey());
        } else {
            orderInfo.setKplx(OrderInfoEnum.ORDER_BILLING_INVOICE_TYPE_0.getKey());
        }
        orderInfoAndItemInfo.setOrderInfo(orderInfo);
        orderInfoAndItemInfo.setOrderItemInfo(orderItemList);

        return orderInfoAndItemInfo;

    }

    /**
     * 订单类型转换
     *
     * @param orderExcel
     * @return
     */
    private static OrderItemInfo orderExcel2OrderItemInfo(NewOrderExcel orderExcel, String xhfNsrsbh) {
        OrderItemInfo orderItemInfo = new OrderItemInfo();
        orderItemInfo.setGgxh(orderExcel.getGgxh());
        orderItemInfo.setSpbm(StringUtils.isBlank(orderExcel.getSpbm()) ? orderExcel.getSpbm() : StringUtil.fillZero(orderExcel.getSpbm(), 19));
        orderItemInfo.setXmdj(StringUtils.isBlank(orderExcel.getXmdj()) ? null : DecimalCalculateUtil.decimalFormatToString(orderExcel.getXmdj(), ConfigureConstant.INT_8));
        orderItemInfo.setXmdw(orderExcel.getXmdw());
        orderItemInfo.setXmje(StringUtils.isBlank(orderExcel.getXmje()) ? null : DecimalCalculateUtil.decimalFormatToString(orderExcel.getXmje(), ConfigureConstant.INT_2));
        orderItemInfo.setXmmc(orderExcel.getXmmc());
        orderItemInfo.setXmsl(StringUtils.isAllBlank(orderExcel.getXmsl()) ? null : DecimalCalculateUtil.decimalFormatToString(orderExcel.getXmsl(), ConfigureConstant.INT_8));
        orderItemInfo.setYhzcbs(OrderInfoEnum.YHZCBS_0.getKey());
        orderItemInfo.setSl(orderExcel.getSl());
        orderItemInfo.setYhzcbs(orderExcel.getYhzcbs());
        orderItemInfo.setZzstsgl(orderExcel.getZzstsgl());
        orderItemInfo.setLslbs(orderExcel.getLslbs());
        orderItemInfo.setHsbz(orderExcel.getHsbz());
        orderItemInfo.setSe(orderExcel.getSe());
        orderItemInfo.setXhfNsrsbh(xhfNsrsbh);
        if (Double.parseDouble(orderExcel.getXmje()) < 0) {
            orderItemInfo.setFphxz(OrderInfoEnum.ORDER_LINE_TYPE_1.getKey());
        } else {
            orderItemInfo.setFphxz(OrderInfoEnum.ORDER_LINE_TYPE_0.getKey());
        }
        orderItemInfo.setZxbm(orderExcel.getZxbm());
        return orderItemInfo;
    }


    /**
     * 订单主体信息转换
     *
     * @param orderExcel
     * @param paramMap
     * @return
     */
    private static OrderInfo orderExcel2OrderInfo(NewOrderExcel orderExcel, Map<String, String> paramMap) {
        OrderInfo orderInfo = new OrderInfo();
        orderInfo.setDdh(orderExcel.getDdh());
        orderInfo.setBz(orderExcel.getBz());
        orderInfo.setGhfDh(orderExcel.getGhf_dh());
        orderInfo.setGhfDz(orderExcel.getGhf_dz());
        orderInfo.setGhfMc(orderExcel.getGhf_mc());
        orderInfo.setGhfNsrsbh(orderExcel.getGhf_nsrsbh());
        orderInfo.setGhfYh(orderExcel.getGhf_yh());
        orderInfo.setGhfEmail(orderExcel.getGhf_yx());
        orderInfo.setGhfZh(orderExcel.getGhf_zh());
        orderInfo.setFpzlDm(Integer.valueOf(orderExcel.getFpzlDm()).toString());
        orderInfo.setGhfQylx(orderExcel.getGhf_qylx());
        orderInfo.setBbmBbh(StringUtils.isBlank(orderExcel.getBmbbbh()) ? SystemConfig.bmbbbh : orderExcel.getBmbbbh());
        orderInfo.setFpqqlsh("");
        orderInfo.setDdh(StringUtils.isBlank(orderExcel.getDdh()) ? RandomUtil.randomNumbers(12) : orderExcel.getDdh());
        orderInfo.setGhfId(orderExcel.getGhf_id());
        if (orderExcel.isCpy()) {
            orderInfo.setQdBz(OrderInfoEnum.QDBZ_CODE_4.getKey());
        } else {
            orderInfo.setQdBz(OrderInfoEnum.QDBZ_CODE_0.getKey());
        }
        orderInfo.setXhfDz(paramMap.get("xhfDz"));
        orderInfo.setXhfNsrsbh(paramMap.get("xhfNsrsbh"));
        orderInfo.setXhfMc(paramMap.get("xhfMc"));
        orderInfo.setXhfDh(paramMap.get("xhfDh"));
        orderInfo.setXhfYh(paramMap.get("xhfYh"));
        orderInfo.setXhfZh(paramMap.get("xhfZh"));

        orderInfo.setNsrmc(paramMap.get("xhfMc"));

        orderInfo.setNsrsbh(paramMap.get("xhfNsrsbh"));
        //行业代码
        orderInfo.setDkbz(OrderInfoEnum.DKBZ_0.getKey());
        orderInfo.setYwlx(orderExcel.getYwlx());
        orderInfo.setBbmBbh(SystemConfig.bmbbbh);
        orderInfo.setKpxm(orderExcel.getXmmc());
        return orderInfo;
    }

    /**
     * 根据excel里面的内容读取客户信息
     *
     * @param is       输入流
     * @param fileName excel名称
     * @return
     * @throws IOException
     */
    public static List<BuyerEntity> getExcelBuyerEntityInfo(InputStream is, String fileName) {
        List<BuyerEntity> buyerEntityList = null;
        try {
            //验证文件名是否合格
            if (!validateExcel(fileName)) {
                return null;
            }
            //根据文件名判断文件是2003版本还是2007版本
            boolean isExcel2003 = true;
            if (ExcelUtil.isExcel2007(fileName)) {
                isExcel2003 = false;
            }
            /** 根据版本选择创建Workbook的方式 */
            Workbook wb = null;
            //当excel是2003时
            if (isExcel2003) {
                wb = new HSSFWorkbook(is);
            } else {//当excel是2007时
                wb = new XSSFWorkbook(is);
            }
            //读取Excel里面客户的信息
            buyerEntityList = readBuyerEntityExcelValue(wb);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return buyerEntityList;
    }


    /**
     * 读取Excel里面客户的信息
     *
     * @param wb
     * @return
     */
    private static List<BuyerEntity> readBuyerEntityExcelValue(Workbook wb) {
        //得到第一个shell
        Sheet sheet = wb.getSheetAt(0);

        //得到Excel的行数
        int totalRows = sheet.getPhysicalNumberOfRows();

        //得到Excel的列数(前提是有行数)
        if (totalRows >= 1 && sheet.getRow(0) != null) {
            int totalCells = sheet.getRow(0).getPhysicalNumberOfCells();
        }
        List<BuyerEntity> buyerEntityList = new ArrayList<>();
        //循环Excel行数,从第二行开始。标题不入库
        for (int r = 1; r < totalRows; r++) {
            Row row = sheet.getRow(r);
            if (row == null) {
                continue;
            }
            BuyerEntity excelBean = new BuyerEntity();
            for (int i = 0; i < 8; i++) {
                if (i == 0) {
                    //购方名称
                    if (row.getCell(i) != null) {
                        row.getCell(i).setCellType(CellType.STRING);
                    }
                    Cell cell = row.getCell(i);
                    if (cell != null) {
                        excelBean.setPurchaseName(cell.getStringCellValue());
                    }
                } else if (i == 1) {
                    //税号
                    if (row.getCell(i) != null) {
                        row.getCell(i).setCellType(CellType.STRING);
                    }
                    Cell cell = row.getCell(i);
                    if (cell != null) {
                        excelBean.setTaxpayerCode(cell.getStringCellValue());
                    }
                } else if (i == 2) {
                    //开户银行
                    if (row.getCell(i) != null) {
                        row.getCell(i).setCellType(CellType.STRING);
                    }
                    Cell cell = row.getCell(i);
                    if (cell != null) {
                        excelBean.setBankOfDeposit(cell.getStringCellValue());
                    }
                } else if (i == 3) {
                    //银行账号
                    if (row.getCell(i) != null) {
                        row.getCell(i).setCellType(CellType.STRING);
                    }
                    Cell cell = row.getCell(i);
                    if (cell != null) {
                        excelBean.setBankNumber(cell.getStringCellValue());
                    }
                } else if (i == 4) {
                    //地址
                    if (row.getCell(i) != null) {
                        row.getCell(i).setCellType(CellType.STRING);
                    }
                    Cell cell = row.getCell(i);
                    if (cell != null) {
                        excelBean.setAddress(cell.getStringCellValue());
                    }
                } else if (i == 5) {
                    //电话
                    if (row.getCell(i) != null) {
                        row.getCell(i).setCellType(CellType.STRING);
                    }
                    Cell cell = row.getCell(i);
                    if (cell != null) {
                        excelBean.setPhone(cell.getStringCellValue());
                    }
                } else if (i == 6) {
                    //备注
                    if (row.getCell(i) != null) {
                        row.getCell(i).setCellType(CellType.STRING);
                    }
                    Cell cell = row.getCell(i);
                    if (cell != null) {
                        excelBean.setRemarks(cell.getStringCellValue());
                    }
                } else if (i == 7) {
                    //购方ID 后期添加
                    if (row.getCell(i) != null) {
                        row.getCell(i).setCellType(CellType.STRING);
                    }
                    Cell cell = row.getCell(i);
                    if (cell != null) {
                        excelBean.setBuyerCode(cell.getStringCellValue());
                    }
                }
            }
            //添加客户
            buyerEntityList.add(excelBean);
        }
        //读取完数据后删除上传的excel
        File path = null;
        try {
            path = new File(ResourceUtils.getURL("classpath:").getPath());
            String absolutePath = path.getAbsolutePath();
            String rootPath = absolutePath + "/templates/file/tem" + File.separator;
            File file = new File(rootPath + EXCEL_NAME);
            file.delete();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        return buyerEntityList;
    }

    /**
     * 验证EXCEL文件
     *
     * @param filePath
     * @return
     */
    public static boolean validateExcel(String filePath) {
        return filePath != null && (ExcelUtil.isExcel2003(filePath) || ExcelUtil.isExcel2007(filePath));
    }

    /**
     * 商品模板导入
     */
    public static List<CommodityCodeEntity> getCommodityExcelInfo(InputStream is, String fileName) {
        List<CommodityCodeEntity> groupCommodity = null;
        try {
            //验证文件名是否合格
            if (!validateExcel(fileName)) {
                return null;
            }
            //根据文件名判断文件是2003版本还是2007版本
            boolean isExcel2003 = true;
            if (ExcelUtil.isExcel2007(fileName)) {
                isExcel2003 = false;
            }

            /** 根据版本选择创建Workbook的方式 */
            Workbook wb = null;
            //当excel是2003时
            if (isExcel2003) {
                wb = new HSSFWorkbook(is);
            } else {//当excel是2007时
                wb = new XSSFWorkbook(is);
            }
            //读取Excel里面客户的信息
            groupCommodity = readCommodityExcelValue(wb);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return groupCommodity;
    }


    /**
     * 读取商品模板里面的信息
     *
     * @param wb
     * @return
     */
    private static List<CommodityCodeEntity> readCommodityExcelValue(Workbook wb) {
        //得到第一个shell
        Sheet sheet = wb.getSheetAt(0);

        //得到Excel的行数
        int totalRows = sheet.getPhysicalNumberOfRows();

        //得到Excel的列数(前提是有行数)
        if (totalRows >= 1 && sheet.getRow(0) != null) {
            int totalCells = sheet.getRow(0).getPhysicalNumberOfCells();
        }

        List<CommodityCodeEntity> customerList = new ArrayList<>();
        //循环Excel行数,从第三行开始。前两行是标题
        for (int r = 2; r < totalRows; r++) {
            Row row = sheet.getRow(r);
            if (row == null) {
                continue;
            }

            CommodityCodeEntity excelBean = new CommodityCodeEntity();
            excelBean.setSortId((long) r);
            for (int i = 0; i < 16; i++) {
                if (i == 0) {
                    //企业名称
                    if (row.getCell(i) != null) {
                        row.getCell(i).setCellType(CellType.STRING);
                    }
                    Cell cell = row.getCell(i);
                    if (cell != null) {
                        excelBean.setEnterpriseName(cell.getStringCellValue());
                    }
                } else if (i == 1) {
                    //企业税号
                    if (row.getCell(i) != null) {
                        row.getCell(i).setCellType(CellType.STRING);
                    }
                    Cell cell = row.getCell(i);
                    if (cell != null) {
                        excelBean.setXhfNsrsbh(cell.getStringCellValue());
                    }
                } else if (i == 2) {
                    //商品分组
                    if (row.getCell(i) != null) {
                        row.getCell(i).setCellType(CellType.STRING);
                    }
                    Cell cell = row.getCell(i);
                    if (cell != null) {
                        excelBean.setGroupName(cell.getStringCellValue());
                    }
                } else if (i == 3) {
                    //商品名称
                    if (row.getCell(i) != null) {
                        row.getCell(i).setCellType(CellType.STRING);
                    }
                    Cell cell = row.getCell(i);
                    if (cell != null) {
                        excelBean.setMerchandiseName(cell.getStringCellValue());
                    }
                } else if (i == 4) {
                    //商品编码
                    if (row.getCell(i) != null) {
                        row.getCell(i).setCellType(CellType.STRING);
                    }
                    Cell cell = row.getCell(i);
                    if (cell != null) {
                        excelBean.setEncoding(cell.getStringCellValue());
                    }
                } else if (i == 5) {
                    //规格型号
                    if (row.getCell(i) != null) {
                        row.getCell(i).setCellType(CellType.STRING);
                    }
                    Cell cell = row.getCell(i);
                    if (cell != null) {
                        excelBean.setSpecificationModel(cell.getStringCellValue());
                    }
                } else if (i == 6) {
                    //计量单位
                    if (row.getCell(i) != null) {
                        row.getCell(i).setCellType(CellType.STRING);
                    }
                    Cell cell = row.getCell(i);
                    if (cell != null) {
                        excelBean.setMeteringUnit(cell.getStringCellValue());
                    }
                } else if (i == 7) {
                    //单价
                    if (row.getCell(i) != null) {
                        row.getCell(i).setCellType(CellType.STRING);
                    }
                    Cell cell = row.getCell(i);
                    if (cell != null) {
                        excelBean.setUnitPrice(cell.getStringCellValue());
                    }
                } else if (i == 8) {
                    //描述
                    if (row.getCell(i) != null) {
                        row.getCell(i).setCellType(CellType.STRING);
                    }
                    Cell cell = row.getCell(i);
                    if (cell != null) {
                        excelBean.setDescription(cell.getStringCellValue());
                    }
                } else if (i == 9) {
                    //税收名称
                    if (row.getCell(i) != null) {
                        row.getCell(i).setCellType(CellType.STRING);
                    }
                    Cell cell = row.getCell(i);
                    if (cell != null) {
                        excelBean.setTaxClassificationName(cell.getStringCellValue());
                    }
                } else if (i == 10) {
                    //税收编码
                    if (row.getCell(i) != null) {
                        row.getCell(i).setCellType(CellType.STRING);
                    }
                    Cell cell = row.getCell(i);
                    if (cell != null) {
                        excelBean.setTaxClassCode(cell.getStringCellValue());
                    }
                } else if (i == 11) {
                    //税收简称
                    if (row.getCell(i) != null) {
                        row.getCell(i).setCellType(CellType.STRING);
                    }
                    Cell cell = row.getCell(i);
                    if (cell != null) {
                        excelBean.setTaxClassAbbreviation(cell.getStringCellValue());
                    }
                } else if (i == 12) {
                    //享受优惠政策
                    if (row.getCell(i) != null) {
                        row.getCell(i).setCellType(CellType.STRING);
                    }
                    Cell cell = row.getCell(i);
                    if (cell != null) {
                        excelBean.setEnjoyPreferentialPolicies(cell.getStringCellValue());
                    }
                } else if (i == 13) {
                    //优惠政策类型
                    if (row.getCell(i) != null) {
                        row.getCell(i).setCellType(CellType.STRING);
                    }
                    Cell cell = row.getCell(i);
                    if (cell != null) {
                        excelBean.setPreferentialPoliciesType(cell.getStringCellValue());
                    }
                } else if (i == 14) {
                    //含价税标志
                    if (row.getCell(i) != null) {
                        row.getCell(i).setCellType(CellType.STRING);
                    }
                    Cell cell = row.getCell(i);
                    if (cell != null) {
                        excelBean.setTaxLogo(cell.getStringCellValue());
                    }

                } else if (i == 15) {
                    //税率
                    if (row.getCell(i) != null) {
                        row.getCell(i).setCellType(CellType.STRING);
                    }
                    Cell cell = row.getCell(i);
                    if (cell != null) {
                        excelBean.setTaxRate(cell.getStringCellValue());
                    }

                }
            }
            //添加商品信息
            customerList.add(excelBean);
        }
        return customerList;
    }

    /**
     * 根据excel里面的内容读取客户信息
     *
     * @param is       输入流
     * @param fileName 名称
     * @return
     * @throws IOException
     */
    public static List<CommodityCodeEntity> getExcelCommodityCodeEntityInfo(InputStream is, String fileName) {
        List<CommodityCodeEntity> groupCommodity = null;
        try {
            //验证文件名是否合格
            if (!validateExcel(fileName)) {
                return null;
            }
            //根据文件名判断文件是2003版本还是2007版本
            boolean isExcel2003 = true;
            if (ExcelUtil.isExcel2007(fileName)) {
                isExcel2003 = false;
            }

            /** 根据版本选择创建Workbook的方式 */
            Workbook wb = null;
            //当excel是2003时
            if (isExcel2003) {
                wb = new HSSFWorkbook(is);
            } else {//当excel是2007时
                wb = new XSSFWorkbook(is);
            }
            //读取Excel里面客户的信息
            groupCommodity = readCommodityCodeEntityExcelValue(wb);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return groupCommodity;
    }


    /**
     * 读取Excel里面客户的信息
     *
     * @param wb
     * @return
     */
    private static List<CommodityCodeEntity> readCommodityCodeEntityExcelValue(Workbook wb) {
        //得到第一个shell
        Sheet sheet = wb.getSheetAt(0);

        //得到Excel的行数
        int totalRows = sheet.getPhysicalNumberOfRows();

        //得到Excel的列数(前提是有行数)
        if (totalRows >= 1 && sheet.getRow(0) != null) {
            int totalCells = sheet.getRow(0).getPhysicalNumberOfCells();
        }

        List<CommodityCodeEntity> customerList = new ArrayList<>();
        //循环Excel行数,从第二行开始。标题不入库
        for (int r = 1; r < totalRows; r++) {
            Row row = sheet.getRow(r);
            if (row == null) {
                continue;
            }

            CommodityCodeEntity excelBean = new CommodityCodeEntity();
            for (int i = 0; i < 15; i++) {
                if (i == 0) {
                    //商品编码
                    if (row.getCell(i) != null) {
                        row.getCell(i).setCellType(CellType.STRING);
                    }
                    Cell cell = row.getCell(i);
                    if (cell != null) {
                        excelBean.setEncoding(cell.getStringCellValue());
                    }
                } else if (i == 1) {
                    //商品名称
                    if (row.getCell(i) != null) {
                        row.getCell(i).setCellType(CellType.STRING);
                    }
                    Cell cell = row.getCell(i);
                    if (cell != null) {
                        excelBean.setMerchandiseName(cell.getStringCellValue());
                    }
                } else if (i == 2) {
                    //简码
                    if (row.getCell(i) != null) {
                        row.getCell(i).setCellType(CellType.STRING);
                    }
                    Cell cell = row.getCell(i);
                    if (cell != null) {
                        excelBean.setBriefCode(cell.getStringCellValue());
                    }
                } else if (i == 3) {
                    //商品税目
                    if (row.getCell(i) != null) {
                        row.getCell(i).setCellType(CellType.STRING);
                    }
                    Cell cell = row.getCell(i);
                    if (cell != null) {
                        excelBean.setTaxItems(cell.getStringCellValue());
                    }
                } else if (i == 4) {
                    //税率
                    if (row.getCell(i) != null) {
                        row.getCell(i).setCellType(CellType.STRING);
                    }
                    Cell cell = row.getCell(i);
                    if (cell != null) {
                        String taxRate = handleTaxRate(cell.getStringCellValue());
                        excelBean.setTaxRate(taxRate);
                    }
                } else if (i == 5) {
                    //规格型号
                    if (row.getCell(i) != null) {
                        row.getCell(i).setCellType(CellType.STRING);
                    }
                    Cell cell = row.getCell(i);
                    if (cell != null) {
                        excelBean.setSpecificationModel(cell.getStringCellValue());
                    }
                } else if (i == 6) {
                    //计价单位
                    if (row.getCell(i) != null) {
                        row.getCell(i).setCellType(CellType.STRING);
                    }
                    Cell cell = row.getCell(i);
                    if (cell != null) {
                        excelBean.setMeteringUnit(cell.getStringCellValue());
                    }
                } else if (i == 7) {
                    //单价
                    if (row.getCell(i) != null) {
                        row.getCell(i).setCellType(CellType.STRING);
                    }
                    Cell cell = row.getCell(i);
                    if (cell != null) {
                        excelBean.setUnitPrice(cell.getStringCellValue());
                    }
                } else if (i == 8) {
                    //含税价标志
                    if (row.getCell(i) != null) {
                        row.getCell(i).setCellType(CellType.STRING);
                    }
                    Cell cell = row.getCell(i);
                    if (cell != null) {
                        excelBean.setTaxLogo(cell.getStringCellValue());
                    }
                } else if (i == 9) {
                    //税收分类编码
                    if (row.getCell(i) != null) {
                        row.getCell(i).setCellType(CellType.STRING);
                    }
                    Cell cell = row.getCell(i);
                    if (cell != null) {
                        excelBean.setTaxClassCode(cell.getStringCellValue());
                    }
                } else if (i == 10) {
                    //隐藏标志
                    if (row.getCell(i) != null) {
                        row.getCell(i).setCellType(CellType.STRING);
                    }
                    Cell cell = row.getCell(i);
                    if (cell != null) {
                        excelBean.setHideTheLogo(cell.getStringCellValue());
                    }
                } else if (i == 11) {
                    //是否享受优惠政策
                    if (row.getCell(i) != null) {
                        row.getCell(i).setCellType(CellType.STRING);
                    }
                    Cell cell = row.getCell(i);
                    if (cell != null) {
                        excelBean.setEnjoyPreferentialPolicies(cell.getStringCellValue());
                    }
                } else if (i == 12) {
                    //优惠政策类型
                    if (row.getCell(i) != null) {
                        row.getCell(i).setCellType(CellType.STRING);
                    }
                    Cell cell = row.getCell(i);
                    if (cell != null) {
                        excelBean.setPreferentialPoliciesType(cell.getStringCellValue());
                    }
                } else if (i == 13) {
                    //分组名称
                    if (row.getCell(i) != null) {
                        row.getCell(i).setCellType(CellType.STRING);
                    }
                    Cell cell = row.getCell(i);
                    if (cell != null) {
                        excelBean.setGroupName(cell.getStringCellValue());
                    }

                }
            }
            //添加客户
            customerList.add(excelBean);
    
        }
        return customerList;
    }
    
    /**
     * 税率转换
     *
     * @param taxRate
     * @return
     */
    private static String handleTaxRate(String taxRate) {
        switch (taxRate) {
            case "0.17":
                taxRate = "17%";
                break;
            case "0.16":
                taxRate = "16%";
                break;
            case "0.11":
                taxRate = "11%";
                break;
            case "0.1":
                taxRate = "10%";
                break;
            case "0.09":
                taxRate = "9%";
                break;
            case "0.07":
                taxRate = "7%";
                break;
            case "0.06":
                taxRate = "6%";
                break;
            case "0.05":
                taxRate = "5%";
                break;
            case "0.04":
                taxRate = "4%";
                break;
            case "0.03":
                taxRate = "3%";
                break;
            case "0.015":
                taxRate = "1.5%";
                break;
            case "0":
                taxRate = "0%";
                break;
            default:
                break;
        }
        return taxRate;
    }

    /**
     * 根据excel里面的内容读取客户信息
     *
     * @param is       输入流
     * @param fileName excel名称
     * @return
     * @throws IOException
     */
    public static List<GroupCommodity> getExcelGroupCommodityInfo(InputStream is, String fileName) {
        List<GroupCommodity> groupCommodity = null;
        try {
            //验证文件名是否合格
            if (!validateExcel(fileName)) {
                return null;
            }
            //根据文件名判断文件是2003版本还是2007版本
            boolean isExcel2003 = true;
            if (ExcelUtil.isExcel2007(fileName)) {
                isExcel2003 = false;
            }

            /** 根据版本选择创建Workbook的方式 */
            Workbook wb = null;
            //当excel是2003时
            if (isExcel2003) {
                wb = new HSSFWorkbook(is);
            } else {//当excel是2007时
                wb = new XSSFWorkbook(is);
            }
            //读取Excel里面客户的信息
            groupCommodity = readExcelGroupCommodityValue(wb);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return groupCommodity;
    }


    /**
     * 读取Excel里面客户的信息
     *
     * @param wb
     * @return
     */
    private static List<GroupCommodity> readExcelGroupCommodityValue(Workbook wb) {
        //得到第一个shell
        Sheet sheet = wb.getSheetAt(0);

        //得到Excel的行数
        int totalRows = sheet.getPhysicalNumberOfRows();

        //得到Excel的列数(前提是有行数)
        if (totalRows >= 1 && sheet.getRow(0) != null) {
            int totalCells = sheet.getRow(0).getPhysicalNumberOfCells();
        }

        List<GroupCommodity> customerList = new ArrayList<>();
        //循环Excel行数,从第二行开始。标题不入库
        for (int r = 1; r < totalRows; r++) {
            Row row = sheet.getRow(r);
            if (row == null) {
                continue;
            }

            GroupCommodity excelBean = new GroupCommodity();
            for (int i = 0; i < 3; i++) {
                if (i == 0) {
                    if (row.getCell(i) != null) {
                        row.getCell(i).setCellType(CellType.STRING);
                    }
                    Cell cell = row.getCell(i);
                    if (cell != null) {
                        excelBean.setGroupCode(cell.getStringCellValue());
                    }
                } else if (i == 1) {
                    if (row.getCell(i) != null) {
                        row.getCell(i).setCellType(CellType.STRING);
                    }
                    Cell cell = row.getCell(i);
                    if (cell != null) {
                        excelBean.setGroupName(cell.getStringCellValue());
                    }
                }

            }
            //添加客户
            customerList.add(excelBean);
        }
        //读取完数据后删除上传的excel
        File path = null;
        try {
            path = new File(ResourceUtils.getURL("classpath:").getPath());
            String absolutePath = path.getAbsolutePath();
            String rootPath = absolutePath + "/templates/file/tmp" + File.separator;
            File file = new File(rootPath + EXCEL_NAME);
            file.delete();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        return customerList;
    }


    /**
     * 商品模板导入
     */
    public static List<GroupTaxClassCodeEntity> getExcelGroupTaxClassCodeEntityInfo(InputStream is, String fileName) {
        List<GroupTaxClassCodeEntity> groupTaxClassCodeEntity = null;
        try {
            //验证文件名是否合格
            if (!validateExcel(fileName)) {
                return null;
            }
            //根据文件名判断文件是2003版本还是2007版本
            boolean isExcel2003 = true;
            if (ExcelUtil.isExcel2007(fileName)) {
                isExcel2003 = false;
            }

            /** 根据版本选择创建Workbook的方式 */
            Workbook wb = null;
            //当excel是2003时
            if (isExcel2003) {
                wb = new HSSFWorkbook(is);
            } else {//当excel是2007时
                wb = new XSSFWorkbook(is);
            }
            //读取Excel里面客户的信息
            groupTaxClassCodeEntity = readGroupTaxClassCodeExcelValue(wb);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return groupTaxClassCodeEntity;
    }

    /**
     * 读取商品模板里面的信息
     *
     * @param wb
     * @return
     */
    private static List<GroupTaxClassCodeEntity> readGroupTaxClassCodeExcelValue(Workbook wb) {
        //得到第一个shell
        Sheet sheet = wb.getSheetAt(0);

        //得到Excel的行数
        int totalRows = sheet.getPhysicalNumberOfRows();

        //得到Excel的列数(前提是有行数)
        if (totalRows >= 1 && sheet.getRow(0) != null) {
            int totalCells = sheet.getRow(0).getPhysicalNumberOfCells();
        }

        List<GroupTaxClassCodeEntity> customerList = new ArrayList<>();
        //循环Excel行数,从第三行开始。前两行是标题
        for (int r = 2; r < totalRows; r++) {
            Row row = sheet.getRow(r);
            if (row == null) {
                continue;
            }

            GroupTaxClassCodeEntity excelBean = new GroupTaxClassCodeEntity();
            excelBean.setSortId((long) r);
            for (int i = 0; i < 10; i++) {
                if (i == 0) {
                    //商品分组
                    if (row.getCell(i) != null) {
                        row.getCell(i).setCellType(CellType.STRING);
                    }
                    Cell cell = row.getCell(i);
                    if (cell != null) {
                        excelBean.setGroupName(cell.getStringCellValue());
                    }
                } else if (i == 1) {
                    //商品名称
                    if (row.getCell(i) != null) {
                        row.getCell(i).setCellType(CellType.STRING);
                    }
                    Cell cell = row.getCell(i);
                    if (cell != null) {
                        excelBean.setMerchandiseName(cell.getStringCellValue());
                    }
                } else if (i == 2) {
                    //商品编码
                    if (row.getCell(i) != null) {
                        row.getCell(i).setCellType(CellType.STRING);
                    }
                    Cell cell = row.getCell(i);
                    if (cell != null) {
                        excelBean.setEncoding(cell.getStringCellValue());
                    }
                } else if (i == 3) {
                    //规格型号
                    if (row.getCell(i) != null) {
                        row.getCell(i).setCellType(CellType.STRING);
                    }
                    Cell cell = row.getCell(i);
                    if (cell != null) {
                        excelBean.setSpecificationModel(cell.getStringCellValue());
                    }
                } else if (i == 4) {
                    //计量单位
                    if (row.getCell(i) != null) {
                        row.getCell(i).setCellType(CellType.STRING);
                    }
                    Cell cell = row.getCell(i);
                    if (cell != null) {
                        excelBean.setMeteringUnit(cell.getStringCellValue());
                    }
                } else if (i == 5) {
                    //单价
                    if (row.getCell(i) != null) {
                        row.getCell(i).setCellType(CellType.STRING);
                    }
                    Cell cell = row.getCell(i);
                    if (cell != null) {
                        excelBean.setUnitPrice(cell.getStringCellValue());
                    }
                } else if (i == 6) {
                    //描述
                    if (row.getCell(i) != null) {
                        row.getCell(i).setCellType(CellType.STRING);
                    }
                    Cell cell = row.getCell(i);
                    if (cell != null) {
                        excelBean.setDescription(cell.getStringCellValue());
                    }
                } else if (i == 7) {
                    //税收名称
                    if (row.getCell(i) != null) {
                        row.getCell(i).setCellType(CellType.STRING);
                    }
                    Cell cell = row.getCell(i);
                    if (cell != null) {
                        excelBean.setTaxClassificationName(cell.getStringCellValue());
                    }
                } else if (i == 8) {
                    //税收编码
                    if (row.getCell(i) != null) {
                        row.getCell(i).setCellType(CellType.STRING);
                    }
                    Cell cell = row.getCell(i);
                    if (cell != null) {
                        excelBean.setTaxClassCode(cell.getStringCellValue());
                    }
                } else if (i == 9) {
                    //税收简称
                    if (row.getCell(i) != null) {
                        row.getCell(i).setCellType(CellType.STRING);
                    }
                    Cell cell = row.getCell(i);
                    if (cell != null) {
                        excelBean.setTaxClassAbbreviation(cell.getStringCellValue());
                    }
                }
            }
            //添加商品信息
            customerList.add(excelBean);
        }
        return customerList;
    }

    public static void main(String[] args) {
        String str = null;

        str = StringUtils.isBlank(str) ? str : StringUtil.fillZero(str, 19);
        System.out.println(str);


    }

    /**
     * @param newOrderExcels
     * @return
     * @description excel转换为订单数据
     */
    /*public static List<CommonOrderInfo> excelToOrderInfo(List<NewOrderExcel> newOrderExcels,Map<String, String> paramMap) {

        Map<String, List<NewOrderExcel>> orderExcelMap = new LinkedHashMap<>();

        List<CommonOrderInfo> commonList = new ArrayList<CommonOrderInfo>();

        //具有相同购方信息和订单号的数据作为同一订单
        for (NewOrderExcel orderExcel : newOrderExcels) {
            String key = orderExcel.getFpzlDm() + orderExcel.getDdh() + orderExcel.getGhf_mc()
                    + orderExcel.getGhf_nsrsbh() + orderExcel.getGhf_dz() + orderExcel.getGhf_dh()
                    + orderExcel.getGhf_zh() + orderExcel.getGhf_yh();
            String hashCode = String.valueOf(key.hashCode());
            // 利用字符串的hascode做key过滤相同条件的数据
            if (orderExcelMap.get(hashCode) == null) {
                List<NewOrderExcel> orderList = new ArrayList<>();
                orderList.add(orderExcel);
                orderExcelMap.put(hashCode, orderList);
            } else {
                List<NewOrderExcel> list = orderExcelMap.get(hashCode);
                list.add(orderExcel);
                orderExcelMap.put(hashCode, list);
            }

        }

        //excel转换成订单数据
        for(Map.Entry<String,List<NewOrderExcel>> entry : orderExcelMap.entrySet()){

            List<NewOrderExcel> value = entry.getValue();




        }

    }*/
}
