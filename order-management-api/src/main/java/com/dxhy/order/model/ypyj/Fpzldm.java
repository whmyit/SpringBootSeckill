package com.dxhy.order.model.ypyj;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

/**
 * 发票种类数据
 *
 * @author ZSC-DXHY
 * @date 创建时间: 2020/6/12 17:40
 */
@Getter
@Setter
public class Fpzldm implements Serializable {
    
    
    /**
     * id
     */
    private String id;
    
    /**
     * 设备编号
     */
    private String sbbh;
    
    /**
     * 设备名称
     */
    private String sbMc;
    
    /**
     * 发票种类代码
     */
    private String fpzldm;
    
    /**
     * 预警份数
     */
    private String yjfs;
}
