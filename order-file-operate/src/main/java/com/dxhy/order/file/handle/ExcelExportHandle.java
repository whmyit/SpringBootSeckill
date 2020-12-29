package com.dxhy.order.file.handle;


import cn.hutool.core.date.DateTime;
import com.dxhy.order.file.exception.ExcelReadException;
import com.dxhy.order.file.common.ExcelImportErrorMessageEnum;
import com.dxhy.order.file.common.ExcelReadContext;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.apache.poi.hssf.util.HSSFColor;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.streaming.SXSSFSheet;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;
import org.apache.poi.xssf.usermodel.XSSFRichTextString;

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 表格导入通用
 *
 * @author ZSC-DXHY
 * @date 创建时间: 2020-08-14 14:48
 */
@Slf4j
public class ExcelExportHandle {


    private ExcelReadContext excelReadContext;


    /**
     * excel通用导出
     *
     * @param inputStram
     * @param <T>
     * @throws ExcelReadException
     * @throws IOException
     */
    public <T> SXSSFWorkbook exportExcel(InputStream inputStram, List<T> list) throws ExcelReadException, IOException {


        final DateTime startTime = DateTime.now();

        // 创建一个工作簿
        SXSSFWorkbook sxssfWorkbook = new SXSSFWorkbook(1000);

        try {
            sxssfWorkbook.setCompressTempFiles(true);
            // 创建一个表格
            SXSSFSheet sheet = sxssfWorkbook.createSheet();
            //冻结首行
            sheet.createFreezePane(0, 1, 0, 1);

            Map<Integer, Integer> colWidthMap = new HashMap<>(5);

            int rowIndex = 0;
            //生成表头
            Map<String, String> headToPropertyMap = excelReadContext.getHeadToPropertyMap();
            if (headToPropertyMap != null) {
                Row row = sheet.createRow(rowIndex);
                CellStyle style = buildHeadStyle(sxssfWorkbook);

                for (Map.Entry<String, String> entry : headToPropertyMap.entrySet()) {

                    if (excelReadContext.getHeaderToColumnMap() == null) {
                        throw new ExcelReadException(ExcelImportErrorMessageEnum.ORDERINFO_PARAM_ERROR_9105.getKey(),
                                ExcelImportErrorMessageEnum.ORDERINFO_PARAM_ERROR_9105.getValue());
                    }

                    Integer columnIndex = excelReadContext.getHeaderToColumnMap().get(entry.getKey());
                    if (columnIndex == null) {
                        throw new ExcelReadException(ExcelImportErrorMessageEnum.ORDERINFO_PARAM_ERROR_9105.getKey(),
                                ExcelImportErrorMessageEnum.ORDERINFO_PARAM_ERROR_9105.getValue());
                    }
                    Cell cell = row.createCell(columnIndex);
                    cell.setCellStyle(style);
                    XSSFRichTextString text = new XSSFRichTextString(entry.getValue());
                    cell.setCellValue(text);
                }
                rowIndex++;
                //自适应列宽
                delColWidth(row, colWidthMap);
            }

            //生成数据
            for (T data : list) {

                Row row = sheet.createRow(rowIndex);
                if (excelReadContext.getHeadToPropertyMap() != null) {
                    for (Map.Entry<String, String> entry : headToPropertyMap.entrySet()) {
                        Integer columnIndex = excelReadContext.getHeaderToColumnMap().get(entry.getKey());
                        Cell cell = row.createCell(columnIndex);
                        String property = entry.getKey();
                        String getMethodName = "get" + property.substring(0, 1).toUpperCase() + property.substring(1);
                        Method getMethod = data.getClass().getMethod(getMethodName, String.class);
                        Object invoke = getMethod.invoke(data);

                        if (!(invoke instanceof String)) {
                            throw new ExcelReadException(ExcelImportErrorMessageEnum.ORDERINFO_PARAM_NOT_SUPPORT_9106.getKey(),
                                    ExcelImportErrorMessageEnum.ORDERINFO_PARAM_NOT_SUPPORT_9106.getValue());
                        }
                        XSSFRichTextString text = new XSSFRichTextString(invoke == null ? "" : String.valueOf(invoke));
                        cell.setCellValue(text);
                    }

                } else {
                    Field[] declaredFields = data.getClass().getDeclaredFields();

                    int columnIndex = 0;
                    for (Field field : declaredFields) {
                        String property = field.getName();
                        String getMethodName = "get" + property.substring(0, 1).toUpperCase() + property.substring(1);
                        Method getMethod = data.getClass().getMethod(getMethodName, String.class);
                        Object invoke = getMethod.invoke(data);
                        if (!(invoke instanceof String)) {
                            throw new ExcelReadException(ExcelImportErrorMessageEnum.ORDERINFO_PARAM_NOT_SUPPORT_9106.getKey(),
                                    ExcelImportErrorMessageEnum.ORDERINFO_PARAM_NOT_SUPPORT_9106.getValue());
                        }

                        Cell cell = row.createCell(columnIndex);
                        XSSFRichTextString text = new XSSFRichTextString(invoke == null ? "" : String.valueOf(invoke));
                        cell.setCellValue(text);
                        columnIndex++;
                    }
                }
                //自适应列宽
                delColWidth(row, colWidthMap);
                rowIndex++;
            }

            //处理列宽
            for (Map.Entry<Integer, Integer> entry : colWidthMap.entrySet()) {
                Integer key = entry.getKey();
                Integer value = entry.getValue();
                if (value < 255 * 256) {
                    sheet.setColumnWidth(key, value);
                } else {
                    sheet.setColumnWidth(key, 6000);
                }

            }
    
            return sxssfWorkbook;
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        }
        return null;
    }
    
    /**
     * 处理列宽
     *
     * @param tableRow
     * @param colWidthMap
     */
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
     * 创建表头格式
     * @param workBook
     * @return
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


}
