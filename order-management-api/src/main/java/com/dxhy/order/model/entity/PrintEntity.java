package com.dxhy.order.model.entity;

import lombok.Data;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

/**
 * 描述信息：
 *
 * @author 谢元强
 * @date Created on 2018-08-31
 */
@Data
public class PrintEntity implements Serializable {
    /**
     * 发票id
     */
    private List<Map> ids;
    /**
     * 销方税号
     */
    private String xhfNsrsbh;
    /**
     *  发票打印类型  fp:发票，qd:清单
     */
    private String printType;
    /**
     *  打印点id
     */
    private String printId;
    /**
     * 上边距
     */
    private String sbj;
    /**
     * 右边距
     */
    private String zbj;
    
    /**
     *
     */
    private String spotKey;
}
