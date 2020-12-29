package com.dxhy.order.utils;

import org.apache.commons.lang3.StringUtils;
import org.apache.poi.hssf.usermodel.*;
import org.apache.poi.hssf.util.HSSFColor;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.util.CellRangeAddress;

import javax.swing.*;
import java.io.*;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import static org.apache.poi.hssf.usermodel.HeaderFooter.numPages;
import static org.apache.poi.hssf.usermodel.HeaderFooter.page;

/**
 * 文件导入工具类
 *
 * @author ZSC-DXHY
 * @date 创建时间: 2020-08-14 14:50
 */
public class FileExportImportUtils {
    
    InputStream os;
    List<List<String>> list = new ArrayList<>();
    
    /**
     * 创建工作本
     */
    public HSSFWorkbook demoWorkBook = new HSSFWorkbook();
    /**
     * 创建表
     */
    public HSSFSheet demoSheet = demoWorkBook.createSheet("Sheet1");
    
    /**
     * 创建行
     *
     * @param cells
     * @param rowIndex
     */
    public void createTableRow(List<String> cells, int rowIndex) {
        //创建第rowIndex行
        HSSFRow row = demoSheet.createRow(rowIndex);
        for (int i = 0; i < cells.size(); i++) {
            //创建第i个单元格
            HSSFCell cell = row.createCell(i);
            cell.setCellValue(cells.get(i));
        }
    }
    
    /**
     * 创建整个Excel表
     *
     * @throws SQLException
     */
    public void createExcelSheeet() throws SQLException {
        for (int i = 0; i < list.size(); i++) {
            createTableRow(list.get(i), i);
        }
    }
    
    /**
     * 导出表格
     *
     * @param sheet
     * @param
     * @throws IOException
     */
    public InputStream exportExcel(HSSFSheet sheet) throws IOException {
        sheet.setGridsPrinted(true);
        HSSFFooter footer = sheet.getFooter();
        footer.setRight("Page " + page() + " of " +
                numPages());
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        try {
            demoWorkBook.write(baos);
        } catch (IOException e) {
            e.printStackTrace();
        }
        byte[] ba = baos.toByteArray();
        os = new ByteArrayInputStream(ba);
        return os;
    }
    
    
    public InputStream export(List<List<String>> zlist) {
        InputStream myos = null;
        try {
            list = zlist;
            createExcelSheeet();
            myos = exportExcel(demoSheet);
            return myos;
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, "表格导出出错，错误信息 ：" + e + "\n错误原因可能是表格已经打开。");
            e.printStackTrace();
            return null;
        } finally {
            try {
                os.close();
                if (myos != null) {
                    myos.close();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        
    }
    
    public HSSFWorkbook getHSSFWorkbook(List<List<String>> zlist) {
        try {
            list = zlist;
            createExcelSheeet();
            demoSheet.setGridsPrinted(true);
            HSSFFooter footer = demoSheet.getFooter();
            footer.setRight("Page " + page() + " of " +
                    numPages());
            return demoWorkBook;
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, "表格导出出错，错误信息 ：" + e + "\n错误原因可能是表格已经打开。");
            e.printStackTrace();
            return null;
        }
        
    }
    
    /**
     * 创建excel
     *
     * @param headName 表头
     * @param list     数据字符串集合
     * @param expName  文件名
     * @return
     * @throws Exception
     */
    public static void createExcel(String[] headName, List<List<String>> list, String expName, OutputStream os) throws IOException {
        // 格式化时间
        HSSFWorkbook workbook = new HSSFWorkbook();
        HSSFSheet sheet = workbook.createSheet("sheet1");
        sheet.setDefaultRowHeightInPoints(20);
        // 创建表头
        HSSFRow row = sheet.createRow(0);
        HSSFCell cell;
        //循环表头信息
        for (int y = 0; y < headName.length; y++) {
            cell = row.createCell(y);
            cell.setCellValue(headName[y]);
        }
        //循环数据信息
        for (int x = 0; x < list.size(); x++) {
            row = sheet.createRow(x + 1);
            List<String> rowString = list.get(x);
            for (int i = 0; i < rowString.size(); i++) {
                //行宽度设置
                sheet.setColumnWidth(i, 5000);
                cell = row.createCell(i);
                cell.setCellValue(rowString.get(i));
            }
        }
        try {
            workbook.write(os);
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            workbook.close();
        }
    }
    
    
    /**
     * 外联方法  sheet调用方法
     * 单元格合并 使用
     *
     * @param list
     * @param colSpan
     * @param hearderRow
     * @param os
     * @throws Exception
     */
    public static void createExcel4Style(List<List<String>> list, List<List<Short>> colSpan, Integer hearderRow, OutputStream os) throws Exception {
        // 格式化时间
        HSSFWorkbook workbook = new HSSFWorkbook();
        creatSheet(workbook, list, "sheet1", colSpan, hearderRow);
        workbook.write(os);
    }
    
    
    /**
     * 多sheet调用方法
     *
     * @param workbook
     * @param list
     * @param sheetName
     * @param colSpan
     * @param hearderRow
     * @return
     * @throws Exception
     */
    public static HSSFSheet creatSheet(HSSFWorkbook workbook, List<List<String>> list, String sheetName, List<List<Short>> colSpan, Integer hearderRow) throws Exception {
        
        HSSFSheet sheet = workbook.createSheet(sheetName);
        sheet.setDefaultRowHeightInPoints(25);
        
        HSSFPalette palette = workbook.getCustomPalette();
        //颜色替换
        palette.setColorAtIndex(HSSFColor.HSSFColorPredefined.GREY_25_PERCENT.getIndex(), (byte) 237, (byte) 237, (byte) 237);
        palette.setColorAtIndex(HSSFColor.HSSFColorPredefined.GREEN.getIndex(), (byte) 0, (byte) 176, (byte) 80);
        
        //蓝色色调
        HSSFCellStyle blueStyle = getBlueStyle(workbook);
        //灰色
        HSSFCellStyle style = getColor(workbook, IndexedColors.GREY_25_PERCENT.getIndex());
        //无色
        HSSFCellStyle style2 = getColor(workbook, IndexedColors.WHITE.getIndex());
        
        HSSFRow row;
        HSSFCell cell;
        
        for (int i = 0; i < hearderRow; i++) {
            row = sheet.createRow(i);
            row.setHeightInPoints((short) 30);
            for (int j = 0, length = list.get(i).size(); j < length; j++) {
                cell = row.createCell(j);
                String cellValue = list.get(i).get(j);
                if (cellValue != null) {
                    cell.setCellValue(cellValue);
                }
                cell.setCellStyle(blueStyle);
            }
        }
        
        for (List<Short> shorts : colSpan) {
            CellRangeAddress rangeAddress = new CellRangeAddress(shorts.get(0), shorts.get(1), shorts.get(2), shorts.get(3));
            sheet.addMergedRegion(rangeAddress);
        }
        
        for (int i = hearderRow, length = list.size(); i < length; i++) {
            row = sheet.createRow(i);
            row.setHeightInPoints((short) 30);
            for (int j = 0, J_length = list.get(i).size(); j < J_length; j++) {
                cell = row.createCell(j);
                String data = list.get(i).get(j);
                
                
                HSSFCellStyle contextstyle = workbook.createCellStyle();
                
                
                if (i % 2 == 0) {
                    contextstyle.cloneStyleFrom(style2);
                } else {
                    contextstyle.cloneStyleFrom(style);
                }
                
                //data是否为数值型
                Boolean isNum = false;
                //data是否为整数
                Boolean isInteger = false;
                //data是否为百分数
                Boolean isPercent = false;
                if (StringUtils.isNotBlank(data)) {
                    //判断data是否为数值型
                    isNum = data.matches("^(-?\\d+)(\\.\\d+)?$");
                    //判断data是否为整数（小数部分是否为0）
                    isInteger = data.matches("^[-+]?[\\d]*$");
                    //判断data是否为百分数（是否包含“%”）
                    isPercent = data.contains("%");
                }
                
                //如果单元格内容是数值类型，涉及到金钱（金额、本、利），则设置cell的类型为数值型，设置data的类型为数值类型
                if (isNum && !isPercent) {
                    // 此处设置数据格式
                    HSSFDataFormat df = workbook.createDataFormat();
                    if (isInteger) {
                        //数据格式只显示整数
                        contextstyle.setDataFormat(HSSFDataFormat.getBuiltinFormat("#,#0"));
                    } else {
                        //保留两位小数点
                        contextstyle.setDataFormat(HSSFDataFormat.getBuiltinFormat("#,##0.00"));
                    }
                    // 设置单元格格式
                    cell.setCellStyle(contextstyle);
                    // 设置单元格内容为double类型
                    cell.setCellValue(Double.parseDouble(data));
                } else {
                    cell.setCellStyle(contextstyle);
                    // 设置单元格内容为字符型
                    cell.setCellValue(data);
                }
            }
            
        }
        for (int i = 0; i < list.get(0).size(); i++) {
            sheet.autoSizeColumn(i);
        }
        return sheet;
    }
    
    
    /**
     * 外联方法  单sheet调用方法
     *
     * @param headName
     * @param list
     * @param expName
     * @param os
     * @throws Exception
     */
    public static void createExcel4Style(String[] headName, List<List<String>> list, String expName, OutputStream os) throws Exception {
        // 格式化时间
        HSSFWorkbook workbook = new HSSFWorkbook();
        creatSheet(workbook, "sheet1", headName, list, expName);
        workbook.write(os);
    }
    
    /**
     * 多sheet调用方法
     *
     * @param workbook
     * @param sheetName
     * @param headName
     * @param list
     * @param expName
     * @return
     * @throws Exception
     */
    public static HSSFSheet creatSheet(HSSFWorkbook workbook, String sheetName, String[] headName, List<List<String>> list, String expName) throws Exception {
        
        HSSFSheet sheet = workbook.createSheet(sheetName);
        sheet.setDefaultRowHeightInPoints(25);
        
        HSSFPalette palette = workbook.getCustomPalette();
        //颜色替换
        palette.setColorAtIndex(HSSFColor.HSSFColorPredefined.GREY_25_PERCENT.getIndex(), (byte) 237, (byte) 237, (byte) 237);
        palette.setColorAtIndex(HSSFColor.HSSFColorPredefined.GREEN.getIndex(), (byte) 0, (byte) 176, (byte) 80);
        
        //蓝色色调
        HSSFCellStyle blueStyle = getBlueStyle(workbook);
        //灰色
        HSSFCellStyle style = getColor(workbook, IndexedColors.GREY_25_PERCENT.getIndex());
        //无色
        HSSFCellStyle style2 = getColor(workbook, IndexedColors.WHITE.getIndex());
        
        
        HSSFRow row2 = sheet.createRow(0);
        row2.setHeightInPoints((short) 30);
        HSSFCell index = row2.createCell(0);
        index.setCellValue(expName);
        index.setCellStyle(blueStyle);
        
        
        CellRangeAddress rangeAddress = new CellRangeAddress(0, 0, (short) 0, (short) headName.length - 1);
        sheet.addMergedRegion(rangeAddress);
        
        // 创建表头
        HSSFRow row = sheet.createRow(1);
        row.setHeightInPoints((short) 30);
        HSSFCell cell = row.createCell(0);
        
        //循环表头信息
        for (int y = 0; y < headName.length; y++) {
            cell = row.createCell(y);
            cell.setCellValue(headName[y]);
            cell.setCellStyle(blueStyle);
        }
        
        //循环数据信息
        for (int x = 0; x < list.size(); x++) {
            row = sheet.createRow(x + 2);
            row.setHeightInPoints((short) 25);
            
            List<String> rowString = list.get(x);
            for (int i = 0; i < rowString.size(); i++) {
                //行宽度设置
                
                cell = row.createCell(i);
                cell.setCellValue(rowString.get(i));
                
                if (x % 2 == 0) {
                    cell.setCellStyle(style2);
                } else {
                    cell.setCellStyle(style);
                }
            }
            
            for (int i = 0; i < headName.length; i++) {
                sheet.autoSizeColumn(i);
            }
            
        }
        return sheet;
    }
    
    
    public static void createExcel4sheet(String[] sheetNames, List<String[]> headName, List<List<List<String>>> list, String[] expName, OutputStream os) throws Exception {
        // 格式化时间
        HSSFWorkbook workbook = new HSSFWorkbook();
        for (int i = 0; i < sheetNames.length; i++) {
            creatSheet(workbook, sheetNames[i], headName.get(i), list.get(i), expName[i]);
        }
        workbook.write(os);
    }
    
    
    private static HSSFCellStyle getBlueStyle(HSSFWorkbook workbook) {
        //蓝色色调
        HSSFCellStyle blueStyle = workbook.createCellStyle();
        //22
        blueStyle.setFillForegroundColor(IndexedColors.GREEN.getIndex());
        blueStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        
        HSSFFont font = workbook.createFont();
        font.setFontName("粗体");
        //粗体
        font.setBold(true);
        //白色字
        font.setColor(HSSFColor.HSSFColorPredefined.WHITE.getIndex());
        blueStyle.setFont(font);
        
        blueStyle.setBottomBorderColor(IndexedColors.BLACK.getIndex());
        blueStyle.setLeftBorderColor(IndexedColors.BLACK.getIndex());
        blueStyle.setRightBorderColor(IndexedColors.BLACK.getIndex());
        blueStyle.setTopBorderColor(IndexedColors.BLACK.getIndex());
        blueStyle.setBorderBottom(BorderStyle.THIN);
        blueStyle.setBorderLeft(BorderStyle.THIN);
        blueStyle.setBorderRight(BorderStyle.THIN);
        blueStyle.setBorderTop(BorderStyle.THIN);
        //垂直
        blueStyle.setVerticalAlignment(VerticalAlignment.CENTER);
        //水平
        blueStyle.setAlignment(HorizontalAlignment.CENTER);
        
        return blueStyle;
    }
    
    private static HSSFCellStyle getColor(HSSFWorkbook workbook, short color) {
        HSSFCellStyle style = workbook.createCellStyle();
        if (color != 0) {
            style.setFillForegroundColor(color);
        }
        
        style.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        style.setBottomBorderColor(IndexedColors.BLACK.getIndex());
        style.setLeftBorderColor(IndexedColors.BLACK.getIndex());
        style.setRightBorderColor(IndexedColors.BLACK.getIndex());
        style.setTopBorderColor(IndexedColors.BLACK.getIndex());
        style.setBorderBottom(BorderStyle.THIN);
        style.setBorderLeft(BorderStyle.THIN);
        style.setBorderRight(BorderStyle.THIN);
        style.setBorderTop(BorderStyle.THIN);
        //垂直
        style.setVerticalAlignment(VerticalAlignment.CENTER);
        //水平
        style.setAlignment(HorizontalAlignment.CENTER);
        
        return style;
    }
    
    
    /** 创建多文件压缩包
     * @param response
     * @param dir 文件路径
     * @param expName 文件名
     */
    /**   public static void createRar(HttpServletResponse response, String dir,OutputStream source, String expName){
     
     if(!new File(dir).exists()){//检测生成路径
     new File(dir).mkdirs();
     }
     File zipfile = new File(dir+"/"+expName+".rar");
     
     //压缩流
     FileUtils.delFile(zipfile);//删除之前的压缩文件
     
     for(int i=0;i<srcfile.size();i++){//删除之前的xls
     FileUtils.delFile(new File(dir+"/"+expName+i+".xls"));
     }
     byte[] buf = new byte[1024];
     String ZIP_ENCODEING = "GBK";
     try {
     ZipOutputStream out = new ZipOutputStream(response.getOutputStream());
     
     String fileName = "";
     out.putNextEntry(new ZipEntry(fileName));
     out.write(source.);
     
     for (int i = 0; i < srcfile.size(); i++) {
     File file = srcfile.get(i);
     FileInputStream in = new FileInputStream(file);
     out.putNextEntry(new ZipEntry(file.getName()));
     int len;
     while ((len = in.read(buf)) > 0) {
     out.write(buf, 0, len);
     }
     out.closeEntry();
     in.close();
     }
     out.close();
     } catch (IOException e) {
     e.printStackTrace();
     }
     try {
     // 设置response的Header
     response.addHeader("Content-Disposition", "attachment;filename="+new String(zipfile.getName().getBytes("gbk"),"iso-8859-1"));  //转码之后下载的文件不会出现中文乱码
     response.addHeader("Content-Length", "" + zipfile.length());
     
     InputStream fis = new BufferedInputStream(new FileInputStream(zipfile));
     byte[] buffer = new byte[fis.available()];
     fis.read(buffer);
     fis.close();
     
     OutputStream toClient = new BufferedOutputStream(response.getOutputStream());
     toClient.write(buffer);
     toClient.flush();
     toClient.close();
     } catch (IOException e) {
     e.printStackTrace();
     }
     
     }*/
    
}
