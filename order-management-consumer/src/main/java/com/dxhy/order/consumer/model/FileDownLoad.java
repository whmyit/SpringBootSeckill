package com.dxhy.order.consumer.model;

import lombok.Getter;
import lombok.Setter;

/**
 * 前端文件下载
 *
 * @author ZSC-DXHY
 */
@Getter
@Setter
public class FileDownLoad {
	private String fileSuffix;
	private String fileName;
	private String fileContent;
}
