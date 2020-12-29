package com.dxhy.order.consumer.utils;

/**
 * 表格判断工具类
 *
 * @author ZSC-DXHY
 * @date 创建时间: 2020-08-14 15:47
 */
public class ExcelUtil {
	
	/**
	 * 是否是2003的excel，返回true是2003
	 *
	 * @param filePath
	 * @return
	 */
	public static boolean isExcel2003(String filePath) {
		return filePath.matches("^.+\\.(?i)(xls)$");
	}
	
	/**
	 * 是否是2007的excel，返回true是2007
	 * @param filePath
	 * @return
	 */
	public static boolean isExcel2007(String filePath) {
		return filePath.matches("^.+\\.(?i)(xlsx)$");
	}
	
}
