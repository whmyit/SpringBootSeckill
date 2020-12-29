package com.dxhy.order.file.common;

import lombok.Getter;
import lombok.Setter;

import java.util.Map;

/**
 * 表格导入头信息
 *
 * @author ZSC-DXHY
 * @date 创建时间: 2020-08-14 14:48
 */
@Setter
@Getter
public class ExcelReadContext {


    private Class<?> cla;

    private Map<String,String> headToPropertyMap;

    private String fileType;

    private String filePrefix;

    private int sheetIndex;

    private int headRow;

    private boolean needRowIndex;

    private Map<String,Integer> headerToColumnMap;



    public ExcelReadContext(Class<?> t,Map<String,String> map,String prefix,int sheetIndex,int headRow){
        this.cla = t;
        this.headToPropertyMap = map;
        this.filePrefix = prefix;
        this.sheetIndex = sheetIndex;
        this.headRow = headRow;
    }

    public ExcelReadContext(Class<?> t,Map<String,String> map,boolean isNeedRowIndex){

        this.cla = t;
        this.headToPropertyMap = map;
        this.filePrefix = ".xlsx";
        this.sheetIndex = 0;
        this.headRow  = 1;
        this.needRowIndex = isNeedRowIndex;
    }


    public ExcelReadContext(Map<String,String> headToPropertyMap,Map<String,Integer> headerToColumnMap){

        this.headToPropertyMap = headToPropertyMap;
        this.sheetIndex = 0;
        this.headRow  = 0;
        this.headerToColumnMap = headerToColumnMap;
    }






}
