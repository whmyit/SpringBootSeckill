package com.dxhy.order.model.c48.sld;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

/**
 * 发票领用列表
 *
 * @author ZSC-DXHY
 * @date 创建时间: 2020/2/14 14:04
 */
@Setter
@Getter
public class FpThlyNew extends FpThly implements Serializable {
    
    private String nsrmc;
}
