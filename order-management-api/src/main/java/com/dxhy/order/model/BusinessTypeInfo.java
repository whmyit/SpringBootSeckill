package com.dxhy.order.model;

import lombok.Data;

import java.io.Serializable;
import java.util.Date;
/**
 * 业务类型
 *
 * @author ZSC-DXHY
 * @date 创建时间: 2020-08-14 16:40
 */
@Data
public class BusinessTypeInfo implements Serializable {
    private static final long serialVersionUID = 1L;
    
    /**
     * id
     */
    private String id;
    
    /**
     * 业务类型名称
     */
    private String businessName;
    
    /**
     * 业务类型ID
     */
    private String businessId;
    
    /**
     * 销货方纳税人识别号
     */
    private String xhfNsrsbh;
    
    /**
     * 销货方纳税人名称
     */
    private String xhfMc;
    /**
     * 数据来源：0接口采集 1页面添加
     */
    private String ly;
    
    /**
     * 业务类型描述
     */
    private String description;
    
    /**
     * 状态（0：有效；1：无效）
     */
    private String status;
    
    /**
     * 创建时间
     */
    private Date createTime;
    
    /**
     * 修改时间
     */
    private Date updateTime;
}
