package com.dxhy.order.model.bwactivexs.dy;

import lombok.Data;

import java.util.List;
/**
 * 打印点列表响应扩展
 *
 * @author ZSC-DXHY
 * @date 创建时间: 2020-08-14 14:46
 */
@Data
public class DydListResponseExtend {
    
    private String total;
	private String size;
	private String pages;
	private String current;
	private List<DydXx> records;
}
