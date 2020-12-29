package com.dxhy.order.model.entity;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

/**
 * 数据字典表
 *
 * @author ZSC-DXHY
 */
@Setter
@Getter
public class DictionaryEntity implements Serializable {
	private static final long serialVersionUID = 1L;
	
	private String id;
	
	private String name;
	
	private String type;
	
	private String code;
	
	private String value;
	
	private Integer orderNum;
	
	private String remark;
	
	private Integer delFlag;
}
