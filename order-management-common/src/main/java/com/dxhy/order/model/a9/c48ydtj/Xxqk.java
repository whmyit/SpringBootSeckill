package com.dxhy.order.model.a9.c48ydtj;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.util.List;
/**
 * 税率信息
 *
 * @author ZSC-DXHY
 * @date 创建时间: 2020-08-14 15:47
 */
@Getter
@Setter
public class Xxqk implements Serializable {
	private String hj;
	
	private String mc;
	
	private List<Xxs> xxs;
	
}
