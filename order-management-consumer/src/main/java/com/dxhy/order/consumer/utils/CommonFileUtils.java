package com.dxhy.order.consumer.utils;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

import java.io.*;
/**
 * 通用文件操作方法
 *
 * @author ZSC-DXHY
 * @date 创建时间: 2020-08-14 14:40
 */
@Slf4j
public class CommonFileUtils {
	
	/**
	 * @description 文件合并方法
	 * @param fpaths
	 * @param resultPath
	 * @return
	 */
	public static boolean mergeFiles(String[] fpaths, String resultPath) {
	    if (fpaths == null || fpaths.length < 1 || StringUtils.isBlank(resultPath)) {
	        return false;
	    }
	    if (fpaths.length == 1) {
	        return new File(fpaths[0]).renameTo(new File(resultPath));
	    }
	 
	    File[] files = new File[fpaths.length];
	    for (int i = 0; i < fpaths.length; i ++) {
	        files[i] = new File(fpaths[i]);
	        if (StringUtils.isBlank(fpaths[i]) || !files[i].exists() || !files[i].isFile()) {
	            return false;
	        }
	    }
	 
	    File resultFile = new File(resultPath);
	   
	 
	    try {
	        int bufSize = 1024;
	        BufferedOutputStream outputStream = new BufferedOutputStream(new FileOutputStream(resultFile));
	        byte[] buffer = new byte[bufSize];
	 
	        for (int i = 0; i < fpaths.length; i ++) {
	            BufferedInputStream inputStream = new BufferedInputStream(new FileInputStream(files[i]));
	            int readcount;
		        while ((readcount = inputStream.read(buffer)) > 0) {
			        outputStream.write(buffer, 0, readcount);
		        }
		        inputStream.close();
	        }
		    outputStream.close();
	    } catch (IOException e) {
		    e.printStackTrace();
		    return false;
	    }
		
		for (int i = 0; i < fpaths.length; i++) {
			files[i].delete();
		}
		return true;
	}
	
	/**
	 * 根据路径和文件名创建文件
	 *
	 * @param path
	 * @param fileName
	 * @return
	 */
	public static File creaetFile(String path, String fileName) {
		File file = new File(path);
		if (!file.exists()) {
			file.mkdirs();
		}
		file = new File(path + fileName);
		if (!file.exists()) {
			try {
				file.createNewFile();
			} catch (IOException e) {
				log.error("创建文件异常，文件名:{};e:{}",path + fileName,e);
			    return null;
			}
        }
		return file;
	}
	

}
